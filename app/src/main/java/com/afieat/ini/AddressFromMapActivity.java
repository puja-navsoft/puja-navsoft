package com.afieat.ini;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

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
import android.media.audiofx.Virtualizer;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.afieat.ini.databinding.ActivityAddressFromMapBinding;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddressFromMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener{

    ActivityAddressFromMapBinding binding;
    GoogleMap mMap;
    private double mLat,mLng;
    FusedLocationProviderClient fusedLocationProviderClient;
    private String region,city;
    private AlertDialog dialogCity, dialogRegion;
    private ListView lvCities,lvRegions;
    private List<City> cities;
    Dialog afieatGifLoaderDialog;
    private List<Region> regions;
    private String mSavedAddress1, mSavedAddress2;
    private String mUserId, mUserFName, mUserLName, mUserFatherName, mUserFamilyName, mUserCityId, mUserRegionId, mUserPh, mCity, mRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_address_from_map);

        lvCities = new ListView(AddressFromMapActivity.this);
        lvRegions = new ListView(AddressFromMapActivity.this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AddressFromMapActivity.this);
        getAddressFromLocation();

        AlertDialog.Builder builderCities = new AlertDialog.Builder(AddressFromMapActivity.this);
        builderCities.setView(lvCities);
        dialogCity = builderCities.create();

        AlertDialog.Builder builderRegions = new AlertDialog.Builder(AddressFromMapActivity.this);
        builderRegions.setView(lvRegions);
        dialogRegion = builderRegions.create();

        lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogCity.hide();
                binding.tetCity.setText(cities.get(position).name);
                mUserCityId = cities.get(position).id;
                loadRegionListFromNW(cities.get(position).id);
            }
        });

        lvRegions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogRegion.hide();
                binding.tetRegion.setText(regions.get(position).name);
                mUserRegionId = regions.get(position).id;
            }
        });

        binding.tetCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCity.show();
            }
        });

        binding.tetRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRegion.show();
            }
        });


//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(AddressFromMapActivity.this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(mLat,mLng);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_home)));

        Log.i("location", "onMapReady: "+latLng);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13f));
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latLng.latitude,latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            region = addresses.get(0).getSubLocality();
            city = addresses.get(0).getSubAdminArea();

            Toast.makeText(this, "Region: "+region+ " City: "+city, Toast.LENGTH_SHORT).show();
//            System.out.println(addresses.get(0).getLocality());
        }

        mMap.setOnMapClickListener(this);


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
            ActivityCompat.requestPermissions(AddressFromMapActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
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
                        mLat = location.getLatitude();
                        mLng = location.getLongitude();
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(AddressFromMapActivity.this);
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

                                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                        .findFragmentById(R.id.map);
                                mapFragment.getMapAsync(AddressFromMapActivity.this);
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

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_home)));
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latLng.latitude,latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            region = addresses.get(0).getSubLocality();
            city = addresses.get(0).getSubAdminArea();
            Toast.makeText(this, "Region: "+region+ " City: "+city, Toast.LENGTH_SHORT).show();
//            System.out.println(addresses.get(0).getLocality());
        }

    }

    private void loadSavedDataFromNW() {
        //  final ProgressDialog progressDialog = AppUtils.showProgress(AddressAddActivity.this, "", getString(R.string.msg_please_wait));
        // afieatGifLoaderDialog();
        Map<String, String> params = new HashMap<>();
//        params.put("address_id", addressId);
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

                            // Sunit 18-01-2017
                            mSavedAddress1 = mResponse.optString("address");
                            // mSavedAddress1 = mResponse.optString("latitude") + "," + mResponse.optString("longitude");
//                            editModeAddress = mResponse.optString("address");

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
//        tetName.setText(fullname);
//        tetFathersName.setText(mUserFatherName);
//        tetFamilyName.setText(mUserFamilyName);
//        tetCity.setText(mCity);
//        tetRegion.setText(mRegion);
//        tetPhone.setText(mUserPh);
        if (mUserCityId.trim().length() > 0) {
            loadRegionListFromNW(mUserCityId);
        }
//        if (inEditMode) {
//            tetAddress1.setText(mSavedAddress1);
//            tetAddress2.setText(mSavedAddress2);
//        }
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
                        lvCities.setAdapter(new ArrayAdapter<>(AddressFromMapActivity.this, R.layout.layout_simple_list_item, citiesArray[0]));
//                        if (inEditMode) {
//                            loadSavedDataFromNW();
//                        }
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
                            AppUtils.showViews(binding.tetRegion);
                            lvRegions.setAdapter(new ArrayAdapter<>(AddressFromMapActivity.this, R.layout.layout_simple_list_item, regionsArray[0]));
//                            if (inEditMode) {
//                                for (Region region : regions) {
//                                    if (mUserRegionId.equals(region.id)) {
//                                        binding.tetRegion.setText(region.name);
//                                        break;
//                                    }
//                                }
//                            }
                        } else {
                            AppUtils.hideViews(binding.tetRegion);
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

    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(this);
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }

    class City {
        String id, name;
    }

    class Region {
        String id, name;
    }
}