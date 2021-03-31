package com.afieat.ini.models;

import org.json.JSONArray;

/**
 * Created by amartya on 05/04/16 with love.
 */
public class Restaurant {
    private String id;
    private String name;
    private String name_ar;
    private String address;
    private String uriThumb;
    private String uriBg;
    private String cuisine;
    private String openingTime;
    private String closingTime;
    private String status;
    private String minOrder;
    private String deliveryTime;
    private String deliveryCharge;
    private String discount;
    private String rating, ratingCount;
    private String orderNumber;
    private String ProcessingTime;
    private String reviewCount;
    private String galleryCount;
    private boolean to_show_rating;
    private AddsBean AddsBean=null;

    public Restaurant.AddsBean getAddsBean() {
        return AddsBean;
    }

    public void setAddsBean(Restaurant.AddsBean addsBean) {
        AddsBean = addsBean;
    }

    public String getGalleryCount() {
        return galleryCount;
    }

    public void setGalleryCount(String galleryCount) {
        this.galleryCount = galleryCount;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public Restaurant() {
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getProcessingTime() {
        return ProcessingTime;
    }

    public void setProcessingTime(String processingTime) {
        ProcessingTime = processingTime;
    }

    public String getUriThumb() {
        return uriThumb;
    }

    public void setUriThumb(String uriThumb) {
        this.uriThumb = uriThumb;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address.trim();
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(JSONArray cuisineArray) {
        final int MAX = 6;
        int i = 0;
        this.cuisine = "";
        while (i < cuisineArray.length()) {
            this.cuisine += cuisineArray.optString(i++);
            if (i < MAX && i < cuisineArray.length()) {
                this.cuisine += ", ";
            }
            if (i == MAX) break;
        }
        if (i == MAX && cuisineArray.length() > MAX) {
            this.cuisine += " and " + (cuisineArray.length() - MAX) + " more";
        }
    }

    public void setCuisine(String cuisine) {
        String[] cuisinesArray = cuisine.split(",");
        final int MAX = 4;
        int index = 0;
        this.cuisine = "";
        for (String c : cuisinesArray) {
            index++;
            this.cuisine += c;
            if (index < MAX && index < cuisinesArray.length) {
                this.cuisine += ", ";
            }
            if (index == MAX) break;
        }
        if (index == MAX && cuisinesArray.length > MAX) {
            this.cuisine += " and " + /*(cuisinesArray.length-MAX) + */"more";
        }
    }

    public String getUriBg() {
        return uriBg;
    }

    public void setUriBg(String uriBg) {
        this.uriBg = uriBg;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        if (deliveryTime.trim().length() > 0 && deliveryTime.contains(":"))
            deliveryTime = deliveryTime.split(":")[1] + " min. delivery";
        this.deliveryTime = deliveryTime;
    }

    public String getMinOrder() {
        return minOrder;
    }

    public void setMinOrder(String minOrder) {
        this.minOrder = minOrder;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(String ratingCount) {
        this.ratingCount = ratingCount;
    }


    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getName_ar() {
        return name_ar;
    }

    public void setName_ar(String name_ar) {
        this.name_ar = name_ar;
    }

    public boolean isTo_show_rating() {
        return to_show_rating;
    }

    public void setTo_show_rating(boolean to_show_rating) {
        this.to_show_rating = to_show_rating;
    }

    public static class AddsBean {
        /**
         * adds_id : 52
         * restaurant_id : 0
         * item_id : 0
         * ads_photo : 1501583295-mango-juice-250x250.jpg
         * sponsored_start_date : 2017-08-01
         * sponsored_expiration : 2019-12-31
         * display_page_id : 2
         * status : publish
         * date_created : 2017-03-10 12:36:31
         * date_modified : 2019-01-10 16:13:37
         * ip_address : 172.31.6.101
         * is_appadds : 1
         * group_ids :
         * city_ids :
         * link_url : https://www.google.com
         */

        private String adds_id;
        private String restaurant_id;
        private String item_id;
        private String ads_photo;
        private String sponsored_start_date;
        private String sponsored_expiration;
        private String display_page_id;
        private String status;
        private String date_created;
        private String date_modified;
        private String ip_address;
        private String is_appadds;
        private String group_ids;
        private String city_ids;
        private String link_url;
        private String link_type = "";
        private String link_option = "";
        private String restaurant_name = "";
        private String restaurant_name_ar = "";
        private String merchant_minimum_order = "";

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

        public String getMerchant_minimum_order() {
            return merchant_minimum_order;
        }

        public void setMerchant_minimum_order(String merchant_minimum_order) {
            this.merchant_minimum_order = merchant_minimum_order;
        }

        public String getLink_type() {
            return link_type;
        }

        public void setLink_type(String link_type) {
            this.link_type = link_type;
        }

        public String getLink_option() {
            return link_option;
        }

        public void setLink_option(String link_option) {
            this.link_option = link_option;
        }

        public String getAdds_id() {
            return adds_id;
        }

        public void setAdds_id(String adds_id) {
            this.adds_id = adds_id;
        }

        public String getRestaurant_id() {
            return restaurant_id;
        }

        public void setRestaurant_id(String restaurant_id) {
            this.restaurant_id = restaurant_id;
        }

        public String getItem_id() {
            return item_id;
        }

        public void setItem_id(String item_id) {
            this.item_id = item_id;
        }

        public String getAds_photo() {
            return ads_photo;
        }

        public void setAds_photo(String ads_photo) {
            this.ads_photo = ads_photo;
        }

        public String getSponsored_start_date() {
            return sponsored_start_date;
        }

        public void setSponsored_start_date(String sponsored_start_date) {
            this.sponsored_start_date = sponsored_start_date;
        }

        public String getSponsored_expiration() {
            return sponsored_expiration;
        }

        public void setSponsored_expiration(String sponsored_expiration) {
            this.sponsored_expiration = sponsored_expiration;
        }

        public String getDisplay_page_id() {
            return display_page_id;
        }

        public void setDisplay_page_id(String display_page_id) {
            this.display_page_id = display_page_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDate_created() {
            return date_created;
        }

        public void setDate_created(String date_created) {
            this.date_created = date_created;
        }

        public String getDate_modified() {
            return date_modified;
        }

        public void setDate_modified(String date_modified) {
            this.date_modified = date_modified;
        }

        public String getIp_address() {
            return ip_address;
        }

        public void setIp_address(String ip_address) {
            this.ip_address = ip_address;
        }

        public String getIs_appadds() {
            return is_appadds;
        }

        public void setIs_appadds(String is_appadds) {
            this.is_appadds = is_appadds;
        }

        public String getGroup_ids() {
            return group_ids;
        }

        public void setGroup_ids(String group_ids) {
            this.group_ids = group_ids;
        }

        public String getCity_ids() {
            return city_ids;
        }

        public void setCity_ids(String city_ids) {
            this.city_ids = city_ids;
        }

        public String getLink_url() {
            return link_url;
        }

        public void setLink_url(String link_url) {
            this.link_url = link_url;
        }
    }

}
