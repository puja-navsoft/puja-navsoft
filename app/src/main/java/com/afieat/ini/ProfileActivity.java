package com.afieat.ini;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.afieat.ini.databinding.ActivityProfileBinding;
import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.Security.Relogin;
import com.afieat.ini.database.DBHelper;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private boolean isPasswordVisible;
    private Dialog afieatGifLoaderDialog;

    private ActivityProfileBinding binding;

    //  private FrameLayout frameLayout;
    private TextInputEditText tetName, tetFathersName, tetEmail, tetMobile, tetCity;
    private TextView tvChangePassword;
    private Button btnSignup;
    private final int REQUEST_LOGIN = 100;
    private ListView lvCities;
    private AlertDialog dialogCity;
    private String mCityId;
    private String mobile;
    private TextView verify;
    private Map<String, String> params;
    private boolean otpVerification = false;

    public ProfileActivity() {
        isPasswordVisible = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_profile);
//        setContentView(R.layout.activity_profile);
        Toolbar mToolbar;
        params = new HashMap<>();
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_update_profile));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tetName = (TextInputEditText) findViewById(R.id.tetName);
        tetFathersName = (TextInputEditText) findViewById(R.id.tetFathersName);
        tetEmail = (TextInputEditText) findViewById(R.id.tetEmail);
        tetMobile = (TextInputEditText) findViewById(R.id.tetMobile);
        tetCity = (TextInputEditText) findViewById(R.id.tetCity);
        tvChangePassword = (TextView) findViewById(R.id.tvChangePassword);
        verify = (TextView) findViewById(R.id.verifyOtpBtn);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        lvCities = new ListView(ProfileActivity.this);
        //frameLayout = (FrameLayout) findViewById(R.id.progressBar);

        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldsValid() && verify.getVisibility() == View.GONE)
                    updateUserDetailsToNW();
                else
                    Toast.makeText(ProfileActivity.this, "Please verify the phone number to continue", Toast.LENGTH_LONG).show();
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneRegistration(tetMobile.getText().toString().trim());
                otpVerification = true;
            }
        });



        tetCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCity.show();
            }
        });
        loadCityListFromNW();

        AlertDialog.Builder builderCities = new AlertDialog.Builder(ProfileActivity.this);
        builderCities.setView(lvCities);
        dialogCity = builderCities.create();

        lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogCity.hide();
                if (position == 0) {
                    tetCity.setText(R.string.select_city);
                } else {
                    tetCity.setText(cities.get(position - 1).name);
                    mCityId = cities.get(position - 1).id;
                }
            }
        });


    }
    class City {
        String id, name;
    }

    private void phoneRegistration(final String phone){

        params.put("phone", phone);
        //       params.put("lastname", lastname);

        params.put("id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));

//                    Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

        RequestQueue queue = Volley.newRequestQueue(this);
        System.out.println("SignUpActivity : Param : " + params);
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            if (phone != null) {
                final ProgressDialog dialog = AppUtils.showProgress(ProfileActivity.this, "Verify phone number", getString(R.string.msg_please_wait));
                NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.OTP_SEND, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                AppUtils.log(response.toString());
                                AppUtils.hideProgress(dialog);
                                String msg = response.optString("msg");
                                String code = response.optString("code");

                                Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_LONG).show();


                                if (code.equalsIgnoreCase("")) {
                                    Intent intent = new Intent(ProfileActivity.this, PhoneOtpScreen.class);
                                    intent.putExtra("from","profile");
                                    intent.putExtra("phoneNumber",phone);
                                    startActivity(intent);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Snackbar snackbar = Snackbar.make(binding.page, getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        phoneRegistration(phone);
                                    }
                                });
                                snackbar.setActionTextColor(Color.RED);
                                snackbar.show();
                                AppUtils.hideProgress(dialog);
                            }
                        }
                ){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("appToken", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.SHARED_PREF_DEV_FIRE_BASE_TOKEN));
                        params.put("Authorization", AppInstance.getInstance(getApplicationContext()).getAuthkeyforall());
                        return params;
                    }
                };

