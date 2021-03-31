package com.afieat.ini.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.models.Review;
import com.afieat.ini.utils.AppUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amartya on 12/07/16 with love.
 */
public class RestaurantReviewAdapter extends BaseAdapter {

    private List<Review> reviews;
    private String mUserId;
    private ReviewsAdapter.OnReviewAlterListener listener;
    //    private LinearLayout mainLayout;
    private View cell;
    private LayoutInflater inflater = null;
    private Context context = null;

    public RestaurantReviewAdapter(Context context,
                                   List<Review> reviews,
                                   String userId,
                                   ReviewsAdapter.OnReviewAlterListener listener) {
        this.reviews = reviews;
        this.mUserId = userId;
        this.listener = listener;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
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

        View mConverterView=convertView;
        if (mConverterView == null) {

            mConverterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_review_list_item, parent, false);
        }
        TextView tvUserName = (TextView) mConverterView.findViewById(R.id.tvUserName);
        RatingBar rating = (RatingBar) mConverterView.findViewById(R.id.rating);
        TextView tvRating = (TextView) mConverterView.findViewById(R.id.tvRating);
        TextView tvReview = (TextView) mConverterView.findViewById(R.id.tvReview);
        TextView tvReviewDate = (TextView) mConverterView.findViewById(R.id.tvReviewDate);
        TextView tvEdit = (TextView) mConverterView.findViewById(R.id.tvEdit);
        TextView tvDelete = (TextView) mConverterView.findViewById(R.id.tvDelete);
        LinearLayout mainLayout = (LinearLayout) mConverterView.findViewById(R.id.linearLayoutGallery);
        final Review review = reviews.get(position);

        tvUserName.setText(review.getClientName());
        tvReview.setText(review.getReviewBody());
        rating.setRating(Float.parseFloat(review.getRating()));

        if (Float.parseFloat(review.getRating()) > 4.5) {
            ((RatingBar) mConverterView.findViewById(R.id.rating_4_5)).setVisibility(View.VISIBLE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_4)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_3_5)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_3)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating)).setVisibility(View.GONE);


            ((RatingBar) mConverterView.findViewById(R.id.rating_4_5)).setRating(Float.parseFloat(review.getRating()));

        } else if (Float.parseFloat(review.getRating()) > 4) {
            ((RatingBar) mConverterView.findViewById(R.id.rating_4)).setVisibility(View.VISIBLE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_4_5)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_3_5)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_3)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating)).setVisibility(View.GONE);


            ((RatingBar) mConverterView.findViewById(R.id.rating_4)).setRating(Float.parseFloat(review.getRating()));

        } else if (Float.parseFloat(review.getRating()) > 3.5) {
            ((RatingBar) mConverterView.findViewById(R.id.rating_3_5)).setVisibility(View.VISIBLE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_4_5)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_4)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_3)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating)).setVisibility(View.GONE);

            ((RatingBar) mConverterView.findViewById(R.id.rating_3_5)).setRating(Float.parseFloat(review.getRating()));

        } else if (Float.parseFloat(review.getRating()) > 3) {

            ((RatingBar) mConverterView.findViewById(R.id.rating_3)).setVisibility(View.VISIBLE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_4_5)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_4)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_3_5)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating)).setVisibility(View.GONE);

            ((RatingBar) mConverterView.findViewById(R.id.rating_3)).setRating(Float.parseFloat(review.getRating()));

        } else if (Float.parseFloat(review.getRating()) > 2.5) {
            ((RatingBar) mConverterView.findViewById(R.id.rating)).setVisibility(View.VISIBLE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_4_5)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_4)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_3_5)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_3)).setVisibility(View.GONE);

            ((RatingBar) mConverterView.findViewById(R.id.rating)).setRating(Float.parseFloat(review.getRating()));

        } else {

            ((RatingBar) mConverterView.findViewById(R.id.rating)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_4_5)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_4)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_3_5)).setVisibility(View.GONE);
            ((RatingBar) mConverterView.findViewById(R.id.rating_3)).setVisibility(View.GONE);
        }

        tvRating.setText(AppUtils.changeToArabic(review.getRating(), parent.getContext()));
        tvReviewDate.setText(AppUtils.formatDate(review.getReviewDate()));

        if (!review.getClientId().equals(mUserId)) {
            ViewGroup.LayoutParams tvEditLayoutParams = tvEdit.getLayoutParams();
            tvEditLayoutParams.width = 0;
            tvEditLayoutParams.height = 0;
            ViewGroup.LayoutParams tvDeleteLayoutParams = tvDelete.getLayoutParams();
            tvDeleteLayoutParams.width = 0;
            tvDeleteLayoutParams.height = 0;
        }

        tvEdit.setVisibility(View.GONE);
        tvDelete.setVisibility(View.GONE);

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReviewEdit(review);
            }
        });

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReviewDelete(review.getReviewId());
            }
        });

        //     mainLayout = (LinearLayout) mConverterView.findViewById(R.id.linearLayoutGallery);
        final ProgressBar progressBar = (ProgressBar) mConverterView.findViewById(R.id.progress);
        Log.d("@@KOUSHIK ", review.getImageName());
        mainLayout.removeAllViewsInLayout();
        mainLayout.setVisibility(View.GONE);
        if (!"null".equalsIgnoreCase(review.getImageName())) {
            mainLayout.setVisibility(View.VISIBLE);
            String[] images = review.getImageName().split(",");


            if (images.length > 0) {
                final int MAX_PICS = 5;


//                if (!(mainLayout.getChildCount() == images.length))
                {
                    final List<String> photoUris = new ArrayList<>();
                    int i = 0;
                    while (i < images.length) {
                        View view = null;
                        String url = "https://d22vvrqrexhw5s.cloudfront.net/upload/review/thumb_81_81/" + images[i];

                        photoUris.add(url);
                        if (i >= MAX_PICS) {
                            i++;
                            continue;
                        }
                        view = LayoutInflater.from(context).inflate(R.layout.layout_simple_thumb, null);
//                    if (i < MAX_PICS - 1 || (i + 1) == images.length) {
//                        view = LayoutInflater.from(context).inflate(R.layout.layout_simple_thumb, null);
//                    } else {
//                        view = LayoutInflater.from(context).inflate(R.layout.layout_simple_thumb_plus, null);
//                        ((TextView) view.findViewById(R.id.tvMore)).setText(AppUtils.changeToArabic(String.valueOf(images.length - (i /*+ 1*/)), context.getApplicationContext()));
//                    }
                        ImageView photoView = (ImageView) view.findViewById(R.id.ivFoodImage);
                        //          photoView.setImageURI(Uri.parse(url));

                        Glide
                                .with(context)
                                .load(Uri.parse(url))
                                .placeholder(R.drawable.placeholder_land)
                                .into(photoView);
                        mainLayout.addView(view);
//                    view.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Fragment fragment = PhotoGalleryFragment.newInstance(photoUris, R.id.flPhotoContainer);
//                            getActivity().getSupportFragmentManager()
//                                    .beginTransaction()
//                                    .add(R.id.flPhotoContainer, fragment)
//                                    .addToBackStack(null)
//                                    .commit();
//                        }
//                    });
                        i++;
                    }
                }
                //     AppUtils.showViews(llPhotos);
//                tvPhotos = (TextView) mView.findViewById(R.id.tvPhotos);
//                tvPhotos.setText(tvPhotos.getText() + " (" + AppUtils.changeToArabic(String.valueOf(photosArray.length()), getActivity().getApplicationContext()) + ")");

                progressBar.setVisibility(View.GONE);
            }
        } else {
            progressBar.setVisibility(View.GONE);
//                   mainLayout.setVisibility(View.GONE);
        }


