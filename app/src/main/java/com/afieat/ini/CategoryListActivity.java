package com.afieat.ini;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.afieat.ini.Security.Relogin;
import com.afieat.ini.adapters.CuisinesAdapter;
import com.afieat.ini.adapters.CustomPagerAdapter;
import com.afieat.ini.fragments.CategoryofFoodFragment;
import com.afieat.ini.fragments.CrazyDealsFragment;
import com.afieat.ini.fragments.dummy.FoodCategoryContent;
import com.afieat.ini.models.Cuisine;
import com.afieat.ini.models.Restaurant;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.AuthkeyValidator;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CategoryListActivity extends AppCompatActivity implements CategoryofFoodFragment.OnListOrderListener {


    private Toolbar mToolbar;
    private ListView lvNav;
    private NavigationView mNavigationView, sortView;
    private DrawerLayout mDrawerLayout;
    //private ProgressBar progressBar;
    private Dialog afieatGifLoaderDialog;
//    private View listFooterView;

    private ImageView ivClearFilter;
    private Spinner spSortList, spCuisine;

    private RadioGroup rgFilter, rgPrice, rgSort;
    private RadioButton rbFilterOffer, rbFilterNewRests, rbFilterRatings, rbFilterOpenRests, rbFilterVeg;
    private RadioButton rbLo2Hi, rbHi2Lo;
    private LayoutInflater inflater = null;

    private Map<String, String> filterParams;

    private  int mStartIndex;
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

    private String[] temp_navitem;
    private int[] temp_navicon = new int[0];

    private final String[] navItems = {"Home", "Address", "My Orders", "My Account","Driver Order", "My Voucher", "My Points", "My Favorites", "My Reviews", "My Deals", "English/عربى", "Help Center"};
    private final String[] navItems_ar = {"الرئيسية" ,"عنوان", "طلبياتي", "حسابي","طلب السائق", "قسائم الشراء الخاصة بي","نقاطي", "مفضلتي", " التعليقات و التقييم", "عروضي", "English/عربى", "مركز المساعدة"};
    private final int[] navIcons = {
            R.drawable.nav_restaurants,
            R.drawable.nav_address,
            R.drawable.nav_orders,
            R.drawable.nav_account,
            R.drawable.menu_driver_order,
            R.drawable.nav_wallet,
            R.drawable.points_black,
            R.drawable.ic_baseline_bookmark_border_24,
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

    public CategoryListActivity() {
        mStartIndex = 0;
        willLoadMore = false;
        restaurants = new ArrayList<>();
        mCuisines = new ArrayList<>();
        filterParams = new HashMap<>();
    }

    TextView tvUsername, tvUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        //       Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        appInstance = AppInstance.getInstance(getApplicationContext());

        AppUtils.APPTOKEN = appInstance.getAuthkey();


        snackNoRestaurants = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_restaurants), Snackbar.LENGTH_SHORT);

        mRegionId = appInstance.getFromSharedPref(AppUtils.PREF_REGION_ID);
        System.out.println("Rahul : CategoryListActivity : mRegionId : " + mRegionId);
        mRegionName = appInstance.getFromSharedPref(AppUtils.PREF_REGION);
        mCityName = appInstance.getFromSharedPref(AppUtils.PREF_CITY);
        mCityId = appInstance.getFromSharedPref(AppUtils.PREF_CITY_ID);
        mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);

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
                Intent intent = new Intent(CategoryListActivity.this, LocationActivity.class);
                intent.putExtra(AppUtils.EXTRA_REQ_LOCATION, String.valueOf(true));
                startActivityForResult(intent, REQUEST_LOCATION);
            }
        });
        setSupportActionBar(mToolbar);

        lvNav = (ListView) findViewById(R.id.lvNav);
        mNavigationView = (NavigationView) findViewById(R.id.navView);
        sortView = (NavigationView) findViewById(R.id.sortView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        // progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        listFooterView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_list_footer_progress, null, false);

        //filters initialization
        initFilters();

//        lvRestaurants.addFooterView(listFooterView);


        lvNav.setAdapter(new NavListAdapter());
        lvNav.setOnItemClickListener(new NavMenuItemClickListener());

        //  afieatGifLoaderDialog();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                System.out.println("Drawer : onDrawerOpened");
                if (mUserId != null) {
                    if (mUserId.length() > 0) {
                        tvUsername.setText(appInstance.getFromSharedPref(AppUtils.PREF_USER_FNAME));
                        AppUtils.log("@@ CALL- Callll" + appInstance.getFromSharedPref(AppUtils.PREF_USER_FNAME));
                        tvUserEmail.setText(appInstance.getFromSharedPref(AppUtils.PREF_USER_PHONE));

                    }
                }

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                System.out.println("Drawer : onDrawerClosed");
            }
        };
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        ((TextView) findViewById(R.id.LogInBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryListActivity.this, LoginActivity.class);
                startActivityForResult(intent, 020);
            }
        });


        //   checkForLoggedInStatus();
        loadCuisineListFromNW();
        filterParams.put("pric", "high");
        filterParams.put("srt", "review_count");


        if (getSupportFragmentManager().findFragmentById(R.id.flContainer) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.flContainer)).commit();

            Fragment fragment = CategoryofFoodFragment.newInstance(mCityId, mRegionId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.flContainer, fragment,"CategoryFrag")
                    .disallowAddToBackStack()
                    .commit();

        } else {
            Fragment fragment = CategoryofFoodFragment.newInstance(mCityId, mRegionId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.flContainer, fragment,"CategoryFrag")
                    .disallowAddToBackStack()
                    .commit();
        }


