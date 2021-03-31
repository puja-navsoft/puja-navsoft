package com.afieat.ini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.afieat.ini.database.DBHelper;
import com.afieat.ini.databinding.ActivityDeliveryPointBinding;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DeliveryPointActivity extends AppCompatActivity {

    ActivityDeliveryPointBinding binding;
    private String mUserId;
    private final int REQUEST_ADDRESS = 101;
    private String mLat,mLng;
    private String mCityId,mRegionId;
    private String where = "";
    private LatLng fromLatlng;
    private LatLng toLatlng;
    private float distance;
    private int deliveryCharge = 0;
    private int orderCharge = 0;
    private int totalCharge = 0;
    private String fromAddressId;
    private String toAddressId;
    private String fromAddress;
    private String toAddress;
    private String timeOfDelivery;
    private String distance1;
    private SimpleDateFormat df;
    private SimpleDateFormat dfTime;
    private String distanceString;
    private int distanceInKm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_delivery_point);
        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(R.string.butler_service);
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mUserId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);

        if (mUserId.isEmpty()) {
            startActivity(new Intent(DeliveryPointActivity.this, LoginActivity.class));
            finish();
        }
        binding.fromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryPointActivity.this, AddressSelectionActivity.class);
                intent.putExtra(AppUtils.EXTRA_REQ_ADDRESS, true);
                intent.putExtra("from","butler");
                where = "from";
                startActivityForResult(intent, REQUEST_ADDRESS);
            }
        });

        binding.toLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryPointActivity.this, AddressSelectionActivity.class);
                intent.putExtra(AppUtils.EXTRA_REQ_ADDRESS, true);
                intent.putExtra("from","butler");
                where = "to";
                startActivityForResult(intent, REQUEST_ADDRESS);
            }
        });

        binding.confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fromLatlng!=null && toLatlng!=null && !binding.tvPackage.getText().toString().isEmpty()){
                    confirmOrder();
                }

                else
                    Toast.makeText(DeliveryPointActivity.this, "Please fill all the details properly", Toast.LENGTH_SHORT).show();

//                if(fromLatlng!=null)
//                    calculateDistance(fromLatlng,toLatlng);
            }
        });

        binding.deliveryTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryPointActivity.this);
                builder.setTitle("Delivery Type");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DeliveryPointActivity.this,
                        android.R.layout.simple_list_item_1);
                arrayAdapter.clear();

                final List<String> tempList = new ArrayList<>();
                tempList.add(getString(R.string.delivery_charge));
                tempList.add(getString(R.string.delivery_charge)+"+"+getString(R.string.order_charge));

                for (int i = 0; i < 2; i++) {
                    arrayAdapter.add(tempList.get(i));
                }
                builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.deliveryTypeTv.setText(tempList.get(which));
                        if (which==1)
                            binding.orderAmountLayout.setVisibility(View.VISIBLE);
                        else {
                            binding.tvOrderAmount.setText("");
                            binding.orderAmountLayout.setVisibility(View.GONE);
                        }
                    }
                });
                builder.setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        binding.tvOrderAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String amount = s.toString();
                if (s.toString().equalsIgnoreCase(""))
                    amount = "0";

                orderCharge = Integer.parseInt(amount);
                binding.tvOrderPrice.setText(String.valueOf(orderCharge));
                totalCharge = orderCharge+deliveryCharge;
                addTotalPrice(deliveryCharge,orderCharge);
            }
        });
    }

    private void calculateDistance(LatLng fromLatlng, LatLng toLatlng) {

        Location fromLocation = new Location("");
        Location toLocation = new Location("");
        fromLocation.setLatitude(fromLatlng.latitude);
        fromLocation.setLongitude(fromLatlng.longitude);

        toLocation.setLatitude(toLatlng.latitude);
        toLocation.setLongitude(toLatlng.longitude);

        distance = fromLocation.distanceTo(toLocation);

        distanceInKm = Math.round(distance/1000);
        requestDeliveryCharge(String.valueOf(distanceInKm));

//        String distance6;
//        distance6 = getDistance(fromLatlng.latitude,fromLatlng.longitude,toLatlng.latitude,toLatlng.longitude);
//        Toast.makeText(this, "Distance: "+distance6, Toast.LENGTH_SHORT).show();
//        Log.i("distance", "calculateDistance: "+distance);
//        String distanceNumber = "0";
//        if (distance.contains("km"))
//            distanceNumber = distance.replace("km","");
//        else if (distance.contains("m"))
//            distanceNumber = distance.replace("m","");
//        distance1 = String.valueOf(Math.round(Float.parseFloat(distanceNumber)));
//        requestDeliveryCharge(distance1);

    }

    public String getDistance(final double lat1, final double lon1, final double lat2, final double lon2){
        final String[] parsedDistance = new String[1];
        final String[] response = new String[1];
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&units=metric&mode=driving&key="+getString(R.string.google_maps_key)/*+"&sensor=false"*/);
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response[0] = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response[0]);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject legsObj = legs.getJSONObject(0);
                    JSONObject duration = legsObj.getJSONObject("duration");
                    timeOfDelivery = duration.get("text").toString();
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance[0] =distance.getString("text");

                    distanceString = String.valueOf(parsedDistance[0]);
