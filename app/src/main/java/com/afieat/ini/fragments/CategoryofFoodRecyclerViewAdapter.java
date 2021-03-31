package com.afieat.ini.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afieat.ini.DeliveryPointActivity;
import com.afieat.ini.DetailActivityClick_Page;
import com.afieat.ini.R;
import com.afieat.ini.fragments.dummy.FoodCategoryContent.CategoryItem;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CategoryItem} and makes a call to the
 * specified {@link     private final CategoryofFoodFragment.OnListOrderListener mListener;
 * }.
 * TODO: Replace the implementation with code for your data type.
 */
public class CategoryofFoodRecyclerViewAdapter extends RecyclerView.Adapter<CategoryofFoodRecyclerViewAdapter.ViewHolder> {

    private final List<CategoryItem> mValues;
    private final CategoryofFoodFragment.OnListOrderListener mListener;
    private Context mContext;
    private static final int MAINCATRGORY = 1;
    private static final int SUBCATEGORY = 2;
    private static final int ADS = 3;
    private int lastPosition = -1;

    public CategoryofFoodRecyclerViewAdapter(Context context, List<CategoryItem> items, CategoryofFoodFragment.OnListOrderListener listener) {
        mContext = context;
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MAINCATRGORY) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_categoryoffood, parent, false);
            return new ViewHolder(view);
        }
        else  if(viewType == ADS){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_subcategoryoffood, parent, false);
            return new ViewHolder(view);
        }
        else {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_subcategoryoffood, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);

        if(holder.getItemViewType() == ADS){
            holder.LL_mainview.setVisibility(View.GONE);

            Glide.with(mContext).load(Apis.AD_IMAGE_PATH + mValues.get(position).getAddsBean().getAds_photo()).placeholder(R.drawable.placeholder_land).into(holder.CatImages);
          //  holder.CatImages.setAdjustViewBounds(true);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mValues.get(position).getAddsBean().getLink_url() != null && !mValues.get(position).getAddsBean().getLink_url().equals("")
                            && !mValues.get(position).getAddsBean().getLink_type().equalsIgnoreCase("internal")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mValues.get(position).getAddsBean().getLink_url()));
                        mContext.startActivity(browserIntent);


                    } else if (mValues.get(position).getAddsBean().getLink_type().equalsIgnoreCase("internal")) {
                        /*Intent intent = new Intent(mContext, RestaurantsDetailActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, mValues.get(position).getAddsBean().getRestaurant_id());
                        intent.putExtra("from", "search");*/

                        Intent intent;
                        if (mValues.get(position).AddsBean.getRestaurant_slug().equalsIgnoreCase("driver-order")){
                            intent = new Intent(mContext, DeliveryPointActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        else {

                            intent = new Intent(mContext, DetailActivityClick_Page.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("res_id", mValues.get(position).getAddsBean().getRestaurant_id());
                            intent.putExtra("MinPrice", mValues.get(position).getAddsBean().getMerchant_minimum_order());
                            intent.putExtra("mRestaurantName", mValues.get(position).getAddsBean().getRestaurant_name());
                            if ("ar".equals(AppInstance.getInstance(mContext).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                                intent.putExtra("mRestaurantName", mValues.get(position).getAddsBean().getRestaurant_name_ar());
                            } else {
                                intent.putExtra("mRestaurantName", mValues.get(position).getAddsBean().getRestaurant_name());
                            }
                            intent.putExtra("page_to_call", "3");


                        }
                        mContext.startActivity(intent);
                    }
                }
            });
        }
        else{
            holder.LL_mainview.setVisibility(View.VISIBLE);
            if ("en".equals(AppInstance.getInstance(mContext).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                holder.mIdView.setText(mValues.get(position).getGroup_name());
            } else {
                holder.mIdView.setText(mValues.get(position).getGroup_name_ar());
            }
            holder.mContentView.setText(mValues.get(position).getResturantCount() + " " + mContext.getResources().getString(R.string.Resturants));
            holder.mContentView.setVisibility(View.GONE);

            String Path = Apis.IMG_PATH + "restaurants/group/thumb_400_400/" + mValues.get(position).getPhoto();
            AppUtils.log("@@@ APPATH-" + Path);
            if ("0".equals(mValues.get(position).getIs_featured()))
                Glide.with(mContext).load(Path).into(holder.CatImages);
            else
                Glide.with(mContext).load(Path).centerCrop().into(holder.CatImages);


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        System.out.println("Rahul : clicked :items : getGroup_id :"+mValues.get(position).getGroup_id());

                        if(("2".equals(mValues.get(position).getGroup_id())))
                        {
                            AppUtils.IS_SUPER_MARKET=true;
                        } else
                            AppUtils.IS_SUPER_MARKET = ("7".equalsIgnoreCase(mValues.get(position).getGroup_id()));

                        // mValues.get(position).getGroup_name()
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(holder.mItem);
                    }
                }
            });
            if (holder.getItemViewType() == MAINCATRGORY) {
                if ("en".equals(AppInstance.getInstance(mContext).getFromSharedPref(AppUtils.PREF_USER_LANG)))
                    holder.itemView.findViewById(R.id.IMG_Shadow).setRotation(0);
                else
                    holder.itemView.findViewById(R.id.IMG_Shadow).setRotation(180);
            }
        }

        if (position > lastPosition) {


            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.zoom_in);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
      //  holder.LL_mainview.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mValues.get(position).getAddsBean()!=null){
            return ADS;
        }
        else{

            if(mValues.get(position).getIs_featured().equals("1")){
                return MAINCATRGORY;
            }
            else{
                return SUBCATEGORY;
            }
           /* if (position < CategoryofFoodFragment.DivderListPositionno)
                return MAINCATRGORY;
            else
                return SUBCATEGORY;*/
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public CategoryItem mItem;
        private ImageView CatImages;
        public FrameLayout LL_mainview;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.id);
            mContentView = view.findViewById(R.id.content);
            CatImages = view.findViewById(R.id.IMG_CATG);
            LL_mainview = view.findViewById(R.id.LL_mainview);


        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
