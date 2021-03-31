package com.afieat.ini;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.RequiresApi;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afieat.ini.adapters.CuisinesAdapter;
import com.afieat.ini.adapters.FavouriteListAdapter;
import com.afieat.ini.adapters.RestaurantsListAdapter;
import com.afieat.ini.fragments.CrazyDealsFragment;
import com.afieat.ini.fragments.SimplePhotoFragment;
import com.afieat.ini.interfaces.FavouriteListner;
import com.afieat.ini.interfaces.OnBottomReachedListener;
import com.afieat.ini.interfaces.OnRestaurantPhotoClicked;
import com.afieat.ini.interfaces.SharedAnimItemClickListener;
import com.afieat.ini.models.Cuisine;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class FavouriteListActivity extends AppCompatActivity implements SharedAnimItemClickListener, OnBottomReachedListener, FavouriteListner {

    private Toolbar mToolbar;
    private ListView lvRestaurants;
    private RecyclerView rvRestaurants;
    private FavouriteListAdapter mAdapterRecyclerView;
    private ListView lvNav;
    private NavigationView mNavigationView, sortView;
    private DrawerLayout mDrawerLayout;
    private ProgressBar progressBar;
    GifImageView afieatLoader;
//    private View listFooterView;

    public static final String EXTRA_ANIMAL_ITEM = "image_url";
    public static final String EXTRA_ANIMAL_IMAGE_TRANSITION_NAME = "image_transition_name";
    private ImageView ivClearFilter;
    private Spinner spSortList, spCuisine;
    private Button btnApplyFilter;
    private RadioGroup rgFilter, rgPrice, rgSort;
    private RadioButton rbFilterOffer, rbFilterNewRests, rbFilterRatings, rbFilterOpenRests, rbFilterVeg;
    private RadioButton rbLo2Hi, rbHi2Lo;
    private LayoutInflater inflater = null;

    private Map<String, String> filterParams;

    private static int mStartIndex;
    private boolean willLoadMore;
    private String mUserId, mRegionId, mRegionName, mCityName, mCityId, GroupID;
    private List<Restaurant> restaurants;
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


    MenuItem itemfilter, itemnotificatin, itemsearch;
    private String search_item_name = "";
    private String isSearch = "";
    private String search_cuisine_id = "";

    public FavouriteListActivity() {
        mStartIndex = 0;
        willLoadMore = false;
        restaurants = new ArrayList<>();
        mCuisines = new ArrayList<>();
        filterParams = new HashMap<>();
    }

    //-------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        //       Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        System.out.println("Rahul : RestaurantListActivity : ");
        appInstance = AppInstance.getInstance(getApplicationContext());

        String lang = appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG);
        if ("ar".equals(lang)) {
            navItems = navItems_ar;
        }

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
        else
            mToolbar.setSubtitle(getString(R.string.tap_to_select_city));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setSubtitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavouriteListActivity.this, LocationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(AppUtils.EXTRA_REQ_LOCATION, String.valueOf(true));
