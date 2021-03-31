package com.afieat.ini;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BasketActivity extends AppCompatActivity {


    private ScrollView svBasket;
    private LinearLayout llOrders, llEstimate;
    private TextView tvSubTotal, tvDiscount, tvPromo, tvPromoHeader, tvTotal;
    private TextView tvAddItem;
    private TextInputEditText tetDelDate, tetDelTime, tetPromo;
    private Button btnAddToBasket, btnApplyPromo;
    private Snackbar snackClosedRestaurant;
    private LinearLayout llQty;
    private ImageView ivRemove;

    private String mUserId, merchantId;
    private boolean mRestaurantStatus;
    private double mTotalPrice, mRestaurantDiscount, mPromoDiscount, mRestaurantDiscPercent, mRestaurantDiscAbove;

    private DecimalFormat df;

    private List<String> mProductNameList = new ArrayList<>();

    public BasketActivity() {
        mTotalPrice = mRestaurantDiscount = mPromoDiscount = mRestaurantDiscPercent = mRestaurantDiscAbove = 0;
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        df = (DecimalFormat) nf;
        df.applyPattern("#0.##");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar mToolbar;
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        mToolbar = (Toolbar) findViewById(R.id.appbar);
        assert mToolbar != null;
        mToolbar.setTitle(getString(R.string.title_basket));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        tvAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnApplyPromo = (Button) findViewById(R.id.btnApplyPromo);
        btnApplyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String promo = tetPromo.getText().toString();
                verifyPromoFromNW(promo);
            }
        });
        btnAddToBasket = (Button) findViewById(R.id.btnAddToBasket);
        AppUtils.disableButtons(btnAddToBasket);

        llEstimate = (LinearLayout) findViewById(R.id.llEstimate);

        tetDelDate = (TextInputEditText) findViewById(R.id.tetDelDate);
        tetDelTime = (TextInputEditText) findViewById(R.id.tetDelTime);
        tetPromo = (TextInputEditText) findViewById(R.id.tetPromo);

        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        month = Integer.parseInt(month) > 10 ? month : "0" + month;
        day = Integer.parseInt(day) > 10 ? day : "0" + day;
        tetDelDate.setText(year + "-" + month + "-" + day);
        tetDelTime.setText("now");

        tetDelDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getSupportFragmentManager(), "Date Picker");
                fragment.setOnDatePickedListener(new OnDatePickListener() {
                    @Override
                    public void onDatePicked(int year, int monthOfYear, int dayOfMonth) {
                        String strMonth = String.valueOf(monthOfYear < 10 ? "0" + monthOfYear : monthOfYear);
                        String strDay = String.valueOf(dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
                        tetDelDate.setText(year + "-" + strMonth + "-" + strDay);
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

        loadBasketItems();
    }

    private void verifyPromoFromNW(String promo) {
        final ProgressDialog dialog = AppUtils.showProgress(BasketActivity.this, "", getString(R.string.msg_please_wait));
        Map<String, String> params = new HashMap<>();
        params.put("promocode", promo);
        params.put("merchant_id", merchantId);
        params.put("delivery_date", tetDelDate.getText().toString());
        params.put("shopuserid", mUserId);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.APPLY_PROMO, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.log(response);
                        AppUtils.hideProgress(dialog);
                        if ("success".equalsIgnoreCase(response.optString("status")) || "1".equalsIgnoreCase(response.optString("status"))) {
                            JSONObject promoDetailsObject = response.optJSONObject("details");
                            String promoAmt = promoDetailsObject.optString("promo_discount_amount");
                            String promoType = promoDetailsObject.optString("promo_discount_type");
                            if ("percentage".equals(promoType)) {
                                double discount = mTotalPrice * Double.parseDouble(promoAmt) / 100;
                                mPromoDiscount = discount;
                                mTotalPrice -= discount;
                                tvPromo.setText(getString(R.string.currency) + String.valueOf(discount));
                                tvTotal.setText(getString(R.string.currency) + String.valueOf(mTotalPrice));
                                tvPromoHeader.setText("Promo (" + tetPromo.getText().toString().trim());
                                tetPromo.setText(tetPromo.getText().toString().trim() + " (applied)");
                                /*tetPromo.setEnabled(false);
                                AppUtils.disableButtons(btnApplyPromo);*/
                            } else {
                                double discount = mTotalPrice - Double.parseDouble(promoAmt);
                                mPromoDiscount = discount;
                                mTotalPrice -= discount;
                                tvPromo.setText(getString(R.string.currency) + String.valueOf(discount));
                                tvTotal.setText(getString(R.string.currency) + String.valueOf(mTotalPrice));
                                tvPromoHeader.setText("Promo (" + tetPromo.getText().toString().trim());
                                tetPromo.setText(tetPromo.getText().toString().trim() + " (applied)");
                                /*tetPromo.setEnabled(false);
                                AppUtils.disableButtons(btnApplyPromo);*/
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.msg_invalid_promo), Toast.LENGTH_LONG).show();
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

    private void loadOffersAndStatusNW() {
        final ProgressDialog dialog = AppUtils.showProgress(BasketActivity.this, "", getString(R.string.msg_please_wait));
        DBHelper db = new DBHelper(this);
        merchantId = db.getBasketMerchantId();
        Map<String, String> params = new HashMap<>();
        String time = tetDelTime.getText().toString();
        if ("now".equals(time)) {
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
                        AppUtils.log(response);
                        mRestaurantStatus = response.optString("status").equals("open");
                        if (mRestaurantStatus) {
                            if (snackClosedRestaurant.isShown()) snackClosedRestaurant.dismiss();
                            AppUtils.enableButtons(btnAddToBasket);
                            mRestaurantDiscPercent = Double.parseDouble(response.optString("offer_percentage"));
                            mRestaurantDiscAbove = Double.parseDouble(response.optString("offer_above_price"));
                            showTotalPrice(mRestaurantDiscPercent, mRestaurantDiscAbove);
                        } else {
                            snackClosedRestaurant.show();
                            AppUtils.disableButtons(btnAddToBasket);
                            showTotalPrice(0, 0);
                        }
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

    private void showTotalPrice(double offerPercentage, double offerAbovePrice) {
        if (mTotalPrice >= offerAbovePrice) {
            tvDiscount.setText(getString(R.string.currency) + String.valueOf((mTotalPrice * offerPercentage) / 100));
            double discount = (mTotalPrice * offerPercentage) / 100;
            mRestaurantDiscount = discount;
            tvSubTotal.setText(getString(R.string.currency) + String.valueOf(mTotalPrice));
            mTotalPrice = (mTotalPrice - discount);
            tvTotal.setText(getString(R.string.currency) + String.valueOf(mTotalPrice));
        }
    }

    private void showSubTotalPrice() {
        final DBHelper db = new DBHelper(this);
        final List<Food> foods = db.getFoodsBasket(mUserId);
        double subTotal = 0;
        for (Food food : foods) {
            subTotal += Double.parseDouble(food.getPriceBasket());
        }
        mTotalPrice = subTotal;
        showTotalPrice(mRestaurantDiscPercent, mRestaurantDiscAbove);
    }

    private void loadBasketItems() {
        try {
            final DBHelper db = new DBHelper(this);
            final List<Food> foods = db.getFoodsBasket(mUserId);
            for (final Food food : foods) {
                final View view = LayoutInflater.from(this).inflate(R.layout.layout_order_item, null);
                TextView tvItemTitle = (TextView) view.findViewById(R.id.tvItemTitle);
                final TextView tvItemPrice = (TextView) view.findViewById(R.id.tvItemPrice);
                ImageView ivAdd = (ImageView) view.findViewById(R.id.ivAdd);
                ImageView ivSubtract = (ImageView) view.findViewById(R.id.ivSubtract);
                final TextView tvQty = (TextView) view.findViewById(R.id.tvQty);
                tvQty.setText(food.getBasketCount());
                LinearLayout llAddOns = (LinearLayout) view.findViewById(R.id.llAddOns);
                llQty = (LinearLayout) view.findViewById(R.id.llQty);
                tvItemTitle.setText(food.getName() + " (" + food.getSizeBasket() + ")");
                mProductNameList.add(tvItemTitle.getText().toString());
                tvItemPrice.setText(getString(R.string.currency) + food.getPriceBasket());
//            mTotalPrice += df.format(Double.parseDouble(food.getPriceBasket()));
                mTotalPrice += Double.parseDouble(AppUtils.changeToEnglish(food.getPriceBasket()));
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
                ivAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int qty = Integer.parseInt(tvQty.getText().toString());
                        tvQty.setText(String.valueOf(++qty));
                        String price = AppUtils.monetize(String.valueOf(Double.parseDouble(food.getPriceBasket()) + (Double.parseDouble(food.getPriceBasket()) / (qty - 1))));
                        mTotalPrice += (Double.parseDouble(price) - Double.parseDouble(food.getPriceBasket()));
                        food.setPriceBasket(price);
//                        tvSubTotal.setText(getString(R.string.currency) + AppUtils.monetize(String.valueOf(mTotalPrice)));
                        tvItemPrice.setText(getString(R.string.currency) + price);
                        db.updateFoodBasket(food.getPriceBasket(), qty, food.getEntryId());
                        showSubTotalPrice();
                    }
                });
                ivSubtract.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int qty = Integer.parseInt(tvQty.getText().toString());
                        if (qty > 1) {
                            tvQty.setText(String.valueOf(--qty));
                            String price = String.valueOf(Double.parseDouble(food.getPriceBasket()) - (Double.parseDouble(food.getPriceBasket()) / (qty + 1)));
                            mTotalPrice -= (Double.parseDouble(food.getPriceBasket()) - Double.parseDouble(price));
                            food.setPriceBasket(price);
//                            tvSubTotal.setText(getString(R.string.currency) + AppUtils.monetize(String.valueOf(mTotalPrice)));
                            tvItemPrice.setText(getString(R.string.currency) + price);
                            db.updateFoodBasket(food.getPriceBasket(), qty, food.getEntryId());
                            showSubTotalPrice();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BasketActivity.this);
                            builder
                                    .setMessage("Sure to remove " + food.getName() + " from basket?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (db.removeFoodBasket(food.getEntryId())) {
                                                AppUtils.dbLog("Removed a row");
                                                mTotalPrice -= Double.parseDouble(food.getPriceBasket());
                                                tvSubTotal.setText(getString(R.string.currency) + AppUtils.monetize(String.valueOf(mTotalPrice)));
                                                foods.remove(food);
                                                llOrders.removeView(view);
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
                                            showSubTotalPrice();
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
            tvSubTotal.setText(getString(R.string.currency) + AppUtils.monetize(String.valueOf(mTotalPrice)));
            loadOffersAndStatusNW();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    svBasket.scrollTo(0, 0);
                }
            }, 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onProceedPayClicked(View view) {
        System.out.println("Rahul : BasketActivity : onProceedPayClicked : mProductNameList : " + mProductNameList.toArray().toString());
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(AppUtils.EXTRA_MERCHANT_ID, merchantId);
        intent.putExtra(AppUtils.EXTRA_RESTAURANT_DISCOUNT, AppUtils.monetize(String.valueOf(mRestaurantDiscount)));
        intent.putExtra(AppUtils.EXTRA_PROMO_DISCOUNT, AppUtils.monetize(String.valueOf(mPromoDiscount)));
        intent.putExtra(AppUtils.EXTRA_TOTAL_COST, AppUtils.monetize(String.valueOf(mTotalPrice)));
        intent.putExtra(AppUtils.EXTRA_TOTAL_COST_USD, AppUtils.monetize(String.valueOf(mTotalPrice * AppUtils.EXCHANGE_RATE)));
        intent.putExtra(AppUtils.EXTRA_DEL_DATE, tetDelDate.getText().toString());
        intent.putExtra(AppUtils.EXTRA_DEL_TIME, tetDelTime.getText().toString());
        startActivityForResult(intent, AppUtils.REQ_PLACE_ORDER);
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

}
