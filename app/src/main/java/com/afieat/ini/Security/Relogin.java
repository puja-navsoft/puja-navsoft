package com.afieat.ini.Security;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.afieat.ini.R;
import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppInstance;
import com.afieat.ini.utils.AppUtils;
import com.afieat.ini.utils.AuthkeyValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by amartya on 10/11/17 with love.
 */

public class Relogin extends AsyncTask<Void, String, String> {

    private ProgressDialog Pdialog;
    private Context mcontext;
    private String Email = "";
    private String Password = "";
    private String DeviceToken = "";
    private OnLoginlistener onLoginlistener;

    public interface OnLoginlistener {
        void OnLoginSucess();

        void OnError(String Errormessage);
    }


    public Relogin(Context context, OnLoginlistener onLoginlistener) {
        this.onLoginlistener = onLoginlistener;
        this.mcontext = context;
        Pdialog = new ProgressDialog(mcontext);
        Pdialog.setMessage(mcontext.getString(R.string.msg_please_wait));
        Pdialog.setCanceledOnTouchOutside(false);
        Pdialog.setCancelable(false);
        Password = AppInstance.getInstance(mcontext).getFromSharedPref(AppUtils.SHARED_PREF_USER_PASSWORD);
        Email = AppInstance.getInstance(mcontext).getFromSharedPref(AppUtils.PREF_USER_EMAIL);
        DeviceToken = AppInstance.getInstance(mcontext).getFromSharedPref(AppUtils.SHARED_PREF_DEV_FIRE_BASE_TOKEN);
    }


    @Override
    protected String doInBackground(Void... voids) {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "username_login=" + Email + "&password=" + Password + "&user_device_token=" + DeviceToken + "&registered_from=" + 2);
        Request request = new Request.Builder()
                .url(Apis.LOG_IN)
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", AppUtils.AUTHRIZATIONKEY)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Pdialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        AppUtils.log("@@ RELOGIN-" + s);
        if (s != null) {
            try {
                JSONObject response = new JSONObject(s);
                if (response.optInt("status") == 111) {
                    new AuthkeyValidator(mcontext).CallForReAuth(new AuthkeyValidator.Authkey() {
                        @Override
                        public void Oncomplete() {
                            Pdialog.dismiss();
                            new Relogin(mcontext, onLoginlistener).execute();
                        }
                    });
                } else if (response.optString("status").equals("0")) {

                    onLoginlistener.OnError("0");
                } else if (response.optString("status").equals("1")) {
                    String id = response.optString("id");
                    String name = response.optString("name");
                    AppInstance.getInstance(mcontext).addToSharedPref(AppUtils.PREF_USER_ID, id);
                    AppInstance.getInstance(mcontext).addToSharedPref(AppUtils.PREF_USER_FNAME, name);
                    AppInstance.getInstance(mcontext).setAuthkey(response.optString("appToken"));

                    AppUtils.APPTOKEN = response.optString("appToken");
                    Pdialog.dismiss();
                    onLoginlistener.OnLoginSucess();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
