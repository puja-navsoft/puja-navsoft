package com.afieat.ini;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.afieat.ini.adapters.CuisinesAdapter;
import com.afieat.ini.adapters.RestaurantsAdapter;
import com.afieat.ini.interfaces.OnLoadMoreListener;
import com.afieat.ini.interfaces.OnRecyclerItemClickListener;
import com.afieat.ini.misc.DividerItemDecoration;
import com.afieat.ini.models.Cuisine;
import com.afieat.ini.models.Restaurant;
import com.afieat.ini.models.RestaurantsFilter;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
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
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private AppInstance appInstance;
    private NavigationView mNavigationView, sortView;
    private DrawerLayout mDrawerLayout;
    private ListView lvNav;
    private RecyclerView rvRestaurants;
    private TextView tvNoRestaurants;


    private Spinner spCuisine;

    private RadioGroup rgFilter;
    private RadioButton rbFilterOffer,
            rbFilterRatings,
            rbFilterOpenRests,
            rbFilterVeg;


    private int startIndex;
    private boolean mLoadMoreReqtd;
    private String mUserId;
    private String mRegionId, mRegionName, mCityName, mCityId;
    private String[] navItems = {"Address", "Restaurants", "My Orders", "My Account", "My Wallet", "My Reviews"};
    private int[] navIcons = {
            R.drawable.nav_address,
            R.drawable.nav_restaurants,
            R.drawable.nav_orders,
            R.drawable.nav_account,
            R.drawable.nav_wallet,
            R.drawable.nav_star,
            /*R.drawable.nav_settings,*/
            /*R.drawable.nav_contact,*/
            /*R.drawable.nav_help,*/
            R.drawable.nav_signout
    };

    private List<Restaurant> mRestaurants;
    private List<Cuisine> mCuisines;

    private RestaurantsFilter oldFilter, newFilter;
    private Map<String, String> filterParams;

    private final int REQUEST_LOCATION = 100;
    private final int REQUEST_CHECKIN = 101;

    private final int PAGE_SIZE = 7;

    public MainActivity() {
        int numRestrnts;
        mRestaurants = new ArrayList<>();
        mCuisines = new ArrayList<>();
        filterParams = new HashMap<>();
        oldFilter = new RestaurantsFilter();
        newFilter = new RestaurantsFilter();
        mLoadMoreReqtd = false;
        numRestrnts = 0;
        startIndex = 0;
        mUserId = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        //      Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appInstance = AppInstance.getInstance(getApplicationContext());
        mRegionId = appInstance.getFromSharedPref(AppUtils.PREF_REGION_ID);
        mRegionName = appInstance.getFromSharedPref(AppUtils.PREF_REGION);
        mCityName = appInstance.getFromSharedPref(AppUtils.PREF_CITY);
        mCityId = appInstance.getFromSharedPref(AppUtils.PREF_CITY_ID);
        mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);

        checkForLoggedInStatus();

        mToolbar = (Toolbar) findViewById(R.id.appbar);
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
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                intent.putExtra(AppUtils.EXTRA_REQ_LOCATION, String.valueOf(true));
                startActivityForResult(intent, REQUEST_LOCATION);
            }
        });
        setSupportActionBar(mToolbar);

        lvNav = (ListView) findViewById(R.id.lvNav);
        lvNav.setAdapter(new NavListAdapter());
        lvNav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                                intent = new Intent(MainActivity.this, AddressSelectionActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                startActivity(new Intent(MainActivity.this, OrdersActivity.class));
                                break;
                            case 3:
                                //TODO: Account
                                if (mUserId.length() > 0) {
                                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.i("test", "hit");
//                                    intent = new Intent(MainActivity.this, CheckInActivity.class);
//                                    startActivityForResult(intent, REQUEST_CHECKIN);
                                    navItems[3] = null;
                                    break;
                                }
                                break;
                            case 4:
                                intent = new Intent(MainActivity.this, WalletActivity.class);
                                startActivity(intent);
                                break;
                            case 5:
                                intent = new Intent(MainActivity.this, ReviewsActivity.class);
                                startActivity(intent);
                                break;
                            case 6:
                                //TODO: Sign Out
                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, "");
                                checkForLoggedInStatus();
                                ((NavListAdapter) lvNav.getAdapter()).notifyDataSetChanged();
                                Snackbar.make(findViewById(R.id.page), getString(R.string.msg_logout_success), Snackbar.LENGTH_SHORT).show();
                                LoginManager.getInstance().logOut();
                                break;
                        }
                    }
                }, 250);
            }
        });

        mNavigationView = (NavigationView) findViewById(R.id.navView);
        sortView = (NavigationView) findViewById(R.id.sortView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        initFilters();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        rvRestaurants = (RecyclerView) findViewById(R.id.rvRestaurants);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvRestaurants.setLayoutManager(mLayoutManager);
        rvRestaurants.setItemAnimator(new DefaultItemAnimator());
        rvRestaurants.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        RestaurantsAdapter adapter = new RestaurantsAdapter(MainActivity.this, mRestaurants);
        rvRestaurants.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClicked(Restaurant restaurant) {
                Intent intent = new Intent(MainActivity.this, RestaurantsDetailActivity.class);
                intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, restaurant.getId());
                startActivity(intent);
            }
        });
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //
            }
        });
        rvRestaurants.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !mLoadMoreReqtd) {
                    int itemCount = mLayoutManager.getItemCount();
                    int itemPos = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
                    if (itemPos + 1 == itemCount) {
                        startIndex += PAGE_SIZE;
                        mLoadMoreReqtd = true;
                        loadRestaurantsFromNW();
                    }
                }
            }
        });

        tvNoRestaurants = (TextView) findViewById(R.id.tvNoRestaurants);

