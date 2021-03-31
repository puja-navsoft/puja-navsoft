package com.afieat.ini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.database.DBHelper;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.vlk.multimager.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class PhoneActivity extends AppCompatActivity {

    TextView code,topText;
    AppCompatEditText phoneField, emailField;
    Button submit;
    private Map<String, String> params;
    private RelativeLayout page;
    LinearLayout phoneLayout, emailLayout;
    AppInstance appInstance;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle("Enter phone number");
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
//        setSupportActionBar(mToolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }

        page = findViewById(R.id.page);
        params = new HashMap<>();
        appInstance = AppInstance.getInstance(this);
        userId = getIntent().getStringExtra("id");
        phoneField = findViewById(R.id.phoneField);
        emailField = findViewById(R.id.emailField);
        submit = findViewById(R.id.phSubmit);
        phoneLayout = findViewById(R.id.phoneLayout);
        emailLayout = findViewById(R.id.emailLayout);
        topText = findViewById(R.id.topText);

        if (appInstance.getFromSharedPref(AppUtils.PREF_USER_EMAIL).isEmpty())
            emailLayout.setVisibility(View.VISIBLE);
        if (appInstance.getFromSharedPref(AppUtils.PREF_USER_PHONE).isEmpty())
            phoneLayout.setVisibility(View.VISIBLE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = "",email = "";
                if (!phoneField.getText().toString().isEmpty() && phoneField.getText().toString().length()>9){
//                    phoneUpdate(phoneField.getText().toString());
                    phone = phoneField.getText().toString();
                }
                else {
                    if (phoneField.getText().toString().length() < 9)
                        phoneField.setError("Please enter a proper phone number");
                    else if (phoneField.getText().toString().isEmpty())
                        phoneField.setError(getString(R.string.msg_field_mandatory));
                }

                if (!emailField.getText().toString().isEmpty() && isValid(emailField.getText().toString())){
                    email = emailField.getText().toString();
                }
                else {

                    if (emailField.getText().toString().isEmpty())
                        emailField.setError(getString(R.string.msg_field_mandatory));
                    else if (!isValid(emailField.getText().toString()))
                        emailField.setError("Please enter a proper email id");

                }

                if (appInstance.getFromSharedPref(AppUtils.PREF_USER_EMAIL).isEmpty() && appInstance.getFromSharedPref(AppUtils.PREF_USER_PHONE).isEmpty()) {
                    if (!phone.isEmpty() && !email.isEmpty())
                        phoneUpdate(phone, email);
                }
                else {
                    if (!phone.isEmpty())
                        phoneUpdate(phone, "");
                    else if (!email.isEmpty())
                        phoneUpdate("", email);
                }
            }
        });

    }

    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private void phoneUpdate(final String phone, final String email){
        final String devToken = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.SHARED_PREF_DEV_FIRE_BASE_TOKEN);

        if (devToken.length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_no_token), Toast.LENGTH_LONG).show();
            return;
        }

        params.put("id", userId);
        if (!phone.isEmpty())
            params.put("phone", phone);
        if (!email.isEmpty())
            params.put("email",email);

        Log.d("check_sign_up", "  test " + params);
//                    Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

        System.out.println("SignUpActivity : Param : " + params);
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {

            final ProgressDialog dialog = AppUtils.showProgress(PhoneActivity.this, getString(R.string.msg_creating_account), getString(R.string.msg_please_wait));
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.USER_PHONE, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            AppUtils.log(response.toString());
                            AppUtils.hideProgress(dialog);
                            String msg = response.optString("msg");
                            String code = response.optString("status");
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

                                final String userID = (String) response.opt("id");
                                if (!phone.isEmpty())
                                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_PHONE, phone);
                                if (!email.isEmpty())
                                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_EMAIL, email);
                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(true));
                                DBHelper db = new DBHelper(PhoneActivity.this);
                                db.updateLoggedInUser(userID);

                                AppUtils.log(msg);
                                Snackbar.make(page, getString(R.string.msg_account_created), Snackbar.LENGTH_LONG).show();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, userId);
                                        Intent intent = new Intent(PhoneActivity.this, CategoryListActivity.class);
                                        startActivity(intent);
                                    }
                                }, 200);

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Snackbar snackbar = Snackbar.make(page, getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
                            snackbar.show();
                            AppUtils.hideProgress(dialog);
                        }
                    }
            );
            AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);

        } else {
            Snackbar snackbar = Snackbar.make(page, getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }


    }
}