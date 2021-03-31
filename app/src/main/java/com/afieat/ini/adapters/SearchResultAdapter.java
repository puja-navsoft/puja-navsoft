package com.afieat.ini.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.afieat.ini.R;
import com.afieat.ini.models.Search;
import com.afieat.ini.models.SearchResponse;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harsh Rastogi on 18/01/18.
 */

public class SearchResultAdapter extends BaseAdapter {

    Context mContext;
    List<Search> results;
    String index;
    ArrayList<Search> searchList;
    private ArrayList<Search> newSearchList;
    ArrayList<SearchResponse.ResultsBean> mainList;

    public SearchResultAdapter(Context mContext, ArrayList<Search> searchList) {
        this.mContext = mContext;
        this.searchList = searchList;
        this.newSearchList = new ArrayList<Search>();
        this.newSearchList.addAll(searchList);

        Log.d("AdapterSearchList==>", "SearchResultAdapter: " + searchList.toString());
        Log.d("AdapterSearchList==>", "SearchResultAdapter: " + newSearchList.toString());


    }

   /* public SearchResultAdapter(Context mContext, ArrayList<Search> searchList, String index) {
        this.mContext = mContext;
        this.searchList = searchList;
        this.newSearchList = new ArrayList<Search>();
        this.newSearchList.addAll(searchList);
        this.index=index;

        Log.d("AdapterSearchList==>", "SearchResultAdapter: "+searchList.toString());
        Log.d("AdapterSearchList==>", "SearchResultAdapter: "+newSearchList.toString());
    }*/


    public void filter(String searchInput) {
        //searchInput = searchInput.toLowerCase(Locale.getDefault());
        searchList.clear();
        if (searchInput.length() >= 3) {
            searchList.addAll(newSearchList);
        } else {
            for (Search searchUserListBean : newSearchList) {
                if (searchUserListBean.getIndex().equals("restaurants")) {

                    if (searchUserListBean.getRestaurant_name().contains(searchInput)) {
                        searchList.add(searchUserListBean);
                    }
                } else if (searchUserListBean.getIndex().equals("items")) {

                    if (searchUserListBean.getItem_name().contains(searchInput)) {
                        searchList.add(searchUserListBean);
                    }
                } else if (searchUserListBean.getIndex().equals("cuisine")) {

                    if (searchUserListBean.getCuisine_name().contains(searchInput)) {
                        searchList.add(searchUserListBean);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return searchList.size();
    }

    @Override
    public Object getItem(int i) {
        return searchList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        }
        Search search = searchList.get(position);


        ImageView iv_type = (ImageView) convertView.findViewById(R.id.iv_type);
  /*      TextView tv_restaurant=(TextView)convertView.findViewById(R.id.tv_restaurant);
        TextView tv_item=(TextView)convertView.findViewById(R.id.tv_item);
        TextView tv_cuisine=(TextView)convertView.findViewById(R.id.tv_cuisine);*/

        String index = search.getIndex();
        Log.d("AdapterSearchIndex", "getView: " + searchList);

        if (index.equalsIgnoreCase("restaurants")) {

          /*  if (search.getRestaurant_name()!=null){
                Glide.with(mContext)
                        .load(R.drawable.restaurant)
                        .placeholder(R.drawable.app_logo)
                        .into(iv_type);
                tv_restaurant.setText(search.getRestaurant_name());
                tv_cuisine.setVisibility(View.GONE);
                tv_item.setVisibility(View.GONE);
            }
            else {
                iv_type.setVisibility(View.GONE);
                tv_restaurant.setVisibility(View.GONE);
            }*/
            Glide.with(mContext)
                    .load(R.drawable.restaurant)
                    .placeholder(R.drawable.app_logo)
                    .into(iv_type);
           /* tv_restaurant.setText(search.getRestaurant_name());
            tv_cuisine.setVisibility(View.GONE);
            tv_item.setVisibility(View.GONE);*/

        } else if (index.equalsIgnoreCase("items")) {

        /*    if (search.getItem_name()!=null){
                Glide.with(mContext)
                        .load(R.drawable.food)
                        .placeholder(R.drawable.app_logo)
                        .into(iv_type);
                tv_item.setText(search.getItem_name());
                tv_cuisine.setVisibility(View.GONE);
                tv_restaurant.setVisibility(View.GONE);
            }else {
                iv_type.setVisibility(View.GONE);
                tv_item.setVisibility(View.GONE);
            }*/

            Glide.with(mContext)
                    .load(R.drawable.food)
                    .placeholder(R.drawable.app_logo)
                    .into(iv_type);
            /*tv_item.setText(search.getItem_name());
            tv_cuisine.setVisibility(View.GONE);
            tv_restaurant.setVisibility(View.GONE);*/
        } else if (index.equalsIgnoreCase("cuisine")) {

      /*      if (search.getCuisine_name()!=null){

                Glide.with(mContext)
                        .load(R.drawable.cuisine)
                        .placeholder(R.drawable.app_logo)
                        .into(iv_type);
                tv_cuisine.setText(search.getCuisine_name());
                tv_item.setVisibility(View.GONE);
                tv_restaurant.setVisibility(View.GONE);
            }
            else {
                iv_type.setVisibility(View.GONE);
                tv_cuisine.setVisibility(View.GONE);
            }*/

            Glide.with(mContext)
                    .load(R.drawable.cuisine)
                    .placeholder(R.drawable.app_logo)
                    .into(iv_type);
           /* tv_cuisine.setText(search.getCuisine_name());
            tv_item.setVisibility(View.GONE);
            tv_restaurant.setVisibility(View.GONE);*/
        }

        return convertView;
    }


}
