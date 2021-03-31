package com.afieat.ini;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afieat.ini.models.Vouchers;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VoucherDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView barCodeImg;
    private TextView shareVoucher;


    private Handler mHandler;
    private Vouchers mVouchers;
    private TextView voucherRefOrderNum,
            voucherStatus,
            voucherDiscountAmt,
            voucherExpiry,
            tvRedeembtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_detail);
        androidx.appcompat.widget.Toolbar mToolbar;

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {

                return false;
            }
        });

        mToolbar = findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_my_voucher_detail));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mVouchers = getIntent().getParcelableExtra("vouchers");
        String imgUrl = mVouchers.getVoucher_img_url();
        String title = mVouchers.getVoucher_Name();
        String ref_code_no = mVouchers.getVoucher_ref_order_no();
        String status = mVouchers.getVoucher_isRedeemed();

        shareVoucher = findViewById(R.id.shareVoucher);
        shareVoucher.setOnClickListener(this);

        barCodeImg = findViewById(R.id.barCodeImg);
        //ImageView refresh=findViewById(R.id.refresh);
        TextView Title = findViewById(R.id.Title);
        voucherRefOrderNum = findViewById(R.id.voucherRefOrderNum);
        voucherStatus = findViewById(R.id.voucherStatus);
        voucherDiscountAmt = findViewById(R.id.voucherDiscountAmt);
        voucherExpiry = findViewById(R.id.voucherExpiry);
        tvRedeembtn = findViewById(R.id.tvReedembtn);

        tvRedeembtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redeemVoucher(mVouchers.getVoucherId());
            }
        });

        Glide
                .with(getApplicationContext())
                .load(imgUrl)
                .placeholder(R.drawable.placeholder_land)
                .into(barCodeImg);

        Title.setText(mVouchers.getVoucher_Restaurant_Name());
        voucherRefOrderNum.setText(mVouchers.getVoucher_ref_order_no());
        voucherStatus.setText(mVouchers.getVoucher_isRedeemed());
        voucherDiscountAmt.setText(mVouchers.getVoucher_discount_amt());

        voucherExpiry.setText(mVouchers.getVoucher_Expiration().replace("/", "-"));
        if ("Active".equalsIgnoreCase(mVouchers.getVoucher_isRedeemed())) {
            voucherStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greenButton));
        } else {
            voucherStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.orangeButton));
            tvRedeembtn.setVisibility(View.GONE);
            shareVoucher.setVisibility(View.GONE);
        }
/*

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              */
/*  overridePendingTransition(0,0);
                startActivity(new Intent(VoucherDetailActivity.this,VoucherDetailActivity.class));
                overridePendingTransition(0,0);
                finish();*//*

            }
        });
*/

        ImageTransitionEffect();
    }


    private void shareVoucher() {
        String url = String.valueOf(Html.fromHtml("<a href=" + mVouchers.getmVoucherShareLink() + ">" + mVouchers.getmVoucherShareLink() + "</a>"));
        url = url.replace("34.205.164.176", "www.afieat.com");
        // Create a deep link and display it in the UI
        //  final Uri deepLink = buildDeepLink(Uri.parse(url.toString()), 0);

        String domain = "https://ymnf9" + ".app.goo.gl?amv=0&apn=com.afieat.ini&link=";

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/html");
        System.out.println("Firebase : " + mVouchers.getmVoucherShareLink());
        // System.out.println("Firebase : "+deepLink.toString());

        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.

        domain = getResources().getString(R.string.voucher_msg) + "\n\n" + domain;
//    domain= Html.fromHtml("<a href="+domain+"</a>");
        System.out.println("Firebase : final : " + domain + url);

        URL myUri = null;
        try {
            myUri = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        share.putExtra(Intent.EXTRA_TEXT, domain + myUri);
        startActivity(Intent.createChooser(share, "Share Afieat Voucher link!"));
    }

    public void ImageTransitionEffect() {

        Bundle extras = getIntent().getExtras();
        String logoPath = extras.getString(RestaurantListActivity.EXTRA_ANIMAL_ITEM);

        supportPostponeEnterTransition();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String imageTransitionName = extras.getString(RestaurantListActivity.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME);
            barCodeImg.setTransitionName(imageTransitionName);
        }

       /* Picasso.with(this)
                .load(logoPath)
                .noFade()
                .into(barCodeImg, new Callback() {
                    @Override
                    public void onSuccess() {
                        supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        supportStartPostponedEnterTransition();
                    }
                });*/

    }

    /*
     */

    /**
     * Build a Firebase Dynamic Link.
     * https://firebase.google.com/docs/dynamic-links/android/create#create-a-dynamic-link-from-parameters
     * <p>
     * //* @param deepLink the deep link your app will open. This link must be a valid URL and use the
     * HTTP or HTTPS scheme.
     * //* @param minVersion the {@code versionCode} of the minimum version of your app that can open
     * the deep link. If the installed app is an older version, the user is taken
     * to the Play store to upgrade the app. Pass 0 if you do not
     * require a minimum version.
     *
     * @return a {@link Uri} representing a properly formed deep link.
     *//*

    @VisibleForTesting
    public Uri buildDeepLink(@NonNull Uri deepLink, int minVersion) {
        String domain = "ymnf9" + ".app.goo.gl";

        // Set dynamic link parameters:
        //  * Domain (required)
        //  * Android Parameters (required)
        //  * Deep link
        // [START build_dynamic_link]
        DynamicLink.Builder builder = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setDynamicLinkDomain(domain)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
                        .setMinimumVersion(minVersion)
                        .build())
                .setLink(deepLink);

        // Build the dynamic link
        DynamicLink link = builder.buildDynamicLink();
        // [END build_dynamic_link]

        // Return the dynamic link as a URI
        return link.getUri();
    }

*/
    private void redeemVoucher(String mVoucherId) {
        System.out.println("VoucherActivity : mVoucherId : " + mVoucherId);
        String lang = "";
        if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

        } else {
            lang = "en";
        }


        Map<String, String> params = new HashMap<>();
        params.put("user_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        params.put("id", mVoucherId);

        NetworkRequest request = new NetworkRequest(Request.Method.POST, "http://34.205.164.176/webs/RedeemMyVoucher", params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("VoucherRedeem : " + response);


                        // mHandler.sendEmptyMessageAtTime(1,1000);
                        final Dialog mDialog = new Dialog(VoucherDetailActivity.this);
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
                            }
                        }, 3000);

                        if (response.optString("status").contentEquals("1")) {
                            voucherStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.orangeButton));
                            voucherStatus.setText("Redeemed");
                            tvRedeembtn.setVisibility(View.GONE);
                            shareVoucher.setVisibility(View.GONE);
                        }

                        System.out.println("Rahul : loadVoucher : response : " + response);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
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
        supportFinishAfterTransition();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.shareVoucher:
                shareVoucher();
                break;
            default:
                break;  }
    }
}
