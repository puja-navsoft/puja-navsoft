package com.afieat.ini.services;

import android.util.Log;

import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        AppInstance.getInstance(getApplicationContext()).addToSharedPref(AppUtils.SHARED_PREF_DEV_FIRE_BASE_TOKEN, refreshedToken);
        Log.d("check device token id", "" + refreshedToken);

        //AWSMobileClient.getInstance().initialize(this).execute();
        //AWSMobileClient.defaultMobileClient().getPinpointManager().getNotificationClient().registerGCMDeviceToken(refreshedToken);
    }
}