//        SendDeviceIdtoServer();

        if (mUserId.length() > 0) flashSaleView();

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
                        break;  }
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
                        break;  }
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

            }
        });
    }

    RadioGroup radio_container;
    private Handler pagerHandler = null;
    private Runnable pagerRunnable = null;
    String big_images_filenames = "";
    private final int PAGER_DURATION = 8000;


    private void flashSaleView() {
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {

            AppUtils.log("test-userid  " + mUserId);
            AppUtils.log("test-lang" + appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG));

            // AppUtils.showViews(progressBar);
            afieatGifLoaderDialog();
            Map<String, String> params = new HashMap<>();
            params.put("lang", appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG));
            params.put("city_id", appInstance.getFromSharedPref(AppUtils.PREF_CITY_ID));
            if (mUserId.length() > 0) params.put("user_id", mUserId);

            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_CRAZY_DEALS, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            System.out.println("Rahul : CategoryListActivity : flashSaleView : response :" + response.toString());
                            AppUtils.log("test-check-test  1" + response.toString());
                            //   AppUtils.hideViews(progressBar);
                            if (afieatGifLoaderDialog != null) {
                                afieatGifLoaderDialog.dismiss();
                            }

                            if (response.has("status") && response.optInt("status") == 111) {
                                new AuthkeyValidator(CategoryListActivity.this).CallForReAuth(new AuthkeyValidator.Authkey() {
                                    @Override
                                    public void Oncomplete() {
                                        flashSaleView();
                                    }
                                });
                            } else if (response.optInt("status") == 999) {
                                new Relogin(CategoryListActivity.this, new Relogin.OnLoginlistener() {
                                    @Override
                                    public void OnLoginSucess() {
                                        flashSaleView();
                                    }

                                    @Override
                                    public void OnError(String Errormessage) {
                                        startActivityForResult(new Intent(CategoryListActivity.this, LoginActivity.class), AppUtils.REQ_LOGIN);
                                    }
                                }).execute();
                            } else
                                try {
                                    System.out.println("Rahul flash : " + response.getJSONArray("details"));
                                    // final int arrayLength = response.getJSONArray("details").length();
                                    if (response.getJSONArray("details").length() > 0) {
                                        inflater = LayoutInflater.from(CategoryListActivity.this);
//                                    dialog = new AlertDialog(RestaurantListActivity.this,  R.style.MaterialDialogSheetNoAnim);
//                                    dialog.setContentView(R.layout.dialog_restaurent_list);
//                                    tabbedFragements = new LinkedList<>();
//                                    viewPager = (ViewPager) dialog.findViewById(R.id.flash_viewPager);
//                                    viewPager.setAdapter(new CustomPagerAdapter(RestaurantListActivity.this, response.getJSONArray("details")));
//                                    dialog.show();

                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CategoryListActivity.this);
                                        View view = inflater.inflate(R.layout.dialog_restaurent_list, null);
                                        alertDialogBuilder.setView(view);


                                        tabbedFragements = new LinkedList<>();
                                        viewPager = (ViewPager) view.findViewById(R.id.flash_viewPager);
                                        viewPager.setAdapter(new CustomPagerAdapter(CategoryListActivity.this, response.getJSONArray("details")));
                                        final AlertDialog alertDialog = alertDialogBuilder.create();
                                        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
                                        tabLayout.setupWithViewPager(viewPager);
                                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        alertDialog.getWindow().setLayout(300, 380);
                                        alertDialog.show();

/*
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (alertDialog!=null)
                                                {
                                                    alertDialog.show();
                                                }


                                            }
                                        });*/
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception ee) {

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
    }

    private void loadCuisineListFromNW() {
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.CUISINE_LIST, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Rahul : CategoryListActivity : loadCuisineListFromNW : response : " + response);
//afieatGifLoaderDialog.dismiss();
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


    private void checkForLoggedInStatus() {

        String lang = appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG);
        if ("ar".equals(lang)) {
            temp_navitem = navItems_ar;
        } else {
            temp_navitem = navItems;
        }

        List<String> navList = new ArrayList<>(Arrays.asList(temp_navitem));
        List<Integer> navIconsList = new ArrayList<>();
        for (int navIcon : navIcons) {
            navIconsList.add(navIcon);
        }
        mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);
        View userPanel = findViewById(R.id.navHeader);
        assert userPanel != null;
        tvUsername = (TextView) userPanel.findViewById(R.id.tvUsername);
        tvUserEmail = (TextView) userPanel.findViewById(R.id.tvUserEmail);
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
                AppUtils.log("@@ CALL- Callll" + appInstance.getFromSharedPref(AppUtils.PREF_USER_FNAME));
                tvUserEmail.setText(appInstance.getFromSharedPref(AppUtils.PREF_USER_PHONE));

                System.out.println("Rahul : user : profile : " + appInstance.getFromSharedPref(AppUtils.PREF_USER_FNAME));
                System.out.println("Rahul : user : profile : " + appInstance.getFromSharedPref(AppUtils.PREF_USER_PHONE));
                tvUsername.setVisibility(View.VISIBLE);
                tvUserEmail.setVisibility(View.VISIBLE);
            }
        }
        else {

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
            AppUtils.showViews(userPanel);
            ((TextView) userPanel.findViewById(R.id.LogInBtn)).setVisibility(View.VISIBLE);
            tvUsername.setVisibility(View.GONE);
            tvUserEmail.setVisibility(View.GONE);
        }

        temp_navitem = new String[navList.size()];
        temp_navicon = new int[navIconsList.size()];
        for (int i = 0; i < navList.size(); i++) {
            temp_navitem[i] = navList.get(i);
            temp_navicon[i] = navIconsList.get(i);
        }
        AppUtils.log("Changing menu");
        ((NavListAdapter) lvNav.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onListFragmentInteraction(FoodCategoryContent.CategoryItem item) {

//        if(getSupportFragmentManager().findFragmentById(R.id.flContainer) != null) {
//            getSupportFragmentManager()
//                    .beginTransaction().
//                    remove(getSupportFragmentManager().findFragmentById(R.id.flContainer)).commit();
//        }
        GroupID = item.group_id;

        Intent intent = new Intent(CategoryListActivity.this, RestaurantListActivity.class);
        AppUtils.GROUP_ID = GroupID;
        intent.putExtra(AppUtils.EXTRA_GROUP_ID, GroupID);
        startActivity(intent);

//        itemfilter.setVisible(true);
//        itemnotificatin.setVisible(true);
//        itemsearch.setVisible(false);


    }

    @Override
    public void OnDtaFethchingStart() {
        //   AppUtils.showViews(progressBar);


    }

    @Override
    public void OnDataFetched() {
        //   AppUtils.hideViews(progressBar);
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
                        case 1:
                            //TODO: Change preferred address
                            intent = new Intent(CategoryListActivity.this, AddressSelectionActivity.class);
                            startActivity(intent);
                            break;
                        case 2:
                            startActivity(new Intent(CategoryListActivity.this, OrdersActivity.class));
                            break;
                        case 3:
                            //TODO: Account
                            if (mUserId.length() > 0) {
                                intent = new Intent(CategoryListActivity.this, ProfileActivity.class);
                                startActivity(intent);
                            }
                            else {

                                intent = new Intent(CategoryListActivity.this, LoginActivity.class);
                                startActivity(intent);

                            }

                            break;
                        case 4:
                            //TODO: Change preferred address
                           intent = new Intent(CategoryListActivity.this, DeliveryPointActivity.class);
                            startActivity(intent);
                            break;
                        case 5:

                            if (mUserId.length() > 0) {

                                intent = new Intent(CategoryListActivity.this, VoucherActivity.class);
                                startActivity(intent);
                                // finish();
                            } else {
                               intent = new Intent(CategoryListActivity.this, CheckInActivity.class);
                               startActivityForResult(intent, REQUEST_CHECKIN);

                            }

                            break;
                        case 6:
                            //     Toast.makeText(getApplicationContext(),position,Toast.LENGTH_SHORT).show();
                            System.out.println("vjhvjvjkbkjbk");
                            if (mUserId.length() > 0) {

                                intent = new Intent(CategoryListActivity.this, MyPoints.class);
                                startActivity(intent);
                                // finish();
                            } else {
                                intent = new Intent(CategoryListActivity.this, CheckInActivity.class);
                                startActivityForResult(intent, REQUEST_CHECKIN);
                                finish();
                            }


                            break;
                        case 7:

                            //     Toast.makeText(getApplicationContext(),position,Toast.LENGTH_SHORT).show();
                            System.out.println("vjhvjvjkbkjbk");
                            if (mUserId.length() > 0) {

                                intent = new Intent(CategoryListActivity.this, FavouriteListActivity.class);
                                startActivity(intent);
                                // finish();
                            } else {
                                intent = new Intent(CategoryListActivity.this, CheckInActivity.class);
                                startActivityForResult(intent, REQUEST_CHECKIN);
                                finish();
                            }

                            break;
                        case 8:
                            if (mUserId.length() > 0) {
                                intent = new Intent(CategoryListActivity.this, ReviewsActivity.class);
                                startActivity(intent);
                            } else {
                                intent = new Intent(CategoryListActivity.this, LanguageSelectionActivity.class);
                                startActivity(intent);
                            }


                            break;
                        case 9:
                            if (mUserId.length() > 0) {
                                intent = new Intent(CategoryListActivity.this, DealsActivity.class);
                                startActivity(intent);
                            } else {
                                intent = new Intent(CategoryListActivity.this, HelpCenterActivity.class);
                                startActivity(intent);
                            }
                            break;
                        case 10:
                            if (mUserId.length() > 0) {
                                startActivityForResult(new Intent(CategoryListActivity.this, LanguageSelectionActivity.class), AppUtils.REQ_CHANGE_LANG);
                            } else {
                                startActivityForResult(new Intent(CategoryListActivity.this, HelpCenterActivity.class), AppUtils.REQ_CHANGE_LANG);
                            }
                            break;
                        case 11:
                            startActivityForResult(new Intent(CategoryListActivity.this, HelpCenterActivity.class), AppUtils.REQ_CHANGE_LANG);

                            break;
                        case 12:
                            //TODO: Sign Out

                            appInstance.setAuthkey("");
                            AppUtils.APPTOKEN = "";
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, "");
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(false));
                            checkForLoggedInStatus();
                            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_logout_success), Snackbar.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();
                            break;
                        default:
                            break; }
                }
            }, 250);
        }
    }

