package com.afieat.ini.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by amartya on 30/03/16 with love.
 */
public class NetworkRequest extends Request<JSONObject> {

    private final int REQ_TIMEOUT = 5000;
    private Map<String, String> params;

    String Url;
    private Listener<JSONObject> listener;
    private ErrorListener errorListener;
    private int method;

    public NetworkRequest(int method, String url, Map<String, String> params,
                          Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.Url = url;
        this.listener = reponseListener;
        this.errorListener = errorListener;
        this.params = params;
        this.method = method;

        AppUtils.nwLog(url);
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                AppUtils.nwLog(entry.getKey() + " : " + entry.getValue());
            }
        }
        setRetryPolicy(new DefaultRetryPolicy(REQ_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        setShouldCache(true);
    }

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    }
    ;

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        AppUtils.log("RESPONSE MAIn URL- " + Url+params);
        AppUtils.log("RESPONSE MAIn CLASS- " + response);
//        else
        listener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("appToken", AppUtils.APPTOKEN);
        params.put("Authorization", AppUtils.AUTHRIZATIONKEY);
        //params.put("Content-Type", "text/html");
        AppUtils.log("AuthKey- " + AppUtils.AUTHRIZATIONKEY + "?????????");
        AppUtils.log("token- " + AppUtils.APPTOKEN + "?????????");
        return params;
    }


}
