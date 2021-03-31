package com.afieat.ini;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.RequiresApi;

import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.afieat.ini.adapters.CuisinesAdapter;
import com.afieat.ini.adapters.RestaurantsListAdapter;
import com.afieat.ini.adapters.RestaurentListAdapterRecyclerView;
import com.afieat.ini.adapters.slidingImageAdapter;
import com.afieat.ini.database.DBHelper;
import com.afieat.ini.fragments.CrazyDealsFragment;
import com.afieat.ini.fragments.SimplePhotoFragment;
import com.afieat.ini.interfaces.OnBottomReachedListener;
import com.afieat.ini.interfaces.OnRestaurantPhotoClicked;
import com.afieat.ini.interfaces.SharedAnimItemClickListener;
import com.afieat.ini.models.AdsModel;
import com.afieat.ini.models.Cuisine;
import com.afieat.ini.models.InProcessOrders;
import com.afieat.ini.models.Restaurant;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.AuthkeyValidator;
import com.afieat.ini.utils.NetworkRequest;
import com.afieat.ini.webservice.ApiClient;
import com.afieat.ini.webservice.ApiInterface;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class RestaurantListActivity extends AppCompatActivity implements SharedAnimItemClickListener, OnBottomReachedListener {

    private Toolbar mToolbar;
    private ListView lvRestaurants;
    private RecyclerView rvRestaurants;
    private RestaurentListAdapterRecyclerView mAdapterRecyclerView;
    private ListView lvNav;
    private NavigationView mNavigationView, sortView;
    private DrawerLayout mDrawerLayout;
    private ProgressBar progressBar;
    private GifImageView afieatLoader;
//    private View listFooterView;

    public static final String EXTRA_ANIMAL_ITEM = "image_url";
    public static final String EXTRA_ANIMAL_IMAGE_TRANSITION_NAME = "image_transition_name";
    private ImageView ivClearFilter;
    private Spinner spSortList, spCuisine;

    private RadioGroup rgFilter,
            rgPrice,
            rgSort;
    private RadioButton rbFilterOffer,
            rbFilterRatings,
            rbFilterOpenRests,
            rbFilterVeg;
    private RadioButton rbLo2Hi;
    private LayoutInflater inflater = null;

    private Map<String, String> filterParams;

    private static int mStartIndex;
    private boolean willLoadMore;
    private String mUserId,
            mRegionId,
            mRegionName,
            mCityName,
            mCityId,
            GroupID;
    private List<Restaurant> restaurants;
    private List<Restaurant> noAddrestaurants;
    private List<Cuisine> mCuisines;
    private Snackbar snackNoRestaurants;
//    private AlertDialog dialog = null;

    private AppInstance appInstance;
    private RecyclerView myList = null;
    private LinearLayoutManager layoutManager = null;
    private String redirectionType = "";
    private ViewPager viewPager = null;
    private LinkedList<CrazyDealsFragment> tabbedFragements = null;


    private String[] navItems = {"Address", "Restaurants", "My Orders", "My Account", "My Wallet", "My Reviews", "My Deals", "English/عربى", "Help Center"};
    private String[] navItems_ar = {"عنوان", "المطاعم", "طلباتي", "حسابي", "قسائم الشراء الخاصة بي", "بلدي التعليقات", "عروضي", "English/عربى", "مركز المساعدة"};
    private int[] navIcons = {
            R.drawable.nav_address,
            R.drawable.nav_restaurants,
            R.drawable.nav_orders,
            R.drawable.nav_account,
            R.drawable.nav_wallet,
            R.drawable.nav_star,
            R.drawable.nav_deal,
            R.drawable.nav_language,
            /*R.drawable.nav_settings,*/
            /*R.drawable.nav_contact,*/
            R.drawable.nav_help,
            /*R.drawable.nav_signout*/
    };

    private final int QUERY_LIMIT = 10;
    private final int REQUEST_LOCATION = 100;
    private final int REQUEST_CHECKIN = 101;


    private MenuItem itemfilter,
            itemnotificatin,
            itemsearch;
    private String search_item_name = "";
    private String isSearch = "";
    private String search_cuisine_id = "";
    private AdsModel res = null;
    private ArrayList<Restaurant.AddsBean> arrAds;
    private String first_position_after = "";
    private String place_interval = "";
    private ArrayList<Integer> arrAdsinterval;

    private DBHelper db;
    private int DBitemCount;
    private View orderCartView;
    private ImageView imgRest;
    private ViewPager mPager;
    private DotsIndicator indicator;
    private ArrayList<InProcessOrders> arrInProcessOrders;


    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator currentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int shortAnimationDuration;
    private boolean isScrolling = false;


    public RestaurantListActivity() {
        mStartIndex = 0;
        willLoadMore = false;
        restaurants = new ArrayList<>();
        noAddrestaurants = new ArrayList<>();
        arrAdsinterval = new ArrayList<>();
        mCuisines = new ArrayList<>();
        filterParams = new HashMap<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        //       Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        System.out.println("Rahul : RestaurantListActivity : ");
        appInstance = AppInstance.getInstance(getApplicationContext());
        // getAds();

        String lang = appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG);
        if ("ar".equals(lang)) {
            navItems = navItems_ar;
        }
        orderCartView = findViewById(R.id.orderCartView);
        indicator = (DotsIndicator) findViewById(R.id.indicator);
        mPager = (ViewPager) findViewById(R.id.pager);

        snackNoRestaurants = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_restaurants), Snackbar.LENGTH_SHORT);

        mRegionId = appInstance.getFromSharedPref(AppUtils.PREF_REGION_ID);
        mRegionName = appInstance.getFromSharedPref(AppUtils.PREF_REGION);
        mCityName = appInstance.getFromSharedPref(AppUtils.PREF_CITY);
        mCityId = appInstance.getFromSharedPref(AppUtils.PREF_CITY_ID);
        mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);
        GroupID = getIntent().getStringExtra(AppUtils.EXTRA_GROUP_ID);
        search_item_name = getIntent().getStringExtra(AppUtils.EXTRA_ITEM_NAME);
        search_cuisine_id = getIntent().getStringExtra(AppUtils.EXTRA_CUISINE_ID);
        isSearch = getIntent().getStringExtra(AppUtils.EXTRA_FROM_SEARCH);
        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        assert mToolbar != null;
        if (mCityName.length() > 0)
            mToolbar.setTitle(mCityName);
        else
            mToolbar.setTitle(getString(R.string.iraq));
        if (mRegionName.length() > 0)
            mToolbar.setSubtitle(mRegionName);
