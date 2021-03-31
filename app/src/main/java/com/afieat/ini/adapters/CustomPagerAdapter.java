package com.afieat.ini.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afieat.ini.FoodAddBasketActivity;
import com.afieat.ini.R;
import com.afieat.ini.RestaurantsDetailActivity;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Nav81 on 8/3/2017.
 */

public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private JSONArray mainArray = null;
    private String user_lang = null;
    private LayoutInflater inflater = null;
    private String sourceFragment = null;
    private String lang = "";
    private String redirectionType = "";
    private String restaurentIds = "", itemIds = "", itemNames = "";
    private AppInstance appInstance;
    private ArrayList<String> total_item_ids;
    private ArrayList<String> total_item_names = new ArrayList<>();

    public CustomPagerAdapter(Context context, JSONArray mainArray) {
        this.mContext = context;
        this.mainArray = mainArray;
        total_item_ids = new ArrayList<>();
        this.inflater = (LayoutInflater) this.mContext.getSystemService(this.mContext.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = this.inflater.inflate(R.layout.item_crazy_deals_restaurent_list, container, false);
        appInstance = AppInstance.getInstance(mContext);
        JSONArray restaurentIdsArray = null;
        JSONArray itemNameArray = null;
        try {

            redirectionType = mainArray.getJSONObject(position).optString("redirection_type");

//            restaurentIdsArray = new JSONArray( mainArray.optJSONObject(position).optString("restaurant_ids"));
//
//            for (int j=0; j < restaurentIdsArray.length(); j++) {
//                restaurentIds = (restaurentIdsArray.optString(j));
//            }
//
//            total_item_ids.add("");
//            if (redirectionType.equals("2")) {
//                JSONArray itemIdsarray = new JSONArray(mainArray.optJSONObject(position).optString("item_ids"));
//                for (int j=0; j < 1; j++) {
//                    itemIds = (itemIdsarray.optString(j));
//                }
//                AppUtils.log("@@ K POSITION-"+position+"  "+itemIds);
//
//                total_item_ids.set(position,itemIds);
//
//                AppUtils.log("lang--11" + appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG));
//                if (appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG).equalsIgnoreCase("ar")) {
//                  //  itemNameArray = new JSONArray(mainArray.optJSONObject(position).optString("deal_name_ar"));
//                    itemNameArray = new JSONArray(mainArray.optJSONObject(position).optString("deal_name_ar"));
//                }else {
//                    itemNameArray = new JSONArray(mainArray.optJSONObject(position).optString("deal_name"));
//                }
//
//                for (int j=0; j < 1; j++) {
//                    itemNames = (itemNameArray.optString(j));
//                }
//                total_item_names.add(position,itemNames);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        try {
            AppUtils.log("lang--" + appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG));

            if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                ((TextView) view.findViewById(R.id.deal_name)).setText(mainArray.getJSONObject(position).getString("deal_name_ar"));
                ((TextView) view.findViewById(R.id.deal_text)).setText(mainArray.getJSONObject(position).getString("deal_text_ar"));
                ((TextView) view.findViewById(R.id.deal_level)).setText(mainArray.getJSONObject(position).getString("deal_label_ar"));

                Glide
                        .with(mContext)
                        .load(mainArray.getJSONObject(position).getString("flashads_photo"))
                        .placeholder(R.drawable.placeholder_land)
                        .into((ImageView) view.findViewById(R.id.dialog_main_img));
            } else {

                ((TextView) view.findViewById(R.id.deal_name)).setText(mainArray.getJSONObject(position).getString("deal_name"));
                ((TextView) view.findViewById(R.id.deal_text)).setText(mainArray.getJSONObject(position).getString("deal_text"));
                ((TextView) view.findViewById(R.id.deal_level)).setText(mainArray.getJSONObject(position).getString("deal_label"));

                Glide
                        .with(mContext)
                        .load(mainArray.getJSONObject(position).getString("flashads_photo"))
                        .placeholder(R.drawable.placeholder_land)
                        .into((ImageView) view.findViewById(R.id.dialog_main_img));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((TextView) view.findViewById(R.id.deal_level)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if ("1".equalsIgnoreCase(mainArray.getJSONObject(position).optString("redirection_type"))) {
                        JSONArray restaurentIdsArray = new JSONArray(mainArray.getJSONObject(position).optString("restaurant_ids"));

                        String restaurentIds = null;
                        for (int j = 0; j < restaurentIdsArray.length(); j++) {
                            restaurentIds = (restaurentIdsArray.optString(j));
                        }
                        if (restaurentIds != null) {
                            Intent intent = new Intent(mContext, RestaurantsDetailActivity.class);
                            intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, restaurentIds);
                            mContext.startActivity(intent);
                        }
                    } else if ("2".equalsIgnoreCase(mainArray.getJSONObject(position).optString("redirection_type"))) {
                        JSONArray itemIdsarray = new JSONArray(mainArray.getJSONObject(position).optString("item_ids"));
                        String itemIds = null;
                        for (int j = 0; j < 1; j++) {
                            itemIds = (itemIdsarray.optString(j));
                        }
                        if (itemIds != null) {
                            Intent intent = new Intent(mContext, FoodAddBasketActivity.class);
                            intent.putExtra(AppUtils.EXTRA_FOOD_ID, itemIds);
                            mContext.startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return mainArray.length();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


}
