package com.my.WorkTimeApp.retrofit.api;

import com.my.WorkTimeApp.entity.WorkTime;
import com.my.WorkTimeApp.entity.Employee;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface EmployeeApi {

    @GET("/employees/get")
    Call<List<Employee>> getAllEmployees();

    @GET("/employees/get/{id}")
    Call<List<Employee>> getEmployeeById(@Path(value = "id") Long id);
}
