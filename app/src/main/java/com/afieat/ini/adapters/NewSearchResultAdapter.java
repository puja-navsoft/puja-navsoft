package com.afieat.ini.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.models.Search;
import com.afieat.ini.utils.AppUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Harsh Rastogi on 18/01/18.
 */

public class NewSearchResultAdapter extends RecyclerView.Adapter<NewSearchResultAdapter.ViewHolder> {

    private Activity mContext;
    //ArrayList<Search> searchList;
    private ArrayList<Search> searchList;
    private SearchListListener mListener;
    private String lang = "";

    public NewSearchResultAdapter(Activity mContext, ArrayList<Search> searchList, String lang) {
        this.mContext = mContext;
        this.searchList = searchList;
        this.lang = lang;

        Log.d("AdapterSearchList==>", "SearchResultAdapter: " + searchList.toString());


    }

    public interface SearchListListener {
         void onItemClick(Search item, int position);

    }

    public void setAdapterListener(SearchListListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Search search = searchList.get(position);

        String index = search.getIndex();
        Log.d("AdapterSearchIndex", "getView: " + searchList);


        if ("ar".equals(lang)) {
            holder.tv_restaurantName.setText(" - " + search.getRestaurant_name_ar());
        } else {
            holder.tv_restaurantName.setText(" - " + search.getRestaurant_name());
        }


        if ("ar".equals(lang)) {
            if (index.equalsIgnoreCase(AppUtils.RESTAURANT_INDEX)) {
                Glide.with(mContext)
                        .load("https://d22vvrqrexhw5s.cloudfront.net/upload/restaurants/image/thumb_81_81/" + search.getPhoto())
                        .placeholder(R.mipmap.ic_restaurant)
                        .into(holder.iv_type);

                holder.tv_type.setText(search.getRestaurant_name_ar());
                holder.tv_type_detail.setText("مطعم");


            } else if (AppUtils.ITEMS_INDEX.equalsIgnoreCase(index)) {
                Glide.with(mContext)
                        .load("https://d22vvrqrexhw5s.cloudfront.net/upload/items/image/thumb_68_68/" + search.getPhoto())
                        .placeholder(R.mipmap.ic_food)
                        .into(holder.iv_type);
                holder.tv_type.setText(search.getItem_name_ar());
                holder.tv_type_detail.setText("طبق");

            } else if (AppUtils.CUISINE_INDEX.equalsIgnoreCase(index)) {
                Glide.with(mContext)
                        .load(R.mipmap.ic_cuisine)
                        .placeholder(R.drawable.app_logo)
                        .into(holder.iv_type);
                holder.tv_type.setText(search.getCuisine_name_ar());
                holder.tv_type_detail.setText("أطباق");

            }
        } else {
            if (AppUtils.RESTAURANT_INDEX.equalsIgnoreCase(index)) {
                Glide.with(mContext)
                        .load("https://d22vvrqrexhw5s.cloudfront.net/upload/restaurants/image/thumb_81_81/" + search.getPhoto())
                        .placeholder(R.mipmap.ic_restaurant)
                        .into(holder.iv_type);

                holder.tv_type.setText(search.getRestaurant_name());
                holder.tv_type_detail.setText("Restaurant");
                System.out.println("Rahul : NewSearchResultAdapter : getPhoto : " + "https://d22vvrqrexhw5s.cloudfront.net/upload/restaurants/image/thumb_81_81/" + search.getPhoto());

            } else if (AppUtils.ITEMS_INDEX.equalsIgnoreCase(index)) {
                Glide.with(mContext)
                        .load("https://d22vvrqrexhw5s.cloudfront.net/upload/items/image/thumb_68_68/" + search.getPhoto())
                        .placeholder(R.mipmap.ic_food)
                        .into(holder.iv_type);
                holder.tv_type.setText(search.getItem_name());
                holder.tv_type_detail.setText("Dish");

                System.out.println("Rahul : NewSearchResultAdapter : getPhoto : " + "https://d22vvrqrexhw5s.cloudfront.net/upload/items/image/thumb_68_68/" + search.getPhoto());

            } else if (AppUtils.CUISINE_INDEX.equalsIgnoreCase(index)) {
                Glide.with(mContext)
                        .load(R.mipmap.ic_cuisine)
                        .placeholder(R.drawable.app_logo)
                        .into(holder.iv_type);
                holder.tv_type.setText(search.getCuisine_name());
                holder.tv_type_detail.setText("Cuisine");

            }
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(searchList.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_type;
        private TextView tv_type;
        private TextView tv_type_detail, tv_restaurantName;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_type = (ImageView) itemView.findViewById(R.id.iv_type);
            tv_type = (TextView) itemView.findViewById(R.id.tv_type);
            tv_restaurantName = (TextView) itemView.findViewById(R.id.tv_restaurantName);
            tv_type_detail = (TextView) itemView.findViewById(R.id.tv_type_detail);
/*
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(searchList.get(getAdapterPosition()), getAdapterPosition());
                }
            });*/

        }
    }
}


