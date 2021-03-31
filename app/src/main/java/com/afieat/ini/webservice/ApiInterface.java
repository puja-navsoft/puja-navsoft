package com.afieat.ini.webservice;


import com.afieat.ini.models.AdsModel;
import com.afieat.ini.models.ChatLink;
import com.afieat.ini.models.InProcessOrders;
import com.afieat.ini.utils.Apis;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by zabingo on 8/2/17.
 */

public interface ApiInterface {
    //  @GET(UrlConstant.GET_ALL_LIBRARY_CATEGORY)
    //  @Headers("Api-Auth-Key: 16RS11#V1@16K*Q")
    // Call<Json_Response> getAllLibraryCategory(@Query("") String blank);


    @FormUrlEncoded
    @POST("placeorder")
    @Headers({
            "appToken", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjb2RlMSI6ImUxMGFkYzM5NDliYTU5YWJiZTU2ZTA1N2YyMGY4ODNlIiwiY29kZTIiOiI2OTE4MSIsImNvZGUzIjoxNTE1NTc4ODIxfQ.UzvDXX96_orXX3V_S79IVfuWxKiCtd6DQD4DlgpKmFo",
            "Authorization", "ZmIzZjI3Yzk2NDVjNTI2OTE1MTU2NTA0MDA="
    })
    Call<Json_Response> setPlaceOrder(@Body String order);

    @POST(Apis.get_playstore_version)
/*@Headers({
"appToken", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjb2RlMSI6ImUxMGFkYzM5NDliYTU5YWJiZTU2ZTA1N2YyMGY4ODNlIiwiY29kZTIiOiI2OTE4MSIsImNvZGUzIjoxNTE1NTc4ODIxfQ.UzvDXX96_orXX3V_S79IVfuWxKiCtd6DQD4DlgpKmFo",
"Authorization","ZmIzZjI3Yzk2NDVjNTI2OTE1MTU2NTA0MDA="
})*/
    Call<PlaystoreVersionReq> get_playstore_version(@Header("appToken") String appToken, @Header("Authorization") String Authorization);


    @POST(Apis.RES_ADS)
    Call<AdsModel> get_Ads(@Header("appToken") String appToken, @Header("Authorization") String Authorization);


    @POST(Apis.GET_UNDELIVER_ORDERS)
    @FormUrlEncoded
    Call<List<InProcessOrders>> getUndeliveredOrders(@Header("appToken") String appToken, @Header("Authorization") String Authorization, @Field("user_id") String  user_id);

    @POST(Apis.CHAT_URL_USER_ID)
    @FormUrlEncoded
    Call<ChatLink> getChat(@Field("a") String  name, @Field("b") String  email, @Field("c") String  phone);

}