//    private class RestaurantListScrollListener implements AbsListView.OnScrollListener {
//
//        @Override
//        public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//        }
//
//        @Override
//        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 1) {
//                if (!willLoadMore) {
//                    willLoadMore = true;
//                    mStartIndex += QUERY_LIMIT;
//                }
//            }
//        }
//    }

//    private class RestaurantListItemClickListener implements AdapterView.OnItemClickListener {
//
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Restaurant restaurant = restaurants.get(position);
//            Intent intent = new Intent(CategoryListActivity.this, RestaurantsDetailActivity.class);
//            intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, restaurant.getId());
//            startActivity(intent);
//        }
//    }

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
        itemsearch.setVisible(true);
        itemfilter.setVisible(false);
        itemnotificatin.setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                startActivity(new Intent(CategoryListActivity.this, SearchActivity.class));
                return true;
            case R.id.menu_notification:
                System.out.println("mUserId.length() : " + mUserId.length());
                if (mUserId.length() == 0) {
                    AppUtils.IS_NOTIFICATION_CLICK = true;
                    AppUtils.IS_FROM_PROCEED_TO_PAY = false;
                    startActivity(new Intent(CategoryListActivity.this, NotificationActivity.class));

                } else {
                    startActivity(new Intent(CategoryListActivity.this, NotificationActivity.class));
                    AppUtils.IS_NOTIFICATION_CLICK = false;
                }
                return true;
            case R.id.menu_filter:
                if (sortView.isShown()) mDrawerLayout.closeDrawer(sortView);
                else mDrawerLayout.openDrawer(sortView);
                return true;
            default:
                break;}
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
                    mRegionId = appInstance.getFromSharedPref(AppUtils.PREF_REGION_ID);
                    System.out.println("Rahul : CategoryListActivity : mRegionId : " + mRegionId);
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

                    if (getSupportFragmentManager().findFragmentById(R.id.flContainer) != null) {
//                        Fragment fragment = CategoryofFoodFragment.newInstance(mCityId,mRegionId);
                        getSupportFragmentManager()
                                .beginTransaction().
                                remove(getSupportFragmentManager().findFragmentById(R.id.flContainer)).commit();
                        Fragment fragment = CategoryofFoodFragment.newInstance(mCityId, mRegionId);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.flContainer, fragment,"CategoryFrag")
                                .disallowAddToBackStack()
                                .commit();
                    } else {
                        itemfilter.setVisible(false);
                        itemnotificatin.setVisible(false);
                        itemsearch.setVisible(true);
                        Fragment fragment = CategoryofFoodFragment.newInstance(mCityId, mRegionId);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.flContainer, fragment,"CategoryFrag")
                                .disallowAddToBackStack()
                                .commit();
                    }
                }
                break;

            case REQUEST_CHECKIN:
                if (resultCode == RESULT_OK) {
                    restaurants.clear();
                    mStartIndex = 0;
//                    checkForLoggedInStatus();
                }
                break;

            case AppUtils.REQ_CHANGE_LANG:
                if (resultCode == RESULT_OK) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }

                break;
            case 020:
                mDrawerLayout.closeDrawer(mNavigationView);
                checkForLoggedInStatus();
                break;
            default:
                break;}
    }

    @Override
    public void onBackPressed() {

        if (mUserId.length() > 0) {
            startActivity(new Intent(this, LocationActivity.class));
            overridePendingTransition(R.anim.entry_in, R.anim.exit_out);
            finish();
        } else
            super.onBackPressed();

    }

    @Override
    protected void onStop() {
        Log.d("@@ APP", " On Stop");
        super.onStop();
    }

    @Override
    protected void onStart() {

        Log.d("@@ APP", " On Start");
        new AuthkeyValidator(this);
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("@@ APP", " On Resume");
        if (mUserId.length() > 0)
            Notificationcountupdate();
        checkForLoggedInStatus();
        AppUtils.APPTOKEN = appInstance.getAuthkey();
        AppUtils.AUTHRIZATIONKEY = appInstance.getAuthkeyforall();
    }

    private void Notificationcountupdate() {
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            Map<String, String> params = new HashMap<>();
            params.put("lang", appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG));
            if (mUserId.length() > 0) params.put("user_id", mUserId);
            params.put("authkey", AppInstance.getInstance(CategoryListActivity.this).getAuthkey());
            AppUtils.log("@@ PARAMS-" + params);

            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_NOTIFICATIONCOUNT, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            AppUtils.log("test-check-test  2" + response.toString());

                            if (response.has("status") && response.optInt("status") == 111) {
                                new AuthkeyValidator(CategoryListActivity.this).CallForReAuth(new AuthkeyValidator.Authkey() {
                                    @Override
                                    public void Oncomplete() {
                                        Notificationcountupdate();
                                    }
                                });
                            }
                            else if (response.optInt("status") == 999) {
                                new Relogin(CategoryListActivity.this, new Relogin.OnLoginlistener() {
                                    @Override
                                    public void OnLoginSucess() {
                                        Notificationcountupdate();
                                    }

                                    @Override
                                    public void OnError(String Errormessage) {
                                        startActivityForResult(new Intent(CategoryListActivity.this, LoginActivity.class), AppUtils.REQ_LOGIN);
                                    }
                                }).execute();
                            } else if (response.optInt("code") == 0) {
                                itemnotificatin.setIcon(buildCounterDrawable(0, R.drawable.ic_notifications_none_white_24dp));
                            }
                            else {
                                try{
                                    itemnotificatin.setIcon(buildCounterDrawable(response.optInt("new_notification"), R.drawable.ic_notifications_none_white_24dp));
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            try {
                                itemnotificatin.setIcon(buildCounterDrawable(0, R.drawable.ic_notifications_none_white_24dp));
                            }catch (Exception e)
                            {
                                System.out.println("Error : try catch  : "+e);
                            }
                        }
                    }
            );
            AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
        }

    }

    class NavListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return temp_navicon.length;
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
            String item = temp_navitem[position];
            int drawable = temp_navicon[position];
            /*TextView view = (TextView) LayoutInflater.from(CategoryListActivity.this).inflate(R.layout.layout_nav_list_item, null, false);
            view.setText(item);
            if (position == 1)
                view.setTextColor(Color.parseColor("#C63A2B"));
            view.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
            view.setCompoundDrawablePadding(55);*/


            View viewNavList = LayoutInflater.from(CategoryListActivity.this).inflate(R.layout.layout_nav_list_item_new, null, false);
            //TextView view = (TextView) LayoutInflater.from(LocationActivity.this).inflate(R.layout.layout_nav_list_item, null, false);

            TextView tvMenuTitle = viewNavList.findViewById(R.id.tvMenuTitle);
            ImageView img = viewNavList.findViewById(R.id.img);

            if (position == 0)
                tvMenuTitle.setTextColor(Color.parseColor("#C63A2B"));


            tvMenuTitle.setText(item);
            if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                tvMenuTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
            } else {
                tvMenuTitle.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);

            }
            tvMenuTitle.setCompoundDrawablePadding(55);
         /*   tvMenuTitle.setCompoundDrawables(drawable,0,0,0);
            img.setBackgroundResource(drawable);*/
            return viewNavList;
        }
    }


    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.notification_category_drawable, null);
        View Image = view.findViewById(R.id.counterBackground);
        Image.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.count);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(this);
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }

}
