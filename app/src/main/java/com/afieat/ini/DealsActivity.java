package com.afieat.ini;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afieat.ini.Security.Relogin;
import com.afieat.ini.adapters.DealListAdapter;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DealsActivity extends AppCompatActivity {


    private AppInstance appInstance;
    private String mUserId = "";
    // private ProgressBar progressBar = null;
    private LayoutInflater inflater = null;
    private ListView dealsList = null;
    private Dialog afieatGifLoaderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);
         Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        assert mToolbar != null;
        mToolbar.setTitle(getString(R.string.title_my_deals));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appInstance = AppInstance.getInstance(getApplicationContext());
        mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        dealsList = (ListView) findViewById(R.id.rcv_deals_list);


        flashSaleView();
    }

    private void flashSaleView() {
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {

            AppUtils.log("test-userid  " + mUserId);
            AppUtils.log("test-lang" + appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG));

            //  AppUtils.showViews(progressBar);
            afieatGifLoaderDialog();
            Map<String, String> params = new HashMap<>();
            params.put("lang", appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG));
            params.put("city_id", appInstance.getFromSharedPref(AppUtils.PREF_CITY_ID));
            if (mUserId.length() > 0) params.put("user_id", mUserId);

            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_CRAZY_DEALS, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            AppUtils.log("test-check-test  " + response.toString());

                            // AppUtils.hideViews(progressBar);
                            if (afieatGifLoaderDialog != null) {
                                afieatGifLoaderDialog.dismiss();
                            }
                            if (response.has("status") && response.optInt("status") == 999) {
                                new Relogin(DealsActivity.this, new Relogin.OnLoginlistener() {
                                    @Override
                                    public void OnLoginSucess() {
                                        flashSaleView();
                                    }

                                    @Override
                                    public void OnError(String Errormessage) {
                                        startActivityForResult(new Intent(DealsActivity.this, LoginActivity.class), AppUtils.REQ_LOGIN);
                                    }
                                }).execute();
                            } else
                                try {
                                    final int arrayLength = response.getJSONArray("details").length();

                                    if (response.getJSONArray("details").length() > 0) {
                                        inflater = LayoutInflater.from(DealsActivity.this);

                                        DealListAdapter dealListAdapter = new DealListAdapter(response.getJSONArray("details"), DealsActivity.this);
                                        dealsList.setAdapter(dealListAdapter);

                                        dealsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                try {
                                                    if ("1".equals(response.getJSONArray("details").getJSONObject(i).optString("redirection_type"))) {
                                                        JSONArray restaurentIdsArray = new JSONArray(response.getJSONArray("details").optJSONObject(i).optString("restaurant_ids"));

                                                        Log.d("@@ LLLLL-", restaurentIdsArray.toString());
                                                        String restaurentIds = null;
                                                        for (int j = 0; j < restaurentIdsArray.length(); j++) {
                                                            restaurentIds = (restaurentIdsArray.optString(j));
                                                        }
                                                        if (restaurentIds != null) {
                                                            Intent intent = new Intent(getApplicationContext(), RestaurantsDetailActivity.class);
                                                            intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, restaurentIds);
                                                            startActivity(intent);
                                                        }
                                                    } else if ("2".equals(response.getJSONArray("details").getJSONObject(i).optString("redirection_type"))) {
                                                        JSONArray itemIdsarray = new JSONArray(response.getJSONArray("details").optJSONObject(i).optString("item_ids"));
                                                        String itemIds = null;
                                                        for (int j = 0; j < 1; j++) {
                                                            itemIds = (itemIdsarray.optString(j));
                                                        }
                                                        if (itemIds != null) {
                                                            Intent intent = new Intent(getApplicationContext(), FoodAddBasketActivity.class);
                                                            intent.putExtra(AppUtils.EXTRA_FOOD_ID, itemIds);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });


//                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RestaurantListActivity.this);
//                                    View view = inflater.inflate(R.layout.dialog_restaurent_list, null);
//                                    alertDialogBuilder.setView(view);
//
//
//                                    tabbedFragements = new LinkedList<>();
//                                    viewPager = (ViewPager) view.findViewById(R.id.flash_viewPager);
//                                    viewPager.setAdapter(new CustomPagerAdapter(RestaurantListActivity.this, response.getJSONArray("details")));
//                                    AlertDialog alertDialog = alertDialogBuilder.create();
//                                    TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
//                                    tabLayout.setupWithViewPager(viewPager);
//                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                                    alertDialog.getWindow().setLayout(200, 300);
//                                    alertDialog.show();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }
            );
            AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }

    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(this);
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }
}
