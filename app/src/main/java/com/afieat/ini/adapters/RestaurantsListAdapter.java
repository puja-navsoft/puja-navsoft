package com.afieat.ini.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.interfaces.OnRestaurantPhotoClicked;
import com.afieat.ini.models.Restaurant;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by amartya on 14/07/16 with love.
 */
public class RestaurantsListAdapter extends BaseAdapter {

    private OnRestaurantPhotoClicked mListener;
    private List<Restaurant> restaurants;
    private Context context;
    private int lastPosition = -1;

    public RestaurantsListAdapter(List<Restaurant> restaurants, Context context) {
        this.restaurants = restaurants;
        this.context = context;
    }

    @Override
    public int getCount() {
        return restaurants.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        View mConverterView=convertView;
        RelativeLayout rlDiscount;
        TextView tvTitle,
                tvAddress,
                tvCuisine,
                tvDeliveryTime,
                tvDiscount,
                tvStatus,
                tvRating,
                TXT_ProcesingTime,
                totalRating,
                processingTime,
                deliveryTime,
                ratingSingle;
        RatingBar ratingRestaurant;
        ImageView ivRestaurantLogo;
        if (mConverterView == null) {

            mConverterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_restaurant_list_item, parent, false);

        }
        rlDiscount = (RelativeLayout) mConverterView.findViewById(R.id.rlDiscount);
        tvTitle = (TextView) mConverterView.findViewById(R.id.tvTitle);
        tvAddress = (TextView) mConverterView.findViewById(R.id.tvAddress);
        ivRestaurantLogo = (ImageView) mConverterView.findViewById(R.id.ivRestaurantLogo);
        tvCuisine = (TextView) mConverterView.findViewById(R.id.tvCuisine);
        tvDeliveryTime = (TextView) mConverterView.findViewById(R.id.tvDeliveryTime);
        tvDiscount = (TextView) mConverterView.findViewById(R.id.tvDiscount);
        tvStatus = (TextView) mConverterView.findViewById(R.id.tvStatus);
        tvRating = (TextView) mConverterView.findViewById(R.id.tvRating);
        TXT_ProcesingTime = (TextView) mConverterView.findViewById(R.id.TXT_ProcesingTime);
        ratingRestaurant = (RatingBar) mConverterView.findViewById(R.id.ratingRestaurant);

        totalRating = (TextView) mConverterView.findViewById(R.id.totalRating);
        processingTime = (TextView) mConverterView.findViewById(R.id.procesingTime);
        deliveryTime = (TextView) mConverterView.findViewById(R.id.deliveryTime);
        ratingSingle = (TextView) mConverterView.findViewById(R.id.ratingSingle);


        Restaurant restaurant = restaurants.get(position);

        tvTitle.setText(restaurant.getName());
        tvAddress.setText(restaurant.getAddress());
        if (!"".equals(restaurant.getDiscount())) {
            int dimen = AppUtils.convertDpToPixel(36, parent.getContext());
            rlDiscount.setLayoutParams(new LinearLayout.LayoutParams(dimen, dimen));
            tvDiscount.setText("-" + restaurant.getDiscount() + "%");
        } else {
            rlDiscount.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }

//        tvStatus.setText(restaurant.getStatus());
        if ("Open".equals(restaurant.getStatus())) {
            if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                tvStatus.setText("فتح");
            } else {
                tvStatus.setText("Open");
            }
            tvStatus.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.greenButton));
        } else if ("Close".equals(restaurant.getStatus())) {
            tvStatus.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.orangeButton));
            if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                tvStatus.setText("مغلق");
            } else {
                tvStatus.setText("Close");
            }
        } else if ("Closed".equals(restaurant.getStatus())) {
            tvStatus.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.orangeButton));
            if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                tvStatus.setText("مغلق");
            } else {
                tvStatus.setText("Closed");
            }
        } else {
            tvStatus.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.orangeButton));
            if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                tvStatus.setText("مشغول");
            } else {
                tvStatus.setText("Busy");
            }
        }
        tvCuisine.setText(restaurant.getCuisine().trim());
        if (restaurant.getDeliveryTime().trim().length() > 0) {
            tvDeliveryTime.setText(AppUtils.changeToArabic(restaurant.getDeliveryTime(), parent.getContext()) + parent.getContext().getString(R.string.min_delivery));
            deliveryTime.setText(restaurant.getDeliveryTime() + "\nDelivery Time");
        }

        if (restaurant.getProcessingTime().trim().length() > 0) {
            TXT_ProcesingTime.setText(AppUtils.changeToArabic(restaurant.getProcessingTime(),
                    parent.getContext()) + " " + parent.getContext().getString(R.string.min_procesing));
            processingTime.setText(restaurant.getProcessingTime() + "\nProcessing Time");

        }

        final Uri logoPath = Uri.parse(restaurant.getUriThumb());
        //   ivRestaurantLogo.setImageURI(logoPath);

        Glide
                .with(context)
                .load(logoPath)
                .placeholder(R.drawable.placeholder_land)
                .into(ivRestaurantLogo);

        tvRating.setText("(" + AppUtils.changeToArabic(restaurant.getRatingCount(), parent.getContext()) + ")");
        totalRating.setText(restaurant.getRatingCount() + "\nRatings");


        ratingRestaurant.setRating(Float.parseFloat(restaurant.getRating()));
        ratingSingle.setText(restaurant.getRating());
        /*if (!restaurant.getName().equals("Kabab Hut")) {
            ivRestaurantLogo.setImageURI(Uri.parse(restaurant.getUriThumb()));
        }*/

        ivRestaurantLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = logoPath.toString();
                AppUtils.log("PATH: " + path);
                if (path.contains("81_81")) {
                    path = path.replace("81_81", "300_300");
                }
                AppUtils.log("NEW PATH: " + path);
                Uri pathUri = Uri.parse(path);
                mListener.onPhotoClicked(pathUri);
            }
        });

        if (position > lastPosition) {

            System.out.println("Rahul : RestaurantsAdapter : position : " + position);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
            ivRestaurantLogo.startAnimation(animation);
            lastPosition = position;
        }

        return mConverterView;
    }

    public void setOnRestaurantPhotoClicked(OnRestaurantPhotoClicked listener) {
        this.mListener = listener;
    }
}
