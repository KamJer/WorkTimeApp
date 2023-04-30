package com.my.WorkTimeApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.my.WorkTimeApp.entity.CzasPracy;
import com.my.WorkTimeApp.retrofit.api.CzasPracyApi;
import com.my.WorkTimeApp.retrofit.api.PracownikApi;
import com.my.WorkTimeApp.retrofit.RetrofitService;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextView startTime;
    private TextView endTime;
    private TextView labelTextView;
    private Button startButton;
    private String startButtonText;
    private String stopButtonText;
    private String beginingOfWorkLabel;
    private String endOfWorkLabel;


    private Boolean clicked = false;

    //    for testing
    private long pracownikId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startTime = findViewById(R.id.beginningOfWork_TextView);
        endTime = findViewById(R.id.endOfWork_TextView);
        labelTextView = findViewById(R.id.label_textview);

        startButton = findViewById(R.id.start_button);

        startButtonText = "Start";
        stopButtonText = "Stop";
        beginingOfWorkLabel = "Początek pracy";
        endOfWorkLabel = "Koniec Pracy";

        Log.d("OnStart", "starytuje");

        RetrofitService retrofitService = new RetrofitService();
        PracownikApi pracownikApi = retrofitService.getRetrofit().create(PracownikApi.class);
        CzasPracyApi czasPracyApi = retrofitService.getRetrofit().create(CzasPracyApi.class);

//        loading notEndedCzasPracy
//        TODO:getNotEndedCzasPracy sets ending czas pracy in a data base so by asking server for this info you are ending a shift
        czasPracyApi.getNotEndedCzasPracy(pracownikId).enqueue(new Callback<CzasPracy>() {

            @Override
            public void onResponse(Call<CzasPracy> call, Response<CzasPracy> response) {
                if (response.body() != null){
                    clicked = true;
                    LocalDateTime dateTimeBeginning = response.body().getBeginningOfWork();
                    startTime.setText(getTime(dateTimeBeginning));
                    startButton.setText(stopButtonText);
                }
            }

            @Override
            public void onFailure(Call<CzasPracy> call, Throwable t) {
                Log.d("OnFirstLoad", t.getMessage());
            }
        });

//        setting view for button
        startButton = findViewById(R.id.start_button);
//        setting text for the button
        startButton.setText(startButtonText);
//        setting behavior
        startButton.setOnClickListener(v -> {
            if (!clicked) { // if button is not started
                onStartAction(czasPracyApi);
            } else { // if button is started
                onStopAction(czasPracyApi);
            }
        });
    }

    private void onStartAction(CzasPracyApi czasPracyApi) {
//        behavior for start of work time
//        setting up a flag
        clicked = true;
//        setting text for the stop button
        startButton.setText(stopButtonText);
//        creating CzasPracy
        CzasPracy czasPracyToPost = new CzasPracy();
        czasPracyToPost.setPracownikId(pracownikId);
        czasPracyToPost.setBeginningOfWork(LocalDateTime.now());
//        sending czas pracy to server
        czasPracyApi.getNewCzasPracy(pracownikId).enqueue(new Callback<CzasPracy>() {
            @Override
            public void onResponse(Call<CzasPracy> call, Response<CzasPracy> response) {
//                getting time from response
                LocalDateTime dateTime = response.body().getBeginningOfWork();
//                setting that time in textView
                startTime.setText(getTime(dateTime));
                endTime.setText(endOfWorkLabel);
            }

            @Override
            public void onFailure(Call<CzasPracy> call, Throwable t) {
                Log.d("OnSecendLoad", t.getMessage());
            }
        });
    }

    private void onStopAction(CzasPracyApi czasPracyApi) {
//      behavior for end of work time
        clicked = false;
        startButton.setText(startButtonText);
        Log.d("MainActivity", "OnStop");
        czasPracyApi.getNotEndedCzasPracyAndEndIt(pracownikId).enqueue(new Callback<CzasPracy>() {
            @Override
            public void onResponse(Call<CzasPracy> call, Response<CzasPracy> response) {
                CzasPracy czasPracyEmployeeJustEnded = response.body();
                LocalDateTime endOfWork = czasPracyEmployeeJustEnded.getEndOfWork();
                endTime.setText(getTime(endOfWork));
            }

            @Override
            public void onFailure(Call<CzasPracy> call, Throwable t) {
                Log.d("OnThridLoad", t.getMessage());
            }
        });
    }

    public static double round2decimalNumbers(float number) {
        Log.d(MainActivity.class.getName(), String.valueOf(number));
        double roundedNumber = Math.round(number * 100.0) / 100.0;
        return roundedNumber;
    }

    public String getTime(LocalDateTime dateTime) {
        String dateTimeString = Integer.toString(dateTime.getHour());
        dateTimeString += ":" + Integer.toString(dateTime.getMinute());
        return dateTimeString;
    }
}