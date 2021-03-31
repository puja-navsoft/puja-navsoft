package com.afieat.ini;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afieat.ini.utils.AppUtils;

public class CardPaymentActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);
        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        assert mToolbar != null;
        mToolbar.setTitle(getString(R.string.title_card_payment));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView webView;
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(getIntent().getStringExtra(AppUtils.EXTRA_PAYMENT_URL));
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            AppUtils.log("URL: " + url);
            if ("http://afieat.com/webs/success_payment".equals(url)) {
                startActivity(new Intent(CardPaymentActivity.this, PaymentSuccessActivity.class)
                        .putExtra(AppUtils.EXTRA_RESTAURANT_ID
                                , getIntent().getStringExtra(AppUtils.EXTRA_RESTAURANT_ID)));
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }
}
