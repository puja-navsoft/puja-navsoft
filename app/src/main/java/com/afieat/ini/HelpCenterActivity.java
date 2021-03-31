package com.afieat.ini;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.afieat.ini.adapters.Helpe_Adapter;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

public class HelpCenterActivity extends AppCompatActivity {

    //    private WebView webHelpCenter;

    private RecyclerView recyclerView;
    private Helpe_Adapter Adapater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);
        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_help_center));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String LangId = "en";
        if (!"en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            LangId = "ar";
        }

        NetworkRequest networkRequest = new NetworkRequest(com.android.volley.Request.Method.GET, Apis.HELPCENTER + "?lang=" + LangId, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray helpandsupport = response.optJSONArray("helpandsupportsArr_android");
                recyclerView.setAdapter(new Helpe_Adapter(HelpCenterActivity.this, helpandsupport));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(networkRequest);
//        webHelpCenter = (WebView) findViewById(R.id.webHelpCenter);
//        assert webHelpCenter != null;
//        webHelpCenter.getSettings().setJavaScriptEnabled(true);
//        webHelpCenter.loadUrl("http://afieat.com/chat.html");

        findViewById(R.id.fab_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(HelpCenterActivity.this, ChatCenterActivity.class));
               // startActivity(new Intent(HelpCenterActivity.this, ReviewActivity.class));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:

                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }


}
