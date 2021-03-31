package com.afieat.ini;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.database.DBHelper;
import com.afieat.ini.fragments.DatePickerFragment;
import com.afieat.ini.fragments.TimePickerFragment;
import com.afieat.ini.interfaces.OnDatePickListener;
import com.afieat.ini.interfaces.OnTimePickListener;
import com.afieat.ini.models.Food;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The most complex class of the app
 */
public class BasketActivity2 extends AppCompatActivity {

    private String mUserId, merchantId;
    private boolean promoClicked;
    private String selectedDate, selectedTime, promoVoucherID, promoVoucherCode;
    private String msg;
    private Snackbar snackClosedRestaurant;
    private ScrollView svBasket;
    private LinearLayout llOrders;
    private TextInputEditText tetDelDate, tetDelTime, tetPromo;
    private TextView tvSubTotal, tvDiscount, tvPromo, tvPromoHeader, tvTotal, tvAddItem;
    private Button btnApplyPromo, btnAddToBasket;
    private TextView tv_minorderalert;
    private double mTotalPrice, mSubTotalPrice, mRestDiscount, mRestDiscountPercentage, mRestDiscountAbove, mPromoAbsoluteDiscount;
    private String MinPrice = "";
    private Promo mPromo;
    private boolean ResturantisOpen = true;
    private JSONArray mProductNameList = new JSONArray();

