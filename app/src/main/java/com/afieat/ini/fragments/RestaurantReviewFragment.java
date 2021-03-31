package com.afieat.ini.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.CustomerReviewActivity;
import com.afieat.ini.DetailActivityClick_Page;
import com.afieat.ini.LoginActivity;
import com.afieat.ini.R;
import com.afieat.ini.RestaurantsDetailActivity;
import com.afieat.ini.adapters.RestaurantReviewAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantReviewFragment extends Fragment {

    private RelativeLayout rlMyReview;
    private ListView lvReviews;
    private EditText etMyReview;

  //  private ProgressBar progressBar;
    private Dialog afieatGifLoaderDialog;
    private TextView tvNoReviews;
    private RatingBar rating;
    private EditText etReview;
    private TextView tvReviewCount, tvRating;
    private RatingBar ratingAvg;

    private String mRestaurantId, mReviewRating;
    private List<Review> reviews;
    private RestaurantsDetailActivity mRestaurantsDetailActivity;
    private AlertDialog alertReview;
    private TextView basedontotal;
    private View view;

    public RestaurantReviewFragment() {
        reviews = new ArrayList<>();
    }

    public static RestaurantReviewFragment newInstance(String resId) {
        RestaurantReviewFragment fragment = new RestaurantReviewFragment();
        fragment.mRestaurantId = resId;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AppUtils.IS_OPEN = true;
        return inflater.inflate(R.layout.fragment_restaurant_review, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        basedontotal = (TextView) view.findViewById(R.id.basedontotal);
        tvNoReviews = (TextView) view.findViewById(R.id.tvNoReviews);
        rlMyReview = (RelativeLayout) view.findViewById(R.id.rlMyReview);
        etMyReview = (EditText) view.findViewById(R.id.etMyReview);
        lvReviews = (ListView) view.findViewById(R.id.lvReviews);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lvReviews.setNestedScrollingEnabled(true);
        }
        ViewCompat.setNestedScrollingEnabled(lvReviews, true);

//        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
         Button btnSubmitReview;
        btnSubmitReview = (Button) view.findViewById(R.id.btnSubmitReview);
        tvRating = (TextView) view.findViewById(R.id.tvRating);
        tvReviewCount = (TextView) view.findViewById(R.id.tvReviewCount);
        ratingAvg = (RatingBar) view.findViewById(R.id.ratingAvg);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String orderNo = sharedPref.getString(AppUtils.PREF_ORDER_NO, "");
        //  Log.d("anup-chk1","    " + orderNo);
        if (!"0".equals(orderNo)) {
            btnSubmitReview.setVisibility(View.VISIBLE);

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View ratingView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_review_add, null, false);
        rating = (RatingBar) ratingView.findViewById(R.id.rating);
        etReview = (EditText) ratingView.findViewById(R.id.etMyReview);
        builder.setView(ratingView);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String star = String.valueOf(rating.getRating());
                String review = etReview.getText().toString().trim();
                rating.setRating(0);
                etReview.setText("");
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

        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID))) {
//                    rating.setRating(0);
//                    etReview.setText("");
//                    alertReview.show();
                    /*
                    @Koushik
                    for activity call add review and edit review also from here have to call add review
                    key purpose=1 means add
                    key purpose=2 means edit
                     */
                    startActivityForResult(new Intent(getActivity(), CustomerReviewActivity.class).putExtra("purpose", 1).putExtra("resid", mRestaurantId).putExtra("resname", ((DetailActivityClick_Page) getActivity()).mRestaurantName), 101);

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.msg_please_login), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });
        etMyReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID))) {
                    rating.setRating(0);
                    etReview.setText("");
                    alertReview.show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.msg_please_login), Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadReviewsFromNW();
    }

    private void publishReviewToNW(String star, String review) {
        try {
          //  AppUtils.showViews(progressBar);
           afieatGifLoaderDialog();
            Map<String, String> params = new HashMap<>();
            params.put("resid", mRestaurantId);
            params.put("user_id", AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
            params.put("ratecount", star);
            params.put("review", review);
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.POST_REVIEW, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                         //   AppUtils.hideViews(progressBar);
                           if(afieatGifLoaderDialog!=null)
                           {
                               afieatGifLoaderDialog.dismiss();
                           }
                            AppUtils.log(response);
                            loadReviewsFromNW();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                           // AppUtils.hideViews(progressBar);
                            if(afieatGifLoaderDialog!=null)
                            {
                                afieatGifLoaderDialog.dismiss();
                            }
                            error.printStackTrace();
                        }
                    }
            );
            AppInstance.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);

        } catch (Exception e) {
            e.printStackTrace();
           if(afieatGifLoaderDialog!=null)
           {
               afieatGifLoaderDialog.dismiss();
           }
           // AppUtils.hideViews(progressBar);
        }
    }

    private void loadReviewsFromNW() {
        final String[] mReviewBody = new String[1];
        try {
          //  AppUtils.showViews(progressBar, rlMyReview);
            afieatGifLoaderDialog();
            reviews.clear();
            tvNoReviews.setVisibility(View.GONE);
            Map<String, String> params = new HashMap<>();
            params.put("resid", mRestaurantId);
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_RESTAURANT_REVIEW, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                           // AppUtils.hideViews(progressBar);
                            if(afieatGifLoaderDialog!=null)
                            {
                                afieatGifLoaderDialog.dismiss();
                            }
                            AppUtils.log(response);
                            System.out.println("RestaurantReviewFragment : loadReviewsFromNW : response :"+response);
                            JSONArray reviewsArray = response.optJSONArray("review");
                            JSONObject totalReviewObject = response.optJSONObject("totalreview");
                            for (int i = 0; i < reviewsArray.length(); i++) {
                                JSONObject reviewObject = reviewsArray.optJSONObject(i);
                                Review review = new Review();
                                review.setReviewId(reviewObject.optString("id"));
                                review.setClientId(reviewObject.optString("client_id"));
                                review.setReviewDate(reviewObject.optString("date_created"));
                                review.setReviewBody(reviewObject.optString("review"));
                                review.setRating(reviewObject.optString("rating"));
                                review.setClientName(reviewObject.optString("client_name"));
                                review.setImageName(reviewObject.optString("reviewimages"));
                                if (reviewObject.optString("client_id").equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID))) {
                                    rlMyReview.setVisibility(View.GONE);
                                }
                                reviews.add(review);
                            }
                            lvReviews.setAdapter(new RestaurantReviewAdapter(getActivity(), reviews, AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID),
                                            new ReviewsAdapter.OnReviewAlterListener() {
                                                @Override
                                                public void onReviewEdit(Review review) {
                                                    mReviewBody[0] = review.getReviewBody();
                                                    mReviewRating = review.getRating();
                                                    rating.setRating(Float.parseFloat(mReviewRating));
                                                    etReview.setText(mReviewBody[0]);
                                                    alertReview.show();
                                                }

                                                @Override
                                                public void onReviewDelete(String reviewId) {
                                                    deleteReviewFromNW(reviewId);
                                                }
                                            }
                                    )
                            );
                            if (totalReviewObject != null) {


                                if (totalReviewObject.optString("avgrating") != null || !totalReviewObject.optString("avgrating").isEmpty()) {
                                    System.out.println("Rahul : restaurant.getRating :  " + totalReviewObject.optString("avgrating"));

                                    if (Double.parseDouble(totalReviewObject.optString("avgrating")) >= 4.5) {
                                        tvReviewCount.setBackgroundResource(R.drawable.rating_above_4_5);
                                    } else if (Double.parseDouble(totalReviewObject.optString("avgrating")) >= 4) {
                                        tvReviewCount.setBackgroundResource(R.drawable.rating_above_4);

                                    } else if (Double.parseDouble(totalReviewObject.optString("avgrating")) >= 3.5) {
                                        tvReviewCount.setBackgroundResource(R.drawable.rating_above_3_5);

                                    } else if (Double.parseDouble(totalReviewObject.optString("avgrating")) >= 3) {
                                        tvReviewCount.setBackgroundResource(R.drawable.rating_above_3);

                                    } else if (Double.parseDouble(totalReviewObject.optString("avgrating")) >= 2.5) {
                                        tvReviewCount.setBackgroundResource(R.drawable.rating_above_2_5);

                                    } else if (Double.parseDouble(totalReviewObject.optString("avgrating")) < 2.5) {

                                        tvReviewCount.setBackgroundResource(R.drawable.rating_above_2_5);
                                        tvReviewCount.setVisibility(View.GONE);

                                    }
                                    tvReviewCount.setPadding(35, 35, 35, 35);
                                    tvReviewCount.setText(AppUtils.changeToArabic(totalReviewObject.optString("avgrating"), getActivity().getApplicationContext()));

                                }

                                if ("ar".equals(AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                                    basedontotal.setText("مرتكز على"+totalReviewObject.optString("totrating")+" استعراض");
                                }else
                                {
                                    basedontotal.setText("Based on "+totalReviewObject.optString("totrating")+" reviews");
                                }


                                ratingAvg.setRating(Float.parseFloat(totalReviewObject.optString("avgrating")));
                                tvRating.setText(AppUtils.changeToArabic(totalReviewObject.optString("avgrating"), getActivity().getApplicationContext(), true));
                            }
                            if (reviews.size() == 0) {
                                tvNoReviews.setVisibility(View.VISIBLE);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                         //   AppUtils.hideViews(progressBar);
                           if(afieatGifLoaderDialog!=null)
                           {
                               afieatGifLoaderDialog.dismiss();
                           }
                            error.printStackTrace();
                        }
                    }
            );
            AppInstance.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
        } catch (Exception e) {
            e.printStackTrace();

            if(afieatGifLoaderDialog!=null)
            {
                afieatGifLoaderDialog.dismiss();
            }
            //AppUtils.hideViews(progressBar);
        }

    }

    private void deleteReviewFromNW(String reviewId) {
        try {
            //AppUtils.showViews(progressBar);
            afieatGifLoaderDialog();
            Map<String, String> params = new HashMap<>();
            params.put("rvwid", reviewId);
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.DELETE_REVIEW, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                          //  AppUtils.hideViews(progressBar);
                            if(afieatGifLoaderDialog!=null)
                            {
                                afieatGifLoaderDialog.dismiss();
                            }
                            AppUtils.log(response);
                            loadReviewsFromNW();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(afieatGifLoaderDialog!=null)
                            {
                                afieatGifLoaderDialog.dismiss();
                            }
                            //AppUtils.hideViews(progressBar);
                            error.printStackTrace();
                        }
                    }
            );
            AppInstance.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
        } catch (Exception e) {
            e.printStackTrace();
            if(afieatGifLoaderDialog!=null)
            {
                afieatGifLoaderDialog.dismiss();
            }
            //AppUtils.hideViews(progressBar);
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            loadReviewsFromNW();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Rahul : RestaurantsDetailActivity : RestaurantReviewFragment : onStop");
        // mRestaurantsDetailActivity.showViewR();
        // ((RestaurantsDetailActivity) getActivity()).showViewR();
        // getActivity().getFragmentManager().popBackStack();
        AppUtils.IS_OPEN = false;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Rahul : RestaurantsDetailActivity : RestaurantReviewFragment : onDestroy");

    }


    private void afieatGifLoaderDialog()
    {
        afieatGifLoaderDialog=new Dialog(getActivity());
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }
}
