package com.afieat.ini;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afieat.ini.adapters.ReviewsAdapter;
import com.afieat.ini.models.Review;
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
import java.util.List;
import java.util.Map;

public class ReviewsActivity extends AppCompatActivity {


    private ListView lvReviews;
    private TextView tvNoReviews;
    // private ProgressBar progressBar;
    private Dialog afieatGifLoaderDialog;
    private RatingBar rating;
    private EditText etReview;

    private String mRestaurantId, mReviewBody, mReviewRating;

    private List<Review> reviews;

    private AlertDialog alertReview;

    public ReviewsActivity() {
        reviews = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
         Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_my_reviews));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lvReviews = (ListView) findViewById(R.id.lvReviews);
        tvNoReviews = (TextView) findViewById(R.id.tvNoReviews);
        //  progressBar = (ProgressBar) findViewById(R.id.progressBar);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View ratingView = LayoutInflater.from(this).inflate(R.layout.layout_review_add, null, false);
        rating = (RatingBar) ratingView.findViewById(R.id.rating);
        etReview = (EditText) ratingView.findViewById(R.id.etMyReview);
        builder.setView(ratingView);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String star = String.valueOf(rating.getRating());
                String review = etReview.getText().toString().trim();
                if (!("0".equals(star) || review.length() == 0)) {
                    publishReviewToNW(star, review);
                }
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rating.setRating(0);
                etReview.setText("");
            }
        });
        alertReview = builder.create();

        if ("".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID))) {
            startActivityForResult(new Intent(this, LoginActivity.class), 100);
        } else {
            fetchReviewsFromNW();
        }
    }

    private void publishReviewToNW(String star, String review) {
        //AppUtils.showViews(progressBar);
        afieatGifLoaderDialog();
        Map<String, String> params = new HashMap<>();
        params.put("resid", mRestaurantId);
        params.put("user_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        params.put("ratecount", star);
        params.put("review", review);
        if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            params.put("lang", "en");
        } else {
            params.put("lang", "ar");
        }
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.POST_REVIEW, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  AppUtils.hideViews(progressBar);
                        afieatGifLoaderDialog.dismiss();
                        AppUtils.log(response);
                        fetchReviewsFromNW();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //AppUtils.hideViews(progressBar);
                        afieatGifLoaderDialog.dismiss();
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void fetchReviewsFromNW() {
        reviews.clear();
        //  AppUtils.showViews(progressBar);
        afieatGifLoaderDialog();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            params.put("lang", "en");
        } else {
            params.put("lang", "ar");
        }
        Log.d("@@ userId- ", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        Log.d("@@ URL- ", Apis.GET_MY_REVIEWS);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_MY_REVIEWS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //   AppUtils.hideViews(progressBar);
                        if (afieatGifLoaderDialog != null) {
                            afieatGifLoaderDialog.dismiss();
                        }
                        AppUtils.log(response);
                        JSONObject reviewDetailsObject = response.optJSONObject("review_details");
                        if (reviewDetailsObject != null) {
                            JSONArray reviewsArray = reviewDetailsObject.optJSONArray("review");
                            for (int i = 0; i < reviewsArray.length(); i++) {
                                JSONObject reviewObject = reviewsArray.optJSONObject(i);
                                Review review = new Review();
                                review.setReviewId(reviewObject.optString("id"));
                                review.setMerchantId(reviewObject.optString("merchant_id"));
                                review.setMerchantName(reviewObject.optString("restaurant_name"));
                                review.setRating(reviewObject.optString("rating"));
                                review.setReviewBody(reviewObject.optString("review"));
                                review.setReviewDate(reviewObject.optString("date_created").split("\\.")[0]);
                                review.setImageName(reviewObject.optString("reviewimages"));
                                reviews.add(review);
                            }
                            ReviewsAdapter reviewsAdapter = new ReviewsAdapter(ReviewsActivity.this, reviews);
                            lvReviews.setAdapter(reviewsAdapter);
                            reviewsAdapter.setOnReviewAlterListener(new ReviewsAdapter.OnReviewAlterListener() {
                                @Override
                                public void onReviewEdit(Review review) {
//                                    mRestaurantId = review.getMerchantId();
//                                    mReviewBody = review.getReviewBody();
//                                    mReviewRating = review.getRating();
//                                    rating.setRating(Float.parseFloat(mReviewRating));
//                                    etReview.setText(mReviewBody);
//                                    alertReview.show();
                                    startActivityForResult(new Intent(ReviewsActivity.this, CustomerReviewActivity.class)
                                            .putExtra("resid", review.getMerchantId())
                                            .putExtra("rev_id", review.getReviewId())
                                            .putExtra("resname", review.getMerchantName())
                                            .putExtra("purpose", 2)
                                            .putExtra("revewtext", review.getReviewBody())
                                            .putExtra("rev_rating", review.getRating())
                                            .putExtra("rev_images", review.getImageName()), 100);

                                }

                                @Override
                                public void onReviewDelete(String reviewId) {
                                    deleteReviewFromNW(reviewId);
                                }
                            });
                        } else {
                            tvNoReviews.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //AppUtils.hideViews(progressBar);
                        if (afieatGifLoaderDialog != null) {
                            afieatGifLoaderDialog.dismiss();
                        }
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void deleteReviewFromNW(String reviewId) {
        //    AppUtils.showViews(progressBar);
        afieatGifLoaderDialog();
        Map<String, String> params = new HashMap<>();
        params.put("rvwid", reviewId);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.DELETE_REVIEW, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //   AppUtils.hideViews(progressBar);
                        if (afieatGifLoaderDialog != null) {
                            afieatGifLoaderDialog.dismiss();
                        }
                        AppUtils.log(response);
                        fetchReviewsFromNW();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //  AppUtils.hideViews(progressBar);
                        if (afieatGifLoaderDialog != null) {
                            afieatGifLoaderDialog.dismiss();
                        }
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100:
                fetchReviewsFromNW();
                break;
            default:
                break;  }
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


    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(this);
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }
}
