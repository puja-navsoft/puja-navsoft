package com.afieat.ini;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class CheckInActivity extends AppCompatActivity {



    private final int REQ_SIGNUP = 100;
    private final int REQ_LOGIN = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
         Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_check_in));
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(5f);
        }*/
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onLoginButtonClicked(View view) {
        Intent intent = new Intent(CheckInActivity.this, LoginActivity.class);
        startActivityForResult(intent, REQ_LOGIN);
    }

    public void onSignUpButtonClicked(View view) {
        Intent intent = new Intent(CheckInActivity.this, SignUpActivity.class);
        startActivityForResult(intent, REQ_SIGNUP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;   }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_LOGIN:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            case REQ_SIGNUP:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            default:
                break; }
    }

    @Override
    public void onBackPressed() {
        finish();
//        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }
}
