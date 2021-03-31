package com.afieat.ini.adapters;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;

import com.afieat.ini.RestaurantsDetailActivity;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.BasketActivity2;
import com.afieat.ini.DetailActivityClick_Page;
import com.afieat.ini.FoodAddBasketActivity;
import com.afieat.ini.R;
import com.afieat.ini.database.DBHelper;
import com.afieat.ini.fragments.RestaurantOrderItemsFragment;
import com.afieat.ini.fragments.RestaurantPopularProductsFragment;
import com.afieat.ini.interfaces.OnBottomReachedListener;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by amartya on 13/04/16 with love.
 */
public class FoodsAdapter extends BaseAdapter {

    int lastPosition = -1;
    private List<Food> foods;
    private OnRestaurantPhotoClicked mListener;
    private Context context;
    private ProgressDialog progressDialog;
    private  ListView mListView;
    private String[] hintText;
    private ListView size_dialog_recyclerview;
    private String platePrice;

    private Food mFood;
    private double mBasePrice;
    private FoodsAdapterCopy mFoodsAdapterCopy;
    private  DBHelper db;
    protected Dialog mSearchDialog;
    private List<String> sales_price_list = new ArrayList<>(),
            actual_price_list = new ArrayList<>(),
            size_type_list = new ArrayList<>();
    private RestaurantOrderItemsFragment mRestaurantOrderItemsFragment;
    private RestaurantPopularProductsFragment mRestaurantPopularProductsFragment;
    OnBottomReachedListener onBottomReachedListener;
    private boolean done = false;
    private long DELAY;
    private Timer timer;


