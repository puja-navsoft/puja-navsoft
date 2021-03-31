package com.afieat.ini;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.Security.Relogin;
import com.afieat.ini.database.DBHelper;
import com.afieat.ini.models.Food;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.afieat.ini.webservice.ApiClient;
import com.afieat.ini.webservice.ApiInterface;
import com.afieat.ini.webservice.Json_Response;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.paytabs.paytabs_sdk.payment.ui.activities.PayTabActivity;
import com.paytabs.paytabs_sdk.utils.PaymentParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;

public class PaymentActivity extends AppCompatActivity {


    private ScrollView svPayment;

    private TextView tvTotal;
    private TextView tvDeliveryCharge;
    private TextView tvNetPayable;
    private TextView tvNetPayableUSD;
    private TextView tvCreditCard, tvPrepaidCard, tvCashOnDel;
    private TextInputEditText tetName, tetMobile, tetFathersName, tetFamilyName, tetCity, tetRegion, tetAddress;
    private TextView tvChangeAddress, txtViewavailableBalance;
    private Button btnAddToBasket;
    String mPointValueUsed, mVoucherAmountToPay, mOriginalAmt;
    CheckBox loyaltyPointsCheckbox;
    private String[] citiesArray, regionsArray;

    private JSONObject orderJsonObject;

    private List<City> cities;
    private List<Region> regions;
    private List<PaymentMethod> paymentMethods;

    private Snackbar snackNotDeliverable;
    private AlertDialog dialogCity, dialogRegion;

    private ListView lvCities, lvRegions;

    private String mTotal, mTempTotal, deliveryCharge, mNetPayable, mNetPayableUSD, mUserId, mCityId, mRegionId, merchantId, mPromo, mRestaurantDiscount, promoVoucherId, promoVoucherCode;
    private String mLat, mLng;
    private Double mConvRate;
    private String my_points;
    private int randomInt;
    private final int REQUEST_LOGIN = 100;
    private final int REQUEST_ADDRESS = 101;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private String discount_type = "";
    private String discount_value = "";

    //Param PayTab Mode
    private String mPayTabparamCustomerPhoneNumber = "", mPayTabParamCustomerEmail = "", mPayTabParamOrderId = "", mPayTabParamProductName = "",
            mPayTabParamAddressBilling, mPayTabParamCityBilling = "", mPayTabParamStateBilling = "", mPayTabParamCountryBilling = "", mPayTabParamPostalCodeBilling = "964";
    private double mPayTabAmountParam = 0;

    public PaymentActivity() {
        cities = new ArrayList<>();
        regions = new ArrayList<>();
        paymentMethods = new ArrayList<>();
        mUserId = "";
        mCityId = "";
        mTotal = mNetPayable = mNetPayableUSD = "0";
        merchantId = "";
        mLat = mLng = "";
        mConvRate = 0.00084;
        randomInt = (int) (System.currentTimeMillis() & 0xfffffff);
    }

    Handler mHandler, mPaytabParamHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar mToolbar;
        mToolbar = findViewById(R.id.appbar);
        assert mToolbar != null;
        mToolbar.setTitle(getString(R.string.title_delivery_payment));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        svPayment = findViewById(R.id.svPayment);

        tvTotal = findViewById(R.id.tvTotal);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvNetPayable = findViewById(R.id.tvNetPayable);
        tvNetPayableUSD = findViewById(R.id.tvNetPayableUSD);

        if ((mTotal = getIntent().getStringExtra(AppUtils.EXTRA_TOTAL_COST)) != null) {
//            if(Float.parseFloat(mTotal = getIntent().getStringExtra(AppUtils.EXTRA_TOTAL_COST)) <= 0.00){
//                tvTotal.setText(getString(R.string.currency) + "0.00");
//            } else {
//                tvTotal.setText(getString(R.string.currency) + AppUtils.changeToArabic(mTotal, getApplicationContext()));
//            }
            tvTotal.setText(getString(R.string.currency) + AppUtils.changeToArabic(mTotal, getApplicationContext()));
            mTempTotal = mTotal;
        }
        mPromo = getIntent().getStringExtra(AppUtils.EXTRA_PROMO_DISCOUNT);
        mRestaurantDiscount = getIntent().getStringExtra(AppUtils.EXTRA_RESTAURANT_DISCOUNT);
        merchantId = getIntent().getStringExtra(AppUtils.EXTRA_MERCHANT_ID);
        promoVoucherId = getIntent().getStringExtra(AppUtils.EXTRA_PROMO_VOUCHER_ID);
        promoVoucherCode = getIntent().getStringExtra(AppUtils.EXTRA_PROMO_VOUCHER_CODE);

        if (getIntent().getStringExtra("ProductName") != null) {
            System.out.println("Rahul : PaymentActivity : mProductNameList : 1 : " + getIntent().getStringExtra("ProductName"));

            String mProductName = "";

            try {
                JSONArray mJsonArray = new JSONArray(getIntent().getStringExtra("ProductName"));
                mProductName = mJsonArray.toString();
                mProductName = mProductName.replace("[", "").replace("]", "").replace("\"", "");

                /*for (int i = 0; i < mJsonArray.length(); i++) {

                    mProductName = mProductName.concat(","+mJsonArray.getString(i));

                }*/
                mProductName = "\"" + mProductName + "\"";
                System.out.println("Rahul : PaymentActivity : mProductNameList : 2 : " + mProductName);
                mPayTabParamProductName = mProductName;
            } catch (JSONException e) {

            }
        }

        tvCreditCard = findViewById(R.id.tvCreditCard);
        tvPrepaidCard = findViewById(R.id.tvPrepaidCard);
        tvCashOnDel = findViewById(R.id.tvCashOnDel);

