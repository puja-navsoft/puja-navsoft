package com.afieat.ini.utils;

import android.content.Context;
import android.provider.Settings;

import com.afieat.ini.Security.WithOutLoginSecurityPermission;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by amartya on 17/11/17 with love.
 */

public class AuthkeyValidator {
    private Context mContext;

    public Authkey authkey;

    public interface Authkey {
        void Oncomplete();
    }


    public AuthkeyValidator() {
    }

    public AuthkeyValidator(final Context mContext) {
        this.mContext = mContext;
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(df.parse(AppInstance.getInstance(mContext).GetAuthkeyCreatedate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar today = Calendar.getInstance();
//        if(cal.equals(today))
//        {
//            authkey.Oncomplete();
//        }else

    }


    public void CallForReAuth(final Authkey authkey) {
        this.authkey = authkey;
        {
            String deviceId = Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            new WithOutLoginSecurityPermission(new WithOutLoginSecurityPermission.OnSecurityResult() {
                @Override
                public void OnAllowPermission(String AuthToken) {
                    AppUtils.AUTHRIZATIONKEY = AuthToken;
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                    String DateCreate = format.format(calendar.getTime());
                    AppInstance.getInstance(mContext).setAuthkeyforall(AuthToken, DateCreate);
                    authkey.Oncomplete();
                    AppUtils.log("@@ Regen Authkeyyyyy-" + AuthToken);
                }

                @Override
                public void OnRejectPermission() {

                }
            }).execute(deviceId);
        }
    }


}
