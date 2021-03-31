package com.afieat.ini.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afieat.ini.FavouriteListActivity;
import com.afieat.ini.R;
import com.afieat.ini.interfaces.FavouriteListner;
import com.afieat.ini.interfaces.OnBottomReachedListener;
import com.afieat.ini.interfaces.OnRestaurantPhotoClicked;
import com.afieat.ini.interfaces.SharedAnimItemClickListener;
import com.afieat.ini.models.Restaurant;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.bumptech.glide.Glide;

import java.util.List;

public class FavouriteListAdapter extends RecyclerView.Adapter<FavouriteListAdapter.MyViewHolder> {

    private final SharedAnimItemClickListener mSharedAnimItemClickListener;
    private OnBottomReachedListener onBottomReachedListener;
    private FavouriteListner mFavouriteListner;
    private OnRestaurantPhotoClicked mListener;
    private List<Restaurant> restaurants;
    private Context context;
    private int lastPosition = -1;
    private FavouriteListActivity mFavouriteListActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {


        private RelativeLayout rlDiscount;
        private TextView tvTitle,
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
        private RatingBar ratingRestaurant;
        private ImageView ivRestaurantLogo;
        private TextView removeFavBtn;

        public MyViewHolder(View view) {
            super(view);

            rlDiscount = (RelativeLayout) view.findViewById(R.id.rlDiscount);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvAddress = (TextView) view.findViewById(R.id.tvAddress);
            ivRestaurantLogo = (ImageView) view.findViewById(R.id.ivRestaurantLogo);
            tvCuisine = (TextView) view.findViewById(R.id.tvCuisine);
            tvDeliveryTime = (TextView) view.findViewById(R.id.tvDeliveryTime);
            tvDiscount = (TextView) view.findViewById(R.id.tvDiscount);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            tvRating = (TextView) view.findViewById(R.id.tvRating);
            TXT_ProcesingTime = (TextView) view.findViewById(R.id.TXT_ProcesingTime);
            ratingRestaurant = (RatingBar) view.findViewById(R.id.ratingRestaurant);

            totalRating = (TextView) view.findViewById(R.id.totalRating);
            processingTime = (TextView) view.findViewById(R.id.procesingTime);
            deliveryTime = (TextView) view.findViewById(R.id.deliveryTime);
            ratingSingle = (TextView) view.findViewById(R.id.ratingSingle);

            removeFavBtn = view.findViewById(R.id.removeFavBtn);
        }


    }


