package com.afieat.ini.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afieat.ini.FullScreenImageDisplay;
import com.afieat.ini.R;
import com.afieat.ini.models.Review;
import com.afieat.ini.utils.AppUtils;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by amartya on 12/07/16 with love.
 */
public class ReviewsAdapter extends BaseAdapter {


    private List<Review> reviews;
    private OnReviewAlterListener listener;
    Context mContext;

    public ReviewsAdapter(Context mContext, List<Review> reviews) {
        this.reviews = reviews;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mConvertView=convertView;
        if (mConvertView == null) {
            mConvertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_review_item, parent, false);
        }
        TextView tvRestName = (TextView) mConvertView.findViewById(R.id.tvRestName);
        RatingBar rating = (RatingBar) mConvertView.findViewById(R.id.rating);
        TextView tvRating = (TextView) mConvertView.findViewById(R.id.tvRating);
        TextView tvReview = (TextView) mConvertView.findViewById(R.id.tvReview);
        TextView tvReviewDate = (TextView) mConvertView.findViewById(R.id.tvReviewDate);
        TextView tvDelete = (TextView) mConvertView.findViewById(R.id.tvDelete);
        TextView tvEdit = (TextView) mConvertView.findViewById(R.id.tvEdit);
        LinearLayout mainLayout = (LinearLayout) mConvertView.findViewById(R.id.l_main);

        final Review review = reviews.get(position);

        tvRestName.setText(review.getMerchantName());
        tvReview.setText(review.getReviewBody());
        rating.setRating(Float.parseFloat(review.getRating()));
        tvRating.setText(review.getRating());
        tvReviewDate.setText(AppUtils.formatDate(review.getReviewDate()));

        mainLayout.removeAllViewsInLayout();

        if (!review.getImageName().equalsIgnoreCase("null") && !review.getImageName().equalsIgnoreCase("")) {
            final String[] images = review.getImageName().split(",");

            if (images.length > 0) {
                for (int i = 0; i < images.length; i++) {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.layout_simple_thumb, null);
                    ImageView photoView = (ImageView) view.findViewById(R.id.ivFoodImage);
                    Log.d("@@ IMAG- ", images[i]);
                    Glide.with(mContext)
                            .load(Uri.parse("https://d22vvrqrexhw5s.cloudfront.net/upload/review/thumb_81_81/" + images[i]))
                            .placeholder(R.drawable.placeholder_land)
                            .into(photoView);
                    Uri pathUri = Uri.parse("https://d22vvrqrexhw5s.cloudfront.net/upload/review/thumb_81_81/" + images[i]);
                    photoView.setTag(i);
                    photoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mContext.startActivity(new Intent(mContext, FullScreenImageDisplay.class)
                                    .putExtra("Images", images).putExtra("FromPage", "MYREVIEW")
                                    .putExtra("SELECTITEM", Integer.parseInt("" + view.getTag())));
                        }
                    });
                    mainLayout.addView(view);
                }
            }

        }


        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReviewDelete(review.getReviewId());
            }
        });

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReviewEdit(review);
            }
        });

        return mConvertView;
    }

    public void setOnReviewAlterListener(OnReviewAlterListener listener) {
        this.listener = listener;
    }

    public interface OnReviewAlterListener {
        void onReviewEdit(Review review);

        void onReviewDelete(String reviewId);
    }
}
