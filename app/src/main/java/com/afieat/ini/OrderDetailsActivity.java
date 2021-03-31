package com.afieat.ini;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afieat.ini.models.Food;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class OrderDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String mOrderId;


    private List<Food> foods;
    Dialog afieatGifLoaderDialog;
    private LinearLayout llOrderItems, llDeliveryBoy;
    private TextView tvTotalQty, tvProdPrice, tvOrderNo, tvDiscountPrice, tvPromoPrice, tvDeliveryCharge, tvTotalPrice;
    private TextView tvRestName, tvDeliveryAddress, tvDriverName;


    private View circleInProcess, lineOnTheWay, circleOnTheWay, lineDelivered, circleDelivered;

    private GoogleMap map;
    private Marker mMarker;
    TextView tvPhNo;

    public OrderDetailsActivity() {
        foods = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
         Toolbar mToolbar;
        mToolbar = findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_order_details));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvPhNo = findViewById(R.id.tvPhNo);
        llDeliveryBoy = findViewById(R.id.llDeliveryBoy);
        llOrderItems = findViewById(R.id.llOrderItems);
        tvTotalQty = findViewById(R.id.tvTotalQty);
        tvProdPrice = findViewById(R.id.tvProdPrice);
        tvDiscountPrice = findViewById(R.id.tvDiscountPrice);
        tvPromoPrice = findViewById(R.id.tvPromoPrice);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvOrderNo = findViewById(R.id.tvOrderNo);
        tvRestName = findViewById(R.id.tvRestName);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);

        circleInProcess = findViewById(R.id.circleInProcess);
        lineOnTheWay = findViewById(R.id.lineOnTheWay);
        circleOnTheWay = findViewById(R.id.circleOnTheWay);
        lineDelivered = findViewById(R.id.lineDelivered);
        circleDelivered = findViewById(R.id.circleDelivered);

        mOrderId = getIntent().getStringExtra(AppUtils.EXTRA_ORDER_ID);

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrag);
//        mapFragment.getMapAsync(this);