    public FavouriteListAdapter(Context context, List<Restaurant> restaurants, FavouriteListActivity mFavouriteListActivity, SharedAnimItemClickListener mSharedAnimItemClickListener, OnBottomReachedListener onBottomReachedListener, FavouriteListner mFavouriteListner) {
        this.restaurants = restaurants;
        this.context = context;
        this.mFavouriteListActivity = mFavouriteListActivity;
        this.mSharedAnimItemClickListener = mSharedAnimItemClickListener;
        this.onBottomReachedListener = onBottomReachedListener;
        this.mFavouriteListner = mFavouriteListner;
        System.out.println("Rahul : RestaurentListAdapterRecyclerView : lenres : " + restaurants.size()
        );
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.favourite_row_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        System.out.println("Rahul : Popular_Category_Adapter : onBindViewHolder ");

        final Restaurant restaurant = restaurants.get(position);


        if (position == restaurants.size() - 1) {

            //System.out.println("Rahul : onBottomReached : position :"+position);
            mFavouriteListActivity.onBottomReachedR();
            //  onBottomReachedListener.onBottomReached(position);

        }

        System.out.println("Rahul : RestaurentListAdapterRecyclerView : name : " + restaurant.getName());
        holder.tvTitle.setText(restaurant.getName());
        holder.tvAddress.setText(restaurant.getAddress());
        //holder.tvDiscount.setText("-" + restaurant.getDiscount() + "%");

        if (!"".equals(restaurant.getDiscount())) {
            int dimen = AppUtils.convertDpToPixel(36, context);
            holder.rlDiscount.setLayoutParams(new LinearLayout.LayoutParams(dimen, dimen));
            holder.tvDiscount.setText("-" + restaurant.getDiscount() + "%");
        } else {
            holder.rlDiscount.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }

//        tvStatus.setText(restaurant.getStatus());
        if ("Open".equals(restaurant.getStatus())) {
            if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                holder.tvStatus.setText("فتح");
            } else {
                holder.tvStatus.setText("Open");
            }
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.greenButton));
        } else if ("Close".equals(restaurant.getStatus())) {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.orangeButton));
            if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                holder.tvStatus.setText("مغلق");
            } else {
                holder.tvStatus.setText("Close");
            }
        } else if ("Closed".equals(restaurant.getStatus())) {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.orangeButton));
            if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                holder.tvStatus.setText("مغلق");
            } else {
                holder.tvStatus.setText("Closed");
            }
        } else {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.orangeButton));
            if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))){
                holder.tvStatus.setText("مشغول");
            } else {
                holder.tvStatus.setText("Busy");
            }
        }
        holder.tvCuisine.setText(restaurant.getCuisine().trim());
        if (restaurant.getDeliveryTime().trim().length() > 0) {
            holder.tvDeliveryTime.setText(AppUtils.changeToArabic(restaurant.getDeliveryTime(), context) + context.getString(R.string.min_delivery));
            holder.deliveryTime.setText(Html.fromHtml("<span style='color:#000'><b>" + restaurant.getDeliveryTime() + " mins</b></span>"));
        }

        if (restaurant.getProcessingTime().trim().length() > 0) {
            holder.TXT_ProcesingTime.setText(AppUtils.changeToArabic(restaurant.getProcessingTime(),
                    context) + " " + context.getString(R.string.min_procesing));
            holder.processingTime.setText(Html.fromHtml("<span style='color:#000'><b>" + restaurant.getProcessingTime() + " mins</b></span>"));

        }

        final Uri logoPath = Uri.parse(restaurant.getUriThumb());
        //   ivRestaurantLogo.setImageURI(logoPath);

        Glide
                .with(context)
                .load(logoPath)
                .placeholder(R.drawable.placeholder_land)
                .into(holder.ivRestaurantLogo);

        holder.tvRating.setText("(" + AppUtils.changeToArabic(restaurant.getRatingCount(), context) + ")");
        holder.totalRating.setText(Html.fromHtml("<span style='color:#000'><b>" + restaurant.getRatingCount() + "</b></span>"));


        holder.ratingRestaurant.setRating(Float.parseFloat(restaurant.getRating()));

        if (restaurant.getRating() != null || restaurant.getRating().trim()!="") {
            System.out.println("Rahul : restaurant.getRating :  " + restaurant.getRating());

            if (Double.parseDouble(restaurant.getRating()) >= 4.5) {
                holder.ratingSingle.setBackgroundResource(R.drawable.rating_above_4_5);
            } else if (Double.parseDouble(restaurant.getRating()) >= 4) {
                holder.ratingSingle.setBackgroundResource(R.drawable.rating_above_4);

            } else if (Double.parseDouble(restaurant.getRating()) >= 3.5) {
                holder.ratingSingle.setBackgroundResource(R.drawable.rating_above_3_5);

            } else if (Double.parseDouble(restaurant.getRating()) >= 3) {
                holder.ratingSingle.setBackgroundResource(R.drawable.rating_above_3);

            } else if (Double.parseDouble(restaurant.getRating()) >= 2.5) {
                holder.ratingSingle.setBackgroundResource(R.drawable.rating_above_2_5);

            } else if (Double.parseDouble(restaurant.getRating()) < 2.5) {
                holder.ratingSingle.setBackgroundResource(R.drawable.rating_above_2_5);
                holder.ratingSingle.setVisibility(View.GONE);

            }
            holder.ratingSingle.setPadding(10, 2, 10, 2);
            holder.ratingSingle.setText(restaurant.getRating());
        }
        /*if (!restaurant.getName().equals("Kabab Hut")) {
            ivRestaurantLogo.setImageURI(Uri.parse(restaurant.getUriThumb()));
        }*/

        holder.ivRestaurantLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           /*     String path = logoPath.toString();
                AppUtils.log("PATH: " + path);
                if (path.contains("81_81")) {
                    path = path.replace("81_81", "300_300");
                }
                AppUtils.log("NEW PATH: "  + path);
                Uri pathUri = Uri.parse(path);
                mListener.onPhotoClicked(pathUri);*/
            }
        });

        ImageView mImageImagView = holder.ivRestaurantLogo;
        if (position > lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
        ViewCompat.setTransitionName(holder.ivRestaurantLogo, restaurant.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSharedAnimItemClickListener.onAnimalItemClick(holder.getAdapterPosition(), logoPath.toString(), holder.ivRestaurantLogo, restaurant.getId(), restaurant.getName());

                //mFavouriteListActivity.RestaurantListItemClick(restaurant.getId(),holder.ivRestaurantLogo,logoPath);
            }
        });

        holder.removeFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFavouriteListner.onFavClickListner(restaurant, restaurants, position);

            }
        });
    }


    @Override
    public int getItemCount() {
        return restaurants.size();
    }


//name(varchar), mobile(varchar), address(varchar), email(varchar), city(varchar), stateId(varchar), zipcode(int), set_primary(int), countryId(int),customerId(int)


}
