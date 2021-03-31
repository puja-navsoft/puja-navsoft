package com.afieat.ini;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afieat.ini.Security.Relogin;
import com.afieat.ini.adapters.OrdersAdapter;
import com.afieat.ini.models.Order;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersActivity extends AppCompatActivity {

    // private ProgressBar progressBar;

    private ListView lvOrders;
    Dialog afieatGifLoaderDialog;
    private List<Order> orders;
    private String mUserId;

    public OrdersActivity() {
        mUserId = "";
        orders = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
         Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_my_orders));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mUserId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        lvOrders = (ListView) findViewById(R.id.lvOrders);

        if (mUserId.length() > 0) {
            fetchPastOrdersFromNW();
        } else {
            startActivityForResult(new Intent(OrdersActivity.this, LoginActivity.class), AppUtils.REQ_LOGIN);
        }
    }

    private void fetchPastOrdersFromNW() {
        // AppUtils.showViews(progressBar);
        afieatGifLoaderDialog();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", mUserId);
        params.put("lmt", "50");
        /*params.put("lmt", "10");
        params.put("ofst", "7");*/
        if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            params.put("lang", "en");
        }
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_MY_ORDERS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  AppUtils.hideViews(progressBar);
                        afieatGifLoaderDialog.dismiss();
                        AppUtils.log(response);

                        if (response.has("status") && response.optInt("status") == 999) {
                            new Relogin(OrdersActivity.this, new Relogin.OnLoginlistener() {
                                @Override
                                public void OnLoginSucess() {
                                    fetchPastOrdersFromNW();
                                }

                                @Override
                                public void OnError(String Errormessage) {
                                    startActivityForResult(new Intent(OrdersActivity.this, LoginActivity.class), AppUtils.REQ_LOGIN);
                                }
                            }).execute();
                        } else {
                            JSONObject orderDetailsObject = response.optJSONObject("order_details");
                            if (orderDetailsObject != null) {
                                JSONArray orderArray = orderDetailsObject.optJSONArray("order");
                                for (int i = 0; i < orderArray.length(); i++) {
                                    JSONObject orderObject = orderArray.optJSONObject(i);
                                    Order order = new Order();
                                    order.setOrderId(orderObject.optString("order_id"));
                                    order.setMerchantName(orderObject.optString("restaurant_name"));
                                    order.setMerchantPic(orderObject.optString("merchant_photo_bg"));
                                    order.setOrderType(orderObject.optString("order_type"));
                                    order.setMerchantId(orderObject.optString("merchant_id"));
                                    order.setOrderNo(orderObject.optString("order_number"));
                                    order.setDelDate(orderObject.optString("delivery_date"));
                                    order.setDelTime(orderObject.optString("delivery_time"));
                                    order.setAmtTotal(AppUtils.changeToArabic(orderObject.optString("voucher_amount"), getApplicationContext()));
                                    order.setStatus(orderObject.optString("stats_id"));
                                    order.setQuantity(orderObject.optString("qty"));
                                    orders.add(order);
                                }
                                lvOrders.setAdapter(new OrdersAdapter(orders, getApplicationContext()));
                                lvOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Order order = orders.get(position);
                                        String orderId = order.getOrderId();
                                        startActivity(new Intent(OrdersActivity.this, OrderDetailsActivity.class).putExtra(AppUtils.EXTRA_ORDER_ID, orderId));
                                    }
                                });
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  AppUtils.hideViews(progressBar);
                        afieatGifLoaderDialog.dismiss();
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppUtils.REQ_LOGIN:
                if (resultCode == RESULT_OK) {
                    mUserId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);
                    fetchPastOrdersFromNW();
                } else {
                    finish();
                }
                break;
            default:
                break; }
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


    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(this);
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }
}
