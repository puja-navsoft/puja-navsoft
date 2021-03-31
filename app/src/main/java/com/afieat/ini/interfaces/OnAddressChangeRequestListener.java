package com.afieat.ini.interfaces;

/**
 * Created by amartya on 27/05/16 with love.
 */
public interface OnAddressChangeRequestListener {
    void onAddressChangeRequested(String adId);
    void onAddressSetDefaultRequested(String adId, int position);
    void onAddressDeleteRequested(String adId, int position);
}
