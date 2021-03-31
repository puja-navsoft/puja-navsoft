package com.afieat.ini.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
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

/**
 * Created by Nav81 on 7/28/2017.
 */

public class CrazyDealsAdapter extends RecyclerView.Adapter<CrazyDealsAdapter.ViewHolder> {

    private Context mContextx = null;
    private LayoutInflater inflater = null;
    private JSONArray mainarray = null;
    private String sourceFragment = null;
    private String lang = "";
    private String redirectionType = "";
    private String restaurentIds = "", itemIds = "", itemNames = "";
    private AppInstance appInstance;


    public CrazyDealsAdapter(Context context, JSONArray mainArray, String lang) {

        this.mContextx = context;
        this.mainarray = mainArray;
        inflater = LayoutInflater.from(context);
        this.lang = lang;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_crazy_deals_restaurent_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        appInstance = AppInstance.getInstance(mContextx);
        JSONArray itemNameArray = null;
        try {

            redirectionType = mainarray.getJSONObject(position).optString("redirection_type");

            JSONArray restaurentIdsArray = new JSONArray(mainarray.optJSONObject(position).optString("restaurant_ids"));

            for (int j = 0; j < restaurentIdsArray.length(); j++) {
                restaurentIds = (restaurentIdsArray.optString(j));
            }


            if (redirectionType.equals("2")) {
                JSONArray itemIdsarray = new JSONArray(mainarray.optJSONObject(position).optString("item_ids"));
                for (int j = 0; j < 1; j++) {
                    itemIds = (itemIdsarray.optString(j));
                }

                if (appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {
                    itemNameArray = new JSONArray(mainarray.optJSONObject(position).optString("items_names_ar"));
                } else {
                    itemNameArray = new JSONArray(mainarray.optJSONObject(position).optString("items_names"));
                }

                for (int j = 0; j < 1; j++) {
                    itemNames = (itemNameArray.optString(j));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            if (lang.equals("ar")) {

                holder.dealName.setText(mainarray.getJSONObject(position).getString("deal_name_ar"));
                holder.dealText.setText(mainarray.getJSONObject(position).getString("deal_text_ar"));
                holder.dealLabel.setText(mainarray.getJSONObject(position).getString("deal_label_ar"));

                Glide
                        .with(mContextx)
                        .load(mainarray.getJSONObject(position).getString("flashads_photo"))
                        .placeholder(R.drawable.placeholder_land)
                        .into(holder.image);
            } else {

                holder.dealName.setText(mainarray.getJSONObject(position).getString("deal_name"));
                holder.dealText.setText(mainarray.getJSONObject(position).getString("deal_text"));
                holder.dealLabel.setText(mainarray.getJSONObject(position).getString("deal_label"));

                Glide
                        .with(mContextx)
                        .load(mainarray.getJSONObject(position).getString("flashads_photo"))
                        .placeholder(R.drawable.placeholder_land)
                        .into(holder.image);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.dealLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (redirectionType.equals("1")) {

                    Intent intent = new Intent(mContextx, RestaurantsDetailActivity.class);
                    intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, restaurentIds);
                    mContextx.startActivity(intent);
                } else if (redirectionType.equals("2")) {

                    Intent intent = new Intent(mContextx, FoodAddBasketActivity.class);
                    intent.putExtra(AppUtils.EXTRA_FOOD_ID, itemIds);

                    intent.putExtra(AppUtils.EXTRA_FOOD_NAME, itemNames);
                    mContextx.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return this.mainarray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dealName = null,
                dealText = null,
                dealLabel = null;
        private ImageView image = null;

        public ViewHolder(View itemView) {
            super(itemView);
            dealName = (TextView) itemView.findViewById(R.id.deal_name);
            dealText = (TextView) itemView.findViewById(R.id.deal_text);
            dealLabel = (TextView) itemView.findViewById(R.id.deal_level);
            image = (ImageView) itemView.findViewById(R.id.dialog_main_img);

        }
    }
}