//            for (int i = 0; i < images.length; i++) {
//
////            imageView.setOnClickListener(new View.OnClickListener() {
////
////                @Override
////                public void onClick(View v) {
////                    // do whatever you want ...
////                    Toast.makeText(TestingActivity.this,
////                            (CharSequence) imageView.getTag(), Toast.LENGTH_SHORT).show();
////                }
////            });
//                String url = "https://d22vvrqrexhw5s.cloudfront.net/upload/review/thumb_81_81/" + images[i];
//                AppUtils.log("link--" +url);
//
//                Glide.with(context)
//                        .load(url)
//                        .listener(new RequestListener<String, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                progressBar.setVisibility(View.GONE);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                progressBar.setVisibility(View.GONE);
//                                return false;
//                            }
//                        })
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .crossFade()
//                        .into(imageView);
//
//                if (mainLayout.getChildAt(i) == null) {
//                    if ((cell.getParent() != null) && (flag_== false )){
//                        ((ViewGroup) cell.getParent()).removeView(cell);
//                        flag_ = true;
//                    }
//
//                    mainLayout.addView(cell);
//                }
//                mainLayout.setVisibility(View.VISIBLE);
////                if (mainLayout.getChildAt(i) == null) {
////                    AppUtils.log("test--");
////                    mainLayout.addView(cell);
////                }
//            }
//        } else {
//            progressBar.setVisibility(View.GONE);
//            mainLayout.setVisibility(View.GONE);
//        }

        return mConverterView;
    }

    public void setOnReviewAlterListener(ReviewsAdapter.OnReviewAlterListener listener) {
        this.listener = listener;
    }


//    public class ListAdapter extends ArrayAdapter<Item> {
//
//        public ListAdapter(Context context, int textViewResourceId) {
//            super(context, textViewResourceId);
//        }
//
//        public ListAdapter(Context context, int resource, List<Item> items) {
//            super(context, resource, items);
//        }
//
//        @Override
//        public View getView(int position, View mConverterView, ViewGroup parent) {
//
//            View v = mConverterView;
//
//            if (v == null) {
//                LayoutInflater vi;
//                vi = LayoutInflater.from(getContext());
//                v = vi.inflate(R.layout.itemlistrow, null);
//            }
//
//            Item p = getItem(position);
//
//            if (p != null) {
//                TextView tt1 = (TextView) v.findViewById(R.id.id);
//                TextView tt2 = (TextView) v.findViewById(R.id.categoryId);
//                TextView tt3 = (TextView) v.findViewById(R.id.description);
//
//                if (tt1 != null) {
//                    tt1.setText(p.getId());
//                }
//
//                if (tt2 != null) {
//                    tt2.setText(p.getCategory().getId());
//                }
//
//                if (tt3 != null) {
//                    tt3.setText(p.getDescription());
//                }
//            }
//
//            return v;
//        }
//
//    }

    private static class ViewHolder {

       private LinearLayout mainLayout = null;


    }

}
