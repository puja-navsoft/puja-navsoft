package com.afieat.ini.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afieat.ini.FullScreenImageDisplay;
import com.afieat.ini.R;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantInfoFragment extends Fragment {

    private View mView;
    private ScrollView svInfo;
    private ProgressBar progressBar;
    private LinearLayout llPhotos,
            llHighlights,
            llRecDishes,
            llOpeningTime;
    private LinearLayout llGallery;
    private TextView tvPhotos,
            tvHighlights,
            tvRecDishes;

    private String mRestaurantId;

 /*   public RestaurantInfoFragment() {
        // Required empty public constructor
    }
*/
    public static RestaurantInfoFragment newInstance(String resId) {
        RestaurantInfoFragment fragment = new RestaurantInfoFragment();
        fragment.mRestaurantId = resId;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        svInfo = (ScrollView) view.findViewById(R.id.svInfo);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        llPhotos = (LinearLayout) view.findViewById(R.id.llPhotos);
        llHighlights = (LinearLayout) view.findViewById(R.id.llHighlights);
        llRecDishes = (LinearLayout) view.findViewById(R.id.llRecDishes);
        llOpeningTime = (LinearLayout) view.findViewById(R.id.llOpeningTime);

        LinearLayout llPayMethods;
        llPayMethods = (LinearLayout) view.findViewById(R.id.llPayMethods);
        mView = view;

        AppUtils.showViews(progressBar);
        AppUtils.hideViews(svInfo, llPhotos, llHighlights, llRecDishes, llPayMethods);

        loadRestaurantInfoFromNW();
    }

    private void loadRestaurantInfoFromNW() {
        Map<String, String> params = new HashMap<>();
        params.put("resid", mRestaurantId);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_RESTAURANT_INFO, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Rahul : RestaurantInfoFragment : response : " + response);
                        AppUtils.hideViews(progressBar);
                        JSONArray photosArray = response.optJSONArray("photo_gallery");
                        String hiText = response.optString("highlight");
                        JSONArray recItemsArray = response.optJSONArray("reco_item_name");
                        JSONArray openingTimesArray = response.optJSONArray("restaurant_openning");
                        if (photosArray != null)
                            showGalleryItems(photosArray);
                        if (hiText.length() > 0)
                            showHighlights(hiText);
                        if (recItemsArray != null)
                            showRecommendedItems(recItemsArray);
                        if (openingTimesArray != null)
                            showOpeningTimes(openingTimesArray);
                        AppUtils.showViews(svInfo);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.page), getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadRestaurantInfoFromNW();
                            }
                        });
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
                    }
                }
        );
        AppInstance.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
    }

    private void showGalleryItems(final JSONArray photosArray) {
        if (photosArray.length() > 0) {
            final int MAX_PICS = 5;
            llGallery = (LinearLayout) mView.findViewById(R.id.llGallery);
            AppUtils.showViews(llPhotos);
            tvPhotos = (TextView) mView.findViewById(R.id.tvPhotos);
            tvPhotos.setText(tvPhotos.getText() + " (" + AppUtils.changeToArabic(String.valueOf(photosArray.length()), getActivity().getApplicationContext()) + ")");
            final List<String> photoUris = new ArrayList<>();
            int i = 0;
            while (i < photosArray.length()) {
                View view = null;
                String url = Apis.IMG_PATH + "restaurants/gallery/thumb_88_88/" + photosArray.optString(i);
                photoUris.add(url);
                if (i >= MAX_PICS) {
                    i++;
                    continue;
                }
                if (i < MAX_PICS - 1 || (i + 1) == photosArray.length()) {
                    view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_simple_thumb, null);
                } else {
                    view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_simple_thumb_plus, null);
                    ((TextView) view.findViewById(R.id.tvMore)).setText(AppUtils.changeToArabic(String.valueOf(photosArray.length() - (i /*+ 1*/)), getActivity().getApplicationContext()));
                }
                ImageView photoView = (ImageView) view.findViewById(R.id.ivFoodImage);
                //          photoView.setImageURI(Uri.parse(url));

                Glide
                        .with(getContext())
                        .load(Uri.parse(url))
                        .placeholder(R.drawable.placeholder_land)
                        .into(photoView);
                view.setTag(i);
                llGallery.addView(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Fragment fragment = PhotoGalleryFragment.newInstance(photoUris, R.id.flPhotoContainer);
//                        getActivity().getSupportFragmentManager()
//                                .beginTransaction()
//                                .add(R.id.flPhotoContainer, fragment)
//                                .addToBackStack(null)
//                                .commit();


                        String[] images = new String[photosArray.length()];
                        for (int i = 0; i < photosArray.length(); i++) {
                            images[i] = photosArray.optString(i);
                        }

                        startActivity(new Intent(getActivity(), FullScreenImageDisplay.class)
                                .putExtra("ResName", "")
                                .putExtra("Images", images).putExtra("FromPage", "REST_INFO")
                                .putExtra("SELECTITEM", Integer.parseInt("" + v.getTag())));
                    }
                });
                i++;
            }
        }
    }

    private void showHighlights(String hiText) {
        if (!"null".equals(hiText)) {
            AppUtils.showViews(llHighlights);
            tvHighlights = (TextView) mView.findViewById(R.id.tvHighlights);
            tvHighlights.setText(Html.fromHtml(hiText));
        }
    }

    private void showRecommendedItems(JSONArray recItemsArray) {
        /*AppUtils.showViews(llRecDishes);
        int i = 0;
        while (i < recItemsArray.length()) {
            TextView textView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_simple_textview, null);
            textView.setText(recItemsArray.optString(i));
            llRecDishes.addView(textView);
            i++;
        }*/
    }

    private void showOpeningTimes(JSONArray openingTimesArray) {
        for (int i = 0; i < openingTimesArray.length(); i++) {
            JSONObject timeObject = openingTimesArray.optJSONObject(i);
            View timeView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_restaurant_info_opening_time_row, null);
            ((TextView) timeView.findViewById(R.id.tvDay)).setText(timeObject.optString("day_name"));
            ((TextView) timeView.findViewById(R.id.tvOpeningTime)).setText(timeObject.optString("open_at"));
            ((TextView) timeView.findViewById(R.id.tvClosingTime)).setText(timeObject.optString("close_at"));
            if ("ar".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                String dayName_ar = "";
                switch (i) {
                    case 0:
                        dayName_ar = "الإثنين";
                        break;

                    case 1:
                        dayName_ar = "الثلاثاء";
                        break;

                    case 2:
                        dayName_ar = "الأربعاء";
                        break;

                    case 3:
                        dayName_ar = "الخميس";
                        break;

                    case 4:
                        dayName_ar = "الجمعة";
                        break;

                    case 5:
                        dayName_ar = "يوم السبت";
                        break;

                    case 6:
                        dayName_ar = "الأحد";
                        break;default:
                        break;
                }
                ((TextView) timeView.findViewById(R.id.tvDay)).setText(dayName_ar);
            }
            llOpeningTime.addView(timeView);
        }
    }
}
