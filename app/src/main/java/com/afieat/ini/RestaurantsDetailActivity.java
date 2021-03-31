package com.afieat.ini;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.Security.WithOutLoginSecurityPermission;
import com.afieat.ini.database.DBHelper;
import com.afieat.ini.fragments.RestaurantDetailsFragment;
import com.afieat.ini.fragments.RestaurantOrderItemsFragment;
import com.afieat.ini.fragments.RestaurantReviewFragment;
import com.afieat.ini.models.Restaurant;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.AuthkeyValidator;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class  RestaurantsDetailActivity extends AppCompatActivity implements View.OnClickListener {

//    private Restaurant restaurant;

    private String mUserId;
    private String mRestaurantId;
    public String mRestaurantName = "";
    public String MinimumPrice = "";
    public static String GroupID = "";
    private CardView cardRestaurant;

    public ImageView bannerImage;
    public TextView viewOnlineOrderMenu;
    private LinearLayout llOpeningTime;
    public AppBarLayout appBarLayout;
    private FragmentManager fragmentManager;
    private String from_push = "";

    private FrameLayout flcontainer;
    private RelativeLayout homecontent;

    public CoordinatorLayout page;

    public TextView tv_num_of_pic;
    public TextView tv_num_of_reviews;
    public TextView ratingSingle;
    public String from;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_detail);
         final Toolbar mToolbar;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        page = findViewById(R.id.page);
        System.out.println("Rahul : RestaurantsDetailActivity : ");
        fragmentManager = getSupportFragmentManager();
        cardRestaurant = (CardView) findViewById(R.id.cardRestaurant);
        llOpeningTime = (LinearLayout) findViewById(R.id.llOpeningTime);
        cardRestaurant.setVisibility(View.GONE);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        bannerImage = findViewById(R.id.bannerImage);
//       mToolbar.setTitle(getString(R.string.title_menus));
        mToolbar.setTitle(" ");
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        GroupID = getIntent().getStringExtra(AppUtils.EXTRA_GROUP_ID) == null ? "" : getIntent().getStringExtra(AppUtils.EXTRA_GROUP_ID);
        Bundle extras = getIntent().getExtras();
        from = extras.getString("from");
     /*    if(from!=null) {
            if (!from.equalsIgnoreCase("search")) {
                ImageTransitionEffect();
            }
        }else {
            ImageTransitionEffect();
        }*/

//        mToolbar.getBackground().setAlpha(0);
        setSupportActionBar(mToolbar);

//        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_red);


        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);


        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                   /* LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, 0, 200);

                    View timeView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.resturant_details_content_scrolling, null);
                    timeView.findViewById(R.id.nnn).setLayoutParams(params);
*/

                    mToolbar.setTitle(mRestaurantName);
/*
                    CollapsingToolbarLayout.LayoutParams paramsh = new CollapsingToolbarLayout.LayoutParams(
                            CollapsingToolbarLayout.LayoutParams.MATCH_PARENT,
                            250
                    );
                    params.setMargins(0, -500, 0, 0);

                    mToolbar.setLayoutParams(paramsh);*/
                    findViewById(R.id.review_photo_rel).setVisibility(View.GONE);
                    isShow = true;
                } else if (isShow) {
                    mToolbar.setTitle(" ");

                    findViewById(R.id.review_photo_rel).setVisibility(View.VISIBLE);
                    //carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        tv_num_of_pic = findViewById(R.id.num_of_pic);
        tv_num_of_reviews = findViewById(R.id.num_of_reviews);
        ratingSingle = findViewById(R.id.ratingSingle);
        tv_num_of_pic.setOnClickListener(this);
        tv_num_of_reviews.setOnClickListener(this);
        ImageView bannerImage = findViewById(R.id.bannerImage);
        bannerImage.setOnClickListener(this);
        tv_num_of_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(getApplicationContext(), "tv_num_of_reviews", Toast.LENGTH_SHORT).show();
                Intent ii = new Intent(RestaurantsDetailActivity.this, DetailActivityClick_Page.class);
                ii.putExtra("res_id", mRestaurantId);
                ii.putExtra("mRestaurantName", mRestaurantName);
                ii.putExtra("page_to_call", "2");
                startActivity(ii);

            }
        });

        tv_num_of_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Toast.makeText(getApplicationContext(), "tv_num_of_pic", Toast.LENGTH_SHORT).show();
                Intent ii = new Intent(RestaurantsDetailActivity.this, DetailActivityClick_Page.class);
                ii.putExtra("res_id", mRestaurantId);
                ii.putExtra("mRestaurantName", mRestaurantName);
                ii.putExtra("page_to_call", "1");
                startActivity(ii);
            }
        });