    public FoodsAdapter(List<Food> foods,
                        Context context,
                        RestaurantOrderItemsFragment mRestaurantOrderItemsFragment,
                        RestaurantPopularProductsFragment mRestaurantPopularProductsFragment) {
        this.foods = foods;
        this.context = context;
        if (mRestaurantOrderItemsFragment != null) {
            this.mRestaurantOrderItemsFragment = mRestaurantOrderItemsFragment;
        } else {
            this.mRestaurantPopularProductsFragment = mRestaurantPopularProductsFragment;
        }

        this.mListView = mListView;
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.msg_please_wait));
        mFood = new Food();
        Gson gson = new Gson();
        String json = gson.toJson(foods);
        System.out.println("sdmlsmv : " + json);
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
        return foods.size();
    }

    @Override
    public Object getItem(int position) {
        return foods.get(position);
    }


    @Override
    public long getItemId(int position) {
        return Long.parseLong(foods.get(position).getId());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        System.out.println("wedofhweofniosbnvo");

        if (position == foods.size() - 1 && !done) {

            if (position == 1) {
                done = true;
            }
            mRestaurantOrderItemsFragment.onBottomReachedR(position);


        }


        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fooditem, parent, false);
        LinearLayout layHeader=(LinearLayout)view.findViewById(R.id.layHeader);
        TextView txtHeader=(TextView)view.findViewById(R.id.txtHeader);
       /* CardView cvFood=(CardView)view.findViewById(R.id.cvFood);
        cvFood.setSelected(false);*/
        if(foods.get(position).getHeaderTxt()!=null && !foods.get(position).getHeaderTxt().equalsIgnoreCase("") &&  foods.get(position).isFirstPos()){
            layHeader.setVisibility(View.VISIBLE);
            txtHeader.setText(foods.get(position).getHeaderTxt());

        }
        else{
            layHeader.setVisibility(View.GONE);
            txtHeader.setText("");

            // mRestaurantOrderItemsFragment.txtFirstHeader.setText(foods.get(position).getHeaderTxt());
        }

        /*if(foods.get(position).getHeaderTxt()!=null && foods.get(position).getHeaderTxt().equalsIgnoreCase("") &&  !foods.get(position).isFirstPos()){
            layHeader.setVisibility(View.GONE);
            txtHeader.setText("");
        }
        else{
            layHeader.setVisibility(View.VISIBLE);
            txtHeader.setText(foods.get(position).getHeaderTxt());

           // mRestaurantOrderItemsFragment.txtFirstHeader.setText(foods.get(position).getHeaderTxt());
        }*/
        if(position==0){
            layHeader.setVisibility(View.GONE);
            txtHeader.setText("");
        }


        if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            view.findViewById(R.id.Add).setBackground(context.getResources().getDrawable(R.drawable.plus_add_bg_ar));
            view.findViewById(R.id.plus).setBackground(context.getResources().getDrawable(R.drawable.plus_bg_ar));
            view.findViewById(R.id.minus).setBackground(context.getResources().getDrawable(R.drawable.minus_bg_ar));

            view.findViewById(R.id.Add).setPadding(25, 15, 25, 15);
            view.findViewById(R.id.plus).setPadding(28, 15, 28, 15);
            view.findViewById(R.id.minus).setPadding(30, 15, 30, 15);


        }

        final Food food = foods.get(position);
        System.out.println("nsdvns : itemNAme : " + food.getName());
        final String logoPath = Apis.IMG_PATH + "items/image/thumb_68_68/" + food.getUriPic();
        Glide.with(context)
                .load(Uri.parse(logoPath))
                .placeholder(R.drawable.placeholder_land)
                .into((ImageView) view.findViewById(R.id.ivFoodLogo));


        ((TextView) view.findViewById(R.id.tvItemTitle)).setText(food.getName());
        if (food.getDiscount().length() > 0)
            ((TextView) view.findViewById(R.id.tvDiscount)).setText("IQD " + food.getDiscount() + " off");
        LinearLayout llPrices = (LinearLayout) view.findViewById(R.id.llPrices);
        int i = 0;


        for (Food.Price priceObject : food.prices) {

            System.out.println("actual : food.actualPrice.get(i).getPrice() :" + food.actualPrice.get(i).getPrice());
            View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_food_plate_price, parent, false);

            if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG)))
            {
                ((TextView) view1.findViewById(R.id.tvPrice)).setText(parent.getContext().getString(R.string.currency) + AppUtils.changeToArabic(priceObject.getPrice(), parent.getContext()));

            }else {
                ((TextView) view1.findViewById(R.id.tvPrice)).setText(parent.getContext().getString(R.string.currency) + priceObject.getPrice());

            }
            ((TextView) view1.findViewById(R.id.tvPlate)).setText("(" + priceObject.getType() + ")");

            double salesPrice = 0, actualPrice = 0;
            if (priceObject.getPrice() != null || !"".equals(priceObject.getPrice().trim())) {
                salesPrice = Double.parseDouble(priceObject.getPrice());
            }
            if (food.actualPrice.get(i).getPrice() != null || !"".equals(food.actualPrice.get(i).getPrice())) {
                actualPrice = Double.parseDouble(food.actualPrice.get(i).getPrice());
            }

            System.out.println("FoodsAdapter4 : salesPrice : " + salesPrice);
            System.out.println("FoodsAdapter4 : actualPrice : " + actualPrice);
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

        view.findViewById(R.id.LLShre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    AppUtils.log("Force request location permission");
                    ActivityCompat.requestPermissions((DetailActivityClick_Page) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else
                    try {
                        new AsyncTask<String, Void, Bitmap>() {
                            @Override
                            protected Bitmap doInBackground(String... voids) {
                                try {
                                    return Glide.
                                            with(context).
                                            load(voids[0]).
                                            asBitmap().
                                            into(376, 312). // Width and height
                                            get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                } catch (RuntimeException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                progressDialog.show();
                            }

                            @Override
                            protected void onPostExecute(Bitmap aVoid) {
                                super.onPostExecute(aVoid);
                                progressDialog.dismiss();
                                try {
                                    String pathofBmp = MediaStore.Images.Media.insertImage(context.getContentResolver(), aVoid, "title", null);
                                    Uri bmpUri = Uri.parse(pathofBmp);
                                    final Intent emailIntent1 = new Intent(android.content.Intent.ACTION_SEND);
                                    emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                    emailIntent1.putExtra(Intent.EXTRA_TEXT, food.getName() + "\n\n" + ((DetailActivityClick_Page) context).mRestaurantName + "\n" + food.getRestaurant_url());
                                    emailIntent1.setType("text/plain");
                                    context.startActivity(Intent.createChooser(emailIntent1, context.getString(R.string.share)));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.execute(Apis.IMG_PATH + "items/image/thumb_376_312/" + food.getUriPic());


                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                sharingIntent.setType("image/jpeg");
//                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(logoPath));
//                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, food.getName());
//                context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share)));
            }
        });


        if (position > lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
            view.startAnimation(animation);
            lastPosition = position;
        }

        ViewCompat.setTransitionName((ImageView) view.findViewById(R.id.ivFoodLogo), food.getName());

        view.findViewById(R.id.ivFoodLogo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = "";
                System.out.println("ivFoodLogo : " + logoPath);
                if (logoPath.contains("68_68")) path = logoPath.replace("68_68", "376_312");
                mListener.onPhotoClicked(Uri.parse(path));
            }
        });
        final LinearLayout add_before_linear = view.findViewById(R.id.add_before_linear);
        final LinearLayout plus_minus_linear = view.findViewById(R.id.plus_minus_linear);
        LinearLayout layFoodname=view.findViewById(R.id.layFoodname);
        final TextView quantity = view.findViewById(R.id.quantity);
        if (AppUtils.IS_SUPER_MARKET) {
            layFoodname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, "Hello--"+position, Toast.LENGTH_SHORT).show();

                    db = new DBHelper(context);


                    if (db.getBasketMerchantId().equals(foods.get(position).getMerchantId()) || db.getBasketMerchantId().length() == 0) {
                        Log.e("MERCHANT_ID==>",""+db.getBasketMerchantId())  ;
                        Log.e("MERCHANT_ID==>",""+foods.get(position).getMerchantId())  ;
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

                            sizeDialogNew(mJsonArray_actual_price, mJsonArray_sales_price, food.getName(), position, foods, view,quantity);
                        }
                    }
                }
            });
        }


        add_before_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  System.out.println("position : " + position);
                System.out.println("position : id : " + food.getId());
                System.out.println("cosndocns : " + food.prices.size());
                System.out.println("jhvjhvjv : getBasketMerchantId : " + db.getBasketMerchantId());
                System.out.println("jhvjhvjv : getMerchantId : " + foods.get(position).getMerchantId());
           */

                db = new DBHelper(context);

                if (db.getBasketMerchantId().equals(foods.get(position).getMerchantId()) || db.getBasketMerchantId().length() == 0) {
                    Log.e("MERCHANT_ID==>",""+db.getBasketMerchantId())  ;
                    Log.e("MERCHANT_ID==>",""+foods.get(position).getMerchantId())  ;
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
                       /* try {
                            //System.out.println("dosdvosnvo : PLATE_PRICE : " + mJsonArray_sales_price.getJSONObject(0).getString("price"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/


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
                        updateBottomView();

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
                                      /*  try {
                                            System.out.println("dosdvosnvo : PLATE_PRICE : " + mJsonArray_sales_price.getJSONObject(0).getString("price"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
*/

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
                                        updateBottomView();
                                        Snackbar.make(mRestaurantOrderItemsFragment.view.findViewById(R.id.page), context.getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();

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




        view.findViewById(R.id.plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db = new DBHelper(context);
                int current_quantity = Integer.parseInt(quantity.getText().toString());
                current_quantity = current_quantity + 1;
                quantity.setText("" + current_quantity);

                if (foods.get(position).getActualPrice().size() > 1) {
                    if(!AppUtils.PLATE_PRICE.isEmpty())
                    {
                        double basePrise=0;
                        List<Food> fd = db.getFoodsBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID));
                        for(int i=0;i<fd.size();i++){
                            if(fd.get(i).getId().equals(foods.get(position).getId())){
                                basePrise= Double.parseDouble(fd.get(i).getUnitPrice());
                                break;
                            }
                        }
                        // String price = AppUtils.monetize(String.valueOf((Double.parseDouble(AppUtils.PLATE_PRICE)) * current_quantity));
                        String price=AppUtils.monetize(String.valueOf(basePrise*current_quantity));
                        System.out.println("priceplus : " + price);
                        food.setPriceBasket(price);
                    }


                } else {
                    String price = AppUtils.monetize(String.valueOf(Double.parseDouble(foods.get(position).getPrices().get(0).getPrice()) * current_quantity));
                    System.out.println("priceplus : " + price);

                    food.setPriceBasket(price);
                }
                //mSubTotalPrice += (Double.parseDouble(price) - Double.parseDouble(food.getPriceBasket()));
//                        tvSubTotal.setText(getString(R.string.currency) + AppUtils.monetize(String.valueOf(mTotalPrice)));
                // tvItemPrice.setText(getString(R.string.currency) + AppUtils.changeToArabic(price, getApplicationContext()));

                db.updateFoodBasket(food.getPriceBasket(), current_quantity, foods.get(position).getId());
                AppUtils.testHashMap.put(food.getId(), String.valueOf(current_quantity));

                updateBottomView();


            }
        });

        view.findViewById(R.id.minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = new DBHelper(context);
                // AppUtils.testHashMap.remove(food.getId());

                int current_quantity = Integer.parseInt(quantity.getText().toString());
                if (current_quantity == 1) {
                    add_before_linear.setVisibility(View.VISIBLE);
                    plus_minus_linear.setVisibility(View.GONE);
                    System.out.println("AppUtils.testHashMap : size before : " + AppUtils.testHashMap.size());
                    AppUtils.testHashMap.remove(food.getId());
                    System.out.println("AppUtils.testHashMap : size after : " + AppUtils.testHashMap.size());
                    try {

                        db.removeFoodBasket(foods.get(position).getId());
                    }catch (Exception w)
                    {
                        System.out.println("Error : try catch : "+w);
                    }
                    updateBottomView();
                }
                else {
                    current_quantity = current_quantity - 1;

                    if (foods.get(position).getActualPrice().size() > 1) {
                        double basePrise=0;
                        List<Food> fd = db.getFoodsBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID));
                        for(int i=0;i<fd.size();i++){
                            if(fd.get(i).getId().equals(foods.get(position).getId())){
                                basePrise= Double.parseDouble(fd.get(i).getUnitPrice());
                                break;
                            }
                        }

                        String price = AppUtils.monetize(String.valueOf(basePrise * current_quantity));
                        System.out.println("priceplus : " + price);
                        food.setPriceBasket(price);

                    } else {
                        String price = AppUtils.monetize(String.valueOf(Double.parseDouble(foods.get(position).getPrices().get(0).getPrice()) * current_quantity));
                        System.out.println("priceplus : " + price);

                        food.setPriceBasket(price);
                    }


                    quantity.setText("" + current_quantity);
                    try {
                        db.updateFoodBasket(food.getPriceBasket(), current_quantity, foods.get(position).getId());
                    }catch (NullPointerException e)
                    {

                    }
                    updateBottomView();
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

