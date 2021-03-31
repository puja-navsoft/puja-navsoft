package com.afieat.ini.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afieat.ini.R;
import com.bumptech.glide.Glide;
import com.vlk.multimager.utils.Image;

import java.util.ArrayList;

/**
 * Created by amartya on 22/09/17 with love.
 */

public class ReviewImageAdapter extends RecyclerView.Adapter<ReviewImageAdapter.Vh> {
    ArrayList<Image> list;
    Activity activity;

    public interface OnImageAction {
        void OnImageDelete(String ImageName, int Position);
    }

    OnImageAction onImageAction;

    public ReviewImageAdapter(ArrayList<Image> list, Activity activity, OnImageAction onImageAction) {
        this.onImageAction = onImageAction;
        this.list = list;
        this.activity = activity;
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(LayoutInflater.from(parent.getContext()).inflate(R.layout.review_image_submit, parent, false));
    }

    @Override
    public void onBindViewHolder(final Vh holder, final int position) {
        final Image entity = list.get(position);
        float height;
        if (holder.IMAGE != null) {
            if (entity.uri != null) {
                Glide.with(activity)
                        .load(entity.uri)
                        .placeholder(com.vlk.multimager.R.drawable.image_processing)
                        .error(com.vlk.multimager.R.drawable.no_image).centerCrop()
                        .into(holder.IMAGE);
                holder.CLOSE.setTag("1");
            } else {
                Glide.with(activity)
                        .load("https://d22vvrqrexhw5s.cloudfront.net/upload/review/thumb_81_81/" + entity.imagePath)
                        .placeholder(com.vlk.multimager.R.drawable.image_processing)
                        .error(com.vlk.multimager.R.drawable.no_image).centerCrop()
                        .into(holder.IMAGE);
                holder.CLOSE.setTag("2");
            }
        }

        holder.CLOSE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag().equals("1")) {
                    list.remove(position);
                    notifyDataSetChanged();
                } else {
                    /*
                    @ Koushik if Image From Server then need to delete Image From Server
                     */
                    onImageAction.OnImageDelete(entity.imagePath, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Vh extends RecyclerView.ViewHolder {
        ImageView IMAGE, CLOSE;

        public Vh(View itemView) {
            super(itemView);
            IMAGE = (ImageView) itemView.findViewById(R.id.IMAGE);
            CLOSE = (ImageView) itemView.findViewById(R.id.CLOSE);
        }
    }
}
