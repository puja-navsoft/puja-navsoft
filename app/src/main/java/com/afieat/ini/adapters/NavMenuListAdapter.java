package com.afieat.ini.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afieat.ini.R;

/**
 * Created by amartya on 14/07/16 with love.
 */
public class NavMenuListAdapter extends BaseAdapter {

    private String[] navItems;
    private int[] navIcons;

    public NavMenuListAdapter(String[] navItems, int[] navIcons) {
        this.navItems = navItems;
        this.navIcons = navIcons;
    }

    @Override
    public int getCount() {
        return navItems.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mConverterView=convertView;
//        if (convertView == null) {
        mConverterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_nav_list_item, parent, false);
//        }
        TextView view = (TextView) mConverterView;
        String item = navItems[position];
        int drawable = navIcons[position];
        view.setText(item);
        if (position == 1)
            view.setTextColor(Color.parseColor("#C63A2B"));
        view.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
        view.setCompoundDrawablePadding(55);
        return mConverterView;
    }
}