//        ivRestaurantBg = (SimpleDraweeView) findViewById(R.id.ivRestaurantBg);


        assert getIntent() != null;
        mRestaurantId = getIntent().getStringExtra(AppUtils.EXTRA_RESTAURANT_ID);
        Log.d("mRestaurantId", "onCreate: " + mRestaurantId);
        from_push = getIntent().getStringExtra(AppUtils.FROM_PUSH);

        Log.d("from_push", "onCreate: " + from_push);


        mUserId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);

        AppUtils.hideViews(cardRestaurant);

        //  loadRestaurantData();
        if (from_push != null && "1".equals(from_push)) {

            getAuth();

           /* RestaurantOrderItemsFragment targetFragment = RestaurantOrderItemsFragment.newInstance(mRestaurantId, MinimumPrice);
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.entry_in, R.anim.entry_out, R.anim.exit_in, R.anim.exit_out)
                    .add(R.id.flContainer, targetFragment)
                    .addToBackStack(null)
                    .commit();
            return;*/
        }
        else{
            loadFragment(from);
        }

        loadRestaurantInfoFromNW();

    /*    @SuppressLint("WrongViewCast") ViewGroup.LayoutParams params = findViewById(R.id.viewBlackBottom).getLayoutParams();

        params.height=getNavigationBarHeight();
        findViewById(R.id.viewBlackBottom).setLayoutParams(params);*/


        ViewGroup.LayoutParams paramsInclude = findViewById(R.id.include_resturant_details_content_scrolling).getLayoutParams();
        //  @SuppressLint("WrongViewCast") ViewGroup.LayoutParams paramsView=  findViewById(R.id.viewOnlineOrderMenu).getLayoutParams();

        viewOnlineOrderMenu = findViewById(R.id.viewOnlineOrderMenu);

        viewOnlineOrderMenu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                //collapsingToolbar.scrollTo(0,0);
                //  appBarLayout.setExpanded(false,true);
               /*collapsingToolbar.setEnabled(false);
               collapsingToolbar.setNestedScrollingEnabled(false);
*/
                Intent ii = new Intent(RestaurantsDetailActivity.this, DetailActivityClick_Page.class);
                ii.putExtra("res_id", mRestaurantId);
                ii.putExtra("mRestaurantName", mRestaurantName);
                ii.putExtra("MinPrice", MinimumPrice);
                ii.putExtra("page_to_call", "3");
                startActivity(ii);
             /*   CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                ((DisableableAppBarLayoutBehavior) layoutParams.getBehavior()).setEnabled(false);
                Fragment targetFragment = RestaurantOrderItemsFragment.newInstance(mRestaurantId);
                emigrateTo(targetFragment);*/
            }
        });


        if (AppUtils.hasSoftKeys(RestaurantsDetailActivity.this)) {
          /*  RelativeLayout viewBlackBottom=findViewById(R.id.viewBlackBottom);
            RelativeLayout.LayoutParams layoutParam= (RelativeLayout.LayoutParams) viewBlackBottom.getLayoutParams();
            layoutParam.setMargins(0,0,0,getNavigationBarHeight());
            layoutParam.height=getNavigationBarHeight();

            viewBlackBottom.setLayoutParams(layoutParam);
*/
            ViewGroup.LayoutParams parambjbkbjks = findViewById(R.id.viewBlackBottom).getLayoutParams();
            parambjbkbjks.height = getNavigationBarHeight();
            findViewById(R.id.viewBlackBottom).setLayoutParams(parambjbkbjks);
        }
        //  findViewById(R.id.include_resturant_details_content_scrolling).setPadding(0,0,0,getNavigationBarHeight()+ viewOnlineOrderMenu.getHeight()+50);


