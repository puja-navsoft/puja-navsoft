package com.afieat.ini;

import android.os.Bundle;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.afieat.ini.adapters.FoodsAdapterCopy;
import com.afieat.ini.adapters.ViewOnlineSearchResultAdapter;
import com.afieat.ini.misc.SearchResultsJsonParser;
import com.afieat.ini.models.Search;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.EndlessRecyclerOnScrollListener;
import com.algolia.search.saas.Client;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewOnlineSearchActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private static final String TAG = "SearchActivity";
    private AppInstance appInstance;
    private String mUserId, mRegionId, mRegionName, mCityName, mCityId, GroupID;
    private final int REQUEST_LOCATION = 100;
    private EditText et_search;
    private String SearchInput = "";
    private Search search;
    ArrayList<Search> searchList = new ArrayList<Search>();
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
    private String cuisine_name_ar = "";
    private String index = "";
    private String price = "";
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
    private ListView rvSearchResults;
    private LinearLayoutManager mLinearLayoutManager;
    private ViewOnlineSearchResultAdapter newSeacrhAdapter;
    private EndlessRecyclerOnScrollListener scrollListener;
    private boolean clearSearch = false;
    private boolean closeClicked = false;
    private ImageView iv_search;
    private ImageView iv_close;
    private String lang = "";
    Map<String, JSONObject> hashMap = new HashMap<String, JSONObject>();

    FoodsAdapterCopy adapter;
    //ArrayList<ViewOnlineSearchModel> viewOnlineSearchModelsArrayList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_);
        appInstance = AppInstance.getInstance(getApplicationContext());


        lang = appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG);

        iv_search = (ImageView) findViewById(R.id.iv_search);
        et_search = (EditText) findViewById(R.id.et_search);
        iv_close = (ImageView) findViewById(R.id.iv_close);

        if ("ar".equals(lang)) {
            et_search.setHint("البحث عن مطعم أو مطبخ أو طبق ...");
        } else {
            et_search.setHint("Search for restaurant,cuisine or dish...");
        }


        rvSearchResults = findViewById(R.id.rvSearchResults);

        ViewCompat.setNestedScrollingEnabled(rvSearchResults, true);

        adapter = new FoodsAdapterCopy(AppUtils.DEMO_FOOD_COPY, getApplicationContext(),null);
        rvSearchResults.setAdapter(adapter);



        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
                //  clearSearch();
            }
        });


        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                SearchInput = editable.toString();
                pageCount = 0;
                searchList.clear();
                //clearSearch();

                if (SearchInput.length() != 0) {

                    if (SearchInput.length() >= 3) {
                        Log.d("SearchInput", "onTextChanged: " + SearchInput);
                        adapter.getFilter().filter(SearchInput);
                        // loadData(SearchInput, pageCount);
                    } else if (SearchInput.length() < 3) {
                        //   clearSearch();

                        Log.e(TAG, "onQueryTextChange: ");

                    }
                } else if (SearchInput.length() == 0) {
                    // clearSearch();
                    Log.e(TAG, "onQueryTextChange: ");

                }


            }
        });


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }


}