//        loadRestaurantsFromNW();
        loadCuisineListFromNW();    // needed for cuisine filter
    }

    private void checkForLoggedInStatus() {
        List<String> navList = new ArrayList<>(Arrays.asList(navItems));
        mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);
        if (mUserId.length() > 0) {
            navList.add("Sign Out");
        } else {
            if (navList.contains("Sign Out")) {
                navList.remove(navList.indexOf("Sign Out"));
            }
        }
        navItems = new String[navList.size()];
        for (int i = 0; i < navList.size(); i++) navItems[i] = navList.get(i);
    }

    private void initFilters() {
        View view = findViewById(R.id.viewFilter);
        assert view != null;

        ImageView ivClearFilter;
        ivClearFilter = (ImageView) view.findViewById(R.id.ivClearFilter);
        ivClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*rbFilterOffer.setChecked(false);
                rbFilterNewRests.setChecked(false);
                rbFilterRatings.setChecked(false);
                rbFilterOpenRests.setChecked(false);
                rbFilterVeg.setChecked(false);*/
                rgFilter.clearCheck();
            }
        });
        Spinner spSortList;
        spSortList = (Spinner) view.findViewById(R.id.spSortList);
        spCuisine = (Spinner) view.findViewById(R.id.spCuisine);

        rgFilter = (RadioGroup) view.findViewById(R.id.rgFilter);
        rbFilterOffer = (RadioButton) view.findViewById(R.id.rbFilterOffer);

        RadioButton rbFilterNewRests;
        rbFilterNewRests = (RadioButton) view.findViewById(R.id.rbFilterNewRests);
        rbFilterRatings = (RadioButton) view.findViewById(R.id.rbFilterRatings);
        rbFilterOpenRests = (RadioButton) view.findViewById(R.id.rbFilterOpenRests);
        rbFilterVeg = (RadioButton) view.findViewById(R.id.rbFilterVeg);

        spSortList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                AppUtils.log(getResources().getStringArray(R.array.sort_menu)[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button btnApplyFilter;
        btnApplyFilter = (Button) view.findViewById(R.id.btnApplyFilter);
        btnApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });
    }

    private void loadRestaurantsFromNW() {
        AppUtils.hideViews(tvNoRestaurants);
        if (startIndex == 0) AppUtils.hideViews(rvRestaurants);
//        mRestaurants.clear();
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            filterParams.put("city_id", mCityId);
            filterParams.put("region_id", mRegionId);
            filterParams.put("limit", String.valueOf(startIndex));
            filterParams.put("offset", String.valueOf(PAGE_SIZE));
            final ProgressDialog dialog = AppUtils.showProgress(MainActivity.this, getString(R.string.msg_fetching_restaurants), getString(R.string.msg_please_wait));
            if (startIndex > 0) AppUtils.hideProgress(dialog);
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.LIST_RESTAURANTS, filterParams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            AppUtils.hideProgress(dialog);
                            JSONArray array = response.optJSONArray("restaurants");
                            if (array != null && array.length() > 0) {
                                for (int i = 0; i < array.length(); i++) {
//                                    new RestaurantJsonParsetask().execute(array.optJSONObject(i));
                                    Restaurant restaurant = new Restaurant();
                                    JSONObject object = array.optJSONObject(i);
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

                                    String offerPercentage = object.optString("offer_percentage");
                                    if (!"".equals(offerPercentage)) {
                                        offerPercentage = offerPercentage.split("\\.")[0];
                                    }
                                    if ("null".equals(offerPercentage)) {
                                        offerPercentage = "";
                                    }
                                    restaurant.setDiscount(offerPercentage);

                                    String deliveryTime = object.optString("delivery_time");
                                    restaurant.setDeliveryTime(deliveryTime);

                                    restaurant.setUriThumb(Apis.IMG_PATH + object.optString("merchant_photo"));
                                    mRestaurants.add(restaurant);
                                }
                                mLoadMoreReqtd = false;
                                rvRestaurants.getAdapter().notifyDataSetChanged();
                                AppUtils.showViews(rvRestaurants);
                            } else {
                                if (mRestaurants.size() == 0)
                                    AppUtils.showViews(tvNoRestaurants);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AppUtils.hideProgress(dialog);
                            if (error.networkResponse == null && error.getClass().equals(TimeoutError.class) || error.getClass().equals(NoConnectionError.class)) {

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
                            error.printStackTrace();
                        }
                    }
            );
            AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
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

    private void loadCuisineListFromNW() {
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.CUISINE_LIST, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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
            TextView view = (TextView) LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_nav_list_item, null, false);
            view.setText(item);
            if (position == 1)
                view.setTextColor(Color.parseColor("#C63A2B"));
            view.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
            view.setCompoundDrawablePadding(55);
            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
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
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
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
//                    loadRestaurantsFromNW();
                }
                break;

            case REQUEST_CHECKIN:
                if (resultCode == RESULT_OK) {
                    checkForLoggedInStatus();
                    NavListAdapter adapter = (NavListAdapter) lvNav.getAdapter();
                    adapter.notifyDataSetChanged();
                }

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRestaurantsFromNW();
    }

    @Override
    protected void onPause() {
        super.onPause();
        startIndex = 0;
        mRestaurants.clear();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
