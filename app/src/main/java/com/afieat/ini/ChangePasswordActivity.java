package com.afieat.ini;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

public class ChangePasswordActivity extends AppCompatActivity {


    private TextInputEditText tetOldPassword, tetNewPassword, tetConfirmPassword;
    private Button btnUpdatePassword;
    private ProgressBar progressBar;
    private RelativeLayout page;
//    private boolean isOldPasswordVisible, isNewPasswordVisible;

  /*  public ChangePasswordActivity() {
//        isOldPasswordVisible = false;
//        isNewPasswordVisible = false;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
         Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_update_password));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tetOldPassword = (TextInputEditText) findViewById(R.id.tetOldPassword);
        tetNewPassword = (TextInputEditText) findViewById(R.id.tetNewPassword);
        tetConfirmPassword = (TextInputEditText) findViewById(R.id.tetConfirmPassword);
        btnUpdatePassword = (Button) findViewById(R.id.btnUpdatePassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        page = (RelativeLayout) findViewById(R.id.page);

//        tetOldPassword.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("en")) {
//                        if (event.getRawX() >= (tetOldPassword.getRight() - tetOldPassword.getCompoundDrawables()[2].getBounds().width())) {
//                            int drawable = isOldPasswordVisible ? R.drawable.ic_visibility_black_18dp : R.drawable.ic_visibility_off_black_18dp;
//                            TransformationMethod method = isOldPasswordVisible ? new PasswordTransformationMethod() : null;
//                            isOldPasswordVisible = !isOldPasswordVisible;
//                            tetOldPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
//                            tetOldPassword.setTransformationMethod(method);
//                            return true;
//                        }
//                    } else {
//                        if (event.getRawX() <= (tetOldPassword.getLeft() - tetOldPassword.getCompoundDrawables()[0].getBounds().width())) {
//                            int drawable = isOldPasswordVisible ? R.drawable.ic_visibility_black_18dp : R.drawable.ic_visibility_off_black_18dp;
//                            TransformationMethod method = isOldPasswordVisible ? new PasswordTransformationMethod() : null;
//                            isOldPasswordVisible = !isOldPasswordVisible;
//                            tetOldPassword.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
//                            tetOldPassword.setTransformationMethod(method);
//                            return true;
//                        }
//                    }
//                }
//                return false;
//            }
//        });
//
//        tetNewPassword.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("en")) {
//                        if (event.getRawX() >= (tetNewPassword.getRight() - tetNewPassword.getCompoundDrawables()[2].getBounds().width())) {
//                            int drawable = isNewPasswordVisible ? R.drawable.ic_visibility_black_18dp : R.drawable.ic_visibility_off_black_18dp;
//                            TransformationMethod method = isNewPasswordVisible ? new PasswordTransformationMethod() : null;
//                            isNewPasswordVisible = !isNewPasswordVisible;
//                            tetNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
//                            tetNewPassword.setTransformationMethod(method);
//                            return true;
//                        }
//                    } else {
//                        if (event.getRawX() <= (tetNewPassword.getLeft() - tetNewPassword.getCompoundDrawables()[0].getBounds().width())) {
//                            int drawable = isNewPasswordVisible ? R.drawable.ic_visibility_black_18dp : R.drawable.ic_visibility_off_black_18dp;
//                            TransformationMethod method = isNewPasswordVisible ? new PasswordTransformationMethod() : null;
//                            isNewPasswordVisible = !isNewPasswordVisible;
//                            tetNewPassword.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
//                            tetNewPassword.setTransformationMethod(method);
//                            return true;
//                        }
//                    }
//
//                }
//                return false;
//            }
//        });

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldsValid()) {
                    updatePasswordToNW();
                }
            }
        });
    }

    private void updatePasswordToNW() {
        AppUtils.showViews(progressBar);
        Map<String, String> params = new HashMap<>();
        if (isValidFields()) {
            params.put("user_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
            params.put("newpwd", tetNewPassword.getText().toString());
            params.put("oldpwd", tetOldPassword.getText().toString());
            Log.d("anu chk     ", "" + params);
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.CHANGE_PASSWORD, params,
                    new Response.Listener<JSONObject>() {
                        public String message;

                        @Override
                        public void onResponse(JSONObject response) {
                            AppUtils.log(response);
                            message = response.optString("msg");
                            AppUtils.hideViews(progressBar);
                            Snackbar.make(page, message, Snackbar.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AppUtils.hideViews(progressBar);
                            error.printStackTrace();
                        }
                    }
            );
            AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
        } else {
            AppUtils.hideViews(progressBar);
            // Snackbar.make(page, getString(R.string.msg_password_matched), Snackbar.LENGTH_LONG).show();
        }

    }

    private boolean isValidFields() {
        Boolean flag = true;

        String newPassword,
                confirmPassword;

        newPassword = tetNewPassword.getText().toString();
        confirmPassword = tetConfirmPassword.getText().toString();
        if (tetNewPassword.getText().toString().equals(tetOldPassword.getText().toString())) {
            Snackbar.make(page, getString(R.string.msg_password_matched), Snackbar.LENGTH_LONG).show();
            flag = false;
        }

        if (newPassword.trim().length() < 6) {
            Snackbar.make(page, getString(R.string.msg_password_min_6_chars), Snackbar.LENGTH_LONG).show();
            flag = false;
        }

        if (!newPassword.equals(confirmPassword)) {
            //       tetConfirmPassword.setError(getString(R.string.msg_confirm_password_matched));
            Snackbar.make(page, getString(R.string.msg_confirm_password_matched), Snackbar.LENGTH_LONG).show();
            flag = false;
        }
        return flag;
    }

    private boolean fieldsValid() {
        if (tetOldPassword.getText().toString().length() == 0) {
            tetOldPassword.setError(getString(R.string.msg_field_mandatory));
            return false;
        }
        if (tetNewPassword.getText().toString().length() == 0) {
            tetNewPassword.setError(getString(R.string.msg_field_mandatory));
            return false;
        }
        return true;
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
}