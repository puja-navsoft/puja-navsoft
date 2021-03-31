package com.afieat.ini.models.RestaurantModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Restaurant {

@SerializedName("merchant_id")
@Expose
private String merchantId;
@SerializedName("restaurant_slug")
@Expose
private String restaurantSlug;
@SerializedName("restaurant_name")
@Expose
private String restaurantName;
@SerializedName("restaurant_name_ar")
@Expose
private String restaurantNameAr;
@SerializedName("search_address")
@Expose
private String searchAddress;
@SerializedName("city_id")
@Expose
private String cityId;
@SerializedName("delivery_circle")
@Expose
private String deliveryCircle;
@SerializedName("cuisine")
@Expose
private String cuisine;
@SerializedName("status")
@Expose
private String status;
@SerializedName("date_created")
@Expose
private String dateCreated;
@SerializedName("merchant_photo_bg")
@Expose
private String merchantPhotoBg;
@SerializedName("merchant_photo")
@Expose
private String merchantPhoto;
@SerializedName("delivery_time")
@Expose
private String deliveryTime;
@SerializedName("busy_time_start")
@Expose
private String busyTimeStart;
@SerializedName("busy_time_end")
@Expose
private String busyTimeEnd;
@SerializedName("merchant_minimum_order")
@Expose
private String merchantMinimumOrder;
@SerializedName("merchant_maximum_order")
@Expose
private String merchantMaximumOrder;
@SerializedName("present_rating")
@Expose
private String presentRating;
@SerializedName("cost_fortwopeople")
@Expose
private String costFortwopeople;
@SerializedName("review_count")
@Expose
private String reviewCount;
@SerializedName("processing_time")
@Expose
private String processingTime;
@SerializedName("merchant_close_store")
@Expose
private String merchantCloseStore;
@SerializedName("own_deliveryman")
@Expose
private String ownDeliveryman;
@SerializedName("is_notrestaurant")
@Expose
private String isNotrestaurant;

public String getMerchantId() {
return merchantId;
}

public void setMerchantId(String merchantId) {
this.merchantId = merchantId;
}

public String getRestaurantSlug() {
return restaurantSlug;
}

public void setRestaurantSlug(String restaurantSlug) {
this.restaurantSlug = restaurantSlug;
}

public String getRestaurantName() {
return restaurantName;
}

public void setRestaurantName(String restaurantName) {
this.restaurantName = restaurantName;
}

public String getRestaurantNameAr() {
return restaurantNameAr;
}

public void setRestaurantNameAr(String restaurantNameAr) {
this.restaurantNameAr = restaurantNameAr;
}

public String getSearchAddress() {
return searchAddress;
}

public void setSearchAddress(String searchAddress) {
this.searchAddress = searchAddress;
}

public String getCityId() {
return cityId;
}

public void setCityId(String cityId) {
this.cityId = cityId;
}

public String getDeliveryCircle() {
return deliveryCircle;
}

public void setDeliveryCircle(String deliveryCircle) {
this.deliveryCircle = deliveryCircle;
}

public String getCuisine() {
return cuisine;
}

public void setCuisine(String cuisine) {
this.cuisine = cuisine;
}

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public String getDateCreated() {
return dateCreated;
}

public void setDateCreated(String dateCreated) {
this.dateCreated = dateCreated;
}

public String getMerchantPhotoBg() {
return merchantPhotoBg;
}

public void setMerchantPhotoBg(String merchantPhotoBg) {
this.merchantPhotoBg = merchantPhotoBg;
}

public String getMerchantPhoto() {
return merchantPhoto;
}

public void setMerchantPhoto(String merchantPhoto) {
this.merchantPhoto = merchantPhoto;
}

public String getDeliveryTime() {
return deliveryTime;
}

public void setDeliveryTime(String deliveryTime) {
this.deliveryTime = deliveryTime;
}

public String getBusyTimeStart() {
return busyTimeStart;
}

public void setBusyTimeStart(String busyTimeStart) {
this.busyTimeStart = busyTimeStart;
}

public String getBusyTimeEnd() {
return busyTimeEnd;
}

public void setBusyTimeEnd(String busyTimeEnd) {
this.busyTimeEnd = busyTimeEnd;
}

public String getMerchantMinimumOrder() {
return merchantMinimumOrder;
}

public void setMerchantMinimumOrder(String merchantMinimumOrder) {
this.merchantMinimumOrder = merchantMinimumOrder;
}

public String getMerchantMaximumOrder() {
return merchantMaximumOrder;
}

public void setMerchantMaximumOrder(String merchantMaximumOrder) {
this.merchantMaximumOrder = merchantMaximumOrder;
}

public String getPresentRating() {
return presentRating;
}

public void setPresentRating(String presentRating) {
this.presentRating = presentRating;
}

public String getCostFortwopeople() {
return costFortwopeople;
}

public void setCostFortwopeople(String costFortwopeople) {
this.costFortwopeople = costFortwopeople;
}

public String getReviewCount() {
return reviewCount;
}

public void setReviewCount(String reviewCount) {
this.reviewCount = reviewCount;
}

public String getProcessingTime() {
return processingTime;
}

public void setProcessingTime(String processingTime) {
this.processingTime = processingTime;
}

public String getMerchantCloseStore() {
return merchantCloseStore;
}

public void setMerchantCloseStore(String merchantCloseStore) {
this.merchantCloseStore = merchantCloseStore;
}

public String getOwnDeliveryman() {
return ownDeliveryman;
}

public void setOwnDeliveryman(String ownDeliveryman) {
this.ownDeliveryman = ownDeliveryman;
}

public String getIsNotrestaurant() {
return isNotrestaurant;
}

public void setIsNotrestaurant(String isNotrestaurant) {
this.isNotrestaurant = isNotrestaurant;
}

}