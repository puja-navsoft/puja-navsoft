package com.afieat.ini;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.adapters.VoucherAdapterRecyclerView;
import com.afieat.ini.interfaces.OnBottomReachedListener;
import com.afieat.ini.interfaces.VoucherListener;
import com.afieat.ini.models.Vouchers;
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
import java.util.Map;

public class VoucherActivity extends AppCompatActivity implements VoucherListener, OnBottomReachedListener {

    private RecyclerView voucherRecyclerView;
    private VoucherAdapterRecyclerView mVoucherAdapter;
    private ArrayList<Vouchers> mVouchersList = new ArrayList<>();
    public static int limit = 10;
    public static final String EXTRA_ANIMAL_ITEM = "image_url";
    public static final String EXTRA_ANIMAL_IMAGE_TRANSITION_NAME = "image_transition_name";
    private Dialog afieatGifLoaderDialog;

    public static final int REQUEST_CODE = 1;
    private boolean willLoadMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        androidx.appcompat.widget.Toolbar mToolbar;
        mToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_my_voucher));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        voucherRecyclerView = findViewById(R.id.voucherRecyclerView);
        voucherRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mVoucherAdapter = new VoucherAdapterRecyclerView(getApplicationContext(), mVouchersList, this, this, this);
        voucherRecyclerView.setAdapter(mVoucherAdapter);

        Intent intent = getIntent();
        // String action = intent.getAction();

        Uri data = null;
        System.out.println("tyfyufuyfufufuyu");
        if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID).length() > 0) {
            data = intent.getData();
            System.out.println("Firebase : data : " + data);
            System.out.println("Firebase : AppUtils.Voucher_Data : " + AppUtils.Voucher_Data);
            if (data != null) {
                if (data != null) {
                    verifyVoucher(data.toString());

                }

            } else if (AppUtils.Voucher_Data.length() > 0) {

                verifyVoucher(AppUtils.Voucher_Data.toString());

            }
        } else {
            AppUtils.Voucher_Data = intent.getData().toString();
            startActivity(new Intent(VoucherActivity.this, LoginActivity.class));
            finish();
        }


    }


    private void loadVoucher(final int limit) {
        System.out.println("VoucherActivity : Limit : " + limit);
        System.out.println("VoucherActivity : USERID : " + AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        String lang = "";

        if (afieatGifLoaderDialog == null) {
            System.out.println("afieatGifLoaderDialog null");
            afieatGifLoaderDialog();
        }

        if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

        } else {
            lang = "en";
        }

        Map<String, String> params = new HashMap<>();
        params.put("user_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        params.put("limit", "10");
        params.put("ofst", String.valueOf(limit));
        params.put("lang", lang);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_MY_VOUCHERS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Voucher : bjbkbkjbk : " + response);
                        afieatGifLoaderDialog.dismiss();
                        if ("success".equalsIgnoreCase(response.optString("msg"))) {
                            JSONArray vouchers = response.optJSONArray("vouchers");
                            System.out.println("Voucher : length : " + vouchers.length());
                            if (vouchers.length() > 0) {
                                if (vouchers != null) {

                                    for (int i = 0; i < vouchers.length(); i++) {

//    public Vouchers(String voucherId, String voucher_Name, String voucher_Restaurant_Name, String voucher_ref_order_no,
// String voucher_discount_amt, String voucher_img_url, String voucher_isRedeemed, String voucher_Expiration,
// String voucher_status) {

                                        Vouchers mVouchers = new Vouchers(
                                                vouchers.optJSONObject(i).optString("voucher_id"),
                                                vouchers.optJSONObject(i).optString("restaurant_name"),
                                                vouchers.optJSONObject(i).optString("voucher_name"),
                                                vouchers.optJSONObject(i).optString("order_number"),
                                                vouchers.optJSONObject(i).optString("amount"),
                                                vouchers.optJSONObject(i).optString("QRCodeImge"),
                                                vouchers.optJSONObject(i).optString("isredemed"),
                                                vouchers.optJSONObject(i).optString("expiration"),
                                                vouchers.optJSONObject(i).optString("status"),
                                                vouchers.optJSONObject(i).optString("merchant_photo_bg"),
                                                vouchers.optJSONObject(i).optString("voucher_share_link"));
                                        mVouchersList.add(mVouchers);

                                    }
                                }
                            } else {
                                System.out.println("Voucher : willLoadMore :  " + willLoadMore);
                                if (limit == 0) {
                                    TextView noFavourites = findViewById(R.id.noVoucher);
                                    noFavourites.setVisibility(View.VISIBLE);
                                }

                                willLoadMore = false;
                            }
                        } else {
                            System.out.println("Rahul : loadVoucher : response : " + response);
                            if (limit == 0) {
                                TextView noFavourites = findViewById(R.id.noVoucher);
                                noFavourites.setVisibility(View.VISIBLE);
                            }
                            willLoadMore = false;
                        }
                        // showOpeningTimes(openingTimesArray);
                        mVoucherAdapter.notifyDataSetChanged();
                        // AppUtils.showViews(svInfo);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (limit == 0) {
                            TextView noFavourites = findViewById(R.id.noVoucher);
                            noFavourites.setVisibility(View.VISIBLE);
                        }
                        willLoadMore = false;
                        error.printStackTrace();
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // loadRestaurantInfoFromNW();
                            }
                        });
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
                        if (afieatGifLoaderDialog != null)
                            afieatGifLoaderDialog.dismiss();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }


    private void verifyVoucher(String urlVoucherVerify) {

        System.out.println("VoucherVerify : USERID : " + AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        String lang = "";

        if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

        } else {
            lang = "en";
        }

        String verifyVoucherURI = urlVoucherVerify + "/" + AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);
        verifyVoucherURI = verifyVoucherURI.replace("www.afieat.com", "34.205.164.176");
        System.out.println("VoucherActivity : verifyVoucherURI : " + verifyVoucherURI);
        NetworkRequest request = new NetworkRequest(Request.Method.GET, verifyVoucherURI, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("VoucherVerify : " + response);


                        final Dialog mDialog = new Dialog(VoucherActivity.this);
                        mDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                        mDialog.setContentView(R.layout.voucher_success_msg_dialog);
                        TextView message = mDialog.findViewById(R.id.message);
                        message.setText(response.optString("msg"));
                        mDialog.show();
                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.dismiss();
                                //  loadVoucher(0);
                            }
                        }, 3000);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // loadRestaurantInfoFromNW();
                            }
                        });
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();


                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }


    @Override
    public void onBottomReached(int position) {

    }

    public void onBottomReachedR() {
        if (willLoadMore) {
            limit = +limit;
            System.out.println("VoucherActivity : onBottomReachedR : " + limit);
            loadVoucher(limit);
        }
    }


    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onVoucherClickListner(Vouchers mVouchers, int pos, ImageView shareImageView) {

        Intent intent = new Intent(VoucherActivity.this, VoucherDetailActivity.class);
        intent.putExtra("vouchers", mVouchers);
        intent.putExtra(EXTRA_ANIMAL_ITEM, mVouchers.getVoucher_img_url());
        intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, mVouchers.getVoucher_ref_order_no());
        intent.putExtra("logoPath", mVouchers.getVoucher_img_url());
        intent.putExtra(EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(shareImageView));
        intent.putExtra("img_url", mVouchers.getVoucher_img_url());

       /* ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                VoucherActivity.this,
                shareImageView,
                ViewCompat.getTransitionName(shareImageView));

        startActivity(intent, options.toBundle());*/
        startActivityForResult(intent, REQUEST_CODE);
    }


    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(this);
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println("mnvjvjm : requestCode : resultCode : " + requestCode + " , " + resultCode);
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE) {

                System.out.println("lvslmvlemlvmsfm");
                //loadVoucher(0);
            }
        } catch (Exception ex) {
            Toast.makeText(VoucherActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        mVouchersList.clear();
        mVoucherAdapter.notifyDataSetChanged();
        loadVoucher(0);
    }


}
