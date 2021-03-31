package com.afieat.ini.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.interfaces.OnAddressChangeRequestListener;
import com.afieat.ini.models.Address;
import com.afieat.ini.utils.AppUtils;

import java.util.List;

/**
 * Created by amartya on 26/05/16 with love.
 */
public class AddressAdapter extends BaseAdapter {

    private List<Address> addresses;

    private Context mContext;

    public OnAddressChangeRequestListener listener;

    String from = "";

    public AddressAdapter(Context mContext, List<Address> addresses, String from) {
        this.mContext = mContext;
        this.addresses = addresses;
        this.from = from;
    }

    @Override
    public int getCount() {
        return addresses.size();
    }

    @Override
    public Object getItem(int position) {
        return addresses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(addresses.get(position).getId());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View mConvertView=convertView;
        if (mConvertView == null) {
            mConvertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_address_item, parent, false);
        }
        TextView tvName = (TextView) mConvertView.findViewById(R.id.tvName);
        TextView tvPhone = (TextView) mConvertView.findViewById(R.id.tvPhone);
        TextView tvAddress = (TextView) mConvertView.findViewById(R.id.tvAddress);
        ImageButton ibMenu = (ImageButton) mConvertView.findViewById(R.id.ibMenu);
        TextView tvDefaultAddress = (TextView) mConvertView.findViewById(R.id.tvDefaultAddress);

        if(from!=null && from.equalsIgnoreCase("butler")) {
            tvName.setText(addresses.get(position).getDescription());
        }
        else
            tvName.setText(addresses.get(position).getName());
        tvPhone.setText(addresses.get(position).getPhone());
        tvAddress.setText(addresses.get(position).getAddress());
        if (addresses.get(position).is_default()) {
            /*int width = AppUtils.convertDpToPixel(80, parent.getContext());
            int height = AppUtils.convertDpToPixel(40, parent.getContext());
            tvDefaultAddress.setLayoutParams(new RelativeLayout.LayoutParams(width, height));*/
            AppUtils.showViews(tvDefaultAddress);
        } else {
//            tvDefaultAddress.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
            AppUtils.hideViews(tvDefaultAddress);
        }

        ibMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(mContext, v);
                MenuInflater inflater = menu.getMenuInflater();
                if (from==null) {
                    inflater.inflate(R.menu.popup_edit_address, menu.getMenu());
                    menu.show();
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_address_edit:
                                    listener.onAddressChangeRequested(String.valueOf(getItemId(position)));
                                    break;
                                case R.id.menu_address_delete:
                                    listener.onAddressDeleteRequested(String.valueOf(getItemId(position)), position);
                                    break;
                                case R.id.menu_address_make_default:
                                    listener.onAddressSetDefaultRequested(String.valueOf(getItemId(position)), position);
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                }

                else if (from!=null && from.equalsIgnoreCase("butler")){
                    inflater.inflate(R.menu.popup_edit_address_butler, menu.getMenu());
                    menu.show();
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_address_edit:
                                    listener.onAddressChangeRequested(String.valueOf(getItemId(position)));
                                    break;
                                case R.id.menu_address_delete:
                                    listener.onAddressDeleteRequested(String.valueOf(getItemId(position)), position);
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                }
            }
        });

        return mConvertView;
    }

    public void setOnAddressChangeRequestListener(OnAddressChangeRequestListener listener) {
        this.listener = listener;
    }
}