        tetName = findViewById(R.id.tetName);
        tetMobile = findViewById(R.id.tetMobile);
        tetFathersName = findViewById(R.id.tetFathersName);
        tetFamilyName = findViewById(R.id.tetFamilyName);
        tetCity = findViewById(R.id.tetCity);
        tetRegion = findViewById(R.id.tetRegion);
        tetAddress = findViewById(R.id.tetAddress);
        txtViewavailableBalance = findViewById(R.id.availableBalance);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        dialogCity = builder.create();
        lvCities = new ListView(this);
        lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogCity.hide();
                if (!mCityId.equals(cities.get(position).id)) {
                    mCityId = cities.get(position).id;
                    tetCity.setText(cities.get(position).name);
                    loadRegionListFromNW(cities.get(position).id);
                }
            }
        });
        dialogCity.setView(lvCities);

        snackNotDeliverable = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_not_deliverable), Snackbar.LENGTH_INDEFINITE);

        dialogRegion = builder.create();
        lvRegions = new ListView(this);
        lvRegions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogRegion.hide();
                mRegionId = regions.get(position).id;
                tetRegion.setText(regions.get(position).name);
                loadDeliveryChargeForRegion();
            }
        });
        dialogRegion.setView(lvRegions);

        tetCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCity.show();
            }
        });

        tetRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCityId.length() > 0) {
                    dialogRegion.show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_select_city), Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvChangeAddress = findViewById(R.id.tvChangeAddress);

        btnAddToBasket = findViewById(R.id.btnAddToBasket);

        paymentMethods.add(new PaymentMethod(tvCreditCard, false));
        paymentMethods.add(new PaymentMethod(tvPrepaidCard, false));
