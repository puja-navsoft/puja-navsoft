package com.afieat.ini.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.vlk.multimager.utils.Image;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by amartya on 22/09/17 with love.
 */

public class K_ApiCall {
    public interface APICall {
        void Onsuccess();

        void OnError(String ex);
    }

    public interface OnResponse {
        void OnSucess(String Response);

        void OnError(String Error);
    }


    public void PhotoUplaodAPICALL(final String url, final JSONObject ParamsObject, final ArrayList<Image> Files, final APICall apiCall) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                AppUtils.log("@@ API-" + url);

            }

            @Override
            protected String doInBackground(Void... voids) {
                Log.d("@@--", "Start" + ParamsObject);
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                if (Files != null && Files.size() > 0)
                    for (int i = 0; i < Files.size(); i++) {
                        if (Files.get(i).uri != null) {
                            File file = new File(Files.get(i).imagePath);
                            if (file.exists()) {
                                MediaType MEDIA_TYPE = MediaType.parse("image/jpeg");
                                builder.addFormDataPart("review_image[" + i + "]", file.getName(), RequestBody.create(MEDIA_TYPE, file));

                            } else {
                                Log.d(TAG, "file not exist ");
                            }
                        }


                    }


//                MediaType JSON
//                        = MediaType.parse("application/json; charset=utf-8");
//                builder.addPart(RequestBody.create(JSON,ParamsObject.toString()));

                try {
                    builder.addFormDataPart("user_id", ParamsObject.getString("user_id"));
                    builder.addFormDataPart("resid", ParamsObject.getString("resid"));
                    builder.addFormDataPart("ratecount", ParamsObject.getString("ratecount"));
                    builder.addFormDataPart("review", ParamsObject.getString("review"));
                    RequestBody requestBody = builder.build();


                    Request request = new Request.Builder()

                            .url(url)
                            .post(requestBody)
                            .addHeader("Authorization", AppUtils.AUTHRIZATIONKEY)
                            .build();
                    Response response = client.newCall(request).execute();
                    return response.body().string();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("@@--", "Exception " + e.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String aVoid) {
                super.onPostExecute(aVoid);
                if (aVoid != null)
                    try {
                        JSONObject object = new JSONObject(aVoid);
                        if (object.getInt("code") == 0) {
                            apiCall.OnError(object.getString("msg"));
                        } else {
                            apiCall.Onsuccess();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                Log.d("@@--", "End " + aVoid);
            }
        }.execute();
    }


    public void PostApiCall(final String URL, final JSONObject Params, final OnResponse apiCall) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {


                try {
                    OkHttpClient client = new OkHttpClient();
                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(mediaType, getParams(Params));
                    Request request = new Request.Builder()
                            .url(URL)
                            .post(body)
                            .addHeader("Authorization", AppUtils.AUTHRIZATIONKEY)
                            .addHeader("content-type", "application/x-www-form-urlencoded")
                            .build();

                    Response response = client.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }


            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    apiCall.OnSucess(s);
                }
            }
        }.execute();
    }

    private String getParams(JSONObject params) throws Exception {
        Iterator keysToCopyIterator = params.keys();
        String Params = "";
        while (keysToCopyIterator.hasNext()) {
            String key = (String) keysToCopyIterator.next();
            String values = params.getString(key);
            Params = values.length() > 0 ? Params + key + "=" + values + "&" : "";
        }
        Log.d("@@#- ", Params);
        return Params;
    }

}
