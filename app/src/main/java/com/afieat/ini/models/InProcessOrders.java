package com.afieat.ini.models;

public class InProcessOrders {

    /**
     * merchant_id : 42
     * restaurant_slug : demo-restaurant
     * merchant_name : Demo R'estaurant
     * restaurant_name_ar : مطعم التجريبي
     * merchant_photo_bg : 1500365073-4.JPG
     * order_id : 1244
     * order_number : 020042000448
     * stats_id : 1
     * status : In Process
     */

    private String merchant_id;
    private String restaurant_slug;
    private String merchant_name;
    private String restaurant_name_ar;
    private String merchant_photo_bg;
    private String order_id;
    private String order_number;
    private String stats_id;
    private String status;

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getRestaurant_slug() {
        return restaurant_slug;
    }

    public void setRestaurant_slug(String restaurant_slug) {
        this.restaurant_slug = restaurant_slug;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }

    public String getRestaurant_name_ar() {
        return restaurant_name_ar;
    }

    public void setRestaurant_name_ar(String restaurant_name_ar) {
        this.restaurant_name_ar = restaurant_name_ar;
    }

    public String getMerchant_photo_bg() {
        return merchant_photo_bg;
    }

    public void setMerchant_photo_bg(String merchant_photo_bg) {
        this.merchant_photo_bg = merchant_photo_bg;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getStats_id() {
        return stats_id;
    }

    public void setStats_id(String stats_id) {
        this.stats_id = stats_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
