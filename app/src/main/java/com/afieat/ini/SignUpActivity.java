package com.afieat.ini;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afieat.ini.database.DBHelper;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class SignUpActivity extends AppCompatActivity {

    private Map<String, String> params;
    private Map<String, String> params2;
    private boolean isPasswordVisible;

    private TextInputEditText tetName,
            tetFathersName,
            tetGrandfathersName,
            tetEmail,
            tetMobile,
            tetPassword,
            tetconfirmPassword,
            spCity;
    private RelativeLayout page;
    private Button btnSignup;
    private String[] citiesArray;
    private String mCityId = "";
    private List<City> cities;
    private ListView lvCities;
    private AlertDialog dialogCity;
    private String email = null;
    private String referral_code_param;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private String mUserId = "";
    private String papaName;
    private String granpaName;
    private String mobile;
    private String password;

    public SignUpActivity() {
        params = new HashMap<>();
        params2 = new HashMap<>();
        isPasswordVisible = false;
        cities = new ArrayList<>();
    }

    String getTokenValue[];


    @Override
    protected void onStart() {
        super.onStart();

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar mToolbar;
        mUserId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);
        if (mUserId.length() > 0) {
            //  finish();
            ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

            System.out.println("Rahubjsbf : " + taskList.get(0).numActivities);
            if (taskList.get(0).numActivities > 1) {
                finish();
            } else {
                finish();
                startActivity(new Intent(SignUpActivity.this, SplashActivity.class));
            }
        /*    for (int i=0;i<taskList.size();i++)
            {
                System.out.println("Rahubjsbf : "+taskList.get(i).numActivities);
            }*/
          /*  if(taskList.get(0).numActivities == 1 &&
                    taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {

            }*/
        }
        Intent intent = getIntent();
        // String action = intent.getAction();
        Uri data = intent.getData();

        System.out.println("Firebase : data : " + data);

        page = (RelativeLayout) findViewById(R.id.page);
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_create_account));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tetName = (TextInputEditText) findViewById(R.id.tetName);
        tetFathersName = (TextInputEditText) findViewById(R.id.tetFathersName);
        tetGrandfathersName = (TextInputEditText) findViewById(R.id.tetGrandfathersName);
        tetEmail = (TextInputEditText) findViewById(R.id.tetEmail);
        //      tetIsd = (TextInputEditText) findViewById(R.id.tetIsd);
        tetMobile = (TextInputEditText) findViewById(R.id.tetMobile);
        tetPassword = (TextInputEditText) findViewById(R.id.tetPassword);
        tetconfirmPassword = (TextInputEditText) findViewById(R.id.tetconfirmPassword);
        lvCities = new ListView(SignUpActivity.this);
        btnSignup = (Button) findViewById(R.id.btnSignup);

        spCity = (TextInputEditText) findViewById(R.id.spCity);
        if (mUserId.length() == 0) {
            loadCityListFromNW();
        }

        AlertDialog.Builder builderCities = new AlertDialog.Builder(SignUpActivity.this);
        builderCities.setView(lvCities);
        dialogCity = builderCities.create();

        lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogCity.hide();
                if (position == 0) {
                    spCity.setText(R.string.select_city);
                } else {
                    spCity.setText(cities.get(position - 1).name);
                    mCityId = cities.get(position - 1).id;
                }
            }
        });

        spCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCity.show();
            }
        });


