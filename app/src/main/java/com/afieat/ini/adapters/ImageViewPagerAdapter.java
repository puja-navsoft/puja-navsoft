package com.afieat.ini.adapters;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afieat.ini.FullScreenImageDisplay;
import com.afieat.ini.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by amartya on 26/09/17 with love.
 */

public class ImageViewPagerAdapter extends PagerAdapter {
    private Context mContext;
   private ArrayList<String> ImagePath;
   private FullScreenImageDisplay mFullScreenImageDisplay;

    public ImageViewPagerAdapter(Context mContext, ArrayList<String> imagePath) {
        this.mContext = mContext;
        mFullScreenImageDisplay = (FullScreenImageDisplay) mContext;
        ImagePath = imagePath;
    }

    @Override
    public int getCount() {
        return ImagePath.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);

    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.fullimage_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        final GifImageView waitProgressBar = (GifImageView) itemView.findViewById(R.id.gifImg);

        Log.d("@@ THUMURL", ImagePath.get(position));
        Glide.with(mContext).load(ImagePath.get(position)).error(R.drawable.no_image_full).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                waitProgressBar.setVisibility(View.GONE);

                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                waitProgressBar.setVisibility(View.GONE);

                return false;
            }
        }).into(imageView);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
