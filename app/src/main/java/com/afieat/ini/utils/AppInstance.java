package com.afieat.ini.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by amartya on 30/03/16 with love.
 */
public class AppInstance {

    private static AppInstance mInstance;
    private RequestQueue mRequestQueue;
    private Context mContext;
    private SharedPreferences sharedPref;

    public AppInstance() {
    }

    public Context getmContext() {
        return mContext;
    }

    private AppInstance(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        sharedPref = mContext.getSharedPreferences(AppUtils.SHARED_PREF_FILENAME, Context.MODE_PRIVATE);
    }

    public static synchronized AppInstance getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppInstance(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public void addToRequestQueue(Request request) {
        getRequestQueue().add(request);
    }

    public void  addToSharedPref(String key, String val) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, val);
        editor.apply();
    }

    public String getFromSharedPref(String key) {
        return sharedPref.getString(key, "");
    }

    /*
    @ Koushik Set Authentication key in app 30.10.17
     */

    public void setAuthkey(String Authkey) {
        AppUtils.APPTOKEN = Authkey;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("AUTHKEY", Authkey);
        editor.apply();
    }

    public String getAuthkey() {
        AppUtils.APPTOKEN = sharedPref.getString("AUTHKEY", "");
         return sharedPref.getString("AUTHKEY", "");
    }

    public void setAuthkeyforall(String Authkey, String date) {
        AppUtils.AUTHRIZATIONKEY = Authkey;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("AUTHKEYALL", Authkey);
        editor.putString("CREATE_DATE", date);
        editor.apply();
    }

    public String getAuthkeyforall() {
        AppUtils.AUTHRIZATIONKEY = sharedPref.getString("AUTHKEYALL", "");
        return sharedPref.getString("AUTHKEYALL", "");
    }

    public String GetAuthkeyCreatedate() {
        return sharedPref.getString("CREATE_DATE", "");
    }

    public void setCurrentResName(String resName) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("resName", resName);
        editor.apply();
    }

    public String getCurrentResName() {
        return sharedPref.getString("resName", "");
    }
    public void setCurrentResNameAR(String resName) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("resNameAr", resName);
        editor.apply();
    }

    public String getCurrentResNameAR() {
        return sharedPref.getString("resNameAr", "");
    }

    public void setCurrentResImage(String resImage) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("resImage", resImage);
        editor.apply();
    }

    public String getCurrentResImage() {
        return sharedPref.getString("resImage", "");
    }
}
