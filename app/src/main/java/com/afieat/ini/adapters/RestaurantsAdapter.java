package com.afieat.ini.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.interfaces.OnLoadMoreListener;
import com.afieat.ini.interfaces.OnRecyclerItemClickListener;
import com.afieat.ini.models.Restaurant;
import com.afieat.ini.utils.AppUtils;

import java.util.List;

/**
 * Created by amartya on 06/04/16 with love.
 */
public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.CustomViewHolder> {

    private Context mContext;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private int lastPosition = -1;
    private OnRecyclerItemClickListener mItemClickListener;
    private OnLoadMoreListener mLoadMoreListener;


    private List<Restaurant> restaurants;

    public RestaurantsAdapter(Context context, List<Restaurant> restaurants) {
        this.mContext = context;
        this.restaurants = restaurants;
    }

    public void setOnItemClickListener(OnRecyclerItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mLoadMoreListener) {

        this.mLoadMoreListener = mLoadMoreListener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_restaurant_list_item, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.bind(restaurants.get(position), mItemClickListener, position, holder);

        if (position > lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.zoom_in);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }


    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rlDiscount;
        private TextView tvTitle, tvAddress, tvCuisine, tvDeliveryTime, tvDiscount;
        private ImageView ivRestaurantLogo;

        public CustomViewHolder(View itemView) {
            super(itemView);
            this.rlDiscount = (RelativeLayout) itemView.findViewById(R.id.rlDiscount);
            this.tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            this.tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            this.ivRestaurantLogo = (ImageView) itemView.findViewById(R.id.ivRestaurantLogo);
            this.tvCuisine = (TextView) itemView.findViewById(R.id.tvCuisine);
            this.tvDeliveryTime = (TextView) itemView.findViewById(R.id.tvDeliveryTime);
            this.tvDiscount = (TextView) itemView.findViewById(R.id.tvDiscount);
        }

        protected void bind(final Restaurant restaurant, final OnRecyclerItemClickListener listener, int position, CustomViewHolder holder) {
            tvTitle.setText(restaurant.getName());
            tvAddress.setText(restaurant.getAddress());
            if (!"".equals(restaurant.getDiscount())) {
                int dimen = AppUtils.convertDpToPixel(36, mContext);
                rlDiscount.setLayoutParams(new LinearLayout.LayoutParams(dimen, dimen));
                tvDiscount.setText("-" + restaurant.getDiscount() + "%");
            } else {
                rlDiscount.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            }
            tvCuisine.setText(restaurant.getCuisine());
            tvDeliveryTime.setText(restaurant.getDeliveryTime());
            if (!restaurant.getName().equals("Kabab Hut")) {
                ivRestaurantLogo.setImageURI(Uri.parse(restaurant.getUriThumb()));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRecyclerItemClicked(restaurant);
                }
            });

            if (position > lastPosition) {

                System.out.println("Rahul : RestaurantsAdapter : position : " + position);
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.up_from_bottom);
                holder.itemView.startAnimation(animation);
                lastPosition = position;
            }

        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }
}
