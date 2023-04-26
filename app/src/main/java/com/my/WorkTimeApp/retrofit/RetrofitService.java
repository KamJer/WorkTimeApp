package com.my.WorkTimeApp.retrofit;

import android.util.Log;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private Retrofit retrofit;
//    for testing
    private String ip = "192.168.42.127";
    private int port = 8080;

   public RetrofitService() {
       String url = "http://" + ip + ":" +  String.valueOf(port);
       Log.d(RetrofitService.class.getName(), url);
        retrofit = new Retrofit.Builder()
                .baseUrl(url).addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
