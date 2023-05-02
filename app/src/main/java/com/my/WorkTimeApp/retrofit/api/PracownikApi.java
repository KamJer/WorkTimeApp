package com.my.WorkTimeApp.retrofit.api;

import com.my.WorkTimeApp.entity.CzasPracy;
import com.my.WorkTimeApp.entity.Pracownik;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface PracownikApi {

    @GET("/pracownicy/get")
    Call<List<Pracownik>> getAllPracownicy();

    @GET("/pracownicy/get/{id}")
    Call<List<Pracownik>> getPracownikById(@Path(value = "id") Long id);
}
