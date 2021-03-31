package com.afieat.ini.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.BasketActivity2;
import com.afieat.ini.FoodAddBasketActivity;
import com.afieat.ini.R;
import com.afieat.ini.RestaurantsDetailActivity;
import com.afieat.ini.adapters.FoodMenuPopupAdapter;
import com.afieat.ini.adapters.FoodsAdapter;
import com.afieat.ini.database.DBHelper;
import com.afieat.ini.interfaces.OnRestaurantPhotoClicked;
import com.afieat.ini.models.CategoryCountModel;
import com.afieat.ini.models.CategoryList;
import com.afieat.ini.models.DemoSearchModel;
import com.afieat.ini.models.Food;
import com.afieat.ini.models.FoodCopy;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantOrderItemsFragment extends Fragment {

    private ViewPager mViewPager;
    public PagerSlidingTabStrip tabStrip;
    // private ProgressBar progressBar;

    private Dialog afieatGifLoaderDialog;
    private TextView tvNoFood;
    public TextView txtFirstHeader;
    private ArrayList<String> hintText;
    private ArrayList<String> hintTextAr;

    public ArrayList<DemoSearchModel> mDemoSearchModelsMain = new ArrayList<>();
    public ArrayList<FoodCopy> mFoodCopiesArrayListMain = new ArrayList<>();
    public ArrayList<FoodCopy> mFoodCopiesArrayListMainForSearch = new ArrayList<>();

    private LinearLayout llPrices;
    private ListView listView;
    private List<FoodsAdapter> mAdaptersList;
    private FoodsAdapter adapter;
    private Context mContext;
    private String minPrice;
    private int sortType = 0;
    private String mRestaurantId;
    private List<Category> categories;
    private List<CategoryList> catList;
    private List<View> views;

    private int numFoodItems = 0;
    public View view;
    private ListView lvFoods;
    private ArrayList<Food> allFoodArray = new ArrayList<>();
    private ArrayList<Food> allCatList = new ArrayList<>();
    private LinearLayout layHeader;
    private ImageView txtMenu;
    private ImageView sort;
    private ArrayList<CategoryCountModel> arrCatCount = new ArrayList<>();
    FoodMenuPopupAdapter foodMenuPopupAdapter;
    Dialog dialog;

    public static final int TOP_LEFT = 1;
    public static final int TOP_RIGHT = 2;
    public static final int CENTER = 3;
    public static final int BOTTOM_LEFT = 4;
    public static final int BOTTOM_RIGHT = 5;

    //Pagination Variables
    private int currentPage = 1;
    private String Cat_id;
    private String maxRecords;
    int[] positionArray = new int[2];
    int currentType;
    int totalItemCount;
    int itemCount = 0;
    boolean isFirstTime = false;
    boolean isScrolling = false;
    int count = 0;
    private int previousTotal = 0;
    private int visibleThreshold = 1;
    int mStartIndex = 0;
    Boolean willLoadMore = false;
    private boolean isLoading = false;
    private final int QUERY_LIMIT = 25;
    int firstVisibleItem ;
    int oldCount ;
    View lvView ;
    int pos ;


    public RestaurantOrderItemsFragment() {
        mStartIndex = 0;
        willLoadMore = false;
        categories = new ArrayList<>();
        views = new ArrayList<>();
        mAdaptersList = new ArrayList<>();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public static RestaurantOrderItemsFragment newInstance(String restaurantId, String minPrice) {
        RestaurantOrderItemsFragment fragment = new RestaurantOrderItemsFragment();
        fragment.mRestaurantId = restaurantId;
        fragment.minPrice = minPrice;

        System.out.println("mdiwidhisjdfi : RestaurantOrderItemsFragment : " + fragment.minPrice);
        AppUtils.MINIMUM_PRICE = minPrice;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       /* ((RestaurantsDetailActivity)getActivity()).viewOnlineOrderMenu.setVisibility(View.GONE);
        ((RestaurantsDetailActivity)getActivity()).appBarLayout.setExpanded(false,true);*/
        view = inflater.inflate(R.layout.fragment_restaurant_order_items, container, false);
        System.out.println("mdiwidhisjdfi : RestaurantOrderItemsFragment : called ");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*mViewPager = (ViewPager) view.findViewById(R.id.pager);
        tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabLayout);*/
        tvNoFood = view.findViewById(R.id.tvNoFood);
        lvFoods = view.findViewById(R.id.lvFoods);
        txtFirstHeader = view.findViewById(R.id.txtFirstHeader);
        layHeader = view.findViewById(R.id.layHeader);
        txtMenu = view.findViewById(R.id.txtMenu);
        sort = view.findViewById(R.id.sortBtn);
        //progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        getFoodItemsFromNW("");


        txtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    openFoodMenu(txtMenu);
                }
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                sortMenu();
            }
        });
    }

    public void getFoodItemsFromNW(String keyword) {

        try {

            //  AppUtils.showViews(progressBar);
            afieatGifLoaderDialog();
            AppUtils.hideViews(tvNoFood);

            categories.clear();
            views.clear();
            allCatList.clear();
            Map<String, String> params = new HashMap<>();
            params.put("resid", mRestaurantId);
            params.put("group_id", RestaurantsDetailActivity.GroupID);
            params.put("has_photo", String.valueOf(sortType));

            System.out.println("RestaurantOrderItemsFragment : getFoodItemsFromNW : mRestaurantId : " + mRestaurantId);
            System.out.println("RestaurantOrderItemsFragment : getFoodItemsFromNW : RestaurantsDetailActivity.GroupID : " + RestaurantsDetailActivity.GroupID);
          /*  params.put("appToken", AppUtils.APPTOKEN);
            params.put("Authorization", AppUtils.AUTHRIZATIONKEY);*/

            if ("en".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                params.put("lang", "en");
            } else {
                params.put("lang", "ar");
            }

            Log.e("FoodItemsFromNW :", "" + Apis.GET_FOOD_ITEMS + "?resid=" + mRestaurantId + "&group_id=" + RestaurantsDetailActivity.GroupID);
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_FOOD_ITEMS, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            System.out.println("getFoodItemsFromNW : response : " + response);
                            AppUtils.SEARCH_JSON_ = response;
                            AppUtils.log("Food Items-" + response);
                            //  AppUtils.hideViews(progressBar);

                            JSONObject foodItemsObject = response.optJSONObject("fooditems");
                            hintText = new ArrayList<>();
                            hintTextAr = new ArrayList<>();

                            JSONObject sizeObject = response.optJSONObject("size");
                            sizeObject.keys();

                            JSONObject catObject = response.optJSONObject("cat_list");
                            Iterator<String> catKeys = catObject.keys();
                            catList = new ArrayList<CategoryList>();
                            while (catKeys.hasNext()) {
                                CategoryList categoryList = new CategoryList();
                                String cat_id = catKeys.next();
                                categoryList.setCat_type(catObject.optString(cat_id));
                                categoryList.setId(cat_id);
                                catList.add(categoryList);
                            }
                            Log.e("Category+LIST==>", "" + catList.toString());
                            AppUtils.log("CatSize-> " + sizeObject.toString());

                            if (foodItemsObject != null) {
                                Iterator<String> keys = foodItemsObject.keys();
                                Category category = null;
                                while (keys.hasNext()) {
                                    String catName = keys.next();
                                    category = new Category();
                                    category.setName(catName);
                                    category.foods = new ArrayList<>();


                                    JSONArray foodsArray = foodItemsObject.optJSONArray(catName);


                                    int i = 0;
                                    while (i < foodsArray.length()) {
                                        JSONObject foodObject = foodsArray.optJSONObject(i);

                                        AppUtils.log("!@@-> " + foodObject.toString());
                                        Food food = new Food();
                                        food.prices = new ArrayList<>();
                                        food.actualPrice = new ArrayList<>();

                                        DemoSearchModel mDemoSearchModelsMainNew = new DemoSearchModel();
                                        mDemoSearchModelsMainNew.mPriceList = new ArrayList<>();
                                        mDemoSearchModelsMainNew.mActualPriceList = new ArrayList<>();


                                        FoodCopy mFoodCopyNew = new FoodCopy();
                                        mFoodCopyNew.prices = new ArrayList<>();
                                        mFoodCopyNew.actualPrice = new ArrayList<>();

                                        try {
                                            JSONObject priceObject = new JSONObject(foodObject.optString("price"));
                                            Iterator<String> priceKeys = priceObject.keys();

                                            JSONObject actualPriceObject = new JSONObject(foodObject.optString("actual_price"));
                                            Iterator<String> actualPriceObjectKeys = actualPriceObject.keys();


                                            while (priceKeys.hasNext()) {

                                                Food.Price price = food.new Price();
                                                String priceId = priceKeys.next();
                                                price.setType(sizeObject.optString(priceId));
                                                price.setPrice(priceObject.optString(priceId));
                                                price.setId(priceId);
                                                food.prices.add(price);


                                                DemoSearchModel.Price mppp = mDemoSearchModelsMainNew.new Price();
                                                mppp.setType(sizeObject.optString(priceId));
                                                mppp.setPrice(priceObject.optString(priceId));
                                                mppp.setId(priceId);

                                                mDemoSearchModelsMainNew.mPriceList.add(mppp);

                                                //-------------------TEMP
                                                FoodCopy.Price fpp = mFoodCopyNew.new Price();
                                                fpp.setType(sizeObject.optString(priceId));
                                                fpp.setPrice(priceObject.optString(priceId));
                                                fpp.setId(priceId);

                                                mFoodCopyNew.prices.add(fpp);
                                                //------------------------------------
                                            }

                                            while (actualPriceObjectKeys.hasNext()) {
                                                Food.ActualPrice mActualPrice = food.new ActualPrice();
                                                String priceId = actualPriceObjectKeys.next();
                                                mActualPrice.setType(sizeObject.optString(priceId));
                                                mActualPrice.setPrice(actualPriceObject.optString(priceId));
                                                mActualPrice.setId(priceId);

                                                System.out.println("Key : " + priceId);

                                                food.actualPrice.add(mActualPrice);

                                                DemoSearchModel.ActualPrice mppp = mDemoSearchModelsMainNew.new ActualPrice();
                                                mppp.setType(sizeObject.optString(priceId));
                                                mppp.setPrice(actualPriceObject.optString(priceId));
                                                mppp.setId(priceId);

                                                mDemoSearchModelsMainNew.mActualPriceList.add(mppp);

                                                //-------------------- TEMP
                                                FoodCopy.ActualPrice fpp = mFoodCopyNew.new ActualPrice();
                                                fpp.setType(sizeObject.optString(priceId));
                                                fpp.setPrice(actualPriceObject.optString(priceId));
                                                fpp.setId(priceId);

                                                mFoodCopyNew.actualPrice.add(fpp);
                                                //----------------------

                                            }
                                            hintText.add(foodObject.optString("item_name"));
                                            hintTextAr.add(foodObject.optString("item_name_ar"));

                                            DemoSearchModel mDemoSearchModel = new DemoSearchModel(foodObject.optString("item_id"),
                                                    foodObject.optString("merchant_id"),
                                                    foodObject.optString("item_name"),
                                                    foodObject.optString("item_name_ar"), mDemoSearchModelsMainNew.mPriceList, mDemoSearchModelsMainNew.mActualPriceList
                                                    , foodObject.optString("photo"), mDemoSearchModelsMainNew.mPriceList.size());


                                            FoodCopy mFoodsAdapterCopy = new FoodCopy(foodObject.optString("item_id"),
                                                    foodObject.optString("merchant_id"),
                                                    foodObject.optString("item_name")
                                                    , foodObject.optString("item_name_ar")
                                                    , "", "", "", "", mFoodCopyNew.prices, mFoodCopyNew.actualPrice, foodObject.optString("photo"));


                                            mDemoSearchModelsMain.add(mDemoSearchModel);

                                            mFoodCopiesArrayListMain.add(mFoodsAdapterCopy);

                                            //Gson hh=new Gson();
                                            //System.out.println("klnflnef : json : " + hh.toJson(mDemoSearchModelsMain));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        food.setId(foodObject.optString("item_id"));
                                        food.setName(foodObject.optString("item_name"));
                                        food.setMerchantId(foodObject.optString("merchant_id"));

                                        if ("ar".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                                            food.setName(foodObject.optString("item_name_ar"));
                                        }
                                        food.setCategory(catName);
                                        food.setDiscount(foodObject.optString("discount"));
                                        food.setDescription(foodObject.optString("item_description"));
                                        food.setUriPic(foodObject.optString("photo"));
                                        food.setRestaurant_url(foodObject.optString("restaurant_url"));

                                        if (i == 0) {
                                            food.setHeaderTxt(category.name);
                                            food.setFirstPos(true);
                                        } else {
                                            food.setHeaderTxt(category.name);
                                            food.setFirstPos(false);
                                            // food.setHeaderTxt("");
                                        }
                                        category.foods.add(food);
                                        i++;
                                    }
                                    ArrayList<Food> arr = new ArrayList<>();
                                    arr.addAll(category.foods);
                                    CategoryCountModel cmodel = new CategoryCountModel();
                                    cmodel.setCategoryName(category.name);
                                    cmodel.setCategoryCount(arr.size());
                                    arrCatCount.add(cmodel);
                                    categories.add(category);
                                    allFoodArray.addAll(category.foods);
                                    numFoodItems++;
                                    arr.clear();
                                    arr = null;


                                   /* listView = (ListView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_listview_fooditems, null);
                                    ViewCompat.setNestedScrollingEnabled(listView, true);
                                    adapter = new FoodsAdapter(category.foods, getContext(), RestaurantOrderItemsFragment.this, null);

                                    //----------------------

                                    adapter.setOnRestaurantPhotoClicked(new OnRestaurantPhotoClicked() {
                                        @Override
                                        public void onPhotoClicked(Uri path) {
                                            Fragment fragment = SimplePhotoFragment.newInstance(path);
                                            getFragmentManager()
                                                    .beginTransaction()
                                                    .add(R.id.frameLayout, fragment)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    });
                                    listView.setAdapter(adapter);
                                    mAdaptersList.add(adapter);


                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            if (!AppUtils.IS_SUPER_MARKET) {

                                            System.out.println("Rahul : RestaurantOrderItemsFragment : onItemClick : String.valueOf(id) " + String.valueOf(id));
                                            System.out.println("Rahul : RestaurantOrderItemsFragment : onItemClick : ((Food) parent.getAdapter().getItem(position)).getName() " + ((Food) parent.getAdapter().getItem(position)).getName());
                                            Intent intent = new Intent(getActivity(), FoodAddBasketActivity.class);
                                            intent.putExtra(AppUtils.EXTRA_FOOD_ID, String.valueOf(id));
                                            intent.putExtra(AppUtils.EXTRA_FOOD_NAME, ((Food) parent.getAdapter().getItem(position)).getName());
                                            startActivity(intent);
                                            }else
                                            {

                                            }
                                        }
                                    });
                                    views.add(listView);
                                    // listView.setSelection(6);
                                    categories.add(category);
                                    numFoodItems++;*/
                                }


                                adapter = new FoodsAdapter(allFoodArray, getContext(), RestaurantOrderItemsFragment.this, null);

                                ViewCompat.setNestedScrollingEnabled(lvFoods, true);
                                lvFoods.setAdapter(adapter);
                                layHeader.setVisibility(View.VISIBLE);
                                txtFirstHeader.setText(allFoodArray.get(0).getHeaderTxt());
                                txtMenu.setVisibility(View.VISIBLE);

                                adapter.setOnRestaurantPhotoClicked(new OnRestaurantPhotoClicked() {
                                    @Override
                                    public void onPhotoClicked(Uri path) {
                                        Fragment fragment = SimplePhotoFragment.newInstance(path);
                                        getFragmentManager()
                                                .beginTransaction()
                                                .add(R.id.frameLayout, fragment)
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                });


                                lvFoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        if (!AppUtils.IS_SUPER_MARKET) {

                                            System.out.println("Rahul : RestaurantOrderItemsFragment : onItemClick : String.valueOf(id) " + id);
                                            System.out.println("Rahul : RestaurantOrderItemsFragment : onItemClick : ((Food) parent.getAdapter().getItem(position)).getName() " + ((Food) parent.getAdapter().getItem(position)).getName());
                                            Intent intent = new Intent(getActivity(), FoodAddBasketActivity.class);
                                            intent.putExtra(AppUtils.EXTRA_FOOD_ID, String.valueOf(id));
                                            intent.putExtra(AppUtils.EXTRA_FOOD_NAME, ((Food) parent.getAdapter().getItem(position)).getName());
                                            startActivity(intent);
                                        }
                                    }
                                });

                                lvFoods.setOnScrollListener(new AbsListView.OnScrollListener() {

                                    @Override
                                    public void onScrollStateChanged(AbsListView view,
                                                                     int scrollState) { // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onScroll(AbsListView view, int firstVisibleItem,
                                                         int visibleItemCount, int totalItemCount) {


                                        if (!allFoodArray.get(firstVisibleItem).getHeaderTxt().equalsIgnoreCase("")) {
                                            txtFirstHeader.setText(allFoodArray.get(firstVisibleItem).getHeaderTxt());
                                        }

                                    }
                                });

                                Gson gson = new Gson();
                                String json = gson.toJson(mDemoSearchModelsMain);

                                System.out.println("klnflnef : json : " + json);


                                String jso1n = gson.toJson(mFoodCopiesArrayListMain);
                                System.out.println("Checkoutthis : json : " + jso1n);

                                AppUtils.HINT_TEXT = hintText;
                                AppUtils.HINT_TEXT_AR = hintTextAr;
                                AppUtils.DEMO_SEARCH_MODEL_LIST = mDemoSearchModelsMain;
                                AppUtils.DEMO_FOOD_COPY = mFoodCopiesArrayListMain;

                                System.out.println("Checkoutthis : AppUtils.HINT_TEXT : " + AppUtils.HINT_TEXT);
                                System.out.println("Checkoutthis : AppUtils.HINT_TEXT_AR : " + AppUtils.HINT_TEXT_AR);

                                if (afieatGifLoaderDialog != null) {
                                    try {
                                        afieatGifLoaderDialog.dismiss();
                                    } catch (IllegalArgumentException e) {

                                    }
                                }
                            }
                           /* if (numFoodItems > 0) {
                                mViewPager.setAdapter(new MyPagerAdapter());
                                tabStrip.setViewPager(mViewPager);
                                //  mViewPager.setCurrentItem(3);

                                adapter.notifyDataSetChanged();


                            }
                            else {
                                AppUtils.showViews(tvNoFood);
                            }*/
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //AppUtils.hideViews(progressBar);
                            if (afieatGifLoaderDialog != null) {
                                afieatGifLoaderDialog.dismiss();
                            }
                            error.printStackTrace();
                        }
                    }
            );
            AppInstance.getInstance(mContext.getApplicationContext()).addToRequestQueue(request);
        } catch (Exception e) {
            e.printStackTrace();
            if (afieatGifLoaderDialog != null) {
                afieatGifLoaderDialog.dismiss();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            //   AppUtils.hideViews(progressBar);
        }


    }

    public void getFoodItemsForSearchFromNW(String keyword) {

        try {

            //  AppUtils.showViews(progressBar);
//            afieatGifLoaderDialog();
            AppUtils.hideViews(tvNoFood);
            mFoodCopiesArrayListMainForSearch.clear();

//            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(),0);

            categories.clear();
            views.clear();
            allCatList.clear();
            Map<String, String> params = new HashMap<>();
            params.put("resid", mRestaurantId);
            params.put("group_id", RestaurantsDetailActivity.GroupID);
//            params.put("has_photo", String.valueOf(sortType));
            params.put("page","1");
            params.put("search_text",keyword);


            System.out.println("RestaurantOrderItemsFragment : getFoodItemsFromNW : mRestaurantId : " + mRestaurantId);
            System.out.println("RestaurantOrderItemsFragment : getFoodItemsFromNW : RestaurantsDetailActivity.GroupID : " + RestaurantsDetailActivity.GroupID);
          /*  params.put("appToken", AppUtils.APPTOKEN);
            params.put("Authorization", AppUtils.AUTHRIZATIONKEY);*/

            if ("en".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                params.put("lang", "en");
            } else {
                params.put("lang", "ar");
            }

            Log.e("FoodItemsFromNW :", "" + Apis.GET_FOOD_ITEMS + "?resid=" + mRestaurantId + "&group_id=" + RestaurantsDetailActivity.GroupID);
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_FOOD_ITEMS, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            System.out.println("getFoodItemsFromNW : response : " + response);
                            AppUtils.SEARCH_JSON_ = response;
                            AppUtils.log("Food Items-" + response);
                            //  AppUtils.hideViews(progressBar);

                            JSONObject foodItemsObject = response.optJSONObject("fooditems");
                            hintText = new ArrayList<>();
                            hintTextAr = new ArrayList<>();

                            JSONObject sizeObject = response.optJSONObject("size");
                            sizeObject.keys();

                            JSONObject catObject = response.optJSONObject("cat_list");
                            Iterator<String> catKeys = catObject.keys();
                            catList = new ArrayList<CategoryList>();
                            while (catKeys.hasNext()) {
                                CategoryList categoryList = new CategoryList();
                                String cat_id = catKeys.next();
                                categoryList.setCat_type(catObject.optString(cat_id));
                                categoryList.setId(cat_id);
                                catList.add(categoryList);
                            }
                            Log.e("Category+LIST==>", "" + catList.toString());
                            AppUtils.log("CatSize-> " + sizeObject.toString());

                            if (foodItemsObject != null) {
                                Iterator<String> keys = foodItemsObject.keys();
                                Category category = null;
                                while (keys.hasNext()) {
                                    String catName = keys.next();
                                    category = new Category();
                                    category.setName(catName);
                                    category.foods = new ArrayList<>();


                                    JSONArray foodsArray = foodItemsObject.optJSONArray(catName);


                                    int i = 0;
                                    while (i < foodsArray.length()) {
                                        JSONObject foodObject = foodsArray.optJSONObject(i);

                                        AppUtils.log("!@@-> " + foodObject.toString());
                                        Food food = new Food();
                                        food.prices = new ArrayList<>();
                                        food.actualPrice = new ArrayList<>();

                                        DemoSearchModel mDemoSearchModelsMainNew = new DemoSearchModel();
                                        mDemoSearchModelsMainNew.mPriceList = new ArrayList<>();
                                        mDemoSearchModelsMainNew.mActualPriceList = new ArrayList<>();


                                        FoodCopy mFoodCopyNew = new FoodCopy();
                                        mFoodCopyNew.prices = new ArrayList<>();
                                        mFoodCopyNew.actualPrice = new ArrayList<>();

                                        try {
                                            JSONObject priceObject = new JSONObject(foodObject.optString("price"));
                                            Iterator<String> priceKeys = priceObject.keys();

                                            JSONObject actualPriceObject = new JSONObject(foodObject.optString("actual_price"));
                                            Iterator<String> actualPriceObjectKeys = actualPriceObject.keys();


                                            while (priceKeys.hasNext()) {

                                                Food.Price price = food.new Price();
                                                String priceId = priceKeys.next();
                                                price.setType(sizeObject.optString(priceId));
                                                price.setPrice(priceObject.optString(priceId));
                                                price.setId(priceId);
                                                food.prices.add(price);


                                                DemoSearchModel.Price mppp = mDemoSearchModelsMainNew.new Price();
                                                mppp.setType(sizeObject.optString(priceId));
                                                mppp.setPrice(priceObject.optString(priceId));
                                                mppp.setId(priceId);

                                                mDemoSearchModelsMainNew.mPriceList.add(mppp);

                                                //-------------------TEMP
                                                FoodCopy.Price fpp = mFoodCopyNew.new Price();
                                                fpp.setType(sizeObject.optString(priceId));
                                                fpp.setPrice(priceObject.optString(priceId));
                                                fpp.setId(priceId);

                                                mFoodCopyNew.prices.add(fpp);
                                                //------------------------------------
                                            }

                                            while (actualPriceObjectKeys.hasNext()) {
                                                Food.ActualPrice mActualPrice = food.new ActualPrice();
                                                String priceId = actualPriceObjectKeys.next();
                                                mActualPrice.setType(sizeObject.optString(priceId));
                                                mActualPrice.setPrice(actualPriceObject.optString(priceId));
                                                mActualPrice.setId(priceId);

                                                System.out.println("Key : " + priceId);

                                                food.actualPrice.add(mActualPrice);

                                                DemoSearchModel.ActualPrice mppp = mDemoSearchModelsMainNew.new ActualPrice();
                                                mppp.setType(sizeObject.optString(priceId));
                                                mppp.setPrice(actualPriceObject.optString(priceId));
                                                mppp.setId(priceId);

                                                mDemoSearchModelsMainNew.mActualPriceList.add(mppp);

                                                //-------------------- TEMP
                                                FoodCopy.ActualPrice fpp = mFoodCopyNew.new ActualPrice();
                                                fpp.setType(sizeObject.optString(priceId));
                                                fpp.setPrice(actualPriceObject.optString(priceId));
                                                fpp.setId(priceId);

                                                mFoodCopyNew.actualPrice.add(fpp);
                                                //----------------------

                                            }
                                            hintText.add(foodObject.optString("item_name"));
                                            hintTextAr.add(foodObject.optString("item_name_ar"));

                                            DemoSearchModel mDemoSearchModel = new DemoSearchModel(foodObject.optString("item_id"),
                                                    foodObject.optString("merchant_id"),
                                                    foodObject.optString("item_name"),
                                                    foodObject.optString("item_name_ar"), mDemoSearchModelsMainNew.mPriceList, mDemoSearchModelsMainNew.mActualPriceList
                                                    , foodObject.optString("photo"), mDemoSearchModelsMainNew.mPriceList.size());


                                            FoodCopy mFoodsAdapterCopy = new FoodCopy(foodObject.optString("item_id"),
                                                    foodObject.optString("merchant_id"),
                                                    foodObject.optString("item_name")
                                                    , foodObject.optString("item_name_ar")
                                                    , "", "", "", "", mFoodCopyNew.prices, mFoodCopyNew.actualPrice, foodObject.optString("photo"));


                                            mDemoSearchModelsMain.add(mDemoSearchModel);

                                            mFoodCopiesArrayListMainForSearch.add(mFoodsAdapterCopy);

                                            //Gson hh=new Gson();
                                            //System.out.println("klnflnef : json : " + hh.toJson(mDemoSearchModelsMain));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        food.setId(foodObject.optString("item_id"));
                                        food.setName(foodObject.optString("item_name"));
                                        food.setMerchantId(foodObject.optString("merchant_id"));

                                        if ("ar".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                                            food.setName(foodObject.optString("item_name_ar"));
                                        }
                                        food.setCategory(catName);
                                        food.setDiscount(foodObject.optString("discount"));
                                        food.setDescription(foodObject.optString("item_description"));
                                        food.setUriPic(foodObject.optString("photo"));
                                        food.setRestaurant_url(foodObject.optString("restaurant_url"));

                                        if (i == 0) {
                                            food.setHeaderTxt(category.name);
                                            food.setFirstPos(true);
                                        } else {
                                            food.setHeaderTxt(category.name);
                                            food.setFirstPos(false);
                                            // food.setHeaderTxt("");
                                        }
                                        category.foods.add(food);
                                        i++;
                                    }
                                    ArrayList<Food> arr = new ArrayList<>();
                                    arr.addAll(category.foods);
                                    CategoryCountModel cmodel = new CategoryCountModel();
                                    cmodel.setCategoryName(category.name);
                                    cmodel.setCategoryCount(arr.size());
                                    arrCatCount.add(cmodel);
                                    categories.add(category);
                                    allFoodArray.addAll(category.foods);
                                    numFoodItems++;
                                    arr.clear();
                                    arr = null;


                                   /* listView = (ListView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_listview_fooditems, null);
                                    ViewCompat.setNestedScrollingEnabled(listView, true);
                                    adapter = new FoodsAdapter(category.foods, getContext(), RestaurantOrderItemsFragment.this, null);

                                    //----------------------

                                    adapter.setOnRestaurantPhotoClicked(new OnRestaurantPhotoClicked() {
                                        @Override
                                        public void onPhotoClicked(Uri path) {
                                            Fragment fragment = SimplePhotoFragment.newInstance(path);
                                            getFragmentManager()
                                                    .beginTransaction()
                                                    .add(R.id.frameLayout, fragment)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    });
                                    listView.setAdapter(adapter);
                                    mAdaptersList.add(adapter);


                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            if (!AppUtils.IS_SUPER_MARKET) {

                                            System.out.println("Rahul : RestaurantOrderItemsFragment : onItemClick : String.valueOf(id) " + String.valueOf(id));
                                            System.out.println("Rahul : RestaurantOrderItemsFragment : onItemClick : ((Food) parent.getAdapter().getItem(position)).getName() " + ((Food) parent.getAdapter().getItem(position)).getName());
                                            Intent intent = new Intent(getActivity(), FoodAddBasketActivity.class);
                                            intent.putExtra(AppUtils.EXTRA_FOOD_ID, String.valueOf(id));
                                            intent.putExtra(AppUtils.EXTRA_FOOD_NAME, ((Food) parent.getAdapter().getItem(position)).getName());
                                            startActivity(intent);
                                            }else
                                            {

                                            }
                                        }
                                    });
                                    views.add(listView);
                                    // listView.setSelection(6);
                                    categories.add(category);
                                    numFoodItems++;*/
                                }


                                adapter = new FoodsAdapter(allFoodArray, getContext(), RestaurantOrderItemsFragment.this, null);

                                ViewCompat.setNestedScrollingEnabled(lvFoods, true);
                                lvFoods.setAdapter(adapter);
                                layHeader.setVisibility(View.VISIBLE);
                                txtFirstHeader.setText(allFoodArray.get(0).getHeaderTxt());
                                txtMenu.setVisibility(View.VISIBLE);

                                adapter.setOnRestaurantPhotoClicked(new OnRestaurantPhotoClicked() {
                                    @Override
                                    public void onPhotoClicked(Uri path) {
                                        Fragment fragment = SimplePhotoFragment.newInstance(path);
                                        getFragmentManager()
                                                .beginTransaction()
                                                .add(R.id.frameLayout, fragment)
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                });


                                lvFoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        if (!AppUtils.IS_SUPER_MARKET) {

                                            System.out.println("Rahul : RestaurantOrderItemsFragment : onItemClick : String.valueOf(id) " + id);
                                            System.out.println("Rahul : RestaurantOrderItemsFragment : onItemClick : ((Food) parent.getAdapter().getItem(position)).getName() " + ((Food) parent.getAdapter().getItem(position)).getName());
                                            Intent intent = new Intent(getActivity(), FoodAddBasketActivity.class);
                                            intent.putExtra(AppUtils.EXTRA_FOOD_ID, String.valueOf(id));
                                            intent.putExtra(AppUtils.EXTRA_FOOD_NAME, ((Food) parent.getAdapter().getItem(position)).getName());
                                            startActivity(intent);
                                        }
                                    }
                                });

                                lvFoods.setOnScrollListener(new AbsListView.OnScrollListener() {

                                    @Override
                                    public void onScrollStateChanged(AbsListView view,
                                                                     int scrollState) { // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onScroll(AbsListView view, int firstVisibleItem,
                                                         int visibleItemCount, int totalItemCount) {


                                        if (!allFoodArray.get(firstVisibleItem).getHeaderTxt().equalsIgnoreCase("")) {
                                            txtFirstHeader.setText(allFoodArray.get(firstVisibleItem).getHeaderTxt());
                                        }

                                    }
                                });

                                Gson gson = new Gson();
                                String json = gson.toJson(mDemoSearchModelsMain);

                                System.out.println("klnflnef : json : " + json);


                                String jso1n = gson.toJson(mFoodCopiesArrayListMainForSearch);
                                System.out.println("Checkoutthis : json : " + jso1n);

                                AppUtils.HINT_TEXT = hintText;
                                AppUtils.HINT_TEXT_AR = hintTextAr;
                                AppUtils.DEMO_SEARCH_MODEL_LIST = mDemoSearchModelsMain;
                                AppUtils.DEMO_FOOD_COPY = mFoodCopiesArrayListMainForSearch;

                                System.out.println("Checkoutthis : AppUtils.HINT_TEXT : " + AppUtils.HINT_TEXT);
                                System.out.println("Checkoutthis : AppUtils.HINT_TEXT_AR : " + AppUtils.HINT_TEXT_AR);

                                if (afieatGifLoaderDialog != null) {
                                    try {
                                        afieatGifLoaderDialog.dismiss();
//                                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                                    } catch (IllegalArgumentException e) {

                                    }
                                }
                            }
                           /* if (numFoodItems > 0) {
                                mViewPager.setAdapter(new MyPagerAdapter());
                                tabStrip.setViewPager(mViewPager);
                                //  mViewPager.setCurrentItem(3);

                                adapter.notifyDataSetChanged();


                            }
                            else {
                                AppUtils.showViews(tvNoFood);
                            }*/
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //AppUtils.hideViews(progressBar);
                            if (afieatGifLoaderDialog != null) {
                                afieatGifLoaderDialog.dismiss();
                            }
                            error.printStackTrace();
                        }
                    }
            );
            AppInstance.getInstance(mContext.getApplicationContext()).addToRequestQueue(request);
        } catch (Exception e) {
            e.printStackTrace();
            if (afieatGifLoaderDialog != null) {
                afieatGifLoaderDialog.dismiss();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            //   AppUtils.hideViews(progressBar);
        }


    }


    public void onBottomReachedR(int position) {

        System.out.println("Rahul : onBottomReached : position :" + position);

        if (afieatGifLoaderDialog != null) {
            afieatGifLoaderDialog.dismiss();
        }

        mStartIndex = mStartIndex + (position);
        oldCount=position+1;
        Log.e("START_INDEX==>", "" + mStartIndex);
        Log.e("positonSTART==>", "" + position+""+ oldCount);



        currentPage++;
        getCategoryItemsFromNW(Cat_id, currentPage);


    }


   /* public void searchListView()
    {
        listView = (ListView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_listview_fooditems, null);
        ViewCompat.setNestedScrollingEnabled(listView, true);
        adapter = new FoodsAdapter(category.foods, getContext(), RestaurantOrderItemsFragment.this, null);

        //----------------------

        adapter.setOnRestaurantPhotoClicked(new OnRestaurantPhotoClicked() {
            @Override
            public void onPhotoClicked(Uri path) {
                Fragment fragment = SimplePhotoFragment.newInstance(path);
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.frameLayout, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        listView.setAdapter(adapter);
        mAdaptersList.add(adapter);
    }*/

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View layout = views.get(position);
            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return categories.get(position).getName();
        }
    }

    class Category {
        private String name;
        private List<Food> foods;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("Rahul : RestaurantOrderItemsFragment : onDestroyView :");
    }


    @Override
    public void onResume() {
        super.onResume();

        System.out.println("RestaurantOrderItemsFragment : onResume : ");
        DBHelper db = new DBHelper(getActivity());
        int itemCount = db.getFoodsBasket(AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_ID)).size();
        System.out.println("RestaurantOrderItemsFragment : itemCount : " + itemCount);
        if (itemCount == 0) {
            view.findViewById(R.id.bottomCartView).setVisibility(View.GONE);


        } else {
            view.findViewById(R.id.bottomCartView).setVisibility(View.VISIBLE);

            TextView cartCount = view.findViewById(R.id.cartCount);
            TextView totalPrice = view.findViewById(R.id.totalPrice);

            if ("en".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                cartCount.setText(itemCount + " Items In Cart");
            } else {
                cartCount.setText(itemCount + "العناصر في السلة ");
            }


            final List<Food> foods = db.getFoodsBasket(AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
            double subTotal = 0;
            for (Food food : foods) {
                subTotal += Double.parseDouble(food.getPriceBasket());
            }
            totalPrice.setText(getString(R.string.currency) + subTotal);

            TextView viewCart = view.findViewById(R.id.viewCart);
            viewCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), BasketActivity2.class);
                    //  AppUtils.MINIMUM_PRICE = minPrice;
                    // AppInstance.getInstance(getContext()).addToSharedPref("min_price",minPrice);
                    // System.out.println("mdiwidhisjdfi : RestaurantOrderItemsFragment : "+fragment.minPrice);

                    intent.putExtra("MinPrice", AppInstance.getInstance(getContext()).getFromSharedPref("min_price"));
                    startActivity(intent);
                }
            });
        }
