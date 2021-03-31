package com.afieat.ini.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class CrazyDealsFragment extends Fragment {


    private Context mContextx = null;
    private LayoutInflater inflater = null;
    private JSONArray mainarray = null;
    private String sourceFragment = null;
    private String lang = "";
    private String redirectionType = "";
    private String restaurentIds = "", itemIds = "", itemNames = "";
    private AppInstance appInstance;

    private JSONObject mainObject = null;

    public static CrazyDealsFragment getInstance(JSONObject jsonObject) {
        CrazyDealsFragment frag_ = new CrazyDealsFragment();
        frag_.mainObject = jsonObject;
        return frag_;
    }


  /*  public CrazyDealsFragment() {
        // Required empty public constructor
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crazy_deals, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        redirectionType = mainObject.optString("redirection_type");


        JSONArray restaurentIdsArray = null;
        JSONArray itemNameArray = null;
        try {
            restaurentIdsArray = new JSONArray(mainObject.optString("restaurant_ids"));

            for (int j = 0; j < restaurentIdsArray.length(); j++) {
                restaurentIds = (restaurentIdsArray.optString(j));
            }

            if ("2".equals(redirectionType)) {
                JSONArray itemIdsarray = new JSONArray(mainObject.optString("item_ids"));
                for (int j = 0; j < 1; j++) {
                    itemIds = (itemIdsarray.optString(j));
                }

                if ("ar".equals(appInstance.getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    itemNameArray = new JSONArray(mainObject.optString("items_names_ar"));
                } else {
                    itemNameArray = new JSONArray(mainObject.optString("items_names"));
                }

                for (int j = 0; j < 1; j++) {
                    itemNames = (itemNameArray.optString(j));
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }


        try {

            if ("ar".equals(lang)) {

                ((TextView) view.findViewById(R.id.deal_name)).setText(mainObject.getString("deal_name_ar"));
                ((TextView) view.findViewById(R.id.deal_text)).setText(mainObject.getString("deal_text_ar"));
                ((TextView) view.findViewById(R.id.deal_level)).setText(mainObject.getString("deal_label_ar"));

                Glide
                        .with(mContextx)
                        .load(mainObject.getString("flashads_photo"))
                        .placeholder(R.drawable.placeholder_land)
                        .into((ImageView) view.findViewById(R.id.dialog_main_img));
            } else {

                ((TextView) view.findViewById(R.id.deal_name)).setText(mainObject.getString("deal_name"));
                ((TextView) view.findViewById(R.id.deal_text)).setText(mainObject.getString("deal_text"));
                ((TextView) view.findViewById(R.id.deal_level)).setText(mainObject.getString("deal_label"));

                Glide
                        .with(mContextx)
                        .load(mainObject.getString("flashads_photo"))
                        .placeholder(R.drawable.placeholder_land)
                        .into((ImageView) view.findViewById(R.id.dialog_main_img));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((TextView) view.findViewById(R.id.deal_level)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("1".equals(redirectionType)) {

                    Intent intent = new Intent(mContextx, RestaurantsDetailActivity.class);
                    intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, restaurentIds);
                    mContextx.startActivity(intent);
                } else if ("2".equals(redirectionType)) {

                    Intent intent = new Intent(mContextx, FoodAddBasketActivity.class);
                    intent.putExtra(AppUtils.EXTRA_FOOD_ID, itemIds);

                    intent.putExtra(AppUtils.EXTRA_FOOD_NAME, itemNames);
                    mContextx.startActivity(intent);
                }
            }
        });


    }
}
