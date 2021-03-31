 package com.afieat.ini;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.DataParser;
import com.afieat.ini.utils.FetchURL;
import com.afieat.ini.utils.LatLngInterpolator;
import com.afieat.ini.utils.NetworkRequest;
import com.afieat.ini.utils.TaskLoadedCallback;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vlk.multimager.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Headers;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class TrackYourOrderActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private FrameLayout flMapContainer;
    private GoogleMap mMap;
    private String mOrderId;
    private Marker mMarker;
    private Marker driverMarker;
    private PolylineOptions lineOptions = null;
    private Double clientLat, driverLat;
    private Double clientLng, driverLng;
    private String driverRotation;
    private Double restaurantLatitude;
    private Double restaurantLongitude;
    private TextView txtOrderStatus;
    private LinearLayout callLay;
    private TextView tvDriverName;
    private TextView tvPhNo;
    private Polyline currentPolyline;
    List<LatLng> latlngDirectionPoints = new ArrayList<>();
    private int index;
    private List<LatLng> polyLineList;
    private int next;
    private LatLng startPosition;
    private LatLng endPosition;
    private float v;
    private double lng,lat;
    private Handler handler;
    private boolean isFirstPosition = true;
    private Bitmap BitMapMarker;
    private Location driverLocation = null;
    private Location updatedDriverLocation = null;
    private Double oldDriverLat,oldDriverLng,updatedDriverLat,updatedDriverLng;
    private boolean animateStatus = false;
    private ArrayList<Marker> markers = new ArrayList<>();
    private Marker homeMarker;
    LatLngBounds bounds;
    Timer timer;
    private String orderType;
    private TextView tvEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_your_order);
        mOrderId = getIntent().getStringExtra(AppUtils.EXTRA_ORDER_ID);
        //       flMapContainer = (FrameLayout) findViewById(R.id.flMap);
        txtOrderStatus = (TextView) findViewById(R.id.txtOrderStatus);
        callLay = (LinearLayout) findViewById(R.id.callLay);
        tvDriverName = (TextView) findViewById(R.id.tvDriverName);
        tvPhNo = (TextView) findViewById(R.id.tvPhNo);
        tvEdt = findViewById(R.id.etdEd);


        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.del_truck_small);
        Bitmap b = bitmapdraw.getBitmap();
        BitMapMarker = Bitmap.createScaledBitmap(b, 110, 60, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        loadOrderDetailsFromNW();



        /*final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 20 seconds
                AppUtils.log("hit UPDATE");
                Timer timer = new Timer();
                timer.schedule(new UpdateDriverPositionTask(), 100, 60000);
                //  handler.postDelayed(this, 10000);

            }
        }, 20000);*/
    }

    private String getUrl(String type, Boolean status, Boolean assigned) {

        // Origin of route
        String str_origin = "origin=" + clientLat + "," + clientLng;

        // Destination of route
        String str_driver = "";
        String str_dest = "destination=" + restaurantLatitude + "," + restaurantLongitude;
        String str_midpoint = "waypoints=optimize:true|"+restaurantLatitude+","+restaurantLongitude;
        if(type.equalsIgnoreCase("driving"))
            str_driver = "destination=" + driverLat + "," + driverLng;

        // Sensor enabled
        String parameters;
//        String sensor = "sensor=false";
        // Travelling Mode
        String mode = "mode=driving";
        AppUtils.log("gmap-origin" + str_origin);
        AppUtils.log("gmap-dest" + str_dest);
        // Building the parameters to the web service
//        if (!type.equalsIgnoreCase("driver"))
//            parameters = str_origin + "&" + str_dest + "&" + "&" + str_midpoint + "&" + sensor + "&" + mode;
//        else
        if (assigned){
            if (status)
                parameters = str_origin + "&" + str_driver + "&" + str_midpoint + "&"/* + sensor + "&"*/ + mode;
            else
                parameters = str_origin + "&" + str_driver + /*"&" + sensor + */"&" + mode;
        }
        else{
            parameters = str_origin + "&" + str_dest + /*"&" + sensor + */"&" + mode;
        }




        //  String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";


        String encGMK = "QUl6YVN5REhLNktYdlZNbW1XTGVTb252dlJ6UHV5SndLRjBuRU9v";
        String decGMK = "";
        byte[] decrypt = Base64.decode(encGMK, Base64.NO_WRAP);
        try {
            decGMK = new String(decrypt, "UTF-8");
            System.out.println("Rahul : SplashActivity : generateSearchQuery : decodedTermstext : " + decGMK);
        } catch (Exception e) {

        }

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        // String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters ;

        return url;
    }

    private void loadOrderDetailsFromNW() {
        final ProgressDialog dialog = AppUtils.showProgress(TrackYourOrderActivity.this, "", getString(R.string.msg_please_wait));
        Map<String, String> params = new HashMap<>();
        params.put("order_id", mOrderId);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.ORDER_DETAILS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.log(response);
                        AppUtils.hideProgress(dialog);
                        JSONArray orderDetailsArray = response.optJSONArray("order_detail");
                        if (orderDetailsArray != null) {
                            JSONObject orderBasicInfoObject = orderDetailsArray.optJSONObject(0);
                            JSONObject restaurantObject = orderDetailsArray.optJSONObject(1);
                            JSONObject clientObject = orderDetailsArray.optJSONObject(2);
                            JSONObject itemsArray = orderDetailsArray.optJSONObject(8);

                            orderType = orderBasicInfoObject.optString("order_type");

//                            if (itemsArray != null) {
//                                for (int i=0; i<itemsArray.length(); i++) {
//                                    JSONObject foodObject = itemsArray.optJSONObject(i);
//                                    Food food = new Food();
//                                    food.setId(foodObject.optString("item_id"));
//                                    food.setName(foodObject.optString("item_name"));
//                                    food.setBasketCount(foodObject.optString("qty"));
//                                    food.setAddOns(foodObject.optString("addon"));
//                                    food.setPriceBasket(AppUtils.changeToArabic(foodObject.optString("normal_price"), getApplicationContext()));
//                                    foods.add(food);
//                                }
//                                showOrderItems(foods);
//                            }
//                            tvOrderNo.setText(orderBasicInfoObject.optString("order_number"));
//                            tvRestName.setText(restaurantObject.optString("restaurant_name"));
//                            if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {
//                                tvRestName.setText(restaurantObject.optString("restaurant_name_ar"));
//                            }
                            String deliveryAddress = clientObject.optString("street");
                            if (clientObject.optString("city").length() > 0) {
                                deliveryAddress += "\n" + clientObject.optString("city");
                            }
                            if (clientObject.optString("state").length() > 0) {
                                deliveryAddress += "\n" + clientObject.optString("state");
                            }
                            clientLat = clientObject.optDouble("latitude");
                            clientLng = clientObject.optDouble("longitude");

                            if(orderType.equalsIgnoreCase("1")){
                                restaurantLatitude = itemsArray.optDouble("pick_latitude");
                                restaurantLongitude = itemsArray.optDouble("pick_longitude");
                            }
                            else {
                                restaurantLatitude = restaurantObject.optDouble("restaurant_latitude");
                                restaurantLongitude = restaurantObject.optDouble("restaurant_longitude");
                            }
                            AppUtils.log("gmap-origin" + clientLat);
                            AppUtils.log("gmap-dest" + clientLng);
                            if (!("".equals(clientLat) || "".equals(clientLng)) && !("null".equalsIgnoreCase(clientLat.toString()) && "null".equalsIgnoreCase(clientLng.toString()))) {

                                Log.i("ctese---------", "" + clientLat);

                                LatLng clientLatLng = new LatLng(clientLat, clientLng);
                                if (mMap != null) {

                                    homeMarker = mMap.addMarker(new MarkerOptions()
                                            .position(clientLatLng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_home)));
                                } else {
                                    AppUtils.log("MAP IS NULL");
                                }

                            }
                            if (!"0".equals(orderBasicInfoObject.optString("assign_driver_id"))) {
                                timer = new Timer();
                                timer.schedule(new UpdateDriverPositionTask(), 100, 2000);
                                final JSONObject driverObject = orderDetailsArray.optJSONObject(4);
                                tvDriverName.setText(driverObject.optString("first_name") + " " + driverObject.optString("last_name"));
                                tvPhNo.setText(driverObject.optString("phone_no"));
                                callLay.setVisibility(View.VISIBLE);
                                callLay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (driverObject.optString("phone_no") != null && !driverObject.optString("phone_no").equals("")) {
                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                            intent.setData(Uri.parse("tel:" + driverObject.optString("phone_no")));
                                            startActivity(intent);
                                        }

                                    }
                                });
                            }
                            if ("In Process".equalsIgnoreCase(orderBasicInfoObject.optString("status"))) {
                                txtOrderStatus.setText(getResources().getString(R.string.txtCurrentStatus) + " " + getResources().getString(R.string.txtInProcess));
                            } else if ("On the way".equalsIgnoreCase(orderBasicInfoObject.optString("status"))) {
                                txtOrderStatus.setText(getResources().getString(R.string.txtCurrentStatus) + " " + getResources().getString(R.string.txtIntheway));
                            }


