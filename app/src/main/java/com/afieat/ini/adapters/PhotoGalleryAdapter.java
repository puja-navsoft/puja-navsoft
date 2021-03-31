package com.afieat.ini.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.afieat.ini.R;

import java.util.List;

/**
 * Created by amartya on 02/05/16 with love.
 */
public class PhotoGalleryAdapter extends BaseAdapter {

    private List<String> photos;

    public PhotoGalleryAdapter(List<String> photos) {
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return photos.size();
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
        View mConverView=convertView;
        if (mConverView == null) {
            mConverView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_simple_thumb_gallery, parent, false);
        }
        ((ImageView) mConverView.findViewById(R.id.ivFoodImage)).setImageURI(Uri.parse(photos.get(position)));
        return mConverView;
    }
}
