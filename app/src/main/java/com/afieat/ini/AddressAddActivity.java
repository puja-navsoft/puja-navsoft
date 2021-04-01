package com.afieat.ini;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.textfield.TextInputLayout;
import com.vlk.multimager.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddressAddActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener{


    private ScrollView svAddAddress;
    private TextInputEditText tetAddress1, tetAddress2, tetAddress3, tetCity, tetRegion, tetPhone, tetName, tetFathersName, tetFamilyName;
    private TextView tvGetAddress,addressAutoHiding;
    private ListView lvCities, lvRegions;
    private Button btnSave,btnOk;
    private AlertDialog dialogCity, dialogRegion;
    Dialog afieatGifLoaderDialog;
    private String mUserId, mUserFName, mUserLName, mUserFatherName, mUserFamilyName, mUserCityId, mUserRegionId, mUserPh, mCity, mRegion;
    private String mSavedAddress1, mSavedAddress2;
    private double mLat, mLng;
    private TextInputLayout citySp,regionSp, phoneLayout;
    LinearLayout locationTv;
    RelativeLayout page;

    GoogleMap mMap;

    private List<City> cities;
    private List<Region> regions;

    private boolean inEditMode = false;
    private String addressId, addressField1;

    private final int REQUEST_PLACE_AUTOCOMPLETE = 100;
    public static final int RESULT_ADDED = 999;
    public static final int RESULT_EDITED = 998;
    View v;

    FusedLocationProviderClient fusedLocationProviderClient;

    final static int REQ_LOCATION_SETTINGS = 199;

    private String editModeAddress = "";
    private double currentmLat,currentmLng;
    private LatLng latLng;
    private SupportMapFragment mapFragment;
    private boolean isButler = false;


    public AddressAddActivity() {
        cities = new ArrayList<>();
        regions = new ArrayList<>();
        addressField1 = "";
        mLat = mLng = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_add);

        Toolbar mToolbar;
        mToolbar = findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.add_new_address));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXXLight));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AddressAddActivity.this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);

        svAddAddress = findViewById(R.id.svAddAddress);
        tvGetAddress = findViewById(R.id.tvGetAddress);
        tetAddress1 = findViewById(R.id.tetAddress1);
        tetAddress2 = findViewById(R.id.tetAddress2);
        tetAddress3 = findViewById(R.id.tetAddress3);
        tetCity = findViewById(R.id.tetCity);
        tetRegion = findViewById(R.id.tetRegion);
        tetName = findViewById(R.id.tetName);
        tetFathersName = findViewById(R.id.tetFathersName);
        tetFamilyName = findViewById(R.id.tetFamilyName);
        tetPhone = findViewById(R.id.tetPhone);
        btnSave = findViewById(R.id.btnSave);
        btnOk = findViewById(R.id.btnOk);
        citySp = findViewById(R.id.citySp);
        addressAutoHiding = findViewById(R.id.addressViewAutoHide);
        regionSp = findViewById(R.id.areaSp);
        phoneLayout = findViewById(R.id.phoneLayout);
        page = findViewById(R.id.page);
        locationTv = findViewById(R.id.locationTv);
        lvCities = new ListView(AddressAddActivity.this);
        lvRegions = new ListView(AddressAddActivity.this);

        if (getIntent().getBooleanExtra(AppUtils.EXTRA_IN_EDIT_MODE, false)) {
            inEditMode = true;
            addressId = getIntent().getStringExtra(AppUtils.EXTRA_AD_ID);
            btnSave.setText(getString(R.string.menu_update));
        }

        if (getIntent().getStringExtra("origin")!=null && getIntent().getStringExtra("origin").equalsIgnoreCase("butler"))
        {
            isButler = true;
            svAddAddress.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
            btnOk.setVisibility(View.VISIBLE);
        }
        AppUtils.hideViews(svAddAddress);

        AlertDialog.Builder builderCities = new AlertDialog.Builder(AddressAddActivity.this);
        builderCities.setView(lvCities);
        dialogCity = builderCities.create();

        AlertDialog.Builder builderRegions = new AlertDialog.Builder(AddressAddActivity.this);
        builderRegions.setView(lvRegions);
        dialogRegion = builderRegions.create();

        tetAddress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<Address> geocoder = new Geocoder(AddressAddActivity.this).getFromLocation(latLng.latitude,latLng.longitude,3);