//                            System.out.println("test-------"+  "  " +orderBasicInfoObject.optString("status"));
//                            if ((orderBasicInfoObject.optString("status").equals("In Process") || orderBasicInfoObject.optString("status").equals("In the way"))) {
//                                if (orderBasicInfoObject.optString("status").equals("In Process")) {
//                                    circleInProcess.setBackgroundResource(R.drawable.circle_green);
//                                }
//                                if (orderBasicInfoObject.optString("status").equals("In the way")) {
//                                    circleInProcess.setBackgroundResource(R.drawable.circle_green);
//                                    circleOnTheWay.setBackgroundResource(R.drawable.circle_green);
//                                    lineOnTheWay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.greenButton));
//                                }
//                                if (!orderBasicInfoObject.optString("assign_driver_id").equals("0")) {
//                                    Timer timer = new Timer();
//                                    timer.schedule(new UpdateDriverPositionTask(), 100, 60000);
//                                    JSONObject driverObject = orderDetailsArray.optJSONObject(4);
//                                    tvDriverName = (TextView) findViewById(R.id.tvDriverName);
//                                    tvDriverName.setText(driverObject.optString("first_name") + " " + driverObject.optString("last_name"));
//                                } else {
//                                    //                  AppUtils.hideViews(flMapContainer, llDeliveryBoy);
//                                }
//                            } else {
//                                // AppUtils.hideViews(flMapContainer, llDeliveryBoy);
//                                if (!orderBasicInfoObject.optString("assign_driver_id").equals("0")) {
//                                    circleInProcess.setBackgroundResource(R.drawable.circle_green);
//                                    circleOnTheWay.setBackgroundResource(R.drawable.circle_green);
//                                    lineOnTheWay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.greenButton));
//                                    lineDelivered.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.greenButton));
//                                    circleDelivered.setBackgroundResource(R.drawable.circle_green);
//                                }
//                            }
//                            tvDeliveryAddress.setText(deliveryAddress);
//                            tvDiscountPrice.setText(getString(R.string.currency) + AppUtils.changeToArabic(orderBasicInfoObject.optString("restaurant_discount"), getApplicationContext()));
//                            tvPromoPrice.setText(getString(R.string.currency) + AppUtils.changeToArabic(orderBasicInfoObject.optString("promo_voucher_discount"), getApplicationContext()));
//                            tvDeliveryCharge.setText(getString(R.string.currency) + AppUtils.changeToArabic(orderBasicInfoObject.optString("delivery_charge"), getApplicationContext()));
//                            if (Float.parseFloat(orderBasicInfoObject.optString("voucher_amount")) <= 0.00) {
//                                tvTotalPrice.setText(getString(R.string.currency) + "0.00");
//                            } else {
//                                tvTotalPrice.setText(getString(R.string.currency) + AppUtils.changeToArabic(orderBasicInfoObject.optString("voucher_amount"), getApplicationContext()));
//                            }

                            showMapMarkers(restaurantObject, clientObject);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        AppUtils.hideProgress(dialog);
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void showMapMarkers(JSONObject restaurantObject, JSONObject clientObject) {
        try {


            /*LatLng clientLatLng = new LatLng(Double.parseDouble(clientObject.optString("latitude")), Double.parseDouble(clientObject.optString("longitude")));
            mMarker = mMap.addMarker(new MarkerOptions().position(clientLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_del_boy)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clientLatLng, 15.0f));*/

            String restaurantIcon = "ic_restaurantmarker";
            String butlerIcon = "ic_marker_normal";

            if (restaurantLongitude != null && restaurantLatitude != null) {
                LatLng clientLatLng = new LatLng(restaurantLatitude, restaurantLongitude);



                if (!orderType.equalsIgnoreCase("1"))
                    mMarker = mMap.addMarker(new MarkerOptions().position(clientLatLng)
                        .icon(bitmapDescriptorFromVector(this, getDrawableId(restaurantIcon))));
                else
                    mMarker = mMap.addMarker(new MarkerOptions().position(clientLatLng)
                            .icon(bitmapDescriptorFromVector(this, getDrawableId(butlerIcon))));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clientLatLng, 12.0f));
//                callDirectionApi("origin");//For Drawing Line From restaurant to Home
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getDrawableId(String name){
        try {
            Field fld = R.drawable.class.getField(name);
            return fld.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * A method to download json data from url
     */
//    private String downloadUrl(String strUrl) throws IOException {
//        String data = "";
//        InputStream iStream = null;
//        HttpURLConnection urlConnection = null;
//        try {
//            URL url = new URL(strUrl);
//
//            // Creating an http connection to communicate with url
//            urlConnection = (HttpURLConnection) url.openConnection();
//
//            // Connecting to url
//            urlConnection.connect();
//
//            // Reading data from url
//            iStream = urlConnection.getInputStream();
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
//
//            StringBuffer sb = new StringBuffer();
//
//            String line = "";
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//
//            data = sb.toString();
//            Log.d("downloadUrl", data);
//            br.close();
//
//        } catch (Exception e) {
//            Log.d("Exception", e.toString());
//        } finally {
//            iStream.close();
//            urlConnection.disconnect();
//        }
//        return data;
//    }

    class UpdateDriverPositionTask extends TimerTask {

        @Override
        public void run() {

            Map<String, String> params = new HashMap<>();
            params.put("order_id", mOrderId);
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.TRACK_DRIVER, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            driverLat = response.optDouble("latitude");
                            driverLng = response.optDouble("longitude");
                            driverRotation = response.optString("rotation");
                            if (driverRotation.equalsIgnoreCase("null")||driverRotation.equalsIgnoreCase(""))
                                driverRotation = "0.0";
                            LatLng driverLatLng = new LatLng(driverLat, driverLng);
//                            if (driverMarker != null) {
//
//                                driverMarker.remove();
//                            }



//                            driverLocation = new Location(String.valueOf(driverLatLng));
//                            updatedDriverLocation = new Location(String.valueOf(driverLatLng));

//                            driverLocation.setLatitude(driverLat);
//                            driverLocation.setLongitude(driverLng);

//                            updatedDriverLocation.setLatitude(driverLat);
//                            updatedDriverLocation.setLongitude(driverLng);

                            Log.i("rotation", "rotation: "+driverRotation);
                            Log.i("rotation", "lat: "+driverLat);
                            Log.i("rotation", "long: "+driverLng);

                            markers.clear();

                            tvDriverName.setText(response.optString("driver_name"));
                            tvPhNo.setText(response.optString("driver_phone"));
                            callLay.setVisibility(View.VISIBLE);
                            callLay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (response.optString("driver_phone") != null && !response.optString("driver_phone").equals("")) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:" + response.optString("driver_phone")));
                                        startActivity(intent);
                                    }

                                }
                            });

//                            latlngDirectionPoints.add(driverLatLng);

//                            driverMarker = mMap.addMarker(new MarkerOptions()
//                                    .position(driverLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.del_truck_small))
//                                    .rotation(Float.parseFloat(driverRotation))
//                                    .anchor((float) 0.5, (float) 0.5));

                            // MarkerAnimation.animateMarkerToICS(driverMarker, driverLatLng, new LatLngInterpolator.Spherical());
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatLng,18f));
//                            driverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng)
//                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.app_logo)).rotation(Float.parseFloat(driverRotation)));
//                            driverMarker.setAnchor(0.5f, 0.5f);
//                            if(animateStatus){
//                                updatedDriverLat = driverLat;
//                                updatedDriverLng = driverLng;
//                            } else{
//                                oldDriverLat = driverLat;
//                                oldDriverLng = driverLng;
//
//                                updatedDriverLat = driverLat;
//                                updatedDriverLng = driverLng;
//
//                                driverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).
//                                        flat(true).icon(BitmapDescriptorFactory.fromBitmap(BitMapMarker)));
//
//                                mMap.animateCamera(CameraUpdateFactory.newLatLng(driverLatLng));
//                            }
//
//                            LatLng updatedLatLng = new LatLng(updatedDriverLat, updatedDriverLng);
//                            changePositionSmoothly(driverMarker, updatedLatLng, Float.valueOf(driverRotation));
//                            animateStatus = true;

                            if ("In Process".equalsIgnoreCase(response.optString("order_status_name"))) {
                                txtOrderStatus.setText(getResources().getString(R.string.txtCurrentStatus) + " " + getResources().getString(R.string.txtInProcess));
                            } else if ("In the way".equalsIgnoreCase(response.optString("order_status_name"))) {
                                txtOrderStatus.setText(getResources().getString(R.string.txtCurrentStatus) + " " + getResources().getString(R.string.txtIntheway));
                            } else if ("Delivered".equalsIgnoreCase(response.optString("order_status_name"))){
                                timer.cancel();
                                Toast.makeText(TrackYourOrderActivity.this, "Order successfully delivered", Toast.LENGTH_LONG).show();
                                finish();
                            }


                            if (!response.optString("driver_assigned").equalsIgnoreCase("0")) {

                                if (isFirstPosition) {
                                    startPosition = new LatLng(driverLat, driverLng);

                                    driverMarker = mMap.addMarker(new MarkerOptions().position(startPosition)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.del_truck_small)).rotation(Float.parseFloat(driverRotation)));
                                    driverMarker.setAnchor(0.5f, 0.5f);

                                    mMap.animateCamera(CameraUpdateFactory
                                            .newLatLng(startPosition));

                                    isFirstPosition = false;

                                } else {
                                    endPosition = new LatLng(driverLat, driverLng);
//                                driverMarker = mMap.addMarker(new MarkerOptions().position(endPosition)
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.del_truck_small)).rotation(Float.parseFloat(driverRotation)));
//                                driverMarker.setAnchor(0.5f, 0.5f);

