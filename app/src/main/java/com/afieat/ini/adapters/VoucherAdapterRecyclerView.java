package com.afieat.ini.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.VoucherActivity;
import com.afieat.ini.interfaces.OnBottomReachedListener;
import com.afieat.ini.interfaces.VoucherListener;
import com.afieat.ini.models.Vouchers;
import com.bumptech.glide.Glide;

import java.util.List;

public class VoucherAdapterRecyclerView extends RecyclerView.Adapter<VoucherAdapterRecyclerView.MyViewHolder> {

    private final VoucherListener mVoucherListener;
    OnBottomReachedListener onBottomReachedListener;
    // private OnVouchersPhotoClicked mListener;
    private List<Vouchers> Voucherss;
    private Context context;
    int lastPosition = -1;
    VoucherActivity mVoucherActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView voucherTitle, voucherStatus, voucherRefOrderNum, tvReedembtn, voucherDiscountAmt, voucherExpiry;
        ImageView barcodeImage;


        public MyViewHolder(View view) {
            super(view);


            voucherTitle = view.findViewById(R.id.voucherTitle);
            barcodeImage = view.findViewById(R.id.barcodeImage);
            voucherStatus = view.findViewById(R.id.voucherStatus);
            voucherRefOrderNum = view.findViewById(R.id.voucherRefOrderNum);
            tvReedembtn = view.findViewById(R.id.tvReedembtn);
            voucherDiscountAmt = view.findViewById(R.id.voucherDiscountAmt);
            voucherExpiry = view.findViewById(R.id.voucherExpiry);


        }


    }


    public VoucherAdapterRecyclerView(Context context, List<Vouchers> Voucherss, VoucherActivity mVoucherActivity, VoucherListener mVoucherListener, OnBottomReachedListener onBottomReachedListener) {
        this.Voucherss = Voucherss;
        this.context = context;
        this.mVoucherActivity = mVoucherActivity;
        this.mVoucherListener = mVoucherListener;
        this.onBottomReachedListener = onBottomReachedListener;
        System.out.println("Rahul : RestaurentListAdapterRecyclerView : lenres : " + Voucherss.size()
        );
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.voucher_row_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        System.out.println("rahul : Popular_Category_Adapter : onBindViewHolder ");

        final Vouchers Vouchers = Voucherss.get(position);

        holder.voucherTitle.setText(Vouchers.getVoucher_Restaurant_Name());
        holder.voucherRefOrderNum.setText(Vouchers.getVoucher_ref_order_no());
        holder.voucherStatus.setText(Vouchers.getVoucher_isRedeemed());
        holder.voucherDiscountAmt.setText(Vouchers.getVoucher_discount_amt());

        String exp = Vouchers.getVoucher_Expiration();
        exp = exp.replace("/", "-");
        holder.voucherExpiry.setText(exp);


        if (Vouchers.getVoucher_isRedeemed().equalsIgnoreCase("Active")) {
            holder.voucherStatus.setTextColor(ContextCompat.getColor(context, R.color.greenButton));

            //   holder.voucherStatus.setText("Redeemed"+"-"+Vouchers.getVoucher_isRedeemed());


        } else {
            holder.voucherStatus.setTextColor(ContextCompat.getColor(context, R.color.orangeButton));

            //  holder.voucherStatus.setText("Active"+"-"+Vouchers.getVoucher_isRedeemed());
        }

        Glide
                .with(context)
                .load(Vouchers.getRestaurant_thumb_img())
                .placeholder(R.drawable.placeholder_land)
                .into(holder.barcodeImage);


        if (position == Voucherss.size() - 1) {

            //System.out.println("Rahul : onBottomReached : position :"+position);
            mVoucherActivity.onBottomReachedR();
            //  onBottomReachedListener.onBottomReached(position);

        }

        if (position > lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
        ViewCompat.setTransitionName(holder.barcodeImage, Vouchers.getVoucher_Name());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mVoucherListener.onVoucherClickListner(Vouchers, holder.getAdapterPosition(), holder.barcodeImage);
                //   mSharedAnimItemClickListener.onAnimalItemClick(holder.getAdapterPosition(), logoPath.toString(), holder.ivVouchersLogo, Vouchers.getId(),Vouchers.getName());

                //mVouchersListActivity.VouchersListItemClick(Vouchers.getId(),holder.ivVouchersLogo,logoPath);
            }
        });
    }


    @Override
    public int getItemCount() {
        return Voucherss.size();
    }


//name(varchar), mobile(varchar), address(varchar), email(varchar), city(varchar), stateId(varchar), zipcode(int), set_primary(int), countryId(int),customerId(int)


}
