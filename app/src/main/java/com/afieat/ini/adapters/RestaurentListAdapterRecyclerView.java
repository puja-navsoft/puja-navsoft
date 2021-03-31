package com.afieat.ini.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import com.afieat.ini.DetailActivityClick_Page;
import com.afieat.ini.R;
import com.afieat.ini.RestaurantListActivity;
import com.afieat.ini.interfaces.OnBottomReachedListener;
import com.afieat.ini.interfaces.OnRestaurantPhotoClicked;
import com.afieat.ini.interfaces.SharedAnimItemClickListener;
import com.afieat.ini.models.Restaurant;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

public class RestaurentListAdapterRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final SharedAnimItemClickListener mSharedAnimItemClickListener;
    OnBottomReachedListener onBottomReachedListener;
    private OnRestaurantPhotoClicked mListener;
    private List<Restaurant> restaurants;
    private Context context;
    int lastPosition = -1;
    RestaurantListActivity mRestaurantListActivity;
    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        System.out.println("rahul : Popular_Category_Adapter : onBindViewHolder ");
        if (position == restaurants.size() - 1) {

            mRestaurantListActivity.onBottomReachedR(position);


        }
        final Restaurant restaurant = restaurants.get(position);
        if (holder.getItemViewType() == TYPE_TWO) {
            if (position == restaurants.size() - 1) {

                //System.out.println("Rahul : onBottomReached : position :"+position);
                mRestaurantListActivity.onBottomReachedR(position);
                //  onBottomReachedListener.onBottomReached(position);

            }

            System.out.println("Rahul : RestaurentListAdapterRecyclerView : name : " + restaurant.getName());
            System.out.println("Rahul : RestaurentRatingCheck : Rating : " + restaurant.getRating());
            ((MyViewHolder) holder).tvTitle.setText(restaurant.getName());
            ((MyViewHolder) holder).tvAddress.setText(restaurant.getAddress());
            //holder.tvDiscount.setText("-" + restaurant.getDiscount() + "%");

            if (!"".equals(restaurant.getDiscount())) {
                int dimen = AppUtils.convertDpToPixel(36, context);
                ((MyViewHolder) holder).rlDiscount.setLayoutParams(new LinearLayout.LayoutParams(dimen, dimen));
                ((MyViewHolder) holder).tvDiscount.setText("-" + restaurant.getDiscount() + "%");
            } else {
                ((MyViewHolder) holder).rlDiscount.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            }

//        tvStatus.setText(restaurant.getStatus());
            if ("Open".equals(restaurant.getStatus())) {
                if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    ((MyViewHolder) holder).tvStatus.setText("فتح");
                } else {
                    ((MyViewHolder) holder).tvStatus.setText("Open");
                }
                ((MyViewHolder) holder).tvStatus.setTextColor(ContextCompat.getColor(context, R.color.greenButton));
            } else if (restaurant.getStatus().equals("Close")) {
                ((MyViewHolder) holder).tvStatus.setTextColor(ContextCompat.getColor(context, R.color.orangeButton));
                if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    ((MyViewHolder) holder).tvStatus.setText("مغلق");
                } else {
                    ((MyViewHolder) holder).tvStatus.setText("Close");
                }
            } else if ("Closed".equals(restaurant.getStatus())) {
                ((MyViewHolder) holder).tvStatus.setTextColor(ContextCompat.getColor(context, R.color.orangeButton));
                if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    ((MyViewHolder) holder).tvStatus.setText("مغلق");
                } else {
                    ((MyViewHolder) holder).tvStatus.setText("Closed");
                }
            } else {
                ((MyViewHolder) holder).tvStatus.setTextColor(ContextCompat.getColor(context, R.color.orangeButton));
                if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    ((MyViewHolder) holder).tvStatus.setText("مشغول");
                } else {
                    ((MyViewHolder) holder).tvStatus.setText("Busy");
                }
            }
            ((MyViewHolder) holder).tvCuisine.setText(restaurant.getCuisine().trim());
            if (restaurant.getDeliveryTime().trim().length() > 0) {
                //holder.tvDeliveryTime.setText(AppUtils.changeToArabic(restaurant.getDeliveryTime(), context) + context.getString(R.string.min_delivery));


                if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    String toArabic = AppUtils.changeToArabic(restaurant.getDeliveryTime(), context, true);
                    ((MyViewHolder) holder).deliveryTime.setText(Html.fromHtml("<span style='color:#000'><b>" + toArabic + " دقيقة</b></span>"));

                } else {
                    ((MyViewHolder) holder).deliveryTime.setText(Html.fromHtml("<span style='color:#000'><b>" + restaurant.getDeliveryTime() + " mins</b></span>"));

                }
            }

            if (restaurant.getProcessingTime().trim().length() > 0) {
                ((MyViewHolder) holder).TXT_ProcesingTime.setText(AppUtils.changeToArabic(restaurant.getProcessingTime(),
                        context) + " " + context.getString(R.string.min_procesing));
                if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                    String str = AppUtils.changeToArabic(restaurant.getProcessingTime(), context, true);
                    ((MyViewHolder) holder).processingTime.setText(Html.fromHtml("<span style='color:#000'><b>" + str + " دقيقة</b></span>"));


                } else {
                    ((MyViewHolder) holder).processingTime.setText(Html.fromHtml("<span style='color:#000'><b>" + restaurant.getProcessingTime() + " mins</b></span>"));

                }

            }


            if (restaurant.getRatingCount().length() > 0) {
                if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                    String str = AppUtils.changeToArabic(restaurant.getRatingCount(), context, true);
                    ((MyViewHolder) holder).totalRating.setText(Html.fromHtml("<span style='color:#000'><b>" + str + "</b></span>"));

                } else {
                    ((MyViewHolder) holder).totalRating.setText(Html.fromHtml("<span style='color:#000'><b>" + restaurant.getRatingCount() + "</b></span>"));
                }
            }

            final Uri logoPath = Uri.parse(restaurant.getUriThumb());
            System.out.println("Rahul : RestaurentListAdapterRecyclerView : logoPath : " + logoPath);
            //   ivRestaurantLogo.setImageURI(logoPath);

           /* Glide
                    .with(context)
                    .load(logoPath)
                    .placeholder(R.drawable.placeholder_land)
                    .into(((MyViewHolder) holder).ivRestaurantLogo);*/

            Glide
                    .with(context)
                    .load(logoPath)
                    .asBitmap()
                    .placeholder(R.drawable.placeholder_land)
                    .into(new SimpleTarget<Bitmap>(300, 300) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {

                            ((MyViewHolder) holder).ivRestaurantLogo.setImageBitmap(resource);
                            // setBackgroundImage(resource);
                        }
                    });


            ((MyViewHolder) holder).ivRestaurantLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap bitmap = ((BitmapDrawable) ((MyViewHolder) holder).ivRestaurantLogo.getDrawable()).getBitmap();
                    mRestaurantListActivity.setImageZoom(bitmap, logoPath.toString().replace("thumb_81_81", "thumb_300_300"));
                }
            });

            ((MyViewHolder) holder).tvRating.setText("(" + AppUtils.changeToArabic(restaurant.getRatingCount(), context) + ")");


            ((MyViewHolder) holder).ratingRestaurant.setRating(Float.parseFloat(restaurant.getRating()));

            if (restaurant.getRating() != null || !"".equals(restaurant.getRating().trim())) {
                System.out.println("Rahul :  :  " + restaurant.getRating() + " : " + restaurant.getName());

                if (Double.parseDouble(restaurant.getRating()) >= 4.5) {
                    ((MyViewHolder) holder).ratingSingle.setBackgroundResource(R.drawable.rating_above_4_5);
                    ((MyViewHolder) holder).ratingSingle.setVisibility(View.VISIBLE);
                } else if (Double.parseDouble(restaurant.getRating()) >= 4) {
                    ((MyViewHolder) holder).ratingSingle.setBackgroundResource(R.drawable.rating_above_4);
                    ((MyViewHolder) holder).ratingSingle.setVisibility(View.VISIBLE);
                } else if (Double.parseDouble(restaurant.getRating()) >= 3.5) {
                    ((MyViewHolder) holder).ratingSingle.setBackgroundResource(R.drawable.rating_above_3_5);
                    ((MyViewHolder) holder).ratingSingle.setVisibility(View.VISIBLE);
                } else if (Double.parseDouble(restaurant.getRating()) >= 3) {
                    ((MyViewHolder) holder).ratingSingle.setBackgroundResource(R.drawable.rating_above_3);
                    ((MyViewHolder) holder).ratingSingle.setVisibility(View.VISIBLE);
                } else if (Double.parseDouble(restaurant.getRating()) >= 2.5) {
                    ((MyViewHolder) holder).ratingSingle.setBackgroundResource(R.drawable.rating_above_2_5);
                    ((MyViewHolder) holder).ratingSingle.setVisibility(View.VISIBLE);
                } else if (Double.parseDouble(restaurant.getRating()) < 2.5) {
                    ((MyViewHolder) holder).ratingSingle.setBackgroundResource(R.drawable.rating_above_2_5);
                    ((MyViewHolder) holder).ratingSingle.setVisibility(View.GONE);

                }
                ((MyViewHolder) holder).ratingSingle.setPadding(10, 2, 10, 2);
                if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {

                    String str = AppUtils.changeToArabic(restaurant.getRating(), context, true);
                    ((MyViewHolder) holder).ratingSingle.setText(AppUtils.changeToArabic(str, context, true));


                } else {
                    ((MyViewHolder) holder).ratingSingle.setText(restaurant.getRating());

                }
            }

        /*if (!restaurant.getName().equals("Kabab Hut")) {
            ivRestaurantLogo.setImageURI(Uri.parse(restaurant.getUriThumb()));
        }*/

       /* holder.ivRestaurantLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           *//*     String path = logoPath.toString();
                AppUtils.log("PATH: " + path);
                if (path.contains("81_81")) {
                    path = path.replace("81_81", "300_300");
                }
                AppUtils.log("NEW PATH: "  + path);
                Uri pathUri = Uri.parse(path);
                mListener.onPhotoClicked(pathUri);*//*
            }
        });*/

            ImageView mImageImagView = ((MyViewHolder) holder).ivRestaurantLogo;
            if (position > lastPosition)

            {

                Animation animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
                holder.itemView.startAnimation(animation);
                lastPosition = position;
            }
            ViewCompat.setTransitionName(((MyViewHolder) holder).ivRestaurantLogo, restaurant.getName());

            final RecyclerView.ViewHolder finalHolder = holder;
            holder.itemView.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View view) {

        /*       DBHelper db = new DBHelper(context);
                if (AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG).equals("ar")) {
                    AppUtils.CURRENT_RESTAURANT_NAME = restaurant.getName_ar();

                    db.updateFoodBasketName(restaurant.getName_ar(),AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID));
                    // tvRestOpeningHours.setText(restaurant.getOpeningTime() + " إلى " + restaurant.getClosingTime());
                } else {
                    AppUtils.CURRENT_RESTAURANT_NAME = restaurant.getName();
                    db.updateFoodBasketName(restaurant.getName_ar(),AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_ID));

                    //tvRestOpeningHours.setText(restaurant.getOpeningTime() + " to " + restaurant.getClosingTime());
                }*/
                    mSharedAnimItemClickListener.onAnimalItemClick(finalHolder.getAdapterPosition(), logoPath.toString(), ((MyViewHolder) finalHolder).ivRestaurantLogo, restaurant.getId(), restaurant.getName());

                    //mRestaurantListActivity.RestaurantListItemClick(restaurant.getId(),holder.ivRestaurantLogo,logoPath);
                }
            });
        } else if (holder.getItemViewType() == TYPE_ONE) {

            Glide.with(context)
                    .load(Apis.AD_IMAGE_PATH + restaurants.get(position).getAddsBean().getAds_photo())
                    .placeholder(R.drawable.placeholder_land)
                    .into(((MyViewHolderAds) holder).imgAds);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (restaurants.get(position).getAddsBean().getLink_url() != null && !restaurants.get(position).getAddsBean().getLink_url().equals("")
                            && !restaurants.get(position).getAddsBean().getLink_type().equalsIgnoreCase("internal")
                            ) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(restaurants.get(position).getAddsBean().getLink_url()));
                        context.startActivity(browserIntent);

                    } else if (restaurants.get(position).getAddsBean().getLink_type().equalsIgnoreCase("internal")) {//internal
                        /*Intent intent = new Intent(context, RestaurantsDetailActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, restaurants.get(position).getAddsBean().getRestaurant_id());
                        intent.putExtra("from", "search");
                        */
                        Intent intent = new Intent(context, DetailActivityClick_Page.class);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("res_id", restaurants.get(position).getAddsBean().getRestaurant_id());
                        intent.putExtra("MinPrice", restaurants.get(position).getAddsBean().getMerchant_minimum_order());

                        if ("ar".equals(AppInstance.getInstance(context).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                            intent.putExtra("mRestaurantName", restaurants.get(position).getAddsBean().getRestaurant_name_ar());
                        } else {
                            intent.putExtra("mRestaurantName", restaurants.get(position).getAddsBean().getRestaurant_name());
                        }

                        intent.putExtra("page_to_call", "3");

                        context.startActivity(intent);
                    }
                }
            });
        }
    }

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

        public MyViewHolder(View view) {
            super(view);

            rlDiscount = view.findViewById(R.id.rlDiscount);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvAddress = view.findViewById(R.id.tvAddress);
            ivRestaurantLogo = view.findViewById(R.id.ivRestaurantLogo);
            tvCuisine = view.findViewById(R.id.tvCuisine);
            tvDeliveryTime = view.findViewById(R.id.tvDeliveryTime);
            tvDiscount = view.findViewById(R.id.tvDiscount);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvRating = view.findViewById(R.id.tvRating);
            TXT_ProcesingTime = view.findViewById(R.id.TXT_ProcesingTime);
            ratingRestaurant = view.findViewById(R.id.ratingRestaurant);

            totalRating = view.findViewById(R.id.totalRating);
            processingTime = view.findViewById(R.id.procesingTime);
            deliveryTime = view.findViewById(R.id.deliveryTime);
            ratingSingle = view.findViewById(R.id.ratingSingle);

        }

    }


    public RestaurentListAdapterRecyclerView(Context context,
                                             List<Restaurant> restaurants,
                                             RestaurantListActivity mRestaurantListActivity,
                                             SharedAnimItemClickListener mSharedAnimItemClickListener,
                                             OnBottomReachedListener onBottomReachedListeneronBottomReachedListener) {
        this.restaurants = restaurants;
        this.context = context;
        this.mRestaurantListActivity = mRestaurantListActivity;
        this.mSharedAnimItemClickListener = mSharedAnimItemClickListener;
        this.onBottomReachedListener = onBottomReachedListener;
        System.out.println("Rahul : RestaurentListAdapterRecyclerView : lenres : " + restaurants.size()
        );
    }

    // determine which layout to use for the row
    @Override
    public int getItemViewType(int position) {

        if (restaurants.get(position).getAddsBean() != null && !restaurants.get(position).getAddsBean().getAds_photo().equals("")) {
            return TYPE_ONE;
        } else {
            return TYPE_TWO;
        }
       /* if (position%5==0) {
            return TYPE_ONE;
        } else if (position>=0) {
            return TYPE_TWO;
        } else {
            return -1;
        }*/
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == TYPE_ONE) {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.layout_restaurant_list_item_ads, parent, false);

            return new MyViewHolderAds(itemView);
        } else {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.layout_restaurant_list_item, parent, false);

            return new MyViewHolder(itemView);
        }

    }

    public class MyViewHolderAds extends RecyclerView.ViewHolder {

        private ImageView imgAds;


        public MyViewHolderAds(View view) {
            super(view);

            imgAds = view.findViewById(R.id.imgAds);

        }

    }


    @Override
    public int getItemCount() {
        return restaurants.size();
    }


//name(varchar), mobile(varchar), address(varchar), email(varchar), city(varchar), stateId(varchar), zipcode(int), set_primary(int), countryId(int),customerId(int)


}
