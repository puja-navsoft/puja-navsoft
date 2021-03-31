package com.afieat.ini;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;

public class LanguageSelectionActivity extends AppCompatActivity {


    private RadioButton rbLangAr, rbLangEn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
         Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        String langChoice = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG);
        if ("ar".equals(langChoice))
            mToolbar.setTitle("لغة");
        else
            mToolbar.setTitle("Language");
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         RadioGroup rgLang;
        rgLang = (RadioGroup) findViewById(R.id.rgLang);
        rbLangAr = (RadioButton) findViewById(R.id.rbLangAr);
        rbLangEn = (RadioButton) findViewById(R.id.rbLangEn);

        if ("ar".equals(langChoice)) {
            rbLangAr.setChecked(true);
        } else {
            rbLangEn.setChecked(true);
        }

        rgLang.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbLangAr:
                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_LANG, "ar");
                        setResult(RESULT_OK);
                        break;
                    case R.id.rbLangEn:
                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_LANG, "en");
                        setResult(RESULT_OK);
                        break;
                    default:
                        break;}
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
