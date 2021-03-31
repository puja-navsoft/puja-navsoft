package com.afieat.ini;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.afieat.ini.Security.Relogin;
import com.afieat.ini.adapters.NotificationAdapter;
import com.afieat.ini.models.Notification;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.NetworkRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private ListView lvNotifications;

    private List<Notification> notifications;

    public NotificationActivity() {
        notifications = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        overridePendingTransition(R.anim.entry_in, R.anim.entry_out);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.appbar);
        assert mToolbar != null;
        mToolbar.setTitle(getString(R.string.title_notification));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.greyXLight));
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvNotifications = (ListView) findViewById(R.id.lvNotifications);

        if (AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID).length() > 0)
            fetchNotificationsFromNW();
        else
            startActivityForResult(new Intent(NotificationActivity.this, LoginActivity.class), AppUtils.REQ_LOGIN);

        lvNotifications.setAdapter(new NotificationAdapter(notifications));
    }

    private void fetchNotificationsFromNW() {
        final ProgressDialog dialog = AppUtils.showProgress(NotificationActivity.this, "", getString(R.string.msg_please_wait));
        Map<String, String> params = new HashMap<>();
        params.put("user_id", AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_ID));
        params.put("limit", String.valueOf(50));
        params.put("offset", String.valueOf(0));
        if ("en".equals(AppInstance.getInstance(getApplicationContext()).getFromSharedPref(AppUtils.PREF_USER_LANG))) {
            params.put("lang", "en");
        } else {
            params.put("lang", "ar");
        }
        NetworkRequest request = new NetworkRequest(Request.Method.POST, Apis.GET_NOTIFICATIONS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.hideProgress(dialog);
                        AppUtils.log(response);
                        if (response.has("status") && response.optInt("status") == 999) {
                            new Relogin(NotificationActivity.this, new Relogin.OnLoginlistener() {
                                @Override
                                public void OnLoginSucess() {
                                    fetchNotificationsFromNW();
                                }

                                @Override
                                public void OnError(String Errormessage) {
                                    startActivityForResult(new Intent(NotificationActivity.this, LoginActivity.class), AppUtils.REQ_LOGIN);
                                }
                            }).execute();
                        } else {
                            JSONArray notificationsArray = response.optJSONArray("notifications");
                            if (notificationsArray != null && notificationsArray.length() > 0) {
                                for (int i = 0; i < notificationsArray.length(); i++) {
                                    JSONObject notificationObject = notificationsArray.optJSONObject(i);
                                    Notification notification = new Notification();
                                    notification.setId(notificationObject.optString("id"));
                                    notification.setTitle(notificationObject.optString("title"));
                                    notification.setMessage(notificationObject.optString("message"));
                                    notification.setIsViewed("1".equals(notificationObject.optString("is_viewed")));
                                    notification.setDateCreated(notificationObject.optString("created"));
                                    notifications.add(notification);
                                }
                                ((NotificationAdapter) lvNotifications.getAdapter()).notifyDataSetChanged();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppUtils.hideProgress(dialog);
                        error.printStackTrace();
                    }
                }
        );
        AppInstance.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;  }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppUtils.REQ_LOGIN:
                if (resultCode == RESULT_OK) {
                    fetchNotificationsFromNW();
                } else {
                    finish();
                }
                break;
            default:
                break;}
    }
}