/*
if(adapter!=null) {
    if (listView.getAdapter().getCount() > 0) {
        listView.setSelection(0);
        listView.setSelection(6);
        adapter.notifyDataSetChanged();
    }
}*/

        updateAdapter();


        System.out.println("RestaurantOrderItemsFragment : onResume  : Check");

    }

    public void updateAdapter() {
        if (adapter != null) {
// Getting Iterator
            Iterator<FoodsAdapter> mFoodsAdapterIterator = mAdaptersList.iterator();

            // Traversing elements
            while (mFoodsAdapterIterator.hasNext()) {

                adapter = mFoodsAdapterIterator.next();
                adapter.notifyDataSetChanged();

            }
            // adapter.updateBottomView();

        }
    }

    public void showSearchDialog() {

        System.out.println("igfhdifj : " + mFoodCopiesArrayListMainForSearch.size());

        adapter.searchDialog(mFoodCopiesArrayListMainForSearch);
//        if (mFoodCopiesArrayListMainForSearch != null) {
//            if (mFoodCopiesArrayListMainForSearch.size() != 0) {
//
//            } else {
//                Toast.makeText(getContext(), getString(R.string.msg_no_food), Toast.LENGTH_SHORT).show();
//            }
//
//        } else {
//            Toast.makeText(getContext(), getString(R.string.msg_no_food), Toast.LENGTH_SHORT).show();
//
//        }

    }

  /*  public void fireSearchQuery(CharSequence searchQuery)

    {
        adapter.fireSearchQuery(searchQuery);
    }*/

    private void afieatGifLoaderDialog() {
        afieatGifLoaderDialog = new Dialog(getActivity());
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }

    public void sortMenu (){
        final int MAX_HEIGHT = 800;

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_sort);
        dialog.setTitle("");

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams param = window.getAttributes();
        if ("en".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            param.gravity = Gravity.CENTER;
        } else {
            param.gravity = Gravity.CENTER;
        }


        param.width = 700;
        // param.height =900;
        param.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(param);


        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        int dialogWidth = lp.width;
        int dialogHeight = lp.height;

        if(dialogHeight > MAX_HEIGHT) {
            dialog.getWindow().setLayout(dialogWidth,MAX_HEIGHT);
        }*/

        /*ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = 300;
        ((WindowManager.LayoutParams) params).gravity=Gravity.BOTTOM|Gravity.END;
        dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);*/
        final Button apply = dialog.findViewById(R.id.applyBtn);
        final LinearLayout layout = dialog.findViewById(R.id.sortLayout);
        final CheckBox image = dialog.findViewById(R.id.imageRb);


        if (sortType == 1){
            image.setChecked(true);
        }
        else
            image.setChecked(false);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image.isChecked()) {
                    if (Cat_id==null || Cat_id.isEmpty()) {
                        sortType = 1;

                        allCatList.clear();
                        allFoodArray.clear();
                        getFoodItemsFromNW("");
                        dialog.dismiss();
                    } else {
                        sortType = 1;

                        isFirstTime = false;
                        currentPage = 1;
                        allCatList.clear();
                        allFoodArray.clear();
                        getCategoryItemsFromNW(Cat_id, currentPage);
                        dialog.dismiss();
                    }
                }
                else{
                    if (Cat_id==null || Cat_id.isEmpty()) {
                        sortType = 0;

                        allCatList.clear();
                        allFoodArray.clear();
                        getFoodItemsFromNW("");
                        dialog.dismiss();
                    } else {
                        sortType = 0;

                        isFirstTime = false;
                        currentPage = 1;
                        allCatList.clear();
                        allFoodArray.clear();
                        getCategoryItemsFromNW(Cat_id, currentPage);
                        dialog.dismiss();
                    }

                }
            }
        });

        dialog.show();

        layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());

                int dialogWidth = lp.width;
                int dialogHeight = dialog.getWindow().getDecorView().getHeight();

                if (dialogHeight > MAX_HEIGHT) {
                    dialog.getWindow().setLayout(dialogWidth, MAX_HEIGHT);
                    dialog.show();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if ("en".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                        showWithCircularRevealAnimation(layout, CENTER);
                    } else {
                        showWithCircularRevealAnimation(layout, CENTER);
                    }


                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void openFoodMenu(final View myview) {
        final int MAX_HEIGHT = 800;

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.food_menu_popup);
        dialog.setTitle("");

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams param = window.getAttributes();
        if ("en".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            param.gravity = Gravity.BOTTOM | Gravity.END;
        } else {
            param.gravity = Gravity.BOTTOM | Gravity.START;
        }


        param.width = 700;
        // param.height =900;
        param.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(param);


        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        int dialogWidth = lp.width;
        int dialogHeight = lp.height;

        if(dialogHeight > MAX_HEIGHT) {
            dialog.getWindow().setLayout(dialogWidth,MAX_HEIGHT);
        }*/

        /*ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = 300;
        ((WindowManager.LayoutParams) params).gravity=Gravity.BOTTOM|Gravity.END;
        dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);*/
        final LinearLayout mainLay = dialog.findViewById(R.id.mainLay);
        TextView tv_allCatItems = dialog.findViewById(R.id.tv_allCatItems);
        tv_allCatItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortType=0;
                Cat_id = null;
                allFoodArray.clear();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                getFoodItemsFromNW("");
            }
        });
        final ListView dialog_ListView = dialog.findViewById(R.id.lvMenuList);
        foodMenuPopupAdapter = new FoodMenuPopupAdapter(catList, getActivity(), RestaurantOrderItemsFragment.this, null);
        dialog_ListView.setAdapter(foodMenuPopupAdapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long l) {

              /*  int count = 0;
                if (itemIndex > 0) {
                    for (int i = 0; i < itemIndex; i++) {
                        count = count + arrCatCount.get(i).getCategoryCount();
                    }

                }
                smoothScrollPos(count);*/
                sortType = 0;
                isFirstTime = false;
                currentPage = 1;
                Cat_id = catList.get(itemIndex).getId();
                allCatList.clear();
                allFoodArray.clear();
                getCategoryItemsFromNW(catList.get(itemIndex).getId(), currentPage);

                dialog.dismiss();
               /* mainLay.post(new Runnable() {
                    @Override
                    public void run() {
                        //create your anim here
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            hideWithCircularRevealAnimation(mainLay,BOTTOM_LEFT);
                        }
                    }
                });*/

            }
        });
        //dialog=AppUtils.setMargins(dialog,0,0,0,100);

        dialog.show();

        mainLay.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());

                int dialogWidth = lp.width;
                int dialogHeight = dialog.getWindow().getDecorView().getHeight();

                if (dialogHeight > MAX_HEIGHT) {
                    dialog.getWindow().setLayout(dialogWidth, MAX_HEIGHT);
                    dialog.show();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if ("en".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                        showWithCircularRevealAnimation(mainLay, BOTTOM_RIGHT);
                    } else {
                        showWithCircularRevealAnimation(mainLay, BOTTOM_LEFT);
                    }


                }
            }
        });
       /* mainLay.post(new Runnable() {
            @Override
            public void run() {
                //create your anim here
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    showWithCircularRevealAnimation(mainLay,BOTTOM_RIGHT);
                }
            }
        });*/


    }

    public void smoothScrollPos(final int selectedPosition) {

        final Handler handler = new Handler();
        //100ms wait to scroll to item after applying changes
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                lvFoods.smoothScrollToPositionFromTop(selectedPosition, 0);
                //lvFoods.smoothScrollToPosition(selectedPosition);
            }
        }, 100);
    }

    //Circular reveal Animation ----

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showWithCircularRevealAnimation(View view, int animationType) {
        currentType = animationType;

        int[] positionArray = getPositions(view, animationType);

        int cx = positionArray[0];
        int cy = positionArray[1];

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.setDuration(getResources().getInteger(R.integer.anim_duration_medium));

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void hideWithCircularRevealAnimation(final View view, int animationType) {
        int[] positionArray = getPositions(view, animationType);

        int cx = positionArray[0];
        int cy = positionArray[1];

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(view.getWidth(), view.getHeight());

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
        anim.setDuration(getResources().getInteger(R.integer.anim_duration_medium));

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void hideWithRevealEffect(final View view) {
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(view.getWidth(), view.getHeight());

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }

    private int[] getPositions(View view, int animationType) {
        int cx = 0, cy = 0;

        if (animationType == TOP_RIGHT) {
            cx = view.getWidth();
        } else if (animationType == CENTER) {
            cx = view.getWidth() / 2;
            cy = view.getHeight() / 2;
        } else if (animationType == BOTTOM_LEFT) {
            cy = view.getHeight();
        } else if (animationType == BOTTOM_RIGHT) {
            cx = view.getWidth();
            cy = view.getHeight();
        }

        positionArray[0] = cx;
        positionArray[1] = cy;

        return positionArray;
    }

    private void getCategoryItemsFromNW(final String cat_id, final int current_Page) {

        try {

            afieatGifLoaderDialog();
            Log.e("ALL_FOOD_ARRAY== >", "" + allFoodArray.size());
            /*categories.clear();
            views.clear();
            mAdaptersList.clear();
            allFoodArray.clear();
           if (!isFirstTime){
              // allFoodArray.clear();
               allCatList.clear();
           }*/

            Log.e("ALL_FOOD_ARRAY== >", "" + allFoodArray.size());

            AppUtils.hideViews(tvNoFood);
            Map<String, String> params = new HashMap<>();
            params.put("resid", mRestaurantId);
            params.put("group_id", RestaurantsDetailActivity.GroupID);
            params.put("cat_id", cat_id);
            params.put("page", String.valueOf(current_Page));
            params.put("has_photo", String.valueOf(sortType));

            System.out.println("RestaurantOrderItemsFragment : getFoodItemsFromNW : mRestaurantId : " + mRestaurantId);
            System.out.println("RestaurantOrderItemsFragment : getFoodItemsFromNW : RestaurantsDetailActivity.GroupID : " + RestaurantsDetailActivity.GroupID);
          /*  params.put("appToken", AppUtils.APPTOKEN);
            params.put("Authorization", AppUtils.AUTHRIZATIONKEY);*/

            if ("en".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                params.put("lang", "en");
            } else {
                params.put("lang", "ar");
            }

            Log.e("CatItemsFromNW :", "" + Apis.GET_FOOD_ITEMS + "?resid=" + mRestaurantId + "&group_id=" + RestaurantsDetailActivity.GroupID + "&cat_id=" + cat_id + "&page=" + current_Page);
            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_FOOD_ITEMS, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("Category_RESPONSE== >", "" + response.toString());


                            System.out.println("getCatItemsFromNW : response : " + response);
                            AppUtils.SEARCH_JSON_ = response;
                            AppUtils.log("Food Items-" + response);
                            //  AppUtils.hideViews(progressBar);

                            JSONObject foodItemsObject = response.optJSONObject("fooditems");
                            hintText = new ArrayList<>();
                            hintTextAr = new ArrayList<>();

                            JSONObject sizeObject = response.optJSONObject("size");
                            sizeObject.keys();


                            try {
                                maxRecords = response.getString("total_item");
                                Log.e("maxRecords==>", "" + maxRecords);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            JSONObject catObject = response.optJSONObject("cat_list");
                            Iterator<String> catKeys = catObject.keys();
                            catList = new ArrayList<CategoryList>();
                            while (catKeys.hasNext()) {
                                CategoryList categoryList = new CategoryList();
                                String cat_id = catKeys.next();
                                categoryList.setCat_type(catObject.optString(cat_id));
                                categoryList.setId(cat_id);
                                catList.add(categoryList);
                            }
                            Log.e("Category+LIST==>", "" + catList.toString());

                            AppUtils.log("CatSize-> " + sizeObject.toString());
                            if (foodItemsObject != null) {
                                isLoading = false;
                                Iterator<String> keys = foodItemsObject.keys();
                                Category category = null;
                                while (keys.hasNext()) {
                                    String catName = keys.next();
                                    category = new Category();
                                    category.setName(catName);
                                    category.foods = new ArrayList<>();


                                    JSONArray foodsArray = foodItemsObject.optJSONArray(catName);


                                    int i = 0;
                                    while (i < foodsArray.length()) {
                                        JSONObject foodObject = foodsArray.optJSONObject(i);

                                        AppUtils.log("!@@-> " + foodObject.toString());
                                        Food food = new Food();
                                        food.prices = new ArrayList<>();
                                        food.actualPrice = new ArrayList<>();

                                        DemoSearchModel mDemoSearchModelsMainNew = new DemoSearchModel();
                                        mDemoSearchModelsMainNew.mPriceList = new ArrayList<>();
                                        mDemoSearchModelsMainNew.mActualPriceList = new ArrayList<>();


                                        FoodCopy mFoodCopyNew = new FoodCopy();
                                        mFoodCopyNew.prices = new ArrayList<>();
                                        mFoodCopyNew.actualPrice = new ArrayList<>();

                                        try {
                                            JSONObject priceObject = new JSONObject(foodObject.optString("price"));
                                            Iterator<String> priceKeys = priceObject.keys();

                                            JSONObject actualPriceObject = new JSONObject(foodObject.optString("actual_price"));
                                            Iterator<String> actualPriceObjectKeys = actualPriceObject.keys();


                                            while (priceKeys.hasNext()) {

                                                Food.Price price = food.new Price();
                                                String priceId = priceKeys.next();
                                                price.setType(sizeObject.optString(priceId));
                                                price.setPrice(priceObject.optString(priceId));
                                                price.setId(priceId);
                                                food.prices.add(price);


                                                DemoSearchModel.Price mppp = mDemoSearchModelsMainNew.new Price();
                                                mppp.setType(sizeObject.optString(priceId));
                                                mppp.setPrice(priceObject.optString(priceId));
                                                mppp.setId(priceId);

                                                mDemoSearchModelsMainNew.mPriceList.add(mppp);

                                                //-------------------TEMP
                                                FoodCopy.Price fpp = mFoodCopyNew.new Price();
                                                fpp.setType(sizeObject.optString(priceId));
                                                fpp.setPrice(priceObject.optString(priceId));
                                                fpp.setId(priceId);

                                                mFoodCopyNew.prices.add(fpp);
                                                //------------------------------------
                                            }

                                            while (actualPriceObjectKeys.hasNext()) {
                                                Food.ActualPrice mActualPrice = food.new ActualPrice();
                                                String priceId = actualPriceObjectKeys.next();
                                                mActualPrice.setType(sizeObject.optString(priceId));
                                                mActualPrice.setPrice(actualPriceObject.optString(priceId));
                                                mActualPrice.setId(priceId);

                                                System.out.println("Key : " + priceId);

                                                food.actualPrice.add(mActualPrice);

                                                DemoSearchModel.ActualPrice mppp = mDemoSearchModelsMainNew.new ActualPrice();
                                                mppp.setType(sizeObject.optString(priceId));
                                                mppp.setPrice(actualPriceObject.optString(priceId));
                                                mppp.setId(priceId);

                                                mDemoSearchModelsMainNew.mActualPriceList.add(mppp);

                                                //-------------------- TEMP
                                                FoodCopy.ActualPrice fpp = mFoodCopyNew.new ActualPrice();
                                                fpp.setType(sizeObject.optString(priceId));
                                                fpp.setPrice(actualPriceObject.optString(priceId));
                                                fpp.setId(priceId);

                                                mFoodCopyNew.actualPrice.add(fpp);
                                                //----------------------

                                            }
                                            hintText.add(foodObject.optString("item_name"));
                                            hintTextAr.add(foodObject.optString("item_name_ar"));

                                            DemoSearchModel mDemoSearchModel = new DemoSearchModel(foodObject.optString("item_id"),
                                                    foodObject.optString("merchant_id"),
                                                    foodObject.optString("item_name"),
                                                    foodObject.optString("item_name_ar"), mDemoSearchModelsMainNew.mPriceList, mDemoSearchModelsMainNew.mActualPriceList
                                                    , foodObject.optString("photo"), mDemoSearchModelsMainNew.mPriceList.size());


                                            FoodCopy mFoodsAdapterCopy = new FoodCopy(foodObject.optString("item_id"),
                                                    foodObject.optString("merchant_id"),
                                                    foodObject.optString("item_name")
                                                    , foodObject.optString("item_name_ar")
                                                    , "", "", "", "", mFoodCopyNew.prices, mFoodCopyNew.actualPrice, foodObject.optString("photo"));


                                            mDemoSearchModelsMain.add(mDemoSearchModel);

                                            mFoodCopiesArrayListMain.add(mFoodsAdapterCopy);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        food.setId(foodObject.optString("item_id"));
                                        food.setName(foodObject.optString("item_name"));
                                        food.setMerchantId(foodObject.optString("merchant_id"));
                                        // Log.e("FOOD_MERCHANT_ID :",""+food.getMerchantId());

                                        if ("ar".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                                            food.setName(foodObject.optString("item_name_ar"));
                                        }
                                        food.setCategory(catName);
                                        food.setDiscount(foodObject.optString("discount"));
                                        food.setDescription(foodObject.optString("item_description"));
                                        food.setUriPic(foodObject.optString("photo"));
                                        food.setRestaurant_url(foodObject.optString("restaurant_url"));

                                        if (i == 0 && !isFirstTime) {
                                            isFirstTime = true;
                                            food.setHeaderTxt(category.name);
                                            food.setFirstPos(true);
                                        } else {
                                            isFirstTime = true;
                                            food.setHeaderTxt(category.name);
                                            food.setFirstPos(false);
                                            // food.setHeaderTxt("");
                                        }
                                        category.foods.add(food);
                                        i++;
                                    }
                                    ArrayList<Food> arr = new ArrayList<>();
                                    arr.addAll(category.foods);
                                    CategoryCountModel cmodel = new CategoryCountModel();
                                    cmodel.setCategoryName(category.name);
                                    cmodel.setCategoryCount(arr.size());
                                    arrCatCount.add(cmodel);
                                    categories.add(category);
                                    allCatList.addAll(category.foods);
                                    numFoodItems++;
                                    arr.clear();
                                    arr = null;
                                  /*  if (adapter.getCount()==0) {
                                        adapter = new FoodsAdapter(allCatList, getContext(), RestaurantOrderItemsFragment.this, null);
                                        lvFoods.setAdapter(adapter);
                                    } else {
                                        if (foodsArray.length() > 0) {
                                            try {
//
                                               ((FoodsAdapter) lvFoods.getAdapter()).notifyDataSetChanged();
                                                System.out.println("Amit : RestaurentListAdapterRecyclerView : notified : restaurantsArray.length() : " + foodsArray.length());


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {

                                        }

                                    }*/


                                }



                                   /* listView = (ListView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_listview_fooditems, null);
                                    ViewCompat.setNestedScrollingEnabled(listView, true);
                                    adapter = new FoodsAdapter(category.foods, getContext(), RestaurantOrderItemsFragment.this, null);

                                    //----------------------

                                    adapter.setOnRestaurantPhotoClicked(new OnRestaurantPhotoClicked() {
                                        @Override
                                        public void onPhotoClicked(Uri path) {
                                            Fragment fragment = SimplePhotoFragment.newInstance(path);
                                            getFragmentManager()
                                                    .beginTransaction()
                                                    .add(R.id.frameLayout, fragment)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    });
                                    listView.setAdapter(adapter);
                                    mAdaptersList.add(adapter);


                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            if (!AppUtils.IS_SUPER_MARKET) {

                                            System.out.println("Rahul : RestaurantOrderItemsFragment : onItemClick : String.valueOf(id) " + String.valueOf(id));
                                            System.out.println("Rahul : RestaurantOrderItemsFragment : onItemClick : ((Food) parent.getAdapter().getItem(position)).getName() " + ((Food) parent.getAdapter().getItem(position)).getName());
                                            Intent intent = new Intent(getActivity(), FoodAddBasketActivity.class);
                                            intent.putExtra(AppUtils.EXTRA_FOOD_ID, String.valueOf(id));
                                            intent.putExtra(AppUtils.EXTRA_FOOD_NAME, ((Food) parent.getAdapter().getItem(position)).getName());
                                            startActivity(intent);
                                            }else
                                            {

                                            }
                                        }
                                    });
                                    views.add(listView);
                                    // listView.setSelection(6);
                                    categories.add(category);
                                    numFoodItems++;*/


                               /* if (current_Page==0){
                                    adapter = new FoodsAdapter(allCatList, getContext(), RestaurantOrderItemsFragment.this, null);

                                   // adapter.notifyDataSetChanged();
                                }*/
                                if (currentPage == 1) {

                                adapter = new FoodsAdapter(allCatList, getContext(), RestaurantOrderItemsFragment.this, null);
                                //ViewCompat.setNestedScrollingEnabled(lvFoods, true);
                                lvFoods.setAdapter(adapter);

                                firstVisibleItem = lvFoods.getFirstVisiblePosition();
                                oldCount = adapter.getCount();
                                lvView = lvFoods.getChildAt(0);
                                pos = (view == null ? 0 : view.getBottom());
                                    Log.e("firstVisibleItem",""+firstVisibleItem);
                                    Log.e("oldCount",""+oldCount);
                                    Log.e("pos",""+pos);
                                    Log.e("currentPage",""+currentPage);
                            }else{

                                   // ((FoodsAdapter)lvFoods.getAdapter()).notifyDataSetChanged();
                                    adapter = new FoodsAdapter(allCatList, getContext(), RestaurantOrderItemsFragment.this, null);
                                    lvFoods.setAdapter(adapter);
                                    lvFoods.setSelection(oldCount);
                                    Log.e("postionListAF",""+(firstVisibleItem + adapter.getCount() - oldCount + 1)+":"+""+pos);
                                    Log.e("firstVisibleItemAF",""+firstVisibleItem);
                                    Log.e("oldCountAF",""+oldCount);
                                    Log.e("posAF",""+pos);
                                    Log.e("currentPageAF",""+currentPage);

                                }



                                ViewCompat.setNestedScrollingEnabled(lvFoods, true);
                                layHeader.setVisibility(View.VISIBLE);
                                txtFirstHeader.setText(allCatList.get(0).getHeaderTxt());
                                txtMenu.setVisibility(View.VISIBLE);

                                adapter.setOnRestaurantPhotoClicked(new OnRestaurantPhotoClicked() {
                                    @Override
                                    public void onPhotoClicked(Uri path) {
                                        Fragment fragment = SimplePhotoFragment.newInstance(path);
                                        getFragmentManager()
                                                .beginTransaction()
                                                .add(R.id.frameLayout, fragment)
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                });


                                lvFoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        if (!AppUtils.IS_SUPER_MARKET) {

                                            System.out.println("Rahul : RestaurantOrderItemsFragment : onItemClick : String.valueOf(id) " + id);
                                            System.out.println("Rahul : RestaurantOrderItemsFragment : onItemClick : ((Food) parent.getAdapter().getItem(position)).getName() " + ((Food) parent.getAdapter().getItem(position)).getName());
                                            Intent intent = new Intent(getActivity(), FoodAddBasketActivity.class);
                                            intent.putExtra(AppUtils.EXTRA_FOOD_ID, String.valueOf(id));
                                            intent.putExtra(AppUtils.EXTRA_FOOD_NAME, ((Food) parent.getAdapter().getItem(position)).getName());
                                            startActivity(intent);
                                        }
                                    }
                                });

                                lvFoods.setOnScrollListener(new AbsListView.OnScrollListener() {

                                    @Override
                                    public void onScrollStateChanged(AbsListView view,
                                                                     int scrollState) {


                                    }

                                    @Override
                                    public void onScroll(AbsListView view, int firstVisibleItem,
                                                         int visibleItemCount, int totalItemCount) {

                                      /*  if (afieatGifLoaderDialog != null) {
                                            afieatGifLoaderDialog.dismiss();
                                        }
                                        currentPage++;
                                        getCategoryItemsFromNW(cat_id, current_Page);
*/

                                    }
                                });

                                Gson gson = new Gson();
                                String json = gson.toJson(mDemoSearchModelsMain);

                                System.out.println("klnflnef : json : " + json);


                                String jso1n = gson.toJson(mFoodCopiesArrayListMain);
                                System.out.println("Checkoutthis : json : " + jso1n);

                                AppUtils.HINT_TEXT = hintText;
                                AppUtils.HINT_TEXT_AR = hintTextAr;
                                AppUtils.DEMO_SEARCH_MODEL_LIST = mDemoSearchModelsMain;
                                AppUtils.DEMO_FOOD_COPY = mFoodCopiesArrayListMain;

                                System.out.println("Checkoutthis : AppUtils.HINT_TEXT : " + AppUtils.HINT_TEXT);
                                System.out.println("Checkoutthis : AppUtils.HINT_TEXT_AR : " + AppUtils.HINT_TEXT_AR);


                                if (afieatGifLoaderDialog != null) {
                                    try {
                                        afieatGifLoaderDialog.dismiss();
                                    } catch (IllegalArgumentException e) {

                                    }

                                }
                            } else {
                                if (afieatGifLoaderDialog != null) {
                                    afieatGifLoaderDialog.dismiss();
                                }
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }

                                if (!isFirstTime) {
                                    Toast.makeText(getActivity(), "No food items found", Toast.LENGTH_SHORT).show();
                                    getFoodItemsFromNW("");
                                } else {
//                                    Toast.makeText(getActivity(), "End of the list", Toast.LENGTH_SHORT).show();
                                }

                                isLoading = false;
                            }
                        }


                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("ERROR :=", "" + error.getMessage().toString());
                            //AppUtils.hideViews(progressBar);
                            if (afieatGifLoaderDialog != null) {
                                afieatGifLoaderDialog.dismiss();
                            }
                            error.printStackTrace();
                            isLoading = false;
                        }
                    }
            );
            AppInstance.getInstance(mContext.getApplicationContext()).addToRequestQueue(request);
        } catch (Exception e) {
            e.printStackTrace();
            if (afieatGifLoaderDialog != null) {
                afieatGifLoaderDialog.dismiss();
            }
            isLoading = false;

            Log.e("ERROR :=", "" + e.getMessage().toString());
            //   AppUtils.hideViews(progressBar);
        }


    }


}
