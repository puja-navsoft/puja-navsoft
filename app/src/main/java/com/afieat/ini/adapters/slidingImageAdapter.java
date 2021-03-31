package com.afieat.ini.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afieat.ini.BasketActivity2;
import com.afieat.ini.OrderDetailsActivity;
import com.afieat.ini.R;
import com.afieat.ini.RestaurantListActivity;
import com.afieat.ini.database.DBHelper;
import com.afieat.ini.fragments.CategoryofFoodFragment;
import com.afieat.ini.models.InProcessOrders;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class slidingImageAdapter extends PagerAdapter {
 
 
    private ArrayList<Integer> IMAGES;
    private LayoutInflater inflater;
    private Context mycontext;
    private ArrayList<InProcessOrders> arrInProcess;
    private DBHelper db;
    private int DBitemCount;
    private int totalListCount=0;
 
 
    public slidingImageAdapter(Context contex,ArrayList<InProcessOrders> arrInProcess) {
        this.mycontext =contex;
        this.arrInProcess=arrInProcess;
        inflater = LayoutInflater.from(mycontext);
        db = new DBHelper(contex);
        DBitemCount = db.getFoodsBasket(AppInstance.getInstance(contex).getFromSharedPref(AppUtils.PREF_USER_ID)).size();
    }
 
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
 
    @Override
    public int getCount() {
        if(DBitemCount>0){
            totalListCount=arrInProcess.size()+1;
        }
        else{
            totalListCount=arrInProcess.size();

        }
        return totalListCount;

    }
 
    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View Layout = inflater.inflate(R.layout.order_cart_view, view, false);
        assert Layout != null;
        CircleImageView imgRest=(CircleImageView)Layout.findViewById(R.id.imgRest);
        TextView restaurantName=(TextView)Layout.findViewById(R.id.restaurantName);
        TextView txtCartMsg=(TextView)Layout.findViewById(R.id.txtCartMsg);
        TextView txtViewCart=(TextView)Layout.findViewById(R.id.txtViewCart);
        FloatingActionButton imgDelete=(FloatingActionButton)Layout.findViewById(R.id.imgDelete);
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    AppUtils.IS_CART_VISIBLE="false";
                    Fragment fragment = ((AppCompatActivity) mycontext).getSupportFragmentManager().findFragmentByTag("CategoryFrag");
                    if(fragment!=null && fragment instanceof CategoryofFoodFragment){
                        ((CategoryofFoodFragment) fragment).hideCartView();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
               try{
                   ((RestaurantListActivity) mycontext).hideCartView();
               }
               catch (Exception e){
                   e.printStackTrace();
               }

            }
        });

        if(position==totalListCount-1 && DBitemCount>0){//From DataBase
            txtCartMsg.setText(mycontext.getResources().getString(R.string.cart_save_item));
           Glide.with(mycontext)
                    .load(AppInstance.getInstance(mycontext).getCurrentResImage())
                    .placeholder(R.drawable.placeholder_land)
                    .into(imgRest);
               if ("ar".equals(AppInstance.getInstance(mycontext).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                restaurantName.setText(AppInstance.getInstance(mycontext).getCurrentResNameAR());
               }
             else {
                restaurantName.setText(AppInstance.getInstance(mycontext).getCurrentResName());
                 }
             txtViewCart.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent = new Intent(mycontext, BasketActivity2.class);
                     intent.putExtra("MinPrice", AppInstance.getInstance(mycontext).getFromSharedPref("min_price"));
                     mycontext.startActivity(intent);
                 }
             });
        }
        else{//From Api
             //im=Apis.IMG_PATH + "restaurants/image/thumb_300_300/"+arrInProcess.get(position).getMerchant_photo_bg();
            Glide.with(mycontext)
                    .load(Apis.IMG_PATH + "restaurants/image/thumb_300_300/"+arrInProcess.get(position).getMerchant_photo_bg())
                    .placeholder(R.drawable.placeholder_land)
                    .into(imgRest);
            if ("ar".equals(AppInstance.getInstance(mycontext).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
                restaurantName.setText(arrInProcess.get(position).getRestaurant_name_ar());
                if(arrInProcess.get(position).getStatus().trim().equalsIgnoreCase("In Process")){
                    txtCartMsg.setText(mycontext.getResources().getString(R.string.orderProcess));
                }
                else if(arrInProcess.get(position).getStatus().trim().equalsIgnoreCase("In the way")){
                    txtCartMsg.setText(mycontext.getResources().getString(R.string.orderonway));
                }

            }
            else {
                restaurantName.setText(arrInProcess.get(position).getMerchant_name());
                String txtStatus=arrInProcess.get(position).getStatus();
                if(arrInProcess.get(position).getStatus().trim().equalsIgnoreCase("In Process")){
                    txtStatus=mycontext.getResources().getString(R.string.txtInProcess);
                }
                else if(arrInProcess.get(position).getStatus().trim().equalsIgnoreCase("In the way")){
                    txtStatus=mycontext.getResources().getString(R.string.txtIntheway);
                }
                txtCartMsg.setText(mycontext.getResources().getString(R.string.cart_txt_item)+" "+txtStatus);
            }

            txtViewCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mycontext.startActivity(new Intent(mycontext, OrderDetailsActivity.class).putExtra(AppUtils.EXTRA_ORDER_ID, arrInProcess.get(position).getOrder_id()));

                }
            });
        }

        view.addView(Layout, 0);
 
        return Layout;
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
 
    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }
 
    @Override
    public Parcelable saveState() {
        return null;
    }
 
 
}