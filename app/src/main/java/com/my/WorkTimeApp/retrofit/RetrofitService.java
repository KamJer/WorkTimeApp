package com.my.WorkTimeApp.retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private Retrofit retrofit;
//    for testing
    private String ip = "192.168.1.85";
    private int port = 8080;

   public RetrofitService() {
       String url = "http://" + ip + ":" +  String.valueOf(port);
       Log.d(RetrofitService.class.getName(), url);
       Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
           @Override
           public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                   throws JsonParseException {
               return LocalDateTime.parse(json.getAsString());
           }
       }).create();
        retrofit = new Retrofit.Builder()
                .baseUrl(url).addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
