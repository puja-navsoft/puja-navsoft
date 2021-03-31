package com.afieat.ini.adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.R;
import com.afieat.ini.database.DBHelper;
import com.afieat.ini.fragments.RestaurantOrderItemsFragment;
import com.afieat.ini.fragments.RestaurantPopularProductsFragment;
import com.afieat.ini.interfaces.OnRestaurantPhotoClicked;
import com.afieat.ini.models.Food;
import com.afieat.ini.models.FoodCopy;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amartya on 13/04/16 with love.
 */
public class FoodsAdapterCopy extends BaseAdapter implements Filterable {

    int lastPosition = -1;
    private List<FoodCopy> foods;
    private List<FoodCopy> foodsFilter;
    private OnRestaurantPhotoClicked mListener;
    private Context context;
    ProgressDialog progressDialog;
    ListView mListView;

    RecyclerView size_dialog_recyclerview;
    String platePrice;

    private Food mFood;
    private double mBasePrice;
    ViewOnlineSearchResultAdapter mViewOnlineSearchResultAdapter;
    DBHelper db;
    Dialog mSearchDialog;
    List<String> sales_price_list = new ArrayList<>(), actual_price_list = new ArrayList<>(), size_type_list = new ArrayList<>();
    RestaurantOrderItemsFragment mRestaurantOrderItemsFragment;
    RestaurantPopularProductsFragment mRestaurantPopularProductsFragment;
    FoodsAdapter mFoodsAdapter;

