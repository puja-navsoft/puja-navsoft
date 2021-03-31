package com.afieat.ini.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.adapters.slidingImageAdapter;
import com.afieat.ini.database.DBHelper;
import com.afieat.ini.fragments.dummy.FoodCategoryContent;
import com.afieat.ini.fragments.dummy.FoodCategoryContent.CategoryItem;
import com.afieat.ini.models.InProcessOrders;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.AuthkeyValidator;
import com.afieat.ini.utils.NetworkRequest;
import com.afieat.ini.webservice.ApiClient;
import com.afieat.ini.webservice.ApiInterface;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static com.afieat.ini.utils.AppUtils.indexExists;


public class CategoryofFoodFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_CITY = "CITY";
    private static final String ARG_REGI = "REGI";
    // TODO: Customize parameters
    private OnListOrderListener mListener;
    public static int DivderListPositionno = 0;
    private Snackbar snackNoRestaurants;
    private String CityId, RegieonId;
    private RecyclerView recyclerView;
    // ProgressDialog Pdialog;
    private Dialog afieatGifLoaderDialog;
    private ArrayList<CategoryItem.AddsBean> arrAds;
    private String first_position_after;
    private String place_interval;
    private ArrayList<Integer> arrAdsinterval;
    private CategoryofFoodRecyclerViewAdapter myadp;
    private CardView layPendingCart;
    private TextView restaurantName;
    private AppCompatImageView imgDelete;
    private TextView txtViewCart;
    private DBHelper db;
    private int DBitemCount;
    private View orderCartView;
    private ImageView imgRest;
    private ViewPager mPager;
    private DotsIndicator indicator;
    private ArrayList<InProcessOrders> arrInProcessOrders;
    private  Handler handler=null;


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CategoryofFoodFragment newInstance(String C_id, String R_Id) {
        CategoryofFoodFragment fragment = new CategoryofFoodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CITY, C_id);
        args.putString(ARG_REGI, R_Id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        if (getArguments() != null) {
            CityId = getArguments().getString(ARG_CITY);
            RegieonId = getArguments().getString(ARG_REGI);
        }
      /*  Pdialog = new ProgressDialog(getActivity());
        Pdialog.setMessage(getString(R.string.msg_please_wait));
        Pdialog.setCanceledOnTouchOutside(false);*/
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem itemfilter = menu.findItem(R.id.menu_filter);
        MenuItem itemnotificatin = menu.findItem(R.id.menu_notification);
        MenuItem itemsearch = menu.findItem(R.id.menu_search);
        itemsearch.setVisible(true);
        itemfilter.setVisible(false);
        itemnotificatin.setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categoryoffood_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         snackNoRestaurants = Snackbar.make(getActivity().findViewById(R.id.page), getString(R.string.msg_no_restaurants), Snackbar.LENGTH_SHORT);
         orderCartView=view.findViewById(R.id.orderCartView);
         indicator = (DotsIndicator) view.findViewById(R.id.indicator);
         mPager = (ViewPager) view.findViewById(R.id.pager);

        // Set the adapter
        /* if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            get_GROUPCATAGORY();
        }*/
       // fetchUndeliveredOrder();
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        get_GROUPCATAGORY();


    }

    private void get_GROUPCATAGORY() {

        try {
            mListener.OnDtaFethchingStart();
            Map<String, String> params = new HashMap<>();
            params.put("cid", CityId);
            params.put("rid", RegieonId);

            AppUtils.log("Q@-" + CityId + "-" + RegieonId);
            // Pdialog.show();
            afieatGifLoaderDialog();

            System.out.println("Rahul : cid : " + CityId);
            System.out.println("Rahul : rid : " + RegieonId);

            NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_GRUPCATEGOTY_LIST_REGION, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    AppUtils.log("xxx : flContainer resp-" + response);
                    System.out.println("xxx : CategoryList resp-" + response);
                    if (response.has("status") && response.optInt("status") == 111) {
                        new AuthkeyValidator(getActivity()).CallForReAuth(new AuthkeyValidator.Authkey() {
                            @Override
                            public void Oncomplete() {
                                get_GROUPCATAGORY();
                            }
                        });
                    } else if (response.optInt("code") == 1) {
                        try {
                            FoodCategoryContent.ClearData();
                            JSONArray array = response.getJSONArray("group");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject ItemObject = array.optJSONObject(i);
                                CategoryItem item = new CategoryItem();
                                item.setGroup_id(ItemObject.optString("group_id"));
                                item.setIs_featured(ItemObject.optString("is_featured"));
                                item.setGroup_name(ItemObject.optString("group_name"));
                                item.setGroup_name_ar(ItemObject.optString("group_name_ar"));
                                item.setGroup_description(ItemObject.optString("group_description"));
                                item.setGroup_description_ar(ItemObject.optString("group_description_ar"));
                                item.setPhoto(ItemObject.optString("photo"));
                                item.setDate_created(ItemObject.optString("date_created"));
                                item.setResturantCount(ItemObject.optInt("restaurant_count"));
                                FoodCategoryContent.addItem(item);
                                if ("0".equals(item.getIs_featured()) && DivderListPositionno == 0) {
                                    DivderListPositionno = i;
                                }

                            }

                            JSONArray addsArray = response.optJSONArray("adds");
                            if(addsArray!=null) {
                                arrAds = new ArrayList<>();
                                for (int i = 0; i < addsArray.length(); i++) {
                                    JSONObject object = addsArray.optJSONObject(i);
                                    CategoryItem.AddsBean model=new CategoryItem.AddsBean();
                                    model.setAdds_id(object.optString("adds_id"));
                                    model.setAds_photo(object.optString("ads_photo"));
                                    model.setCity_ids(object.optString("city_ids"));
                                    model.setDate_created(object.optString("date_created"));
                                    model.setDisplay_page_id(object.optString("display_page_id"));
                                    model.setDate_modified(object.optString("date_modified"));
                                    model.setGroup_ids(object.optString("group_ids"));
                                    model.setIp_address(object.optString("ip_address"));
                                    model.setLink_url(object.optString("link_url"));
                                    model.setRestaurant_id(object.optString("restaurant_id"));
                                    model.setStatus(object.optString("status"));
                                    model.setItem_id(object.optString("item_id"));
                                    model.setSponsored_expiration(object.optString("sponsored_expiration"));
                                    model.setSponsored_start_date(object.optString("sponsored_start_date"));
                                    model.setLink_option(object.optString("link_option"));
                                    model.setLink_type(object.optString("link_type"));
                                    model.setRestaurant_name(object.optString("restaurant_name"));
                                    model.setRestaurant_name_ar(object.optString("restaurant_name_ar"));
                                    model.setMerchant_minimum_order(object.optString("merchant_minimum_order"));
                                    model.setRestaurant_slug(object.optString("restaurant_slug"));
                                    CategoryItem resmodel=new CategoryItem();
                                    resmodel.setAddsBean(model);
                                    arrAds.add(model);
                                    model=null;
                                    resmodel=null;
                                }
                            }
                            JSONObject addsPosi = response.optJSONObject("adds_position");
                            if(addsPosi!=null) {
                                first_position_after = addsPosi.optString("first_position_after");
                                place_interval = addsPosi.optString("place_interval");

                                arrAdsinterval=new ArrayList<>();
                                arrAdsinterval.add(Integer.parseInt(first_position_after));
                                arrAdsinterval.add(Integer.parseInt(first_position_after)+Integer.parseInt(place_interval));
                                int count=Integer.parseInt(place_interval)+Integer.parseInt(first_position_after);
                                for(int i=2;i<FoodCategoryContent.ITEMS.size();i++){

                                    count=count+Integer.parseInt(place_interval);
                                    arrAdsinterval.add(count);
                                }
                                Collections.sort(FoodCategoryContent.ITEMS, new Comparator<CategoryItem>()
                                {
                                    @Override
                                    public int compare(CategoryItem lhs, CategoryItem rhs) {

                                        return rhs.getIs_featured().compareTo(lhs.getIs_featured());
                                    }
                                });
                            }

                          /*  if(arrAds!=null && arrAds.size()>0){

                                for(int i=0;i<arrAdsinterval.size();i++){

                                    if(arrAdsinterval.get(i)<FoodCategoryContent.ITEMS.size() && FoodCategoryContent.ITEMS.get(arrAdsinterval.get(i)).getAddsBean()==null){
                                        CategoryItem resmodel=new CategoryItem();
                                        if(indexExists(FoodCategoryContent.ITEMS,i)){
                                            resmodel.setAddsBean(arrAds.get(i));
                                            resmodel.setIs_featured("1");
                                            if(indexExists(FoodCategoryContent.ITEMS,arrAdsinterval.get(i))){
                                                FoodCategoryContent.addItem(resmodel,arrAdsinterval.get(i));
                                            }
                                        }

                                    }
                                }

                               *//* CategoryItem resmodel=new CategoryItem();
                                resmodel.setAddsBean(arrAds.get(0));
                                if(indexExists(FoodCategoryContent.ITEMS,arrAdsinterval.get(0))){
                                    FoodCategoryContent.addItem(resmodel,arrAdsinterval.get(0));
                                }*//*

                            }*/

                            try {
                                if (arrAds != null && arrAds.size() > 0) {

                                    for (int i = 0; i < arrAdsinterval.size(); i++) {

                                        if (arrAdsinterval.get(i) < FoodCategoryContent.ITEMS.size() && FoodCategoryContent.ITEMS.get(arrAdsinterval.get(i)).getAddsBean() == null) {
                                            CategoryItem resmodel = new CategoryItem();
                                            if (indexExists(FoodCategoryContent.ITEMS, i)) {
                                                if(i<arrAds.size()) {
                                                    resmodel.setAddsBean(arrAds.get(i));
                                                    resmodel.setIs_featured("1");
                                                    if (indexExists(FoodCategoryContent.ITEMS, arrAdsinterval.get(i))) {
                                                        FoodCategoryContent.addItem(resmodel, arrAdsinterval.get(i));

                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            myadp=new CategoryofFoodRecyclerViewAdapter(getActivity(), FoodCategoryContent.ITEMS, mListener);
                            GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
                            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                @Override
                                public int getSpanSize(int position) {
                                    if(FoodCategoryContent.ITEMS.get(position).getAddsBean()!=null){
                                        int ret=2;
                                        try {
                                            if (!FoodCategoryContent.ITEMS.get(position - 1).getIs_featured().equals("1")) {
                                                ret=1;
                                                return ret;
                                            }
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        return  ret;
                                    }
                                    else{

                                        if(FoodCategoryContent.ITEMS.get(position).getIs_featured().equals("1")){
                                            return 2;
                                        }
                                        else{
                                            return  1;
                                        }
                                       /* if (position > DivderListPositionno - 1)
                                            return 1;
                                        else
                                            return 2;*/
                                    }

                                }
                            });
                            recyclerView.setLayoutManager(manager);
                           // recyclerView.setAdapter(new CategoryofFoodRecyclerViewAdapter(getActivity(), FoodCategoryContent.ITEMS, mListener));
                            recyclerView.setAdapter(myadp);
                            mListener.OnDataFetched();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();

                        }

                    } else {
                        if (!snackNoRestaurants.isShown())
                            snackNoRestaurants.show();
                    }
                    //  Pdialog.dismiss();
                    if (afieatGifLoaderDialog != null) {
                        afieatGifLoaderDialog.dismiss();
                    }
                    if(AppUtils.IS_CART_VISIBLE.equalsIgnoreCase("")) {
                        fetchUndeliveredOrder();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Pdialog.dismiss();
                    if (afieatGifLoaderDialog != null) {
                        afieatGifLoaderDialog.dismiss();
                    }
                }
            });
            AppInstance.getInstance(getActivity()).addToRequestQueue(request);

        } catch (Exception e) {
            e.printStackTrace();
            // Pdialog.dismiss();
            if (afieatGifLoaderDialog != null) {
                afieatGifLoaderDialog.dismiss();
            }
        }

    }


    @Override
    public void onAttach(Context context) throws RuntimeException{
        super.onAttach(context);
        if (context instanceof OnListOrderListener) {
            mListener = (OnListOrderListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() throws RuntimeException{
        super.onStart();
        if (getActivity() instanceof OnListOrderListener) {
            mListener = (OnListOrderListener) getActivity();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnListOrderListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(CategoryItem item);

        void OnDtaFethchingStart();

        void OnDataFetched();
    }

    private void afieatGifLoaderDialog() {

        afieatGifLoaderDialog = new Dialog(getActivity());
        afieatGifLoaderDialog.setCancelable(false);
        afieatGifLoaderDialog.setContentView(R.layout.afieat_gif_loader_dialog);
        afieatGifLoaderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        afieatGifLoaderDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(AppUtils.IS_CART_VISIBLE.equalsIgnoreCase("")) {
            fetchUndeliveredOrder();
        }
       /* handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                fetchUndeliveredOrder();
            }
        }, 4000);
*/
    }

    public  void fetchUndeliveredOrder() {
        //afieatGifLoaderDialog();
        try {
           /* HashMap<String, Object> params = new HashMap<>();
            params.put("user_id", AppInstance.getInstance(getActivity()).getFromSharedPref(AppUtils.PREF_USER_ID));*/
            ApiInterface apiService = ApiClient.GetClient().create(ApiInterface.class);
            Call<List<InProcessOrders>> call = apiService.getUndeliveredOrders(AppInstance.getInstance(getActivity()).getAuthkey(),AppUtils.AUTHRIZATIONKEY,AppInstance.getInstance(getActivity()).getFromSharedPref(AppUtils.PREF_USER_ID));
            call.enqueue(new Callback<List<InProcessOrders>>() {
                @Override
                public void onResponse(Call<List<InProcessOrders>> call, retrofit2.Response<List<InProcessOrders>> response) {
                    if(response!=null){

                        try{
                            arrInProcessOrders=(ArrayList<InProcessOrders>) response.body();
                            try {
                                if(getActivity()!=null) {
                                    db = new DBHelper(getActivity());
                                    DBitemCount = db.getFoodsBasket(AppInstance.getInstance(getContext()).getFromSharedPref(AppUtils.PREF_USER_ID)).size();
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }

                            if(DBitemCount>0 || arrInProcessOrders.size()>0){
                                orderCartView.setVisibility(View.VISIBLE);
                                mPager.setAdapter(new slidingImageAdapter(getActivity(),arrInProcessOrders));
                                indicator.setViewPager(mPager);
                            }
                            else{
                                orderCartView.setVisibility(View.GONE);
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }


                    }

                    if (afieatGifLoaderDialog != null) {
                        afieatGifLoaderDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<List<InProcessOrders>> call, Throwable t) {
                    System.out.print(t.toString());

                    if (afieatGifLoaderDialog != null) {
                        afieatGifLoaderDialog.dismiss();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void hideCartView(){
        orderCartView.setVisibility(View.GONE);
    }
}
