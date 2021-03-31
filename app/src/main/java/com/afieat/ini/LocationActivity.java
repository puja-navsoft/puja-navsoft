package com.afieat.ini;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.adapters.RegionAdapter;
import com.afieat.ini.adapters.StateAdapter;
import com.afieat.ini.models.CityModel;
import com.afieat.ini.models.RegionModel;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.afieat.ini.webservice.ApiClient;
import com.afieat.ini.webservice.ApiInterface;
import com.afieat.ini.webservice.PlaystoreVersionReq;
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

import retrofit2.Call;
import retrofit2.Callback;

//import pl.droidsonroids.gif.GifImageView;


public class LocationActivity extends AppCompatActivity {


    private Spinner spCity, spRegion;
    public Button btnShowRestaurants, btnShowAllRestaurants;
    private List<City> cities;

    private List<CityModel> mCityModelMain = new ArrayList<>();
    private List<RegionModel> mRCityModelsMain = new ArrayList<>();

    public TextView tvChooseCity;
    public TextView tvChooseRegion;

    private List<Region> regions;
    private String[] citiesArray, citiesArray_ar;
    private String[] regionsArray, regionsArray_ar;
    StateAdapter mStateAdapter;
    RegionAdapter mRegionAdapter;
    public String mCityId, mRegionId, mCityName, mRegionName;

    private RadioGroup rgLang;
    private RadioButton rbLangAr, rbLangEn;

    private boolean reqdLoc;

    public Dialog mDialogState;
    private FrameLayout dialogLoader;
    ImageView openDrawer;
    ImageView fakeViewDrawer;
    private boolean toggle = false;

    public LocationActivity() {
        cities = new ArrayList<>();
        regions = new ArrayList<>();
//        mCityId = "2";
        mRegionId = "";
//        mCityName = "Basrah";
        mRegionName = "";
        reqdLoc = false;
    }

    private AppInstance appInstance;
    private String mUserId;
    private final int REQUEST_CHECKIN = 101;
    //-------------drawerLayout

    DrawerLayout drawerLayout;
    private ListView lvNav;
    private NavigationView mNavigationView;

    private String[] temp_navitem;
    private int[] temp_navicon = new int[0];

    private final String[] navItems = {"Login", "Login", "English/عربى", "Facebook", "Help Center", "About Afieat"};
    private final String[] navItems_ar = {"تسجيل الدخول", "تسجيل الدخول", " عربى/English", "فيس بوك", "مركز المساعدة", "حول Afieat"};
    private final int[] navIcons = {
            R.drawable.login_24,
            R.drawable.login_24,
            R.drawable.nav_language,
            R.drawable.fb_24,
            R.drawable.nav_help,
            R.drawable.about_24,


            /*R.drawable.nav_settings,*/
            /*R.drawable.nav_contact,*/

            /*R.drawable.nav_signout*/
    };

    @Override
    protected void onStart() {
        super.onStart();
        forceUpdate();
    }

    //GifImageView bikeLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_drawer_new);
         Toolbar mToolbar;


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        appInstance = AppInstance.getInstance(getApplicationContext());

        AppUtils.APPTOKEN = appInstance.getAuthkey();


        mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);

        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_address));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        mToolbar.getBackground().setAlpha(0);
        setSupportActionBar(mToolbar);

        openDrawer = findViewById(R.id.openDrawer);
        fakeViewDrawer = findViewById(R.id.fakeViewDrawer);


        lvNav = findViewById(R.id.lvNav);
        mNavigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawerLayout);

        // bikeLoader=findViewById(R.id.bikeLoader);

        lvNav.setAdapter(new NavListAdapter());
        lvNav.setOnItemClickListener(new NavMenuItemClickListener());

        tvChooseCity = findViewById(R.id.tvChooseCity);
        tvChooseRegion = findViewById(R.id.tvChooseRegion);


        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerLayout.openDrawer(mNavigationView);

            }
        });


        fakeViewDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerLayout.openDrawer(mNavigationView);
            }
        });
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getStringExtra(AppUtils.EXTRA_REQ_LOCATION) != null) {
            reqdLoc = true;
        }

        rgLang = (RadioGroup) findViewById(R.id.rgLang);
        rbLangAr = (RadioButton) findViewById(R.id.rbLangAr);
        rbLangEn = (RadioButton) findViewById(R.id.rbLangEn);

        String langChoice = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG);
        if ("ar".equals(langChoice)) {
            rbLangAr.setChecked(true);
            tvChooseCity.setText("اختر مدينتك");
            tvChooseRegion.setText("اختر منطقتك");
            mCityName = "البصرة";
        } else {
            mCityName = "Basrah";
            rbLangEn.setChecked(true);
            tvChooseCity.setText("Choose Your City");
            tvChooseRegion.setText("Choose Your Region");
        }

        rgLang.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbLangAr:
                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_LANG, "ar");
                        setResult(RESULT_OK);
                        break;
                    case R.id.rbLangEn:
                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_LANG, "en");
                        setResult(RESULT_OK);
                        break;
                    default:
                        break; }
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        btnShowRestaurants = (Button) findViewById(R.id.btnShowRestaurants);
        btnShowRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"Choose Your City".equalsIgnoreCase(tvChooseCity.getText().toString()) || !tvChooseCity.getText().toString().equalsIgnoreCase("اختر مدينتك")) {
                    Intent intent = new Intent(LocationActivity.this, CategoryListActivity.class);
                    intent.putExtra(AppUtils.EXTRA_REGION_ID, mRegionId);
                    intent.putExtra(AppUtils.EXTRA_CITY_NAME, mCityName);
                    intent.putExtra(AppUtils.EXTRA_REGION_NAME, mRegionName);
                    if (reqdLoc) setResult(RESULT_OK, intent);
                    else startActivity(intent);

                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_CITY, mCityName);
                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_REGION, mRegionName);
                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_CITY_ID, mCityId);
                    AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_REGION_ID, mRegionId);

                    finish();   // remove this activity from backstack
                    overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
                } else {
                    if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {
                        Toast.makeText(getApplicationContext(), "الرجاء اختيار المدينة والمنطقة", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Please Select City and region", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

//        btnShowAllRestaurants = (Button) findViewById(R.id.btnShowAllRestaurants);
//        btnShowAllRestaurants.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (!"Choose Your City".equalsIgnoreCase(tvChooseCity.getText().toString()) || !tvChooseCity.getText().toString().equalsIgnoreCase("اختر مدينتك")) {
//                    System.out.println("isFrom : " + getIntent().getStringExtra("isFrom"));
//                    if (getIntent().getStringExtra("isFrom") != null) {
//                        System.out.println("isFrom ");
//                        if (getIntent().getStringExtra("isFrom").contentEquals("RestaurantListActivity")) {
//                            Intent intent = new Intent(LocationActivity.this, RestaurantListActivity.class);
//                            intent.putExtra(AppUtils.EXTRA_GROUP_ID, AppUtils.GROUP_ID);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }
//                    else {
//
//                        Intent intent = new Intent(LocationActivity.this, CategoryListActivity.class);
//     //                 Intent intent = new Intent(LocationActivity.this, RestaurantListActivity.class);
//                        if (reqdLoc) setResult(RESULT_OK, intent);
//                        else startActivity(intent);
//
//                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_CITY, mCityName);
//                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_REGION, "");
//                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_CITY_ID, mCityId);
//                        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_REGION_ID, "");
//
//                        System.out.println("Rahul : btnShowAllRestaurants : mCityName : " + mCityName);
//                        System.out.println("Rahul : btnShowAllRestaurants : mCityId : " + mCityId);
//                        finish();   // remove this activity from backstack
//                        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
//                    }
//
//                } else {
//                    if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
//                        Toast.makeText(getApplicationContext(), "الرجاء اختيار المدينة والمنطقة", Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Please Select City and region", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
       /* AppUtils.disableButtons(btnShowRestaurants);
        AppUtils.disableButtons(btnShowAllRestaurants);*/

        spCity = (Spinner) findViewById(R.id.spCity);
        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String citySelected = citiesArray[position];
                    mCityName = citySelected;
                    for (City city : cities) {
                        if (city.name.equals(citySelected)) {
                            mCityId = city.id;
                            break;
                        }
                    }
                    AppUtils.enableButtons(btnShowAllRestaurants);
                    loadRegionListFromNW(mCityId);
                } else {
                    AppUtils.disableButtons(btnShowAllRestaurants);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spRegion = (Spinner) findViewById(R.id.spRegion);
        spRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    AppUtils.enableButtons(btnShowRestaurants);
                    String regionSelected = regionsArray[position];
                    mRegionName = regionSelected;
                    String idSelected = "";
                    for (Region region : regions) {
                        if (region.name.equals(regionSelected)) {
                            idSelected = region.id;
                            break;
                        }
                    }
                    mRegionId = idSelected;
                    System.out.println("Rahul : LocationActivity : mRegionId : " + mRegionId);

                } else {
                    AppUtils.disableButtons(btnShowRestaurants);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadCityListFromNW();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AppUtils.log("Force request location permission");
            ActivityCompat.requestPermissions(LocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }


        //--------------Rahul Development


        tvChooseCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cityLoadDialog();
            }
        });

        tvChooseRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!"Choose Your City".equalsIgnoreCase(tvChooseCity.getText().toString()) || !tvChooseCity.getText().toString().equalsIgnoreCase("اختر مدينتك")) {

                    regionLoadDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Select City", Toast.LENGTH_SHORT).show();
                    cityLoadDialog();
                }

            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }


    public void cityLoadDialog() {
        mDialogState = new Dialog(this, R.style.AppTheme);
        mDialogState.setContentView(R.layout.city_recyclerview_dialog);
        mDialogState.getWindow().setWindowAnimations(R.style.DialogAnimation);
        RecyclerView stateRecyclerView = mDialogState.findViewById(R.id.stateRecyclerView);
        TextView title = mDialogState.findViewById(R.id.title);

        if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

            title.setText(" اختر مدينتك");
        } else {
            title.setText("Choose Your City");
        }
        dialogLoader = mDialogState.findViewById(R.id.afieatLoaderFrame);
        stateRecyclerView.setNestedScrollingEnabled(false);
        mStateAdapter = new StateAdapter(this, mCityModelMain);
        stateRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManagerDrawer = new LinearLayoutManager(getApplicationContext());
        stateRecyclerView.setLayoutManager(mLayoutManagerDrawer);
        stateRecyclerView.setItemAnimator(new DefaultItemAnimator());
        stateRecyclerView.setAdapter(mStateAdapter);

        loadCityListFromTvClick();
        mDialogState.show();
    }

    public void regionLoadDialog() {
        mDialogState = new Dialog(this, R.style.AppTheme);
        mDialogState.setContentView(R.layout.city_recyclerview_dialog);
        mDialogState.getWindow().setWindowAnimations(R.style.DialogAnimation);
        RecyclerView stateRecyclerView = mDialogState.findViewById(R.id.stateRecyclerView);
        ImageView searchImg = mDialogState.findViewById(R.id.searchImg);
        searchImg.setVisibility(View.VISIBLE);
        final EditText searchEditText = mDialogState.findViewById(R.id.searchEditText);
        loadRegionListTvClick(mCityId);

        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!toggle){
                    searchEditText.setVisibility(View.VISIBLE);
                    toggle = true;
                    searchEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
                }
                else {
                    searchEditText.setVisibility(View.GONE);
                    toggle = false;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                }
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                filterList(s.toString());
            }
        });
        TextView title = mDialogState.findViewById(R.id.title);
        if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

            title.setText("اختر منطقتك");
        } else {
            title.setText("Choose Your Region");
        }
        dialogLoader = mDialogState.findViewById(R.id.afieatLoaderFrame);
        stateRecyclerView.setNestedScrollingEnabled(false);
        mRegionAdapter = new RegionAdapter(this, mRCityModelsMain);
        stateRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManagerDrawer = new LinearLayoutManager(getApplicationContext());
        stateRecyclerView.setLayoutManager(mLayoutManagerDrawer);
        stateRecyclerView.setItemAnimator(new DefaultItemAnimator());
        stateRecyclerView.setAdapter(mRegionAdapter);
        mDialogState.show();
    }

    private void filterList(String s) {

        ArrayList<RegionModel> filteredRegion = new ArrayList<>();
        for (RegionModel item: mRCityModelsMain){
            if (item.getName().toLowerCase().contains(s.toLowerCase()))
                filteredRegion.add(item);
        }

        mRegionAdapter.setFilteredData(filteredRegion);
    }


    public void forceUpdate() {
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // packageInfo = null;
        String currentVersion = packageInfo.versionName;
        final int current_version_code=packageInfo.versionCode;
        new Thread(new Runnable() {
            @Override
            public void run() {
                get_playstore_version_code(current_version_code);
            }
        }).start();

        /*new ForceUpdateAsync(currentVersion,current_version_code, LocationActivity
                .this).execute();*/
    }


    private void loadCityListFromTvClick() {

        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            //final ProgressDialog dialog = AppUtils.showProgress(LocationActivity.this, "", getString(R.string.msg_please_wait));
            Map<String, String> params = new HashMap<>();
            if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                params.put("lang", "en");
            } else {
                params.put("lang", "ar");
            }
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.LIST_CITIES, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //AppUtils.hideProgress(dialog);
                            JSONArray cityArray = response.optJSONArray("cityllist");
                            System.out.println("Rahul : cityllist : " + cityArray);
                            citiesArray = new String[cityArray.length() + 1];
                            int i = 0;
                            int selectionPosition = 0;
                            citiesArray[0] = getString(R.string.select_city);
                            mCityModelMain.clear();
                            while (i < cityArray.length()) {
                                JSONObject cityObject = cityArray.optJSONObject(i++);
                                /*City city = new City();
                                city.id = cityObject.optString("city_id");
                                city.name = cityObject.optString("city_name");

*/

                                CityModel mCityModel = new CityModel(cityObject.optString("city_id"), cityObject.optString("city_name"));

                                mCityModelMain.add(mCityModel);

                              /*  cities.add(city);
                                citiesArray[i] = city.name;
*/
//                                AppUtils.log("@@ CITY iD-"+city.id);
//                                if(city.id=="2")
//                                    selectionPosition=i;

                            }


                            AppUtils.log("@@ CITY Position-" + selectionPosition);


                           /* spCity.setAdapter(new ArrayAdapter<>(LocationActivity.this, R.layout.layout_simple_spinner_item, R.id.tvItemTitle, citiesArray));
                            spCity.setSelection(1);*/

                            dialogLoader.setVisibility(View.GONE);
                            mStateAdapter.notifyDataSetChanged();
                            // wdlcflwdcl;wmv;wmd;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // AppUtils.hideProgress(dialog);
                            dialogLoader.setVisibility(View.GONE);
                            if (error.networkResponse == null) {
                                if (error.getClass().equals(TimeoutError.class) || error.getClass().equals(NoConnectionError.class)) {
                                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_server_no_response), Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            cityLoadDialog();
                                            snackbar.dismiss();
                                        }
                                    });
                                    snackbar.show();
                                }
                            }
                            error.printStackTrace();
                        }
                    }
            );
            AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
        } else {
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cityLoadDialog();
                    snackbar.dismiss();

                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }


    private void loadCityListFromNW() {
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            //  final ProgressDialog dialog = AppUtils.showProgress(LocationActivity.this, "", getString(R.string.msg_please_wait));
            //   bikeLoader.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                params.put("lang", "en");
            } else {
                params.put("lang", "ar");
            }
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.LIST_CITIES, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // AppUtils.hideProgress(dialog);
                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //bikeLoader.setVisibility(View.GONE);
                                }
                            }, 3000);

                            JSONArray cityArray = response.optJSONArray("cityllist");
                            citiesArray = new String[cityArray.length() + 1];
                            int i = 0;
                            int selectionPosition = 0;
                            citiesArray[0] = getString(R.string.select_city);
                            while (i < cityArray.length()) {
                                JSONObject cityObject = cityArray.optJSONObject(i++);
                                City city = new City();
                                city.id = cityObject.optString("city_id");
                                city.name = cityObject.optString("city_name");


                                CityModel mCityModel = new CityModel(cityObject.optString("city_id"), cityObject.optString("city_name"));

                                mCityModelMain.add(mCityModel);

                                cities.add(city);
                                citiesArray[i] = city.name;

//                                AppUtils.log("@@ CITY iD-"+city.id);
//                                if(city.id=="2")
//                                    selectionPosition=i;

                            }


                            AppUtils.log("@@ CITY Position-" + selectionPosition);


                            spCity.setAdapter(new ArrayAdapter<>(LocationActivity.this, R.layout.layout_simple_spinner_item, R.id.tvItemTitle, citiesArray));
                            spCity.setSelection(1);
                            // mStateAdapter.notifyDataSetChanged();
                            // wdlcflwdcl;wmv;wmd;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //  AppUtils.hideProgress(dialog);
                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //bikeLoader.setVisibility(View.GONE);
                                }
                            }, 3000);
                            if (error.networkResponse == null) {
                                if (error.getClass().equals(TimeoutError.class) || error.getClass().equals(NoConnectionError.class)) {
                                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_server_no_response), Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            loadCityListFromNW();
                                            snackbar.dismiss();
                                        }
                                    });
                                    snackbar.show();
                                }
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
                    loadCityListFromNW();
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }


    private void loadRegionListTvClick(final String id) {
        String idNew=id;
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            Map<String, String> params = new HashMap<>();
            params.put("city_id", idNew);
            if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                params.put("lang", "en");
            } else {
                params.put("lang", "ar");
            }
            // final ProgressDialog dialog = AppUtils.showProgress(LocationActivity.this, "", getString(R.string.msg_please_wait));
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.LIST_REGIONS, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // AppUtils.hideProgress(dialog);
                            JSONArray array = response.optJSONArray("regionlist");
                            if (array.length() > 0) {
                                regionsArray = new String[array.length() + 1];
                                regionsArray[0] = getString(R.string.select_region);
                                int i = 0;
                                mRCityModelsMain.clear();
                                while (i < array.length()) {
                                    JSONObject regionObject = array.optJSONObject(i++);
                                    Region region = new Region();
                                    region.id = regionObject.optString("region_id");
                                    region.name = regionObject.optString("region_name");
                                    regions.add(region);
                                    regionsArray[i] = region.name;


                                    RegionModel mRegionModel = new RegionModel(regionObject.optString("region_id"), regionObject.optString("region_name"));

                                    mRCityModelsMain.add(mRegionModel);
                                }
                                //spRegion.setAdapter(new ArrayAdapter<>(LocationActivity.this, R.layout.layout_simple_spinner_item, R.id.tvItemTitle, regionsArray));

                                dialogLoader.setVisibility(View.GONE);
                                mRegionAdapter.notifyDataSetChanged();
                                //  AppUtils.showViews(spRegion);
                            } else {
                                AppUtils.hideViews(spRegion);
                                AppUtils.disableButtons(btnShowRestaurants);
                                Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_regions), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //AppUtils.hideProgress(dialog);
                            dialogLoader.setVisibility(View.GONE);
                            mDialogState.dismiss();
                            if (error.networkResponse == null) {
                                if (error.getClass().equals(TimeoutError.class) || error.getClass().equals(NoConnectionError.class)) {
                                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_server_no_response), Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!"Choose Your City".equalsIgnoreCase(tvChooseCity.getText().toString()) || !tvChooseCity.getText().toString().equalsIgnoreCase("اختر مدينتك")) {

                                                regionLoadDialog();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Please Select City", Toast.LENGTH_SHORT).show();
                                                cityLoadDialog();
                                            }
                                            snackbar.dismiss();
                                        }
                                    });
                                    snackbar.show();
                                }
                            }
                            error.printStackTrace();
                        }
                    }
            );
            AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
        } else {

            final Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!"Choose Your City".equalsIgnoreCase(tvChooseCity.getText().toString()) || !tvChooseCity.getText().toString().equalsIgnoreCase("اختر مدينتك")) {

                        regionLoadDialog();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Select City", Toast.LENGTH_SHORT).show();
                        cityLoadDialog();
                    }
                    snackbar.dismiss();
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }


    private void loadRegionListFromNW(final String id) {
        final String idNew=id;
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            Map<String, String> params = new HashMap<>();
            params.put("city_id", idNew);
            if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                params.put("lang", "en");
            } else {
                params.put("lang", "ar");
            }
            final ProgressDialog dialog = AppUtils.showProgress(LocationActivity.this, "", getString(R.string.msg_please_wait));
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.LIST_REGIONS, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            AppUtils.hideProgress(dialog);
                            JSONArray array = response.optJSONArray("regionlist");
                            if (array.length() > 0) {
                                regionsArray = new String[array.length() + 1];
                                regionsArray[0] = getString(R.string.select_region);
                                int i = 0;
                                while (i < array.length()) {
                                    JSONObject regionObject = array.optJSONObject(i++);
                                    Region region = new Region();
                                    region.id = regionObject.optString("region_id");
                                    region.name = regionObject.optString("region_name");
                                    regions.add(region);
                                    regionsArray[i] = region.name;
                                }
                                spRegion.setAdapter(new ArrayAdapter<>(LocationActivity.this, R.layout.layout_simple_spinner_item, R.id.tvItemTitle, regionsArray));

                                AppUtils.showViews(spRegion);
                            } else {
                                AppUtils.hideViews(spRegion);
                                AppUtils.disableButtons(btnShowRestaurants);
                                Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_regions), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AppUtils.hideProgress(dialog);
                            if (error.networkResponse == null) {
                                if (error.getClass().equals(TimeoutError.class) || error.getClass().equals(NoConnectionError.class)) {
                                    final Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_server_no_response), Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            loadRegionListFromNW(idNew);
                                            snackbar.dismiss();
                                        }
                                    });
                                    snackbar.show();
                                }
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
                    loadCityListFromNW();
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    private void checkForLoggedInStatus() {


        String lang = appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG);
        if (lang.equals("ar")) {
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
       /* View userPanel = findViewById(R.id.navHeader);
        assert userPanel != null;
        TextView tvUsername = (TextView) userPanel.findViewById(R.id.tvUsername);
        TextView tvUserEmail = (TextView) userPanel.findViewById(R.id.tvUserEmail);
        AppUtils.log("UserId: " + mUserId);
*/
        if (mUserId.length() > 0) {
            // logged in

            // Sunit 27-01-2017


            if (!navList.contains("Sign Out") && !navList.contains("خروج")) {
                AppUtils.log("test11");

                if (appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {
                    navList.add("خروج");
                    navList.remove(navList.indexOf("تسجيل الدخول"));
                    navIconsList.remove(navIconsList.indexOf(R.drawable.login_24));
                    navList.remove(navList.indexOf("تسجيل الدخول"));
                    navIconsList.remove(navIconsList.indexOf(R.drawable.login_24));

                } else {

                    navList.add("Sign Out");
                    navList.remove(navList.indexOf("Login"));
                    navIconsList.remove(navIconsList.indexOf(R.drawable.login_24));
                    navList.remove(navList.indexOf("Login"));
                    navIconsList.remove(navIconsList.indexOf(R.drawable.login_24));


                    AppUtils.log("test11-33");
                }

                navIconsList.add(R.drawable.nav_signout);


               /* AppUtils.showViews(userPanel);
                ((TextView) userPanel.findViewById(R.id.LogInBtn)).setVisibility(View.GONE);
                tvUsername.setText(appInstance.getFromSharedPref(AppUtils.PREF_USER_FNAME));
                AppUtils.log("@@ CALL- Callll" + appInstance.getFromSharedPref(AppUtils.PREF_USER_FNAME));
                tvUserEmail.setText(appInstance.getFromSharedPref(AppUtils.PREF_USER_EMAIL));
                tvUsername.setVisibility(View.VISIBLE);
                tvUserEmail.setVisibility(View.VISIBLE);*/
            }
        } else {

            // not loggeed in
            //     AppUtils.hideViews(userPanel);

            if (navList.contains("Sign Out") || navList.contains("خروج")) {
                AppUtils.log("test22");
                if (appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {
                    navList.remove(navList.indexOf("خروج"));
                    navList.remove(navList.indexOf("تسجيل الدخول"));
                    //  navList.remove(navList.indexOf("حسابي"));
                    AppUtils.log("test11-44");
                } else {
                    navList.remove(navList.indexOf("Sign Out"));
                    navList.remove(navList.indexOf("Login"));
                    //   navList.remove(navList.indexOf("My Deals"));

                    AppUtils.log("test11-55");
                }
                navIconsList.remove(navIconsList.indexOf(R.drawable.login_24));
                //navIconsList.remove(navIconsList.indexOf(R.drawable.nav_deal));

               /* AppUtils.showViews(userPanel);
                ((TextView) userPanel.findViewById(R.id.LogInBtn)).setVisibility(View.VISIBLE);
                tvUsername.setVisibility(View.GONE);
                tvUserEmail.setVisibility(View.GONE);*/
            } else {

                if (navList.contains("Login") || navList.contains("تسجيل الدخول")) {
                    if (appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {

                        navList.remove(navList.indexOf("تسجيل الدخول"));
//                        navList.remove(navList.indexOf("حسابي"));
                    } else {
                        navList.remove(navList.indexOf("Login"));
                        // navList.remove(navList.indexOf("My Deals"));
                    }

                    navIconsList.remove(navIconsList.indexOf(R.drawable.login_24));
                    //  navIconsList.remove(navIconsList.indexOf(R.drawable.nav_deal));
                }


            }
         /*   AppUtils.showViews(userPanel);
            ((TextView) userPanel.findViewById(R.id.LogInBtn)).setVisibility(View.VISIBLE);
            tvUsername.setVisibility(View.GONE);
            tvUserEmail.setVisibility(View.GONE);*/
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
            View viewNavList = LayoutInflater.from(LocationActivity.this).inflate(R.layout.layout_nav_list_item, null, false);
            //TextView view = (TextView) LayoutInflater.from(LocationActivity.this).inflate(R.layout.layout_nav_list_item, null, false);

            TextView tvMenuTitle = viewNavList.findViewById(R.id.tvMenuTitle);
            //ImageView img=viewNavList.findViewById(R.id.img);

            tvMenuTitle.setText(item);
            if ("ar".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                tvMenuTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
            } else {
                tvMenuTitle.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);

            }
            tvMenuTitle.setCompoundDrawablePadding(55);
            // img.setBackgroundResource(drawable);
            /*view.setText(item);
            //  if (position == 1)
            //  view.setTextColor(Color.parseColor("#C63A2B"));
            view.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
            view.setCompoundDrawablePadding(55);*/
            return viewNavList;
        }
    }


    private class NavMenuItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
            drawerLayout.closeDrawer(mNavigationView);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    System.out.println("Rahul  : NavMenuItemClickListener :  " + position);
                    Intent intent;
                    switch (position) {
                        case 0:
                            //TODO: Change preferred address
                            if (mUserId.length() > 0) {

                                startActivity(new Intent(LocationActivity.this, LanguageSelectionActivity.class));

                            } else {
                                intent = new Intent(LocationActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            break;
                        case 1:
                            if (mUserId.length() > 0) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/afieat"));
                                startActivity(browserIntent);
                            } else {
                                startActivity(new Intent(LocationActivity.this, LanguageSelectionActivity.class));

                            }

                            break;
                        case 2:
                            //TODO: Account
                            // startActivity(new Intent(LocationActivity.this, LanguageSelectionActivity.class));
                            if (mUserId.length() > 0) {


                                intent = new Intent(LocationActivity.this, HelpCenterActivity.class);
                                startActivity(intent);

                            } else {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/afieat"));
                                startActivity(browserIntent);
                            }


                            break;
                        case 3:
                            if (mUserId.length() > 0) {
                                intent = new Intent(LocationActivity.this, AboutusActivity.class);
                                startActivity(intent);
                            } else {
                                if (mUserId.length() > 0) {
                                    intent = new Intent(LocationActivity.this, DealsActivity.class);
                                    startActivity(intent);
                                } else {
                                    intent = new Intent(LocationActivity.this, HelpCenterActivity.class);
                                    startActivity(intent);
                                }
                            }
                            break;
                        case 4:
                            if (mUserId.length() > 0) {
                                //TODO: Sign Out

                                appInstance.setAuthkey("");
                                AppUtils.APPTOKEN = "";
                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, "");
                                AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(false));
                                checkForLoggedInStatus();
                                Snackbar.make(findViewById(R.id.page), getString(R.string.msg_logout_success), Snackbar.LENGTH_SHORT).show();
                            } else {
                                intent = new Intent(LocationActivity.this, AboutusActivity.class);
                                startActivity(intent);
                            }
                          /*  if (mUserId.length() > 0) {
                                intent = new Intent(LocationActivity.this, DealsActivity.class);
                                startActivity(intent);
                            } else {
                                intent = new Intent(LocationActivity.this, HelpCenterActivity.class);
                                startActivity(intent);
                            }*/

                            break;
                        case 6:
                            if (mUserId.length() > 0) {
                                intent = new Intent(LocationActivity.this, DealsActivity.class);
                                startActivity(intent);
                            } else {
                                intent = new Intent(LocationActivity.this, HelpCenterActivity.class);
                                startActivity(intent);
                            }
                            //TODO: Sign Out

                         /*   appInstance.setAuthkey("");
                            AppUtils.APPTOKEN = "";
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, "");
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(false));
                            checkForLoggedInStatus();
                            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_logout_success), Snackbar.LENGTH_SHORT).show();*/
                            break;
                        case 7:
                            if (mUserId.length() > 0) {
                                startActivityForResult(new Intent(LocationActivity.this, LanguageSelectionActivity.class), AppUtils.REQ_CHANGE_LANG);
                            } else {
                                startActivityForResult(new Intent(LocationActivity.this, HelpCenterActivity.class), AppUtils.REQ_CHANGE_LANG);
                            }

                            break;
                        case 8:
                            startActivityForResult(new Intent(LocationActivity.this, HelpCenterActivity.class), AppUtils.REQ_CHANGE_LANG);
                            break;
                        case 9:
                            //TODO: Sign Out

                            appInstance.setAuthkey("");
                            AppUtils.APPTOKEN = "";
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_USER_ID, "");
                            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.PREF_LOGGED_IN, String.valueOf(false));
                            checkForLoggedInStatus();
                            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_logout_success), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            break;    }
                }
            }, 250);
        }
    }

    class City {
        String id, name;


    }

    class Region {
        String id, name;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForLoggedInStatus();
        String langChoice = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG);
        if ("ar".equals(langChoice)) {
            rbLangAr.setChecked(true);
            tvChooseCity.setText("اختر مدينتك");
            tvChooseRegion.setText("اختر منطقتك");
        } else {
            rbLangEn.setChecked(true);
            tvChooseCity.setText("Choose Your City");
            tvChooseRegion.setText("Choose Your Region");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break; }
        return false;
    }

    @Override
    public void onBackPressed() {
        toggle = false;
        if (reqdLoc) setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }
    private void get_playstore_version_code(final int curret_version_code) {
        try {
            // final ProgressDialog dialog = AppUtils.showProgress(context.this, getString(R.string.msg_placing_order), getString(R.string.msg_please_wait));

            ApiInterface apiService = ApiClient.GetClient().create(ApiInterface.class);
            Call<PlaystoreVersionReq> call = apiService.get_playstore_version(AppUtils.APPTOKEN,AppUtils.AUTHRIZATIONKEY);
            call.enqueue(new Callback<PlaystoreVersionReq>() {
                @Override
                public void onResponse(Call<PlaystoreVersionReq> call, retrofit2.Response<PlaystoreVersionReq> response) {
                    if(response!=null){
                    Log.e("RESPONSE>>>>", response.body().toString() + ">>>>>>>>>>>>>>");
                    System.out.println("VERSION CHECK : " + response.body().toString());
                    PlaystoreVersionReq res = response.body();
                    System.out.println("VERSION CHECK : curret_version_code : " + curret_version_code);
                    System.out.println("VERSION CHECK : res " + res.getAndroid().toString());

                    if (res != null) {
                        if (Integer.parseInt(res.getAndroid()) > 0) {

                            if (curret_version_code < Integer.parseInt(res.getAndroid())) {
                                showForceUpdateDialog();
                            }
                        }
                    }
                }

                }

                @Override
                public void onFailure(Call<PlaystoreVersionReq> call, Throwable t) {
                    System.out.print(t.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showForceUpdateDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(LocationActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog));

        alertDialogBuilder.setTitle(getResources().getString(R.string.youAreNotUpdatedTitle));
        alertDialogBuilder.setMessage(getResources().getString(R.string.youAreNotUpdatedMessage));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + LocationActivity.this.getPackageName())));
                dialog.cancel();
            }
        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.skip), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialogBuilder.show();
    }
}
