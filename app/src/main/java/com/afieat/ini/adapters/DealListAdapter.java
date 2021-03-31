package com.afieat.ini.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Nav81 on 9/7/2017.
 */

public class DealListAdapter extends BaseAdapter {
    private Context context = null;
    private JSONArray mainArray = null;
    private String redirectionType = "";
    private String itemIds = "",
            itemNames = "";
    private AppInstance appInstance;
    private ArrayList<String> total_item_ids = new ArrayList<>();
    private ArrayList<String> total_item_names = new ArrayList<>();


    public DealListAdapter(JSONArray mainArray, Context context) {
        this.mainArray = mainArray;
        this.context = context;
        appInstance = AppInstance.getInstance(context);
    }

    @Override
    public int getCount() {
        return mainArray.length();
    }

    @Override
    public Object getItem(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View mConverterView = convertView;
        String restaurentIds = "";
        if (mConverterView == null) {
            mConverterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_deals_list_item, parent, false);
        }


        JSONArray restaurentIdsArray = null;
        JSONArray itemNameArray = null;
        try {

            redirectionType = mainArray.getJSONObject(position).optString("redirection_type");

            restaurentIdsArray = new JSONArray(mainArray.optJSONObject(position).optString("restaurant_ids"));

            Log.d("@@ LLLLL-", restaurentIdsArray.toString());

            for (int j = 0; j < restaurentIdsArray.length(); j++) {
                restaurentIds = (restaurentIdsArray.optString(j));
            }


            if ("2".equals(redirectionType)) {
                JSONArray itemIdsarray = new JSONArray(mainArray.optJSONObject(position).optString("item_ids"));
                for (int j = 0; j < 1; j++) {
                    itemIds = (itemIdsarray.optString(j));
                }
                total_item_ids.add(itemIds);
                AppUtils.log("lang--11" + appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG));
                if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    //  itemNameArray = new JSONArray(mainArray.optJSONObject(position).optString("deal_name_ar"));
                    itemNameArray = new JSONArray(mainArray.optJSONObject(position).optString("deal_name_ar"));
                } else {
                    itemNameArray = new JSONArray(mainArray.optJSONObject(position).optString("deal_name"));
                }

                for (int j = 0; j < 1; j++) {
                    itemNames = (itemNameArray.optString(j));
                }
                total_item_names.add(itemNames);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            AppUtils.log("lang--" + appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG));

            if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                ((TextView) mConverterView.findViewById(R.id.deal_name)).setText(mainArray.getJSONObject(position).getString("deal_name_ar"));
                ((TextView) mConverterView.findViewById(R.id.deal_text)).setText(mainArray.getJSONObject(position).getString("deal_text_ar"));


                Glide
                        .with(context)
                        .load(mainArray.getJSONObject(position).getString("flashads_photo"))
                        .placeholder(R.drawable.placeholder_land)
                        .into((ImageView) mConverterView.findViewById(R.id.dialog_main_img));
            } else {

                ((TextView) mConverterView.findViewById(R.id.deal_name)).setText(mainArray.getJSONObject(position).getString("deal_name"));
                ((TextView) mConverterView.findViewById(R.id.deal_text)).setText(mainArray.getJSONObject(position).getString("deal_text"));


                Glide
                        .with(context)
                        .load(mainArray.getJSONObject(position).getString("flashads_photo"))
                        .placeholder(R.drawable.placeholder_land)
                        .into((ImageView) mConverterView.findViewById(R.id.dialog_main_img));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        mConverterView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AppUtils.log("Check_onClick " +position);
//                if (redirectionType.equals("1")) {
//
//                    Intent intent = new Intent(context, RestaurantsDetailActivity.class);
//                    intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID,restaurentIds);
//                    context.startActivity(intent);
//                } else if (redirectionType.equals("2")) {
//
//                    Intent intent = new Intent(context, FoodAddBasketActivity.class);
//                    intent.putExtra(AppUtils.EXTRA_FOOD_ID,total_item_ids.get(position));
//                    context.startActivity(intent);
//                }
//            }
//        });

        return mConverterView;
    }
}
