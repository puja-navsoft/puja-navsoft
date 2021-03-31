package com.afieat.ini.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.fragments.RestaurantOrderItemsFragment;
import com.afieat.ini.fragments.RestaurantPopularProductsFragment;
import com.afieat.ini.models.CategoryCountModel;
import com.afieat.ini.models.CategoryList;

import java.util.ArrayList;
import java.util.List;

public class FoodMenuPopupAdapter extends BaseAdapter {
    private List<CategoryList> arrCatList;
    private Context context;
    private RestaurantOrderItemsFragment mRestaurantOrderItemsFragment;
    private RestaurantPopularProductsFragment mRestaurantPopularProductsFragment;

    public FoodMenuPopupAdapter(List<CategoryList> arrCatList,
                                Context context,
                                RestaurantOrderItemsFragment mRestaurantOrderItemsFragment,
                                RestaurantPopularProductsFragment mRestaurantPopularProductsFragment) {
        this.arrCatList = arrCatList;
        this.context = context;
        if (mRestaurantOrderItemsFragment != null) {
            this.mRestaurantOrderItemsFragment = mRestaurantOrderItemsFragment;
        } else {
            this.mRestaurantPopularProductsFragment = mRestaurantPopularProductsFragment;
        }

    }

    @Override
    public int getCount() {
        return arrCatList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrCatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_menu_popup_item, parent, false);
        TextView txt_Category=view.findViewById(R.id.txt_Category);
      //  TextView txt_CategoryCount=view.findViewById(R.id.txt_CategoryCount);
        View divView=view.findViewById(R.id.divView);
        divView.setVisibility(View.VISIBLE);
        txt_Category.setText(arrCatList.get(position).getCat_type());
        //txt_CategoryCount.setText(""+arrCatList.get(position).getCategoryCount());

        if(position==arrCatList.size()-1){
            divView.setVisibility(View.GONE);
        }

        return view;
    }
}