//                                Log.d(TAG, startPosition.latitude + "--" + endPosition.latitude + "--Check --" + startPosition.longitude + "--" + endPosition.longitude);

                                    if ((startPosition.latitude != endPosition.latitude) || (startPosition.longitude != endPosition.longitude)) {

//                                    LObjectog.e(TAG, "NOT SAME");
                                        startBikeAnimation(startPosition, endPosition);

                                    } else {

//                                    Log.e(TAG, "SAMME");
                                    }
                                }
                            }

                            if (!response.optString("driver_assigned").equalsIgnoreCase("0")) {

                                if (response.optString("order_status_name").equalsIgnoreCase("In Process")) {
                                    markers.add(homeMarker);
                                    markers.add(mMarker);
                                    markers.add(driverMarker);
                                }
                                else if (response.optString("order_status_name").equalsIgnoreCase("In the way")){
                                    markers.add(homeMarker);
                                    markers.add(driverMarker);
                                }

                                if (response.optString("order_status_name").equalsIgnoreCase("In Process")) {
                                    new FetchURL(TrackYourOrderActivity.this, tvEdt).execute(getUrl("driving", true, true), "driving");

                                }
                                else if (response.optString("order_status_name").equalsIgnoreCase("In the way")) {
                                    new FetchURL(TrackYourOrderActivity.this, tvEdt).execute(getUrl("driving", false, true), "driving");

                                }
                            }

                            else {

                                markers.add(homeMarker);
                                markers.add(mMarker);
                                new FetchURL(TrackYourOrderActivity.this, tvEdt).execute(getUrl("driving", true, false), "driving");

                            }

                            LatLngBounds.Builder builder = new LatLngBounds.Builder();

                            for (Marker m :markers) {
                                builder.include(m.getPosition());
                                bounds = builder.build();
                            }

                            if (bounds!=null){
                                int width = getResources().getDisplayMetrics().widthPixels;
                                int height = getResources().getDisplayMetrics().heightPixels;
                                int padding = (int) (width * 0.2);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,width,height,padding));
                            }