    public FoodsAdapterCopy(List<FoodCopy> foods, Context context, FoodsAdapter mFoodsAdapter) {
        this.foods = foods;
        foodsFilter = foods;
        this.context = context;
        this.mFoodsAdapter = mFoodsAdapter;
      /*  if (mRestaurantOrderItemsFragment != null) {
            this.mRestaurantOrderItemsFragment = mRestaurantOrderItemsFragment;
        } else {
            this.mRestaurantPopularProductsFragment = mRestaurantPopularProductsFragment;
        }*/

        this.mListView = mListView;
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.msg_please_wait));
        mFood = new Food();

        mFoodsAdapter.updateBottomViewSearchOne();
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }


    @Override
    public int getCount() {
        return foodsFilter.size();
    }

    @Override
    public Object getItem(int position) {
        return foodsFilter.get(position);
    }


    @Override
    public long getItemId(int position) {
        return Long.parseLong(foodsFilter.get(position).getId());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        try {

            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fooditem, parent, false);


            final FoodCopy food = foodsFilter.get(position);
            System.out.println("nsdvns : merchant_id : " + food.getMerchantId());
            final String logoPath = Apis.IMG_PATH + "items/image/thumb_68_68/" + food.getUriPic();
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    //     ((ImageView) view.findViewById(R.id.ivFoodLogo)).setImageURI(Uri.parse(logoPath));
                    Glide
                            .with(context)
                            .load(Uri.parse(logoPath))
                            .placeholder(R.drawable.placeholder_land)
                            .into((ImageView) view.findViewById(R.id.ivFoodLogo));
                }
            });

            if (AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG).equalsIgnoreCase("ar")) {
                view.findViewById(R.id.Add).setBackground(context.getResources().getDrawable(R.drawable.plus_add_bg_ar));
                view.findViewById(R.id.plus).setBackground(context.getResources().getDrawable(R.drawable.plus_bg_ar));
                view.findViewById(R.id.minus).setBackground(context.getResources().getDrawable(R.drawable.minus_bg_ar));

                view.findViewById(R.id.Add).setPadding(25, 15, 25, 15);
                view.findViewById(R.id.plus).setPadding(28, 15, 28, 15);
                view.findViewById(R.id.minus).setPadding(30, 15, 30, 15);
                ((TextView) view.findViewById(R.id.tvItemTitle)).setText(food.getName_ar());

            }else
            {
                ((TextView) view.findViewById(R.id.tvItemTitle)).setText(food.getName());
            }


            if (food.getDiscount().length() > 0)
                ((TextView) view.findViewById(R.id.tvDiscount)).setText("IQD " + food.getDiscount() + " off");
            LinearLayout llPrices = (LinearLayout) view.findViewById(R.id.llPrices);
            int i = 0;


            for (FoodCopy.Price priceObject : food.prices) {


                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_food_plate_price, parent, false);
                ((TextView) view1.findViewById(R.id.tvPrice)).setText(parent.getContext().getString(R.string.currency) + AppUtils.changeToArabic(priceObject.getPrice(), parent.getContext()));
                ((TextView) view1.findViewById(R.id.tvPlate)).setText("(" + priceObject.getType() + ")");

                double salesPrice = 0, actualPrice = 0;
                if (priceObject.getPrice() != null || !"".equals(priceObject.getPrice().trim())) {
                    salesPrice = Double.parseDouble(priceObject.getPrice());
                }
                if (food.actualPrice.get(i).getPrice() != null || !"".equals(food.actualPrice.get(i).getPrice().trim())) {
                    actualPrice = Double.parseDouble(food.actualPrice.get(i).getPrice());
                }

                if (salesPrice != actualPrice) {
                    ((TextView) view1.findViewById(R.id.tvActualPrice)).setText(parent.getContext().getString(R.string.currency) + AppUtils.changeToArabic(food.actualPrice.get(i).getPrice(), parent.getContext()));
                } else {
                    ((RelativeLayout) view1.findViewById(R.id.relPrice)).setVisibility(View.GONE);
                }

                sales_price_list.add(String.valueOf(salesPrice));
                actual_price_list.add(String.valueOf(actualPrice));
                size_type_list.add(String.valueOf(priceObject.getType()));


                llPrices.addView(view1);
                i++;
            }


            if (position > lastPosition) {

                Animation animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
                view.startAnimation(animation);
                lastPosition = position;
            }

            ViewCompat.setTransitionName((ImageView) view.findViewById(R.id.ivFoodLogo), food.getName());

   /*     view.findViewById(R.id.ivFoodLogo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = "";
                System.out.println("ivFoodLogo : " + logoPath);
                if (logoPath.contains("68_68")) path = logoPath.replace("68_68", "376_312");
                mListener.onPhotoClicked(Uri.parse(path));
            }
        });*/
            final LinearLayout add_before_linear = view.findViewById(R.id.add_before_linear);
            final LinearLayout plus_minus_linear = view.findViewById(R.id.plus_minus_linear);

            add_before_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    db = new DBHelper(context);
                    Log.e("MERCHANT_ID==>",""+db.getBasketMerchantId())  ;
                    Log.e("MERCHANT_ID==>",""+foodsFilter.get(position).getMerchantId())  ;
                    if (db.getBasketMerchantId().equals(foodsFilter.get(position).getMerchantId()) || db.getBasketMerchantId().length() == 0) {
                        Gson gson = new Gson();
                        JSONArray mJsonArray_actual_price = null, mJsonArray_sales_price = null;
                        if (food.prices.size() > 1) {
                            //   Toast.makeText(,food.prices.size(),Toast.LENGTH_SHORT).show();
                            System.out.println("ytcyvvbjkkkl");

                            //  Gson gson1 = new Gson();
                            String json_actual_price = gson.toJson(food.getActualPrice());
                            String json_sales_price = gson.toJson(food.getPrices());

                            try {
                                mJsonArray_actual_price = new JSONArray(json_actual_price);
                                mJsonArray_sales_price = new JSONArray(json_sales_price);
                                System.out.println("add_before_linear : mJsonArray_actual_price : " + mJsonArray_actual_price.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println("add_before_linear : json_actual_price : " + json_actual_price);
                            System.out.println("add_before_linear : json_sales_price : " + json_sales_price);

                            sizeDialog(mJsonArray_actual_price, mJsonArray_sales_price, food.getName(), position, foodsFilter, view);
                        } else {

                            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor mEditor = sharedpreferences.edit();

                            mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL", AppUtils.CURRENT_RESTAURANT_NAME);
                            mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL_AR", AppUtils.CURRENT_RESTAURANT_NAME_AR);
                            mEditor.apply();

                            String json_actual_price = gson.toJson(food.getActualPrice());
                            String json_sales_price = gson.toJson(food.getPrices());
                            try {
                                //  mJsonArray_actual_price = new JSONArray(json_actual_price);
                                mJsonArray_sales_price = new JSONArray(json_sales_price);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mBasePrice = Double.parseDouble(foodsFilter.get(position).getPrices().get(0).getPrice());


                            System.out.println("dosdvosnvo : getId : " + foodsFilter.get(position).getId());
                            System.out.println("dosdvosnvo : getName : " + foodsFilter.get(position).getName());
                            System.out.println("dosdvosnvo : getMerchantId : " + foodsFilter.get(position).getMerchantId());
                            try {
                                System.out.println("dosdvosnvo : PLATE_PRICE : " + mJsonArray_sales_price.getJSONObject(0).getString("price"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            //Set Food Id,Name,
                            mFood.setId(foodsFilter.get(position).getId());
                            mFood.setName(foodsFilter.get(position).getName());
                            mFood.setMerchantId(foodsFilter.get(position).getMerchantId());
                            mFood.setPriceBasket(AppUtils.monetize(String.valueOf(mBasePrice)));
                            mFood.setUnitPrice(String.valueOf(mBasePrice / 1));

                            try {
                                mFood.setSizeBasketId(mJsonArray_sales_price.getJSONObject(0).getString("id"));
                                mFood.setSizeBasket(mJsonArray_sales_price.getJSONObject(0).getString("type"));

                                System.out.println("lsnkvn : " + mJsonArray_sales_price.getJSONObject(0).getString("id"));
                                System.out.println("lsnkvn :" + mJsonArray_sales_price.getJSONObject(0).getString("type"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            mFood.setAddOnIds("");
                            mFood.setIngredientIds("");
                            mFood.setAddOns("");
                            mFood.setComment("");
                            mFood.setIngredientPrices("");
                            mFood.setIngredients("");

                /*mFood.setSizeBasket(argFood.get(position).getSizeBasket());
                mFood.setSizeBasketId(argFood.get(position).getSizeBasketId());
                */
                            mFood.setBasketCount("1");

                            View view1 = LayoutInflater.from(context).inflate(R.layout.fragment_restaurant_order_items, null);
                            add_before_linear.setVisibility(View.GONE);
                            plus_minus_linear.setVisibility(View.VISIBLE);

                            System.out.println("FoodsAdapter : food_id : " + food.getId());

                            AppUtils.testHashMap.put(mFood.getId(), "1");
                            //----db update
                            db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
                            AppInstance.getInstance(context).addToSharedPref("min_price",AppUtils.MINIMUM_PRICE);
                            mFoodsAdapter.updateBottomViewSearchOne();

                        }

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(context.getString(R.string.msg_pending_order))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.deleteAll();
                                        AppUtils.testHashMap.clear();

                                   /* AppUtils.testHashMap.put(mFood.getId(), "1");
                                    db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);*/
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                            }
                                        }, 1500);
//-------------------------------------------------------------------------------------------------------------------
                                        Gson gson = new Gson();
                                        JSONArray mJsonArray_actual_price = null, mJsonArray_sales_price = null;
                                        if (food.prices.size() > 1) {
                                            //   Toast.makeText(,food.prices.size(),Toast.LENGTH_SHORT).show();
                                            System.out.println("ytcyvvbjkkkl");


                                            //  Gson gson1 = new Gson();
                                            String json_actual_price = gson.toJson(food.getActualPrice());
                                            String json_sales_price = gson.toJson(food.getPrices());

                                            try {
                                                mJsonArray_actual_price = new JSONArray(json_actual_price);
                                                mJsonArray_sales_price = new JSONArray(json_sales_price);
                                                System.out.println("add_before_linear : mJsonArray_actual_price : " + mJsonArray_actual_price.toString());

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            System.out.println("add_before_linear : json_actual_price : " + json_actual_price);
                                            System.out.println("add_before_linear : json_sales_price : " + json_sales_price);

                                            sizeDialog(mJsonArray_actual_price, mJsonArray_sales_price, food.getName(), position, foodsFilter, view);
                                        } else {

                                            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
                                            SharedPreferences.Editor mEditor = sharedpreferences.edit();

                                            mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL", AppUtils.CURRENT_RESTAURANT_NAME);
                                            mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL_AR", AppUtils.CURRENT_RESTAURANT_NAME_AR);
                                            mEditor.apply();

                                            String json_actual_price = gson.toJson(food.getActualPrice());
                                            String json_sales_price = gson.toJson(food.getPrices());
                                            try {
                                                //  mJsonArray_actual_price = new JSONArray(json_actual_price);
                                                mJsonArray_sales_price = new JSONArray(json_sales_price);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mBasePrice = Double.parseDouble(foodsFilter.get(position).getPrices().get(0).getPrice());


                                            System.out.println("dosdvosnvo : getId : " + foodsFilter.get(position).getId());
                                            System.out.println("dosdvosnvo : getName : " + foodsFilter.get(position).getName());
                                            System.out.println("dosdvosnvo : getMerchantId : " + foodsFilter.get(position).getMerchantId());
                                            try {
                                                System.out.println("dosdvosnvo : PLATE_PRICE : " + mJsonArray_sales_price.getJSONObject(0).getString("price"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                            //Set Food Id,Name,
                                            mFood.setId(foodsFilter.get(position).getId());
                                            mFood.setName(foodsFilter.get(position).getName());
                                            mFood.setMerchantId(foodsFilter.get(position).getMerchantId());
                                            mFood.setPriceBasket(AppUtils.monetize(String.valueOf(mBasePrice)));
                                            mFood.setUnitPrice(String.valueOf(mBasePrice / 1));

                                            try {
                                                mFood.setSizeBasketId(mJsonArray_sales_price.getJSONObject(0).getString("id"));
                                                mFood.setSizeBasket(mJsonArray_sales_price.getJSONObject(0).getString("type"));

                                                System.out.println("lsnkvn : " + mJsonArray_sales_price.getJSONObject(0).getString("id"));
                                                System.out.println("lsnkvn :" + mJsonArray_sales_price.getJSONObject(0).getString("type"));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            mFood.setAddOnIds("");
                                            mFood.setIngredientIds("");
                                            mFood.setAddOns("");
                                            mFood.setComment("");
                                            mFood.setIngredientPrices("");
                                            mFood.setIngredients("");

                /*mFood.setSizeBasket(argFood.get(position).getSizeBasket());
                mFood.setSizeBasketId(argFood.get(position).getSizeBasketId());
                */
                                            mFood.setBasketCount("1");

                                            View view1 = LayoutInflater.from(context).inflate(R.layout.fragment_restaurant_order_items, null);
                                            //dbUpdateView(view1);
                                            add_before_linear.setVisibility(View.GONE);
                                            plus_minus_linear.setVisibility(View.VISIBLE);

                                            System.out.println("FoodsAdapter : food_id : " + food.getId());
                                            AppUtils.testHashMap.put(mFood.getId(), "1");
                                            db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
                                            AppInstance.getInstance(context).addToSharedPref("min_price",AppUtils.MINIMUM_PRICE);
                                            mFoodsAdapter.updateBottomViewSearchOne();
                                            Snackbar.make(mFoodsAdapter.mSearchDialog.findViewById(R.id.linearSnackBar), context.getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();

                                        }


//-------------------------------------------------------------------------------------------------------------------


                                        //Snackbar.make(mRestaurantOrderItemsFragment.view.findViewById(R.id.page), context.getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();
                                        //updateBottomView();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });


          /*  view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    db = new DBHelper(context);

                    if (db.getBasketMerchantId().equals(foods.get(position).getMerchantId()) || db.getBasketMerchantId().length() == 0) {
                        Gson gson = new Gson();
                        JSONArray mJsonArray_actual_price = null, mJsonArray_sales_price = null;
                        if (food.prices.size() > 1) {
                            //   Toast.makeText(,food.prices.size(),Toast.LENGTH_SHORT).show();
                            System.out.println("ytcyvvbjkkkl");

                            //  Gson gson1 = new Gson();
                            String json_actual_price = gson.toJson(food.getActualPrice());
                            String json_sales_price = gson.toJson(food.getPrices());

                            try {
                                mJsonArray_actual_price = new JSONArray(json_actual_price);
                                mJsonArray_sales_price = new JSONArray(json_sales_price);
                                System.out.println("add_before_linear : mJsonArray_actual_price : " + mJsonArray_actual_price.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println("add_before_linear : json_actual_price : " + json_actual_price);
                            System.out.println("add_before_linear : json_sales_price : " + json_sales_price);

                            sizeDialog(mJsonArray_actual_price, mJsonArray_sales_price, food.getName(), position, foods, view);
                        } else {

                            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor mEditor = sharedpreferences.edit();

                            mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL", AppUtils.CURRENT_RESTAURANT_NAME);
                            mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL_AR", AppUtils.CURRENT_RESTAURANT_NAME_AR);
                            mEditor.apply();

                            String json_actual_price = gson.toJson(food.getActualPrice());
                            String json_sales_price = gson.toJson(food.getPrices());
                            try {
                                //  mJsonArray_actual_price = new JSONArray(json_actual_price);
                                mJsonArray_sales_price = new JSONArray(json_sales_price);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mBasePrice = Double.parseDouble(foods.get(position).getPrices().get(0).getPrice());


                            System.out.println("dosdvosnvo : getId : " + foods.get(position).getId());
                            System.out.println("dosdvosnvo : getName : " + foods.get(position).getName());
                            System.out.println("dosdvosnvo : getMerchantId : " + foods.get(position).getMerchantId());
                            try {
                                System.out.println("dosdvosnvo : PLATE_PRICE : " + mJsonArray_sales_price.getJSONObject(0).getString("price"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            //Set Food Id,Name,
                            mFood.setId(foods.get(position).getId());
                            mFood.setName(foods.get(position).getName());
                            mFood.setMerchantId(foods.get(position).getMerchantId());
                            mFood.setPriceBasket(AppUtils.monetize(String.valueOf(mBasePrice)));
                            mFood.setUnitPrice(String.valueOf(mBasePrice / 1));

                            try {
                                mFood.setSizeBasketId(mJsonArray_sales_price.getJSONObject(0).getString("id"));
                                mFood.setSizeBasket(mJsonArray_sales_price.getJSONObject(0).getString("type"));

                                System.out.println("lsnkvn : " + mJsonArray_sales_price.getJSONObject(0).getString("id"));
                                System.out.println("lsnkvn :" + mJsonArray_sales_price.getJSONObject(0).getString("type"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            mFood.setAddOnIds("");
                            mFood.setIngredientIds("");
                            mFood.setAddOns("");
                            mFood.setComment("");
                            mFood.setIngredientPrices("");
                            mFood.setIngredients("");

                *//*mFood.setSizeBasket(argFood.get(position).getSizeBasket());
                mFood.setSizeBasketId(argFood.get(position).getSizeBasketId());
                *//*
                            mFood.setBasketCount("1");

                            View view1 = LayoutInflater.from(context).inflate(R.layout.fragment_restaurant_order_items, null);
                            add_before_linear.setVisibility(View.GONE);
                            plus_minus_linear.setVisibility(View.VISIBLE);

                            System.out.println("FoodsAdapter : food_id : " + food.getId());

                            AppUtils.testHashMap.put(mFood.getId(), "1");
                            //----db update
                            db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
                            AppInstance.getInstance(context).addToSharedPref("min_price",AppUtils.MINIMUM_PRICE);
                            mFoodsAdapter.updateBottomViewSearchOne();

                        }

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(context.getString(R.string.msg_pending_order))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.deleteAll();
                                        AppUtils.testHashMap.clear();

                                   *//* AppUtils.testHashMap.put(mFood.getId(), "1");
                                    db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);*//*
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                            }
                                        }, 1500);
//-------------------------------------------------------------------------------------------------------------------
                                        Gson gson = new Gson();
                                        JSONArray mJsonArray_actual_price = null, mJsonArray_sales_price = null;
                                        if (food.prices.size() > 1) {
                                            //   Toast.makeText(,food.prices.size(),Toast.LENGTH_SHORT).show();
                                            System.out.println("ytcyvvbjkkkl");


                                            //  Gson gson1 = new Gson();
                                            String json_actual_price = gson.toJson(food.getActualPrice());
                                            String json_sales_price = gson.toJson(food.getPrices());

                                            try {
                                                mJsonArray_actual_price = new JSONArray(json_actual_price);
                                                mJsonArray_sales_price = new JSONArray(json_sales_price);
                                                System.out.println("add_before_linear : mJsonArray_actual_price : " + mJsonArray_actual_price.toString());

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            System.out.println("add_before_linear : json_actual_price : " + json_actual_price);
                                            System.out.println("add_before_linear : json_sales_price : " + json_sales_price);

                                            sizeDialog(mJsonArray_actual_price, mJsonArray_sales_price, food.getName(), position, foods, view);
                                        } else {

                                            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
                                            SharedPreferences.Editor mEditor = sharedpreferences.edit();

                                            mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL", AppUtils.CURRENT_RESTAURANT_NAME);
                                            mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL_AR", AppUtils.CURRENT_RESTAURANT_NAME_AR);
                                            mEditor.apply();

                                            String json_actual_price = gson.toJson(food.getActualPrice());
                                            String json_sales_price = gson.toJson(food.getPrices());
                                            try {
                                                //  mJsonArray_actual_price = new JSONArray(json_actual_price);
                                                mJsonArray_sales_price = new JSONArray(json_sales_price);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mBasePrice = Double.parseDouble(foods.get(position).getPrices().get(0).getPrice());


                                            System.out.println("dosdvosnvo : getId : " + foods.get(position).getId());
                                            System.out.println("dosdvosnvo : getName : " + foods.get(position).getName());
                                            System.out.println("dosdvosnvo : getMerchantId : " + foods.get(position).getMerchantId());
                                            try {
                                                System.out.println("dosdvosnvo : PLATE_PRICE : " + mJsonArray_sales_price.getJSONObject(0).getString("price"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                            //Set Food Id,Name,
                                            mFood.setId(foods.get(position).getId());
                                            mFood.setName(foods.get(position).getName());
                                            mFood.setMerchantId(foods.get(position).getMerchantId());
                                            mFood.setPriceBasket(AppUtils.monetize(String.valueOf(mBasePrice)));
                                            mFood.setUnitPrice(String.valueOf(mBasePrice / 1));

                                            try {
                                                mFood.setSizeBasketId(mJsonArray_sales_price.getJSONObject(0).getString("id"));
                                                mFood.setSizeBasket(mJsonArray_sales_price.getJSONObject(0).getString("type"));

                                                System.out.println("lsnkvn : " + mJsonArray_sales_price.getJSONObject(0).getString("id"));
                                                System.out.println("lsnkvn :" + mJsonArray_sales_price.getJSONObject(0).getString("type"));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            mFood.setAddOnIds("");
                                            mFood.setIngredientIds("");
                                            mFood.setAddOns("");
                                            mFood.setComment("");
                                            mFood.setIngredientPrices("");
                                            mFood.setIngredients("");

                *//*mFood.setSizeBasket(argFood.get(position).getSizeBasket());
                mFood.setSizeBasketId(argFood.get(position).getSizeBasketId());
                *//*
                                            mFood.setBasketCount("1");

                                            View view1 = LayoutInflater.from(context).inflate(R.layout.fragment_restaurant_order_items, null);
                                            //dbUpdateView(view1);
                                            add_before_linear.setVisibility(View.GONE);
                                            plus_minus_linear.setVisibility(View.VISIBLE);

                                            System.out.println("FoodsAdapter : food_id : " + food.getId());
                                            AppUtils.testHashMap.put(mFood.getId(), "1");
                                            db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
                                            AppInstance.getInstance(context).addToSharedPref("min_price",AppUtils.MINIMUM_PRICE);
                                            mFoodsAdapter.updateBottomViewSearchOne();
                                            Snackbar.make(mFoodsAdapter.mSearchDialog.findViewById(R.id.linearSnackBar), context.getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();

                                        }


//-------------------------------------------------------------------------------------------------------------------


                                        //Snackbar.make(mRestaurantOrderItemsFragment.view.findViewById(R.id.page), context.getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();
                                        //updateBottomView();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
*/

            final TextView quantity = view.findViewById(R.id.quantity);

            view.findViewById(R.id.plus).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    db = new DBHelper(context);
                    int current_quantity = Integer.parseInt(quantity.getText().toString());
                    current_quantity = current_quantity + 1;
                    quantity.setText("" + current_quantity);

                    if (foodsFilter.get(position).getActualPrice().size() > 1) {
                        String price = AppUtils.monetize(String.valueOf((Double.parseDouble(AppUtils.PLATE_PRICE)) * current_quantity));
                        System.out.println("priceplus : " + price);
                        food.setPriceBasket(price);

                    } else {
                        String price = AppUtils.monetize(String.valueOf(Double.parseDouble(foodsFilter.get(position).getPrices().get(0).getPrice()) * current_quantity));
                        System.out.println("priceplus : " + price);

                        food.setPriceBasket(price);
                    }
                    //mSubTotalPrice += (Double.parseDouble(price) - Double.parseDouble(food.getPriceBasket()));
//                        tvSubTotal.setText(getString(R.string.currency) + AppUtils.monetize(String.valueOf(mTotalPrice)));
                    // tvItemPrice.setText(getString(R.string.currency) + AppUtils.changeToArabic(price, getApplicationContext()));

                    db.updateFoodBasket(food.getPriceBasket(), current_quantity, foodsFilter.get(position).getId());
                    AppUtils.testHashMap.put(food.getId(), String.valueOf(current_quantity));

                    mFoodsAdapter.updateBottomViewSearchOne();

                }
            });

            view.findViewById(R.id.minus).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // AppUtils.testHashMap.remove(food.getId());
                     db=new DBHelper(context);
                    int current_quantity = Integer.parseInt(quantity.getText().toString());
                    if (current_quantity == 1) {
                        add_before_linear.setVisibility(View.VISIBLE);
                        plus_minus_linear.setVisibility(View.GONE);
                        System.out.println("AppUtils.testHashMap : size before : " + AppUtils.testHashMap.size());
                        AppUtils.testHashMap.remove(food.getId());
                        System.out.println("AppUtils.testHashMap : size after : " + AppUtils.testHashMap.size());
                        db.removeFoodBasket(foodsFilter.get(position).getId());
                        mFoodsAdapter.updateBottomViewSearchOne();
                    } else {
                        current_quantity = current_quantity - 1;
                        if (foodsFilter.get(position).getActualPrice().size() > 1) {
                            String price = AppUtils.monetize(String.valueOf((Double.parseDouble(AppUtils.PLATE_PRICE)) * current_quantity));
                            System.out.println("priceplus : " + price);
                            food.setPriceBasket(price);

                        } else {
                            String price = AppUtils.monetize(String.valueOf(Double.parseDouble(foodsFilter.get(position).getPrices().get(0).getPrice()) * current_quantity));
                            System.out.println("priceplus : " + price);

                            food.setPriceBasket(price);
                        }


                        quantity.setText("" + current_quantity);
                        db.updateFoodBasket(food.getPriceBasket(), current_quantity, foodsFilter.get(position).getId());
                        mFoodsAdapter.updateBottomViewSearchOne();
                        AppUtils.testHashMap.put(food.getId(), String.valueOf(current_quantity));
                    }


                }
            });


            if (AppUtils.IS_SUPER_MARKET) {
                add_before_linear.setVisibility(View.VISIBLE);
                view.findViewById(R.id.share_linear).setVisibility(View.GONE);
                if (AppUtils.testHashMap.size() > 0) {
                    if (AppUtils.testHashMap.get(food.getId()) != null) {


                        add_before_linear.setVisibility(View.GONE);
                        plus_minus_linear.setVisibility(View.VISIBLE);
                        quantity.setText("" + AppUtils.testHashMap.get(food.getId()));
                    }

                }
            } else {
                add_before_linear.setVisibility(View.GONE);
                plus_minus_linear.setVisibility(View.GONE);
                view.findViewById(R.id.share_linear).setVisibility(View.VISIBLE);
            }
            return view;
        } catch (IndexOutOfBoundsException Ex) {
            //Toast.makeText(context, "Please try again!", Toast.LENGTH_SHORT).show();
        }
        return  LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fooditem, parent, false);

    }

    public void setOnRestaurantPhotoClicked(OnRestaurantPhotoClicked listener) {
        mListener = listener;
    }

    public void sizeDialog(final JSONArray mJsonArray_actual_price, final JSONArray mJsonArray_sales_price, String dishName, final int position, final List<FoodCopy> argFood, final View argView) {

        final Dialog mSizeDialog = new Dialog(context, android.R.style.Theme_Light_NoTitleBar);
        mSizeDialog.setContentView(R.layout.select_size_dialog);
        TextView mTextViewTitle = mSizeDialog.findViewById(R.id.dishName);
        mTextViewTitle.setText(dishName);
        RecyclerView size_dialog_recyclerview = mSizeDialog.findViewById(R.id.size_dialog_recyclerview);
        size_dialog_recyclerview.setLayoutManager(new LinearLayoutManager(context));

        mSizeDialog.findViewById(R.id.backCancelDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSizeDialog.dismiss();

            }
        });

        Button btnAddToBasket = mSizeDialog.findViewById(R.id.btnAddToBasket);
        btnAddToBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppUtils.PLATE_TYPE.equalsIgnoreCase("") || AppUtils.PLATE_TYPE.isEmpty()) {
                    Toast.makeText(context, "Please select size first.", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor mEditor = sharedpreferences.edit();

                    mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL", AppUtils.CURRENT_RESTAURANT_NAME);
                    mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL_AR", AppUtils.CURRENT_RESTAURANT_NAME_AR);
                    mEditor.apply();

                    mBasePrice = Double.parseDouble(AppUtils.PLATE_PRICE);


                    System.out.println("dosdvosnvo : getId : " + argFood.get(position).getId());
                    System.out.println("dosdvosnvo : getName : " + argFood.get(position).getName());
                    System.out.println("dosdvosnvo : getMerchantId : " + argFood.get(position).getMerchantId());
                    System.out.println("dosdvosnvo : PLATE_PRICE : " + AppUtils.PLATE_PRICE);


                    //Set Food Id,Name,
                    mFood.setId(argFood.get(position).getId());
                    mFood.setName(argFood.get(position).getName());
                    mFood.setMerchantId(argFood.get(position).getMerchantId());
                    mFood.setPriceBasket(AppUtils.monetize(String.valueOf(mBasePrice)));
                    mFood.setUnitPrice(String.valueOf(mBasePrice / 1));

                    mFood.setSizeBasketId(AppUtils.PLATE_ID);
                    mFood.setSizeBasket(AppUtils.PLATE_TYPE);
                    mFood.setAddOnIds("");
                    mFood.setIngredientIds("");
                    mFood.setAddOns("");
                    mFood.setComment("");
                    mFood.setIngredientPrices("");
                    mFood.setIngredients("");

                    System.out.println("lsnkvn :" + argFood.get(position).getSizeBasket());
                    System.out.println("lsnkvn :" + argFood.get(position).getSizeBasketId());

                /*mFood.setSizeBasket(argFood.get(position).getSizeBasket());
                mFood.setSizeBasketId(argFood.get(position).getSizeBasketId());
                */
                    mFood.setBasketCount("1");

                    argView.findViewById(R.id.add_before_linear).setVisibility(View.GONE);
                    argView.findViewById(R.id.plus_minus_linear).setVisibility(View.VISIBLE);

                    System.out.println("FoodsAdapter : food_id : " + mFood.getId());

                    AppUtils.testHashMap.put(mFood.getId(), "1");

//                    dbUpdate(mSizeDialog);


                    db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
                    mFoodsAdapter.updateBottomViewSearchOne();
                    AppInstance.getInstance(context).addToSharedPref("min_price",AppUtils.MINIMUM_PRICE);
                    Snackbar.make(mSizeDialog.findViewById(R.id.linearSnackBar), context.getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();
                    mSizeDialog.dismiss();
                }
            }

        });

        //ADAPTER
        SelectSizeDialogAdapter adapter = new SelectSizeDialogAdapter(context, mJsonArray_actual_price, mJsonArray_sales_price);
        size_dialog_recyclerview.setAdapter(adapter);

        AppUtils.selectedSize = -1;
        mSizeDialog.show();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        System.out.println("bhjguygbuguigbu");
    }

    /*public void onAddToBasketClicked(View view) {

     *//*   AppUtils.CURRENT_RESTAURANT_NAME_FINAL=AppUtils.CURRENT_RESTAURANT_NAME;
        AppUtils.CURRENT_RESTAURANT_NAME_FINAL_AR=AppUtils.CURRENT_RESTAURANT_NAME_AR;*//*

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor mEditor = sharedpreferences.edit();

        mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL", AppUtils.CURRENT_RESTAURANT_NAME);
        mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL_AR", AppUtils.CURRENT_RESTAURANT_NAME_AR);
        mEditor.apply();

        // mFood.setPriceBasket(AppUtils.monetize(String.valueOf(mBasePrice + mAddOnPrice + mIngredientPrice)));
     *//*   mFood.setBasketCount(String.valueOf(mQty));
        for (FoodAddBasketActivity.Plate plate : plates) {
            if (plate.selected) {
                mFood.setSizeBasket(plate.size);
                mFood.setSizeBasketId(plate.id);
            }
        }*//*
*//*        String strAddOns = "";
        String strAddOnIds = "";
        String strAddOnPrices = "";
        for (FoodAddBasketActivity.Addon addon : addons) {
            if (addon.checkBox.isChecked()) {
                if (strAddOns.length() > 0) {
                    strAddOns += ";;";
                    strAddOnIds += ";;";
                    strAddOnPrices += ";;";
                }
                strAddOns += addon.name;
                strAddOnIds += addon.id;
                strAddOnPrices += addon.price;
            }
        }
        mFood.setAddOns(strAddOns);
        mFood.setAddOnIds(strAddOnIds);
        mFood.setAddOnPrices(strAddOnPrices);*//*

*//*        String strIngredients = "";
        String strIngredientIds = "";
        String strIngredientPrices = "";
        for (FoodAddBasketActivity.Ingredient ingredient : ingredients) {
            if (ingredient.checkBox.isChecked()) {
                if (strIngredients.length() > 0) {
                    strIngredients += ";;";
                    strIngredientIds += ";;";
                    strIngredientPrices += ";;";
                }
                strIngredients += ingredient.name;
                strIngredientIds += ingredient.id;
                strIngredientPrices += ingredient.price;
            }
        }
        mFood.setIngredients(strIngredients);
        mFood.setIngredientIds(strIngredientIds);
        mFood.setIngredientPrices(strIngredientPrices);*//*


        final DBHelper db = new DBHelper(context);
        if (db.getBasketMerchantId().equals(mFood.getMerchantId()) || db.getBasketMerchantId().length() == 0) {
            db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
          *//*  new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onBackPressed();
                }
            }, 1500);
            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();*//*
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getString(R.string.msg_pending_order))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteAll();
                            db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
                           *//* new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onBackPressed();
                                }
                            }, 1500);
                            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();*//*
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }*/


    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                try {
                    String charString = charSequence.toString();

                    System.out.println("CHECKCJCJDJDDJ : " + charString);
                    if (charString.isEmpty()) {
                        foodsFilter = foods;

                    } else {
                        ArrayList<FoodCopy> filteredList = new ArrayList<>();

                        for (FoodCopy row : foods) {
                            System.out.println("CHECKCJCJDJDDJ : FoodsAdapterCopy :  Name : " + row.getName());
                            System.out.println("CHECKCJCJDJDDJ : FoodsAdapterCopy :  Name Ar: " + row.getName_ar());
                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG).equalsIgnoreCase("en")) {
                                System.out.println("CHECKCJCJDJDDJ : english");
                            if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getName().contains(charSequence)) {
                                filteredList.add(row);
                                System.out.println("CHECKCJCJDJDDJ : arabic : matched"+charString);
                            }
                            }
                            else {
                                System.out.println("CHECKCJCJDJDDJ : arabic : "+charString);
                                if (row.getName_ar().toLowerCase().contains(charString.toLowerCase()) || row.getName_ar().contains(charSequence)) {
                                    filteredList.add(row);
                                    System.out.println("CHECKCJCJDJDDJ : arabic : matched "+charString);
                                }
                            }

                        }

                        System.out.println("CHECKCJCJDJDDJ : sdasdasd");
                        foodsFilter = filteredList;
                        Gson skn = new Gson();
                        System.out.println("CHECKCJCJDJDDJddd : Gson :" + skn.toJson(foodsFilter));
                    }


                } catch (Exception e) {
                    System.out.println("ERRRRRRR : " + e.getMessage());
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = foodsFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                try {

                    foodsFilter = (ArrayList<FoodCopy>) filterResults.values;
                    Gson mGson = new Gson();
                    System.out.println("Search  : FoodsAdapterCopy : publishResults : " + mGson.toJson(foodsFilter));

                    //mFoodsAdapter.updateSearchRecyclerView(mDemoSearchModelsMainFilter);
                    notifyDataSetChanged();


                } catch (Exception e) {
                    System.out.println("ERRRRRRR : " + e.getMessage());

                }
            }
        };
    }

}
