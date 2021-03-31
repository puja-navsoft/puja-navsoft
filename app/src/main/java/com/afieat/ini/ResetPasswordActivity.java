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
import android.util.Log;
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

public class ResetPasswordActivity extends AppCompatActivity {
    private Map<String, String> params;

    private TextInputEditText textCode, textPassword, textConfirmPassword;
    private Button btnResetPassword;
    private RelativeLayout page;

    public ResetPasswordActivity() {
        params = new HashMap<>();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

         Toolbar mToolbar;
        page = (RelativeLayout) findViewById(R.id.page);
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_forgot_password));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_forgot_password));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));

        textCode = (TextInputEditText) findViewById(R.id.txtCode);
        textPassword = (TextInputEditText) findViewById(R.id.txtPassword);
        textConfirmPassword = (TextInputEditText) findViewById(R.id.txtConfirmPassword);
        page = (RelativeLayout) findViewById(R.id.page);
        btnResetPassword = (Button) findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
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

    public void resetPassword() {
        params.clear();
        if (isSameFields()) {
            String code = "";
            String password = "";
            String confirmPassword = "";
            code = textCode.getText().toString().trim();
            password = textPassword.getText().toString().trim();
            confirmPassword = textConfirmPassword.getText().toString().trim();
            params.put("reset_code", code);
            params.put("newpassword", password);
            params.put("confirmpassword", confirmPassword);

            Log.d("check paramss ", "" + params);
            if (AppUtils.isNetworkAvailable(getApplicationContext())) {
                final ProgressDialog dialog = AppUtils.showProgress(ResetPasswordActivity.this, getString(R.string.msg_reseting_password), getString(R.string.msg_please_wait));
                NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.RESET_PASSWORD, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                String message = response.optString("msg");
                                AppUtils.hideProgress(dialog);
                                AppUtils.log(response + "");
                                if ("0".equals(response.optString("code"))) {
                                    Snackbar.make(page, getString(R.string.msg_reseting_failed), Snackbar.LENGTH_LONG).show();
                                    Snackbar.make(page, message, Snackbar.LENGTH_LONG).show();

                                } else if ("1".equals(response.optString("code")) || "success".equals(response.optString("msg"))) {

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Snackbar.make(page, getString(R.string.msg_reset_password_success), Snackbar.LENGTH_LONG).show();
                                            finish();
                                        }
                                    }, 1000);
                                    Snackbar.make(page, message, Snackbar.LENGTH_LONG).show();
                                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
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
        } else {
            Snackbar.make(page, getString(R.string.msg_password_not_matched), Snackbar.LENGTH_LONG).show();
        }
    }

    private boolean isSameFields() {
        boolean flag = false;
        if ((textPassword.getText().toString().trim()).equals((textConfirmPassword.getText().toString().trim()))) {
            flag = true;
        }
        return flag;
    }
}