//                            mMap.animateCamera(CameraUpdateFactory.newLatLng(driverLatLng));
                            Log.i("Driver update test", "onResponse: updated");
//                            animateMarker(driverLatLng,driverMarker);
//                            animateMarker(mMap,driverMarker,latlngDirectionPoints,false);
//                            animateMarker(driverLatLng, driverMarker);
//                            callDirectionApi("driver");


                            /*String url = getUrl();
                            Log.d("onMapClick", url);
                            FetchUrl FetchUrl = new FetchUrl();
                            // Start downloading json data from Google Directions API
                            FetchUrl.execute(url);*/
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

    void changePositionSmoothly(final Marker myMarker, final LatLng newLatLng, final Float bearing) {

        final LatLng startPosition = new LatLng(oldDriverLat, oldDriverLng);
        final LatLng finalPosition = newLatLng;
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                myMarker.setRotation(bearing);
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                        startPosition.longitude * (1 - t) + finalPosition.longitude * t);

                myMarker.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
                oldDriverLat = newLatLng.latitude;
                oldDriverLng = newLatLng.longitude;
            }
        });
    }

    private static void animateMarker(GoogleMap myMap, final Marker marker, final List<LatLng> directionPoint,
                                      final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = myMap.getProjection();
        final long duration = 30000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                if (i < directionPoint.size())
                    marker.setPosition(directionPoint.get(i));
                i++;


                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    // Fetches data from url passed
//    private class FetchUrl extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... url) {
//
//            // For storing data from web service
//            String data = "";
//
//            try {
//                // Fetching the data from web service
//                data = downloadUrl(url[0]);
//                Log.d("Background Task data", data);
//            } catch (Exception e) {
//                Log.d("Background Task", e.toString());
//            }
//            return data;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            ParserTask parserTask = new ParserTask();
//
//            // Invokes the thread for parsing the JSON data
//            parserTask.execute(result);
//
//        }
//    }
//
//    /**
//     * A class to parse the Google Places in JSON format
//     */
//    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
//
//        // Parsing the data in non-ui thread
//        @Override
//        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
//
//            JSONObject jObject;
//            List<List<HashMap<String, String>>> routes = null;
//
//            try {
//                jObject = new JSONObject(jsonData[0]);
//                Log.d("ParserTask", jsonData[0]);
//                DataParser parser = new DataParser();
//                Log.d("ParserTask", parser.toString());
//
//                // Starts parsing data
//                routes = parser.parse(jObject);
//                Log.d("ParserTask", "Executing routes");
//                Log.d("ParserTask", routes.toString());
//
//            } catch (Exception e) {
//                Log.d("ParserTask", e.toString());
//                e.printStackTrace();
//            }
//            return routes;
//        }
//
//        // Executes in UI thread, after the parsing process
//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
//            ArrayList<LatLng> points;
//            PolylineOptions lineOptions = null;
//
//            // Traversing through all the routes
//            for (int i = 0; i < result.size(); i++) {
//                points = new ArrayList<>();
//                lineOptions = new PolylineOptions();
//
//                // Fetching i-th route
//                List<HashMap<String, String>> path = result.get(i);
//
//                // Fetching all the points in i-th route
//                for (int j = 0; j < path.size(); j++) {
//                    HashMap<String, String> point = path.get(j);
//
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lng = Double.parseDouble(point.get("lng"));
//                    LatLng position = new LatLng(lat, lng);
//
//                    points.add(position);
//                }
//
//                // Adding all the points in the route to LineOptions
//                lineOptions.addAll(points);
//                lineOptions.width(20);
//                lineOptions.color(Color.RED);
//
//                Log.d("onPostExecute", "onPostExecute lineoptions decoded");
//
//            }
//
//            // Drawing polyline in the Google Map for the i-th route
//            if (lineOptions != null) {
//                mMap.addPolyline(lineOptions);
//            } else {
//                Log.d("onPostExecute", "without Polylines drawn");
//            }
//        }
//    }
//
//    private void callDirectionApi(String type) {
//        String url = getUrl(type);
//        Log.d("onMapClick", url);
//        FetchUrl FetchUrl = new FetchUrl();
//        // Start downloading json data from Google Directions API
//        FetchUrl.execute(url);
//    }

    private void startBikeAnimation(final LatLng start, final LatLng end) {

        Log.i("driver", "startBikeAnimation called...");

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                //LogMe.i(TAG, "Car Animation Started...");
                v = valueAnimator.getAnimatedFraction();
                lng = v * end.longitude + (1 - v)
                        * start.longitude;
                lat = v * end.latitude + (1 - v)
                        * start.latitude;

                LatLng newPos = new LatLng(lat, lng);
                driverMarker.setPosition(newPos);
                driverMarker.setAnchor(0.5f, 0.5f);
                driverMarker.setRotation(getBearing(start,end));

                // todo : Shihab > i can delay here
                mMap.animateCamera(CameraUpdateFactory
                        .newLatLng(newPos)
                                );

                startPosition = driverMarker.getPosition();

            }

        });
        valueAnimator.start();
    }