//        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position > 0) {
//                    String citySelected = citiesArray[position];
//                    for (CityModel city : cities) {
//                        if (city.name.equals(citySelected)) {
//                            mCityId = city.id;
//                            break;
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        tetPassword.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("en")) {
//                        if (event.getRawX() >= (tetPassword.getRight() - tetPassword.getCompoundDrawables()[2].getBounds().width())) {
//                            int drawable = isPasswordVisible ? R.drawable.ic_visibility_black_18dp : R.drawable.ic_visibility_off_black_18dp;
//                            TransformationMethod method = isPasswordVisible ? new PasswordTransformationMethod() : null;
//                            isPasswordVisible = !isPasswordVisible;
//                            tetPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
//                            tetPassword.setTransformationMethod(method);
//                            return true;
//                        }
//                    } else {
//                        if (event.getRawX() <= (tetPassword.getLeft() - tetPassword.getCompoundDrawables()[0].getBounds().width())) {
//                            int drawable = isPasswordVisible ? R.drawable.ic_visibility_black_18dp : R.drawable.ic_visibility_off_black_18dp;
//                            TransformationMethod method = isPasswordVisible ? new PasswordTransformationMethod() : null;
//                            isPasswordVisible = !isPasswordVisible;
//                            tetPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
//                            tetPassword.setTransformationMethod(method);
//                            return true;
//                        }
//                    }
//                }
//                return false;
//            }
//        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.clear();

//                final Intent intent  = new Intent(SignUpActivity.this,PhoneRegistration.class);


                if (isValidFields()) {

                    final String devToken = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.SHARED_PREF_DEV_FIRE_BASE_TOKEN);

//                    if (devToken.length() == 0) {
//                        Toast.makeText(getApplicationContext(), getString(R.string.msg_no_token), Toast.LENGTH_LONG).show();
//                        return;
//                    }

//                    intent.putExtra("papaName", tetFathersName.getText().toString().trim());
//                    intent.putExtra("email", tetEmail.getText().toString().trim());
//                    intent.putExtra("password", tetPassword.getText().toString().trim());
//                    intent.putExtra("name", tetName.getText().toString().trim());
//                    intent.putExtra("devToken", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.SHARED_PREF_DEV_FIRE_BASE_TOKEN));
//                    intent.putExtra("city", mCityId);
//                    intent.putExtra("referal_code", referral_code_param);

                    String firstname = "";
                    String lastname = "";
                    String name = tetName.getText().toString().trim();
                    firstname = name;
//                    final String devToken = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.SHARED_PREF_DEV_FIRE_BASE_TOKEN);

                    if (devToken.length() == 0) {
                        Toast.makeText(getApplicationContext(), getString(R.string.msg_no_token), Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        firstname = name.split(" ")[0];
                        lastname = name.split(firstname)[1].trim();
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                    papaName = tetFathersName.getText().toString().trim();
                    granpaName = tetGrandfathersName.getText().toString().trim();
                    mobile = tetMobile.getText().toString().trim().length() > 0 ? tetMobile.getText().toString().trim() : "";
                    email = tetEmail.getText().toString().trim();
                    password = tetPassword.getText().toString().trim();
                    //  final String mobile = tetMobile.getText().toString().trim().length() > 0 ? tetMobile.getText().toString().trim() : "";
//                    if(isNumeric(tetEmail.getText().toString().trim())) {
//                     //   email = (tetEmail.getText().toString().trim().length() > 6) && (tetEmail.getText().toString().trim().length() < 13) ? tetMobile.getText().toString().trim() : "";
//                       if ((tetEmail.getText().toString().trim().length() > 9) && (tetEmail.getText().toString().trim().length() < 12)) {
//                           email = tetEmail.getText().toString().trim();
//
//                       } else {
//                           tetEmail.setText("");
//                           email = null;
//                       }
//
//                    } else {
//                        email = tetEmail.getText().toString().trim();
//                    }

//                        tetEmail .addTextChangedListener(new TextWatcher() {
//                            public void afterTextChanged(Editable s) {
//
//                                if (tetEmail.getText().toString().trim().matches(emailPattern) && s.length() > 0)
//                                {
//                                    email = tetEmail.getText().toString().trim();
////                                    Toast.makeText(getApplicationContext(),"valid email address",Toast.LENGTH_SHORT).show();
////                                    // or
////                                    textView.setText("valid email");
//                                }
//                                else
//                                {
////                                    Toast.makeText(getApplicationContext(),"Invalid email address",Toast.LENGTH_SHORT).show();
////                                    //or
////                                    textView.setText("invalid email");
//                                }
//                            }
//                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                                // other stuffs
//                            }
//                            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                                // other stuffs
//                            }
//                        });

                    params.put("firstname", firstname);
                    //       params.put("lastname", lastname);
                    params.put("username_reg", email);
                    params.put("father_name", papaName);
                    //            params.put("grand_father_name", granpaName);
                    params.put("phone", mobile);
                    params.put("user_device_token", devToken);
                    params.put("password", password);
                    params.put("city", mCityId);
                    params.put("referal_code", referral_code_param);

                    Log.d("check_sign_up", "  test " + params);
//                    Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

                    System.out.println("SignUpActivity : Param : " + params);
                    if (AppUtils.isNetworkAvailable(getApplicationContext())) {

                        final String finalFirstname = firstname;
                        //               final String finalLastname = lastname;
//                        if (mobile != null) {
                            final ProgressDialog dialog = AppUtils.showProgress(SignUpActivity.this, getString(R.string.msg_creating_account), getString(R.string.msg_please_wait));
                        final String finalFirstname1 = firstname;
                        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.SIGN_UP, params,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            AppUtils.log(response.toString());
                                            AppUtils.hideProgress(dialog);
                                            String msg = response.optString("msg");
                                            String code = response.optString("code");
                                            if ("0".equals(code)) {
//                                            tetEmail.setError(msg);
//                                            tetEmail.requestFocus();
                                                Snackbar.make(page, msg, Snackbar.LENGTH_LONG).show();
                                            } else {



                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        setResult(RESULT_OK);
                                                        finish();
                                                    }
                                                }, 2000);

                                                JSONObject userdataObject = response.optJSONObject("userdata");
                                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_FNAME, finalFirstname);
                                                //                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_LNAME, finalLastname);
                                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_FATHER, papaName);
                                                //                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_FAMILY, granpaName);
                                                //                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_PHONE, mobile);
                                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, userdataObject.optString("id"));
                                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_EMAIL, userdataObject.optString("email"));
                                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(true));
                                                DBHelper db = new DBHelper(SignUpActivity.this);
                                                db.updateLoggedInUser(userdataObject.optString("id"));

                                                AppUtils.log(msg);
                                                Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