//        FragmentManager fmanager = getSupportFragmentManager();
//        Fragment fragment = fmanager.findFragmentById(R.id.mapFrag);
//        SupportMapFragment supportmapfragment = (SupportMapFragment)fragment;
//        GoogleMap supportMap = supportmapfragment.getMap();


        findViewById(R.id.track_driver_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailsActivity.this, TrackYourOrderActivity.class);
                intent.putExtra(AppUtils.EXTRA_ORDER_ID, mOrderId);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrderDetailsFromNW();
    }

    class UpdateDriverPositionTask extends TimerTask {

        @Override
        public void run() {
            Map<String, String> params = new HashMap<>();
            params.put("order_id", mOrderId);
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.TRACK_DRIVER, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            LatLng driverLatLng = new LatLng(response.optDouble("latitude"), response.optDouble("longitude"));
                            if (mMarker != null) mMarker.remove();
                            mMarker = map.addMarker(new MarkerOptions()
                                    .position(driverLatLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_del_boy)));
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatLng, 12.0f));
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

    private void loadOrderDetailsFromNW() {
        // final ProgressDialog dialog = AppUtils.showProgress(OrderDetailsActivity.this, "", getString(R.string.msg_please_wait));
        afieatGifLoaderDialog();
        foods.clear();
        try {
            Map<String, String> params = new HashMap<>();
            params.put("order_id", mOrderId);
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.ORDER_DETAILS, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            AppUtils.log(response);
                            //AppUtils.hideProgress(dialog);
                            afieatGifLoaderDialog.dismiss();
                            JSONArray orderDetailsArray = response.optJSONArray("order_detail");
                            if (orderDetailsArray != null) {
                                JSONObject orderBasicInfoObject = orderDetailsArray.optJSONObject(0);
                                JSONObject restaurantObject = orderDetailsArray.optJSONObject(1);
                                JSONObject clientObject = orderDetailsArray.optJSONObject(2);
                                JSONArray itemsArray = orderDetailsArray.optJSONArray(3);

                                if (itemsArray != null) {
                                    for (int i = 0; i < itemsArray.length(); i++) {
                                        JSONObject foodObject = itemsArray.optJSONObject(i);
                                        Food food = new Food();
                                        food.setId(foodObject.optString("item_id"));
                                        food.setName(foodObject.optString("item_name"));
                                        food.setBasketCount(foodObject.optString("qty"));
                                        food.setAddOns(foodObject.optString("addon"));
                                        food.setPriceBasket(AppUtils.changeToArabic(foodObject.optString("normal_price"), getApplicationContext()));
                                        foods.add(food);
                                    }
                                    showOrderItems(foods);
                                }
                                tvOrderNo.setText(orderBasicInfoObject.optString("order_number"));
                                tvRestName.setText(restaurantObject.optString("restaurant_name"));
                                if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {
                                    tvRestName.setText(restaurantObject.optString("restaurant_name_ar"));
                                }
                                String deliveryAddress="";
                                if (clientObject!=null) {
                                    deliveryAddress = clientObject.optString("street");
                                    if (clientObject.optString("city").length() > 0) {
                                        deliveryAddress += "\n" + clientObject.optString("city");
                                    }
                                    if (clientObject.optString("state").length() > 0) {
                                        deliveryAddress += "\n" + clientObject.optString("state");
                                    }
                                }
//                            String clientLat = clientObject.optString("latitude");
//                            String clientLng = clientObject.optString("longitude");
//                            if (!(clientLat.equals("") || clientLng.equals(""))) {
//                                if(!(clientLat.toString().equalsIgnoreCase("null") && clientLng.toString().equalsIgnoreCase("null"))){
//                                    Log.i("ctese---------", "" + clientLat);
//
//                                    LatLng clientLatLng = new LatLng(Double.parseDouble(clientLat), Double.parseDouble(clientLng));
//                                    if (map != null) {
//                                        map.addMarker(new MarkerOptions()
//                                                .position(clientLatLng)
//                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_home)));
//                                    } else {
//                                        AppUtils.log("MAP IS NULL");
//                                    }
//                                }
//
//                            }
                                System.out.println("test-------" + "  " + orderBasicInfoObject.optString("status"));
                                if (("In Process".equals(orderBasicInfoObject.optString("status")) || "In the way".equals(orderBasicInfoObject.optString("status")))) {
                                    if ("In Process".equals(orderBasicInfoObject.optString("status"))) {
                                        circleInProcess.setBackgroundResource(R.drawable.circle_green);
                                    }
                                    if ("In the way".equals(orderBasicInfoObject.optString("status"))) {
                                        circleInProcess.setBackgroundResource(R.drawable.circle_green);
                                        circleOnTheWay.setBackgroundResource(R.drawable.circle_green);
                                        lineOnTheWay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.greenButton));
                                    }

                                    findViewById(R.id.track_driver_btn).setVisibility(View.VISIBLE);
                                    if (!"0".equals(orderBasicInfoObject.optString("assign_driver_id"))) {
//
                                        JSONObject driverObject = orderDetailsArray.optJSONObject(4);
                                        tvDriverName = findViewById(R.id.tvDriverName);
                                        tvDriverName.setText(driverObject.optString("first_name") + " " + driverObject.optString("last_name"));
                                        tvPhNo.setText(driverObject.optString("phone_no"));
                                    } else {
                                       // findViewById(R.id.track_driver_btn).setVisibility(View.GONE);

                                    }
                                } else {
                                    findViewById(R.id.track_driver_btn).setVisibility(View.GONE);
                                    // AppUtils.hideViews(flMapContainer, llDeliveryBoy);
                                    if (!"0".equals(orderBasicInfoObject.optString("assign_driver_id"))) {
                                        circleInProcess.setBackgroundResource(R.drawable.circle_green);
                                        circleOnTheWay.setBackgroundResource(R.drawable.circle_green);
                                        lineOnTheWay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.greenButton));
                                        lineDelivered.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.greenButton));
                                        circleDelivered.setBackgroundResource(R.drawable.circle_green);
                                        JSONObject driverObject = orderDetailsArray.optJSONObject(4);
                                        tvDriverName = findViewById(R.id.tvDriverName);
                                        tvDriverName.setText(driverObject.optString("first_name") + " " + driverObject.optString("last_name"));
                                        tvPhNo.setText(driverObject.optString("phone_no"));
                                    }
                                }
                                tvDeliveryAddress.setText(deliveryAddress);
                                tvDiscountPrice.setText(getString(R.string.currency) + AppUtils.changeToArabic(orderBasicInfoObject.optString("restaurant_discount"), getApplicationContext()));
                                tvPromoPrice.setText(getString(R.string.currency) + AppUtils.changeToArabic(orderBasicInfoObject.optString("promo_voucher_discount"), getApplicationContext()));
                                tvDeliveryCharge.setText(getString(R.string.currency) + AppUtils.changeToArabic(orderBasicInfoObject.optString("delivery_charge"), getApplicationContext()));
                                if (Float.parseFloat(orderBasicInfoObject.optString("voucher_amount")) <= 0.00) {
                                    tvTotalPrice.setText(getString(R.string.currency) + "0.00");
                                } else {
                                    tvTotalPrice.setText(getString(R.string.currency) + AppUtils.changeToArabic(orderBasicInfoObject.optString("voucher_amount"), getApplicationContext()));
                                }

                                //          showMapMarkers(restaurantObject, clientObject);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            //AppUtils.hideProgress(dialog);
                            afieatGifLoaderDialog.dismiss();
                        }
                    }
            );
            AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
        } catch (Exception e) {
            e.printStackTrace();
            afieatGifLoaderDialog.dismiss();
            //AppUtils.hideProgress(dialog);
        }

    }


    private void showOrderItems(List<Food> foods) {
        int totalQty = 0;
        double totalPrice = 0;

        llOrderItems.removeAllViews();

        for (Food food : foods) {
            View layout = LayoutInflater.from(OrderDetailsActivity.this).inflate(R.layout.layout_orders_detail_item, null, false);
            ((TextView) layout.findViewById(R.id.tvProdName)).setText(food.getName());
            ((TextView) layout.findViewById(R.id.tvProdQty)).setText(food.getBasketCount());
            ((TextView) layout.findViewById(R.id.tvProdPrice)).setText(food.getPriceBasket());
            llOrderItems.addView(layout);
            totalQty += Integer.parseInt(food.getBasketCount());
            totalPrice += Double.parseDouble(AppUtils.changeToEnglish(food.getPriceBasket()));
        }
        tvTotalQty.setText(String.valueOf(totalQty));
        tvProdPrice.setText(getString(R.string.currency) + AppUtils.changeToArabic(String.valueOf(totalPrice), getApplicationContext()));
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

    @Override
    public void onMapReady(GoogleMap googleMap) {


        map = googleMap;
        System.out.println("Map ready---------------");
        loadOrderDetailsFromNW();

    }


    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(this);
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }
}
