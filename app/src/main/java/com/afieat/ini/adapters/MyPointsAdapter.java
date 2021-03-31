package com.afieat.ini.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.afieat.ini.MyPoints;
import com.afieat.ini.R;
import com.afieat.ini.models.MyPointsModel;

import org.json.JSONArray;

import java.util.List;

public class MyPointsAdapter extends RecyclerView.Adapter<MyPointsAdapter.ViewHolder> {
    private List<MyPointsModel> mMyPoints;
    private Context context;
   private String mRestaurantName;
   private JSONArray photosArray;
    private int lastPosition = -1;
    private MyPoints myPoints;

    public MyPointsAdapter(Context context, List<MyPointsModel> mMyPoints) {
        this.mMyPoints = mMyPoints;
        myPoints= (MyPoints) context;
        this.photosArray = photosArray;
        this.context = context;
        this.mRestaurantName = mRestaurantName;
        System.out.println("Rahul : MyAdapter : galleryList.size : " + mMyPoints.size());
    }

    @Override
    public MyPointsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_points_row_layout, viewGroup, false);
        view.setTag(i);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyPointsAdapter.ViewHolder viewHolder, final int i) {

        final MyPointsModel myPointsModel = mMyPoints.get(i);

        viewHolder.orderNo.setText(myPointsModel.getOrder_no());
        viewHolder.amount.setText(context.getString(R.string.currency) + myPointsModel.getAmount());
        viewHolder.date.setText(myPointsModel.getDate().toString().replace("-", "/"));

        if ("Delivered".equalsIgnoreCase(myPointsModel.getStatus())) {
            viewHolder.status.setBackgroundResource(R.drawable.corner_border_status_my_point);

        } else {
            viewHolder.status.setBackgroundResource(R.drawable.corner_border_status_pending_my_point);

        }
        viewHolder.status.setText(myPointsModel.getStatus());
        viewHolder.status.setPadding(15, 10, 15, 10);

        if (!"0".equalsIgnoreCase(myPointsModel.getPoints())) {
            viewHolder.points.setText(myPointsModel.getPoints().toString());
        }

        if (!"0".equalsIgnoreCase(myPointsModel.getRedeem_points())) {
            viewHolder.redeem_points.setText(myPointsModel.getRedeem_points().toString());
        }else
        {
            viewHolder.redeem_points.setVisibility(View.GONE);
            viewHolder.redeemPointtxt.setVisibility(View.GONE);
        }
   /*     System.out.println("Rahul : MyAdapter : galleryList.get(i) : " + galleryList.get(i));
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
        });*/

   if(i==mMyPoints.size()-1)
   {
       myPoints.onBottomReached();
   }

        if (i > lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
            viewHolder.itemView.startAnimation(animation);
            lastPosition = i;
        }
    }

    @Override
    public int getItemCount() {
        return mMyPoints.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView orderNo,
                amount,
                points,
                status,
                date,
                redeem_points,
                redeemPointtxt;

        public ViewHolder(View view) {
            super(view);

            orderNo = view.findViewById(R.id.orderNo);
            amount = view.findViewById(R.id.amount);
            points = view.findViewById(R.id.points);
            status = view.findViewById(R.id.status);
            date = view.findViewById(R.id.date);
            redeem_points = view.findViewById(R.id.redeemPoints);
            redeemPointtxt = view.findViewById(R.id.redeemPointtxt);


        }
    }
}