package com.afieat.ini;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afieat.ini.database.DBHelper;
import com.afieat.ini.models.Restaurant;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentSuccessActivity extends AppCompatActivity {


    private TextView tvOrderNo,
            tvDeliveryDate,
            tvDeliveryTimeslot;
    private CardView cardRestaurant;

    private String mRestaurantId, mUserId;
    ImageView orderSuccessResImage;

    public PaymentSuccessActivity() {
        mRestaurantId = mUserId = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //       Fresco.initialize(this);
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.appbar);
        assert mToolbar != null;
        mToolbar.setTitle(getString(R.string.title_success));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRestaurantId = getIntent().getStringExtra(AppUtils.EXTRA_RESTAURANT_ID);
        mUserId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);

        cardRestaurant = (CardView) findViewById(R.id.cardRestaurant);

         Button btnContinue;
        btnContinue = (Button) findViewById(R.id.btnContinue);
        tvOrderNo = (TextView) findViewById(R.id.tvOrderNo);
        tvDeliveryDate = (TextView) findViewById(R.id.tvDeliveryDate);
        tvDeliveryTimeslot = (TextView) findViewById(R.id.tvDeliveryTimeslot);

        TextView tvDeliveryAddress;
        tvDeliveryAddress = (TextView) findViewById(R.id.tvDeliveryAddress);

        tvOrderNo.setText(getIntent().getStringExtra(AppUtils.EXTRA_ORDER_NO));
        // Sunit 25-01-2017
        tvDeliveryDate.setText(parseDateToddMMyyyy(getIntent().getStringExtra(AppUtils.EXTRA_DEL_DATE)));
        tvDeliveryTimeslot.setText(getIntent().getStringExtra(AppUtils.EXTRA_DEL_TIME));
        tvDeliveryAddress.setText(getIntent().getStringExtra(AppUtils.EXTRA_DEL_ADDRESS));
        try {
            DBHelper dbHelper = new DBHelper(PaymentSuccessActivity.this);
            dbHelper.deleteAll();
            AppUtils.IS_CART_VISIBLE="";
        }
        catch (Exception e){
            e.printStackTrace();
        }
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentSuccessActivity.this, CategoryListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                /*onBackPressed();*/
            }
        });

        AppUtils.hideViews(cardRestaurant);

        orderSuccessResImage = findViewById(R.id.orderSuccessResImage);
        if (mRestaurantId!=null)
            loadRestaurantDetailsFromNW();
        else {
            orderSuccessResImage.setImageDrawable(getResources().getDrawable(R.drawable.delivery_type));
            orderSuccessResImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

    }

    private void loadRestaurantDetailsFromNW() {
        final ProgressDialog dialog = AppUtils.showProgress(PaymentSuccessActivity.this, "", getString(R.string.msg_please_wait));
        String params = "?restaurant_id=" + mRestaurantId;
        if (mUserId.length() > 0) params += "&shopuserid=" + mUserId;
        NetworkRequest request = new NetworkRequest(Request.Method.GET, Apis.RESTAURANT_DETAILS + params, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.hideProgress(dialog);
                        Restaurant restaurant = new Restaurant();

                        JSONObject restaurantDetails = response.optJSONObject("restaurant_details");

                        restaurant.setName(restaurantDetails.optString("restaurant_name"));
                        restaurant.setAddress(restaurantDetails.optString("search_address"));
                        restaurant.setId(restaurantDetails.optString("merchant_id"));
                        restaurant.setUriThumb(Apis.IMG_PATH + "restaurants/image/thumb_115_115/" + restaurantDetails.optString("merchant_photo_bg"));
                        AppUtils.log(restaurant.getUriThumb());
//                        restaurant.setUriBg(Apis.IMG_PATH + restaurantDetails.optString("merchant_photo_bg"));
                        // Sunit 25-01-2017
                        restaurant.setOpeningTime(restaurantDetails.optString("openning_time"));
                        restaurant.setClosingTime(restaurantDetails.optString("closing_time"));

                        restaurant.setRating(restaurantDetails.optString("present_rating"));
                        restaurant.setRatingCount(restaurantDetails.optString("review_count"));
//                        restaurant.setClosingTime(restaurantDetails.optString("today_closing_time_pm"));
                        restaurant.setStatus(restaurantDetails.optString("status"));
                        restaurant.setDeliveryTime(restaurantDetails.optString("delivery_time"));
                        restaurant.setMinOrder(restaurantDetails.optString("merchant_minimum_order"));
                        restaurant.setCuisine(restaurantDetails.optString("cuisine"));

                        if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                            restaurant.setName(restaurantDetails.optString("restaurant_name_ar"));
                            restaurant.setCuisine(restaurantDetails.optString("cuisine_ar"));
                        }

                        displayRestaurantData(restaurant);
                        AppUtils.testHashMap.clear();
                        AppUtils.LAST_SELECTED_LISTVIEW_ITEM = 0;
                        AppUtils.LAST_SELECTED_TAP_POSITION = 0;
                        AppUtils.PLATE_PRICE="";
                        AppUtils.PLATE_TYPE="";
                        AppUtils.PLATE_ID="";
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

    private void displayRestaurantData(final Restaurant restaurant) {
        final ImageView ivRestaurantLogo = (ImageView) cardRestaurant.findViewById(R.id.ivRestaurantLogo);
        System.out.println("PaymentSuccessActivity : img uri : " + restaurant.getUriThumb());

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //   ivRestaurantLogo.setImageURI(Uri.parse(restaurant.getUriThumb()));
                Glide
                        .with(getApplicationContext())
                        .load(Uri.parse(restaurant.getUriThumb()))
                        .placeholder(R.drawable.placeholder_land)
                        .into(orderSuccessResImage);
            }
        });

        TextView tvRestTitle = (TextView) cardRestaurant.findViewById(R.id.tvRestTitle);
        TextView tvRestCuisine = (TextView) cardRestaurant.findViewById(R.id.tvRestCuisine);
        TextView tvRestStatus = (TextView) cardRestaurant.findViewById(R.id.tvRestStatus);
        TextView tvRestOpeningHours = (TextView) cardRestaurant.findViewById(R.id.tvRestOpeningHours);
        TextView tvMinOrder = (TextView) cardRestaurant.findViewById(R.id.tvMinOrder);
        TextView tvDelhiveryTime = (TextView) cardRestaurant.findViewById(R.id.tvDeliveryTime);
        TextView tvOrderNow = (TextView) cardRestaurant.findViewById(R.id.tvOrderNow);
        RatingBar ratingbar = (RatingBar) cardRestaurant.findViewById(R.id.ratingbar);
        TextView tvRating = (TextView) cardRestaurant.findViewById(R.id.tvRating);

        tvRestTitle.setText(restaurant.getName());
        tvRestCuisine.setText(restaurant.getCuisine().trim());
        tvRestStatus.setText(restaurant.getStatus());
        if ("Open".equals(restaurant.getStatus())) {
            tvRestStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greenButton));
        } else if ("Closed".equals(restaurant.getStatus())) {
            tvRestStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        } else {
            tvRestStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.orangeButton));
        }
        /*if (restaurant.getOpeningTime().trim().length() > 0 && restaurant.getClosingTime().length() > 0)
            tvRestOpeningHours.setText(restaurant.getOpeningTime() + " to " + restaurant.getClosingTime());
        else
            tvRestOpeningHours.setText("N/A");*/
        ratingbar.setRating(Float.parseFloat(restaurant.getRating()));
        tvRating.setText("(" + restaurant.getRatingCount() + ")");

        // Sunit 25-01-2017
        if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            tvRestOpeningHours.setText(restaurant.getOpeningTime() + " إلى " + restaurant.getClosingTime());
        } else {
            tvRestOpeningHours.setText(restaurant.getOpeningTime() + " to " + restaurant.getClosingTime());
        }


        tvMinOrder.setText(getString(R.string.currency) + restaurant.getMinOrder());
        String delTime = restaurant.getDeliveryTime().trim().length() > 0 ? restaurant.getDeliveryTime().trim() + " mins delivery" : "N/A";
        tvDelhiveryTime.setText(delTime);

        /*tvOrderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestaurantOrderItemsFragment targetFragment = RestaurantOrderItemsFragment.newInstance(mRestaurantId);
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.entry_in, R.anim.entry_out, R.anim.exit_in, R.anim.exit_out)
                        .add(R.id.flContainer, targetFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });*/

        AppUtils.showViews(cardRestaurant);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;  }
        return false;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);

    }

    // Sunit 25-01-2017
    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd/MM/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}
