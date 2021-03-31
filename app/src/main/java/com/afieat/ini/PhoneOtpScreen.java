package com.afieat.ini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afieat.ini.database.DBHelper;
import com.afieat.ini.databinding.ActivityPhoneOtpScreenBinding;
import com.afieat.ini.services.OtpReceiver;
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
import com.vlk.multimager.utils.Params;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PhoneOtpScreen extends AppCompatActivity {

    private ActivityPhoneOtpScreenBinding binding;
    String timerNow;

    String papaName;
    String email;
    String password;
    String name;
    String devToken;
    String city;
    String referal_code;
    String phoneNumber;
    private Map<String, String> params;
    private String from = "";
    private String id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_phone_otp_screen);

        getIntentData();

        params = new HashMap<>();


//        requestSmsPermission();

//        new OtpReceiver().setEditText(binding.otpTxt);

        startTimer();
        binding.resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phoneRegistration(phoneNumber);
                startTimer();

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.resendOtp.getText().toString().equalsIgnoreCase(getString(R.string.resend_otp))) {
                    onBackPressed();
                }
                else{
                    Toast.makeText(PhoneOtpScreen.this, "Please wait for "+timerNow+ " to go back", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.btnOtpVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!binding.otpTxt.getText().toString().isEmpty())

                    if (from.equalsIgnoreCase("profile"))
                        registrationProcessForProfile(binding.otpTxt.getText().toString().trim());
                    else if (from.equalsIgnoreCase("social"))
                        registrationProcessForSocialLogin(binding.otpTxt.getText().toString().trim());
                    else
                        registrationProcess(binding.otpTxt.getText().toString().trim());
                else
                    binding.otpTxt.requestFocus();
            }
        });
    }

    private void phoneRegistration(final String phone){

        params.put("phone", phone);
        params.put("email",email);
        //       params.put("lastname", lastname);

        params.put("id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));

//                    Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

        RequestQueue queue = Volley.newRequestQueue(this);
        System.out.println("SignUpActivity : Param : " + params);
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            if (phone != null) {
                final ProgressDialog dialog = AppUtils.showProgress(PhoneOtpScreen.this, getString(R.string.msg_creating_account), getString(R.string.msg_please_wait));
                NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.OTP_SEND, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                AppUtils.log(response.toString());
                                AppUtils.hideProgress(dialog);
                                String msg = response.optString("msg");
                                String code = response.optString("code");

                                Toast.makeText(PhoneOtpScreen.this, msg, Toast.LENGTH_LONG).show();
                                dialog.dismiss();
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

    private void getIntentData() {

        papaName = getIntent().getStringExtra("papaName");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        name = getIntent().getStringExtra("name");
        devToken = getIntent().getStringExtra("devToken");
        city = getIntent().getStringExtra("city");
        referal_code = getIntent().getStringExtra("referal_code");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        from = getIntent().getStringExtra("from");
        id = getIntent().getStringExtra("id");

    }

    private void registrationProcess(final String code) {

        if (devToken.length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_no_token), Toast.LENGTH_LONG).show();
            return;
        }

        params.put("code", code);
        //       params.put("lastname", lastname);
        params.put("phone", phoneNumber);
        params.put("email",email);

//                    Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

        System.out.println("SignUpActivity : Param : " + params);
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {

            RequestQueue queue = Volley.newRequestQueue(this);
            //               final String finalLastname = lastname;
            if (phoneNumber != null) {
                final ProgressDialog dialog = AppUtils.showProgress(PhoneOtpScreen.this, "Otp Verification", getString(R.string.msg_please_wait));
                NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.OTP_VERIFY, params,
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
                                    Snackbar.make(binding.page, msg, Snackbar.LENGTH_LONG).show();
                                } else {
                                    loginAfterSignup(phoneNumber, password, devToken);
                                    dialog.dismiss();



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
                                Snackbar snackbar = Snackbar.make(binding.page, getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        registrationProcess(code);
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
                queue.add(request);
            }

        } else {
            Snackbar snackbar = Snackbar.make(binding.page, getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registrationProcess(code);
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }

    }

    private void registrationProcessForProfile(final String code) {

        params.put("code", code);
        //       params.put("lastname", lastname);
        params.put("phone", phoneNumber);

//                    Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

        System.out.println("SignUpActivity : Param : " + params);
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {

            RequestQueue queue = Volley.newRequestQueue(this);
            //               final String finalLastname = lastname;
            if (phoneNumber != null) {
                final ProgressDialog dialog = AppUtils.showProgress(PhoneOtpScreen.this, "Otp Verification", getString(R.string.msg_please_wait));
                NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.OTP_VERIFY, params,
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
                                    Snackbar.make(binding.page, msg, Snackbar.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(PhoneOtpScreen.this, "Phone verification successful", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    finish();


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
                                Snackbar snackbar = Snackbar.make(binding.page, getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        registrationProcessForProfile(code);
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
                queue.add(request);
            }

        } else {
            Snackbar snackbar = Snackbar.make(binding.page, getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registrationProcessForProfile(code);
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }

    }

    private void registrationProcessForSocialLogin(final String code) {

        params.put("code", code);
        //       params.put("lastname", lastname);
        params.put("phone", phoneNumber);

//                    Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

        System.out.println("SignUpActivity : Param : " + params);
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {

            RequestQueue queue = Volley.newRequestQueue(this);
            //               final String finalLastname = lastname;
            if (phoneNumber != null) {
                final ProgressDialog dialog = AppUtils.showProgress(PhoneOtpScreen.this, "Otp Verification", getString(R.string.msg_please_wait));
                NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.OTP_VERIFY, params,
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
                                    Snackbar.make(binding.page, msg, Snackbar.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(PhoneOtpScreen.this, "Phone verification successful", Toast.LENGTH_LONG).show();

                                    if (!phoneNumber.isEmpty())
                                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_PHONE, phoneNumber);
                                    if (!email.isEmpty())
                                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_EMAIL, email);
                                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(true));
                                    DBHelper db = new DBHelper(PhoneOtpScreen.this);
                                    db.updateLoggedInUser(id);

                                    AppUtils.log(msg);
                                    Snackbar.make(binding.page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, id);
                                            Intent intent = new Intent(PhoneOtpScreen.this, LocationActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }, 200);

                                    finish();


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
                                Snackbar snackbar = Snackbar.make(binding.page, getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        registrationProcessForSocialLogin(code);
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
                queue.add(request);
            }

        } else {
            Snackbar snackbar = Snackbar.make(binding.page, getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registrationProcessForSocialLogin(code);
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }

    }

    public void loginAfterSignup(String email,String password,String devToken){
        final ProgressDialog dialog = AppUtils.showProgress(PhoneOtpScreen.this, getString(R.string.msg_logging_in), getString(R.string.msg_please_wait));
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
                                    Snackbar.make(binding.page, getString(R.string.msg_login_fail), Snackbar.LENGTH_LONG).show();
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
                                            DBHelper db = new DBHelper(PhoneOtpScreen.this);
                                            db.updateLoggedInUser(id);
                                            setResult(RESULT_OK);

                                        }
                                    }, 1000);
                                    Snackbar.make(binding.page, getString(R.string.msg_login_success), Snackbar.LENGTH_LONG).show();

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(PhoneOtpScreen.this, LocationActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            dialog.dismiss();
                                            finish();
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

    private void requestSmsPermission() {

        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this,permission);
        if (grant != PackageManager.PERMISSION_GRANTED){
            String[] permissionList = new String[1];
            permissionList[0] = permission;
            ActivityCompat.requestPermissions(this,permissionList,1);
        }

    }

    private void startTimer() {
        /*  TIMER  */
        new CountDownTimer(15000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                binding.resendOtp.setClickable(false);
                binding.resendOtp.setText(getResources().getString(R.string.resend_after)+String.format(" %2d "+getResources().getString(R.string.Seconds),
                                TimeUnit.SECONDS.toSeconds(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished))));
                timerNow = String.format(" %2d "+getResources().getString(R.string.Seconds),
                        TimeUnit.SECONDS.toSeconds(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)));

            }

            public void onFinish() {
                binding.resendOtp.setText(getResources().getString(R.string.resend_otp));
                binding.resendOtp.setClickable(true);
                // binding.tvCountDown.setText("done!");
            }
        }.start();
    }

    @Override
    public void onBackPressed() {

        if (binding.resendOtp.getText().toString().equalsIgnoreCase(getString(R.string.resend_otp))) {
            super.onBackPressed();
            overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
        }
        else{
            Toast.makeText(PhoneOtpScreen.this, getString(R.string.Please_wait_for)+timerNow+getString(R.string.to_go_back), Toast.LENGTH_LONG).show();
        }
    }
}