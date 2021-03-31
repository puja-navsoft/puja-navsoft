package com.afieat.ini.interfaces;

import android.widget.ImageView;

import com.afieat.ini.models.Vouchers;

/**
 * Created by msc10 on 19/02/2017.
 */

public interface VoucherListener {
    void onVoucherClickListner(Vouchers mVouchers,int pos, ImageView shareImageView);
}