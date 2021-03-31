package com.afieat.ini;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.adapters.MyPointsAdapter;
import com.afieat.ini.models.MyPointsModel;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;*/

public class MyPoints extends AppCompatActivity {

    private Dialog afieatGifLoaderDialog;
    private  ArrayList<MyPointsModel> mMyPointsArrayList = new ArrayList<>();
    private  RecyclerView rewardPointRecyclerView;
    private MyPointsAdapter mMyPointsAdapter;

    private ImageView shareReferralCode;
    private TextView rewardPointsValue;
    private TextView referalCode;
   public int ofst = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_points);

        androidx.appcompat.widget.Toolbar mToolbar;
        mToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.my_points_title));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        rewardPointRecyclerView = findViewById(R.id.rewardPointRecyclerView);
        rewardPointRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mMyPointsAdapter = new MyPointsAdapter(this, mMyPointsArrayList);
        rewardPointRecyclerView.setAdapter(mMyPointsAdapter);

        shareReferralCode = findViewById(R.id.shareReferralCode);
        shareReferralCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String domain = "https://afieat.page.link/?amv=0&apn=com.afieat.ini&link=http://afieat.com/signup/" + referalCode.getText().toString();

                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");

                //System.out.println("Firebase : " + mVouchers.getmVoucherShareLink());
                // System.out.println("Firebase : "+deepLink.toString());

                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                share.putExtra(Intent.EXTRA_TEXT, domain);
                startActivity(Intent.createChooser(share, "Share Afieat Voucher link!"));


            }
        });

        referalCode = findViewById(R.id.referalCode);

        ImageView copyReferalCode = findViewById(R.id.copyReferalCode);

        copyReferalCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager myClipboard = myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ;
                ClipData myClip;
                myClip = ClipData.newPlainText("text", referalCode.getText().toString());
                myClipboard.setPrimaryClip(myClip);

                Toast.makeText(getApplicationContext(), "Referal Code Copied",
                        Toast.LENGTH_SHORT).show();
            }
        });

        TextView referFriendBtn=findViewById(R.id.referFriendBtn);

        referFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String domain = "https://afieat.page.link/?amv=0&apn=com.afieat.ini&link=http://afieat.com/signup/" + referalCode.getText().toString();

                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                //System.out.println("Firebase : " + mVouchers.getmVoucherShareLink());
                // System.out.println("Firebase : "+deepLink.toString());

                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                share.putExtra(Intent.EXTRA_TEXT, domain);
                startActivity(Intent.createChooser(share, "Share Afieat Voucher link!"));
                //createDynamicLink(getApplicationContext());
            }
        });
        fetchMyPoints(0);


    }


/*    public void createDynamicLink(final Context context){
        final Uri[] mInvitationUrl = {null};
        String link = "http://voucher";
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDynamicLinkDomain("afieat.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder()
                                .build())
                .setGoogleAnalyticsParameters(
                        new DynamicLink.GoogleAnalyticsParameters.Builder()
                                .setSource("In-App")
                                .setMedium("social")
                                .setCampaign("Word-Word")
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("Afieat")
                                .setDescription("Sent and Receive Notification like this.Download the app now")
                                .build())

                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        mInvitationUrl[0] = shortDynamicLink.getShortLink();
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        context.startActivity(Intent.createChooser(intent, "Share"));
                    }
                });

    }*/

    private void fetchMyPoints(int argofst) {
        // AppUtils.showViews(progressBar);
        afieatGifLoaderDialog();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", "72976");
        params.put("lang", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG));
        params.put("lmt", "10");
        params.put("ofst", String.valueOf(argofst));



        /*if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("en")) {
            params.put("lang", "en");
        }*/
        System.out.println("param : " + params);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.MY_POINTS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  AppUtils.hideViews(progressBar);

                        System.out.println("huhuhuhuhuhu : " + response);
                        afieatGifLoaderDialog.dismiss();


                        try {
                            //System.out.println("huhuhuhuhuhu : "+response);
                            JSONObject order_details = response.getJSONObject("order_details");
                            JSONArray order = order_details.getJSONArray("order");

                            // System.out.println("huhuhuhuhuhu : "+response.getString("point"));
                            rewardPointsValue = findViewById(R.id.rewardPointsValue);
                            rewardPointsValue.setText(response.getString("point"));
                            TextView loyaltyGName = findViewById(R.id.loyaltyGName);
                            loyaltyGName.setText(response.getString("loyalitygname"));
                            if (response.getString("referal_code").isEmpty()) {

                                findViewById(R.id.rell1).setVisibility(View.GONE);
                            } else {
                                referalCode.setText(response.getString("referal_code"));
                            }
                            TextView referalCount = findViewById(R.id.referalCount);
                            referalCount.setText(response.getString("referal_count"));
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.MY_POINTS_VALUE, response.getString("point"));

                            for (int i = 0; i < order.length(); i++) {
                                String order_id = order.getJSONObject(i).getString("order_id");
                                String order_number = order.getJSONObject(i).getString("order_number");
                                String merchant_id = order.getJSONObject(i).getString("merchant_id");
                                String voucher_amount = order.getJSONObject(i).getString("voucher_amount");
                                String date = order.getJSONObject(i).getString("delivery_date");
                                String status = order.getJSONObject(i).getString("status");
                                String pointadd = order.getJSONObject(i).getString("pointadd");
                                String pointredeem = order.getJSONObject(i).getString("pointredeem");


                                MyPointsModel myPointsModel = new MyPointsModel(order_id, order_number, merchant_id, voucher_amount, pointadd, date, status, pointredeem);
                                mMyPointsArrayList.add(myPointsModel);

                            }

                            mMyPointsAdapter.notifyDataSetChanged();
                            afieatGifLoaderDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            afieatGifLoaderDialog.dismiss();
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

    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(this);
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }


    public void onBottomReached()
    {
        ofst=ofst+10;

        fetchMyPoints(ofst);


    }

}
