package com.afieat.ini.models.RestaurantModel;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantListModel {

@SerializedName("restaurants")
@Expose
private List<Restaurant> restaurants = null;
@SerializedName("status")
@Expose
private Integer status;
@SerializedName("code")
@Expose
private Integer code;
@SerializedName("restaurant_count")
@Expose
private Integer restaurantCount;
@SerializedName("msg")
@Expose
private String msg;

public List<Restaurant> getRestaurants() {
return restaurants;
}

public void setRestaurants(List<Restaurant> restaurants) {
this.restaurants = restaurants;
}

public Integer getStatus() {
return status;
}

public void setStatus(Integer status) {
this.status = status;
}

public Integer getCode() {
return code;
}

public void setCode(Integer code) {
this.code = code;
}

public Integer getRestaurantCount() {
return restaurantCount;
}

public void setRestaurantCount(Integer restaurantCount) {
this.restaurantCount = restaurantCount;
}

public String getMsg() {
return msg;
}

public void setMsg(String msg) {
this.msg = msg;
}

}