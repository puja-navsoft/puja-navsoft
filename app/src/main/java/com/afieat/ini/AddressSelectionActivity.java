package com.afieat.ini;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.adapters.AddressAdapter;
import com.afieat.ini.interfaces.OnAddressChangeRequestListener;
import com.afieat.ini.models.Address;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
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

public class AddressSelectionActivity extends AppCompatActivity {

    private List<Address> butlerAddresses;
    private List<Address> restaurantAddresses;
    private String mUserId, mAddressId;
    private boolean reqAddress;


    private FloatingActionButton fabAddAddress;
    private TextView tvNoAddress, tvAddAddress;
    private ListView lvAddress;
    // private ProgressBar progressBar;
    Dialog afieatGifLoaderDialog;

    private final int REQUEST_ADD_ADDRESS = 100;
    private final int REQUEST_EDIT_ADDRESS = 101;
    private final int REQUEST_LOGIN = 102;

    Intent intent;
    String from = "";

    public AddressSelectionActivity() {
        butlerAddresses = new ArrayList<>();
        restaurantAddresses = new ArrayList<>();
        reqAddress = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_selection);
         Toolbar mToolbar;
        tvNoAddress = (TextView) findViewById(R.id.tvNoAddress);
        fabAddAddress = (FloatingActionButton) findViewById(R.id.fabAddAddress);
        tvAddAddress = (TextView) findViewById(R.id.tvAddAddress);
        intent = getIntent();

        reqAddress = getIntent().getBooleanExtra(AppUtils.EXTRA_REQ_ADDRESS, false);

