package com.my.WorkTimeApp.retrofit.api;

import com.my.WorkTimeApp.entity.CzasPracy;

import java.time.LocalTime;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CzasPracyApi {
    @GET("/czas-pracy/get-not-ended-czasPracy/{pracownikId}")
    Call<CzasPracy> getNotEndedCzasPracy(@Path(value = "pracownikId") Long pracownikId);

    @POST("/czas-pracy/post")
    Call<Boolean> postCzasPracy(@Body CzasPracy CzasPracy);

    @GET("/czas-pracy/get-new-czasPracy/{pracownikId}")
    Call<CzasPracy> getNewCzasPracy(@Path(value = "pracownikId") Long pracownikId);

    @GET("/czas-pracy/get-not-ended-czasPracy-and-end-it/{pracownikId}")
    Call<CzasPracy> getNotEndedCzasPracyAndEndIt(@Path(value = "pracownikId") Long pracownikId);
}
