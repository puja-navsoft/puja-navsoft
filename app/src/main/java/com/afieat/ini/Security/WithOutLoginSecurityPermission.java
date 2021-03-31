package com.afieat.ini.Security;

import android.os.AsyncTask;

import com.afieat.ini.utils.Apis;
import com.afieat.ini.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by amartya on 09/11/17 with love.
 */

public class WithOutLoginSecurityPermission extends AsyncTask<String, String, String> {
    public interface OnSecurityResult {
        void OnAllowPermission(String AuthToken);

        void OnRejectPermission();
    }

    OnSecurityResult onSecurityResult;
    String DeviceID = "";

    public WithOutLoginSecurityPermission(OnSecurityResult onSecurityResult) {
        this.onSecurityResult = onSecurityResult;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s != null && s.equals("Retry"))
            new WithOutLoginSecurityPermission(onSecurityResult).execute(DeviceID);
        else if (s != null) {
            onSecurityResult.OnAllowPermission(s);
        } else {
            onSecurityResult.OnRejectPermission();
        }
    }

    @Override
    protected String doInBackground(String... strings) {

        DeviceID = strings[0];
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "device_type=Android&device_id=" + strings[0]);
        Request request = new Request.Builder()
                .url(Apis.UPDATEDEVICEID)
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();
        try {
            Response response = client.newCall(request).execute();
           // String respn = response.body().string();

            AppUtils.log("@@ DEVICE ID UPDATE - " + Apis.GET_KEY_WITHOUTLOGIN + response.body().string());
//            new JSONObject(response.body().string().replace("\\s",""));
//            int rescode=new JSONObject(response.body().string().replace("\\s","")).optInt("code");
            if (response.code() == 200) {
                Request request2 = new Request.Builder()
                        .url(Apis.GET_KEY_WITHOUTLOGIN)
                        .addHeader("Authorization", strings[0])
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .get()
                        .build();
                Response respon = client.newCall(request2).execute();

                if (respon.code() == 200) {
                    String respns = respon.body().string();
                    AppUtils.log("@@ GET AUTH TOKEN - " + respns);
                    if (new JSONObject(respns).optInt("status") == 1)
                        return new JSONObject(respns).optString("key");
                } else {
                    return "Retry";
                }
            } else {
                return "Retry";
            }
        } catch (IOException e) {
            e.printStackTrace();
            AppUtils.log("@@ Error1-" + e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