    public BasketActivity2() {
        mUserId = merchantId = "";
        selectedDate = selectedTime = "";
        mTotalPrice = mSubTotalPrice = mRestDiscount = mRestDiscountPercentage = mRestDiscountAbove = mPromoAbsoluteDiscount = 0;
        mPromo = new Promo();
        promoClicked = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.appbar);
        assert mToolbar != null;

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {
            System.out.println("BasketActivity2 : CURRENT_RESTAURANT_NAME_FINAL_AR : " + AppUtils.CURRENT_RESTAURANT_NAME_FINAL_AR);
            mToolbar.setTitle(getString(R.string.title_basket) + " - " + sharedpreferences.getString("CURRENT_RESTAURANT_NAME_FINAL_AR", ""));
        } else {
            System.out.println("BasketActivity2 : CURRENT_RESTAURANT_NAME_FINAL : " + AppUtils.CURRENT_RESTAURANT_NAME_FINAL);
            mToolbar.setTitle(getString(R.string.title_basket) + " - " + sharedpreferences.getString("CURRENT_RESTAURANT_NAME_FINAL", ""));
        }


        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // this.MinPrice = getIntent().getStringExtra("MinPrice");
        this.MinPrice = AppInstance.getInstance(getApplicationContext()).getFromSharedPref("min_price");
        System.out.println("mdiwidhisjdfi : BasketActivity2  :  MinPrice : " + MinPrice);
        mUserId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);

        snackClosedRestaurant = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_restaurant_closed), Snackbar.LENGTH_INDEFINITE);

        svBasket = (ScrollView) findViewById(R.id.svBasket);
        llOrders = (LinearLayout) findViewById(R.id.llOrders);
        tvSubTotal = (TextView) findViewById(R.id.tvSubTotal);
        tvDiscount = (TextView) findViewById(R.id.tvDiscount);
        tvPromo = (TextView) findViewById(R.id.tvPromo);
        tvPromoHeader = (TextView) findViewById(R.id.tvPromoHeader);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        tvAddItem = (TextView) findViewById(R.id.tvAddItem);
        tetDelDate = (TextInputEditText) findViewById(R.id.tetDelDate);
        tetDelTime = (TextInputEditText) findViewById(R.id.tetDelTime);
        tetPromo = (TextInputEditText) findViewById(R.id.tetPromo);
        btnApplyPromo = (Button) findViewById(R.id.btnApplyPromo);
        btnAddToBasket = (Button) findViewById(R.id.btnAddToBasket);
        tv_minorderalert = (TextView) findViewById(R.id.tv_minorderalert);
        AppUtils.disableButtons(btnAddToBasket);

        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        month = Integer.parseInt(month) > 9 ? month : "0" + month;
        day = Integer.parseInt(day) > 9 ? day : "0" + day;
        selectedDate = year + "-" + month + "-" + day;
        selectedTime = "" + getString(R.string.select_later_time_to_connect_order);
        tetDelDate.setText(selectedDate);
        tetDelTime.setText(selectedTime);

        tetDelDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getSupportFragmentManager(), "Date Picker");
                fragment.setOnDatePickedListener(new OnDatePickListener() {
                    @Override
                    public void onDatePicked(int year, int monthOfYear, int dayOfMonth) {
                        String strMonth = String.valueOf(monthOfYear < 9 ? "0" + (monthOfYear + 1) : (monthOfYear + 1));
                        String strDay = String.valueOf(dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
                        selectedDate = year + "-" + strMonth + "-" + strDay;
                        tetDelDate.setText(selectedDate);
                        loadOffersAndStatusNW();
                    }
                });
            }
        });

        tetDelTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment fragment = new TimePickerFragment();
                fragment.show(getSupportFragmentManager(), "Time Picker");
                fragment.setOnTimePickedListener(new OnTimePickListener() {
                    @Override
                    public void onTimePicked(int hourOfDay, int minute) {
                        String strMin = String.valueOf(minute);
                        String strHour = String.valueOf(hourOfDay);
                        if (minute < 10) {
                            strMin = "0" + minute;
                        }
                        if (hourOfDay < 10) {
                            strHour = "0" + hourOfDay;
                        }
                        tetDelTime.setText(strHour + ":" + strMin);
                        loadOffersAndStatusNW();
                    }
                });
            }
        });

        tvAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnApplyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPromoFromNW(tetPromo.getText().toString().trim());
            }
        });

        loadBasketItems();
    }

    private void loadBasketItems() {
        try {
            final DBHelper db = new DBHelper(this);
            final List<Food> foods = db.getFoodsBasket(mUserId);
            for (final Food food : foods) {

                Gson gson = new Gson();
                String json = gson.toJson(foods);

                System.out.println("BasketActivity2 : loadBasketItems : " + json);

                final View view = LayoutInflater.from(this).inflate(R.layout.layout_order_item, null);

                TextView tvItemTitle = (TextView) view.findViewById(R.id.tvItemTitle);
                final TextView tvItemPrice = (TextView) view.findViewById(R.id.tvItemPrice);
                ImageView ivAdd = (ImageView) view.findViewById(R.id.ivAdd);
                ImageView ivSubtract = (ImageView) view.findViewById(R.id.ivSubtract);
                final TextView tvQty = (TextView) view.findViewById(R.id.tvQty);
                LinearLayout llAddOns = (LinearLayout) view.findViewById(R.id.llAddOns);

                tvQty.setText(food.getBasketCount());
                tvItemTitle.setText(food.getName() + " (" + food.getSizeBasket() + ")");
                tvItemPrice.setText(getString(R.string.currency) + AppUtils.changeToArabic(food.getPriceBasket(), getApplicationContext()));

                mProductNameList.put(tvItemTitle.getText().toString());

                mSubTotalPrice += Double.parseDouble(AppUtils.changeToEnglish(food.getPriceBasket()));
                String[] addOns = food.getAddOns().split(";;");
                if (addOns.length > 0) {
                    AppUtils.showViews(llAddOns);
                    for (String addOn : addOns) {
                        if ("".equals(addOn)) {
                            continue;
                        }
                        View viewAddon = LayoutInflater.from(this).inflate(R.layout.layout_order_item_addons, null);
                        TextView tvAddonTitle = (TextView) viewAddon.findViewById(R.id.tvAddonTitle);
                        tvAddonTitle.setText("+ " + addOn);
                        llAddOns.addView(viewAddon);
                    }
                }
                String[] ingredients = food.getIngredients().split(";;");
                if (ingredients.length > 0) {
                    AppUtils.showViews(llAddOns);
                    for (String addOn : ingredients) {
                        if ("".equals(addOn)) {
                            continue;
                        }
                        View viewAddon = LayoutInflater.from(this).inflate(R.layout.layout_order_item_addons, null);
                        TextView tvAddonTitle = (TextView) viewAddon.findViewById(R.id.tvAddonTitle);
                        tvAddonTitle.setText("+ " + addOn);
                        llAddOns.addView(viewAddon);
                    }
                }


                /** Add & Subtract buttons **/
                ivAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int qty = Integer.parseInt(tvQty.getText().toString());
                        tvQty.setText(String.valueOf(++qty));
                        String price = AppUtils.monetize(String.valueOf(Double.parseDouble(food.getPriceBasket()) + (Double.parseDouble(food.getPriceBasket()) / (qty - 1))));
                        mSubTotalPrice += (Double.parseDouble(price) - Double.parseDouble(food.getPriceBasket()));
                        food.setPriceBasket(price);
//                        tvSubTotal.setText(getString(R.string.currency) + AppUtils.monetize(String.valueOf(mTotalPrice)));
                        tvItemPrice.setText(getString(R.string.currency) + AppUtils.changeToArabic(price, getApplicationContext()));
                        db.updateFoodBasket(food.getPriceBasket(), qty, food.getId());
                        AppUtils.testHashMap.put(food.getId(), String.valueOf(qty));
                        showTotalPrice();
                    }
                });
                ivSubtract.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int qty = Integer.parseInt(tvQty.getText().toString());
                        if (qty > 1) {
                            tvQty.setText(String.valueOf(--qty));
                            AppUtils.testHashMap.put(food.getId(), String.valueOf(qty));
                            String price = String.valueOf(Double.parseDouble(food.getPriceBasket()) - (Double.parseDouble(food.getPriceBasket()) / (qty + 1)));
                            mSubTotalPrice -= (Double.parseDouble(food.getPriceBasket()) - Double.parseDouble(price));
                            food.setPriceBasket(price);
//                            tvSubTotal.setText(getString(R.string.currency) + AppUtils.monetize(String.valueOf(mTotalPrice)));
                            tvItemPrice.setText(getString(R.string.currency) + AppUtils.changeToArabic(price, getApplicationContext()));
                            db.updateFoodBasket(food.getPriceBasket(), qty, food.getId());
                            showTotalPrice();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BasketActivity2.this);
                            builder
                                    .setMessage("Sure to remove " + food.getName() + " from basket?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (db.removeFoodBasket(food.getId())) {
                                                AppUtils.dbLog("Removed a row");
                                                mSubTotalPrice -= Double.parseDouble(food.getPriceBasket());
                                                tvSubTotal.setText(getString(R.string.currency) + AppUtils.monetize(String.valueOf(mSubTotalPrice)));
                                                foods.remove(food);
                                                llOrders.removeView(view);
                                                AppUtils.testHashMap.remove(food.getId());
                                                if (llOrders.getChildCount() == 0) {
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            onBackPressed();
                                                        }
                                                    }, 500);
                                                }
                                            }