/*mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {



            if (AppUtils.testHashMap.size() > 0) {
                if (!AppUtils.testHashMap.containsKey(food.getId())) {


                    add_before_linear.setVisibility(View.VISIBLE);
                    plus_minus_linear.setVisibility(View.GONE);

                }

            }


        //Check if the last view is visible
        if (++firstVisibleItem + visibleItemCount > totalItemCount) {
            //load more content
            System.out.println("listview njjjkj");
        }
    }
});*/


//        ((TextView) view.findViewById(R.id.tvItemPrice1)).setText(foods.get(position).getName());
        return view;
    }

    public void setOnRestaurantPhotoClicked(OnRestaurantPhotoClicked listener) {
        mListener = listener;
    }

    public void sizeDialog(final JSONArray mJsonArray_actual_price, final JSONArray mJsonArray_sales_price, String dishName, final int position, final List<Food> argFood, final View argView) {

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
                    Toast.makeText(context, context.getString(R.string.please_select_size_first), Toast.LENGTH_SHORT).show();
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
                    AppInstance.getInstance(context).addToSharedPref("min_price",AppUtils.MINIMUM_PRICE);
                    updateBottomView();

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


    public void updateBottomView() {

        View bottomView = mRestaurantOrderItemsFragment.view;
        System.out.println("RestaurantOrderItemsFragment : onResume : ");
        DBHelper db = new DBHelper(context);
        int itemCount = db.getFoodsBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID)).size();
        System.out.println("RestaurantOrderItemsFragment : itemCount : " + itemCount);
        if (itemCount == 0) {
            bottomView.findViewById(R.id.bottomCartView).setVisibility(View.GONE);


        } else {
            bottomView.findViewById(R.id.bottomCartView).setVisibility(View.VISIBLE);

            final TextView cartCount = bottomView.findViewById(R.id.cartCount);
            TextView totalPrice = bottomView.findViewById(R.id.totalPrice);

            if (AppInstance.getInstance(context.getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("en")) {
                cartCount.setText(itemCount + " Items In Cart");
            } else {
                cartCount.setText(itemCount + "العناصر في السلة ");
            }


            final List<Food> foods = db.getFoodsBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID));
            double subTotal = 0;
            for (Food food : foods) {
                subTotal += Double.parseDouble(food.getPriceBasket());
            }
            totalPrice.setText(context.getString(R.string.currency) + String.valueOf(subTotal));
            TextView viewCart = bottomView.findViewById(R.id.viewCart);
            viewCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BasketActivity2.class);
                    // AppUtils.MINIMUM_PRICE = minPrice;

                    intent.putExtra("MinPrice", AppUtils.MINIMUM_PRICE);
                    context.startActivity(intent);
                }
            });
        }

    }


    public void updateBottomViewSearchOne() {


        System.out.println("RestaurantOrderItemsFragment : onResume : ");
        DBHelper db = new DBHelper(context);
        int itemCount = db.getFoodsBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID)).size();
        System.out.println("RestaurantOrderItemsFragment : itemCount : " + itemCount);
        if (itemCount == 0) {
            mSearchDialog.findViewById(R.id.bottomCartView).setVisibility(View.GONE);


        } else {
            mSearchDialog.findViewById(R.id.bottomCartView).setVisibility(View.VISIBLE);

            final TextView cartCount = mSearchDialog.findViewById(R.id.cartCount);
            TextView totalPrice = mSearchDialog.findViewById(R.id.totalPrice);

            if (AppInstance.getInstance(context.getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("en")) {
                cartCount.setText(itemCount + " Items In Cart");
            } else {
                cartCount.setText(itemCount + "العناصر في السلة ");
            }


            final List<Food> foods = db.getFoodsBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID));
            double subTotal = 0;
            for (Food food : foods) {
                subTotal += Double.parseDouble(food.getPriceBasket());
            }
            totalPrice.setText(context.getString(R.string.currency) + String.valueOf(subTotal));
            TextView viewCart = mSearchDialog.findViewById(R.id.viewCart);
            viewCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BasketActivity2.class);
                    // AppUtils.MINIMUM_PRICE = minPrice;

                    intent.putExtra("MinPrice",AppInstance.getInstance(context).getFromSharedPref("min_price"));
                    context.startActivity(intent);
                }
            });
        }

    }

    public void searchDialog(ArrayList<FoodCopy> argDemoSearchModelsMain) {
        mSearchDialog = new Dialog(context, R.style.SearchDiaogTheme);
        mSearchDialog.setContentView(R.layout.search_dialog);
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mSearchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                System.out.println("vklsmvfmsvfml : called ");
                mRestaurantOrderItemsFragment.updateAdapter();
                // notifyDataSetChanged();
            }
        });
        mSearchDialog.findViewById(R.id.backCancelDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchDialog.dismiss();

            }
        });

        mSearchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                imm.hideSoftInputFromWindow(size_dialog_recyclerview.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        Gson m = new Gson();

        System.out.println("searchDialog : " + m.toJson(argDemoSearchModelsMain));

        size_dialog_recyclerview = mSearchDialog.findViewById(R.id.size_dialog_recyclerview);
        ViewCompat.setNestedScrollingEnabled(size_dialog_recyclerview, true);
//        mFoodsAdapterCopy = new FoodsAdapterCopy(AppUtils.DEMO_FOOD_COPY, context,this);
//        size_dialog_recyclerview.setAdapter(mFoodsAdapterCopy);


        size_dialog_recyclerview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!AppUtils.IS_SUPER_MARKET)
                {
                    System.out.println("Rahul : RestaurantOrderItemsFragment : onItemClick : String.valueOf(id) " + String.valueOf(id));
                    System.out.println("Rahul : RestaurantOrderItemsFragment : onItemClick : ((Food) parent.getAdapter().getItem(position)).getName() " + ((FoodCopy) parent.getAdapter().getItem(position)).getName());
                    Intent intent = new Intent(context, FoodAddBasketActivity.class);
                    intent.putExtra(AppUtils.EXTRA_FOOD_ID, String.valueOf(id));
                    intent.putExtra(AppUtils.EXTRA_FOOD_NAME, ((FoodCopy) parent.getAdapter().getItem(position)).getName());
                    context.startActivity(intent);
                }

            }
        });

