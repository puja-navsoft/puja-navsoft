package com.afieat.ini.models;

/**
 * Created by amartya on 22/04/16 with love.
 */
public class RestaurantsFilter {
    protected String val;
    protected String cuisine;

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public RestaurantsFilter() {
        val = "";
        cuisine = "";
    }

    public boolean equals(RestaurantsFilter filter) {
        return (this.val.equals(filter.val) && this.cuisine.equals(filter.cuisine));
    }

    public void convertTo(RestaurantsFilter filter) {
        this.val = filter.val;
        this.cuisine = filter.cuisine;
    }

    public void empty() {
        this.val = "";
        this.cuisine = "";
    }
}