//                                            showTotalPrice(mRestaurantDiscPercent, mRestaurantDiscAbove);
                                            showTotalPrice();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
                llOrders.addView(view);
            }
            loadOffersAndStatusNW();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    svBasket.scrollTo(0, 0);
                }
            }, 50);
            showTotalPrice();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOffersAndStatusNW() {
        final ProgressDialog dialog = AppUtils.showProgress(BasketActivity2.this, "", getString(R.string.msg_please_wait));
        DBHelper db = new DBHelper(this);
        merchantId = db.getBasketMerchantId();
        Map<String, String> params = new HashMap<>();
        String time = tetDelTime.getText().toString();
        if (("" + getString(R.string.select_later_time_to_connect_order)).equals(time)) {
            Calendar calendar = Calendar.getInstance();
            String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            hour = hour.length() < 2 ? "0" + hour : hour;
            String minute = String.valueOf(calendar.get(Calendar.MINUTE));
            minute = minute.length() < 2 ? "0" + minute : minute;
            time = hour + ":" + minute;
        }
        params.put("tm", time);
        params.put("dt", tetDelDate.getText().toString());
        params.put("res", merchantId);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_RESTAURANT_STATUS_OFFER, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.hideProgress(dialog);
                        AppUtils.log("@@ CHHH- " + response);
                        System.out.println("dneaknf : " + response);
                        ResturantisOpen = response.optString("status").equals("open");
                        if (ResturantisOpen) {
                            if (snackClosedRestaurant.isShown())
                                snackClosedRestaurant.dismiss();
                            AppUtils.enableButtons(btnAddToBasket);

                            mRestDiscountPercentage = Double.parseDouble(AppUtils.changeToEnglish(response.optString("offer_percentage")));
                            mRestDiscountAbove = Double.parseDouble(AppUtils.changeToEnglish(response.optString("offer_above_price")));
                        } else {
                            snackClosedRestaurant.show();
                            AppUtils.disableButtons(btnAddToBasket);
                            tv_minorderalert.setVisibility(View.VISIBLE);
                        }
                        showTotalPrice();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppUtils.hideProgress(dialog);
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void verifyPromoFromNW(final String promo) {
        final ProgressDialog dialog = AppUtils.showProgress(BasketActivity2.this, "", getString(R.string.msg_please_wait));
        Map<String, String> params = new HashMap<>();
        params.put("promocode", promo);
        params.put("merchant_id", merchantId);
        params.put("delivery_date", tetDelDate.getText().toString());
        params.put("shopuserid", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));

        System.out.println("PARAM PROMOCODE : " + params);

        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.APPLY_PROMO, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println("PROMOCODE : response : " + response);

                        String code = response.optString("code");
                        msg = response.optString("msg");
                        AppUtils.log(response);
                        AppUtils.hideProgress(dialog);

                        //       0=>Invalid parameters,2=>"Already redemed voucher",3=>"Invalid voucher",1=>"Prmo Voucher is valid to apply"
                        if ("success".equals(response.optString("status"))) {

                            JSONObject promoDetailsObject = response.optJSONObject("details");
                            String promoAmt = promoDetailsObject.optString("promo_discount_amount");
                            String promoType = promoDetailsObject.optString("promo_discount_type");
                            promoVoucherID = promoDetailsObject.optString("promo_voucher_id");
                            promoVoucherCode = promoDetailsObject.optString("promo_voucher_code");

                            mPromo.promoAmt = Double.parseDouble(promoAmt);
                            mPromo.promoType = promoType;
                            mPromo.promoCode = promo;
                            try {
                                mPromo.promoIds.clear();
                                JSONArray promoIdsArray = new JSONArray(promoDetailsObject.optString("promo_item_ids"));
                                for (int i = 0; i < promoIdsArray.length(); i++) {
                                    mPromo.promoIds.add(promoIdsArray.optString(i));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            promoClicked = true;
                            showTotalPrice();
                        } else {
                            AppUtils.log("## USR ID-" + mUserId);
                            if (mUserId.length() > 0) {
//                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                if ("2".equals(code)) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.promo_error_2), Toast.LENGTH_LONG).show();
                                } else if ("3".equals(code)) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.promo_error_3), Toast.LENGTH_LONG).show();
                                } else if ("4".equals(code)) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.promo_error_4), Toast.LENGTH_LONG).show();
                                }
                            } else
                                Toast.makeText(getApplicationContext(), getString(R.string.msg_login_promo), Toast.LENGTH_LONG).show();
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

    private void showTotalPrice() {
        tvSubTotal.setText(getString(R.string.currency) + AppUtils.changeToArabic(AppUtils.monetize(String.valueOf(mSubTotalPrice)), getApplicationContext()));
        double discountPromo = 0;
        double discount = 0;
        double totalPrice = 0;
        /*if (mRestDiscountAbove < mSubTotalPrice) {
            discount = (mSubTotalPrice * mRestDiscountPercentage) / 100;
        }
        totalPrice = mSubTotalPrice - discount;*/

        List<Food> foods = new DBHelper(BasketActivity2.this).getFoodsBasket(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        int foodCount = 0;
        double foodPrice = 0;
        for (Food food : foods) {
            if (mPromo.promoIds.contains(food.getId())) {
                foodCount += Integer.parseInt(food.getBasketCount());
                foodPrice += Double.parseDouble(food.getPriceBasket());
            } else if (mPromo.promoIds.size() == 1) {
                foodCount += Integer.parseInt(food.getBasketCount());
                foodPrice += Double.parseDouble(food.getPriceBasket());
            }
        }
        if (promoClicked) {
            promoClicked = false;
            if (foodCount == 0 && foodPrice == 0) {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_valid_promo), Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), getString(R.string.msg_promo_not_applicable), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_valid_promo), Toast.LENGTH_LONG).show();
            }
        }
        if ("fixed amount".equals(mPromo.promoType)) {
            // discountPromo = mPromo.promoAmt * foodCount;
            discountPromo = mPromo.promoAmt;

        } else if ("percentage".equals(mPromo.promoType)) {
            discountPromo = (foodPrice * mPromo.promoAmt) / 100;
        }
        mPromoAbsoluteDiscount = discountPromo;
        totalPrice = mSubTotalPrice - discountPromo;
        if (discountPromo == 0) {
            if (mRestDiscountAbove < totalPrice) {
                discount = (totalPrice * mRestDiscountPercentage) / 100;
                mRestDiscount = discount;
            }
        }

        totalPrice -= discount;
        mTotalPrice = totalPrice;
        tvPromo.setText(getString(R.string.currency) + AppUtils.changeToArabic(String.valueOf(discountPromo), getApplicationContext()));
        tvDiscount.setText(getString(R.string.currency) + AppUtils.changeToArabic(String.valueOf(discount), getApplicationContext()));
        tvTotal.setText(getString(R.string.currency) + AppUtils.changeToArabic(String.valueOf(totalPrice), getApplicationContext()));

        /*
        @@ Set Btn enable condition as per Santantu da order at 06 th oct 2017
         */

        AppUtils.log("@@ Ece-" + MinPrice);
        try {

            if (Double.parseDouble(MinPrice) > mTotalPrice) {
                AppUtils.disableButtons(btnAddToBasket);
                tv_minorderalert.setVisibility(View.VISIBLE);
                tv_minorderalert.setText(getResources().getString(R.string.min_order_alert) + " " + getString(R.string.currency) + AppUtils.changeToArabic((MinPrice), getApplicationContext()));
            } else {
                if (ResturantisOpen)
                    AppUtils.enableButtons(btnAddToBasket);
                tv_minorderalert.setVisibility(View.GONE);
            }
        } catch (NumberFormatException e) {

        }