        mToolbar = (Toolbar) findViewById(R.id.appbar);
        mToolbar.setTitle(getString(R.string.title_address));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXXLight));
        from = intent.getStringExtra("from");

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //  progressBar = (ProgressBar) findViewById(R.id.progressBar);

        lvAddress = (ListView) findViewById(R.id.lvAddress);
        lvAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (reqAddress) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(AppUtils.EXTRA_ADDRESS_ID, String.valueOf(id));
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        mUserId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);

        if (mUserId.length() == 0) {
            startActivityForResult(new Intent(AddressSelectionActivity.this, LoginActivity.class), REQUEST_LOGIN);
        }
        if (AppUtils.isNetworkAvailable(getApplicationContext())) {
            loadAddressFromNW();
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.page), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.snack_retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadAddressFromNW();
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }

        fabAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAddAddress.performClick();
            }
        });
        tvAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserId.length() > 0) {
//                    if (from == null) {
//                        Intent intent = new Intent(AddressSelectionActivity.this, AddressAddActivity.class);
//                        intent.putExtra(AppUtils.EXTRA_IN_EDIT_MODE, false);
//                        startActivityForResult(intent, REQUEST_ADD_ADDRESS);
//                    } else if (from.equalsIgnoreCase("butler")){
//                        Intent intent = new Intent(AddressSelectionActivity.this, AddressFromMapActivity.class);
////                        intent.putExtra(AppUtils.EXTRA_IN_EDIT_MODE, false);
//                        startActivity(intent);
//                    }

                    Intent intent = new Intent(AddressSelectionActivity.this, AddressAddActivity.class);
                    intent.putExtra(AppUtils.EXTRA_IN_EDIT_MODE, false);
                    if (from!=null && from.equalsIgnoreCase("butler"))
                        intent.putExtra("origin","butler");
                    startActivityForResult(intent, REQUEST_ADD_ADDRESS);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_please_login), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadAddressFromNW() {
        AppUtils.hideViews(tvNoAddress);
        butlerAddresses.clear();
        restaurantAddresses.clear();
        //  final ProgressDialog dialog = AppUtils.showProgress(AddressSelectionActivity.this, "", getString(R.string.msg_please_wait));
        afieatGifLoaderDialog();
        Map<String, String> params = new HashMap<>();
        params.put("userid", mUserId);
        final NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.LIST_ADDRESS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //   AppUtils.hideProgress(dialog);
                        afieatGifLoaderDialog.dismiss();
                        AppUtils.log(response);
                        String msg = response.optString("msg");
                        if ("No record exist".equals(msg)) {
                            AppUtils.showViews(tvNoAddress);
                        } else if ("Success".equals(msg)) {
                            JSONArray addressArray = response.optJSONArray("address_list");
                            int i = 0;
                            while (i < addressArray.length()) {
                                Address address = new Address();
                                JSONObject addressObject = addressArray.optJSONObject(i);
                                address.setId(addressObject.optString("id"));
                                address.set_default("1".equals(addressObject.optString("is_default")));
                                String address1String = addressObject.optString("address");
                                String address2String = addressObject.optString("address_two");
                                String is_butler = addressObject.optString("is_butler");
                                String location_address = addressObject.optString("location_address");
                                String addressString = address1String;
                                if (address2String.length() > 0)
                                    addressString += ", " + address2String;
                                address.setDescription(address2String);
                                address.setAddress(location_address);
                                address.setName(addressObject.optString("firstname") + " " + addressObject.optString("lastname"));
                                address.setPhone(addressObject.optString("phone"));
                                if (is_butler!=null && is_butler.equalsIgnoreCase("1"))
                                    butlerAddresses.add(address);
                                else
                                    restaurantAddresses.add(address);
                                i++;
                            }
                            AddressAdapter addressAdapter;
                            if (from!=null && from.equalsIgnoreCase("butler")) {
                                addressAdapter = new AddressAdapter(AddressSelectionActivity.this, butlerAddresses,from);
                            }
                            else
                                addressAdapter = new AddressAdapter(AddressSelectionActivity.this, restaurantAddresses,from);
                            addressAdapter.setOnAddressChangeRequestListener(new OnAddressChangeRequestListener() {
                                @Override
                                public void onAddressChangeRequested(String adId) {
                                    Intent intent = new Intent(AddressSelectionActivity.this, AddressAddActivity.class);
                                    intent.putExtra(AppUtils.EXTRA_IN_EDIT_MODE, true);
                                    intent.putExtra(AppUtils.EXTRA_AD_ID, adId);
                                    if (from!=null && from.equalsIgnoreCase("butler"))
                                        intent.putExtra("origin","butler");
                                    startActivityForResult(intent, REQUEST_EDIT_ADDRESS);
                                }

                                @Override
                                public void onAddressSetDefaultRequested(String adId, int position) {
                                    if (from == null) {
                                        setDefaultOrDeleteAddress("default", adId, position);
                                    }
                                }

                                @Override
                                public void onAddressDeleteRequested(final String adId, final int position) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AddressSelectionActivity.this);
                                    builder.setMessage(getString(R.string.msg_sure_delete_address));
                                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            setDefaultOrDeleteAddress("delete", adId, position);
                                        }
                                    });
                                    builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    });
                                    AlertDialog dialog1 = builder.create();
                                    dialog1.show();
                                }
                            });
                            lvAddress.setAdapter(addressAdapter);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        afieatGifLoaderDialog.dismiss();
                        //AppUtils.hideProgress(dialog);
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void setDefaultOrDeleteAddress(final String addressAction, String adId, final int position) {
        //  AppUtils.showViews(progressBar);
        afieatGifLoaderDialog();
        String api = "";
        String msg = "";
        Map<String, String> params = new HashMap<>();
        params.put("address_id", adId);
        if ("default".equals(addressAction)) {
            api = Apis.SET_ADDRESS_DEFAULT;
            params.put("userid", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        } else if ("delete".equals(addressAction)) {
            api = Apis.DELETE_ADDRESS;
        }
        NetworkRequest request = new NetworkRequest(Request.Method.POST, api, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // AppUtils.hideViews(progressBar);
                        afieatGifLoaderDialog.dismiss();
                        if (from!=null && from.equalsIgnoreCase("butler")) {
                            /*if ("default".equals(addressAction)) {

                                for (Address address : butlerAddresses) {
                                    if (position == butlerAddresses.indexOf(address)) {
                                        address.set_default(true);
                                    } else {
                                        address.set_default(false);
                                    }
                                }
                                ((AddressAdapter) lvAddress.getAdapter()).notifyDataSetChanged();
                            } else*/ if ("delete".equals(addressAction)) {
                                if ("Success".equals(response.optString("msg"))) {
                                    butlerAddresses.remove(position);
                                    ((AddressAdapter) lvAddress.getAdapter()).notifyDataSetChanged();
                                }
                            }
                        }
                        else{
                            if ("default".equals(addressAction)) {

                                for (Address address : restaurantAddresses) {
                                    if (position == restaurantAddresses.indexOf(address)) {
                                        address.set_default(true);
                                    } else {
                                        address.set_default(false);
                                    }
                                }
                                ((AddressAdapter) lvAddress.getAdapter()).notifyDataSetChanged();
                            } else if ("delete".equals(addressAction)) {
                                if ("Success".equals(response.optString("msg"))) {
                                    restaurantAddresses.remove(position);
                                    ((AddressAdapter) lvAddress.getAdapter()).notifyDataSetChanged();
                                }
                            }
                        }
                        AppUtils.log(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        afieatGifLoaderDialog.dismiss();
                        //AppUtils.hideViews(progressBar);
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ADD_ADDRESS:
            case REQUEST_EDIT_ADDRESS:
                if (resultCode == AddressAddActivity.RESULT_ADDED) {
                    loadAddressFromNW();
                }
                break;

            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    mUserId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID);
                    loadAddressFromNW();
                }
                break;

                default:

                    break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }

    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(this);
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }
}