//    Runnable staticCarRunnable = new Runnable() {
//        @Override
//        public void run() {
//            Log.i("Driver", "staticCarRunnable handler called...");
//            if (index < (polyLineList.size() - 1)) {
//                index++;
//                next = index + 1;
//            } else {
//                index = -1;
//                next = 1;
//                stopRepeatingTask();
//                return;
//            }
//
//            if (index < (polyLineList.size() - 1)) {
////                startPosition = polyLineList.get(index);
//                startPosition = driverMarker.getPosition();
//                endPosition = polyLineList.get(next);
//            }
//
//            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
//            valueAnimator.setDuration(3000);
//            valueAnimator.setInterpolator(new LinearInterpolator());
//            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator valueAnimator) {
//
////                    Log.i(TAG, "Car Animation Started...");
//
//                    v = valueAnimator.getAnimatedFraction();
//                    lng = v * endPosition.longitude + (1 - v)
//                            * startPosition.longitude;
//                    lat = v * endPosition.latitude + (1 - v)
//                            * startPosition.latitude;
//                    LatLng newPos = new LatLng(lat, lng);
//                    driverMarker.setPosition(newPos);
//                    driverMarker.setAnchor(0.5f, 0.5f);
//                    driverMarker.setRotation(getBearing(startPosition, newPos));
//                    mMap.moveCamera(CameraUpdateFactory
//                            .newCameraPosition
//                                    (new CameraPosition.Builder()
//                                            .target(newPos)
//                                            .zoom(15.5f)
//                                            .build()));
//
//
//                }
//            });
//            valueAnimator.start();
//            handler.postDelayed(this, 5000);
//
//        }
//    };


    public static float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static void animateMarker(final LatLng driverLatLng, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(driverLatLng.latitude, driverLatLng.longitude);

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(500); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setRotation(startRotation);
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    public static float CalculateBearingAngle(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        double Phi1 = Math.toRadians(startLatitude);
        double Phi2 = Math.toRadians(endLatitude);
        double DeltaLambda = Math.toRadians(endLongitude - startLongitude);

        double Theta = atan2((sin(DeltaLambda) * cos(Phi2)), (cos(Phi1) * sin(Phi2) - sin(Phi1) * cos(Phi2) * cos(DeltaLambda)));
        return (float) Math.toDegrees(Theta);
    }

    @Override
    public void onTaskDone(Object... values) {

        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        if (currentPolyline!=null){
            currentPolyline.setColor(getResources().getColor(R.color.polyline_blue));
        }
    }

}
