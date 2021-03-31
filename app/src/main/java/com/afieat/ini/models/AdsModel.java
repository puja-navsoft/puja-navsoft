package com.afieat.ini.models;

import java.util.List;

public class AdsModel {

    /**
     * code : 1
     * msg : Success:Adds listed
     * status : 1
     * adds : [{"adds_id":"52","restaurant_id":"0","item_id":"0","ads_photo":"1501583295-mango-juice-250x250.jpg","sponsored_start_date":"2017-08-01","sponsored_expiration":"2019-12-31","display_page_id":"2","status":"publish","date_created":"2017-03-10 12:36:31","date_modified":"2019-01-10 16:13:37","ip_address":"172.31.6.101","is_appadds":"1","group_ids":"","city_ids":"","link_url":"https://www.google.com"},{"adds_id":"57","restaurant_id":"0","item_id":"0","ads_photo":"1500364358-4.JPG","sponsored_start_date":"2017-04-10","sponsored_expiration":"2019-12-31","display_page_id":"1","status":"publish","date_created":"2017-04-10 09:58:43","date_modified":"2019-01-10 16:11:55","ip_address":"172.31.6.101","is_appadds":"1","group_ids":"","city_ids":"","link_url":"https://www.google.com"},{"adds_id":"58","restaurant_id":"0","item_id":"0","ads_photo":"1492523182-1460538463-restaurant4.jpeg","sponsored_start_date":"2017-04-11","sponsored_expiration":"2018-07-31","display_page_id":"1","status":"publish","date_created":"2017-04-10 10:15:36","date_modified":"2019-01-10 16:10:54","ip_address":"172.31.6.101","is_appadds":"1","group_ids":"","city_ids":"","link_url":"https://www.google.com"},{"adds_id":"59","restaurant_id":"0","item_id":"0","ads_photo":"1545137958-chick1.jpeg","sponsored_start_date":"2018-12-19","sponsored_expiration":"2019-05-31","display_page_id":"1","status":"publish","date_created":"2018-12-18 15:59:23","date_modified":"2019-01-10 16:10:34","ip_address":"172.31.6.101","is_appadds":"1","group_ids":"","city_ids":"","link_url":"https://www.google.com"},{"adds_id":"60","restaurant_id":"0","item_id":"0","ads_photo":"1545815730-pinapl2.jpeg","sponsored_start_date":"2018-12-26","sponsored_expiration":"2019-01-31","display_page_id":"1","status":"publish","date_created":"2018-12-26 12:15:49","date_modified":"2019-01-10 16:04:25","ip_address":"172.31.6.101","is_appadds":"1","group_ids":"","city_ids":"","link_url":"https://www.google.com"},{"adds_id":"62","restaurant_id":"0","item_id":"0","ads_photo":"1546859839-images.jpg","sponsored_start_date":"2019-01-07","sponsored_expiration":"2019-01-31","display_page_id":"1","status":"publish","date_created":"2019-01-07 14:17:34","date_modified":"2019-01-10 16:03:56","ip_address":"172.31.6.101","is_appadds":"1","group_ids":"","city_ids":"","link_url":"https://www.google.iq/"}]
     */

    private int code;
    private String msg;
    private int status;
    private List<AddsBean> adds;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<AddsBean> getAdds() {
        return adds;
    }

    public void setAdds(List<AddsBean> adds) {
        this.adds = adds;
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