//                AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
                queue.add(request);
            }

        } else {
            Snackbar snackbar = Snackbar.make(binding.page, getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phoneRegistration(phone);
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }

    }



    private String[] citiesArray;
    private List<City> cities = new ArrayList<>();
    private void loadCityListFromNW() {
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            final ProgressDialog dialog = AppUtils.showProgress(ProfileActivity.this, "", getString(R.string.msg_please_wait));
            Map<String, String> params = new HashMap<>();
            if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                params.put("lang", "en");
            } else {
                params.put("lang", "ar");
            }
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.LIST_CITIES, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            setFieldsFromNW();
                            AppUtils.hideProgress(dialog);
                            JSONArray cityArray = response.optJSONArray("cityllist");
                            citiesArray = new String[cityArray.length() + 1];
                            int i = 0;
                            citiesArray[0] = getString(R.string.select_city);
                            while (i < cityArray.length()) {
                                JSONObject cityObject = cityArray.optJSONObject(i++);
                                City city = new City();

                                city.id = cityObject.optString("city_id");
                                city.name = cityObject.optString("city_name");
                                cities.add(city);
                                citiesArray[i] = city.name;
                            }
                            AppUtils.hideProgress(dialog);
                            lvCities.setAdapter(new ArrayAdapter<>(ProfileActivity.this, R.layout.layout_simple_list_item, citiesArray));

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AppUtils.hideProgress(dialog);
                            if (error.networkResponse == null) {
                                if (error.getClass().equals(TimeoutError.class) || error.getClass().equals(NoConnectionError.class)) {
                                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_server_no_response), Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            loadCityListFromNW();
                                            snackbar.dismiss();
                                        }
                                    });
                                    snackbar.show();
                                }
                            }
                            error.printStackTrace();
                        }
                    }
            );
            AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadCityListFromNW();
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    private void setFieldsFromNW() {
        //AppUtils.showViews(frameLayout);
        afieatGifLoaderDialog();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.USER_DETAILS, params,
                new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject mRJsonObject = response;
                        //AppUtils.hideViews(frameLayout);
                        afieatGifLoaderDialog.dismiss();
                        AppUtils.log(response);
                        mRJsonObject = response.optJSONObject("user_details");
                        if ((mRJsonObject.optString("firstname").length() > 0) && (mRJsonObject.optString("lastname").length() > 0)) {
                            tetName.setText(mRJsonObject.optString("firstname") + " " + mRJsonObject.optString("lastname"));
                        } else {
                            tetName.setText(mRJsonObject.optString("firstname"));
                        }

                        tetFathersName.setText(mRJsonObject.optString("father_name"));
                        tetEmail.setText(mRJsonObject.optString("email"));
                        tetMobile.setText(mRJsonObject.optString("phone"));

                        mobile = mRJsonObject.optString("phone");
                        mCityId = mRJsonObject.optString("city");

                        tetMobile.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {


                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (!s.toString().equalsIgnoreCase(mobile)){
                                    verify.setVisibility(View.VISIBLE);
                                }
                                else
                                    verify.setVisibility(View.GONE);

                            }
                        });

//                        loadCityListFromNW();
                        for (int i = 0;i<cities.size();i++){
                            if (mCityId.equalsIgnoreCase(cities.get(i).id)){
                                tetCity.setText(cities.get(i).name);
                                break;
                            }

                        }
//                        if((mRJsonObject.optString("email").length()) > 0) {
//                            tetEmail.setText(mRJsonObject.optString("email"));
//                        } else {
//                            tetMobile.setText(mRJsonObject.optString("phone"));
//                        }

                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_FNAME, mRJsonObject.optString("firstname"));
                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_PHONE, mRJsonObject.optString("phone"));

                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_EMAIL, mRJsonObject.optString("email"));
                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(true));
                        DBHelper db = new DBHelper(ProfileActivity.this);
                        db.updateLoggedInUser(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // AppUtils.hideViews(frameLayout);
                        afieatGifLoaderDialog.dismiss();
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void updateUserDetailsToNW() {
        //  AppUtils.showViews(frameLayout);
        afieatGifLoaderDialog();
        Map<String, String> params = new HashMap<>();
        String fullname = tetName.getText().toString();
        String fname = fullname;
        String lname = "";
        if (fullname.trim().contains(" ")) {
            fname = fullname.split(" ")[0];
            lname = fullname.split(fname)[1].trim();
        }
        params.put("user_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        params.put("firstname", fname);
        params.put("lastname", lname);
        params.put("father_name", tetFathersName.getText().toString().trim());
        params.put("email", tetEmail.getText().toString().trim());
        params.put("phone", tetMobile.getText().toString().trim());
        params.put("city_id",mCityId);
        Log.d("Request parameter ", " test " + params);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.UPDATE_USER, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //AppUtils.hideViews(frameLayout);
                        afieatGifLoaderDialog.dismiss();
                        AppUtils.log(response);
                        if (response.optString("msg").length() > 0) {


                            if (response.has("code") && response.optInt("code") == 999) {
                                new Relogin(ProfileActivity.this, new Relogin.OnLoginlistener() {
                                    @Override
                                    public void OnLoginSucess() {
                                        updateUserDetailsToNW();

                                    }

                                    @Override
                                    public void OnError(String Errormessage) {
                                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                        startActivityForResult(intent, REQUEST_LOGIN);
                                    }
                                }).execute();
                            } else {
                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_FNAME, tetName.getText().toString());
                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_EMAIL, tetEmail.getText().toString());
                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(true));
                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_PHONE, tetMobile.getText().toString().trim());
                                DBHelper db = new DBHelper(ProfileActivity.this);
                                db.updateLoggedInUser(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
                                Snackbar.make(findViewById(R.id.page), response.optString("msg"), Snackbar.LENGTH_SHORT).show();
                                otpVerification = false;
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  AppUtils.hideViews(frameLayout);
                        afieatGifLoaderDialog.dismiss();
                        error.printStackTrace();
                    }
                }
        );

        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private boolean fieldsValid() {
        boolean flag = true;
        if (tetName.getText().toString().trim().length() == 0) {
            tetName.setError(getString(R.string.msg_field_mandatory));
            flag = false;
        }
        if ((tetMobile.getText().toString().trim().length() < 9) || (tetMobile.getText().toString().trim().length() > 12)) {
            tetMobile.setError(getString(R.string.mobile_msg_field_mandatory));
            flag = false;
        }
        if (tetMobile.getText().toString().trim().length() == 0) {
            tetMobile.setError(getString(R.string.msg_field_mandatory));
            flag = false;
        }

        if (mCityId==null){
            tetCity.setError(getString(R.string.msg_field_mandatory));
            flag = false;
        }
//        if (tetFathersName.getText().toString().trim().length() == 0) {
//            tetFathersName.setError(getString(R.string.msg_field_mandatory));
//            flag = false;
//        }
        return flag;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }


    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(this);
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.verifyOtpBtn.setVisibility(View.GONE);
        mobile = tetMobile.getText().toString();
        if (otpVerification)
            setFieldsFromNW();

    }
}
