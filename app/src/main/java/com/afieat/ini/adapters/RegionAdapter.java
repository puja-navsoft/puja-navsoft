package com.afieat.ini.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afieat.ini.LocationActivity;
import com.afieat.ini.R;
import com.afieat.ini.models.RegionModel;
import com.afieat.ini.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.MyViewHolder> {

    private List<RegionModel> mRegionModels;
    private Context context;
   private LocationActivity mLocationActivity;

    public void setFilteredData(ArrayList<RegionModel> filteredRegion) {

        mRegionModels = filteredRegion;
        notifyDataSetChanged();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // pc - popular_category

        public TextView stateName;

        public MyViewHolder(View view) {
            super(view);
            stateName = (TextView) view.findViewById(R.id.stateName);
        }
    }


    public RegionAdapter(Context context, List<RegionModel> mRegionModels) {
        this.context = context;
        this.mRegionModels = mRegionModels;
        this.context = context;
        mLocationActivity = (LocationActivity) context;
        //  this.mStateIDS = mStateIDS;
        // System.out.println("rahul : StateAdapter : context  :StateAdapter Size : " + mStateIDS.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_recyclerview_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        System.out.println("rahul : StateAdapter : onBindViewHolder ");

        final RegionModel mCity = mRegionModels.get(position);
        holder.stateName.setText(mCity.getName());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mLocationActivity.tvChooseRegion.setText(mCity.getName());
                mLocationActivity.mDialogState.dismiss();
                mLocationActivity.mRegionId = mCity.getId();
                mLocationActivity.mRegionName = mCity.getName();
                AppUtils.REGION_ID = mCity.getId();
                System.out.println("region_id : " + AppUtils.REGION_ID);
                AppUtils.enableButtons(mLocationActivity.btnShowRestaurants);
//                AppUtils.disableButtons(mLocationActivity.btnShowAllRestaurants);

            }
        });

       /* final StateID mStateID = mStateIDS.get(position);

        holder.stateName.setText(mStateID.getState_name());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAddNewAddressDeliveryPage.state.setText(mStateID.getState_name().toString());

                    mAddNewAddressDeliveryPage.mDialogState.dismiss();
            *//*    System.out.println("Rahul : product_id : "+mBest_seller.getProduct_id() );
                Intent intent = new Intent(context, ProductDetailPage.class);
                intent.putExtra("product_id",""+mBest_seller.getProduct_id());
                intent.putExtra("image_url",mBest_seller.getImageUrl());
                Constants.PRODUCT_ID= String.valueOf(mBest_seller.getProduct_id());
                context.startActivity(intent);*//*
                // mMainActivity.overridePendingTransition(R.anim.popup_hide, R.anim.popup_show);
            }
        });*/
    }


    @Override
    public int getItemCount() {
        return mRegionModels.size();
    }


}