//        SearchView et_search = mSearchDialog.findViewById(R.id.et_search);
//        et_search.setMaxWidth(Integer.MAX_VALUE);
//
//        et_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // filter recycler view when query submitted
//                System.out.println("Search dfsdfsdfsdf : " + query);
//                try {
//                    if (query.length() <= 0) {
//                        size_dialog_recyclerview.setVisibility(View.GONE);
//                    } else {
//                        size_dialog_recyclerview.setVisibility(View.VISIBLE);
//                        mFoodsAdapterCopy.getFilter().filter(query);
//                    }
//                } catch (Exception e) {
//                    System.out.println("ERRRRRRR : " + e.getMessage());
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//                // filter recycler view when text is changed
//                System.out.println("Search : " + query);
//                try {
//                    size_dialog_recyclerview.setVisibility(View.VISIBLE);
//                    mFoodsAdapterCopy.getFilter().filter(query);
///*
//                    if (query.length() <= 0) {
//                        size_dialog_recyclerview.setVisibility(View.GONE);
//                    } else {
//                        size_dialog_recyclerview.setVisibility(View.VISIBLE);
//                        mViewOnlineSearchResultAdapter.getFilter().filter(query);
//                    }*/
//                } catch (Exception e) {
//                    System.out.println("ERRRRRRR : " + e.getMessage());
//                }
//                return false;
//            }
//        });

        final AutoCompleteTextView  autoCompleteTextView1=mSearchDialog.findViewById(R.id.autoCompleteTextView1);
   /*     ArrayAdapter adapter;
        if (AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG).equalsIgnoreCase("ar")) {
             adapter = new
                    ArrayAdapter(context,android.R.layout.simple_list_item_1,AppUtils.HINT_TEXT_AR);
        }else
        {
             adapter = new
                    ArrayAdapter(context,android.R.layout.simple_list_item_1,AppUtils.HINT_TEXT);
        }


        autoCompleteTextView1.setAdapter(adapter);
        autoCompleteTextView1.setThreshold(1);*/

        autoCompleteTextView1.addTextChangedListener(new TextWatcher() {

            CountDownTimer timer = null;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {

                try {
                    if(charSequence.length()>=3) {

                        size_dialog_recyclerview.setVisibility(View.VISIBLE);

                        if (timer != null) {
                            timer.cancel();
                        }

                        timer = new CountDownTimer(1500, 1000) {

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {


                                mRestaurantOrderItemsFragment.getFoodItemsForSearchFromNW(charSequence.toString());
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (mFoodsAdapterCopy == null) {
                                            mFoodsAdapterCopy = new FoodsAdapterCopy(AppUtils.DEMO_FOOD_COPY, context, FoodsAdapter.this);
                                            size_dialog_recyclerview.setAdapter(mFoodsAdapterCopy);
                                        } else
                                            mFoodsAdapterCopy.notifyDataSetChanged();
                                    }
                                }, 1500);

                            }

                        }.start();

//                        mFoodsAdapterCopy.getFilter().filter(charSequence);

//                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(autoCompleteTextView1.getWindowToken(),0);

                    }
/*
                    if (query.length() <= 0) {
                        size_dialog_recyclerview.setVisibility(View.GONE);
                    } else {
                        size_dialog_recyclerview.setVisibility(View.VISIBLE);
                        mViewOnlineSearchResultAdapter.getFilter().filter(query);
                    }*/
                } catch (Exception e) {
                    System.out.println("ERRRRRRR : " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {





            }
        });


        mSearchDialog.show();


    }