//        else
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setSubtitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
//        mToolbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RestaurantListActivity.this, LocationActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra(AppUtils.EXTRA_REQ_LOCATION, String.valueOf(true));
//                intent.putExtra("isFrom", "RestaurantListActivity");
////                startActivityForResult(intent, REQUEST_LOCATION);
//                startActivity(intent);
//                finish();
//            }
//        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvRestaurants = (ListView) findViewById(R.id.lvRestaurants);
        rvRestaurants = findViewById(R.id.rvRestaurants);
        rvRestaurants.setHasFixedSize(true);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(this));

        lvNav = (ListView) findViewById(R.id.lvNav);
        mNavigationView = (NavigationView) findViewById(R.id.navView);
        sortView = (NavigationView) findViewById(R.id.sortView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        afieatLoader = findViewById(R.id.afieatLoader);
//        listFooterView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_list_footer_progress, null, false);

        initFilters();

//        lvRestaurants.addFooterView(listFooterView);
        RestaurantsListAdapter adapter = new RestaurantsListAdapter(restaurants, getApplicationContext());
        mAdapterRecyclerView = new RestaurentListAdapterRecyclerView(getApplicationContext(), restaurants, RestaurantListActivity.this, RestaurantListActivity.this, this);

        lvRestaurants.setAdapter(adapter);

        rvRestaurants.setAdapter(mAdapterRecyclerView);


        adapter.setOnRestaurantPhotoClicked(new OnRestaurantPhotoClicked() {
            @Override
            public void onPhotoClicked(Uri logoPath) {
                Fragment fragment = SimplePhotoFragment.newInstance(logoPath);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        lvRestaurants.setOnScrollListener(new RestaurantListScrollListener());
        lvRestaurants.setOnItemClickListener(new RestaurantListItemClickListener());

        rvRestaurants.setOnScrollListener(new RestaurantListScrollListener());


        lvNav.setAdapter(new NavListAdapter());
        lvNav.setOnItemClickListener(new NavMenuItemClickListener());

        AppUtils.hideViews(afieatLoader);

//        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//            }
//        };
//        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();

        ((TextView) findViewById(R.id.LogInBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantListActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        //   checkForLoggedInStatus();
        loadCuisineListFromNW();
        filterParams.put("pric", "high");
        filterParams.put("srt", "review_count");

        if (isSearch != null && "items".equals(isSearch)) {
            loadRestaurantBySearchItems(search_item_name);
            return;
        }
        if (isSearch != null && "cuisine".equals(isSearch)) {
            loadRestaurantBySearchCuisine(search_cuisine_id);
            return;
        }

        loadRestaurantsFromNW();


    }

    public void setImageZoom(Bitmap argImg, String argUri) {
        /*BitmapDrawable drawable = (BitmapDrawable) argImg.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);*/
       /* ImageView image2 = (ImageView) findViewById(R.id.expanded_image);
        image2.setImageBitmap(bmp);*/


        final Dialog imageZoomDialog = new Dialog(RestaurantListActivity.this);
        imageZoomDialog.setContentView(R.layout.zoom_imageview_dialog);
        imageZoomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final ImageView expanded_image = imageZoomDialog.findViewById(R.id.expanded_image);

       /* Glide
                .with(getApplicationContext())
                .load(argImg)
                .placeholder(R.drawable.placeholder_land)
                .into(expanded_image);*/

        Glide
                .with(getApplicationContext())
                .load(argUri)
                .asBitmap()
                .placeholder(R.drawable.placeholder_land)
                .into(new SimpleTarget<Bitmap>(300, 300) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {

                        expanded_image.setImageBitmap(resource);
                        imageZoomDialog.findViewById(R.id.pbLoading).setVisibility(View.GONE);

                        // setBackgroundImage(resource);
                    }
                });

        // expanded_image.setImageBitmap(argImg);
        ScaleAnimation fade_in = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(300);
        fade_in.setFillAfter(true);
        expanded_image.startAnimation(fade_in);



/*
        ((RestaurentListAdapterRecyclerView.MyViewHolder) holder).ivRestaurantLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) ((RestaurentListAdapterRecyclerView.MyViewHolder) holder).ivRestaurantLogo.getDrawable()).getBitmap();
                mRestaurantListActivity.setImageZoom(bitmap);
            }
        });*/

        imageZoomDialog.show();
    }

    public void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }

    private void loadRestaurantBySearchCuisine(String search_cuisine_id) {
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {

            AppUtils.showViews(afieatLoader);
            Map<String, String> params = new HashMap<>();

            params.put("cid", mCityId);
            params.put("rid", mRegionId);
            params.put("csn", search_cuisine_id);
            if (GroupID != null)
                params.put("group_id", GroupID);
            params.put("lmt", String.valueOf(QUERY_LIMIT));
            params.put("restofst", String.valueOf(mStartIndex));
            params.putAll(filterParams);
            if (mUserId.length() > 0) params.put("shopuserid", mUserId);

            Log.e("Parameter", params.toString() + ">>>@@@@@@@>>>");

            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.DEMOGROPULISTCHECK, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            AppUtils.log(response);
                            AppUtils.hideViews(afieatLoader);
                            //  AppUtils.showViews(lvRestaurants);
                            AppUtils.showViews(rvRestaurants);


                            if (response.has("status") && response.optInt("status") == 111) {
                                new AuthkeyValidator(RestaurantListActivity.this).CallForReAuth(new AuthkeyValidator.Authkey() {
                                    @Override
                                    public void Oncomplete() {
                                        loadRestaurantsFromNW();
                                    }
                                });
                            } else {

                                JSONArray restaurantsArray = response.optJSONArray("restaurants");
                                for (int i = 0; i < restaurantsArray.length(); i++) {
                                    JSONObject object = restaurantsArray.optJSONObject(i);
                                    Log.d("!@@ ReTRO- ", object + "");
                                    Restaurant restaurant = new Restaurant();
                                    restaurant.setId(object.optString("merchant_id"));

                                    String name = object.optString("restaurant_name");
                                    restaurant.setName("".equals(name) ? object.optString("branch_name") : name);

                                    String address = object.optString("search_address");
                                    if ("".equals(address)) {
                                        address = object.optString("branch_search_address");
                                    }
                                    if (address.contains(", Iraq")) {
                                        address = address.replace(", Iraq", "");
                                    }
                                    restaurant.setAddress(address);

                                    String cuisine = object.optString("cuisine");
                                    restaurant.setCuisine(cuisine);

                                    String status = object.optString("status");
                                    restaurant.setStatus(status);

                                    if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                                        restaurant.setName(object.optString("restaurant_name_ar"));
                                        restaurant.setCuisine(object.optString("cuisine_ar"));
                                    }

                                    String offerPercentage = object.optString("offer_percentage");
                                    if (!"".equals(offerPercentage)) {
                                        offerPercentage = offerPercentage.split("\\.")[0];
                                    }
                                    if ("null".equals(offerPercentage)) {
                                        offerPercentage = "";
                                    }
                                    restaurant.setDiscount(offerPercentage);

                                    restaurant.setRating(object.optString("present_rating"));
                                    restaurant.setRatingCount(object.optString("review_count"));

                                    String deliveryTime = object.optString("delivery_time");
                                    restaurant.setDeliveryTime(deliveryTime);
                                    restaurant.setProcessingTime(object.optString("processing_time"));

                                    restaurant.setUriThumb(Apis.IMG_PATH + "restaurants/image/thumb_81_81/" + object.optString("merchant_photo_bg"));
                                    restaurants.add(restaurant);
                                }
                                willLoadMore = false;
                                if (restaurantsArray.length() > 0) {
                                    try {
//                                    ((RestaurantsListAdapter) ((HeaderViewListAdapter) lvRestaurants.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                                        ((RestaurantsListAdapter) lvRestaurants.getAdapter()).notifyDataSetChanged();
                                        mAdapterRecyclerView.notifyDataSetChanged();

//                                    lvRestaurants.setAdapter(new RestaurantsListAdapter(restaurants));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
//                                lvRestaurants.removeFooterView(listFooterView);
                                }

                                if (restaurants.size() == 0) {
                                    ((RestaurantsListAdapter) lvRestaurants.getAdapter()).notifyDataSetChanged();
                                    mAdapterRecyclerView.notifyDataSetChanged();
                                    if (!snackNoRestaurants.isShown())
                                        snackNoRestaurants.show();
                                } else {
                                    if (snackNoRestaurants.isShown())
                                        snackNoRestaurants.dismiss();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            AppUtils.hideViews(afieatLoader);
                            if (error.networkResponse == null) {
                                if (error.getClass().equals(TimeoutError.class) || error.getClass().equals(NoConnectionError.class)) {
                                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_server_no_response), Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            snackbar.dismiss();
                                            loadRestaurantsFromNW();
                                        }
                                    });
                                    snackbar.show();
                                }
                            }
                        }
                    }
            );
            appInstance.addToRequestQueue(request);

        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadRestaurantsFromNW();
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }

    }

    private void loadRestaurantBySearchItems(String search_item_name) {
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {

            AppUtils.showViews(afieatLoader);
            Map<String, String> params = new HashMap<>();

            params.put("lmt", String.valueOf(QUERY_LIMIT));
            params.put("item_name", String.valueOf(search_item_name));
            params.put("restofst", String.valueOf(mStartIndex));
            params.putAll(filterParams);
            if (mUserId.length() > 0) params.put("shopuserid", mUserId);

            Log.e("Parameter", params.toString() + ">>>@@@@@@@>>>");

            System.out.println("Rahul : ");
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.SEARCH_RESTAURANT_BY_ITEM_NAME, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            AppUtils.log(response);
                            AppUtils.hideViews(afieatLoader);
                            // AppUtils.showViews(lvRestaurants);
                            AppUtils.showViews(rvRestaurants);
                            System.out.println();
                            if (response.has("status") && response.optInt("status") == 111) {
                                new AuthkeyValidator(RestaurantListActivity.this).CallForReAuth(new AuthkeyValidator.Authkey() {
                                    @Override
                                    public void Oncomplete() {
                                        loadRestaurantsFromNW();
                                    }
                                });
                            } else {

                                JSONArray restaurantsArray = response.optJSONArray("restaurants");
                                for (int i = 0; i < restaurantsArray.length(); i++) {
                                    JSONObject object = restaurantsArray.optJSONObject(i);
                                    Log.d("!@@ ReTRO- ", object + "");
                                    Restaurant restaurant = new Restaurant();
                                    restaurant.setId(object.optString("merchant_id"));

                                    String name = object.optString("restaurant_name");
                                    restaurant.setName("".equals(name) ? object.optString("branch_name") : name);

                                    String address = object.optString("search_address");
                                    if ("".equals(address)) {
                                        address = object.optString("branch_search_address");
                                    }
                                    if (address.contains(", Iraq")) {
                                        address = address.replace(", Iraq", "");
                                    }
                                    restaurant.setAddress(address);

                                    String cuisine = object.optString("cuisine");
                                    restaurant.setCuisine(cuisine);

                                    String status = object.optString("status");
                                    restaurant.setStatus(status);

                                    if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                                        restaurant.setName(object.optString("restaurant_name_ar"));
                                        restaurant.setCuisine(object.optString("cuisine_ar"));
                                    }

                                    String offerPercentage = object.optString("offer_percentage");
                                    if (!"".equals(offerPercentage)) {
                                        offerPercentage = offerPercentage.split("\\.")[0];
                                    }
                                    if ("null".equals(offerPercentage)) {
                                        offerPercentage = "";
                                    }
                                    restaurant.setDiscount(offerPercentage);

                                    restaurant.setRating(object.optString("present_rating"));
                                    restaurant.setRatingCount(object.optString("review_count"));

                                    String deliveryTime = object.optString("delivery_time");
                                    restaurant.setDeliveryTime(deliveryTime);
                                    restaurant.setProcessingTime(object.optString("processing_time"));

                                    restaurant.setUriThumb(Apis.IMG_PATH + "restaurants/image/thumb_81_81/" + object.optString("merchant_photo_bg"));
                                    restaurants.add(restaurant);
                                }
                                willLoadMore = false;
                                if (restaurantsArray.length() > 0) {
                                    try {
//                                    ((RestaurantsListAdapter) ((HeaderViewListAdapter) lvRestaurants.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                                        ((RestaurantsListAdapter) lvRestaurants.getAdapter()).notifyDataSetChanged();
                                        mAdapterRecyclerView.notifyDataSetChanged();

//                                    lvRestaurants.setAdapter(new RestaurantsListAdapter(restaurants));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
//                                lvRestaurants.removeFooterView(listFooterView);
                                }

                                if (restaurants.size() == 0) {
                                    ((RestaurantsListAdapter) lvRestaurants.getAdapter()).notifyDataSetChanged();
                                    mAdapterRecyclerView.notifyDataSetChanged();
                                    if (!snackNoRestaurants.isShown())
                                        snackNoRestaurants.show();
                                } else {
                                    if (snackNoRestaurants.isShown())
                                        snackNoRestaurants.dismiss();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            AppUtils.hideViews(afieatLoader);
                            if (error.networkResponse == null) {
                                if (error.getClass().equals(TimeoutError.class) || error.getClass().equals(NoConnectionError.class)) {
                                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_server_no_response), Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            snackbar.dismiss();
                                            loadRestaurantsFromNW();
                                        }
                                    });
                                    snackbar.show();
                                }
                            }
                        }
                    }
            );
            appInstance.addToRequestQueue(request);

        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadRestaurantsFromNW();
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }


    private void initFilters() {
        View view = findViewById(R.id.viewFilter);
        assert view != null;

        ivClearFilter = (ImageView) view.findViewById(R.id.ivClearFilter);
        ivClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rgFilter.clearCheck();
                if (filterParams.containsKey("attrb")) {
                    filterParams.remove("attrb");
                }
            }
        });