//        paymentMethods.add(new PaymentMethod(tvPaypal, false));
        paymentMethods.add(new PaymentMethod(tvCashOnDel, true));

        tvChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, AddressSelectionActivity.class);
                intent.putExtra(AppUtils.EXTRA_REQ_ADDRESS, true);
                startActivityForResult(intent, REQUEST_ADDRESS);
            }
        });

        tvCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePaymentMethod(tvCreditCard);
            }
        });
        tvPrepaidCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePaymentMethod(tvPrepaidCard);
            }
        });
        tvCashOnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePaymentMethod(tvCashOnDel);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                svPayment.scrollTo(0, 0);
            }
        }, 50);
        btnAddToBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPayment();
            }
        });
        AppUtils.disableButtons(btnAddToBasket);

        if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID).length() == 0) {
            Intent intent = new Intent(PaymentActivity.this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        } else {
            mPayTabParamCustomerEmail = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_EMAIL);

            loadConversionRateFromNW();
        }
        loyaltyPointsCheckbox = findViewById(R.id.loyaltyPointsCheckbox);
        final TextView my_point_value = findViewById(R.id.my_point_value);
        final TextView loyaltyPointsCheckboxTxt = findViewById(R.id.loyaltyPointsCheckboxTxt);
        final TextView my_point_show = findViewById(R.id.my_point_show);


        my_points = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.MY_POINTS);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == 1) {
                    mPointValueUsed = "0";
                    if (deliveryCharge != null) {
                        mVoucherAmountToPay = String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge));
                        mOriginalAmt = String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge));
                    } else {
                        mVoucherAmountToPay = String.valueOf(Double.parseDouble(mTempTotal));
                        mOriginalAmt = String.valueOf(Double.parseDouble(mTempTotal));
                    }
                }


            }
        };


        mPaytabParamHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case 1:

                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    case 10:
                        break;
                    case 11:
                        break;
                    case 12:
                        break;
                }
            }
        };

        if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.MY_POINTS).isEmpty()) {
            findViewById(R.id.relativeLayMyPoints).setVisibility(View.GONE);
        } else if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.MY_POINTS_VALUE).isEmpty()) {
            findViewById(R.id.relativeLayMyPoints).setVisibility(View.GONE);
        } else if ("0".equalsIgnoreCase(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.MY_POINTS_VALUE))) {
            findViewById(R.id.relativeLayMyPoints).setVisibility(View.GONE);
        } else if ("0".equalsIgnoreCase(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.MY_POINTS))) {
            findViewById(R.id.relativeLayMyPoints).setVisibility(View.GONE);
        } else {
            if (!my_points.isEmpty() || !"0".equalsIgnoreCase(my_points)) {
                if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    txtViewavailableBalance.setText("(Available Balance : " + getString(R.string.currency) + AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.MY_POINTS) + " )");
                    //my_point_show.setText("(My Points : " + my_points + ")");
                    my_point_show.setText("( " + AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.MY_POINTS_VALUE) + ")");
                    my_point_value.setText(getString(R.string.currency) + my_points);
                    System.out.println("MyPoints : Available :  " + my_points);


                } else {
                    txtViewavailableBalance.setText("(الرصيد المتوفر : " + getString(R.string.currency) + AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.MY_POINTS) + " )");
                    // my_point_show.setText("(نقاطي :" + my_points + ")");
                    my_point_show.setText("( " + AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.MY_POINTS_VALUE) + ")");
                    my_point_value.setText(getString(R.string.currency) + AppUtils.changeToArabic(my_points, getApplicationContext(), true));

                }

            } else {
                findViewById(R.id.relativeLayMyPoints).setVisibility(View.GONE);
            }


        }


        loyaltyPointsCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loyaltyPointsCheckbox.isChecked()) {
                    my_point_value.setVisibility(View.VISIBLE);

                    if (deliveryCharge != null) {
                        if (deliveryCharge.equalsIgnoreCase("0") || deliveryCharge.equalsIgnoreCase("") || deliveryCharge.isEmpty()) {

                            if (Double.parseDouble(mTempTotal) < Double.parseDouble(my_points)) {
                                tvNetPayable.setText("0");
                                mVoucherAmountToPay = "0";
                                mPointValueUsed = mTempTotal;
                                if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                                    my_point_value.setText(getString(R.string.currency) + (Double.parseDouble(mTotal)));
                                } else {
                                    my_point_value.setText(getString(R.string.currency) + AppUtils.changeToArabic(String.valueOf(Double.parseDouble(mTotal)), getApplicationContext(), true));

                                }

                            } else {
                                tvNetPayable.setText(String.valueOf(Double.parseDouble(mTempTotal) - Double.parseDouble(my_points)));
                                mVoucherAmountToPay = String.valueOf(Double.parseDouble(mTempTotal) - Double.parseDouble(my_points));
                                mPointValueUsed = my_points;
                                if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                                    my_point_value.setText(getString(R.string.currency) + my_points);
                                } else {
                                    my_point_value.setText(getString(R.string.currency) + AppUtils.changeToArabic(my_points, getApplicationContext(), true));

                                }
                            }
                            mOriginalAmt = mTempTotal;

                        } else {
                            System.out.println("jkggib : mypoints : " + my_points);

                            if (Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge) < Double.parseDouble(my_points)) {
                                tvNetPayable.setText("0");
                                mVoucherAmountToPay = "0";
                                mPointValueUsed = String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge));
                                if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                                    my_point_value.setText(getString(R.string.currency) + (Double.parseDouble(mTotal) + Double.parseDouble(deliveryCharge)));
                                } else {
                                    my_point_value.setText(getString(R.string.currency) + AppUtils.changeToArabic(String.valueOf(Double.parseDouble(mTotal) + Double.parseDouble(deliveryCharge)), getApplicationContext(), true));

                                }

                            } else {
                                tvNetPayable.setText(String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge) - Double.parseDouble(my_points)));
                                mVoucherAmountToPay = String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge) - Double.parseDouble(my_points));
                                mPointValueUsed = my_points;
                                if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                                    my_point_value.setText(getString(R.string.currency) + my_points);
                                } else {
                                    my_point_value.setText(getString(R.string.currency) + AppUtils.changeToArabic(my_points, getApplicationContext(), true));

                                }
                            }

                            mOriginalAmt = String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge));
                        }
                        System.out.println("hgcghccy : if");

                    } else {
                        System.out.println("hgcghccy : else");

                        if (Double.parseDouble(mTempTotal) < Double.parseDouble(my_points)) {
                            tvNetPayable.setText("0");
                            mVoucherAmountToPay = "0";
                            mPointValueUsed = mTempTotal;
                            if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                                my_point_value.setText(getString(R.string.currency) + mTotal);
                            } else {
                                my_point_value.setText(getString(R.string.currency) + AppUtils.changeToArabic(mTotal, getApplicationContext(), true));

                            }

                        } else {
                            tvNetPayable.setText(String.valueOf(Double.parseDouble(mTempTotal) - Double.parseDouble(my_points)));
                            mVoucherAmountToPay = String.valueOf(Double.parseDouble(mTempTotal) - Double.parseDouble(my_points));
                            mPointValueUsed = my_points;
                            if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                                my_point_value.setText(getString(R.string.currency) + my_points);
                            } else {
                                my_point_value.setText(getString(R.string.currency) + AppUtils.changeToArabic(my_points, getApplicationContext(), true));

                            }
                        }

                        mOriginalAmt = mTempTotal;

                    }
                    System.out.println("mTotal : " + mTotal);

                } else {
                    my_point_value.setVisibility(View.GONE);
                    if (deliveryCharge != null) {
                        tvNetPayable.setText(String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge)));
                        mVoucherAmountToPay = String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge));
                        mOriginalAmt = String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge));
                    } else {
                        // mTotal = String.valueOf(Double.parseDouble(mTempTotal) - Double.parseDouble(my_points));
                        tvNetPayable.setText(mTempTotal);
                        mVoucherAmountToPay = mTempTotal;
                        mOriginalAmt = mTempTotal;

                    }
                    mPointValueUsed = "0";


                }
            }
        });

        loyaltyPointsCheckboxTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loyaltyPointsCheckbox.isChecked()) {
                    loyaltyPointsCheckbox.setChecked(false);
                    my_point_value.setVisibility(View.GONE);
                    // tvNetPayable.setText(mTempTotal);
                    if (deliveryCharge != null) {
                        tvNetPayable.setText(String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge)));
                        mVoucherAmountToPay = String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge));
                        mOriginalAmt = String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge));
                    } else {
                        tvNetPayable.setText(mTempTotal);
                        mVoucherAmountToPay = mTempTotal;
                        mOriginalAmt = mTempTotal;
                    }
                    mPointValueUsed = "0";

                } else {
                    loyaltyPointsCheckbox.setChecked(true);
                    my_point_value.setVisibility(View.VISIBLE);


                    if (deliveryCharge != null) {
                        if (deliveryCharge.equalsIgnoreCase("0") || deliveryCharge.equalsIgnoreCase("") || deliveryCharge.isEmpty()) {

                            if (Double.parseDouble(mTempTotal) < Double.parseDouble(my_points)) {
                                tvNetPayable.setText("0");
                                mVoucherAmountToPay = "0";
                                mPointValueUsed = mTempTotal;
                                if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                                    my_point_value.setText(getString(R.string.currency) + (Double.parseDouble(mTotal)));
                                } else {
                                    my_point_value.setText(getString(R.string.currency) + AppUtils.changeToArabic(String.valueOf(Double.parseDouble(mTotal)), getApplicationContext(), true));

                                }
                            } else {
                                tvNetPayable.setText(String.valueOf(Double.parseDouble(mTempTotal) - Double.parseDouble(my_points)));
                                mVoucherAmountToPay = String.valueOf(Double.parseDouble(mTempTotal) - Double.parseDouble(my_points));
                                mPointValueUsed = my_points;
                                if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                                    my_point_value.setText(getString(R.string.currency) + my_points);
                                } else {
                                    my_point_value.setText(getString(R.string.currency) + AppUtils.changeToArabic(my_points, getApplicationContext(), true));

                                }

                            }
                            mOriginalAmt = mTempTotal;

                        } else {

                            if (Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge) < Double.parseDouble(my_points)) {
                                tvNetPayable.setText("0");
                                mVoucherAmountToPay = "0";
                                mPointValueUsed = String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge));
                                if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                                    my_point_value.setText(getString(R.string.currency) + (Double.parseDouble(mTotal) + Double.parseDouble(deliveryCharge)));
                                } else {
                                    my_point_value.setText(getString(R.string.currency) + AppUtils.changeToArabic(String.valueOf(Double.parseDouble(mTotal) + Double.parseDouble(deliveryCharge)), getApplicationContext(), true));

                                }
                            } else {
                                tvNetPayable.setText(String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge) - Double.parseDouble(my_points)));
                                mVoucherAmountToPay = String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge) - Double.parseDouble(my_points));
                                mPointValueUsed = my_points;
                                if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                                    my_point_value.setText(getString(R.string.currency) + my_points);
                                } else {
                                    my_point_value.setText(getString(R.string.currency) + AppUtils.changeToArabic(my_points, getApplicationContext(), true));

                                }
                            }
                            mOriginalAmt = String.valueOf(Double.parseDouble(mTempTotal) + Double.parseDouble(deliveryCharge));

                        }
                    } else {

                        if (Double.parseDouble(mTempTotal) < Double.parseDouble(my_points)) {
                            tvNetPayable.setText("0");
                            mVoucherAmountToPay = "0";
                            mPointValueUsed = mTempTotal;
                            if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                                my_point_value.setText(getString(R.string.currency) + mTotal);
                            } else {
                                my_point_value.setText(getString(R.string.currency) + AppUtils.changeToArabic(mTotal, getApplicationContext(), true));

                            }

                        } else {
                            tvNetPayable.setText(String.valueOf(Double.parseDouble(mTempTotal) - Double.parseDouble(my_points)));
                            mVoucherAmountToPay = String.valueOf(Double.parseDouble(mTempTotal) - Double.parseDouble(my_points));
                            mPointValueUsed = my_points;
                            if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                                my_point_value.setText(getString(R.string.currency) + my_points);
                            } else {
                                my_point_value.setText(getString(R.string.currency) + AppUtils.changeToArabic(my_points, getApplicationContext(), true));

                            }


                        }

                        mOriginalAmt = mTempTotal;
                        System.out.println("hgcghccy : else");
                    }

                }


            }

        });

    }


    private void loadAddressFromNW() {
        Map<String, String> params = new HashMap<>();
        params.put("userid", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        final ProgressDialog progressDialog = AppUtils.showProgress(PaymentActivity.this, "", getString(R.string.msg_please_wait));
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_ADDRESS_DEFAULT, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.hideProgress(progressDialog);
                        JSONObject mResponse = response;
                        JSONArray addressArray = mResponse.optJSONArray("address_list");
                        if (addressArray != null) {
                            mResponse = addressArray.optJSONObject(0);
                            if (mResponse != null) {
                                AppUtils.log(mResponse);
                                String phone = mResponse.optString("phone");
                                String name = mResponse.optString("firstname") + " " + mResponse.optString("lastname");
                                name = name.trim();
                                String address = mResponse.optString("address") + ", " + mResponse.optString("address_two");
                                address = address.trim();
                                String fathersName = mResponse.optString("fathers_name");
                                String familyName = mResponse.optString("grandfathers_name");
                                mCityId = mResponse.optString("city");
                                mRegionId = mResponse.optString("province_id");
                                mLat = mResponse.optString("latitude");
                                mLng = mResponse.optString("longitude");
                                tetName.setText(name);
                                tetMobile.setText(phone);
                                tetFathersName.setText(fathersName);
                                tetFamilyName.setText(familyName);
                                tetAddress.setText(address);

                              /*  Message message = new Message();
                                Bundle mB = new Bundle();
                                mB.putString("mPayTabparamCustomerPhoneNumber", phone);
                                message.setData(mB);
                                mPaytabParamHandler.send(message);*/

                                mPayTabparamCustomerPhoneNumber = phone;
                                mPayTabParamCustomerEmail = "";
                                mPayTabParamAddressBilling = address;


                            } else {
                                tetAddress.setText("No address found");
                            }
                            loadCityListFromNW();
                            loadRegionListFromNW(mCityId);
                        } else {
                            startActivityForResult(new Intent(PaymentActivity.this, AddressSelectionActivity.class).putExtra(AppUtils.EXTRA_REQ_ADDRESS, true), REQUEST_ADDRESS);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppUtils.hideProgress(progressDialog);
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void requestorderId(String argCityId, String argMerhantId) {

        Map<String, String> mParam = new HashMap();
        mParam.put("city_id", argCityId);
        mParam.put("merchant_id", argMerhantId);

        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_ORDER_NO, mParam,
                new Response.Listener<JSONObject>() {
                    public String message;

                    @Override
                    public void onResponse(JSONObject response) {

                        Gson mGson = new Gson();

                        System.out.println("Rahul : SearchActivity : requestorderId : response : " + response);
                        try {
                            mPayTabParamOrderId = response.getString("order_no");
                            mPayTabParamCountryBilling = "IRQ";
                            if (mPayTabParamCustomerEmail.isEmpty()) {

                                final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(PaymentActivity.this, R.style.DialogStyle);
                                mBottomSheetDialog.setContentView(R.layout.enter_mail_id);
                                final TextInputEditText edtMail = mBottomSheetDialog.findViewById(R.id.edtMail);
                                Button btnSave = mBottomSheetDialog.findViewById(R.id.btnSave);

                                btnSave.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        mPayTabParamCustomerEmail = edtMail.getText().toString();
                                        mBottomSheetDialog.dismiss();

                                        processPayTabPaymentMode();
                                    }
                                });

                                mBottomSheetDialog.show();

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

    private void loadConversionRateFromNW() {
        final ProgressDialog progressDialog = AppUtils.showProgress(PaymentActivity.this, "", getString(R.string.msg_please_wait));
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_CONV_RATE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.log(" Convert rate resp-" + response);
                        AppUtils.hideProgress(progressDialog);
                        if (response.optInt("status") == 999) {
                            new Relogin(PaymentActivity.this, new Relogin.OnLoginlistener() {
                                @Override
                                public void OnLoginSucess() {
                                    loadConversionRateFromNW();
                                }

                                @Override
                                public void OnError(String Errormessage) {
                                    startActivityForResult(new Intent(PaymentActivity.this, LoginActivity.class), AppUtils.REQ_LOGIN);
                                }
                            }).execute();
                        } else {
                            mConvRate = Double.parseDouble(response.optString("convesionrate"));
                            loadAddressFromNW();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        AppUtils.hideProgress(progressDialog);
                        loadAddressFromNW();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void loadCityListFromNW() {
        cities.clear();
        final ProgressDialog progressDialog = AppUtils.showProgress(PaymentActivity.this, "", getString(R.string.msg_please_wait));
        Map<String, String> params = new HashMap<>();
        if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            params.put("lang", "en");
        }
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.LIST_CITIES, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray cityArray = response.optJSONArray("cityllist");
                        citiesArray = new String[cityArray.length()];
                        int i = 0;
                        while (i < cityArray.length()) {
                            JSONObject cityObject = cityArray.optJSONObject(i);
                            City city = new City();
                            city.id = cityObject.optString("city_id");
                            city.name = cityObject.optString("city_name");

                            cities.add(city);
                            citiesArray[i] = city.name;
                            i++;
                        }
                        AppUtils.hideProgress(progressDialog);
                        lvCities.setAdapter(new ArrayAdapter<>(PaymentActivity.this, R.layout.layout_simple_list_item, citiesArray));
                        for (City city : cities) {
                            if (city.id.equals(mCityId)) {
                                tetCity.setText(city.name);

                                mPayTabParamStateBilling = city.name;
                                break;
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        AppUtils.hideProgress(progressDialog);
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void loadRegionListFromNW(String cityId) {
        regions.clear();
        Map<String, String> params = new HashMap<>();
        params.put("city_id", cityId);
        if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            params.put("lang", "en");
        }
        final ProgressDialog progressDialog = AppUtils.showProgress(PaymentActivity.this, "", getString(R.string.msg_please_wait));
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.LIST_REGIONS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.hideProgress(progressDialog);
                        JSONArray array = response.optJSONArray("regionlist");
                        if (array.length() > 0) {
                            regionsArray = new String[array.length()];
                            int i = 0;
                            while (i < array.length()) {
                                JSONObject regionObject = array.optJSONObject(i);
                                Region region = new Region();
                                region.id = regionObject.optString("region_id");
                                region.name = regionObject.optString("region_name");
                                regions.add(region);
                                regionsArray[i] = region.name;
                                i++;
                            }
                            AppUtils.showViews(tetRegion);
                            lvRegions.setAdapter(new ArrayAdapter<>(PaymentActivity.this, R.layout.layout_simple_list_item, regionsArray));
                            tetRegion.setText(regions.get(0).name);
//                            mRegionId = regions.get(0).id;
                            for (Region region : regions) {
                                if (region.id.equals(mRegionId)) {
                                    tetRegion.setText(region.name);

                                    mPayTabParamCityBilling = region.name;
                                    mRegionId = region.id;
                                    break;
                                }
                            }
                            loadDeliveryChargeForRegion();
                        } else {
                            AppUtils.hideViews(tetRegion);
                            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_regions), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        AppUtils.hideProgress(progressDialog);
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void loadDeliveryChargeForRegion() {
        final ProgressDialog dialog = AppUtils.showProgress(PaymentActivity.this, "", getString(R.string.msg_please_wait));
        Map<String, String> params = new HashMap<>();
        params.put("region_id", mRegionId);
        params.put("merchant_id", merchantId);
        params.put("user_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_DELIVERY_CHARGE, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.log(response);
                        if (response.optString("delivery_charge").length() > 0) {
                            AppUtils.hideProgress(dialog);
                            if (!"notfound".equalsIgnoreCase(response.optString("msg"))) {
                                AppUtils.enableButtons(btnAddToBasket);
                                if (snackNotDeliverable.isShown()) snackNotDeliverable.dismiss();
                                deliveryCharge = response.optString("delivery_charge");
                                discount_type = response.optString("discount_type");
                                discount_value = response.optString("discount_value");
                                mHandler.sendEmptyMessage(1);
                                System.out.println("delCharge : " + deliveryCharge);
                                if (deliveryCharge != null) {
                                    if (Double.parseDouble(deliveryCharge) == 0) {
                                        //findViewById(R.id.relDeliveryCharge).setVisibility(View.GONE);
                                        //findViewById(R.id.tvDeliveryCharge).setVisibility(View.GONE);
                                        tvDeliveryCharge.setText(getString(R.string.free_delivery));
                                    } else if (".00".equalsIgnoreCase(deliveryCharge)) {
                                        // findViewById(R.id.relDeliveryCharge).setVisibility(View.GONE);
                                        //findViewById(R.id.tvDeliveryCharge).setVisibility(View.GONE);
                                        tvDeliveryCharge.setText(getString(R.string.free_delivery));
                                    } else {
                                        tvDeliveryCharge.setText(getString(R.string.currency) + AppUtils.changeToArabic(deliveryCharge, getApplicationContext()));

                                    }
                                } else {

                                    findViewById(R.id.relDeliveryCharge).setVisibility(View.GONE);
                                    findViewById(R.id.tvDeliveryCharge).setVisibility(View.GONE);
                                    TextView dltxt = findViewById(R.id.delTxt);
                                    dltxt.setText(getString(R.string.free_delivery));

                                }
                                String netPayable = String.valueOf(Double.parseDouble(AppUtils.changeToEnglish(deliveryCharge)) + Double.parseDouble(AppUtils.changeToEnglish(mTotal)));
                                if (Float.parseFloat(netPayable) <= 0.00) {
                                    tvNetPayable.setText(getString(R.string.currency) + "0.00");
                                } else {
                                    tvNetPayable.setText(getString(R.string.currency) + AppUtils.changeToArabic(AppUtils.monetize(netPayable), getApplicationContext()));
                                }
                                mNetPayable = netPayable;
                                mNetPayableUSD = String.valueOf(Double.parseDouble(mNetPayable) * mConvRate);
                                if (Float.parseFloat(mNetPayableUSD) <= 0.00) {
                                    tvNetPayableUSD.setText(getString(R.string.currency_us) + "0.00");
                                } else {
                                    tvNetPayableUSD.setText(getString(R.string.currency_us) + AppUtils.changeToArabic(mNetPayableUSD, getApplicationContext(), false));
                                    mPayTabAmountParam = Double.parseDouble(AppUtils.changeToArabic(mNetPayableUSD, getApplicationContext(), false));
                                }

                            } else {
                                AppUtils.disableButtons(btnAddToBasket);
                                snackNotDeliverable.show();
                            }
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

    private void loadAddressFromNW(String addressId) {
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
                                mCityId = response.optString("city");
                                mRegionId = response.optString("province_id");
                                tetName.setText(name);
                                tetMobile.setText(phone);
                                tetFathersName.setText(fathersName);
                                tetFamilyName.setText(familyName);
                                tetAddress.setText(address);
                                loadCityListFromNW();
                                loadRegionListFromNW(mCityId);
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

    private void changePaymentMethod(TextView textView) {
        for (PaymentMethod method : paymentMethods) {
            if (method.checked) {
                if (method.name != textView) {
                    method.setUnchecked();
                }
            } else {
                if (method.name == textView) {
                    method.setChecked();
                }
            }
        }
    }

    private void processPayTabPaymentMode() {
        Intent in = new Intent(getApplicationContext(), PayTabActivity.class);

        /*String mPayTabparamCustomerPhoneNumber = "", mPayTabParamCustomerEmail = "", mPayTabParamOrderId = "", mPayTabParamProductName = "",
                mPayTabParamAddressBilling = "", mPayTabParamCityBilling = "", mPayTabParamStateBilling = "", mPayTabParamCountryBilling = "", mPayTabParamPostalCodeBilling = "";
        double mPayTabAmountParam = 0;*/

        System.out.println("Rahul : PaymentActivity : processPayTabPaymentMode : mPayTabAmountParam : " + mPayTabAmountParam);
        System.out.println("Rahul : PaymentActivity : processPayTabPaymentMode : mPayTabparamCustomerPhoneNumber : " + mPayTabparamCustomerPhoneNumber);
        System.out.println("Rahul : PaymentActivity : processPayTabPaymentMode : mPayTabParamCustomerEmail : " + mPayTabParamCustomerEmail);
        System.out.println("Rahul : PaymentActivity : processPayTabPaymentMode : mPayTabParamOrderId : " + mPayTabParamOrderId);
        System.out.println("Rahul : PaymentActivity : processPayTabPaymentMode : mPayTabParamProductName : " + mPayTabParamProductName);
        System.out.println("Rahul : PaymentActivity : processPayTabPaymentMode : mPayTabParamAddressBilling : " + mPayTabParamAddressBilling);
        System.out.println("Rahul : PaymentActivity : processPayTabPaymentMode : mPayTabParamCityBilling : " + mPayTabParamCityBilling);
        System.out.println("Rahul : PaymentActivity : processPayTabPaymentMode : mPayTabParamStateBilling : " + mPayTabParamStateBilling);
        System.out.println("Rahul : PaymentActivity : processPayTabPaymentMode : mPayTabParamCountryBilling : " + mPayTabParamCountryBilling);
        System.out.println("Rahul : PaymentActivity : processPayTabPaymentMode : mPayTabParamPostalCodeBilling : " + mPayTabParamPostalCodeBilling);

        in.putExtra(PaymentParams.MERCHANT_EMAIL, "afieat21@gmail.com"); //this a demo account for testing the sdk
        in.putExtra(PaymentParams.SECRET_KEY, "rpm3BzVZlDjYzZoCZFhPM3DCfOOk5gVxGiAiSvhd8BcIBAkAgBp2F3FVRtfXKburu4JnA1AICNreoLPFnfH2inYaRe8CqC3UHNhi");//Add your Secret Key Here
        in.putExtra(PaymentParams.LANGUAGE, PaymentParams.ENGLISH);
        in.putExtra(PaymentParams.TRANSACTION_TITLE, "Afieat");
        in.putExtra(PaymentParams.AMOUNT, mPayTabAmountParam);

        in.putExtra(PaymentParams.CURRENCY_CODE, "USD");
        in.putExtra(PaymentParams.CUSTOMER_PHONE_NUMBER, mPayTabparamCustomerPhoneNumber);
        in.putExtra(PaymentParams.CUSTOMER_EMAIL, mPayTabParamCustomerEmail);
        in.putExtra(PaymentParams.ORDER_ID, mPayTabParamOrderId);
        in.putExtra(PaymentParams.PRODUCT_NAME, mPayTabParamProductName);

//Billing Address
        in.putExtra(PaymentParams.ADDRESS_BILLING, mPayTabParamAddressBilling);
        in.putExtra(PaymentParams.CITY_BILLING, mPayTabParamCityBilling);
        in.putExtra(PaymentParams.STATE_BILLING, mPayTabParamStateBilling);
        in.putExtra(PaymentParams.COUNTRY_BILLING, mPayTabParamCountryBilling);
        in.putExtra(PaymentParams.POSTAL_CODE_BILLING, mPayTabParamPostalCodeBilling); //Put Country Phone code if Postal code not available '00973'

//Shipping Address
        in.putExtra(PaymentParams.ADDRESS_SHIPPING, mPayTabParamAddressBilling);
        in.putExtra(PaymentParams.CITY_SHIPPING, mPayTabParamCityBilling);
        in.putExtra(PaymentParams.STATE_SHIPPING, mPayTabParamStateBilling);
        in.putExtra(PaymentParams.COUNTRY_SHIPPING, mPayTabParamCountryBilling);
        in.putExtra(PaymentParams.POSTAL_CODE_SHIPPING, mPayTabParamPostalCodeBilling); //Put Country Phone code if Postal code not available '00973'

//Payment Page Style
        in.putExtra(PaymentParams.PAY_BUTTON_COLOR, "#2474bc");

//Tokenization
        in.putExtra(PaymentParams.IS_TOKENIZATION, true);


        startActivityForResult(in, PaymentParams.PAYMENT_REQUEST_CODE);
    }

    private void initPayment() {
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            orderJsonObject = new JSONObject();
            DBHelper db = new DBHelper(PaymentActivity.this);
          /*  Map<String, String> params = new HashMap<>();
            params.put("merchant_id", db.getBasketMerchantId());
            params.put("sub_total", mTotal);
            System.out.println("nhfubfuhfneif : param : "+params);*/
            String paymentMethodStr = "";
            for (PaymentMethod paymentMethod : paymentMethods) {
                if (paymentMethod.checked) {
                    switch (paymentMethods.indexOf(paymentMethod)) {
                        case 0:
                            paymentMethodStr = "PAYTAB";
                            break;
                        case 1:
                            paymentMethodStr = "PREPAIDCARD";
                            break;
                        case 2:
                            paymentMethodStr = "COD";
                            break;
                        default:
                            break;
                    }
                    break;
                }
            }
//            params.put("payment_provider_name", )
            List<Food> foods = db.getFoodsBasket(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
            JSONArray foodsArray = new JSONArray();
            try {
                for (Food food : foods) {
                    JSONObject foodObject = new JSONObject();
                    foodObject.put("item_id", food.getId());
                    foodObject.put("size_id", food.getSizeBasketId());
                    foodObject.put("item_name", food.getName());
                    foodObject.put("item_prc", food.getUnitPrice());
                    foodObject.put("item_qty", food.getBasketCount());
                    foodObject.put("order_message", food.getComment());
                    JSONArray addOnsArray = new JSONArray();
                    String addOnsString = food.getAddOnIds();
                    String addOnPricesString = food.getAddOnPrices();
                    if (addOnsString.length() > 0) {
                        if (addOnsString.contains(";;")) {
                            String[] addOnsStringArray = addOnsString.split(";;");
                            String[] addOnPricesArray = addOnPricesString.split(";;");
                            for (int i = 0; i < addOnsStringArray.length; i++) {
                                JSONObject addOnObject = new JSONObject();
                                addOnObject.put("addon_id", addOnsStringArray[i]);
                                addOnObject.put("addon_price", addOnPricesArray[i]);
                                addOnsArray.put(i, addOnObject);
                            }
                        } else {
                            JSONObject addOnObject = new JSONObject();
                            addOnObject.put("addon_id", addOnsString);
                            addOnObject.put("addon_price", addOnPricesString);
                            addOnsArray.put(0, addOnObject);
                        }
                    }
                    foodObject.put("addons", addOnsArray);

                    JSONArray ingredientsArray = new JSONArray();
                    String ingredientsString = food.getIngredientIds();
                    String ingredientPricesString = food.getIngredientPrices();
                    if (ingredientsString.length() > 0) {
                        if (ingredientsString.contains(";;")) {
                            String[] ingredientsStringArray = ingredientsString.split(";;");
                            String[] ingredientPricesArray = ingredientPricesString.split(";;");
                            for (int i = 0; i < ingredientsStringArray.length; i++) {
                                JSONObject ingredientObject = new JSONObject();
                                ingredientObject.put("ingredient_id", ingredientsStringArray[i]);
                                ingredientObject.put("ingredient_price", ingredientPricesArray[i]);
                                ingredientsArray.put(i, ingredientObject);
                            }
                        } else {
                            JSONObject ingredientObject = new JSONObject();
                            ingredientObject.put("ingredient_id", ingredientsString);
                            ingredientObject.put("ingredient_price", ingredientPricesString);
                            ingredientsArray.put(0, ingredientObject);
                        }
                    }
                    foodObject.put("ingredients", ingredientsArray);

                    foodsArray.put(foods.indexOf(food), foodObject);
                }
                DecimalFormat form = new DecimalFormat("0.00");
                orderJsonObject.put("app_request_code", randomInt);
                orderJsonObject.put("client_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
                orderJsonObject.put("delivery_date", getIntent().getStringExtra(AppUtils.EXTRA_DEL_DATE));
                orderJsonObject.put("delivery_time", getIntent().getStringExtra(AppUtils.EXTRA_DEL_TIME));
                orderJsonObject.put("delivery_charge", String.valueOf(Double.parseDouble(mNetPayable) - Double.parseDouble(mTotal)));
                orderJsonObject.put("rest_discount", mRestaurantDiscount);
                orderJsonObject.put("promo_discount_amount", mPromo);
                orderJsonObject.put("merchant_id", db.getBasketMerchantId());
                orderJsonObject.put("voucher_amount_topay", mVoucherAmountToPay);
                // orderJsonObject.put("voucher_amount_topay",String.format("Value of mVoucherAmountToPay: %.2f",mVoucherAmountToPay));
                orderJsonObject.put("client_name", tetName.getText().toString());
                orderJsonObject.put("contact_phone", tetMobile.getText().toString());
                orderJsonObject.put("fathers_name", tetFathersName.getText().toString());
                orderJsonObject.put("family_name", tetFamilyName.getText().toString());
                orderJsonObject.put("city_id", mCityId);
                orderJsonObject.put("region_id", mRegionId);
                orderJsonObject.put("street", tetAddress.getText().toString());
                orderJsonObject.put("payment_method", paymentMethodStr);
                orderJsonObject.put("latitude", mLat);
                orderJsonObject.put("longitude", mLng);
                orderJsonObject.put("food_items", foodsArray);
                orderJsonObject.put("promo_voucher_id", promoVoucherId);
                orderJsonObject.put("promo_voucher_code", promoVoucherCode);
                orderJsonObject.put("point_value_used", mPointValueUsed);
                orderJsonObject.put("point_value_original", my_points);
                orderJsonObject.put("delivery_discount_type", discount_type);
                orderJsonObject.put("delivery_discount_value", discount_value);
                AppUtils.log(orderJsonObject.toString());
                if ("PREPAIDCARD".equals(paymentMethodStr)) {
                    checkWalletBalance();
                } else if ("PAYTAB".equals(paymentMethodStr)) {
                    requestorderId(mCityId, merchantId);
                } else {
                    placeOrderToNW();
                    //placeOrderTask();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnAddToBasket.performClick();
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    private void checkWalletBalance() {
        final ProgressDialog progressDialog = AppUtils.showProgress(this, "", getString(R.string.msg_please_wait));
        Map<String, String> params = new HashMap<>();
        params.put("shopuser_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_PREPAID_CARDS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.hideProgress(progressDialog);
                        AppUtils.log(response.toString());

                        if (Double.parseDouble(response.optString("wallet_balance")) < Double.parseDouble(mNetPayableUSD)) {
                            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_not_enough_wallet_balance), Snackbar.LENGTH_SHORT).show();
                        } else {
                            placeOrderToNW();
                            //placeOrderTask();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppUtils.hideProgress(progressDialog);
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    /*private String getFormattedPaymentMethod(String paymentMethodStr) {
        String payment = "";
        if (paymentMethodStr.equals("Credit Card")) {
            payment = "CREDITCARD";
        } else if (paymentMethodStr.equals("Prepaid Card")) {
            payment = "PREPAIDCARD";
        } else if (paymentMethodStr.equals("Cash On Delivery")) {
            payment = "COD";
        } else if (paymentMethodStr.equals("Paypal")) {
            payment = "PAYPAL";
        }
        return payment;
    }*/

    private void placeOrderToNW() {

        //Toast.makeText(this, "test--"+mVoucherAmountToPay, Toast.LENGTH_LONG).show();

        System.out.println("placeOrderToNW : called : ");
        try {
            Map<String, String> params = new HashMap<>();
            final ProgressDialog dialog = AppUtils.showProgress(PaymentActivity.this, getString(R.string.msg_placing_order), getString(R.string.msg_please_wait));
            params.put("order", orderJsonObject.toString());


            System.out.println("placeOrderToNW : param : " + params);


            String api = Apis.PLACE_ORDER;
            if ("CREDITCARD".equals(orderJsonObject.optString("payment_method")))
                api = Apis.PLACE_ORDER_CC;
            NetworkRequest request = new NetworkRequest(Request.Method.POST, api, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // AppUtils.log("Payment Success: " + response);
                    System.out.println("placeOrderToNW RES : " + response.toString());

                    Log.d("1,Response check", "" + response);
                    Log.d("****check", "   " + randomInt);

                    AppUtils.hideProgress(dialog);
                    if (response.optInt("status") == 999) {
                        new Relogin(PaymentActivity.this, new Relogin.OnLoginlistener() {
                            @Override
                            public void OnLoginSucess() {
                                placeOrderToNW();
                            }

                            @Override
                            public void OnError(String Errormessage) {
                                startActivityForResult(new Intent(PaymentActivity.this, LoginActivity.class), AppUtils.REQ_LOGIN);
                            }
                        }).execute();
                    } else {
                        if ("success".equals(response.optString("status")) || "1".equals(response.optString("success"))) {
                            startActivityForResult(new Intent(PaymentActivity.this, PaymentSuccessActivity.class)
                                    .putExtra(AppUtils.EXTRA_RESTAURANT_ID, merchantId)
                                    .putExtra(AppUtils.EXTRA_DEL_ADDRESS, response.optString("address"))
                                    .putExtra(AppUtils.EXTRA_ORDER_NO, response.optString("order_no"))
                                    .putExtra(AppUtils.EXTRA_DEL_DATE, getIntent().getStringExtra(AppUtils.EXTRA_DEL_DATE))
                                    .putExtra(AppUtils.EXTRA_DEL_TIME, response.optString("delivery_time")), AppUtils.REQ_PLACE_ORDER);

                            try {
                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.MY_POINTS, response.getString("point_value"));
                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.MY_POINTS_VALUE, response.getString("point"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        if (response.optString("payment_url").length() > 0) {
                            startActivityForResult(new Intent(PaymentActivity.this, CardPaymentActivity.class)
                                            .putExtra(AppUtils.EXTRA_PAYMENT_URL, response.optString("payment_url"))
                                            .putExtra(AppUtils.EXTRA_RESTAURANT_ID, merchantId),
                                    AppUtils.REQ_PLACE_ORDER);
                        }
                    }
                }

            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("2,error check", "" + error);
                            System.out.println("placeOrderToNW : error :" + error.getMessage());
                            error.printStackTrace();
                            btnAddToBasket.setClickable(true);
                            AppUtils.hideProgress(dialog);
                        }
                    }
            );
            AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
            btnAddToBasket.setClickable(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//..................................

    /**
     * search all vedio by category task
     */
    private void placeOrderTask() {
        try {
            final ProgressDialog dialog = AppUtils.showProgress(PaymentActivity.this, getString(R.string.msg_placing_order), getString(R.string.msg_please_wait));

            ApiInterface apiService = ApiClient.GetClient().create(ApiInterface.class);
            Call<Json_Response> call = apiService.setPlaceOrder(orderJsonObject.toString());
            call.enqueue(new Callback<Json_Response>() {
                @Override
                public void onResponse(Call<Json_Response> call, retrofit2.Response<Json_Response> response) {
                    Log.e("RESPONSE>>>>", response.toString() + ">>>>>>>>>>>>>>");
                    AppUtils.hideProgress(dialog);


                }

                @Override
                public void onFailure(Call<Json_Response> call, Throwable t) {
                    AppUtils.hideProgress(dialog);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyRewardPoints() {
        try {

            Map<String, String> params = new HashMap<>();
            params.put("resid", "");
            params.put("group_id", "");
            AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);

            if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                params.put("lang", "en");
            } else {
                params.put("lang", "ar");
            }
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_FOOD_ITEMS, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            System.out.println("getFoodItemsFromNW : response : " + response);

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
        } catch (Exception e) {
            e.printStackTrace();

            //   AppUtils.hideViews(progressBar);
        }

    }


    //.......................................
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    /*DBHelper db = new DBHelper(this);
                    db.updateLoggedInUser(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));*/
                    loadAddressFromNW();
                } else
                    finish();
                break;
            case REQUEST_ADDRESS:
                if (resultCode == RESULT_OK) {
                    String addressId = data.getStringExtra(AppUtils.EXTRA_ADDRESS_ID);
                    loadAddressFromNW(addressId);
                } else {
                    finish();
                }
                break;
            case AppUtils.REQ_PLACE_ORDER:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    DBHelper dbHelper = new DBHelper(PaymentActivity.this);
                    dbHelper.deleteAll();
                    finish();
                }
                break;
            default:
                break;
        }

        if (resultCode == RESULT_OK && requestCode == PaymentParams.PAYMENT_REQUEST_CODE) {
            Log.e("Tag", data.getStringExtra(PaymentParams.RESPONSE_CODE));
            Log.e("Tag", data.getStringExtra(PaymentParams.TRANSACTION_ID));
            System.out.println("Rahul : PaymentActivity : onActivityResult : RESPONSE_CODE : " + data.getStringExtra(PaymentParams.RESPONSE_CODE));
            System.out.println("Rahul : PaymentActivity : onActivityResult : TRANSACTION_ID : " + data.getStringExtra(PaymentParams.TRANSACTION_ID));
            System.out.println("Rahul : PaymentActivity : onActivityResult : RESULT_MESSAGE : " + data.getStringExtra(PaymentParams.RESULT_MESSAGE));

            if (data.getStringExtra(PaymentParams.RESPONSE_CODE).equals("100")) {
                try {
                    orderJsonObject.put("transactionID", data.getStringExtra(PaymentParams.TRANSACTION_ID));
                    orderJsonObject.put("order_number", mPayTabParamOrderId);
                    placeOrderToNW();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), data.getStringExtra(PaymentParams.RESULT_MESSAGE), Toast.LENGTH_SHORT).show();
            }

            if (data.hasExtra(PaymentParams.TOKEN) && !data.getStringExtra(PaymentParams.TOKEN).isEmpty()) {
                Log.e("Tag", data.getStringExtra(PaymentParams.TOKEN));
                Log.e("Tag", data.getStringExtra(PaymentParams.CUSTOMER_EMAIL));
                Log.e("Tag", data.getStringExtra(PaymentParams.CUSTOMER_PASSWORD));
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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

    class PaymentMethod {
        TextView name;
        boolean checked;

        public PaymentMethod(TextView name, boolean checked) {
            this.name = name;
            this.checked = checked;
        }

        public void setChecked() {
            checked = true;
            if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG)))
                name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
            else
                name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
        }

        public void setUnchecked() {
            checked = false;
            if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG)))
                name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.uncheck, 0);
            else
                name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.uncheck, 0, 0, 0);
        }
    }

    class City {
        String id, name;
    }

    class Region {
        String id, name;
    }


}
