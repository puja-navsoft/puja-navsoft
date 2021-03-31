package com.afieat.ini;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afieat.ini.models.ChatLink;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.webservice.ApiClient;
import com.afieat.ini.webservice.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;

public class ChatCenterActivity extends AppCompatActivity {
    //
    private WebView webHelpCenter;
    String name,email,phone;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_center);
        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_help_center));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String LangId = "en";
        if (!"en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            LangId = "ar";
        }

//        NetworkRequest networkRequest=new NetworkRequest(com.android.volley.Request.Method.GET, Apis.HELPCENTER+"?lang="+LangId, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                JSONArray helpandsupport=response.optJSONArray("helpandsupportsArr_android");
//                recyclerView.setAdapter(new Helpe_Adapter(ChatCenterActivity.this,helpandsupport));
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });

//        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(networkRequest);
        name = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_FNAME);
        String[] fname=name.split(" ");
        name=fname[0];
        email = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_EMAIL);
        phone = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_PHONE);


       // WEB_URL="http://afieat.com/chat.html?a="+name.trim().replace(" ","")+"&b="+email.trim().replace(" ","")+"&c="+phone.trim().replace(" ","");



        webHelpCenter = (WebView) findViewById(R.id.webHelpCenter);
        assert webHelpCenter != null;
       /* webHelpCenter.getSettings().setJavaScriptEnabled(true);
        webHelpCenter.getSettings().setAllowFileAccess(true);
        webHelpCenter.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webHelpCenter.loadUrl("http://34.205.164.176/chat.html");
        webHelpCenter.loadUrl(WEB_URL);

        webHelpCenter.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webHelpCenter.loadUrl(url);
                AppUtils.log("@@ KK-" + url);
                return false;
            }
        });*/

        getChatLinkNW(name,email,phone);

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

    private void getChatLinkNW(String name, String email, String phone) {
        try {

            ApiInterface apiService = ApiClient.GetClient().create(ApiInterface.class);
            Call<ChatLink> call = apiService.getChat(name,email,phone);
            call.enqueue(new Callback<ChatLink>() {
                @Override
                public void onResponse(Call<ChatLink> call, retrofit2.Response<ChatLink> response) {
                    if(response!=null){

                      //  Log.e("CHAT_LINK :",""+response.toString());

                        chatView(response.body().getChatLink());
                    }

                }

                @Override
                public void onFailure(Call<ChatLink> call, Throwable t) {
                    System.out.print(t.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chatView(String chatUrl){

        webHelpCenter.getSettings().setJavaScriptEnabled(true);
        webHelpCenter.getSettings().setAllowFileAccess(true);
        webHelpCenter.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webHelpCenter.loadUrl("http://34.205.164.176/chat.html");
        webHelpCenter.loadUrl(chatUrl);

        webHelpCenter.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webHelpCenter.loadUrl(url);
                AppUtils.log("@@ KK-" + url);
                return false;
            }
        });

    }

}