//        spSortList = (Spinner) view.findViewById(R.id.spSortList);
        spCuisine = (Spinner) view.findViewById(R.id.spCuisine);

        rgSort = (RadioGroup) view.findViewById(R.id.rgSort);

        rgFilter = (RadioGroup) view.findViewById(R.id.rgFilter);
        rbFilterOffer = (RadioButton) view.findViewById(R.id.rbFilterOffer);

        RadioButton rbFilterNewRests;
        rbFilterNewRests = (RadioButton) view.findViewById(R.id.rbFilterNewRests);
        rbFilterRatings = (RadioButton) view.findViewById(R.id.rbFilterRatings);
        rbFilterOpenRests = (RadioButton) view.findViewById(R.id.rbFilterOpenRests);
        rbFilterVeg = (RadioButton) view.findViewById(R.id.rbFilterVeg);

        rgPrice = (RadioGroup) view.findViewById(R.id.rgPrice);

        RadioButton rbHi2Lo;
        rbHi2Lo = (RadioButton) view.findViewById(R.id.rbHi2Lo);
        rbLo2Hi = (RadioButton) view.findViewById(R.id.rbLo2Hi);

        Button btnApplyFilter;

        btnApplyFilter = (Button) view.findViewById(R.id.btnApplyFilter);
        btnApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterParams.clear();
                /*if (spSortList.getSelectedItem().equals("Rating")) {
                    filterParams.put("srt", "rating_count");
                } else {
                    filterParams.put("srt", "review_count");
                }*/
                switch (rgSort.getCheckedRadioButtonId()) {
                    case R.id.rbPopularity:
                        filterParams.put("srt", "review_count");
                        break;
                    case R.id.rbRatings:
                        filterParams.put("srt", "rating_count");
                        break;
                    default:
                        break;
                }
                switch (rgFilter.getCheckedRadioButtonId()) {
                    case R.id.rbFilterOffer:
                        filterParams.put("attrb", "offers");
                        break;
                    case R.id.rbFilterNewRests:
                        filterParams.put("attrb", "new");
                        break;
                    case R.id.rbFilterOpenRests:
                        filterParams.put("attrb", "open");
                        break;
                    default:
                        break;
                }
                switch (rgPrice.getCheckedRadioButtonId()) {
                    case R.id.rbLo2Hi:
                        filterParams.put("pric", "high");
                        break;
                    case R.id.rbHi2Lo:
                        filterParams.put("pric", "low");
                        break;
                    default:
                        break;
                }
                if (!((Cuisine) spCuisine.getSelectedItem()).getId().equals("-1")) {
                    filterParams.put("csn", ((Cuisine) spCuisine.getSelectedItem()).getId());
                }
                mDrawerLayout.closeDrawer(sortView);
                mStartIndex = 0;
                restaurants.clear();
                loadRestaurantsFromNW();

            }
        });
    }

    RadioGroup radio_container;
    private Handler pagerHandler = null;
    private Runnable pagerRunnable = null;
    String big_images_filenames = "";
    private final int PAGER_DURATION = 8000;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
        // overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }

    private void loadCuisineListFromNW() {
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.CUISINE_LIST, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.has("status") && response.optInt("status") == 111) {
                            new AuthkeyValidator(RestaurantListActivity.this).CallForReAuth(new AuthkeyValidator.Authkey() {
                                @Override
                                public void Oncomplete() {
                                    loadCuisineListFromNW();
                                }
                            });
                        } else {
                            JSONArray cuisinesArray = response.optJSONArray("cuisinelist");
                            assert cuisinesArray != null;
                            mCuisines.clear();
                            mCuisines.add(new Cuisine("-1", "All"));
                            int i = 0;
                            while (i < cuisinesArray.length()) {
                                Cuisine cuisine = new Cuisine();
                                JSONObject cuisineObject = cuisinesArray.optJSONObject(i++);
                                cuisine.setId(cuisineObject.optString("cuisine_id"));
                                cuisine.setName(cuisineObject.optString("cuisine_name"));
                                mCuisines.add(cuisine);
                            }
                            spCuisine.setAdapter(new CuisinesAdapter(mCuisines));
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

    private void loadRestaurantsFromNW() {
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {

            AppUtils.showViews(afieatLoader);
            Map<String, String> params = new HashMap<>();
            params.put("cid", mCityId);
            params.put("rid", mRegionId);
            if (GroupID != null)
                params.put("group_id", GroupID);
            params.put("lmt", String.valueOf(QUERY_LIMIT));
            params.put("restofst", String.valueOf(mStartIndex));
            params.putAll(filterParams);
            if (mUserId.length() > 0) params.put("shopuserid", mUserId);
            Log.e("loadRestaurantsFromNW :", params.toString() + ">>>@@@@@@@>>>");
            System.out.println("Rahul : loadRestaurantsFromNW : params : " + params.toString());
            if (noAddrestaurants!=null && !isScrolling) {
                noAddrestaurants.clear();
            }
            isScrolling = false;

            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.DEMOGROPULISTCHECK, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                           // Log.e("")
                            System.out.println("Rahul : loadRestaurantsFromNW : Callled : ");
                            System.out.println("Rahul : loadRestaurantsFromNW : response : " + response);
                            AppUtils.log(response);
                            AppUtils.hideViews(afieatLoader);
                            // AppUtils.showViews(lvRestaurants);
                            AppUtils.showViews(rvRestaurants);


                            if (response.has("status") && response.optInt("status") == 111) {
                                new AuthkeyValidator(RestaurantListActivity.this).CallForReAuth(new AuthkeyValidator.Authkey() {
                                    @Override
                                    public void Oncomplete() {
                                        loadRestaurantsFromNW();
                                    }
                                });
                            } else {

                                JSONArray restaurantsArray = response.optJSONArray("restaurants");
                                for (int i = 0; i < restaurantsArray.length(); i++) {
                                    JSONObject object = restaurantsArray.optJSONObject(i);
                                    Log.d("!@@ ReTRO- ", object + "");
                                    Restaurant restaurant = new Restaurant();
                                    restaurant.setId(object.optString("merchant_id"));

                                    String name = object.optString("restaurant_name");
                                    restaurant.setName("".equals(name) ? object.optString("branch_name") : name);

                                    String address = object.optString("search_address");
                                    if ("".equals(address)) {
                                        address = object.optString("branch_search_address");
                                    }
                                    if (address.contains(", Iraq")) {
                                        address = address.replace(", Iraq", "");
                                    }
                                    restaurant.setAddress(address);

                                    String cuisine = object.optString("cuisine");
                                    restaurant.setCuisine(cuisine);

                                    String status = object.optString("status");
                                    restaurant.setStatus(status);

                                    if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                                        restaurant.setName(object.optString("restaurant_name_ar"));
                                        restaurant.setCuisine(object.optString("cuisine_ar"));
                                    }

                                    String offerPercentage = object.optString("offer_percentage");
                                    if (!"".equals(offerPercentage)) {
                                        offerPercentage = offerPercentage.split("\\.")[0];
                                    }
                                    if ("null".equals(offerPercentage)) {
                                        offerPercentage = "";
                                    }
                                    restaurant.setDiscount(offerPercentage);

                                    restaurant.setRating(object.optString("present_rating"));
                                    restaurant.setRatingCount(object.optString("review_count"));

                                    String deliveryTime = object.optString("delivery_time");
                                    restaurant.setDeliveryTime(deliveryTime);
                                    restaurant.setProcessingTime(object.optString("processing_time"));

                                    restaurant.setUriThumb(Apis.IMG_PATH + "restaurants/image/thumb_81_81/" + object.optString("merchant_photo_bg"));
                                    restaurants.add(restaurant);
                                    noAddrestaurants.add(restaurant);
                                }
                                JSONArray addsArray = response.optJSONArray("adds");
                                if (addsArray != null) {
                                    arrAds = new ArrayList<>();
                                    for (int i = 0; i < addsArray.length(); i++) {
                                        JSONObject object = addsArray.optJSONObject(i);
                                        Restaurant.AddsBean model = new Restaurant.AddsBean();
                                        model.setAdds_id(object.optString("adds_id"));
                                        model.setAds_photo(object.optString("ads_photo"));
                                        model.setCity_ids(object.optString("city_ids"));
                                        model.setDate_created(object.optString("date_created"));
                                        model.setDisplay_page_id(object.optString("display_page_id"));
                                        model.setDate_modified(object.optString("date_modified"));
                                        model.setGroup_ids(object.optString("group_ids"));
                                        model.setIp_address(object.optString("ip_address"));
                                        model.setLink_url(object.optString("link_url"));
                                        model.setRestaurant_id(object.optString("restaurant_id"));
                                        model.setStatus(object.optString("status"));
                                        model.setItem_id(object.optString("item_id"));
                                        model.setSponsored_expiration(object.optString("sponsored_expiration"));
                                        model.setSponsored_start_date(object.optString("sponsored_start_date"));
                                        model.setLink_option(object.optString("link_option"));
                                        model.setLink_type(object.optString("link_type"));
                                        model.setRestaurant_name(object.optString("restaurant_name"));
                                        model.setRestaurant_name_ar(object.optString("restaurant_name_ar"));
                                        model.setMerchant_minimum_order(object.optString("merchant_minimum_order"));
                                        Restaurant resmodel = new Restaurant();
                                        resmodel.setAddsBean(model);
                                        arrAds.add(model);
                                        model = null;
                                        resmodel = null;
                                    }
                                }
                                JSONObject addsPosi = response.optJSONObject("adds_position");
                                if (addsPosi != null) {
                                    first_position_after = addsPosi.optString("first_position_after");
                                    place_interval = addsPosi.optString("place_interval");

                                    arrAdsinterval.clear();
                                    arrAdsinterval.add(Integer.parseInt(first_position_after));
                                    arrAdsinterval.add(Integer.parseInt(first_position_after) + Integer.parseInt(place_interval));
                                    int count = Integer.parseInt(first_position_after) + Integer.parseInt(place_interval);
                                    ;
                                    for (int i = 2; i < noAddrestaurants.size(); i++) {
                                        count = count + Integer.parseInt(place_interval);
                                        arrAdsinterval.add(count);
                                    }
                                    restaurants.clear();
                                    restaurants.addAll(noAddrestaurants);
                                }

                                if (arrAds != null && arrAds.size() > 0) {
                                    for (int i = 0; i < arrAdsinterval.size(); i++) {
                                       /* Restaurant.AddsBean model = new Restaurant.AddsBean();
                                        model = arrAds.get(0);
                                        Restaurant resmodel = new Restaurant();
                                        resmodel.setAddsBean(model);*/
                                        if (arrAdsinterval.get(i) < restaurants.size() && restaurants.get(arrAdsinterval.get(i)).getAddsBean() == null) {
                                            Restaurant.AddsBean model = new Restaurant.AddsBean();
                                            Restaurant resmodel = new Restaurant();
                                            if (indexExists(arrAds, i)) {
                                                model = arrAds.get(i);
                                                resmodel.setAddsBean(model);
                                                restaurants.add(arrAdsinterval.get(i), resmodel);
                                            }

                                        }
                                    }
                                }

                                /*for(int i=0;i<res.getAdds().size();i++){
                                    Restaurant.AddsBean model=new Restaurant.AddsBean();
                                    model.setAdds_id(res.getAdds().get(i).getAdds_id());
                                    model.setAds_photo(res.getAdds().get(i).getAds_photo());
                                    model.setCity_ids(res.getAdds().get(i).getCity_ids());
                                    model.setDate_created(res.getAdds().get(i).getDate_created());
                                    model.setDisplay_page_id(res.getAdds().get(i).getDisplay_page_id());
                                    model.setDate_modified(res.getAdds().get(i).getDate_modified());
                                    model.setGroup_ids(res.getAdds().get(i).getGroup_ids());
                                    model.setIp_address(res.getAdds().get(i).getIp_address());
                                    model.setIs_appadds(res.getAdds().get(i).getIs_appadds());
                                    model.setLink_url(res.getAdds().get(i).getLink_url());
                                    model.setRestaurant_id(res.getAdds().get(i).getRestaurant_id());
                                    model.setStatus(res.getAdds().get(i).getStatus());
                                    model.setItem_id(res.getAdds().get(i).getItem_id());
                                    model.setSponsored_expiration(res.getAdds().get(i).getSponsored_expiration());
                                    model.setSponsored_start_date(res.getAdds().get(i).getSponsored_start_date());
                                    Restaurant resmodel=new Restaurant();
                                    resmodel.setAddsBean(model);
                                    restaurants.add(resmodel);

                                }*/
                                willLoadMore = false;
                                if (restaurantsArray.length() > 0) {
                                    try {

//                                    ((RestaurantsListAdapter) ((HeaderViewListAdapter) lvRestaurants.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                                        ((RestaurantsListAdapter) lvRestaurants.getAdapter()).notifyDataSetChanged();
                                        System.out.println("Rahul : RestaurentListAdapterRecyclerView : notified : restaurantsArray.length() : " + restaurantsArray.length());
                                        mAdapterRecyclerView.notifyDataSetChanged();
//                                    lvRestaurants.setAdapter(new RestaurantsListAdapter(restaurants));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
//                                lvRestaurants.removeFooterView(listFooterView);
                                }

                                if (restaurants.size() == 0) {
                                    System.out.println("Rahul : RestaurentListAdapterRecyclerView : notified : restaurantsArray.length() : " + restaurantsArray.length());

                                    ((RestaurantsListAdapter) lvRestaurants.getAdapter()).notifyDataSetChanged();
                                    mAdapterRecyclerView.notifyDataSetChanged();
                                    if (!snackNoRestaurants.isShown())
                                        snackNoRestaurants.show();
                                } else {
                                    if (snackNoRestaurants.isShown())
                                        snackNoRestaurants.dismiss();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                         /*   System.out.println("Rahul : loadRestaurantsFromNW : error : " + error.getMessage());

                            System.out.println("Rahul : loadRestaurantsFromNW : error : " + error.getStackTrace());*/
                            AppUtils.hideViews(afieatLoader);
                            if (error.networkResponse == null) {
                                if (error.getClass().equals(TimeoutError.class) || error.getClass().equals(NoConnectionError.class)) {
                                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_server_no_response), Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            snackbar.dismiss();
                                            loadRestaurantsFromNW();
                                        }
                                    });
                                    snackbar.show();
                                }
                            }
                        }
                    }
            );
            appInstance.addToRequestQueue(request);

        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadRestaurantsFromNW();
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    private void checkForLoggedInStatus() {

        List<String> navList = new ArrayList<>(Arrays.asList(navItems));
        List<Integer> navIconsList = new ArrayList<>();
        for (int navIcon : navIcons) {
            navIconsList.add(navIcon);
        }
        mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);
        View userPanel = findViewById(R.id.navHeader);
        assert userPanel != null;
        TextView tvUsername = (TextView) userPanel.findViewById(R.id.tvUsername);
        TextView tvUserEmail = (TextView) userPanel.findViewById(R.id.tvUserEmail);
        AppUtils.log("UserId: " + mUserId);

        if (mUserId.length() > 0) {
            // logged in

            // Sunit 27-01-2017


            if (!navList.contains("Sign Out") && !navList.contains("خروج")) {
                AppUtils.log("test11");

                if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    navList.add("خروج");

                } else {

                    navList.add("Sign Out");

                    AppUtils.log("test11-33");
                }

                navIconsList.add(R.drawable.nav_signout);


                AppUtils.showViews(userPanel);
                ((TextView) userPanel.findViewById(R.id.LogInBtn)).setVisibility(View.GONE);
                tvUsername.setText(appInstance.getFromSharedPref(AppUtils.PREF_USER_FNAME));
                tvUserEmail.setText(appInstance.getFromSharedPref(AppUtils.PREF_USER_EMAIL));
            }
        } else {

            // not loggeed in
            //     AppUtils.hideViews(userPanel);

            if (navList.contains("Sign Out") || navList.contains("خروج")) {
                AppUtils.log("test22");
                if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    navList.remove(navList.indexOf("خروج"));
                    navList.remove(navList.indexOf("عروضي"));
                    navList.remove(navList.indexOf("حسابي"));
                    AppUtils.log("test11-44");
                } else {
                    navList.remove(navList.indexOf("Sign Out"));
                    navList.remove(navList.indexOf("My Account"));
                    navList.remove(navList.indexOf("My Deals"));

                    AppUtils.log("test11-55");
                }
                navIconsList.remove(navIconsList.indexOf(R.drawable.nav_account));
                navIconsList.remove(navIconsList.indexOf(R.drawable.nav_deal));

                AppUtils.showViews(userPanel);
                ((TextView) userPanel.findViewById(R.id.LogInBtn)).setVisibility(View.VISIBLE);
                tvUsername.setVisibility(View.GONE);
                tvUserEmail.setVisibility(View.GONE);
            } else {

                if (navList.contains("My Account") || navList.contains("حسابي")) {
                    if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                        navList.remove(navList.indexOf("عروضي"));
                        navList.remove(navList.indexOf("حسابي"));
                    } else {
                        navList.remove(navList.indexOf("My Account"));
                        navList.remove(navList.indexOf("My Deals"));
                    }

                    navIconsList.remove(navIconsList.indexOf(R.drawable.nav_account));
                    navIconsList.remove(navIconsList.indexOf(R.drawable.nav_deal));
                }


            }
        }

        navItems = new String[navList.size()];
        navIcons = new int[navIconsList.size()];
        for (int i = 0; i < navList.size(); i++) {
            navItems[i] = navList.get(i);
            navIcons[i] = navIconsList.get(i);
        }
        AppUtils.log("Changing menu");
        ((NavListAdapter) lvNav.getAdapter()).notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAnimalItemClick(int pos, String ImageURl, ImageView shareImageView, String restaurantId, String mResNAme) {
        System.out.println("Rahul : RestaurantListActivity : onAnimalItemClick : " + pos + "," + ImageURl + "," + shareImageView);
        //ImageView testImae=findViewById(R.id.testImae);


        Intent intent = new Intent(RestaurantListActivity.this, RestaurantsDetailActivity.class);
        intent.putExtra(EXTRA_ANIMAL_ITEM, ImageURl);
        intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, restaurantId);
        intent.putExtra(AppUtils.EXTRA_GROUP_ID, GroupID);
        intent.putExtra(EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(shareImageView));
