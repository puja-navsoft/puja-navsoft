package com.afieat.ini.webservice;

import androidx.annotation.NonNull;

import com.afieat.ini.utils.Apis;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zabingo on 8/2/17.
 */

public class ApiClient {
    private static Retrofit retrofit = null;

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Json_Response.class, new ArrayObjectDualityDeserializer())
            .create();


    public static Retrofit GetClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.MINUTES)
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Apis.BASE)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }


    @NonNull
    public static RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    public static class ArrayObjectDualityDeserializer implements JsonDeserializer<Json_Response> {

        public Json_Response deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Json_Response response = new Json_Response();
            JsonObject object = json.getAsJsonObject();
            //  response.setResponseCode(object.get("response_code").getAsInt());
            //response.setResponseMsg(object.get("response_msg").getAsString());

            /*
            if (object.get("response_data").isJsonArray()) {
                try {
                    LibraryCategory[] libraryCategories = gson.fromJson(object.get("response_data"), LibraryCategory[].class);
                    response.setLibraryCategories(libraryCategories);
                } catch (JsonSyntaxException e) {

                }



            } else if (object.get("response_data").isJsonObject()) {
                try {
                    LaughofTheDay laughofTheDay = gson.fromJson(object.get("response_data"), LaughofTheDay.class);
                    response.setLaughOfday(laughofTheDay);
                } catch (JsonSyntaxException e) {

                }



            } else if (object.get("response_data").isJsonNull()) {

            }
*/
            return response;
        }
    }
}
