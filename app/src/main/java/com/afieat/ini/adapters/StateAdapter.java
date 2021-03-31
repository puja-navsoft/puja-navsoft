package com.afieat.ini.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afieat.ini.LocationActivity;
import com.afieat.ini.R;
import com.afieat.ini.models.CityModel;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;

import java.util.List;

public class StateAdapter extends RecyclerView.Adapter<StateAdapter.MyViewHolder> {

    private List<CityModel> mCities;
    private Context context;
    LocationActivity mLocationActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // pc - popular_category

        public TextView stateName;

        public MyViewHolder(View view) {
            super(view);
            stateName = (TextView) view.findViewById(R.id.stateName);


        }
    }


    public StateAdapter(Context context, List<CityModel> mCities) {
        this.context = context;
        this.mCities = mCities;
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

        final CityModel mCity = mCities.get(position);
        holder.stateName.setText(mCity.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("StateAdapter : " + mCity.getName());
                System.out.println("StateAdapter : " + mLocationActivity.tvChooseCity.getText().toString());
                if (!mLocationActivity.tvChooseCity.getText().toString().equalsIgnoreCase(mCity.getName())) {
                    System.out.println("StateAdapter : " + mCity.getName());
                    if (AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {
                        mLocationActivity.tvChooseRegion.setText("اختر منطقتك");
                    } else {
                        mLocationActivity.tvChooseRegion.setText("Choose Your Region");

                    }
                    mLocationActivity.mRegionName = "";
                    mLocationActivity.mRegionId = "";
                }
                mLocationActivity.tvChooseCity.setText(mCity.getName());

                mLocationActivity.mDialogState.dismiss();
                mLocationActivity.mCityId = mCity.getId();
                mLocationActivity.mCityName = mCity.getName();
//                mLocationActivity.btnShowAllRestaurants.setEnabled(true);
//                AppUtils.enableButtons(mLocationActivity.btnShowAllRestaurants);
                AppUtils.disableButtons(mLocationActivity.btnShowRestaurants);
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
        return mCities.size();
    }


}