/*

        AppUtils.CURRENT_RESTAURANT_NAME="";
        AppUtils.CURRENT_RESTAURANT_NAME=mResNAme;
*/

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                RestaurantListActivity.this,
                shareImageView,
                ViewCompat.getTransitionName(shareImageView));

        startActivity(intent, options.toBundle());
    }

    /*@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Transition enterTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(2000);

        return bounds;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Transition returnTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setInterpolator(new DecelerateInterpolator());
        bounds.setDuration(2000);

        return bounds;
    }*/

    @Override
    public void onBottomReached(int position) {

        System.out.println("Rahul : onBottomReached : position :");
        if (!willLoadMore) {
            willLoadMore = true;
            mStartIndex += QUERY_LIMIT;
            //  mStartIndex=mStartIndex+res.getAdds().size();

            if (isSearch != null && "item".equals(isSearch)) {
                loadRestaurantBySearchItems(search_item_name);
                return;
            }
            if (isSearch != null && "cuisine".equals(isSearch)) {
                loadRestaurantBySearchCuisine(search_cuisine_id);
                return;
            }


            loadRestaurantsFromNW();
        }
    }

    public void onBottomReachedR(int position) {
        System.out.println("Rahul : onBottomReached : position :" + position);
        if (!willLoadMore) {
            willLoadMore = true;
            mStartIndex += QUERY_LIMIT;
            //  mStartIndex=mStartIndex+res.getAdds().size();
            if (isSearch != null && "item".equals(isSearch)) {
                loadRestaurantBySearchItems(search_item_name);
                return;
            }
            if (isSearch != null && "cuisine".equals(isSearch)) {
                loadRestaurantBySearchCuisine(search_cuisine_id);
                return;
            }

            isScrolling = true;
            loadRestaurantsFromNW();
        }

    }

    private class NavMenuItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            mDrawerLayout.closeDrawer(mNavigationView);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    switch (position) {
                        case 0:
                            //TODO: Change preferred address
                            intent = new Intent(RestaurantListActivity.this, AddressSelectionActivity.class);
                            startActivity(intent);
                            break;
                        case 2:
                            startActivity(new Intent(RestaurantListActivity.this, OrdersActivity.class));
                            break;
                        case 3:
                            //TODO: Account
                            if (mUserId.length() > 0) {
                                intent = new Intent(RestaurantListActivity.this, ProfileActivity.class);
                                startActivity(intent);
                            } else {
                                intent = new Intent(RestaurantListActivity.this, CheckInActivity.class);
                                startActivityForResult(intent, REQUEST_CHECKIN);
                            }
                            break;
                        case 4:
                            intent = new Intent(RestaurantListActivity.this, WalletActivity.class);
                            startActivity(intent);
                            break;
                        case 5:
                            if (mUserId.length() > 0) {
                                intent = new Intent(RestaurantListActivity.this, ReviewsActivity.class);
                                startActivity(intent);
                            } else {
                                intent = new Intent(RestaurantListActivity.this, LanguageSelectionActivity.class);
                                startActivity(intent);
                            }

                            break;
                        case 6:
                            if (mUserId.length() > 0) {
                                intent = new Intent(RestaurantListActivity.this, DealsActivity.class);
                                startActivity(intent);
                            } else {
                                intent = new Intent(RestaurantListActivity.this, HelpCenterActivity.class);
                                startActivity(intent);
                            }

                            break;
                        case 7:
                            if (mUserId.length() > 0) {
                                startActivityForResult(new Intent(RestaurantListActivity.this, LanguageSelectionActivity.class), AppUtils.REQ_CHANGE_LANG);
                            } else {
                                startActivityForResult(new Intent(RestaurantListActivity.this, HelpCenterActivity.class), AppUtils.REQ_CHANGE_LANG);
                            }

                            break;
                        case 8:
                            startActivityForResult(new Intent(RestaurantListActivity.this, HelpCenterActivity.class), AppUtils.REQ_CHANGE_LANG);
                            break;
                        case 9:
                            //TODO: Sign Out
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, "");
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(false));
                            checkForLoggedInStatus();
                            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_logout_success), Snackbar.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();
                            break;
                        default:
                            break;
                    }
                }
            }, 250);
        }
    }

    private class RestaurantListScrollListener extends RecyclerView.OnScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 1) {
                if (!willLoadMore) {
                    willLoadMore = true;
                    mStartIndex += QUERY_LIMIT;

                    if (isSearch != null && "items".equals(isSearch)) {
                        loadRestaurantBySearchItems(search_item_name);
                        return;
                    }
                    if (isSearch != null && "cuisine".equals(isSearch)) {
                        loadRestaurantBySearchCuisine(search_cuisine_id);
                        return;
                    }

                    loadRestaurantsFromNW();
                }
            }
        }
    }

    private class RestaurantListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Restaurant restaurant = restaurants.get(position);
            Intent intent = new Intent(RestaurantListActivity.this, RestaurantsDetailActivity.class);
            intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, restaurant.getId());
            intent.putExtra(AppUtils.EXTRA_GROUP_ID, GroupID);
            startActivity(intent);
        }
    }

    public void RestaurantListItemClick(String resId, ImageView mImageView, Uri logoPath) {
        Intent intent = new Intent(RestaurantListActivity.this, RestaurantsDetailActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(RestaurantListActivity.this,
                        mImageView,
                        ViewCompat.getTransitionName(mImageView));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, resId);
            intent.putExtra("logoPath", logoPath.toString());
            intent.putExtra(AppUtils.EXTRA_GROUP_ID, GroupID);
            startActivity(intent, options.toBundle());
        }

       /* Intent intent = new Intent(RestaurantListActivity.this, RestaurantsDetailActivity.class);
        intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, resId);
        intent.putExtra(AppUtils.EXTRA_GROUP_ID, GroupID);
        startActivity(intent);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        itemfilter = menu.findItem(R.id.menu_filter);
        itemnotificatin = menu.findItem(R.id.menu_notification);
        itemsearch = menu.findItem(R.id.menu_search);

        if (isSearch != null && "items".equals(isSearch)) {
            itemsearch.setVisible(false);
            itemfilter.setVisible(false);
            itemnotificatin.setVisible(false);
        } else if (isSearch != null && "cuisine".equals(isSearch)) {
            itemsearch.setVisible(false);
            itemfilter.setVisible(false);
            itemnotificatin.setVisible(false);
        } else {

            itemsearch.setVisible(false);
            itemfilter.setVisible(true);
            itemnotificatin.setVisible(false);
        }


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_search:
                startActivity(new Intent(RestaurantListActivity.this, SearchActivity.class));
                return true;
            case R.id.menu_notification:
                startActivity(new Intent(RestaurantListActivity.this, NotificationActivity.class));
                return true;
            case R.id.menu_filter:
                if (sortView.isShown()) mDrawerLayout.closeDrawer(sortView);
                else mDrawerLayout.openDrawer(sortView);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
                    AppUtils.hideViews(lvRestaurants);
                    // AppUtils.hideViews(rvRestaurants);
                    mRegionId = appInstance.getFromSharedPref(AppUtils.PREF_REGION_ID);
                    mRegionName = appInstance.getFromSharedPref(AppUtils.PREF_REGION);
                    mCityName = appInstance.getFromSharedPref(AppUtils.PREF_CITY);
                    mCityId = appInstance.getFromSharedPref(AppUtils.PREF_CITY_ID);
                    mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);
                    if (mCityName.length() > 0)
                        mToolbar.setTitle(mCityName);
                    else
                        mToolbar.setTitle(getString(R.string.iraq));
                    if (mRegionName.length() > 0)
                        mToolbar.setSubtitle(mRegionName);
                    else
                        mToolbar.setSubtitle(getString(R.string.tap_to_select_city));
                    restaurants.clear();
                    mStartIndex = 0;

                }
                break;

            case REQUEST_CHECKIN:
                if (resultCode == RESULT_OK) {
                    restaurants.clear();
                    mStartIndex = 0;
//                    checkForLoggedInStatus();
                    loadRestaurantsFromNW();
                }
                break;

            case AppUtils.REQ_CHANGE_LANG:
                if (resultCode == RESULT_OK) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }

                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForLoggedInStatus();
        if (AppUtils.IS_CART_VISIBLE.equalsIgnoreCase("")) {
            fetchUndeliveredOrder();


        }

    }

    class NavListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return navItems.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String item = navItems[position];
            int drawable = navIcons[position];
           /* TextView view = (TextView) LayoutInflater.from(RestaurantListActivity.this).inflate(R.layout.layout_nav_list_item, null, false);
            view.setText(item);
            if (position == 1)
                view.setTextColor(Color.parseColor("#C63A2B"));
            view.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
            view.setCompoundDrawablePadding(55);*/

            View viewNavList = LayoutInflater.from(RestaurantListActivity.this).inflate(R.layout.layout_nav_list_item_new, null, false);
            //TextView view = (TextView) LayoutInflater.from(LocationActivity.this).inflate(R.layout.layout_nav_list_item, null, false);

            TextView tvMenuTitle = viewNavList.findViewById(R.id.tvMenuTitle);
            ImageView img = viewNavList.findViewById(R.id.img);

            if (position == 1)
                tvMenuTitle.setTextColor(Color.parseColor("#C63A2B"));


            tvMenuTitle.setText(item);
            img.setBackgroundResource(drawable);
            return viewNavList;
        }
    }


  /*  OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            //Here adding null object to last position,check the condition in getItemViewType() method,if object is null then display progress
            dataModels.add(null);
            adapter.notifyItemInserted(dataModels.size() - 1);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dataModels.remove(dataModels.size() - 1);
                    adapter.notifyItemRemoved(dataModels.size());

                    // Call you API, then update the result into dataModels, then call adapter.notifyDataSetChanged().
                    //Update the new data into list object
                    loadData();
                    adapter.notifyDataSetChanged();
                    loading = false;
                }
            }, 1000);
        }
    };*/


    private void getAds() {
        try {
            ApiInterface apiService = ApiClient.GetClient().create(ApiInterface.class);
            Call<AdsModel> call = apiService.get_Ads(AppInstance.getInstance(RestaurantListActivity.this).getAuthkey(), AppUtils.AUTHRIZATIONKEY);
            call.enqueue(new Callback<AdsModel>() {
                @Override
                public void onResponse(Call<AdsModel> call, retrofit2.Response<AdsModel> response) {
                    if (response != null) {
                        Log.e("RESPONSE>>>>", response.body().toString() + ">>>>>>>>>>>>>>");
                        System.out.println("VERSION CHECK : " + response.body().toString());
                        res = response.body();

                        if (res != null && res.getStatus() == 1) {

                        }
                    }

                }

                @Override
                public void onFailure(Call<AdsModel> call, Throwable t) {
                    System.out.print(t.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean indexExists(final ArrayList list, final int index) {
        return index >= 0 && index < list.size();
    }

    public void fetchUndeliveredOrder() {

        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("user_id", AppInstance.getInstance(this).getFromSharedPref(AppUtils.PREF_USER_ID));
            ApiInterface apiService = ApiClient.GetClient().create(ApiInterface.class);
            Call<List<InProcessOrders>> call = apiService.getUndeliveredOrders(AppInstance.getInstance(this).getAuthkey(), AppUtils.AUTHRIZATIONKEY, AppInstance.getInstance(this).getFromSharedPref(AppUtils.PREF_USER_ID));
            call.enqueue(new Callback<List<InProcessOrders>>() {
                @Override
                public void onResponse(Call<List<InProcessOrders>> call, retrofit2.Response<List<InProcessOrders>> response) {
                    if (response != null) {

                        arrInProcessOrders = (ArrayList<InProcessOrders>) response.body();

                        db = new DBHelper(RestaurantListActivity.this);
                        DBitemCount = db.getFoodsBasket(AppInstance.getInstance(RestaurantListActivity.this).getFromSharedPref(AppUtils.PREF_USER_ID)).size();

                        if (DBitemCount > 0 || arrInProcessOrders.size() > 0) {
                            orderCartView.setVisibility(View.VISIBLE);
                            mPager.setAdapter(new slidingImageAdapter(RestaurantListActivity.this, arrInProcessOrders));
                            indicator.setViewPager(mPager);
                        } else {
                            orderCartView.setVisibility(View.GONE);
                        }

                    }


                }

                @Override
                public void onFailure(Call<List<InProcessOrders>> call, Throwable t) {
                    System.out.print(t.toString());


                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideCartView() {
        orderCartView.setVisibility(View.GONE);
    }
}
