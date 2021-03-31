package com.afieat.ini;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.afieat.ini.adapters.NewSearchResultAdapter;
import com.afieat.ini.database.DBHelper;
import com.afieat.ini.misc.SearchResultsJsonParser;
import com.afieat.ini.models.RestaurantModel.RestaurantListModel;
import com.afieat.ini.models.Search;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.EndlessRecyclerOnScrollListener;
import com.afieat.ini.utils.NetworkRequest;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.IndexQuery;
import com.algolia.search.saas.Query;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private static final String TAG = "SearchActivity";
    private AppInstance appInstance;
    private String mUserId,
            mRegionId,
            mRegionName,
            mCityName,
            mCityId,
            GroupID;
    private final int REQUEST_LOCATION = 100;
    private EditText et_search;
    private String SearchInput = "";
    private Search search;
    private ArrayList<Search> searchList = new ArrayList<Search>();
    private SearchView searchView;
    private ListView lvSearchResults;
    private int merchant_id = 0;
    private String status = "";
    private String restaurant_name = "";
    private String restaurant_name_ar = "";
    private String item_id = "";
    private String item_name = "";
    private String item_name_ar = "";
    private String cuisine_id = "";
    private String cuisine_name = "";
    private String photo_url = "";
    private String cuisine_name_ar = "";
    private String index = "";
    //private SearchResponse.ResultsBean.HitsBean search;

    private int lastRequestedPage = 0;
    private int lastDisplayedPage = 0;
    private boolean endReached = false;
    private Client client;
    private int lastDisplayedSeqNo;
    private int currentSearchSeqNo;
    private int lastSearchedSeqNo;
    private int pageCount = 0;
    private SearchResultsJsonParser resultsParser;
    private RecyclerView rvSearchResults;
    private LinearLayoutManager mLinearLayoutManager;
    private NewSearchResultAdapter newSeacrhAdapter;
    private EndlessRecyclerOnScrollListener scrollListener;
    private boolean clearSearch = false;
    private boolean closeClicked = false;
    private ImageView iv_search;
    private ImageView iv_close;
    private String lang = "";
    private Map<String, JSONObject> hashMap = new HashMap<String, JSONObject>();
    private RestaurantListModel mRestaurantListModel;
    private DBHelper mDbHelper;
    private String city_id,region_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        appInstance = AppInstance.getInstance(getApplicationContext());
        mDbHelper = new DBHelper(getApplicationContext());

        requestRestaurantList();
        /*searchView=(SearchView)findViewById(R.id.search);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search fo restaurant,cuisine or dish...");
        searchView.setFocusable(true);*/

        lang = appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG);

        iv_search = (ImageView) findViewById(R.id.iv_search);
        et_search = (EditText) findViewById(R.id.et_search);

        iv_close = (ImageView) findViewById(R.id.iv_close);

        rvSearchResults = (RecyclerView) findViewById(R.id.rvSearchResults);

        if ("ar".equals(lang)) {
            et_search.setHint("البحث عن مطعم أو مطبخ أو طبق ...");
        } else {
            et_search.setHint("Search for restaurant,cuisine or dish...");
        }

        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvSearchResults.setLayoutManager(mLinearLayoutManager);
        newSeacrhAdapter = new NewSearchResultAdapter(SearchActivity.this, searchList, lang);

        newSeacrhAdapter.setAdapterListener(new NewSearchResultAdapter.SearchListListener() {
            @Override
            public void onItemClick(Search item, int position) {


                System.out.println("Serac : msldmf  : " + item.getIndex());
                if (AppUtils.RESTAURANT_INDEX.equals(item.getIndex())) {

                    Intent intent = new Intent(getApplicationContext(), RestaurantsDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, String.valueOf(item.getMerchant_id()));
                    intent.putExtra("from", "search");
                    startActivity(intent);
                    return;
                }
                if (AppUtils.ITEMS_INDEX.equals(item.getIndex())) {

                    /*Intent intent = new Intent(getApplicationContext(), RestaurantListActivity.class);
                    intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, String.valueOf(item.getMerchant_id()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    if ("ar".equals(lang)) {

                        intent.putExtra(AppUtils.EXTRA_ITEM_NAME, item.getItem_name_ar());
                    } else {

                        intent.putExtra(AppUtils.EXTRA_ITEM_NAME, item.getItem_name());
                    }
                    intent.putExtra(AppUtils.EXTRA_FROM_SEARCH, "items");
                    startActivity(intent);*/

                    Intent intent = new Intent(getApplicationContext(), RestaurantsDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, String.valueOf(item.getMerchant_id()));
                    intent.putExtra("from", "search");
                    startActivity(intent);

                    return;
                }
                if (AppUtils.CUISINE_INDEX.equals(item.getIndex())) {

                    Intent intent = new Intent(getApplicationContext(), RestaurantListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(AppUtils.EXTRA_CUISINE_ID, item.getCuisine_id());
                    intent.putExtra(AppUtils.EXTRA_FROM_SEARCH, "cuisine");
                    startActivity(intent);
                }
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
                clearSearch();
            }
        });


        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                SearchInput = editable.toString();
                pageCount = 0;
                searchList.clear();
                //clearSearch();
                newSeacrhAdapter.notifyDataSetChanged();
                if (SearchInput.length() != 0) {

                    if (SearchInput.length() >= 3) {
                        Log.d("SearchInput", "onTextChanged: " + SearchInput);

                        loadData(SearchInput, pageCount);
                    } else if (SearchInput.length() < 3) {
                        clearSearch();
                        Log.e(TAG, "onQueryTextChange: ");

                    }
                } else if (SearchInput.length() == 0) {
                    clearSearch();
                    Log.e(TAG, "onQueryTextChange: ");

                }


            }
        });


        mRegionId = appInstance.getFromSharedPref(AppUtils.PREF_REGION_ID);
        mRegionName = appInstance.getFromSharedPref(AppUtils.PREF_REGION);
        mCityName = appInstance.getFromSharedPref(AppUtils.PREF_CITY);
        mCityId = appInstance.getFromSharedPref(AppUtils.PREF_CITY_ID);
        mUserId = appInstance.getFromSharedPref(AppUtils.PREF_USER_ID);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
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
                Intent intent = new Intent(SearchActivity.this, LocationActivity.class);
                intent.putExtra(AppUtils.EXTRA_REQ_LOCATION, String.valueOf(true));
                startActivityForResult(intent, REQUEST_LOCATION);
            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void clearSearch() {
        //Toast.makeText(SearchActivity.this, "CLOSE", Toast.LENGTH_SHORT).show();
       /* Log.d(TAG, "onClose: ");
        searchView.setQuery("",true);
        CharSequence text=searchView.getQuery();
        if (TextUtils.isEmpty(text)){
            searchList.removeAll(searches);
            newSeacrhAdapter.notifyDataSetChanged();
            rvSearchResults.getRecycledViewPool().clear();
        }*/
        //closeClicked=true;
        searchList.clear();
        newSeacrhAdapter.notifyDataSetChanged();
        SearchInput = "";
        pageCount = 0;


        /*if (SearchInput.length()!=0){
            loadData(SearchInput);
        }
        else {
            Toast.makeText(this, "Please enter something", Toast.LENGTH_SHORT).show();
        }*/
    }


    private void requestRestaurantList() {


        NetworkRequest request = new NetworkRequest(Request.Method.GET, Apis.GET_ALL_RESTAURANTS, null,
                new Response.Listener<JSONObject>() {
                    public String message;

                    @Override
                    public void onResponse(JSONObject response) {

                        Gson mGson = new Gson();

                        mRestaurantListModel = mGson.fromJson(response.toString(), RestaurantListModel.class);
                        mDbHelper.deleteAllRestaurant();
                        for (int i = 0; i < mRestaurantListModel.getRestaurants().size(); i++) {
                            mDbHelper.addRestauranttable(mRestaurantListModel.getRestaurants().get(i));
                        }
                        System.out.println("Rahul : SearchActivity : requestRestaurantList : response : " + response);

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

    private void loadData(final String searchInput, final int pCount) {


        client = new Client("V3AJ8MVX1S", "731b737b3ed56ff9e06e4b66a2504f8a");
        List<IndexQuery> queries = new ArrayList<>();

                  /*  queries.add(new IndexQuery("restaurants", new Query(SearchInput).setHitsPerPage(10)));
                    queries.add(new IndexQuery("items", new Query(SearchInput).setHitsPerPage(10)));
                    queries.add(new IndexQuery("cuisine", new Query(SearchInput).setHitsPerPage(10))); */

        queries.add(new IndexQuery(AppUtils.RESTAURANT_INDEX, new Query(searchInput).setHitsPerPage(50).setPage(pCount)));
        queries.add(new IndexQuery(AppUtils.ITEMS_INDEX, new Query(searchInput).setHitsPerPage(50).setPage(pCount)));
        queries.add(new IndexQuery(AppUtils.CUISINE_INDEX, new Query(searchInput).setHitsPerPage(50).setPage(pCount)));


        final Client.MultipleQueriesStrategy strategy = Client.MultipleQueriesStrategy.NONE; // Execute the sequence of queries until the end.

        client.multipleQueriesAsync(queries, strategy, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                // [...]

                try {
                    Log.d("SEARCH==>", "requestCompleted: " + content.toString());
                    System.out.println("SearchActivity : requestCompleted : " + content.toString());
                    JSONArray totalArray = content.getJSONArray("results");
                    //Log.d("SEARCH_ARRAY==>", "requestCompleted: "+totalArray);

                    hashMap = new HashMap<String, JSONObject>();
                    searchList.clear();
                    for (int i = 0; i < totalArray.length(); i++) {

                        JSONObject jsonObject = totalArray.getJSONObject(i);
                        index = jsonObject.optString("index");


                        JSONArray hitsArray = jsonObject.getJSONArray("hits");
                        Log.d("hitsArray===", "requestCompleted: " + hitsArray);
                        Log.d("index==>", "requestCompleted: " + index);


                        for (int j = 0; j < hitsArray.length(); j++) {

                            JSONObject mJsonObject = hitsArray.getJSONObject(j);

                            merchant_id = mJsonObject.optInt("merchant_id");
                            status = mJsonObject.optString("status");
                            restaurant_name = mJsonObject.optString("restaurant_name");
                            restaurant_name_ar = mJsonObject.optString("restaurant_name_ar");
                            item_id = mJsonObject.optString("item_id");
                            item_name = mJsonObject.optString("item_name");
                            item_name_ar = mJsonObject.optString("item_name_ar");
                            cuisine_id = mJsonObject.optString("cuisine_id");
                            cuisine_name = mJsonObject.optString("cuisine_name");
                            cuisine_name_ar = mJsonObject.optString("cuisine_name_ar");
                            photo_url = mJsonObject.optString("photo");
                            city_id = mJsonObject.optString("city_id");
                            region_id = mJsonObject.optString("region_ids");


                            search = new Search();
                            search.setIndex(index);
                            search.setMerchant_id(merchant_id);
                            search.setStatus(status);
                            search.setRestaurant_name(mDbHelper.getRestaurantNameById(String.valueOf(merchant_id)));
                            search.setRestaurant_name_ar(mDbHelper.getRestaurantNameArById(String.valueOf(merchant_id)));
                            search.setItem_id(item_id);
                            search.setItem_name(item_name);
                            search.setItem_name_ar(item_name_ar);
                            search.setCuisine_id(cuisine_id);
                            search.setCuisine_name(cuisine_name);
                            search.setCuisine_name_ar(cuisine_name_ar);
                            search.setPhoto(photo_url);
                            search.setCity_id(city_id);
                            search.setRegion_id(region_id);


                            if (search.getCity_id().equalsIgnoreCase(mCityId)) {
                                if (!mRegionId.isEmpty()) {
                                    if (search.getRegion_id().contains(mRegionId))
                                        searchList.add(search);
                                }
                                else
                                    searchList.add(search);
                            }



                            //hashMap.put(item_name,mJsonObject);
                            //hashMap.put(restaurant_name,mJsonObject);


                            Log.d("@@item_name====>", "requestCompleted: " + item_name);
                            System.out.println("restaurant_name====>" + "requestCompleted: " + restaurant_name);
                            Log.d("cuisine_name====>", "requestCompleted: " + cuisine_name);


                        }

                       /* Iterator it = hashMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();

                            System.out.println("@@"+pair.getKey() + " = " + pair.getValue());

                                JSONObject mJsonObject = (JSONObject)  pair.getValue();
                                merchant_id = mJsonObject.optInt("merchant_id");
                                status = mJsonObject.optString("status");
                                restaurant_name = mJsonObject.optString("restaurant_name");
                                restaurant_name_ar = mJsonObject.optString("restaurant_name_ar");
                                item_id = mJsonObject.optString("item_id");
                                item_name = mJsonObject.optString("item_name");
                                item_name_ar = mJsonObject.optString("item_name_ar");
                                cuisine_id = mJsonObject.optString("cuisine_id");
                                cuisine_name = mJsonObject.optString("cuisine_name");
                                cuisine_name_ar = mJsonObject.optString("cuisine_name_ar");
                                search = new Search();
                                search.setIndex(index);
                                search.setMerchant_id(merchant_id);
                                search.setStatus(status);
                                search.setRestaurant_name(restaurant_name);
                                search.setRestaurant_name_ar(restaurant_name_ar);
                                search.setItem_id(item_id);
                                search.setItem_name(item_name);
                                search.setItem_name_ar(item_name_ar);
                                search.setCuisine_id(cuisine_id);
                                search.setCuisine_name(cuisine_name);
                                search.setCuisine_name_ar(cuisine_name_ar);

                                searchList.add(search);
                                it.remove();
                             // avoids a ConcurrentModificationException
                        }*/


                    }


                     /*   HashSet<Search> hashSet = new HashSet<Search>(searchList);
                        for (Search mSearch:hashSet
                                ) {
                            Log.d(TAG, "@@HASSET==> "+mSearch.getItem_name());
                        }*/
                    Log.d("searchList======>", "requestCompleted: " + searchList.toString());

                    System.out.println("Array after removing duplicates");
                    rvSearchResults.setAdapter(newSeacrhAdapter);


                    scrollListener = new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
                        @Override
                        public void onLoadMore(int current_page, int totalItemCount) {


                            pageCount = ++pageCount;
                            loadMore(SearchInput, pageCount);


                        }
                    };
                    rvSearchResults.addOnScrollListener(scrollListener);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {

                }
            }
        });
    }


    private void loadMore(String searchInput, int NpageCount) {

        if (searchInput.length() != 0) {
            client = new Client("V3AJ8MVX1S", "731b737b3ed56ff9e06e4b66a2504f8a");
            List<IndexQuery> queries = new ArrayList<>();

            queries.add(new IndexQuery(AppUtils.RESTAURANT_INDEX, new Query(searchInput).setHitsPerPage(50).setPage(NpageCount)));
            queries.add(new IndexQuery(AppUtils.ITEMS_INDEX, new Query(searchInput).setHitsPerPage(50).setPage(NpageCount)));
            queries.add(new IndexQuery(AppUtils.CUISINE_INDEX, new Query(searchInput).setHitsPerPage(50).setPage(NpageCount)));


            final Client.MultipleQueriesStrategy strategy = Client.MultipleQueriesStrategy.NONE; // Execute the sequence of queries until the end.

            client.multipleQueriesAsync(queries, strategy, new CompletionHandler() {
                @Override
                public void requestCompleted(JSONObject content, AlgoliaException error) {
                    // [...]
                    Log.d("SEARCH==>", "requestCompleted: " + content.toString());

                    try {
                        JSONArray totalArray = content.getJSONArray("results");
                        //Log.d("SEARCH_ARRAY==>", "requestCompleted: "+totalArray);

                        for (int i = 0; i < totalArray.length(); i++) {

                            JSONObject jsonObject = totalArray.getJSONObject(i);

                            index = jsonObject.optString("index");
                            JSONArray hitsArray = jsonObject.getJSONArray("hits");
                            Log.d("hitsArray===", "requestCompleted: " + hitsArray);
                            Log.d("index==>", "requestCompleted: " + index);


                            for (int j = 0; j < hitsArray.length(); j++) {

                                JSONObject mJsonObject = hitsArray.getJSONObject(j);

                                merchant_id = mJsonObject.optInt("merchant_id");
                                status = mJsonObject.optString("status");
                                restaurant_name = mJsonObject.optString("restaurant_name");
                                restaurant_name_ar = mJsonObject.optString("restaurant_name_ar");
                                item_id = mJsonObject.optString("item_id");
                                item_name = mJsonObject.optString("item_name");
                                item_name_ar = mJsonObject.optString("item_name_ar");
                                cuisine_id = mJsonObject.optString("cuisine_id");
                                cuisine_name = mJsonObject.optString("cuisine_name");
                                cuisine_name_ar = mJsonObject.optString("cuisine_name_ar");
                                city_id = mJsonObject.optString("city_id");
                                region_id = mJsonObject.optString("region_ids");

                                //hashMap.put(item_name,mJsonObject);

                                Log.d("item_name====>", "requestCompleted: " + item_name);
                                Log.d("restaurant_name====>", "requestCompleted: " + restaurant_name);
                                Log.d("cuisine_name====>", "requestCompleted: " + cuisine_name);

                                search = new Search();
                                search.setIndex(index);
                                search.setMerchant_id(merchant_id);
                                search.setStatus(status);
                                search.setRestaurant_name(restaurant_name);
                                search.setRestaurant_name_ar(restaurant_name_ar);
                                search.setItem_id(item_id);
                                search.setItem_name(item_name);
                                search.setItem_name_ar(item_name_ar);
                                search.setCuisine_id(cuisine_id);
                                search.setCuisine_name(cuisine_name);
                                search.setCuisine_name_ar(cuisine_name_ar);
                                search.setCity_id(city_id);
                                search.setRegion_id(region_id);

                                if (search.getCity_id().equalsIgnoreCase(mCityId)) {
                                    if (!mRegionId.isEmpty()) {
                                        if (search.getRegion_id().contains(mRegionId))
                                            searchList.add(search);
                                    }
                                    else
                                        searchList.add(search);
                                }

                            }

                            /*Iterator it = hashMap.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry)it.next();
                                System.out.println("@@"+pair.getKey() + " = " + pair.getValue());
                                JSONObject mJsonObject = (JSONObject)  pair.getValue();
                                merchant_id = mJsonObject.optInt("merchant_id");
                                status = mJsonObject.optString("status");
                                restaurant_name = mJsonObject.optString("restaurant_name");
                                restaurant_name_ar = mJsonObject.optString("restaurant_name_ar");
                                item_id = mJsonObject.optString("item_id");
                                item_name = mJsonObject.optString("item_name");
                                item_name_ar = mJsonObject.optString("item_name_ar");
                                cuisine_id = mJsonObject.optString("cuisine_id");
                                cuisine_name = mJsonObject.optString("cuisine_name");
                                cuisine_name_ar = mJsonObject.optString("cuisine_name_ar");
                                search = new Search();
                                search.setIndex(index);
                                search.setMerchant_id(merchant_id);
                                search.setStatus(status);
                                search.setRestaurant_name(restaurant_name);
                                search.setRestaurant_name_ar(restaurant_name_ar);
                                search.setItem_id(item_id);
                                search.setItem_name(item_name);
                                search.setItem_name_ar(item_name_ar);
                                search.setCuisine_id(cuisine_id);
                                search.setCuisine_name(cuisine_name);
                                search.setCuisine_name_ar(cuisine_name_ar);

                                searchList.add(search);
                                it.remove(); // avoids a ConcurrentModificationException
                            }*/

                        }


                        Log.d("searchList======>", "requestCompleted: " + searchList.toString());
                        newSeacrhAdapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {

                    }
                }
            });
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
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
                break;
        }
    }
}