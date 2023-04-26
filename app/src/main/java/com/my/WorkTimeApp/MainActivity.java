package com.my.WorkTimeApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.my.WorkTimeApp.entity.CzasPracy;
import com.my.WorkTimeApp.retrofit.api.CzasPracyApi;
import com.my.WorkTimeApp.retrofit.api.PracownikApi;
import com.my.WorkTimeApp.retrofit.RetrofitService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextView startTime;
    private Button startButton;
    private String startButtonText;
    private String stopButtonText;

    private Boolean clicked = false;

//    for testing
    private long pracownikId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startTime = findViewById(R.id.empty_textview);
        startButton = findViewById(R.id.start_button);
        startButtonText = "Start";
        stopButtonText = "Stop";

        RetrofitService retrofitService = new RetrofitService();
        PracownikApi pracownikApi = retrofitService.getRetrofit().create(PracownikApi.class);
        CzasPracyApi czasPracyApi = retrofitService.getRetrofit().create(CzasPracyApi.class);
//        setting view for button
        startButton = findViewById(R.id.start_button);
//        setting text for the button
        startButton.setText(startButtonText);
//        setting behavior
        startButton.setOnClickListener(v -> {
            if (!clicked) { // if button is not started
                onStartAction(czasPracyApi);
            } else { // if button is started
                onStopAction();
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
        czasPracyApi.getNewCzasPracy(pracownikId).enqueue(new Callback<Response<CzasPracy>>() {
            @Override
            public void onResponse(Call<Response<CzasPracy>> call, Response<Response<CzasPracy>> response) {
//                startTime.setText(response.body());
                Log.d("OnResponse", response.toString());
            }

            @Override
            public void onFailure(Call<Response<CzasPracy>> call, Throwable t) {
                Log.d(MainActivity.class.getName(), t.getMessage());
            }
        });
//        czasPracyApi.postCzasPracy(czasPracyToPost).enqueue(new Callback<Boolean>() {
//            @Override
//            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
//                startTime.setText(czasPracyToPost.getBeginningOfWork().toString());
//                Log.d(MainActivity.class.getName(), czasPracyToPost.getBeginningOfWork().toString());
//            }
//
//            @Override
//            public void onFailure(Call<Boolean> call, Throwable t) {
//                    Log.d(MainActivity.class.getName(), t.getMessage());
//            }
//        });
    }

    private void onStopAction() {
//      behavior for end of work time
        clicked = false;
        startButton.setText(startButtonText);
        Log.d("MainActivity", "OnStop");
    }
}