//                                                loginAfterSignup(mobile,password,devToken);
//                                                startActivity(intent);
                                                phoneRegistration(mobile, finalFirstname1);

                                                /*final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent = new Intent(SignUpActivity.this, CategoryListActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }, 200);*/

                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            error.printStackTrace();
                                            Snackbar snackbar = Snackbar.make(page, getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
                                            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    btnSignup.performClick();
                                                }
                                            });
                                            snackbar.setActionTextColor(Color.RED);
                                            snackbar.show();
                                            AppUtils.hideProgress(dialog);
                                        }
                                    }
                            );
                            AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
//                        } else {
//                            tetMobile.setError(getString(R.string.msg_field_mandatory));
//                        }

                    } else {
                        Snackbar snackbar = Snackbar.make(page, getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btnSignup.performClick();
                            }
                        });
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
                    }

                }
            }
        });


        tetPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                        if (event.getRawX() >= (tetPassword.getRight() - tetPassword.getCompoundDrawables()[2].getBounds().width() - 60)) {
                            int drawable = isPasswordVisible ? R.drawable.ic_visibility_black_18dp : R.drawable.ic_visibility_off_black_18dp;
                            TransformationMethod method = isPasswordVisible ? new PasswordTransformationMethod() : null;
                            isPasswordVisible = !isPasswordVisible;
                            tetPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
                            tetPassword.setTransformationMethod(method);
                            return true;
                        }
                    } else {
                        if (event.getRawX() <= (tetPassword.getLeft() - tetPassword.getCompoundDrawables()[0].getBounds().width() - 60)) {
                            int drawable = isPasswordVisible ? R.drawable.ic_visibility_black_18dp : R.drawable.ic_visibility_off_black_18dp;
                            TransformationMethod method = isPasswordVisible ? new PasswordTransformationMethod() : null;
                            isPasswordVisible = !isPasswordVisible;
                            tetPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
                            tetPassword.setTransformationMethod(method);
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        tetconfirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                        if (event.getRawX() >= (tetconfirmPassword.getRight() - tetconfirmPassword.getCompoundDrawables()[2].getBounds().width() - 60)) {
                            int drawable = isPasswordVisible ? R.drawable.ic_visibility_black_18dp : R.drawable.ic_visibility_off_black_18dp;
                            TransformationMethod method = isPasswordVisible ? new PasswordTransformationMethod() : null;
                            isPasswordVisible = !isPasswordVisible;
                            tetconfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
                            tetconfirmPassword.setTransformationMethod(method);
                            return true;
                        }
                    } else {
                        if (event.getRawX() <= (tetconfirmPassword.getLeft() - tetconfirmPassword.getCompoundDrawables()[0].getBounds().width() - 60)) {
                            int drawable = isPasswordVisible ? R.drawable.ic_visibility_black_18dp : R.drawable.ic_visibility_off_black_18dp;
                            TransformationMethod method = isPasswordVisible ? new PasswordTransformationMethod() : null;
                            isPasswordVisible = !isPasswordVisible;
                            tetconfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
                            tetconfirmPassword.setTransformationMethod(method);
                            return true;
                        }
                    }
                }
                return false;
            }
        });


        Intent referal_code = getIntent();
        // String action = intent.getAction();
        if (referal_code != null) {
            if (referal_code.getData() != null) {

                Uri referal_code_data = referal_code.getData();

                System.out.println("Firebase : data : SignUpActivity : " + referal_code_data);

                if (referal_code_data.toString().contains("signup")) {
                    StringTokenizer st1 = new StringTokenizer(referal_code_data.toString(), "/");

                    System.out.println("StringTokenizer : count : " + st1.countTokens());

                    getTokenValue = new String[st1.countTokens()];
                    int i = 0;
                    while (st1.hasMoreTokens()) {
                        getTokenValue[i] = st1.nextToken();
                        i = i + 1;
                    }
                    if (mUserId.length() == 0) {
                        Toast.makeText(getApplicationContext(), R.string.referal_code_applied, Toast.LENGTH_SHORT).show();

                    } else if (mUserId.length() > 0) {

                    }
                    referral_code_param = getTokenValue[3];
                } else {
                    referral_code_param = "";
                }
                //System.out.println("StringTokenizer : i got you : "+getTokenValue[3]);
            } else {
                referral_code_param = "";
            }
        } else {
            referral_code_param = "";
        }


    }

    private void phoneRegistration(final String phone, final String firstname){

        params2.put("phone", phone);
        //       params.put("lastname", lastname);
        params2.put("email",email);
        params2.put("id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));

//                    Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

        RequestQueue queue = Volley.newRequestQueue(this);
        System.out.println("SignUpActivity : Param : " + params2);
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            if (phone != null) {
                final ProgressDialog dialog = AppUtils.showProgress(SignUpActivity.this, getString(R.string.msg_creating_account), getString(R.string.msg_please_wait));
                NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.OTP_SEND, params2,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                AppUtils.log(response.toString());
                                AppUtils.hideProgress(dialog);
                                String msg = response.optString("msg");
                                String code = response.optString("code");
                                String status = response.optString("status");

                                Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_LONG).show();
                                if (!status.equalsIgnoreCase("0")) {
                                    Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_LONG).show();


                                    Intent intent = new Intent(SignUpActivity.this, PhoneOtpScreen.class);

                                    intent.putExtra("papaName", papaName);
                                    intent.putExtra("email", email);
                                    intent.putExtra("password", password);
                                    intent.putExtra("name", firstname);
                                    intent.putExtra("devToken", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.SHARED_PREF_DEV_FIRE_BASE_TOKEN));
//                                    intent.putExtra("city", city);
//                                    intent.putExtra("referal_code", referal_code);
                                    intent.putExtra("phoneNumber", phone);
                                    intent.putExtra("id",AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
                                    intent.putExtra("from","registration");
                                    startActivity(intent);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Snackbar snackbar = Snackbar.make(page, getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        phoneRegistration(phone,firstname);
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
            Snackbar snackbar = Snackbar.make(page, getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phoneRegistration(phone,firstname);
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogCity.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private boolean isValidFields() {
        boolean flag = true;
        if (tetName.getText().toString().trim().length() == 0) {
            tetName.setError(getString(R.string.msg_field_mandatory));
            tetName.requestFocus();
            return false;
        }
//        else if (tetMobile.getText().toString().trim().length() == 0) {
//            tetMobile.setError(getString(R.string.msg_field_mandatory));
//            tetMobile.requestFocus();
//            return false;
//        }
        if (tetEmail.getText().toString().trim().length() == 0) {
            tetEmail.setError(getString(R.string.msg_field_mandatory));
            flag = false;
        }
//        if(!isNumeric(tetEmail.getText().toString().trim())) {
//            if (!AppUtils.isValidEmail(tetEmail.getText().toString().trim())) {
//                tetEmail.setError(getString(R.string.msg_login_fail));
//                flag = false;
//            }
//        }

        if (spCity.getText().toString().equals(R.string.select_city)) {
            spCity.setError(getString(R.string.msg_select_city));
            return false;
        }

//        if ((tetMobile.getText().toString().trim().length() < 9) || (tetMobile.getText().toString().trim().length() > 12)) {
//            tetMobile.setError(getString(R.string.mobile_msg_field_mandatory));
//            tetMobile.requestFocus();
//            return false;
//        }
        if (tetPassword.getText().toString().trim().length() < 6) {
            tetPassword.setError(getString(R.string.msg_password_min_6_chars));
            tetPassword.requestFocus();
            return false;
        }
        if (tetconfirmPassword.getText().toString().trim().length() < 6) {
            tetconfirmPassword.setError(getString(R.string.msg_password_min_6_chars));
            tetconfirmPassword.requestFocus();
            return false;
        }
        if (!tetconfirmPassword.getText().toString().trim().equals(tetPassword.getText().toString().trim())) {
            tetconfirmPassword.setError(getString(R.string.confirmpassorwdarenotmatching));
            tetconfirmPassword.requestFocus();
            return false;
        }

        return flag;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }

    private void loadCityListFromNW() {
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            final ProgressDialog dialog = AppUtils.showProgress(SignUpActivity.this, "", getString(R.string.msg_please_wait));
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
                            lvCities.setAdapter(new ArrayAdapter<>(SignUpActivity.this, R.layout.layout_simple_list_item, citiesArray));
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

    class City {
        String id, name;
    }

    //   public static boolean isNumeric(String email){
//        return email.matches("-?\\d+(\\.\\d+)?");
//    }


    public void loginAfterSignup(String email,String password,String devToken){
        final ProgressDialog dialog = AppUtils.showProgress(SignUpActivity.this, getString(R.string.msg_logging_in), getString(R.string.msg_please_wait));
        params.put("username_login", email);
        params.put("password", password);
        params.put("user_device_token", devToken);
        params.put("registered_from", "2");

        Log.d("Request parameter ", " test " + params);

        if (AppUtils.isNetworkAvailable(getApplicationContext())) {

            final String finalEmail = email;
            final String finalPassword = password;
            try {
                NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.LOG_IN, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {

                                AppUtils.hideProgress(dialog);
                                System.out.println("Rahul : loginResponse : " + response);
                                if ("0".equals(response.optString("status"))) {
                                    Snackbar.make(page, getString(R.string.msg_login_fail), Snackbar.LENGTH_LONG).show();
                                }
                                else if ("1".equals(response.optString("status"))) {
                                    final String id = response.optString("id");
                                    String name = response.optString("name");
                                    String phone = response.optString("phone");

                                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, id);
                                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_FNAME, name);
                                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_PHONE, phone);
                                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.MY_POINTS, response.optString("point_value"));
                                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.MY_POINTS_VALUE, response.optString("point"));


                                    if (response.has("appToken"))
                                        AppInstance.getInstance(getApplicationContext()).setAuthkey(response.optString("appToken"));

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.SHARED_PREF_USER_PASSWORD, finalPassword);
                                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_EMAIL, finalEmail);
                                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(true));
                                            DBHelper db = new DBHelper(SignUpActivity.this);
                                            db.updateLoggedInUser(id);
                                            setResult(RESULT_OK);

                                        }
                                    }, 1000);
                                    Snackbar.make(page, getString(R.string.msg_login_success), Snackbar.LENGTH_LONG).show();

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(SignUpActivity.this, CategoryListActivity.class);
                                            startActivity(intent);
                                        }
                                    }, 200);
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //AppUtils.hideProgress(dialog);
                                AppUtils.hideProgress(dialog);
                                error.printStackTrace();
                            }
                        }
                );
                AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);

            } catch (Exception e) {
                e.printStackTrace();
                //   AppUtils.hideProgress(dialog);
                AppUtils.hideProgress(dialog);
            }

        }

    }
}