//    long delay = 1000; // 1 seconds after user stops typing
//    long last_text_edit = 0;
//    Handler handler = new Handler();
//
//    private Runnable input_finish_checker = new Runnable() {
//        public void run() {
//            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
//                // TODO: do what you need here
//                // ............
//                // ............
//                mRestaurantOrderItemsFragment.getFoodItemsForSearchFromNW(editable.toString());
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        if (mFoodsAdapterCopy==null) {
//                            mFoodsAdapterCopy = new FoodsAdapterCopy(AppUtils.DEMO_FOOD_COPY, context, FoodsAdapter.this);
//                            size_dialog_recyclerview.setAdapter(mFoodsAdapterCopy);
//                        }
//                        else
//                            mFoodsAdapterCopy.notifyDataSetChanged();
//                    }
//                },1500);
//            }
//        }
//    };
/*
    public void updateSearchRecyclerView(ArrayList<DemoSearchModel> argDemoSearchModelsMain) {
        size_dialog_recyclerview = mSearchDialog.findViewById(R.id.size_dialog_recyclerview);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(context);
        size_dialog_recyclerview.setLayoutManager(mLinearLayoutManager);
        mViewOnlineSearchResultAdapter = new ViewOnlineSearchResultAdapter(context, argDemoSearchModelsMain, this);
        size_dialog_recyclerview.setAdapter(mViewOnlineSearchResultAdapter);
        notifyDataSetChanged();
    }*/

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        System.out.println("bhjguygbuguigbu");
    }

    public void onAddToBasketClicked(View view) {

     /*   AppUtils.CURRENT_RESTAURANT_NAME_FINAL=AppUtils.CURRENT_RESTAURANT_NAME;
        AppUtils.CURRENT_RESTAURANT_NAME_FINAL_AR=AppUtils.CURRENT_RESTAURANT_NAME_AR;*/

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor mEditor = sharedpreferences.edit();

        mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL", AppUtils.CURRENT_RESTAURANT_NAME);
        mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL_AR", AppUtils.CURRENT_RESTAURANT_NAME_AR);
        mEditor.apply();

        // mFood.setPriceBasket(AppUtils.monetize(String.valueOf(mBasePrice + mAddOnPrice + mIngredientPrice)));
     /*   mFood.setBasketCount(String.valueOf(mQty));
        for (FoodAddBasketActivity.Plate plate : plates) {
            if (plate.selected) {
                mFood.setSizeBasket(plate.size);
                mFood.setSizeBasketId(plate.id);
            }
        }*/
/*        String strAddOns = "";
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
        mFood.setAddOnPrices(strAddOnPrices);*/

/*        String strIngredients = "";
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
        mFood.setIngredientPrices(strIngredientPrices);*/


        final DBHelper db = new DBHelper(context);
        if (db.getBasketMerchantId().equals(mFood.getMerchantId()) || db.getBasketMerchantId().length() == 0) {
            db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
          /*  new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onBackPressed();
                }
            }, 1500);
            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();*/
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getString(R.string.msg_pending_order))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteAll();
                            db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
                           /* new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onBackPressed();
                                }
                            }, 1500);
                            Snackbar.make(findViewById(R.id.page), getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();*/
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


    private void dbUpdate(final Dialog mSizeDialog) {
        // mFood.setUnitPrice(String.valueOf(mBasePrice / mQty));
        //   tvTotal.setText(getString(R.string.currency)+ AppUtils.changeToArabic(String.valueOf(mBasePrice + mAddOnPrice + mIngredientPrice), getApplicationContext()));
        db = new DBHelper(context);

        System.out.println("nksdnfk : db.getBasketMerchantId() : " + db.getBasketMerchantId());
        System.out.println("nksdnfk : mFood.getMerchantId() : " + mFood.getMerchantId());

        if (db.getBasketMerchantId().equals(mFood.getMerchantId()) || db.getBasketMerchantId().length() == 0) {
            db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 1500);
            Snackbar.make(mRestaurantOrderItemsFragment.view.findViewById(R.id.linearSnackBar), context.getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getString(R.string.msg_pending_order))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteAll();
                            AppUtils.testHashMap.clear();
                            AppUtils.testHashMap.put(mFood.getId(), "1");
                            db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                }
                            }, 1500);
                            Snackbar.make(mRestaurantOrderItemsFragment.view.findViewById(R.id.page), context.getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();
                            updateBottomView();
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

        updateBottomView();
        mSizeDialog.dismiss();

    }

    private void dbUpdateView(final View mSizeDialog) {
        // mFood.setUnitPrice(String.valueOf(mBasePrice / mQty));
        //   tvTotal.setText(getString(R.string.currency)+ AppUtils.changeToArabic(String.valueOf(mBasePrice + mAddOnPrice + mIngredientPrice), getApplicationContext()));


        db = new DBHelper(context);
        System.out.println("nksdnfk : db.getBasketMerchantId() : " + db.getBasketMerchantId());
        System.out.println("nksdnfk : mFood.getMerchantId() : " + mFood.getMerchantId());
        Log.e("MERCHANT_ID==>",""+db.getBasketMerchantId())  ;
        Log.e("MERCHANT_ID==>",""+mFood.getMerchantId())  ;
        if (db.getBasketMerchantId().equals(mFood.getMerchantId()) || db.getBasketMerchantId().length() == 0) {
            db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 1500);
            Snackbar.make(mRestaurantOrderItemsFragment.view.findViewById(R.id.page), context.getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getString(R.string.msg_pending_order))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteAll();
                            AppUtils.testHashMap.clear();
                            AppUtils.testHashMap.put(mFood.getId(), "1");
                            db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                }
                            }, 1500);
                            Snackbar.make(mRestaurantOrderItemsFragment.view.findViewById(R.id.page), context.getString(R.string.msg_food_added_basket), Snackbar.LENGTH_SHORT).show();
                            updateBottomView();
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

        updateBottomView();

    }



    public void sizeDialogNew(final JSONArray mJsonArray_actual_price, final JSONArray mJsonArray_sales_price, String dishName, final int position, final List<Food> argFood, final View argView,final TextView quantity) {
        int current_quantity=1;
        final Dialog mSizeDialog = new Dialog(context, android.R.style.Theme_Light_NoTitleBar);
        mSizeDialog.setContentView(R.layout.select_size_dialog);
        if(!quantity.getText().toString().equals("")){
            current_quantity = Integer.parseInt(quantity.getText().toString());
        }

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
        final int finalCurrent_quantity = current_quantity;
        btnAddToBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppUtils.PLATE_TYPE.equalsIgnoreCase("") || AppUtils.PLATE_TYPE.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.please_select_size_first), Toast.LENGTH_SHORT).show();
                }
                else {
                    SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor mEditor = sharedpreferences.edit();

                    mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL", AppUtils.CURRENT_RESTAURANT_NAME);
                    mEditor.putString("CURRENT_RESTAURANT_NAME_FINAL_AR", AppUtils.CURRENT_RESTAURANT_NAME_AR);
                    mEditor.apply();
                    mBasePrice = Double.parseDouble(AppUtils.PLATE_PRICE)*finalCurrent_quantity;
                    System.out.println("dosdvosnvo : getId : " + argFood.get(position).getId());
                    System.out.println("dosdvosnvo : getName : " + argFood.get(position).getName());
                    System.out.println("dosdvosnvo : getMerchantId : " + argFood.get(position).getMerchantId());
                    System.out.println("dosdvosnvo : PLATE_PRICE : " + AppUtils.PLATE_PRICE);
                    //Set Food Id,Name,
                    mFood.setId(argFood.get(position).getId());
                    mFood.setName(argFood.get(position).getName());
                    mFood.setMerchantId(argFood.get(position).getMerchantId());
                    mFood.setPriceBasket(AppUtils.monetize(String.valueOf(mBasePrice)));
                    mFood.setUnitPrice(String.valueOf(mBasePrice / finalCurrent_quantity));

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
                    mFood.setBasketCount(String.valueOf(finalCurrent_quantity));

                    argView.findViewById(R.id.add_before_linear).setVisibility(View.GONE);
                    argView.findViewById(R.id.plus_minus_linear).setVisibility(View.VISIBLE);

                    System.out.println("FoodsAdapter : food_id : " + mFood.getId());

                    AppUtils.testHashMap.put(mFood.getId(),String.valueOf(finalCurrent_quantity));
                    try {
                        db.removeFoodBasket(mFood.getId());
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
                    AppInstance.getInstance(context).addToSharedPref("min_price",AppUtils.MINIMUM_PRICE);
                    updateBottomView();
                    notifyDataSetChanged();

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

    static class ViewHolder {
        ImageView downloadImageButton;
        TextView catlogTitle;
        ImageView icon;
        int position;
    }
}