/*      // findViewById(R.id.viewOnlineOrderMenu).setPadding(0,0,0,getNavigationBarHeight());


        TextView viewOnlineOrderMenu=findViewById(R.id.viewOnlineOrderMenu);
    ViewGroup.LayoutParams params = viewOnlineOrderMenu.getLayoutParams();
// Changes the height and width to the specified *pixels*
        params.height = getNavigationBarHeight();

        ViewGroup.LayoutParams paramsj = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        paramsj.setMargins(left, top, right, bottom);
        paramsj.

        viewOnlineOrderMenu.setLayoutParams(params);*/

    }

    private int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public void ImageTransitionEffect() {

        Bundle extras = getIntent().getExtras();
        String logoPath = extras.getString(RestaurantListActivity.EXTRA_ANIMAL_ITEM);
        System.out.println("dfsdf : " + logoPath);
        //logoPath=logoPath.replace("thumb_81_81","thumb_300_300");
        bannerImage = findViewById(R.id.bannerImage);
        supportPostponeEnterTransition();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String imageTransitionName = extras.getString(RestaurantListActivity.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME);
            bannerImage.setTransitionName(imageTransitionName);
        }

        Picasso.with(this)
                .load("https://d22vvrqrexhw5s.cloudfront.net/upload/restaurants/image/thumb_300_300/1480488558-13346996_471985796340291_3529359004293614045_n.jpg")
                .noFade()
                .into(bannerImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        supportStartPostponedEnterTransition();
                    }
                });

    }

    public void ImageTransitionEffectFrag() {

        Bundle extras = getIntent().getExtras();
        String logoPath = extras.getString(RestaurantListActivity.EXTRA_ANIMAL_ITEM);
        System.out.println("gcyc : " + logoPath);
        logoPath = logoPath.replace("thumb_81_81", "thumb_300_300");
        bannerImage = findViewById(R.id.bannerImage);
        supportPostponeEnterTransition();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String imageTransitionName = extras.getString(RestaurantListActivity.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME);
            bannerImage.setTransitionName(imageTransitionName);
        }

        Picasso.with(this)
                .load(logoPath)
                .noFade()
                .into(bannerImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        supportStartPostponedEnterTransition();
                    }
                });

    }


    public void showViewR() {
        System.out.println("Rahul : RestaurantsDetailActivity : showViewR : ");
        findViewById(R.id.homeContentRel).setVisibility(View.VISIBLE);
        findViewById(R.id.flContainer).setVisibility(View.GONE);

        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

      /*  flcontainer=findViewById(R.id.flContainer);
        homecontent=findViewById(R.id.homeContentRel);

        */
        System.out.println("Rahul : onResumeCalled : RestaurantsDetailActivity");
       /* findViewById(R.id.homeContentRel).setVisibility(View.VISIBLE);
        findViewById(R.id.flContainer).setVisibility(View.GONE);*/

        LinearLayout view = (LinearLayout) findViewById(R.id.fabLayout);
        assert view != null;
        FloatingActionButton fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        FloatingActionButton fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        FloatingActionButton fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);
        FloatingActionButton fab4 = (FloatingActionButton) view.findViewById(R.id.fab4);
        FloatingActionButton fab1_ar = (FloatingActionButton) view.findViewById(R.id.fab1_ar);
        FloatingActionButton fab2_ar = (FloatingActionButton) view.findViewById(R.id.fab2_ar);
        FloatingActionButton fab3_ar = (FloatingActionButton) view.findViewById(R.id.fab3_ar);
        FloatingActionButton fab4_ar = (FloatingActionButton) view.findViewById(R.id.fab4_ar);
        AppUtils.hideViews(fab1, fab2, fab3, fab4, fab1_ar, fab2_ar, fab3_ar, fab4_ar);
        DBHelper db = new DBHelper(this);
        AppUtils.log(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        int itemCount = db.getFoodsBasket(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID)).size();
        AppUtils.log(itemCount + "");
        switch (itemCount) {
            case 0:
                //do nothing
                break;
            case 1:
//                if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {
//                    AppUtils.showViews(fab1_ar);
//                } else {
                AppUtils.showViews(fab1);
//                }
                break;
            case 2:
//                if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {
//                    AppUtils.showViews(fab2_ar);
//                } else {
                AppUtils.showViews(fab2);
//                }
                break;
            case 3:
//                if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {
//                    AppUtils.showViews(fab3_ar);
//                } else {
                AppUtils.showViews(fab3);
//                }
                break;
            default:
//                if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {
//                    AppUtils.showViews(fab4_ar);
//                } else {
                AppUtils.showViews(fab4);
//                }
                break;
        }
        FabClicked fabClicked = new FabClicked();
        fab1.setOnClickListener(fabClicked);
        fab2.setOnClickListener(fabClicked);
        fab3.setOnClickListener(fabClicked);
        fab4.setOnClickListener(fabClicked);
        fab1_ar.setOnClickListener(fabClicked);
        fab2_ar.setOnClickListener(fabClicked);
        fab3_ar.setOnClickListener(fabClicked);
        fab4_ar.setOnClickListener(fabClicked);
        db.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.num_of_reviews:
                // Toast.makeText(getApplicationContext(),"num_of_reviews",Toast.LENGTH_SHORT).show();
                break;
            case R.id.num_of_pic:
                //Toast.makeText(getApplicationContext(),"num_of_pic",Toast.LENGTH_SHORT).show();

                break;

            case R.id.bannerImage:

                // Toast.makeText(getApplicationContext(),"Image",Toast.LENGTH_SHORT).show();

                break;
            default:
                break; }
    }

    class FabClicked implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RestaurantsDetailActivity.this, BasketActivity2.class);
            System.out.println("MinPrice : " + MinimumPrice);
           // AppUtils.MINIMUM_PRICE = MinimumPrice;
          //  AppInstance.getInstance(getApplicationContext()).addToSharedPref("min_price",MinimumPrice);
            intent.putExtra("MinPrice", AppInstance.getInstance(getApplicationContext()).getFromSharedPref("min_price"));
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break; }
        return false;
    }

    private void loadRestaurantData() {
        final ProgressDialog dialog = AppUtils.showProgress(RestaurantsDetailActivity.this, "", getString(R.string.msg_please_wait));
        String params = "?restaurant_id=" + mRestaurantId;
        if (mUserId.length() > 0) params += "&shopuserid=" + mUserId;
        Log.d("restaurntDetails params", "  " + params);

        NetworkRequest request = new NetworkRequest(Request.Method.GET, Apis.RESTAURANT_DETAILS + params, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.hideProgress(dialog);

                        if (response.has("status") && response.optInt("status") == 111) {
                            new AuthkeyValidator(RestaurantsDetailActivity.this).CallForReAuth(new AuthkeyValidator.Authkey() {
                                @Override
                                public void Oncomplete() {
                                    loadRestaurantData();
                                }
                            });
                        } else {
                            Restaurant restaurant = new Restaurant();

                            AppUtils.log("check response" + response.toString());
                            System.out.println("Rahul : RestaurantsDetailActivity : loadRestaurantData : response : " + response.toString());
                            JSONObject restaurantDetails = response.optJSONObject("restaurant_details");

                            // AppUtils.log("check restaurantDetails" + "" + restaurantDetails.toString());
                            restaurant.setName(restaurantDetails.optString("restaurant_name"));
                            restaurant.setAddress(restaurantDetails.optString("search_address"));
                            restaurant.setId(restaurantDetails.optString("merchant_id"));
                            restaurant.setReviewCount(restaurantDetails.optString("review_count"));
                            restaurant.setUriThumb(Apis.IMG_PATH + "restaurants/image/thumb_81_81/" + restaurantDetails.optString("merchant_photo_bg"));
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
                            restaurant.setProcessingTime(restaurantDetails.optString("processing_time"));
                            restaurant.setMinOrder(restaurantDetails.optString("merchant_minimum_order"));
                            restaurant.setCuisine(restaurantDetails.optString("cuisine"));
                            restaurant.setGalleryCount(restaurantDetails.optString("merchant_gallery_count"));


                            // Anurup 27-02-17
                            //       Log.i("Anurp---orderno","   "+restaurantDetails.optString("no_of_order"));
                            restaurant.setOrderNumber(restaurantDetails.optString("no_of_order"));
                            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(AppUtils.PREF_ORDER_NO, restaurantDetails.optString("no_of_order"));
                            editor.commit();
                            if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                                restaurant.setName(restaurantDetails.optString("restaurant_name_ar"));
                                restaurant.setCuisine(restaurantDetails.optString("cuisine_ar"));
                            }

                            displayRestaurantData(restaurant);
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

    private void displayRestaurantData(final Restaurant restaurant) {
        final ImageView ivRestaurantLogo = (ImageView) cardRestaurant.findViewById(R.id.ivRestaurantLogo);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //    ivRestaurantLogo.setImageURI(Uri.parse(restaurant.getUriThumb()));
                Glide
                        .with(getApplicationContext())
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


        TextView tv_num_of_pic = findViewById(R.id.num_of_pic);
        TextView tv_num_of_reviews = findViewById(R.id.num_of_reviews);

        TextView tv_add_favorite,
                tv_add_reviews,
                tv_add_photos;

        tv_add_favorite = findViewById(R.id.add_favorite);
        tv_add_reviews = findViewById(R.id.add_review);
        tv_add_photos = findViewById(R.id.add_photo);

        tv_add_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment targetFragment = RestaurantReviewFragment.newInstance(mRestaurantId);
                emigrateTo(targetFragment);
            }
        });

        tv_add_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment targetFragment = RestaurantReviewFragment.newInstance(mRestaurantId);
                emigrateTo(targetFragment);
            }
        });

        TextView tvRestaurantName = findViewById(R.id.restaurantName);
        TextView tvRestaurantLocation = findViewById(R.id.restaurantLocation);
        TextView tvRestaurantSpeciality = findViewById(R.id.restaurantSpeciality);
        TextView tvRestaurantOpeningStatus = findViewById(R.id.restaurantOpeningStatus);
        TextView tvRestaurantReviews = findViewById(R.id.restaurantReviews);

        tvRestaurantReviews.setText(restaurant.getReviewCount() + " Reviews");
        tv_num_of_reviews.setText(restaurant.getReviewCount() + " Reviews");

        tv_num_of_pic.setText(restaurant.getGalleryCount() + " Photos");

        TextView tvRestaurantMinimumOrder = findViewById(R.id.restaurantMinimumOrder);
        TextView tvResProcessingTime = findViewById(R.id.resProcessingTime);
        TextView tvResDeliveryTime = findViewById(R.id.resDeliveryTime);
        TextView tvRestaurantDeliveryCharge = findViewById(R.id.restaurantDeliveryCharge);


        tvRestaurantMinimumOrder.setText(Html.fromHtml("Minimum Order<br>" + "<span style='color:#000'><b>IQD " + restaurant.getMinOrder() + "</b></span>"));
        tvResProcessingTime.setText(Html.fromHtml("Processing Time<br>" + "<span style='color:#000'><b>" + restaurant.getProcessingTime() + "</b></span>"));
        tvResDeliveryTime.setText(Html.fromHtml("Delivery Time<br>" + "<span style='color:#000'><b>" + restaurant.getDeliveryTime() + "</b></span>"));
        // tvRestaurantReviews.setText(restaurant.get);

        TextView ratingSingle = findViewById(R.id.ratingSingle);

        tvRestTitle.setText(restaurant.getName());
        mRestaurantName = restaurant.getName();
        tvRestaurantName.setText(mRestaurantName);
        tvRestCuisine.setText(restaurant.getCuisine().trim());
        tvRestaurantSpeciality.setText(restaurant.getCuisine().trim());
        tvRestStatus.setText(restaurant.getStatus());
        tvRestaurantLocation.setText(restaurant.getAddress());
        tvRestaurantDeliveryCharge.setText("IQD " + restaurant.getDeliveryCharge() + " delivery charge");

        /*if (restaurant.getStatus().equals("Open")) {
            tvRestStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greenButton));
        } else if (restaurant.getStatus().equals("Closed")) {
            tvRestStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        } else {
            tvRestStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.orangeButton));
        }*/
        System.out.println("Rahul : RestaurantsDetailActivity : getStatus : " + restaurant.getStatus());
        if ("Open".equals(restaurant.getStatus())) {
            if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                tvRestStatus.setText("فتح");
                tvRestaurantOpeningStatus.setText("مفتوح الان");
                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                tvRestaurantOpeningStatus.setText("<span style='color:#00B378;'> - مفتوح الان</span>" + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")");

            } else {
                tvRestStatus.setText("Open");
                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#00B378;'><b>Open now - </b></span>" + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")"));

            }
            tvRestStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greenButton));
            //  tvRestaurantOpeningStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greenButton));
        } else if ("Close".equalsIgnoreCase(restaurant.getStatus())) {
            tvRestStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.orangeButton));
            if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                tvRestStatus.setText("مغلق");

                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                tvRestaurantOpeningStatus.setText("مغلق الان - " + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")");

            } else {
                tvRestStatus.setText("Close");
                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#C63A2B;'>Closed now - </span>" + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")"));

            }
        } else if ("Closed".equalsIgnoreCase(restaurant.getStatus())) {
            tvRestStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.orangeButton));
            if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                tvRestStatus.setText("مغلق");
                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                tvRestaurantOpeningStatus.setText("مغلق الان : " + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")");


            } else {
                tvRestStatus.setText("Closed");
                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                tvRestaurantOpeningStatus.setText(Html.fromHtml("<span style='color:#C63A2B;'>Closed now - </span>" + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")"));

            }
        } else {
            tvRestStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.orangeButton));
            if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                tvRestStatus.setText("مشغول");
                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                tvRestaurantOpeningStatus.setText("مشغول الآن : " + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")");

            } else {
                tvRestStatus.setText("Busy");
                String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                tvRestaurantOpeningStatus.setText("Busy now: " + restaurant.getOpeningTime() + " to " + restaurant.getClosingTime() + " (" + dayLongName + ")");

            }
        }
        /*if (restaurant.getOpeningTime().trim().length() > 0 && restaurant.getClosingTime().length() > 0)
            tvRestOpeningHours.setText(restaurant.getOpeningTime() + " to " + restaurant.getClosingTime());
        else
            tvRestOpeningHours.setText("N/A");*/
        ratingbar.setRating(Float.parseFloat(restaurant.getRating()));
        ratingSingle.setText(restaurant.getRating());


        tvRating.setText("(" + AppUtils.changeToArabic(restaurant.getRatingCount(), getApplicationContext()) + ")");
        // Sunit 25-01-2017
        if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            tvRestOpeningHours.setText(restaurant.getOpeningTime() + " إلى " + restaurant.getClosingTime());
        } else {
            tvRestOpeningHours.setText(restaurant.getOpeningTime() + " to " + restaurant.getClosingTime());
        }

        tvMinOrder.setText(getString(R.string.currency) + AppUtils.changeToArabic(restaurant.getMinOrder(), getApplicationContext()));
        MinimumPrice = restaurant.getMinOrder();
        String delTime = restaurant.getDeliveryTime().trim().length() > 0
                ? AppUtils.changeToArabic(restaurant.getDeliveryTime().trim(), getApplicationContext()) + getString(R.string.min_delivery)
