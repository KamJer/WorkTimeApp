package com.my.WorkTimeApp.retrofit.api;

import com.my.WorkTimeApp.entity.Pracownik;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface PracownikApi {

    @GET("/api/pracownicy")
    Call<List<Pracownik>> getAllPracownicy();

    @GET("/api/pracownicy/{id}")
    Call<List<Pracownik>> getPRacownikById(@Path(value = "id") Long id);

    @POST("/api/pracownicy")
    Call<Pracownik> postPracownik(@Body Pracownik pracownik);

    @DELETE("/api/pracownicy/delete/{id}")
    Call<Void> deletePracownik (@Path(value ="id") Long id);
}
