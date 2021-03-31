package com.afieat.ini.models;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Harsh Rastogi on 17/01/18.
 */

public class Search {
    private int merchant_id;
    private String status;
    private String restaurant_name;
    private String restaurant_name_ar;
    private String item_id;
    private String item_name;
    private String item_name_ar;
    private String cuisine_id;
    private String photo;
    private String cuisine_name;
    private String cuisine_name_ar;
    private String index;
    public List<Price> prices;
    private String city_id;
    private String region_id;

/*
    public Search(int merchant_id, String status, String restaurant_name, String restaurant_name_ar, String item_id, String item_name, String item_name_ar, String cuisine_id, String cuisine_name, String cuisine_name_ar) {

        this.merchant_id = merchant_id;
        this.status = status;
        this.restaurant_name = restaurant_name;
        this.restaurant_name_ar = restaurant_name_ar;
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_name_ar = item_name_ar;
        this.cuisine_id = cuisine_id;
        this.cuisine_name = cuisine_name;
        this.cuisine_name_ar = cuisine_name_ar;
    }
*/

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getIndex() {
        return index;
    }

    public int getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(int merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getRestaurant_name_ar() {
        return restaurant_name_ar;
    }

    public void setRestaurant_name_ar(String restaurant_name_ar) {
        this.restaurant_name_ar = restaurant_name_ar;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_name_ar() {
        return item_name_ar;
    }

    public void setItem_name_ar(String item_name_ar) {
        this.item_name_ar = item_name_ar;
    }

    public String getCuisine_id() {
        return cuisine_id;
    }

    public void setCuisine_id(String cuisine_id) {
        this.cuisine_id = cuisine_id;
    }

    public String getCuisine_name() {
        return cuisine_name;
    }

    public void setCuisine_name(String cuisine_name) {
        this.cuisine_name = cuisine_name;
    }

    public String getCuisine_name_ar() {
        return cuisine_name_ar;
    }

    public void setCuisine_name_ar(String cuisine_name_ar) {
        this.cuisine_name_ar = cuisine_name_ar;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

    /*  public Search(int merchant_id, String status, String restaurant_name, String restaurant_name_ar, String item_id, String item_name, String item_name_ar, String cuisine_id, String cuisine_name, String cuisine_name_ar) {

        this.merchant_id = merchant_id;
        this.status = status;
        this.restaurant_name = restaurant_name;
        this.restaurant_name_ar = restaurant_name_ar;
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_name_ar = item_name_ar;
        this.cuisine_id = cuisine_id;
        this.cuisine_name = cuisine_name;
        this.cuisine_name_ar = cuisine_name_ar;
    }*/

    @Override
    public String toString() {
        return "Search{" +
                "merchant_id=" + merchant_id +
                ", status='" + status + '\'' +
                ", restaurant_name='" + restaurant_name + '\'' +
                ", restaurant_name_ar='" + restaurant_name_ar + '\'' +
                ", item_id='" + item_id + '\'' +
                ", item_name='" + item_name + '\'' +
                ", item_name_ar='" + item_name_ar + '\'' +
                ", cuisine_id='" + cuisine_id + '\'' +
                ", photo='" + photo + '\'' +
                ", cuisine_name='" + cuisine_name + '\'' +
                ", cuisine_name_ar='" + cuisine_name_ar + '\'' +
                ", index='" + index + '\'' +
                ", prices=" + prices +
                '}';
    }

    public Search parse(JSONObject hit) {
        return null;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public class Price{

       private String price;

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }

}
