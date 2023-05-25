package com.my.WorkTimeApp.retrofit.api;

import com.my.WorkTimeApp.entity.WorkTime;

import java.time.LocalTime;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WorkTimeApi {
    @GET("/work-time/get-not-ended-WorkTime/{employeeId}")
    Call<WorkTime> getNotEndedWorkTime(@Path(value = "employeeId") Long employeeId);

    @POST("/work-time/post")
    Call<Boolean> postWorkTime(@Body WorkTime WorkTime);

    @GET("/work-time/get-new-WorkTime/{employeeId}")
    Call<WorkTime> getNewWorkTime(@Path(value = "employeeId") Long employeeId);

    @GET("/work-time/get-not-ended-workTime-and-end-it/{employeeId}")
    Call<WorkTime> getNotEndedWorkTimeAndEndIt(@Path(value = "employeeId") Long employeeId);
}
