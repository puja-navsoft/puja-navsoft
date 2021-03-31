package com.afieat.ini.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afieat.ini.BasketActivity2;
import com.afieat.ini.R;
import com.afieat.ini.database.DBHelper;
import com.afieat.ini.models.DemoSearchModel;
import com.afieat.ini.models.Food;
import com.afieat.ini.models.Search;
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
 * Created by Harsh Rastogi on 18/01/18.
 */

public class ViewOnlineSearchResultAdapterCopy extends RecyclerView.Adapter<ViewOnlineSearchResultAdapterCopy.ViewHolder> implements Filterable {

    private Food mFood;
    private View view;
    private Context context;
    private ViewGroup mViewGroup;
    private DBHelper db;
    private ArrayList<DemoSearchModel> mDemoSearchModelsMain;
    private ArrayList<DemoSearchModel> mDemoSearchModelsMainFilter;

    private double mBasePrice;
    private LinearLayout llPrices;

    public ViewOnlineSearchResultAdapterCopy(Context mContext,
                                             ArrayList<DemoSearchModel> mDemoSearchModelsMain,
                                             FoodsAdapter mFoodsAdapter) {
        context = mContext;

        this.mDemoSearchModelsMain = mDemoSearchModelsMain;
        mDemoSearchModelsMainFilter = mDemoSearchModelsMain;

        mFood = new Food();
        Gson mGson = new Gson();
        System.out.println("Search : ViewOnlineSearchResultAdapter : " + mGson.toJson(mDemoSearchModelsMainFilter));


    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                try {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        mDemoSearchModelsMainFilter = mDemoSearchModelsMain;

                    } else {
                        ArrayList<DemoSearchModel> filteredList = new ArrayList<>();

                        for (DemoSearchModel row : mDemoSearchModelsMain) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG).equalsIgnoreCase("en")) {
                                if (row.getFoodName().toLowerCase().contains(charString.toLowerCase()) || row.getFoodName().contains(charSequence)) {
                                    filteredList.add(row);
                                }
                            } else {

                                if (row.getFoodNameAr().toLowerCase().contains(charString.toLowerCase()) || row.getFoodNameAr().contains(charSequence)) {
                                    filteredList.add(row);
                                }
                            }

                        }


                        mDemoSearchModelsMainFilter = filteredList;
                    }


                } catch (Exception e) {
                    System.out.println("ERRRRRRR : " + e.getMessage());
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mDemoSearchModelsMainFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                try {

                    mDemoSearchModelsMainFilter = (ArrayList<DemoSearchModel>) filterResults.values;
                    Gson mGson = new Gson();

                    notifyDataSetChanged();

                    System.out.println("Search  : ViewOnlineSearchResultAdapter : publishResults : " + mGson.toJson(mDemoSearchModelsMainFilter));

                } catch (Exception e) {
                    System.out.println("ERRRRRRR : " + e.getMessage());

                }
            }
        };
    }

    public interface SearchListListener {
        public void onItemClick(Search item, int position);

    }
