package com.afieat.ini.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Navsoft on 26-03-2018.
 */

public class Vouchers implements Parcelable {

    private String voucherId;
    private String voucher_Name;
    private String voucher_Restaurant_Name;
    private String voucher_ref_order_no;
    private String voucher_discount_amt;
    private String voucher_img_url;
    private String voucher_isRedeemed;
    private String voucher_Expiration;
    private String voucher_status;
    private String restaurant_thumb_img;
    private String mVoucherShareLink;


    public Vouchers(String voucherId,
                    String voucher_Restaurant_Name,
                    String voucher_Name,
                    String voucher_ref_order_no,
                    String voucher_discount_amt,
                    String voucher_img_url,
                    String voucher_isRedeemed,
                    String voucher_Expiration,
                    String voucher_status,
                    String restaurant_thumb_img,
                    String mVoucherShareLink) {
        this.voucherId = voucherId;
        this.voucher_Name = voucher_Name;
        this.voucher_Restaurant_Name = voucher_Restaurant_Name;
        this.voucher_ref_order_no = voucher_ref_order_no;
        this.voucher_discount_amt = voucher_discount_amt;
        this.voucher_img_url = voucher_img_url;
        this.voucher_isRedeemed = voucher_isRedeemed;
        this.voucher_Expiration = voucher_Expiration;
        this.voucher_status = voucher_status;
        this.restaurant_thumb_img = restaurant_thumb_img;
        this.mVoucherShareLink = mVoucherShareLink;
    }


    protected Vouchers(Parcel in) {
        voucherId = in.readString();
        voucher_Name = in.readString();
        voucher_Restaurant_Name = in.readString();
        voucher_ref_order_no = in.readString();
        voucher_discount_amt = in.readString();
        voucher_img_url = in.readString();
        voucher_isRedeemed = in.readString();
        voucher_Expiration = in.readString();
        voucher_status = in.readString();
        restaurant_thumb_img = in.readString();
        mVoucherShareLink = in.readString();
    }

    public static final Creator<Vouchers> CREATOR = new Creator<Vouchers>() {
        @Override
        public Vouchers createFromParcel(Parcel in) {
            return new Vouchers(in);
        }

        @Override
        public Vouchers[] newArray(int size) {
            return new Vouchers[size];
        }
    };

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public String getVoucher_Name() {
        return voucher_Name;
    }

    public void setVoucher_Name(String voucher_Name) {
        this.voucher_Name = voucher_Name;
    }

    public String getVoucher_Restaurant_Name() {
        return voucher_Restaurant_Name;
    }

    public void setVoucher_Restaurant_Name(String voucher_Restaurant_Name) {
        this.voucher_Restaurant_Name = voucher_Restaurant_Name;
    }

    public String getVoucher_ref_order_no() {
        return voucher_ref_order_no;
    }

    public void setVoucher_ref_order_no(String voucher_ref_order_no) {
        this.voucher_ref_order_no = voucher_ref_order_no;
    }

    public String getVoucher_discount_amt() {
        return voucher_discount_amt;
    }

    public void setVoucher_discount_amt(String voucher_discount_amt) {
        this.voucher_discount_amt = voucher_discount_amt;
    }

    public String getVoucher_img_url() {
        return voucher_img_url;
    }

    public void setVoucher_img_url(String voucher_img_url) {
        this.voucher_img_url = voucher_img_url;
    }

    public String getVoucher_isRedeemed() {
        return voucher_isRedeemed;
    }

    public void setVoucher_isRedeemed(String voucher_isRedeemed) {
        this.voucher_isRedeemed = voucher_isRedeemed;
    }

    public String getVoucher_Expiration() {
        return voucher_Expiration;
    }

    public void setVoucher_Expiration(String voucher_Expiration) {
        this.voucher_Expiration = voucher_Expiration;
    }

    public String getVoucher_status() {
        return voucher_status;
    }

    public void setVoucher_status(String voucher_status) {
        this.voucher_status = voucher_status;
    }

    public String getRestaurant_thumb_img() {
        return restaurant_thumb_img;
    }

    public void setRestaurant_thumb_img(String restaurant_thumb_img) {
        this.restaurant_thumb_img = restaurant_thumb_img;
    }

    public String getmVoucherShareLink() {
        return mVoucherShareLink;
    }

    public void setmVoucherShareLink(String mVoucherShareLink) {
        this.mVoucherShareLink = mVoucherShareLink;
    }

    public static Creator<Vouchers> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {


        parcel.writeString(voucherId);
        parcel.writeString(voucher_Name);
        parcel.writeString(voucher_Restaurant_Name);
        parcel.writeString(voucher_ref_order_no);
        parcel.writeString(voucher_discount_amt);
        parcel.writeString(voucher_img_url);
        parcel.writeString(voucher_isRedeemed);
        parcel.writeString(voucher_Expiration);
        parcel.writeString(voucher_status);
        parcel.writeString(restaurant_thumb_img);
        parcel.writeString(mVoucherShareLink);

    }
}
