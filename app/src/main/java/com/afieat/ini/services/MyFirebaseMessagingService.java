package com.afieat.ini.services;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.afieat.ini.CategoryListActivity;
import com.afieat.ini.LocationActivity;
import com.afieat.ini.R;
import com.afieat.ini.RestaurantsDetailActivity;
import com.afieat.ini.database.DBHelper;
import com.afieat.ini.models.Notification;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;

public class
MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


//        int color = getResources().getColor(R.color.my_notif_color);
//        int color = ContextCompat.getColor(context, R.color.my_notif_color);
        /*AppUtils.log("From: " + remoteMessage.getFrom());
        AppUtils.log("Comtent: " + remoteMessage.getNotification().getBody());*/
//        String content = remoteMessage.getNotification().getBody();
//        String title = remoteMessage.getNotification().getTitle();
//        Log.d("Content    ","" + content);
//        Log.d("title    ","" + title);
        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        System.out.println("RahulFire : object : " + object.length());
        try {
            if (object.length() > 1) {
                String title = object.optString("title");
                String body = object.optString("body");
                String restaurant_id = object.optString("restaurant_id");
                String image = object.optString("image");

                sendAllTypeNotification(title,
                        body,
                        restaurant_id,
                        image);

            }

        } catch (NullPointerException e) {
            System.out.println("Rahul MyFirebaseMessagingService : NullPointerException : " + e.getMessage());

        }

 /*      String body= object.getString("body");
                object.getString("restaurant_id"),
                object.getString("image")
                        */
        Log.i("JSON_OBJECT", object.toString());
        //      sendNotification("Test1234", object.toString());


        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {

            Log.i("Test1234", "key, " + entry.getKey() + " value " + entry.getValue());

        }

    }

    private void sendAllTypeNotification(String title,
                                         String content,
                                         String restaurant_id,
                                         String image) {

        Bitmap bitmap = getBitmapfromUrl(image);

        Intent intent;
        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String dateString = date + "/" + month + "/" + year;
        String timeString = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        DBHelper db = new DBHelper(getApplicationContext());
        String driverId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.SHARED_PREF_USER_ID);
        Notification notification;
        notification = new Notification();
        notification.setDateCreated(dateString);
        notification.setMessage(content);
        notification.setTime(timeString);

        db.addNotification(driverId, notification);


        //mContextx.startActivity(intent);

        if ("".equals(restaurant_id) || "0".equals(restaurant_id)) {
            intent = new Intent(this, CategoryListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent = new Intent(this, RestaurantsDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(AppUtils.EXTRA_RESTAURANT_ID, restaurant_id);
            intent.putExtra(AppUtils.FROM_PUSH, "1");
        }

        TaskStackBuilder stackBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder = TaskStackBuilder.create(MyFirebaseMessagingService.this);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (stackBuilder != null) {
                stackBuilder.addParentStack(RestaurantsDetailActivity.class);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (stackBuilder != null) {
                stackBuilder.addNextIntent(intent);
            }
        }

        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            if (stackBuilder != null) {
                pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            }
        }

        /*PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                PendingIntent.FLAG_ONE_SHOT);*/

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (!"".equals(image)) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setColor(getApplicationContext().getResources()
                            .getColor(R.color.orangeButton))
                    .setVibrate(new long[]{1000, 500})
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(content));
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //For Android O-----
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(getApplicationContext().getResources()
                        .getColor(R.color.orangeButton));
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{1000, 500});
                assert notificationManager != null;
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            //----------------
            if (notificationManager != null) {
                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            }
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setColor(getApplicationContext().getResources()
                            .getColor(R.color.orangeButton))
                    .setVibrate(new long[]{1000, 500})
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content));
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //------------------For Android O
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(getApplicationContext().getResources()
                        .getColor(R.color.orangeButton));
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{1000, 500});
                assert notificationManager != null;
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            //--------------------
            if (notificationManager != null) {
                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            }
        }


    }


    private void sendNotification(String title, String content) {
        Intent intent;
        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String dateString = date + "/" + month + "/" + year;
        String timeString = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        DBHelper db = new DBHelper(getApplicationContext());
        String driverId = AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.SHARED_PREF_USER_ID);
        Notification notification;
        if ("logout".equals(title)) {
            intent = new Intent(this, LocationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.SHARED_PREF_USER_ID, "");
            /*PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    intent, 0);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.app_logo)
                    .setContentTitle("FCM Message")
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{500, 1000})
                    .setContentIntent(contentIntent);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());*/
            startActivity(intent);
        } else if ("neworder".equals(title)) {

            notification = new Notification();
            notification.setDateCreated(dateString);
            notification.setMessage(content);
            notification.setTime(timeString);

            db.addNotification(driverId, notification);

            intent = new Intent(this, LocationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[]{1000, 500})
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
        else {
            intent = new Intent(this, LocationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            notification = new Notification();
            notification.setDateCreated(dateString);
            notification.setMessage(content);
            notification.setTime(timeString);

            db.addNotification(driverId, notification);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[]{1000, 500})
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }


    }


    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}
