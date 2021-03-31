package com.afieat.ini.fragments;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afieat.ini.R;
import com.afieat.ini.adapters.ItemDecorationAlbumColumns;
import com.afieat.ini.adapters.MyAdapter;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
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
public class RestaurantGalleryFragment extends Fragment {

    /*  private View mView;
      private ScrollView svInfo;
      private ProgressBar progressBar;
      private LinearLayout llPhotos, llHighlights, llRecDishes, llPayMethods, llOpeningTime;
      private LinearLayout llGallery;
      private TextView tvPhotos, tvHighlights, tvRecDishes;*/
    private Dialog afieatGifLoaderDialog;
    private String mRestaurantName;
    // private ProgressBar progressBar;
    private String mRestaurantId;
    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private List<String> photoUrisFinal = new ArrayList<>();

   /* public RestaurantGalleryFragment() {
        // Required empty public constructor
    }*/

    public static RestaurantGalleryFragment newInstance(String resId, String mRestaurantName) {
        RestaurantGalleryFragment fragment = new RestaurantGalleryFragment();
        fragment.mRestaurantId = resId;
        fragment.mRestaurantName = mRestaurantName;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resaurant_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
     /*   svInfo = (ScrollView) view.findViewById(R.id.svInfo);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        llPhotos = (LinearLayout) view.findViewById(R.id.llPhotos);
        llHighlights = (LinearLayout) view.findViewById(R.id.llHighlights);
        llRecDishes = (LinearLayout) view.findViewById(R.id.llRecDishes);
        llOpeningTime = (LinearLayout) view.findViewById(R.id.llOpeningTime);
        llPayMethods = (LinearLayout) view.findViewById(R.id.llPayMethods);
        mView = view;
*/
        //  AppUtils.showViews(progressBar);
        /*   AppUtils.hideViews(svInfo, llPhotos, llHighlights, llRecDishes, llPayMethods);
         */
        //progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) view.findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.addItemDecoration(new ItemDecorationAlbumColumns(10, 3));
        recyclerView.setLayoutManager(layoutManager);

        //   AppUtils.showViews(progressBar);
        afieatGifLoaderDialog();
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
                        //   AppUtils.hideViews(progressBar);
                        if (afieatGifLoaderDialog != null) {
                            afieatGifLoaderDialog.dismiss();
                        }
                        JSONArray photosArray = response.optJSONArray("photo_gallery");

                        if (photosArray != null)
                            showGalleryItems(photosArray);


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
                        if (afieatGifLoaderDialog != null) {
                            afieatGifLoaderDialog.dismiss();
                        }
                    }
                }
        );
        AppInstance.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
    }

    private void showGalleryItems(final JSONArray photosArray) {


        if (photosArray.length() > 0) {
            final int MAX_PICS = 5;
          /*  llGallery = (LinearLayout) mView.findViewById(R.id.llGallery);
            AppUtils.showViews(llPhotos);
            tvPhotos = (TextView) mView.findViewById(R.id.tvPhotos);
            tvPhotos.setText(tvPhotos.getText() + " (" + AppUtils.changeToArabic(String.valueOf(photosArray.length()), getActivity().getApplicationContext()) + ")");*/
            final List<String> photoUris = new ArrayList<>();
            int i = 0;
            while (i < photosArray.length()) {
                View view = null;
                String url = Apis.IMG_PATH + "restaurants/gallery/thumb_115_115/" + photosArray.optString(i);
                System.out.println("Rahul : galleryList.get(i) : " + url);

                photoUris.add(url);
                //   view.setTag(i);


               /* if (i >= MAX_PICS) {
                    i++;
                    continue;
                }
                if (i < MAX_PICS - 1 || (i + 1) == photosArray.length()) {
                    view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_simple_thumb, null);
                } else {
                    view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_simple_thumb_plus, null);
                    ((TextView) view.findViewById(R.id.tvMore)).setText(AppUtils.changeToArabic(String.valueOf(photosArray.length() - (i *//*+ 1*//*)), getActivity().getApplicationContext()));
                }
                ImageView photoView = (ImageView) view.findViewById(R.id.ivFoodImage);
      //          photoView.setImageURI(Uri.parse(url));

                Glide
                        .with(getContext())
                        .load(Uri.parse(url))
                        .placeholder(R.drawable.placeholder_land)
                        .into(photoView);
                view.setTag(i);*/
                //llGallery.addView(view);
         /*       view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Fragment fragment = PhotoGalleryFragment.newInstance(photoUris, R.id.flPhotoContainer);
//                        getActivity().getSupportFragmentManager()
//                                .beginTransaction()
//                                .add(R.id.flPhotoContainer, fragment)
//                                .addToBackStack(null)
//                                .commit();


                        String[]images = new String[photosArray.length()];
                        for(int i=0;i<photosArray.length();i++)
                        {
                            images[i]=photosArray.optString(i);
                        }

                        startActivity(new Intent(getActivity(), FullScreenImageDisplay.class)
                                .putExtra("Images",images).putExtra("FromPage","REST_INFO")
                                .putExtra("SELECTITEM",Integer.parseInt(""+ v.getTag())));
                    }
                });
                i++;*/
                i++;
            }
            photoUrisFinal = photoUris;
            adapter = new MyAdapter(getContext(), photoUrisFinal, photosArray, mRestaurantName);
            recyclerView.setAdapter(adapter);

            //  adapter.notifyDataSetChanged();
        }
    }


    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(getActivity());
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }

}