//                    Toast.makeText(DeliveryPointActivity.this, "Distance: "+distanceString, Toast.LENGTH_SHORT).show();
                    String distanceNumber = "0";
                    if (distanceString.contains("km"))
                        distanceNumber = distanceString.replace("km","");
                    else if (distanceString.contains("m"))
                        distanceNumber = distanceString.replace("m","");
                    distance1 = String.valueOf(Math.round(Float.parseFloat(distanceNumber)));
                    requestDeliveryCharge(distance1);

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return parsedDistance[0];
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
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);

    }



    private void loadAddressFromNW(String addressId, final String where) {
        Map<String, String> params = new HashMap<>();
        params.put("address_id", addressId);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_ADDRESS_DETAILS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray addressArray = response.optJSONArray("address_list");
                        if (addressArray.length() > 0) {
                            response = addressArray.optJSONObject(0);
                            if (response != null) {
                                AppUtils.log(response);
                                String phone = response.optString("phone");
                                String name = response.optString("firstname") + " " + response.optString("lastname");
                                name = name.trim();
                                String address = response.optString("address") + ", " + response.optString("address_two");
                                address = address.trim();
                                mLat = response.optString("latitude");
                                mLng = response.optString("longitude");
                                String fathersName = response.optString("fathers_name");
                                String familyName = response.optString("grandfathers_name");
                                String location_address = response.optString("location_address");
                                mCityId = response.optString("city");
                                mRegionId = response.optString("province_id");
                                if (where.equalsIgnoreCase("from")) {
                                    String sourceString = "<b>"+response.optString("address_two")+"</b>,<b> Location:</b> " +location_address;
                                    fromAddress = location_address;
                                    binding.tvDeliverFrom.setText(Html.fromHtml(sourceString));
                                    fromLatlng = new LatLng(Double.parseDouble(mLat),Double.parseDouble(mLng));
                                }
                                else if (where.equalsIgnoreCase("to")) {
                                    String sourceString = "<b>"+response.optString("address_two")+ "</b>,<b> Location:</b> " +location_address;
                                    binding.tvDeliverTo.setText(Html.fromHtml(sourceString));
                                    toAddress = location_address;
                                    toLatlng = new LatLng(Double.parseDouble(mLat),Double.parseDouble(mLng));
                                }
//                                tetName.setText(name);
//                                tetMobile.setText(phone);
//                                tetFathersName.setText(fathersName);
//                                tetFamilyName.setText(familyName);
//                                tetAddress.setText(address);
//                                loadCityListFromNW();
//                                loadRegionListFromNW(mCityId);

                                if (fromLatlng !=null && toLatlng!=null){
                                    calculateDistance(fromLatlng,toLatlng);
                                }

                            }
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

    private void requestDeliveryCharge(String distance) {

        Map<String, String> mParam = new HashMap();
        mParam.put("distance", distance);

        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_DELIVERY_CHARGE_BUTLER, mParam,
                new Response.Listener<JSONObject>() {
                    public String message;

                    @Override
                    public void onResponse(JSONObject response) {

//                        Toast.makeText(DeliveryPointActivity.this, ""+response, Toast.LENGTH_SHORT).show();

                        try {
                            if (response.getString("status").equalsIgnoreCase("1")){
                                deliveryCharge = Integer.parseInt(response.getString("delivery_rate"));
                                binding.tvDeliveryCharge.setText(String.valueOf(deliveryCharge));

                                totalCharge = deliveryCharge+orderCharge;

                                addTotalPrice(deliveryCharge,orderCharge);

//                                binding.deliveryTimeLayout.setVisibility(View.VISIBLE);
//                                binding.tvTimeDel.setText(timeOfDelivery);

                            }

                            System.out.println("Rahul : SearchActivity : requestorderId : response : " + response);
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

    private void addTotalPrice(int deliveryCharge, int orderCharge) {
        binding.tvTotalPrice.setText(String.valueOf(orderCharge+deliveryCharge));
    }


    private void confirmOrder() {

        Map<String, String> mParam = new HashMap();

        JSONObject jsonObject = new JSONObject();

        try {


            jsonObject.put("app_request_code", String.valueOf((int) (System.currentTimeMillis() & 0xfffffff)));
            jsonObject.put("client_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
            df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            dfTime = new SimpleDateFormat("hh:mm", Locale.US);

            jsonObject.put("device_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.APPTOKEN));
            jsonObject.put("device_type", "android");
            jsonObject.put("delivery_date", df.format(Calendar.getInstance().getTime()));
            jsonObject.put("delivery_time", timeOfDelivery);
            jsonObject.put("delivery_charge", String.valueOf(deliveryCharge));
            jsonObject.put("rest_discount", "0");
            jsonObject.put("promo_discount_amount", "0");
            jsonObject.put("merchant_id", "0");
            jsonObject.put("voucher_amount_topay", String.valueOf(totalCharge));
            // orderJsonObject.put("voucher_amount_topay",String.format("Value of mVoucherAmountToPay: %.2f",mVoucherAmountToPay));
            jsonObject.put("client_name", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_FNAME));
            jsonObject.put("contact_phone", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_PHONE));
            jsonObject.put("fathers_name", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_FATHER));
            jsonObject.put("family_name", "");
            jsonObject.put("city_id", mCityId);
            jsonObject.put("region_id", mRegionId);
            jsonObject.put("street", toAddress);
            jsonObject.put("payment_method", "COD");
            jsonObject.put("latitude", String.valueOf(toLatlng.latitude));
            jsonObject.put("longitude", String.valueOf(toLatlng.longitude));
            jsonObject.put("food_items", "");
            jsonObject.put("promo_voucher_id", "");
            jsonObject.put("promo_voucher_code", "");
            jsonObject.put("point_value_used", "");
            jsonObject.put("point_value_original", "");
            jsonObject.put("delivery_discount_type", "");
            jsonObject.put("delivery_discount_value", "");
            jsonObject.put("pickup_address_id", fromAddressId);
            jsonObject.put("drop_address_id", toAddressId);
            jsonObject.put("order_type", "1");
            jsonObject.put("order_price", String.valueOf(orderCharge));
            jsonObject.put("order_details", binding.tvPackage.getText().toString());
            if (binding.deliveryTypeTv.getText().toString().equalsIgnoreCase(getString(R.string.delivery_charge)))
                jsonObject.put("driver_order_delivery_type", "1");
            else
                jsonObject.put("driver_order_delivery_type", "2");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mParam.put("order",jsonObject.toString());

        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.PLACE_DRIVER_ORDER_BUTLER, mParam,
                new Response.Listener<JSONObject>() {
                    public String message;

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getString("status").equalsIgnoreCase("success")){

                                Intent intent = new Intent(DeliveryPointActivity.this, PaymentSuccessActivity.class);

//                                intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, merchantId);
                                intent.putExtra(AppUtils.EXTRA_DEL_ADDRESS, response.optString("address"));
                                intent.putExtra(AppUtils.EXTRA_ORDER_NO, response.optString("order_no"));
                                intent.putExtra(AppUtils.EXTRA_DEL_DATE, df.format(Calendar.getInstance().getTime()));
                                intent.putExtra(AppUtils.EXTRA_DEL_TIME, response.optString("delivery_time"));

                                startActivityForResult(intent,AppUtils.REQ_PLACE_ORDER);
                            }

//                            Toast.makeText(DeliveryPointActivity.this, ""+response, Toast.LENGTH_LONG).show();

                            System.out.println("Rahul : SearchActivity : requestorderId : response : " + response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

//                        Toast.makeText(DeliveryPointActivity.this, ""+error, Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ADDRESS:
                if (resultCode == RESULT_OK) {
                    String addressId = data.getStringExtra(AppUtils.EXTRA_ADDRESS_ID);
                    loadAddressFromNW(addressId,where);
                    if (where.equalsIgnoreCase("from")) {
                        fromAddressId = addressId;
                    }
                    else {
                        toAddressId = addressId;
                    }
                } else {
                    finish();
                }
                break;

            case AppUtils.REQ_PLACE_ORDER:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    DBHelper dbHelper = new DBHelper(DeliveryPointActivity.this);
                    dbHelper.deleteAll();
                    finish();
                }
                break;
            default:
                break;
        }
    }

}