package com.afieat.ini.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.afieat.ini.FoodAddBasketActivity;
import com.afieat.ini.R;
import com.afieat.ini.adapters.FoodsAdapter;
import com.afieat.ini.interfaces.OnRestaurantPhotoClicked;
import com.afieat.ini.models.Food;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

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
public class RestaurantPopularProductsFragment extends Fragment {

    private List<Food> foods;
    private String mRestaurantId;

    private ListView lvFoods;
    private ProgressBar progressBar;

    public RestaurantPopularProductsFragment() {
        foods = new ArrayList<>();
    }

    public static RestaurantPopularProductsFragment newInstance(String restaurantId) {
        RestaurantPopularProductsFragment fragment = new RestaurantPopularProductsFragment();
        fragment.mRestaurantId = restaurantId;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_popular_products, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        lvFoods = (ListView) view.findViewById(R.id.lvFoods);
        FoodsAdapter adapter = new FoodsAdapter(foods, getContext(),null,RestaurantPopularProductsFragment.this);
        adapter.setOnRestaurantPhotoClicked(new OnRestaurantPhotoClicked() {
            @Override
            public void onPhotoClicked(Uri path) {
                Fragment fragment = SimplePhotoFragment.newInstance(path);
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lvFoods.setNestedScrollingEnabled(true);
        }
        ViewCompat.setNestedScrollingEnabled(lvFoods, true);
        lvFoods.setAdapter(adapter);
        lvFoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FoodAddBasketActivity.class);
                intent.putExtra(AppUtils.EXTRA_FOOD_ID, String.valueOf(id));
                intent.putExtra(AppUtils.EXTRA_FOOD_NAME, ((Food) parent.getAdapter().getItem(position)).getName());
                startActivity(intent);
            }
        });
        loadPopularFoodsFromNW();
    }

    private void loadPopularFoodsFromNW() {
        AppUtils.showViews(progressBar);
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", mRestaurantId);
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_POPULAR_PRODUCTS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.hideViews(progressBar);
                        AppUtils.log(response);
                        JSONArray foodItemsObject = response.optJSONArray("fooditems");
                        JSONObject sizeObject = response.optJSONObject("size");
                        int i = 0;
                        while (i < foodItemsObject.length()) {
                            JSONObject foodObject = foodItemsObject.optJSONObject(i);
                            Food food = new Food();
                            food.prices = new ArrayList<>();
                            try {
                                JSONObject priceObject = new JSONObject(foodObject.optString("price"));
                                Iterator<String> priceKeys = priceObject.keys();
                                while (priceKeys.hasNext()) {
                                    Food.Price price = food.new Price();
                                    String priceId = priceKeys.next();
                                    price.setType(sizeObject.optString(priceId));
                                    price.setPrice(priceObject.optString(priceId));
                                    food.prices.add(price);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            food.setId(foodObject.optString("item_id"));
                            food.setName(foodObject.optString("item_name"));
                            if ("ar".equals(AppInstance.getInstance(getActivity().getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                                food.setName(foodObject.optString("item_name_ar"));
                            }
                            food.setDiscount(foodObject.optString("discount"));
                            food.setDescription(foodObject.optString("item_description"));
                            food.setUriPic(foodObject.optString("photo"));
                            foods.add(food);
                            i++;
                        }
                        ((FoodsAdapter) lvFoods.getAdapter()).notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppUtils.hideViews(progressBar);
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
    }
}