//                : "N/A";
                : "";

        String processingTime = restaurant.getProcessingTime().trim().length() > 0 ? AppUtils.changeToArabic(restaurant.getProcessingTime().trim(), getApplicationContext()) + (getString(R.string.min_procesing_2)) : "";

        tvDeliveryTime.setText(processingTime + ", " + delTime);

        tvOrderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestaurantOrderItemsFragment targetFragment = RestaurantOrderItemsFragment.newInstance(mRestaurantId, MinimumPrice);
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.entry_in, R.anim.entry_out, R.anim.exit_in, R.anim.exit_out)
                        .add(R.id.flContainer, targetFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        AppUtils.showViews(cardRestaurant);
    }

    private void emigrateTo(Fragment targetFragment) {


        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.entry_in, R.anim.entry_out, R.anim.exit_in, R.anim.exit_out)
                .add(R.id.flContainer, targetFragment)
                .addToBackStack(null)
                .commit();
        /*findViewById(R.id.homeContentRel).setVisibility(View.GONE);
        findViewById(R.id.flContainer).setVisibility(View.VISIBLE);*/

    }

    private void loadFragment(String from) {

        Context context = RestaurantsDetailActivity.this;
        Fragment fragment = RestaurantDetailsFragment.newInstance(mRestaurantId, from, context);
        fragmentManager
                .beginTransaction()
                .add(R.id.flContainer, fragment)
                .addToBackStack(null)
                .commit();
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
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_operation_not_completed), Snackbar.LENGTH_INDEFINITE);
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
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void showOpeningTimes(JSONArray openingTimesArray) {
        for (int i = 0; i < openingTimesArray.length(); i++) {
            JSONObject timeObject = openingTimesArray.optJSONObject(i);
            View timeView = LayoutInflater.from(this).inflate(R.layout.layout_restaurant_info_opening_time_row, null);
            ((TextView) timeView.findViewById(R.id.tvDay)).setText(timeObject.optString("day_name"));
            ((TextView) timeView.findViewById(R.id.tvOpeningTime)).setText(timeObject.optString("open_at"));
            ((TextView) timeView.findViewById(R.id.tvClosingTime)).setText(timeObject.optString("close_at"));
            if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
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
                        break;
                    default:
                        break;  }
                ((TextView) timeView.findViewById(R.id.tvDay)).setText(dayName_ar);

            }
            llOpeningTime.addView(timeView);
        }
    }


    @Override
    public void onBackPressed() {


        //finishAfterTransition();
        if (from_push != null && "1".equals(from_push)) {
            finish();
            Intent intent = new Intent(getApplicationContext(), CategoryListActivity.class);
            startActivity(intent);

        } else {

            if (fragmentManager.getBackStackEntryCount() > 1) {
                fragmentManager.popBackStack();
            } else {

                supportFinishAfterTransition();
                //  finishAfterTransition();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    /*@Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);

        System.out.println("overridePendingTransition : called : ");
    }*/

    public void getAuth(){

        String deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        AppUtils.log("@@ Online- " + AppUtils.isNetworkAvailable(getApplicationContext()));
        if (AppUtils.isNetworkAvailable(getApplicationContext()))
            new WithOutLoginSecurityPermission(new WithOutLoginSecurityPermission.OnSecurityResult() {
                @Override
                public void OnAllowPermission(String AuthToken) {
                    AppUtils.AUTHRIZATIONKEY = AuthToken;
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                    String DateCreate = format.format(calendar.getTime());
                    AppInstance.getInstance(RestaurantsDetailActivity.this).setAuthkeyforall(AuthToken, DateCreate);

                    RestaurantOrderItemsFragment targetFragment = RestaurantOrderItemsFragment.newInstance(mRestaurantId, MinimumPrice);
                    fragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.entry_in, R.anim.entry_out, R.anim.exit_in, R.anim.exit_out)
                            .add(R.id.flContainer, targetFragment)
                            .addToBackStack(null)
                            .commit();
                    return;
                   /* if (mUserId.length() > 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                AppUtils.AUTHRIZATIONKEY = AppInstance.getInstance(getApplicationContext()).getAuthkeyforall();
                             *//*   startActivity(new Intent(SplashActivity.this, LocationActivity.class));
                                finish();*//*
                                networkDone = true;
                                if (aminDone && networkDone) {
                                    startActivity(new Intent(SplashActivity.this, LocationActivity.class));
                                    finish();
                                }
                            }
                        }, 2000);

                    } else {
                        LoginActivity.FromSpalshPage = true;
                        networkDone = true;
                        if (aminDone && networkDone) {
                            startActivity(new Intent(SplashActivity.this, LocationActivity.class));
                            finish();
                        }

                    }*/
                    //AppUtils.log("@@ APPTOKEN-" + AuthToken);
                }

                @Override
                public void OnRejectPermission() {
                    Log.e("Auth","AUTHRIZATIONKEY NOT FOUND");
                }
            }).execute(deviceId);
        else
            Toast.makeText(getApplicationContext(), getString(R.string.msg_no_internet), Toast.LENGTH_LONG).show();
    }
}
