package com.afieat.ini.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;

public class SelectSizeDialogAdapter extends RecyclerView.Adapter<SelectSizeDialogAdapter.MyViewHolder> {


    private  Context c;
    private  JSONArray mJsonArray_sales_price, mJsonArray_actual_price;


    public SelectSizeDialogAdapter(Context c, JSONArray mJsonArray_actual_price, JSONArray mJsonArray_sales_price) {
        this.c = c;
        this.mJsonArray_sales_price = mJsonArray_sales_price;
        this.mJsonArray_actual_price = mJsonArray_actual_price;

        System.out.println("SelectSizeDialogAdapter : mJsonArray_sales_price  : "+mJsonArray_sales_price);
        System.out.println("SelectSizeDialogAdapter : mJsonArray_actual_price  : "+mJsonArray_actual_price);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.size_row_dialog, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        try {
            mJsonArray_actual_price.getJSONObject(position).getString("price");
            mJsonArray_actual_price.getJSONObject(position).getString("type");

            mJsonArray_sales_price.getJSONObject(position).getString("price");
            mJsonArray_sales_price.getJSONObject(position).getString("type");


            if(AppUtils.selectedSize==position)
            {
                holder.mrRadioButton.setChecked(true);
            }else {
                holder.mrRadioButton.setChecked(false);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        AppUtils.PLATE_ID=mJsonArray_sales_price.getJSONObject(position).getString("id");
                        AppUtils.PLATE_TYPE=mJsonArray_sales_price.getJSONObject(position).getString("type");
                        AppUtils.PLATE_PRICE=mJsonArray_sales_price.getJSONObject(position).getString("price");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    holder.mrRadioButton.setChecked(true);
                    AppUtils.selectedSize=position;
                    notifyItemRangeChanged(0, mJsonArray_actual_price.length());
                }
            });

           /* if(radioButtonSelection!=null) {
                if(radioButtonSelection.get(position)!=null) {
                    if (radioButtonSelection.get(position)) {

                        holder.mrRadioButton.setSelected(true);
                    } else {
                        holder.mrRadioButton.setSelected(false);
                    }
                }
            }*/

            if ( mJsonArray_actual_price.getJSONObject(position).getString("price").equalsIgnoreCase(mJsonArray_sales_price.getJSONObject(position).getString("price"))) {
                holder.tvPlate.setText(mJsonArray_sales_price.getJSONObject(position).getString("type"));
                holder.tvActualPrice.setText("");
                holder.tvPrice.setText("IQD "+mJsonArray_sales_price.getJSONObject(position).getString("price"));

            } else {
                holder.tvPlate.setText(mJsonArray_sales_price.getJSONObject(position).getString("type"));
                holder.tvActualPrice.setText("IQD "+mJsonArray_actual_price.getJSONObject(position).getString("price"));
                holder.tvPrice.setText("IQD "+mJsonArray_sales_price.getJSONObject(position).getString("price"));

            }




            holder.mrRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    try {
                        AppUtils.PLATE_ID=mJsonArray_sales_price.getJSONObject(position).getString("id");
                        AppUtils.PLATE_TYPE=mJsonArray_sales_price.getJSONObject(position).getString("type");
                        AppUtils.PLATE_PRICE=mJsonArray_sales_price.getJSONObject(position).getString("price");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AppUtils.selectedSize=position;
                    notifyItemRangeChanged(0, mJsonArray_actual_price.length());
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //BIND DATA

/*

        System.out.println("");

        final Food food = foods.get(position);

        Food.Price priceObject = (Food.Price) food.prices;


        System.out.println("actual : food.actualPrice.get(i).getPrice() :" + food.actualPrice.get(position).getPrice());

        holder.tvPrice.setText(c.getString(R.string.currency) + AppUtils.changeToArabic(priceObject.getPrice(), c));
        holder.tvPlate.setText("(" + priceObject.getType() + ")");


        double salesPrice = 0, actualPrice = 0;
        if (priceObject.getPrice() != null || !priceObject.getPrice().isEmpty()) {
            salesPrice = Double.parseDouble(priceObject.getPrice());
        }
        if (food.actualPrice.get(position).getPrice() != null || !food.actualPrice.get(position).getPrice().isEmpty()) {
            actualPrice = Double.parseDouble(food.actualPrice.get(position).getPrice());
        }

        System.out.println("FoodsAdapter4 : salesPrice : " + salesPrice);
        System.out.println("FoodsAdapter4 : actualPrice : " + actualPrice);
        if (salesPrice != actualPrice) {
            holder.tvActualPrice.setText(c.getString(R.string.currency) + AppUtils.changeToArabic(food.actualPrice.get(position).getPrice(),c));
        } else {
            holder.relPrice.setVisibility(View.GONE);
        }

*/

    }

    @Override
    public int getItemCount() {
        return mJsonArray_actual_price.length();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvPrice, tvPlate, tvActualPrice;
        RelativeLayout relPrice;
        CheckBox mrRadioButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvPlate = (TextView) itemView.findViewById(R.id.tvPlate);
            tvActualPrice = (TextView) itemView.findViewById(R.id.tvActualPrice);
            mrRadioButton=itemView.findViewById(R.id.radioButton);

        }
    }

}