//                startActivityForResult(intent, REQUEST_LOCATION);
                startActivity(intent);
                finish();
            }
        });
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
        mAdapterRecyclerView = new FavouriteListAdapter(getApplicationContext(), restaurants, FavouriteListActivity.this, FavouriteListActivity.this, this, this);

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
                Intent intent = new Intent(FavouriteListActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        //   checkForLoggedInStatus();
        loadCuisineListFromNW();
        filterParams.put("pric", "high");
        filterParams.put("srt", "review_count");


        loadFav();


    }


    private void removeFavourites(String arg_merchant_id, final List<Restaurant> restaurants, final int position) {
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {

            AppUtils.showViews(afieatLoader);
            Map<String, String> params = new HashMap<>();
            params.put("user_id", mUserId);
            params.put("action_flag", "remove");
            params.put("merchant_id", arg_merchant_id);

            System.out.println("Rahul : FavouriteListActivity : removeFavourites : params : " + params.toString());
            Log.e("Parameter", params.toString() + ">>>@@@@@@@>>>");

            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.ADD_REMOVE_TO_FAVOURITE, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            restaurants.remove(position);
                            mAdapterRecyclerView.notifyItemChanged(position);
                            mAdapterRecyclerView.notifyItemRangeChanged(position, restaurants.size());

                            Snackbar snackbar = Snackbar
                                    .make((RelativeLayout) findViewById(R.id.page), "Removed Successfully", Snackbar.LENGTH_LONG);

                            if (mAdapterRecyclerView.getItemCount() == 0) {
                                TextView noFavourites = findViewById(R.id.noFavourites);
                                noFavourites.setVisibility(View.VISIBLE);
                            }
                            snackbar.show();
                            System.out.println("FavouriteListActivity : response : " + response.toString());
                            AppUtils.log(response);
                            AppUtils.hideViews(afieatLoader);
                            // AppUtils.showViews(lvRestaurants);


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            AppUtils.hideViews(afieatLoader);
                            if (error.networkResponse == null && (error.getClass().equals(TimeoutError.class) || error.getClass().equals(NoConnectionError.class))) {

                                final Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_server_no_response), Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        snackbar.dismiss();

                                    }
                                });
                                snackbar.show();

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
                    loadFav();
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
        rbFilterNewRests = (RadioButton) view.findViewById(R.id.rbFilterNewRests);
        rbFilterRatings = (RadioButton) view.findViewById(R.id.rbFilterRatings);
        rbFilterOpenRests = (RadioButton) view.findViewById(R.id.rbFilterOpenRests);
        rbFilterVeg = (RadioButton) view.findViewById(R.id.rbFilterVeg);

        rgPrice = (RadioGroup) view.findViewById(R.id.rgPrice);
        rbHi2Lo = (RadioButton) view.findViewById(R.id.rbHi2Lo);
        rbLo2Hi = (RadioButton) view.findViewById(R.id.rbLo2Hi);

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
                        break;}
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
                        break;}
                switch (rgPrice.getCheckedRadioButtonId()) {
                    case R.id.rbLo2Hi:
                        filterParams.put("pric", "high");
                        break;
                    case R.id.rbHi2Lo:
                        filterParams.put("pric", "low");
                        break;
                    default:
                        break;}
                if (!((Cuisine) spCuisine.getSelectedItem()).getId().equals("-1")) {
                    filterParams.put("csn", ((Cuisine) spCuisine.getSelectedItem()).getId());
                }
                mDrawerLayout.closeDrawer(sortView);
                mStartIndex = 0;
                restaurants.clear();
                loadFav();

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
                            new AuthkeyValidator(FavouriteListActivity.this).CallForReAuth(new AuthkeyValidator.Authkey() {
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

    private void loadFav() {
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {

            AppUtils.showViews(afieatLoader);
            Map<String, String> params = new HashMap<>();
            params.put("user_id", mUserId);
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

            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.FAVOURITE, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("Rahul : Favourite : Callled : ");
                            System.out.println("Rahul : Favourite : response : " + response);
                            AppUtils.log(response);
                            AppUtils.hideViews(afieatLoader);
                            // AppUtils.showViews(lvRestaurants);
                            AppUtils.showViews(rvRestaurants);


                            if (response.has("status") && response.optInt("status") == 0) {
                                new AuthkeyValidator(FavouriteListActivity.this).CallForReAuth(new AuthkeyValidator.Authkey() {
                                    @Override
                                    public void Oncomplete() {
                                        //  loadFav();

                                        if (mStartIndex == 0) {
                                            TextView noFavourites = findViewById(R.id.noFavourites);
                                            noFavourites.setVisibility(View.VISIBLE);

                                        }

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
                            /*System.out.println("Rahul : loadRestaurantsFromNW : error : " + error.getMessage());

                            System.out.println("Rahul : loadRestaurantsFromNW : error : " + error.getStackTrace());*/
                            AppUtils.hideViews(afieatLoader);
                            if (error.networkResponse == null && (error.getClass().equals(TimeoutError.class) || error.getClass().equals(NoConnectionError.class))) {

                                final Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_server_no_response), Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        snackbar.dismiss();
                                        loadFav();
                                    }
                                });
                                snackbar.show();

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
                    loadFav();
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
        ImageURl = ImageURl.replace("thumb_81_81", "thumb_300_300");
        Intent intent = new Intent(FavouriteListActivity.this, RestaurantsDetailActivity.class);
        intent.putExtra(EXTRA_ANIMAL_ITEM, ImageURl);
        intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, restaurantId);
        intent.putExtra("logoPath", ImageURl);
        intent.putExtra(AppUtils.EXTRA_GROUP_ID, GroupID);
        intent.putExtra(EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(shareImageView));

     /*   AppUtils.CURRENT_RESTAURANT_NAME = "";
        AppUtils.CURRENT_RESTAURANT_NAME = mResNAme;
*/
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                FavouriteListActivity.this,
                shareImageView,
                ViewCompat.getTransitionName(shareImageView));

        startActivity(intent, options.toBundle());
    }

    @Override
    public void onBottomReached(int position) {

        System.out.println("Rahul : onBottomReached : position :");
        if (!willLoadMore) {
            willLoadMore = true;
            mStartIndex += QUERY_LIMIT;

         /*   if (isSearch != null && isSearch.equals("items")) {
                loadRestaurantBySearchItems(search_item_name);
                return;
            }
            if (isSearch != null && isSearch.equals("cuisine")) {
                loadRestaurantBySearchCuisine(search_cuisine_id);
                return;
            }*/

            loadFav();
        }
    }

    public void onBottomReachedR() {
        System.out.println("Rahul : onBottomReached : position :");
        if (!willLoadMore) {
            willLoadMore = true;
            mStartIndex += QUERY_LIMIT;

           /* if (isSearch != null && isSearch.equals("items")) {
                loadRestaurantBySearchItems(search_item_name);
                return;
            }
            if (isSearch != null && isSearch.equals("cuisine")) {
                loadRestaurantBySearchCuisine(search_cuisine_id);
                return;
            }*/

            loadFav();
        }
    }

    @Override
    public void onFavClickListner(Restaurant mRestaurant, List<Restaurant> restaurants, int position) {


        removeFavourites(mRestaurant.getId(), restaurants, position);

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
                            intent = new Intent(FavouriteListActivity.this, AddressSelectionActivity.class);
                            startActivity(intent);
                            break;
                        case 2:
                            startActivity(new Intent(FavouriteListActivity.this, OrdersActivity.class));
                            break;
                        case 3:
                            //TODO: Account
                            if (mUserId.length() > 0) {
                                intent = new Intent(FavouriteListActivity.this, ProfileActivity.class);
                                startActivity(intent);
                            } else {
                                intent = new Intent(FavouriteListActivity.this, CheckInActivity.class);
                                startActivityForResult(intent, REQUEST_CHECKIN);
                            }
                            break;
                        case 4:
                            intent = new Intent(FavouriteListActivity.this, WalletActivity.class);
                            startActivity(intent);
                            break;
                        case 5:
                            if (mUserId.length() > 0) {
                                intent = new Intent(FavouriteListActivity.this, ReviewsActivity.class);
                                startActivity(intent);
                            } else {
                                intent = new Intent(FavouriteListActivity.this, LanguageSelectionActivity.class);
                                startActivity(intent);
                            }

                            break;
                        case 6:
                            if (mUserId.length() > 0) {
                                intent = new Intent(FavouriteListActivity.this, DealsActivity.class);
                                startActivity(intent);
                            } else {
                                intent = new Intent(FavouriteListActivity.this, HelpCenterActivity.class);
                                startActivity(intent);
                            }

                            break;
                        case 7:
                            if (mUserId.length() > 0) {
                                startActivityForResult(new Intent(FavouriteListActivity.this, LanguageSelectionActivity.class), AppUtils.REQ_CHANGE_LANG);
                            } else {
                                startActivityForResult(new Intent(FavouriteListActivity.this, HelpCenterActivity.class), AppUtils.REQ_CHANGE_LANG);
                            }

                            break;
                        case 8:
                            startActivityForResult(new Intent(FavouriteListActivity.this, HelpCenterActivity.class), AppUtils.REQ_CHANGE_LANG);
                            break;
                        case 9:
                            //TODO: Sign Out
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, "");
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(false));
                            checkForLoggedInStatus();
                            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_logout_success), Snackbar.LENGTH_SHORT).show();
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

                /*    if (isSearch != null && isSearch.equals("items")) {
                        loadRestaurantBySearchItems(search_item_name);
                        return;
                    }
                    if (isSearch != null && isSearch.equals("cuisine")) {
                        loadRestaurantBySearchCuisine(search_cuisine_id);
                        return;
                    }*/

                    loadFav();
                }
            }
        }
    }

    private class RestaurantListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Restaurant restaurant = restaurants.get(position);
            Intent intent = new Intent(FavouriteListActivity.this, RestaurantsDetailActivity.class);
            intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, restaurant.getId());
            intent.putExtra(AppUtils.EXTRA_GROUP_ID, GroupID);
            startActivity(intent);
        }
    }

    public void RestaurantListItemClick(String resId, ImageView mImageView, Uri logoPath) {
        Intent intent = new Intent(FavouriteListActivity.this, RestaurantsDetailActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(FavouriteListActivity.this,
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
        itemfilter = menu.findItem(R.id.menu_filter).setVisible(false);
        itemfilter.setVisible(false);
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
            itemfilter.setVisible(false);
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
                startActivity(new Intent(FavouriteListActivity.this, SearchActivity.class));
                return true;
            case R.id.menu_notification:
                startActivity(new Intent(FavouriteListActivity.this, NotificationActivity.class));
                return true;
            case R.id.menu_filter:
                if (sortView.isShown()) mDrawerLayout.closeDrawer(sortView);
                else mDrawerLayout.openDrawer(sortView);
                return true;
            default:
                break;  }
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
                    loadFav();
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
                break;}
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForLoggedInStatus();
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

            View viewNavList = LayoutInflater.from(FavouriteListActivity.this).inflate(R.layout.layout_nav_list_item_new, null, false);
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
}