//                    Snackbar.make(page,String.valueOf(geocoder.get(0).getAddressLine(0)),Snackbar.LENGTH_LONG);
                    Toast.makeText(AddressAddActivity.this, ""+geocoder.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();
//                    addressAutoHiding.setText(geocoder.get(0).getAddressLine(0));
//                    addressAutoHiding.setVisibility(View.VISIBLE);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            addressAutoHiding.setVisibility(View.GONE);
//                        }
//                    },1000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogCity.hide();
                tetCity.setText(cities.get(position).name);
                mUserCityId = cities.get(position).id;
                loadRegionListFromNW(cities.get(position).id);
            }
        });

        lvRegions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogRegion.hide();
                tetRegion.setText(regions.get(position).name);
                mUserRegionId = regions.get(position).id;
            }
        });

        tetCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCity.show();
            }
        });

        tetRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRegion.show();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                mMap.getUiSettings().setAllGesturesEnabled(false);
                citySp.setVisibility(View.GONE);
                regionSp.setVisibility(View.GONE);
                locationTv.setVisibility(View.GONE);
                phoneLayout.setVisibility(View.VISIBLE);
                AppUtils.showViews(svAddAddress);
                btnSave.setVisibility(View.VISIBLE);
                btnOk.setVisibility(View.GONE);
                svAddAddress.setElevation(10);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tetPhone.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_enter_phone), Toast.LENGTH_SHORT).show();
                } else  if (tetAddress2.getText().toString().trim().length()==0){
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_enter_description), Toast.LENGTH_SHORT).show();

                }

                else {
                    saveAddress();

                }


/*
                if (fieldsValid()) {

                    saveAddress();
                }
*/
            }
        });

        if (!inEditMode) {
            loadUserData();
            showUserData();
        } else {
            mToolbar.setTitle(getString(R.string.title_update_address));
        }

        if (getIntent().getStringExtra("origin")!=null && getIntent().getStringExtra("origin").equalsIgnoreCase("butler"))
        {
            svAddAddress.setVisibility(View.GONE);
        }

        loadCityListFromNW();

//        getAddressFromLocation();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogCity.dismiss();
        dialogRegion.dismiss();
    }

