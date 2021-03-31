package com.afieat.ini.interfaces;


import com.afieat.ini.models.Restaurant;

import java.util.List;


public interface FavouriteListner {

    void onFavClickListner(Restaurant mRestaurant, List<Restaurant> restaurants, int position);
}