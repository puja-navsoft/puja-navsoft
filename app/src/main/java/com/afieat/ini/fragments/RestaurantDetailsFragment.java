package com.afieat.ini.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.DetailActivityClick_Page;
import com.afieat.ini.R;
import com.afieat.ini.RestaurantsDetailActivity;
import com.afieat.ini.models.Restaurant;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.AuthkeyValidator;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantDetailsFragment extends Fragment {

    private Context mContext;
    private String mRestaurantId;
    private Dialog afieatGifLoaderDialog;
    private TextView tv_add_favorite;
    private String[] items = {"Selection of food and demand", "Recommended dishes", "Reviews & ratings", "Info"};
    private String[] items_ar = {"اختيار الأغذية والطلب", "أطباق الموصى بها", "الآراء و تصنيفات", "معلومات"};


    private FragmentManager fragmentManager;
    private View view;
    private String mRestaurantName;
    private LinearLayout llOpeningTime;

    static String fromD;
    private Context context;

    public RestaurantDetailsFragment() {
        // Required empty public constructor
    }

    public static RestaurantDetailsFragment newInstance(String restaurantId, String from, Context context) {
        RestaurantDetailsFragment fragment = new RestaurantDetailsFragment();
        fragment.mRestaurantId = restaurantId;
        fragment.context = context;
        fromD = from;
        System.out.println("Rahul : RestaurantDetailsFragment : called  ");
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_restaurant_details, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);/*
        lvRestaurantItems = (ListView) view.findViewById(R.id.lvRestaurantItems);
        if (AppInstance.getInstance(getActivity().getContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar"))
            items = items_ar;
        lvRestaurantItems.setAdapter(new ArrayAdapter<>(mContext, R.layout.layout_simple_list_item, items));
        lvRestaurantItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment targetFragment;
                switch (position) {
                    case 0:
                        targetFragment = RestaurantOrderItemsFragment.newInstance(mRestaurantId);
                        emigrateTo(targetFragment);
                        break;
                    case 3:
                        targetFragment = RestaurantInfoFragment.newInstance(mRestaurantId);
                        emigrateTo(targetFragment);
                        break;
                    case 1:
                        targetFragment = RestaurantPopularProductsFragment.newInstance(mRestaurantId);
                        emigrateTo(targetFragment);
                        break;
                    case 2:
                        targetFragment = RestaurantReviewFragment.newInstance(mRestaurantId);
                        emigrateTo(targetFragment);
                        break;
                }
            }

            private void emigrateTo(Fragment targetFragment) {
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.entry_in, R.anim.entry_out, R.anim.exit_in, R.anim.exit_out)
                        .add(R.id.flContainer, targetFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });*/

        loadRestaurantData();
        System.out.println("Rahul NavigationBar Height : " + getNavigationBarHeight());
        getNavigationBarHeight();

        if (AppUtils.hasSoftKeys(getActivity())) {

            view.findViewById(R.id.detailFragmentRel).setPadding(0, 0, 0, getNavigationBarHeight());
        }
       /* ViewGroup.LayoutParams params = view.findViewById(R.id.viewBottom).getLayoutParams();
// Changes the height and width to the specified *pixels*
        params.height = getNavigationBarHeight() + view.findViewById(R.id.viewBottom).getLayoutParams().height;

        view.findViewById(R.id.viewBottom).setLayoutParams(params);*/

    }

    private int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private void loadRestaurantData() {
        // final ProgressDialog dialog = AppUtils.showProgress(getActivity(), "", getString(R.string.msg_please_wait));
        afieatGifLoaderDialog();
        Map<String, String> paramsLo = new HashMap<>();
        paramsLo.put("shopuserid", AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        paramsLo.put("restaurant_id", mRestaurantId);
        paramsLo.put("region_id", AppUtils.REGION_ID);
        System.out.println("region_id jhjgvj : " + paramsLo);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.RESTAURANT_DETAILS, paramsLo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println("Rahul : RestaurantsDetailActivity : loadRestaurantData : response : " + response.toString());
                        //AppUtils.hideProgress(dialog);
                        if (afieatGifLoaderDialog != null) {
                            afieatGifLoaderDialog.dismiss();
                        }
                        if (response.has("status") && response.optInt("status") == 111) {
                            new AuthkeyValidator(getContext()).CallForReAuth(new AuthkeyValidator.Authkey() {
                                @Override
                                public void Oncomplete() {
                                    loadRestaurantData();
                                }
                            });
                        } else {
                            if ("0".equalsIgnoreCase(response.optString("status"))) {
                                AppUtils.hideViews(((RestaurantsDetailActivity) getActivity()).findViewById(R.id.app_bar));
                                AppUtils.hideViews(((RestaurantsDetailActivity) getActivity()).findViewById(R.id.viewOnlineOrderMenu));
                                AppUtils.hideViews(((RestaurantsDetailActivity) getActivity()).findViewById(R.id.include_resturant_details_content_scrolling));
//                                AppUtils.showViews(((RestaurantsDetailActivity)getActivity()).findViewById(R.id.noDetailMsg));
                            } else {

                                Restaurant restaurant = new Restaurant();

                                /* try {*/
                                AppUtils.log("check response" + response.toString());
                                System.out.println("Rahul : RestaurantsDetailActivity : loadRestaurantData : response : " + response.toString());
                                JSONObject restaurantDetails = response.optJSONObject("restaurant_details");


//                            AppUtils.log("check restaurantDetails" + "" + restaurantDetails.toString());
                                restaurant.setName(restaurantDetails.optString("restaurant_name"));
                                restaurant.setName_ar(restaurantDetails.optString("restaurant_name_ar"));
                                mRestaurantName = restaurantDetails.optString("restaurant_name");
                                restaurant.setAddress(restaurantDetails.optString("search_address"));
                                restaurant.setId(restaurantDetails.optString("merchant_id"));
                                restaurant.setReviewCount(restaurantDetails.optString("review_count"));
                                //restaurant.setUriThumb(Apis.IMG_PATH + "restaurants/image/thumb_81_81/" + restaurantDetails.optString("merchant_photo_bg"));
                                restaurant.setUriThumb(Apis.IMG_PATH + "restaurants/image/thumb_300_300/" + restaurantDetails.optString("merchant_photo_bg"));
                                AppUtils.log(restaurant.getUriThumb());
//                        restaurant.setUriBg(Apis.IMG_PATH + restaurantDetails.optString("merchant_photo_bg"));
                                // Sunit 25-01-2017
                                restaurant.setOpeningTime(restaurantDetails.optString("openning_time"));
                                restaurant.setClosingTime(restaurantDetails.optString("closing_time"));
                                restaurant.setRating(restaurantDetails.optString("present_rating"));
                                restaurant.setRatingCount(restaurantDetails.optString("review_count"));
//                        restaurant.setClosingTime(restaurantDetails.optString("today_closing_time_pm"));
                                restaurant.setStatus(restaurantDetails.optString("status"));
                                restaurant.setDeliveryTime(restaurantDetails.optString("delivery_time"));
                                restaurant.setDeliveryCharge(restaurantDetails.optString("delivery_charge"));
                                restaurant.setDiscount(restaurantDetails.optString("restaurant_discount"));

                                System.out.println("hgfhf : " + restaurantDetails.optString("restaurant_discount"));
                                restaurant.setProcessingTime(restaurantDetails.optString("processing_time"));
                                restaurant.setMinOrder(restaurantDetails.optString("merchant_minimum_order"));
                                restaurant.setCuisine(restaurantDetails.optString("cuisine"));
                                restaurant.setGalleryCount(restaurantDetails.optString("merchant_gallery_count"));


                                // Anurup 27-02-17
                                //       Log.i("Anurp---orderno","   "+restaurantDetails.optString("no_of_order"));
                                restaurant.setOrderNumber(restaurantDetails.optString("no_of_order"));
                                SharedPreferences sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(AppUtils.PREF_ORDER_NO, restaurantDetails.optString("no_of_order"));
                                editor.commit();
                                if ("ar".equals(AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                                    restaurant.setName(restaurantDetails.optString("restaurant_name_ar"));
                                    restaurant.setCuisine(restaurantDetails.optString("cuisine_ar"));
                                }

                                // Restaurant data displaying
                                displayRestaurantData(restaurant);
                           /* } catch (NullPointerException e) {
                                System.out.println("RestaurantDetailsFragment : err : "+e.getMessage());
                                Toast.makeText(getContext(), getString(R.string.msg_operation_not_completed), Toast.LENGTH_SHORT).show();
                                getActivity().findViewById(R.id.page).setVisibility(View.INVISIBLE);
                                getActivity().finish();
                                *//*Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.page), getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loadRestaurantInfoFromNW();
                                    }
                                });
                                snackbar.setActionTextColor(Color.RED);
                                snackbar.show();*//*
                            }*/
                            }

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (afieatGifLoaderDialog != null) {
                            afieatGifLoaderDialog.dismiss();
                        }
                        //AppUtils.hideProgress(dialog);
                    }
                }
        );
        AppInstance.getInstance(getContext()).addToRequestQueue(request);
    }

    private void displayRestaurantData(final Restaurant restaurant) {
        System.out.println("displayRestaurantData : delCharge : " + restaurant.getDeliveryCharge());
        /*final ImageView ivRestaurantLogo = (ImageView) cardRestaurant.findViewById(R.id.ivRestaurantLogo);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //    ivRestaurantLogo.setImageURI(Uri.parse(restaurant.getUriThumb()));
                Glide
                        .with(getContext())
                        .load(Uri.parse(restaurant.getUriThumb()))
                        .placeholder(R.drawable.placeholder_land)
                        .into(ivRestaurantLogo);
            }
        });

        TextView tvRestTitle = (TextView) cardRestaurant.findViewById(R.id.tvRestTitle);
        TextView tvRestCuisine = (TextView) cardRestaurant.findViewById(R.id.tvRestCuisine);
        TextView tvRestStatus = (TextView) cardRestaurant.findViewById(R.id.tvRestStatus);
        TextView tvRestOpeningHours = (TextView) cardRestaurant.findViewById(R.id.tvRestOpeningHours);
        TextView tvMinOrder = (TextView) cardRestaurant.findViewById(R.id.tvMinOrder);
        TextView tvDeliveryTime = (TextView) cardRestaurant.findViewById(R.id.tvDeliveryTime);
        TextView tvOrderNow = (TextView) cardRestaurant.findViewById(R.id.tvOrderNow);
        RatingBar ratingbar = (RatingBar) cardRestaurant.findViewById(R.id.ratingbar);
        TextView tvRating = (TextView) cardRestaurant.findViewById(R.id.tvRating);

*/
     /*   TextView tv_num_of_pic = view.findViewById(R.id.num_of_pic);
        TextView tv_num_of_reviews = view.findViewById(R.id.num_of_reviews);
*/

       /* if (((RestaurantsDetailActivity) getActivity()).from != null) {
            if (((RestaurantsDetailActivity) getActivity()).from.equalsIgnoreCase("search")) {

                System.out.println("getUriBg : " + restaurant.getUriBg());
                ImageView imageView = ((RestaurantsDetailActivity) getActivity()).findViewById(R.id.bannerImage);
                Picasso.with(getActivity())
                        .load(restaurant.getUriThumb())
                        .noFade()
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                            }
                        });
            }

        }*/
        System.out.println("getUriBg : " + restaurant.getUriBg());
        ImageView imageView = ((RestaurantsDetailActivity) getActivity()).bannerImage;
        //if (context)
        if (getActivity() instanceof RestaurantsDetailActivity) {
            Picasso.with((RestaurantsDetailActivity) getActivity())
                    .load(restaurant.getUriThumb())
                    .noFade()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        TextView tv_add_reviews, tv_add_photos;
        tv_add_favorite = view.findViewById(R.id.add_favorite);
        tv_add_reviews = view.findViewById(R.id.add_review);
        tv_add_photos = view.findViewById(R.id.add_photo);

        llOpeningTime = (LinearLayout) view.findViewById(R.id.llOpeningTime);

        tv_add_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* if (!AppUtils.IS_OPEN) {
                    Fragment targetFragment = RestaurantReviewFragment.newInstance(mRestaurantId);
                    emigrateTo(targetFragment);
                }*/
                Intent ii = new Intent(getActivity(), DetailActivityClick_Page.class);
                ii.putExtra("res_id", mRestaurantId);
                ii.putExtra("mRestaurantName", mRestaurantName);
                ii.putExtra("page_to_call", "2");
                startActivity(ii);
            }
        });

        tv_add_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ii = new Intent(getActivity(), DetailActivityClick_Page.class);
                ii.putExtra("res_id", mRestaurantId);
                ii.putExtra("mRestaurantName", mRestaurantName);
                ii.putExtra("page_to_call", "1");
                startActivity(ii);
            }
        });


        tv_add_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_ID).length() > 0) {
                    addFavRestaurant(mRestaurantId);

                } else {
                    Toast.makeText(getContext(), "Please Log In", Toast.LENGTH_LONG).show();
                }

            }
        });

        TextView tvRestaurantName = view.findViewById(R.id.restaurantName);
        TextView tvRestaurantLocation = view.findViewById(R.id.restaurantLocation);
        TextView tvRestaurantSpeciality = view.findViewById(R.id.restaurantSpeciality);
        TextView tvRestaurantOpeningStatus = view.findViewById(R.id.restaurantOpeningStatus);
        TextView tvRestaurantReviews = view.findViewById(R.id.restaurantReviews);
        TextView tvRestaurantdiscountoff = view.findViewById(R.id.discountoff);
        TextView tvRestaurantdiscountoffText = view.findViewById(R.id.discountoffText);



      /*  tv_num_of_reviews.setText(restaurant.getReviewCount() + " Reviews");

        tv_num_of_pic.setText(restaurant.getGalleryCount()+" Photos");
*/
        TextView tvRestaurantMinimumOrder = view.findViewById(R.id.restaurantMinimumOrder);
        TextView tvResProcessingTime = view.findViewById(R.id.resProcessingTime);
        TextView tvResDeliveryTime = view.findViewById(R.id.resDeliveryTime);
        TextView tvRestaurantDeliveryCharge = view.findViewById(R.id.restaurantDeliveryCharge);

        if (restaurant.getDiscount() != null || !"".equals(restaurant.getDiscount().trim()) || Integer.parseInt(restaurant.getDiscount()) != 0) {
            if ("0".equalsIgnoreCase(restaurant.getDiscount())) {
                view.findViewById(R.id.relativeLay1).setVisibility(View.GONE);

            } else {
                tvRestaurantdiscountoff.setText(restaurant.getDiscount());
                if ("ar".equals(AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    tvRestaurantdiscountoffText.setText(restaurant.getDiscount() + " على كل طلبك ");

                } else {
                    tvRestaurantdiscountoffText.setText(restaurant.getDiscount() + " off on all your order");
                }
            }
        } else {
            view.findViewById(R.id.relativeLay1).setVisibility(View.GONE);
        }

        //  view.findViewById(R.id.relativeLay1).setVisibility(View.GONE);
        if ("ar".equals(AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

            String str = "";
            ((RestaurantsDetailActivity) getActivity()).MinimumPrice = restaurant.getMinOrder();

            str = AppUtils.changeToArabic(restaurant.getMinOrder().toString(), context, true);
            tvRestaurantMinimumOrder.setText("IQD " + str);
            str = AppUtils.changeToArabic(restaurant.getProcessingTime().toString(), context, true);
            tvResProcessingTime.setText("" + str + "دقيقة");
            str = AppUtils.changeToArabic(restaurant.getDeliveryTime().toString(), context, true);
            tvResDeliveryTime.setText("" + str + "دقيقة");

        } else {
            ((RestaurantsDetailActivity) getActivity()).MinimumPrice = restaurant.getMinOrder();

            tvRestaurantMinimumOrder.setText("IQD " + restaurant.getMinOrder());
            tvResProcessingTime.setText("" + restaurant.getProcessingTime() + "mins");
            tvResDeliveryTime.setText("" + restaurant.getDeliveryTime() + "mins");
        }
        // tvRestaurantReviews.setText(restaurant.get);

        //  TextView ratingSingle = view.findViewById(R.id.ratingSingle);


        tvRestaurantName.setText(restaurant.getName());
        tvRestaurantSpeciality.setText(restaurant.getCuisine().trim());
        tvRestaurantLocation.setText(restaurant.getAddress());

        /*if (restaurant.getStatus().equals("Open")) {
            tvRestStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.greenButton));
        } else if (restaurant.getStatus().equals("Closed")) {
            tvRestStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } else {
            tvRestStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.orangeButton));
        }*/
        System.out.println("Rahul : RestaurantsDetailActivity : getStatus : " + restaurant.getStatus());
        if (restaurant.getStatus().equals("Open")) {
            if ("ar".equals(AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                //  tvRestStatus.setText("فتح");
                tvRestaurantOpeningStatus.setText("مفتوح الان");
                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
              /*  String status =
                        "<span style = 'color: # 00B378؛'>  مفتوح الان </ span>" + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")";
                tvRestaurantOpeningStatus.setText(status);*/
                //   tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#00B378;'><b>مفتوح الان -</b></span>" + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")"));
                tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#00B378;'><b>مفتوح الان </b></span>"));


            } else {
                //tvRestStatus.setText("Open");
                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
//                tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#00B378;'><b>Open now - </b></span>" + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")"));
                tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#00B378;'><b>Open now </b></span>"));

            }
            //tvRestStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.greenButton));
            //  tvRestaurantOpeningStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.greenButton));
        } else if (restaurant.getStatus().equals("Close")) {
            //tvRestStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.orangeButton));
            if ("ar".equals(AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                //  tvRestStatus.setText("مغلق");

                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                //tvRestaurantOpeningStatus.setText("مغلق الان - " + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")");
                // tvRestaurantOpeningStatus.setText("مغلق الان  " );
                tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#C63A2B;'><b>مغلق الان </b></span>"));


            } else {
                //tvRestStatus.setText("Close");
                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                //   tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#C63A2B;'>Closed now - </span>" + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")"));
                tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#C63A2B;'><b>Closed now </b></span>"));

            }
        } else if (restaurant.getStatus().equals("Closed")) {
            //  tvRestStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.orangeButton));
            if ("ar".equals(AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                //    tvRestStatus.setText("مغلق");
                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                //  tvRestaurantOpeningStatus.setText("مغلق الان : " + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")");
                //   tvRestaurantOpeningStatus.setText("مغلق الان ");
                tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#C63A2B;'><b>مغلق الان </b></span>"));


            } else {
                //  tvRestStatus.setText("Closed");
                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
//                tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#C63A2B;'>Closed now - </span>" + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")"));
                tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#C63A2B;'><b>Closed now  </b></span>"));

            }
        } else {
            //  tvRestStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.orangeButton));
            if ("ar".equals(AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                //tvRestStatus.setText("مشغول");
                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
//                tvRestaurantOpeningStatus.setText("مشغول الآن : " + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")");
                //tvRestaurantOpeningStatus.setText("مشغول الآن ");
                tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#C63A2B;'><b>مشغول الآن  </b></span>"));


            } else {
                //tvRestStatus.setText("Busy");
                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                //      tvRestaurantOpeningStatus.setText("Busy now: " + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")");
                // tvRestaurantOpeningStatus.setText("Busy now ");
                tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#C63A2B;'><b>Busy now </b></span>"));


            }
        }

        /*if (restaurant.getOpeningTime().trim().length() > 0 && restaurant.getClosingTime().length() > 0)
            tvRestOpeningHours.setText(restaurant.getOpeningTime() + " to " + restaurant.getClosingTime());
        else
            tvRestOpeningHours.setText("N/A");*/
        //ratingbar.setRating(Float.parseFloat(restaurant.getRating()));


        // RestaurantsDetailActivity Views Update------------------------------------------------

        if (restaurant.getRating() != null || !"".equals(restaurant.getRating().trim())) {
            System.out.println("Rahul : restaurant.getRating :  " + restaurant.getRating());

            if (Double.parseDouble(restaurant.getRating()) >= 4.5) {
                ((RestaurantsDetailActivity) getActivity()).ratingSingle.setBackgroundResource(R.drawable.rating_above_4_5);
            } else if (Double.parseDouble(restaurant.getRating()) >= 4) {
                ((RestaurantsDetailActivity) getActivity()).ratingSingle.setBackgroundResource(R.drawable.rating_above_4);

            } else if (Double.parseDouble(restaurant.getRating()) >= 3.5) {
                ((RestaurantsDetailActivity) getActivity()).ratingSingle.setBackgroundResource(R.drawable.rating_above_3_5);

            } else if (Double.parseDouble(restaurant.getRating()) >= 3) {
                ((RestaurantsDetailActivity) getActivity()).ratingSingle.setBackgroundResource(R.drawable.rating_above_3);

            } else if (Double.parseDouble(restaurant.getRating()) >= 2.5) {
                ((RestaurantsDetailActivity) getActivity()).ratingSingle.setBackgroundResource(R.drawable.rating_above_2_5);

            } else if (Double.parseDouble(restaurant.getRating()) < 2.5) {
                ((RestaurantsDetailActivity) getActivity()).ratingSingle.setBackgroundResource(R.drawable.rating_above_2_5);
                ((RestaurantsDetailActivity) getActivity()).ratingSingle.setVisibility(View.GONE);

            }
            ((RestaurantsDetailActivity) getActivity()).ratingSingle.setPadding(20, 8, 20, 8);
            ((RestaurantsDetailActivity) getActivity()).ratingSingle.setText(restaurant.getRating());
        }

        if ("ar".equals(AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

            ((RestaurantsDetailActivity) getActivity()).tv_num_of_pic.setText(restaurant.getGalleryCount() + " الصور");
            ((RestaurantsDetailActivity) getActivity()).tv_num_of_reviews.setText(restaurant.getReviewCount() + " التعليقات");

            tvRestaurantReviews.setText(restaurant.getReviewCount() + "  التعليقات");
            System.out.println("delCharge : " + restaurant.getDeliveryCharge());
            if (restaurant.getDeliveryCharge() != null) {
                if (Double.parseDouble(restaurant.getDeliveryCharge()) != 0) {
                    String str = AppUtils.changeToArabic(restaurant.getDeliveryCharge().toString(), context, true);
                    tvRestaurantDeliveryCharge.setText("IQD " + str + " رسوم التوصيل");
                } else if (".00".equalsIgnoreCase(restaurant.getDeliveryCharge())) {
                    tvRestaurantDeliveryCharge.setText(getString(R.string.free_delivery));
                }
            } else {
                tvRestaurantDeliveryCharge.setText(getString(R.string.free_delivery));
            }

        } else {
            if ("".equals(restaurant.getGalleryCount().toString().trim())) {
                ((RestaurantsDetailActivity) getActivity()).tv_num_of_pic.setVisibility(View.GONE);
                tv_add_photos.setVisibility(View.GONE);
            } else {
                if (Integer.parseInt(restaurant.getGalleryCount()) == 0) {
                    ((RestaurantsDetailActivity) getActivity()).tv_num_of_pic.setVisibility(View.GONE);
                    tv_add_photos.setVisibility(View.GONE);
                }
                {
                    ((RestaurantsDetailActivity) getActivity()).tv_num_of_pic.setText(restaurant.getGalleryCount() + " Photos");
                }

            }
            ((RestaurantsDetailActivity) getActivity()).tv_num_of_reviews.setText(restaurant.getReviewCount() + " Reviews");

            tvRestaurantReviews.setText(restaurant.getReviewCount() + " Reviews");


            if (restaurant.getDeliveryCharge() != null) {
                if (Double.parseDouble(restaurant.getDeliveryCharge()) != 0) {
                    tvRestaurantDeliveryCharge.setText("IQD " + restaurant.getDeliveryCharge() + " delivery charge");
                } else if (".00".equalsIgnoreCase(restaurant.getDeliveryCharge())) {
                    tvRestaurantDeliveryCharge.setText(getString(R.string.free_delivery));
                }
            } else {
                tvRestaurantDeliveryCharge.setText(getString(R.string.free_delivery));
            }

        }


        ((RestaurantsDetailActivity) getActivity()).mRestaurantName = restaurant.getName();


        //  ((RestaurantsDetailActivity) getActivity()).mToolbar.setTitle(restaurant.getName());


        //tvRating.setText("(" + AppUtils.changeToArabic(restaurant.getRatingCount(), getContext()) + ")");
        // Sunit 25-01-2017
        AppUtils.CURRENT_RESTAURANT_NAME_AR = restaurant.getName_ar();
        System.out.println("CURRENT : resname : " + restaurant.getName_ar());
        // tvRestOpeningHours.setText(restaurant.getOpeningTime() + " إلى " + restaurant.getClosingTime());
        AppUtils.CURRENT_RESTAURANT_NAME = restaurant.getName();
        AppUtils.CURRENT_RESTAURANT_IMAGE=restaurant.getUriThumb();

        /*AppInstance.getInstance(getActivity()).setCurrentResName(restaurant.getName());
        AppInstance.getInstance(getActivity()).setCurrentResNameAR(restaurant.getName_ar());
        AppInstance.getInstance(getActivity()).setCurrentResImage(restaurant.getUriThumb());*/

        System.out.println("CURRENT : resname : " + restaurant.getName());
        //tvRestOpeningHours.setText(restaurant.getOpeningTime() + " to " + restaurant.getClosingTime());


        //  tvMinOrder.setText(getString(R.string.currency) + AppUtils.changeToArabic(restaurant.getMinOrder(), getContext()));
        //MinimumPrice = restaurant.getMinOrder();
      /*  String delTime = restaurant.getDeliveryTime().trim().length() > 0
                ? AppUtils.changeToArabic(restaurant.getDeliveryTime().trim(), getContext()) + getString(R.string.min_delivery)
//                : "N/A";
                : "";*/

        String processingTime = restaurant.getProcessingTime().trim().length() > 0 ? AppUtils.changeToArabic(restaurant.getProcessingTime().trim(), getContext()) + (getString(R.string.min_procesing_2)) : "";

        //  tvDeliveryTime.setText(processingTime + ", " + delTime);

      /*  tvOrderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestaurantOrderItemsFragment targetFragment = RestaurantOrderItemsFragment.newInstance(mRestaurantId);
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.entry_in, R.anim.entry_out, R.anim.exit_in, R.anim.exit_out)
                        .add(R.id.flContainer, targetFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });*/
        loadRestaurantInfoFromNW();

    }


    private void addFavRestaurant(String arg_merchant_id) {
        if (AppUtils.isNetworkAvailable(getContext())) {

            //AppUtils.showViews(afieatLoader);
            afieatGifLoaderDialog();
            Map<String, String> params = new HashMap<>();
            params.put("user_id", AppInstance.getInstance(mContext).getFromSharedPref(AppUtils.PREF_USER_ID));
            params.put("action_flag", "add");
            params.put("merchant_id", arg_merchant_id);

            System.out.println("Rahul : FavouriteListActivity : addFavRestaurant : params : " + params.toString());
            Log.e("Parameter", params.toString() + ">>>@@@@@@@>>>");

            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.ADD_REMOVE_TO_FAVOURITE, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Toast.makeText(getContext(), response.optString("msg"), Toast.LENGTH_LONG).show();

                            tv_add_favorite.setTextSize(11);
                            tv_add_favorite.setTextColor(getResources().getColor(R.color.orangeButton));
                           /* Snackbar snackbar = Snackbar
                                    .make(((RestaurantsDetailActivity) getActivity()).viewOnlineOrderMenu, response.optString("msg"), Snackbar.LENGTH_LONG);


                            snackbar.show();*/
                            System.out.println("FavouriteListActivity : response : " + response.toString());
                            AppUtils.log(response);
                            //AppUtils.hideViews(afieatLoader);
                            if (afieatGifLoaderDialog != null) {
                                afieatGifLoaderDialog.dismiss();
                            }
                            // AppUtils.showViews(lvRestaurants);


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            if (afieatGifLoaderDialog != null) {
                                afieatGifLoaderDialog.dismiss();
                            }
                            //AppUtils.hideViews(afieatLoader);
                            if (error.networkResponse == null&&error.getClass().equals(TimeoutError.class) || error.getClass().equals(NoConnectionError.class)) {

                                   /* final Snackbar snackbar = Snackbar.make(((RestaurantsDetailActivity) getActivity()).page, getString(R.string.msg_server_no_response), Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            snackbar.dismiss();

                                        }
                                    });
                                    snackbar.show();*/
                                    Toast.makeText(getContext(), getString(R.string.msg_server_no_response), Toast.LENGTH_LONG).show();


                            }
                        }
                    }
            );
            AppInstance.getInstance(getContext()).addToRequestQueue(request);

        } else {
            /*Snackbar snackbar = Snackbar.make(((RestaurantsDetailActivity) getActivity()).page, getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            snackbar.setActionTextColor(Color.RED);

            snackbar.show();*/
            Toast.makeText(getContext(), getString(R.string.msg_no_internet), Toast.LENGTH_LONG).show();

        }

    }

    private void loadRestaurantInfoFromNW() {
        Map<String, String> params = new HashMap<>();
        params.put("resid", mRestaurantId);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_RESTAURANT_INFO, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Rahul : RestaurantInfoFragment : response : " + response);
                        JSONArray openingTimesArray = response.optJSONArray("restaurant_openning");

                        if (openingTimesArray != null) {
                        }
                        showOpeningTimes(openingTimesArray);

                        // AppUtils.showViews(svInfo);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Snackbar snackbar = Snackbar.make(view.findViewById(R.id.page), getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
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
        AppInstance.getInstance(getContext()).addToRequestQueue(request);
    }

    private void showOpeningTimes(JSONArray openingTimesArray) {
        try {
            for (int i = 0; i < openingTimesArray.length(); i++) {
                JSONObject timeObject = openingTimesArray.optJSONObject(i);
                View timeView = LayoutInflater.from(getContext()).inflate(R.layout.layout_restaurant_info_opening_time_row, null);
                ((TextView) timeView.findViewById(R.id.tvDay)).setText(timeObject.optString("day_name").substring(0, 3));
                ((TextView) timeView.findViewById(R.id.tvOpeningTime)).setText(timeObject.optString("open_at"));
                ((TextView) timeView.findViewById(R.id.tvClosingTime)).setText(timeObject.optString("close_at"));
                if ("ar".equals(AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    String dayName_ar = "";
                    switch (i) {
                        case 0:
                            dayName_ar = "السبت";
                            break;

                        case 1:
                            dayName_ar = "الأحد";
                            break;

                        case 2:
                            dayName_ar = "الإثنين";
                            break;

                        case 3:
                            dayName_ar = "الثلاثاء";
                            break;

                        case 4:
                            dayName_ar = "الأربعاء";
                            break;

                        case 5:
                            dayName_ar = "الخميس";
                            break;

                        case 6:
                            dayName_ar = "الجمعة";
                            break;
                            default:
                                break;
                    }
                    ((TextView) timeView.findViewById(R.id.tvDay)).setText(dayName_ar);

                }
                llOpeningTime.addView(timeView);
            }
       /* if(AppUtils.hasSoftKeys(getActivity()))
        {
          *//*  RelativeLayout viewBlackBottom=findViewById(R.id.viewBlackBottom);
            RelativeLayout.LayoutParams layoutParam= (RelativeLayout.LayoutParams) viewBlackBottom.getLayoutParams();
            layoutParam.setMargins(0,0,0,getNavigationBarHeight());
            layoutParam.height=getNavigationBarHeight();

            viewBlackBottom.setLayoutParams(layoutParam);*//*

             *//*  ViewGroup.LayoutParams parambjbkbjks = findViewById(R.id.viewBlackBottom).getLayoutParams();
          parambjbkbjks.
            findViewById(R.id.viewBlackBottom).setLayoutParams(params);*//*
            LinearLayout.LayoutParams layoutParam= (LinearLayout.LayoutParams) llOpeningTime.getLayoutParams();
            layoutParam.setMargins(0,0,0,getNavigationBarHeight());
            layoutParam.height=getNavigationBarHeight();

            llOpeningTime.setLayoutParams(layoutParam);
        }*/
        } catch (NullPointerException e) {
            System.out.println("RestaurantDetailsFragment : " + e.getMessage());
        }
    }

    private void emigrateTo(Fragment targetFragment) {


        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.entry_in, R.anim.entry_out, R.anim.exit_in, R.anim.exit_out)
                .add(R.id.flContainer, targetFragment)
                .addToBackStack(null)
                .commit();
    }


    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(getActivity());
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }
}