//    public void getAddressFromLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            AppUtils.log("Force request location permission");
//            ActivityCompat.requestPermissions(AddressAddActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//            return;
//        }
//        try {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, AddressAddActivity.this);
//
//        } catch (IllegalStateException e) {
//            Toast.makeText(getApplicationContext(), getString(R.string.try_again), Toast.LENGTH_SHORT);
//        }
//        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (location == null) {
//            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                    .addLocationRequest(mLocationRequest);
//            builder.setAlwaysShow(true);
//            PendingResult<LocationSettingsResult> result =
//                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
//            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//                @Override
//                public void onResult(LocationSettingsResult result) {
//                    final Status status = result.getStatus();
//                    final LocationSettingsStates state = result.getLocationSettingsStates();
//                    switch (status.getStatusCode()) {
//                        case LocationSettingsStatusCodes.SUCCESS:
//                            getAddressFromLocation();
//                            /*startService(new Intent(SplashActivity.this, LocationUpdateService.class));
//                            new TokenFetchTask().execute();*/
//                            break;
//                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                            // Location settings are not satisfied. But could be fixed by showing the user
//                            // a dialog.
//                            try {
//                                status.startResolutionForResult(AddressAddActivity.this, REQ_LOCATION_SETTINGS);
//                            } catch (IntentSender.SendIntentException e) {
//                                finish();
//                            }
//                            break;
//                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                            finish();
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            });
//        } else {
//            try {
//                Geocoder geocoder = new Geocoder(getApplicationContext()/*, new Locale("ar")*/);
//                android.location.Address address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
////                android.location.Address address = geocoder.getFromLocation(30.4896186, 47.8162637, 2).get(1);
////                android.location.Address address = geocoder.getFromLocation(30.0444, 31.2357, 2).get(1);
//                mLat = location.getLatitude();
//                mLng = location.getLongitude();
////                tetAddress1.setText(address.getAddressLine(0) + ", " + address.getAddressLine(1) + ", " + address.getAddressLine(2));
////                addressField1 = address.getAddressLine(0) + ", " + address.getAddressLine(1) + ", " + address.getAddressLine(2);
//                tetAddress1.setText(mLat + ", " + mLng);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void loadSavedDataFromNW() {
        //  final ProgressDialog progressDialog = AppUtils.showProgress(AddressAddActivity.this, "", getString(R.string.msg_please_wait));
        // afieatGifLoaderDialog();
        Map<String, String> params = new HashMap<>();
        params.put("address_id", addressId);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_ADDRESS_DETAILS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //AppUtils.hideProgress(progressDialog);
                        //   afieatGifLoaderDialog.dismiss();
                        JSONObject mResponse = response.optJSONArray("address_list").optJSONObject(0);

                        if (mResponse != null) {
                            mUserFName = mResponse.optString("firstname");
                            mUserLName = mResponse.optString("lastname");
                            mUserFatherName = mResponse.optString("fathers_name");
                            mUserFamilyName = mResponse.optString("grandfathers_name");
                            mUserPh = mResponse.optString("phone");
                            mUserCityId = mResponse.optString("city");
                            mUserRegionId = mResponse.optString("province_id");
                            getAddressFromLocation();

                            // Sunit 18-01-2017
                            mSavedAddress1 = mResponse.optString("address");
                            // mSavedAddress1 = mResponse.optString("latitude") + "," + mResponse.optString("longitude");
                            editModeAddress = mResponse.optString("address");

                            try {
                                mLng = Double.parseDouble(mResponse.optString("longitude"));
                                mLat = Double.parseDouble(mResponse.optString("latitude"));

                            } catch (Exception e) {

                            }

                            mSavedAddress2 = mResponse.optString("address_two");

                            for (City city : cities) {
                                if (city.id.equals(mUserCityId)) {
                                    mCity = city.name;
                                    break;
                                }
                            }
                            showUserData();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //afieatGifLoaderDialog.dismiss();
                        // AppUtils.hideProgress(progressDialog);
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void loadUserData() {
        mUserFName = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_FNAME);
        mUserLName = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LNAME);
        mUserFatherName = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_FATHER);
        mUserFamilyName = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_FAMILY);
        mUserPh = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_PHONE);
        mUserRegionId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_REGION_ID);
        mUserCityId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_CITY_ID);
        mCity = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_CITY);
        mRegion = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_REGION);
    }

    private void showUserData() {
        String fullname = mUserFName + " " + mUserLName;
        fullname = fullname.trim();
        tetName.setText(fullname);
        tetFathersName.setText(mUserFatherName);
        tetFamilyName.setText(mUserFamilyName);
        tetCity.setText(mCity);
        tetRegion.setText(mRegion);
        tetPhone.setText(mUserPh);
        if (mUserCityId.trim().length() > 0) {
            loadRegionListFromNW(mUserCityId);
        }
        if (inEditMode) {
            tetAddress1.setText(mSavedAddress1);
            tetAddress2.setText(mSavedAddress2);
        }
    }

    private void loadCityListFromNW() {
        final String[][] citiesArray = new String[1][1];
        cities.clear();
        //   final ProgressDialog progressDialog = AppUtils.showProgress(AddressAddActivity.this, "", getString(R.string.msg_please_wait));
        // afieatGifLoaderDialog();
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
                        JSONArray cityArray = response.optJSONArray("cityllist");
                        citiesArray[0] = new String[cityArray.length()];
                        int i = 0;
                        while (i < cityArray.length()) {
                            JSONObject cityObject = cityArray.optJSONObject(i);
                            City city = new City();
                            city.id = cityObject.optString("city_id");
                            city.name = cityObject.optString("city_name");
                            cities.add(city);
                            citiesArray[0][i] = city.name;
                            i++;
                        }
                        //  AppUtils.hideProgress(progressDialog);
                        // afieatGifLoaderDialog.dismiss();
                        if (getIntent().getStringExtra("origin")!=null && getIntent().getStringExtra("origin").equalsIgnoreCase("butler"))
                            AppUtils.hideViews(svAddAddress);
                        else
                            AppUtils.showViews(svAddAddress);
                        lvCities.setAdapter(new ArrayAdapter<>(AddressAddActivity.this, R.layout.layout_simple_list_item, citiesArray[0]));
                        if (inEditMode) {
                            loadSavedDataFromNW();
                        }
                        else
                            getAddressFromLocation();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //  AppUtils.hideProgress(progressDialog);
                        //  afieatGifLoaderDialog.dismiss();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void loadRegionListFromNW(String cityId) {
        final String[][] regionsArray = new String[1][1];
        regions.clear();
        Map<String, String> params = new HashMap<>();
        params.put("city_id", cityId);
        if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            params.put("lang", "en");
        } else {
            params.put("lang", "ar");
        }
        //  final ProgressDialog progressDialog = AppUtils.showProgress(AddressAddActivity.this, "", getString(R.string.msg_please_wait));
        afieatGifLoaderDialog();
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.LIST_REGIONS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // AppUtils.hideProgress(progressDialog);
                        if (afieatGifLoaderDialog != null) {
                            afieatGifLoaderDialog.dismiss();
                        }
                        JSONArray array = response.optJSONArray("regionlist");
                        if (array.length() > 0) {
                            regionsArray[0] = new String[array.length()];
                            int i = 0;
                            while (i < array.length()) {
                                JSONObject regionObject = array.optJSONObject(i);
                                Region region = new Region();
                                region.id = regionObject.optString("region_id");
                                region.name = regionObject.optString("region_name");
                                regions.add(region);
                                regionsArray[0][i] = region.name;
                                i++;
                            }
                            AppUtils.showViews(tetRegion);
                            lvRegions.setAdapter(new ArrayAdapter<>(AddressAddActivity.this, R.layout.layout_simple_list_item, regionsArray[0]));
                            if (inEditMode) {
                                for (Region region : regions) {
                                    if (mUserRegionId.equals(region.id)) {
                                        tetRegion.setText(region.name);
                                        break;
                                    }
                                }
                            }
                        } else {
                            AppUtils.hideViews(tetRegion);
                            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_regions), Snackbar.LENGTH_SHORT).show();
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
                        //  AppUtils.hideProgress(progressDialog);
                        //afieatGifLoaderDialog.dismiss();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private boolean fieldsValid() {
        /*if (tetName.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_enter_name), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tetFathersName.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_enter_father_name), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tetFamilyName.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_enter_family_name), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tetPhone.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_enter_phone), Toast.LENGTH_SHORT).show();
            return false;
        }*/
        if (tetCity.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_select_city), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tetRegion.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_select_region), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tetAddress1.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_add_address), Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            if (mLat == 0 && mLng == 0) {
                android.location.Address address = new Geocoder(getApplicationContext()).getFromLocationName(tetAddress1.getText().toString() + ", " + tetAddress2.getText().toString() + ", "
                        + tetRegion.getText().toString() + ", " + tetCity.getText().toString(), 1).get(0);
                mLat = address.getLatitude();
                mLng = address.getLongitude();
            }
        } catch (IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            android.location.Address address = null;
            try {
                address = new Geocoder(getApplicationContext()).getFromLocationName(tetCity.getText().toString(), 1).get(0);
                mLat = address.getLatitude();
                mLng = address.getLongitude();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return true;
    }

    private void saveAddress() {
        //final ProgressDialog progressDialog = AppUtils.showProgress(AddressAddActivity.this, "Saving address", getString(R.string.msg_please_wait));
        afieatGifLoaderDialog();
        Map<String, String> params = new HashMap<>();
        String fullname = tetName.getText().toString().trim();
        String firstname = fullname;
        String lastname = "";
        if (fullname.contains(" ")) {
            firstname = fullname.split(" ")[0];
            for (int i = 1; i < fullname.split(" ").length; i++) {
                if (i > 1)
                    lastname += " ";
                lastname += fullname.split(" ")[i];
            }
        }
        if (inEditMode) params.put("address_id", addressId);
        params.put("userid", mUserId);
        params.put("location_address",tetAddress3.getText().toString().trim());
        /*params.put("firstname", firstname);
        params.put("lastname", lastname);
        params.put("fathers_name", tetFathersName.getText().toString().trim());*/
//        if (addressField1.trim().length() == 0) {
        addressField1 = tetAddress1.getText().toString().trim();
        //       }
        params.put("address", addressField1);
/*        if(inEditMode){
            params.put("address", editModeAddress);
        }*/
        params.put("address_two", tetAddress2.getText().toString().trim());
        params.put("city", mUserCityId);
        params.put("province_id", mUserRegionId);
        if (getIntent().getStringExtra("origin")!=null && getIntent().getStringExtra("origin").equalsIgnoreCase("butler"))
            params.put("phone", tetPhone.getText().toString().trim());
        params.put("longitude", String.valueOf(mLng));
        params.put("latitude", String.valueOf(mLat));
        if (getIntent().getStringExtra("origin")!=null && getIntent().getStringExtra("origin").equalsIgnoreCase("butler"))
            params.put("is_butler","1");
        else
            params.put("is_butler","0");
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.SAVE_ADDRESS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Rahul : AddressAddActivity : saveAddress :  response : " + response);
                        // AppUtils.hideProgress(progressDialog);
                        afieatGifLoaderDialog.dismiss();
                        String msg = response.optString("msg");
                        if (msg != null) {
                            if ("Address added successfully".equalsIgnoreCase(msg) || "Address updated successfully".equalsIgnoreCase(msg)) {
                                msg = getString(R.string.msg_address_added);
                                if (inEditMode) msg = getString(R.string.msg_address_updated);
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                setResult(RESULT_ADDED);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.msg_operation_not_completed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //        AppUtils.hideProgress(progressDialog);
                        afieatGifLoaderDialog.dismiss();
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_address, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (svAddAddress.getVisibility() == View.VISIBLE && isButler){
                    svAddAddress.setVisibility(View.GONE);
                    locationTv.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.GONE);
                    btnOk.setVisibility(View.VISIBLE);
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                }
                else
                    onBackPressed();
                break;
            case R.id.menu_save_address:
                if (btnSave.getVisibility() == View.VISIBLE)
                    btnSave.performClick();
                else
                    btnOk.performClick();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //   mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onMapClick(LatLng latLng) {

        if (svAddAddress.getVisibility() != View.VISIBLE) {

            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker)));
            mLat = latLng.latitude;
            mLng = latLng.longitude;
            if (getIntent().getStringExtra("origin") != null && getIntent().getStringExtra("origin").equalsIgnoreCase("butler"))
                locationTv.setVisibility(View.VISIBLE);
            List<Address> geocoder = null;
            try {
                geocoder = new Geocoder(AddressAddActivity.this).getFromLocation(latLng.latitude, latLng.longitude, 3);
                tetAddress3.setText(geocoder.get(0).getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }

            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

          //  tetAddress1.setText("" + latLng.latitude + ", " + latLng.longitude);
            tetAddress1.setText(tetAddress3.getText().toString());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        if (mLat!=0.0) {
            latLng = new LatLng(mLat, mLng);
        }
        else {
            latLng = new LatLng(currentmLat, currentmLng);
        }

        if (getIntent().getStringExtra("origin")!=null && getIntent().getStringExtra("origin").equalsIgnoreCase("butler"))
            locationTv.setVisibility(View.VISIBLE);
        try {
            List<Address> geocoder = new Geocoder(AddressAddActivity.this).getFromLocation(latLng.latitude,latLng.longitude,3);
            tetAddress3.setText(geocoder.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker)));



        Log.i("location", "onMapReady: "+latLng);



        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13f));
      //  tetAddress1.setText(""+latLng.latitude+", "+latLng.longitude);
        List<Address> geocoder = null;
        try {
            geocoder = new Geocoder(AddressAddActivity.this).getFromLocation(latLng.latitude,latLng.longitude,3);

           // tetAddress1.setText(geocoder.get(0).getAddressLine(0));
            tetAddress1.setText(tetAddress3.getText().toString());
            Log.v("address","address"+tetAddress3.getText().toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (getIntent().getStringExtra("origin")!=null && getIntent().getStringExtra("origin").equalsIgnoreCase("butler")) {
            mMap.setOnMapClickListener(this);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
        }
    }


    class City {
        String id, name;
    }

    class Region {
        String id, name;
    }


    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(this);
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }

    public void getAddressFromLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            getCurrentLocation();

        }

        else{
            ActivityCompat.requestPermissions(AddressAddActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }
    }

    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    final Location location = task.getResult();
                    if (location!=null){
                        currentmLat = location.getLatitude();
                        currentmLng = location.getLongitude();
                        v = getSupportFragmentManager().findFragmentById(R.id.map).getView();
                        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(AddressAddActivity.this);
                    }
                    else{
                        LocationRequest request = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(1000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                mLat = location1.getLatitude();
                                mLng = location1.getLongitude();
                                v = getSupportFragmentManager().findFragmentById(R.id.map).getView();
                                mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                        .findFragmentById(R.id.map);
                                mapFragment.getMapAsync(AddressAddActivity.this);
                            }
                        };

                        fusedLocationProviderClient.requestLocationUpdates(request,locationCallback, Looper.myLooper());
                    }


                }
            });
        } else{
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode ==100 && grantResults.length>0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            getCurrentLocation();

        }else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    // ===////////////////////////

    //Puja


}

