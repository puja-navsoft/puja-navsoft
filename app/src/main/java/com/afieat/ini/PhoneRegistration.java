package com.afieat.ini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.afieat.ini.database.DBHelper;
import com.afieat.ini.databinding.ActivityPhoneRegistrationBinding;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PhoneRegistration extends AppCompatActivity {

    ActivityPhoneRegistrationBinding binding;

    String papaName ="";
    String email="";
    String password="";
    String firstname="";
    String devToken="";
    String city="";
    String referal_code="";
    private Map<String, String> params;
    private String from="";
    private String id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_phone_registration);
        params = new HashMap<>();

        intentData();


        binding.btnPhReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String mobile = binding.tetMobile.getText().toString().trim().length() > 7 ? binding.tetMobile.getText().toString().trim() : "";

                if (!mobile.equalsIgnoreCase("")) {

                    phoneRegistration(mobile);
                }

                else {
                    binding.tetMobile.setError("Please enter a valid number");
                    binding.tetMobile.requestFocus();
                }
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }


    private void phoneRegistration(final String phone){

        params.put("phone", phone);
        //       params.put("lastname", lastname);

        if (id!=null && !id.equalsIgnoreCase(""))
            params.put("id", id);
        else
            params.put("id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));

//                    Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

        RequestQueue queue = Volley.newRequestQueue(this);
        System.out.println("SignUpActivity : Param : " + params);
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            if (phone != null) {
                final ProgressDialog dialog = AppUtils.showProgress(PhoneRegistration.this, getString(R.string.msg_creating_account), getString(R.string.msg_please_wait));
                NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.OTP_SEND, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                AppUtils.log(response.toString());
                                AppUtils.hideProgress(dialog);
                                String msg = response.optString("msg");
                                String code = response.optString("code");
                                String status = response.optString("status");

                                Toast.makeText(PhoneRegistration.this, msg, Toast.LENGTH_LONG).show();
                                if (!status.equalsIgnoreCase("0")) {
                                    Toast.makeText(PhoneRegistration.this, msg, Toast.LENGTH_LONG).show();


                                    Intent intent = new Intent(PhoneRegistration.this, PhoneOtpScreen.class);

                                    intent.putExtra("papaName", papaName);
                                    intent.putExtra("email", email);
                                    intent.putExtra("password", password);
                                    intent.putExtra("name", firstname);
                                    intent.putExtra("devToken", devToken);
                                    intent.putExtra("city", city);
                                    intent.putExtra("referal_code", referal_code);
                                    intent.putExtra("phoneNumber", binding.tetMobile.getText().toString());
                                    if (id!=null && !id.equalsIgnoreCase(""))
                                        intent.putExtra("id",id);
                                    else
                                        intent.putExtra("id",AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
                                    if (from==null)
                                        intent.putExtra("from","registration");
                                    else
                                        intent.putExtra("from",from);
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
                        params.put("appToken", devToken);
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

    private void intentData() {

        papaName = getIntent().getStringExtra("papaName");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        firstname = getIntent().getStringExtra("name");
        devToken = getIntent().getStringExtra("devToken");
        city = getIntent().getStringExtra("city");
        referal_code = getIntent().getStringExtra("referal_code");
        from = getIntent().getStringExtra("from");
        id = getIntent().getStringExtra("id");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }
}