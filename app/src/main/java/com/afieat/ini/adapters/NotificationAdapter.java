package com.afieat.ini.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afieat.ini.R;
import com.afieat.ini.models.Notification;

import java.util.List;

/**
 * Created by amartya on 15/09/16 with love.
 */
public class NotificationAdapter extends BaseAdapter {

    private List<Notification> notifications;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(notifications.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View mConverterView=convertView;
        if (mConverterView == null) {
            mConverterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notification_row, parent, false);
        }

        TextView tvNotifTitle = (TextView) mConverterView.findViewById(R.id.tvNotifTitle);
        TextView tvNotifDate = (TextView) mConverterView.findViewById(R.id.tvNotifDate);
        TextView tvNotifBody = (TextView) mConverterView.findViewById(R.id.tvNotifBody);

        Notification notification = notifications.get(position);
        tvNotifTitle.setText(notification.getTitle());
        tvNotifDate.setText(notification.getDateCreated());
        tvNotifBody.setText(notification.getMessage());

        return mConverterView;
    }
}
