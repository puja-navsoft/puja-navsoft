package com.afieat.ini.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.models.Cuisine;

import java.util.List;

/**
 * Created by amartya on 19/04/16 with love.
 */
public class CuisinesAdapter extends BaseAdapter {

    private List<Cuisine> cuisines;

    public CuisinesAdapter(List<Cuisine> cuisines) {
        this.cuisines = cuisines;
    }

    @Override
    public int getCount() {
        return cuisines.size();
    }

    @Override
    public Object getItem(int position) {
        return cuisines.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(cuisines.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_simple_spinner_item, parent, false);
        ((TextView) view.findViewById(R.id.tvItemTitle)).setText(cuisines.get(position).getName());
        return view;
    }
}
