package com.afieat.ini.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.afieat.ini.FullScreenImageDisplay;
import com.afieat.ini.R;
import com.bumptech.glide.Glide;

import org.json.JSONArray;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<String> galleryList;
    private Context context;
   private String mRestaurantName;
    private JSONArray photosArray;
    private int lastPosition = -1;

    public MyAdapter(Context context, List<String> galleryList, JSONArray photosArray, String mRestaurantName) {
        this.galleryList = galleryList;
        this.photosArray = photosArray;
        this.context = context;
        this.mRestaurantName = mRestaurantName;
        System.out.println("Rahul : MyAdapter : galleryList.size : " + galleryList.size());
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_row_items, viewGroup, false);
        view.setTag(i);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, final int i) {

        System.out.println("Rahul : MyAdapter : galleryList.get(i) : " + galleryList.get(i));
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        viewHolder.img.setImageURI(Uri.parse(galleryList.get(i)));


        Glide
                .with(context)
                .load(Uri.parse(galleryList.get(i)))
                .placeholder(R.drawable.placeholder_land)
                .into(viewHolder.img);


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] images = new String[photosArray.length()];
                for (int i = 0; i < photosArray.length(); i++) {
                    images[i] = photosArray.optString(i);
                }

                context.startActivity(new Intent(context, FullScreenImageDisplay.class)
                        .putExtra("mRestaurantName", mRestaurantName)
                        .putExtra("Images", images).putExtra("FromPage", "REST_INFO")
                        .putExtra("SELECTITEM", i));
            }
        });

        if (i > lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
            viewHolder.itemView.startAnimation(animation);
            lastPosition = i;
        }
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;

        public ViewHolder(View view) {
            super(view);

            img = (ImageView) view.findViewById(R.id.img);

        }
    }
}