//        if(totalPrice <= 0){
//            tvTotal.setText(getString(R.string.currency) + "0.00");
//        } else {
//            tvTotal.setText(getString(R.string.currency) + AppUtils.changeToArabic(String.valueOf(totalPrice), getApplicationContext()));
//        }

    }

    public void onProceedPayClicked(View view) {

        System.out.println("Rahul : BasketActivity : onProceedPayClicked : mProductNameList : " + mProductNameList);

        if (mUserId.length() <= 0) {
            System.out.println("Rahul : BasketActivity : len : IS_FROM_PROCEED_TO_PAY " + mUserId.length());
            AppUtils.IS_FROM_PROCEED_TO_PAY = true;
            AppUtils.IS_NOTIFICATION_CLICK = false;
        } else {
            System.out.println("Rahul : BasketActivity2 : len : IS_FROM_PROCEED_TO_PAY " + mUserId.length());
            AppUtils.IS_FROM_PROCEED_TO_PAY = false;
        }
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(AppUtils.EXTRA_MERCHANT_ID, merchantId);
        intent.putExtra(AppUtils.EXTRA_RESTAURANT_DISCOUNT, String.valueOf(mRestDiscount));
        intent.putExtra(AppUtils.EXTRA_PROMO_DISCOUNT, String.valueOf(mPromoAbsoluteDiscount));
        intent.putExtra(AppUtils.EXTRA_TOTAL_COST, String.valueOf(mTotalPrice));
        intent.putExtra(AppUtils.EXTRA_TOTAL_COST_USD, String.valueOf(mTotalPrice * AppUtils.EXCHANGE_RATE));
        intent.putExtra(AppUtils.EXTRA_DEL_DATE, tetDelDate.getText().toString());
        intent.putExtra("ProductName", mProductNameList.toString());
        String time = tetDelTime.getText().toString();
        if (("" + getString(R.string.select_later_time_to_connect_order)).equals(time))
            intent.putExtra(AppUtils.EXTRA_DEL_TIME, "now");
        else
            intent.putExtra(AppUtils.EXTRA_DEL_TIME, tetDelTime.getText().toString());
        intent.putExtra(AppUtils.EXTRA_PROMO_VOUCHER_ID, promoVoucherID);
        intent.putExtra(AppUtils.EXTRA_PROMO_VOUCHER_CODE, promoVoucherCode);
        startActivityForResult(intent, AppUtils.REQ_PLACE_ORDER);
    }

    class Promo {

        private String promoCode;
        private double promoAmt;
        private String promoType;
        private List<String> promoIds;

        public Promo() {
            promoCode = promoType = "";
            promoAmt = 0;
            promoIds = new ArrayList<>();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppUtils.REQ_PLACE_ORDER:
                if (resultCode == RESULT_OK)
                    finish();
                break;
            default:
                break;
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
        finish();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);
        AppUtils.APPTOKEN = AppInstance.getInstance(getApplicationContext()).getAuthkey();
        AppUtils.AUTHRIZATIONKEY = AppInstance.getInstance(getApplicationContext()).getAuthkeyforall();
    }
}
