package com.afieat.ini.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amartya on 13/04/16 with love.
 */
public class FoodCopy {

    private String id;
    private String entryId; // unique to each and every entry in the database
    private String merchantId;
    private String category;
    private String name;
    private String name_ar;
    private String description;
    private String discount;
    private String comment;
    private String uriPic;
    private String basketCount;
    private String sizeBasket;
    private String sizeBasketId;
    private String priceBasket;
    private String addOns, addOnIds, addOnPrices;
    private String ingredients, ingredientIds, ingredientPrices;
    private String unitPrice;
    private String restaurant_url;
    public List<Price> prices;
    public List<ActualPrice> actualPrice;



    public String getRestaurant_url() {
        return restaurant_url;
    }

    public void setRestaurant_url(String restaurant_url) {
        this.restaurant_url = restaurant_url;
    }

    public FoodCopy() {
        prices = new ArrayList<>();
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getUriPic() {
        return uriPic;
    }

    public void setUriPic(String uriPic) {
        this.uriPic = uriPic;
    }

    public String getBasketCount() {
        return basketCount;
    }

    public void setBasketCount(String basketCount) {
        this.basketCount = basketCount;
    }

    public String getSizeBasket() {
        return sizeBasket;
    }

    public void setSizeBasket(String sizeBasket) {
        this.sizeBasket = sizeBasket;
    }

    public String getSizeBasketId() {
        return sizeBasketId;
    }

    public void setSizeBasketId(String sizeBasketId) {
        this.sizeBasketId = sizeBasketId;
    }

    public String getPriceBasket() {
        return priceBasket;
    }

    public void setPriceBasket(String priceBasket) {
        this.priceBasket = priceBasket;
    }

    public String getAddOns() {
        return addOns;
    }

    public void setAddOns(String addOns) {
        this.addOns = addOns;
    }

    public String getAddOnIds() {
        return addOnIds;
    }

    public void setAddOnIds(String addOnIds) {
        this.addOnIds = addOnIds;
    }

    public String getAddOnPrices() {
        return addOnPrices;
    }

    public void setAddOnPrices(String addOnPrices) {
        this.addOnPrices = addOnPrices;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getIngredientIds() {
        return ingredientIds;
    }

    public void setIngredientIds(String ingredientIds) {
        this.ingredientIds = ingredientIds;
    }

    public String getIngredientPrices() {
        return ingredientPrices;
    }

    public void setIngredientPrices(String ingredientPrices) {
        this.ingredientPrices = ingredientPrices;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public List<ActualPrice> getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(List<ActualPrice> actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getName_ar() {
        return name_ar;
    }

    public void setName_ar(String name_ar) {
        this.name_ar = name_ar;
    }

    public FoodCopy(String id,
                    String merchantId,
                    String name,
                    String name_ar,
                    String discount,
                    String sizeBasket,
                    String sizeBasketId,
                    String priceBasket,
                    List<Price> prices,
                    List<ActualPrice> actualPrice,
                    String uriPic) {
        this.id = id;
        this.merchantId = merchantId;
        this.name = name;
        this.discount = discount;
        this.sizeBasket = sizeBasket;
        this.sizeBasketId = sizeBasketId;
        this.priceBasket = priceBasket;
        this.prices = prices;
        this.actualPrice = actualPrice;
        this.uriPic = uriPic;
        this.name_ar = name_ar;
    }

    public class Price {


        private String type;
        private String price;
        private String id;

/*
        public Price( String type, String price)
        {

            this.type=type;
            this.price=price;
        }
*/

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }


    public class ActualPrice {

        private String type;
        private String price;
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
}
