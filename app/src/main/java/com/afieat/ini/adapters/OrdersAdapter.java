package com.afieat.ini.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.models.Order;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppUtils;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by amartya on 13/07/16 with love.
 */
public class OrdersAdapter extends BaseAdapter {

    private List<Order> orders;
    private Context context;
    private int lastPosition = -1;

    public OrdersAdapter(List<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View mConverterView=convertView;

        if (mConverterView == null) {
            mConverterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_orders_row, parent, false);
        }
        ImageView ivRestaurantLogo = (ImageView) mConverterView.findViewById(R.id.ivRestaurantLogo);
        TextView tvMerchantName = (TextView) mConverterView.findViewById(R.id.tvMerchantName);
        TextView tvOrderQty = (TextView) mConverterView.findViewById(R.id.tvOrderQty);
        TextView tvOrderNo = (TextView) mConverterView.findViewById(R.id.tvOrderNo);
        TextView tvAmt = (TextView) mConverterView.findViewById(R.id.tvAmt);
        TextView tvDateTime = (TextView) mConverterView.findViewById(R.id.tvDateTime);
        TextView tvStatus = (TextView) mConverterView.findViewById(R.id.tvStatus);

        Order order = orders.get(position);

        //    ivRestaurantLogo.setImageURI(Uri.parse(Apis.IMG_PATH + "restaurants/image/thumb_81_81/" + order.getMerchantPic()));
        if (order.getOrderType().equalsIgnoreCase("1"))
            Glide
                    .with(context)
                    .load(R.drawable.delivery_type)
                    .placeholder(R.drawable.placeholder_land)
                    .fitCenter()
                    .into(ivRestaurantLogo);
        else
            Glide
                    .with(context)
                    .load(Uri.parse(Apis.IMG_PATH + "restaurants/image/thumb_81_81/" + order.getMerchantPic()))
                    .placeholder(R.drawable.placeholder_land)
                    .into(ivRestaurantLogo);
        tvMerchantName.setText(order.getMerchantName());
        tvOrderNo.setText(order.getOrderNo());
        tvAmt.setText(parent.getContext().getString(R.string.currency) + order.getAmtTotal());
        tvOrderQty.setText(AppUtils.changeToArabic(order.getQuantity(), parent.getContext()));

        String dateform = order.getDelDate();

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = inputFormat.parse(dateform);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateStr = outputFormat.format(date);
        tvDateTime.setText(outputDateStr + parent.getContext().getString(R.string.at) + AppUtils.changeToArabic(order.getDelTime(), parent.getContext()));

        //  tvDateTime.setText(order.getDelDate() + " at " + order.getDelTime());

        if ("1".equalsIgnoreCase(order.getStatus())) {
            tvStatus.setText(parent.getContext().getString(R.string.in_process));
            tvStatus.setTextColor(Color.parseColor("#666699"));
        } else if ("3".equalsIgnoreCase(order.getStatus())) {
            tvStatus.setText(parent.getContext().getString(R.string.delivered));
            tvStatus.setTextColor(Color.parseColor("#446600"));

        } else if ("2".equalsIgnoreCase(order.getStatus())) {
            tvStatus.setText(parent.getContext().getString(R.string.on_the_way));
            tvStatus.setTextColor(Color.parseColor("#00a8e6"));

        } else if ("4".equalsIgnoreCase(order.getStatus())) {
            tvStatus.setText(parent.getContext().getString(R.string.not_recivied));
            tvStatus.setTextColor(Color.parseColor("#ff471a"));

        } else if ("5".equalsIgnoreCase(order.getStatus())) {
            tvStatus.setText(parent.getContext().getString(R.string.cancelled_by_admin));
            tvStatus.setTextColor(Color.parseColor("#ff471a"));

        }

        if (position > lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
            mConverterView.startAnimation(animation);
            lastPosition = position;
        }
        return mConverterView;
    }
}
