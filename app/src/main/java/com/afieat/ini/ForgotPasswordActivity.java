package com.afieat.ini;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {
    private Map<String, String> params;

    private TextInputEditText textEmail;
    private Button buttonContinue;
    private RelativeLayout page;

    public ForgotPasswordActivity() {
        params = new HashMap<>();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
         Toolbar mToolbar;
        page = (RelativeLayout) findViewById(R.id.page);
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_forgot_password));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        textEmail = (TextInputEditText) findViewById(R.id.textEmail);
        page = (RelativeLayout) findViewById(R.id.page);
        buttonContinue = (Button) findViewById(R.id.btnContnue);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEmail();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    public void submitEmail() {
        params.clear();
//        if (isValidFields())
        {
            String email = "";
            email = textEmail.getText().toString().trim();
            params.put("email", email);
            params.put("lang", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG));

            if (AppUtils.isNetworkAvailable(getApplicationContext())) {
                final ProgressDialog dialog = AppUtils.showProgress(ForgotPasswordActivity.this, getString(R.string.msg_email_sms), getString(R.string.msg_please_wait));

                NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.FORGOT_PASSWORD, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                AppUtils.hideProgress(dialog);
                                AppUtils.log(response + "");
                                if ("0".equals(response.optString("code"))) {
//                                            Snackbar.make(page, getString(R.string.msg_phone_sending_fail), Snackbar.LENGTH_LONG).show();
                                      Snackbar.make(page, response.optString("msg"), Snackbar.LENGTH_LONG).show();
                                } else if ("1".equals(response.optString("code")) || "success".equals(response.optString("msg"))) {
                                    Snackbar.make(page, getString(R.string.msg_code_send_success), Snackbar.LENGTH_LONG).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 1000);
                                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                                    startActivity(intent);

                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                AppUtils.hideProgress(dialog);
                                error.printStackTrace();
                            }
                        }
                );
                AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
            }
        }
    }

    private boolean isValidFields() {
        boolean flag = true;
        if (!AppUtils.isValidEmail(textEmail.getText().toString().trim())) {
            textEmail.setError(getString(R.string.msg_email_invalid));
            flag = false;
        }
        return flag;
    }

}
