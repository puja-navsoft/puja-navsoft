package com.afieat.ini.fragments.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class FoodCategoryContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<CategoryItem> ITEMS = new ArrayList<CategoryItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, CategoryItem> ITEM_MAP = new HashMap<String, CategoryItem>();


    public static void addItem(CategoryItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.group_id, item);
    }
    public static void addItem(CategoryItem item,int pos) {
        ITEMS.add(pos,item);
        ITEM_MAP.put(item.group_id, item);
    }

    public static void ClearData() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }


    public static class CategoryItem {
        public String group_id;
        public String is_featured;
        public String group_name;
        public String group_name_ar;
        public String group_description;
        public String group_description_ar;
        public String photo;
        public String date_created;
        public int resturantCount;

        public AddsBean AddsBean=null;

        public CategoryItem.AddsBean getAddsBean() {
            return AddsBean;
        }

        public void setAddsBean(CategoryItem.AddsBean addsBean) {
            AddsBean = addsBean;
        }

        public int getResturantCount() {
            return resturantCount;
        }

        public void setResturantCount(int resturantCount) {
            this.resturantCount = resturantCount;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public void setIs_featured(String is_featured) {
            this.is_featured = is_featured;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }

        public void setGroup_name_ar(String group_name_ar) {
            this.group_name_ar = group_name_ar;
        }

        public void setGroup_description(String group_description) {
            this.group_description = group_description;
        }

        public void setGroup_description_ar(String group_description_ar) {
            this.group_description_ar = group_description_ar;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public void setDate_created(String date_created) {
            this.date_created = date_created;
        }

        public String getGroup_id() {
            return group_id;
        }

        public String getIs_featured() {
            return is_featured;
        }

        public String getGroup_name() {
            return group_name;
        }

        public String getGroup_name_ar() {
            return group_name_ar;
        }

        public String getGroup_description() {
            return group_description;
        }

        public String getGroup_description_ar() {
            return group_description_ar;
        }

        public String getPhoto() {
            return photo;
        }

        public String getDate_created() {
            return date_created;
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
            private String restaurant_slug = "";

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

            public String getRestaurant_slug() {
                return restaurant_slug;
            }

            public void setRestaurant_slug(String restaurant_slug) {
                this.restaurant_slug = restaurant_slug;
            }
        }
    }


}
