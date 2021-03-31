package com.afieat.ini.models;

import java.util.HashMap;
import java.util.List;

public class DemoSearchModel {

    private String food_id;
    private String merchant_id;
    private String foodName;
    private String foodNameAr;
    private String image_url;
    public List<Price> mPriceList;
    private int priceLitingSize;
    public List<ActualPrice> mActualPriceList;
    public HashMap<String, String> mPriceHashMap;
    public HashMap<String, String> mActualPriceHashMap;

    public DemoSearchModel(String food_id,
                           String merchant_id,
                           String foodName,
                           String foodNameAr,
                           List<Price> mPriceList,
                           List<ActualPrice> mActualPricesList,
                           String image_url,
                           int priceLitingSize) {

        this.food_id = food_id;
        this.merchant_id = merchant_id;
        this.foodName = foodName;
        this.foodNameAr = foodNameAr;
        this.mPriceHashMap = mPriceHashMap;
        this.mPriceList = mPriceList;
        this.mActualPriceList = mActualPricesList;
        this.mActualPriceHashMap = mActualPriceHashMap;
        this.image_url = image_url;
        this.priceLitingSize = priceLitingSize;
    }

    public DemoSearchModel() {
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodNameAr() {
        return foodNameAr;
    }

    public void setFoodNameAr(String foodNameAr) {
        this.foodNameAr = foodNameAr;
    }

    public HashMap<String, String> getmPriceHashMap() {
        return mPriceHashMap;
    }

    public void setmPriceHashMap(HashMap<String, String> mPriceHashMap) {
        this.mPriceHashMap = mPriceHashMap;
    }

    public HashMap<String, String> getmActualPriceHashMap() {
        return mActualPriceHashMap;
    }

    public void setmActualPriceHashMap(HashMap<String, String> mActualPriceHashMap) {
        this.mActualPriceHashMap = mActualPriceHashMap;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public List<Price> getmPriceList() {
        return mPriceList;
    }

    public void setmPriceList(List<Price> mPriceList) {
        this.mPriceList = mPriceList;
    }

    public List<ActualPrice> getmActualPriceList() {
        return mActualPriceList;
    }

    public void setmActualPriceList(List<ActualPrice> mActualPriceList) {
        this.mActualPriceList = mActualPriceList;
    }

    public int getPriceLitingSize() {
        return priceLitingSize;
    }

    public void setPriceLitingSize(int priceLitingSize) {
        this.priceLitingSize = priceLitingSize;
    }

    public class Price {

        private String id,
                price,
                type;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public class ActualPrice {

        String id, price, type;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