/*
    public void setAdapterListener(SearchListListener mListener) {
        this.mListener = mListener;
    }*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_online_search_item, parent, false);
        mViewGroup = parent;
        System.out.println("Search : onCreateViewHolder : ");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            llPrices = view.findViewById(R.id.llPrices);

            System.out.println("Search : onBindViewHolder : Called ");
            final DemoSearchModel search = mDemoSearchModelsMainFilter.get(position);

            System.out.println("Search : onBindViewHolder : " + search.getFoodName());

            if (AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG).equalsIgnoreCase("en")) {
                holder.tv_type.setText(search.getFoodName());
            } else {
                holder.tv_type.setText(search.getFoodNameAr());
            }


            final String logoPath = Apis.IMG_PATH + "items/image/thumb_68_68/" + search.getImage_url();
            System.out.println("Search : onBindViewHolder : search.getmPriceList().size() : " + search.getmPriceList().size());
            Glide.with(context)
                    .load(logoPath)
                    .placeholder(R.drawable.placeholder_land)
                    .error(R.drawable.placeholder_land)
                    .into(holder.iv_type);

            Gson bbub = new Gson();

            int listsize = search.getmPriceList().size();


            setPricingVariant(search.mPriceList, search.mActualPriceList);


            //  llPrices.removeAllViews();


            // System.out.println("forloop out : i : "+i);
            holder.add_before_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("add_before_linear : click");

                    db = new DBHelper(context);

                    if (db.getBasketMerchantId().equals(search.getMerchant_id()) || db.getBasketMerchantId().length() == 0) {
                        System.out.println("add_before_linear : click : 1");

                        JSONArray mJsonArray_actual_price = null,
                                mJsonArray_sales_price = null;
                        Gson gson = new Gson();
                        String json_price = gson.toJson(search.getmPriceList());
                        String json_actual_price = gson.toJson(search.getmActualPriceList());

                        System.out.println("ViewOnlineSearchResultAdapter : arrayPrice : " + json_price);
                        System.out.println("ViewOnlineSearchResultAdapter : arrayActualPrice : " + json_actual_price);

                        try {
                            mJsonArray_actual_price = new JSONArray(json_actual_price);
                            mJsonArray_sales_price = new JSONArray(json_price);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (search.getmPriceList().size() > 1) {
                            sizeDialog(mJsonArray_actual_price, mJsonArray_sales_price, search.getFood_id(), search.getMerchant_id(), search.getFoodName(), holder.itemView);
                        } else {
                            mBasePrice = Double.parseDouble(search.getmPriceList().get(0).getPrice());

                            System.out.println("dosdvosnvo : getId : " + search.getFood_id());
                            System.out.println("dosdvosnvo : getName : " + search.getFoodName());
                            System.out.println("dosdvosnvo : getMerchantId : " + search.getMerchant_id());
                            System.out.println("dosdvosnvo : PLATE_PRICE : " + search.getmPriceList().get(0).getPrice());

                            //Set Food Id,Name,
                            mFood.setId(search.getFood_id());
                            mFood.setName(search.getFoodName());
                            mFood.setMerchantId(search.getMerchant_id());
                            mFood.setPriceBasket(AppUtils.monetize(String.valueOf(mBasePrice)));
                            mFood.setUnitPrice(String.valueOf(mBasePrice / 1));

                            mFood.setSizeBasketId(search.getmPriceList().get(0).getId());
                            mFood.setSizeBasket(search.getmPriceList().get(0).getType());
                            mFood.setAddOnIds("");
                            mFood.setIngredientIds("");
                            mFood.setAddOns("");
                            mFood.setComment("");
                            mFood.setIngredientPrices("");
                            mFood.setIngredients("");
                            mFood.setBasketCount("1");

                            System.out.println("FoodsAdapter : food_id : " + mFood.getId());
                            AppUtils.testHashMap.put(mFood.getId(), "1");
                            db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);


                            holder.add_before_linear.setVisibility(View.GONE);
                            holder.plus_minus_linear.setVisibility(View.VISIBLE);
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
                                        System.out.println("add_before_linear : click : 1");

                                        JSONArray mJsonArray_actual_price = null, mJsonArray_sales_price = null;
                                        Gson gson = new Gson();
                                        String json_price = gson.toJson(search.getmPriceList());
                                        String json_actual_price = gson.toJson(search.getmActualPriceList());

                                        System.out.println("ViewOnlineSearchResultAdapter : arrayPrice : " + json_price);
                                        System.out.println("ViewOnlineSearchResultAdapter : arrayActualPrice : " + json_actual_price);

                                        try {
                                            mJsonArray_actual_price = new JSONArray(json_actual_price);
                                            mJsonArray_sales_price = new JSONArray(json_price);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if (search.getmPriceList().size() > 1) {
                                            sizeDialog(mJsonArray_actual_price, mJsonArray_sales_price, search.getFood_id(), search.getMerchant_id(), search.getFoodName(), holder.itemView);
                                        } else {
                                            mBasePrice = Double.parseDouble(search.getmPriceList().get(0).getPrice());

                                            System.out.println("dosdvosnvo : getId : " + search.getFood_id());
                                            System.out.println("dosdvosnvo : getName : " + search.getFoodName());
                                            System.out.println("dosdvosnvo : getMerchantId : " + search.getMerchant_id());
                                            System.out.println("dosdvosnvo : PLATE_PRICE : " + search.getmPriceList().get(0).getPrice());

                                            //Set Food Id,Name,
                                            mFood.setId(search.getFood_id());
                                            mFood.setName(search.getFoodName());
                                            mFood.setMerchantId(search.getMerchant_id());
                                            mFood.setPriceBasket(AppUtils.monetize(String.valueOf(mBasePrice)));
                                            mFood.setUnitPrice(String.valueOf(mBasePrice / 1));

                                            mFood.setSizeBasketId(search.getmPriceList().get(0).getId());
                                            mFood.setSizeBasket(search.getmPriceList().get(0).getType());
                                            mFood.setAddOnIds("");
                                            mFood.setIngredientIds("");
                                            mFood.setAddOns("");
                                            mFood.setComment("");
                                            mFood.setIngredientPrices("");
                                            mFood.setIngredients("");
                                            mFood.setBasketCount("1");

                                            System.out.println("FoodsAdapter : food_id : " + mFood.getId());
                                            AppUtils.testHashMap.put(mFood.getId(), "1");
                                            db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);


                                            holder.add_before_linear.setVisibility(View.GONE);
                                            holder.plus_minus_linear.setVisibility(View.VISIBLE);
                                        }
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


            holder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    db = new DBHelper(context);
                    int current_quantity = Integer.parseInt(holder.quantity.getText().toString());
                    current_quantity = current_quantity + 1;
                    holder.quantity.setText("" + current_quantity);

                    if (search.getmPriceList().size() > 1) {
                        String price = AppUtils.monetize(String.valueOf((Double.parseDouble(AppUtils.PLATE_PRICE)) * current_quantity));
                        System.out.println("priceplus : " + price);
                        mFood.setPriceBasket(price);

                    } else {
                        String price = AppUtils.monetize(String.valueOf(Double.parseDouble(search.getmPriceList().get(0).getPrice()) * current_quantity));
                        System.out.println("priceplus : " + price);
                        mFood.setPriceBasket(price);
                    }

                    db.updateFoodBasket(mFood.getPriceBasket(), current_quantity, search.getFood_id());
                    AppUtils.testHashMap.put(search.getFood_id(), String.valueOf(current_quantity));


                }
            });


            holder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    db = new DBHelper(context);
                    int current_quantity = Integer.parseInt(holder.quantity.getText().toString());

                    if (current_quantity == 1) {
                        holder.add_before_linear.setVisibility(View.VISIBLE);
                        holder.plus_minus_linear.setVisibility(View.GONE);
                        db.removeFoodBasket(search.getFood_id());
                        AppUtils.testHashMap.remove(search.getFood_id());

                    } else {
                        current_quantity = current_quantity - 1;

                        if (search.getmPriceList().size() > 1) {
                            String price = AppUtils.monetize(String.valueOf((Double.parseDouble(AppUtils.PLATE_PRICE)) * current_quantity));
                            System.out.println("priceplus : " + price);
                            mFood.setPriceBasket(price);

                        } else {
                            String price = AppUtils.monetize(String.valueOf(Double.parseDouble(search.getmPriceList().get(0).getPrice()) * current_quantity));
                            System.out.println("priceplus : " + price);
                            mFood.setPriceBasket(price);
                        }

                        db.updateFoodBasket(mFood.getPriceBasket(), current_quantity, search.getFood_id());
                        AppUtils.testHashMap.put(search.getFood_id(), String.valueOf(current_quantity));
                        holder.quantity.setText(String.valueOf(current_quantity));
                    }


                }
            });

            if (AppUtils.IS_SUPER_MARKET && AppUtils.testHashMap.size() > 0 && AppUtils.testHashMap.get(search.getFood_id()) != null) {


                holder.add_before_linear.setVisibility(View.GONE);
                holder.plus_minus_linear.setVisibility(View.VISIBLE);
                holder.quantity.setText("" + AppUtils.testHashMap.get(search.getFood_id()));
            } else {
                holder.add_before_linear.setVisibility(View.VISIBLE);
                holder.plus_minus_linear.setVisibility(View.GONE);
            }


             /*else {
            holder.add_before_linear.setVisibility(View.GONE);
            holder.plus_minus_linear.setVisibility(View.GONE);
//            view.findViewById(R.id.share_linear).setVisibility(View.VISIBLE);
        }*/

        } catch (Exception e) {
            System.out.println("ERRRRRRR : " + e.getMessage());

        }
    }

    public void setPricingVariant(List<DemoSearchModel.Price> mPriceList, List<DemoSearchModel.ActualPrice> mActualPriceList) {
        for (int i = 0; i < mPriceList.size(); i++) {

            View view1 = LayoutInflater.from(context).inflate(R.layout.layout_food_plate_price, null);


            ((TextView) view1.findViewById(R.id.tvPrice)).setText(context.getString(R.string.currency) + AppUtils.changeToArabic(mPriceList.get(i).getPrice(), context));
            ((TextView) view1.findViewById(R.id.tvPlate)).setText("(" + mPriceList.get(i).getType() + ")");

            double salesPrice = 0, actualPrice = 0;
            if (mPriceList.get(i).getPrice() != null || !"".equals(mPriceList.get(i).getPrice().trim())) {
                salesPrice = Double.parseDouble(mPriceList.get(i).getPrice());
            }
            if (mActualPriceList.get(i).getPrice() != null || !"".equals(mActualPriceList.get(i).getPrice().trim())) {
                actualPrice = Double.parseDouble(mActualPriceList.get(i).getPrice());
            }

        /*System.out.println("CHECKCKKKCKCK : getFoodName " + search.getFoodName());
        System.out.println("CHECKCKKKCKCK : getmPriceList " + bbub.toJson(search.getmPriceList()));*/
            // System.out.println("FoodsAdapter4 : salesPrice : " + salesPrice);
            System.out.println("CHECKCKKKCKCK : actualPrice,salesPrice : " + actualPrice + "/" + salesPrice);
            if (salesPrice != actualPrice) {
                ((TextView) view1.findViewById(R.id.tvActualPrice)).setText(context.getString(R.string.currency) + AppUtils.changeToArabic(mActualPriceList.get(i).getPrice(), context));
            } else {
                ((RelativeLayout) view1.findViewById(R.id.relPrice)).setVisibility(View.GONE);
            }

            llPrices.addView(view1);
            llPrices.setVisibility(View.GONE);
            llPrices.setVisibility(View.VISIBLE);
            view1 = null;
            Gson uu = new Gson();
            i = i + 1;
            //System.out.println("Chekcckc : pricelist : " + i + " : " + uu.toJson(search.getmPriceList()));


        }
    }

    @Override
    public int getItemCount() {
        return mDemoSearchModelsMainFilter.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_type;
        private TextView tv_type,
                minus,
                quantity,
                plus;
        private TextView tv_type_detail;
        private LinearLayout searchLayout;
        private LinearLayout add_before_linear,
                plus_minus_linear;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_type = (ImageView) itemView.findViewById(R.id.ivFoodLogo);
            tv_type = (TextView) itemView.findViewById(R.id.tvItemTitle);
            // tv_type_detail = (TextView) itemView.findViewById(R.id.tv_type_detail);
            // searchLayout = itemView.findViewById(R.id.searchLayout);
            add_before_linear = itemView.findViewById(R.id.add_before_linear);
            plus_minus_linear = itemView.findViewById(R.id.plus_minus_linear);
            minus = itemView.findViewById(R.id.minus);
            quantity = itemView.findViewById(R.id.quantity);
            plus = itemView.findViewById(R.id.plus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  mListener.onItemClick(searchList.get(getAdapterPosition()), getAdapterPosition());
                }
            });

        }
    }


    public void sizeDialog(final JSONArray mJsonArray_actual_price, final JSONArray mJsonArray_sales_price,
                           final String food_id, final String merchant_id,
                           final String dishName, final View argView) {

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


                    System.out.println("dosdvosnvo : getId : " + food_id);
                    System.out.println("dosdvosnvo : getName : " + dishName);
                    System.out.println("dosdvosnvo : getMerchantId : " + merchant_id);
                    System.out.println("dosdvosnvo : PLATE_PRICE : " + AppUtils.PLATE_PRICE);


                    //Set Food Id,Name,
                    mFood.setId(food_id);
                    mFood.setName(dishName);
                    mFood.setMerchantId(merchant_id);
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
                    mFood.setBasketCount("1");

                    System.out.println("FoodsAdapter : food_id : " + mFood.getId());

                    AppUtils.testHashMap.put(mFood.getId(), "1");
                    DBHelper db = new DBHelper(context);

                    db.addFoodBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID), mFood);
                    argView.findViewById(R.id.add_before_linear).setVisibility(View.GONE);
                    argView.findViewById(R.id.plus_minus_linear).setVisibility(View.VISIBLE);
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


    public void updateBottomViewSearchOne() {


        View bottomView = LayoutInflater.from(context).inflate(R.layout.search_dialog, null);


        System.out.println("updateBottomViewSearchOne  : ");
        DBHelper db = new DBHelper(context);
        int itemCount = db.getFoodsBasket(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID)).size();
        System.out.println("updateBottomViewSearchOne : itemCount : " + itemCount);
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

}


