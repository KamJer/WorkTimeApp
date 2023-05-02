package com.my.WorkTimeApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.my.WorkTimeApp.entity.CzasPracy;
import com.my.WorkTimeApp.entity.Pracownik;
import com.my.WorkTimeApp.fileUtil.FileUtil;
import com.my.WorkTimeApp.retrofit.api.CzasPracyApi;
import com.my.WorkTimeApp.retrofit.RetrofitService;

import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//TODO: change name this one is for development only
public class MainActivity extends AppCompatActivity {
//    defining different elements of a view
    private TextView startTime;
    private TextView endTime;
    private Button startButton;
    private String startButtonText;
    private String stopButtonText;
    private String beginningOfWorkLabel;
    private String endOfWorkLabel;

//    flags and intents
    private Boolean clicked = false;
    private Intent settingsIntent;
    private Gson gson = new Gson();
    private CzasPracyApi czasPracyApi;

//    private Toast errorToast;

//    active employee
    private Pracownik activeEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startTime = findViewById(R.id.beginningOfWork_TextView);
        endTime = findViewById(R.id.endOfWork_TextView);

        startButton = findViewById(R.id.start_button);

        startButtonText = "Start";
        stopButtonText = "Stop";
        endOfWorkLabel = "Koniec Pracy";
        beginningOfWorkLabel = "Początek Pracy";

        RetrofitService retrofitService = new RetrofitService();
        czasPracyApi = retrofitService.getRetrofit().create(CzasPracyApi.class);
//        setting text for the button
        startButton.setText(startButtonText);
//        setting behavior
        startButton.setOnClickListener(v -> {
            if (!clicked) { // if button is not started
                onStartAction(czasPracyApi);
            } else {        // if button is started
                onStopAction(czasPracyApi);
            }
        });
//      initializing intent and loading active employee from file, if such employee (it's first time app is running or for some reason file was deleted)
//      activity for selecting active employee is started, if not app load data from serwer
        settingsIntent = new Intent(this, EmployeeSettingsActivity.class);
        activeEmployee = FileUtil.readFromFile(this, gson, FileUtil.FILE_NAME);
        if (activeEmployee == null) {
            startActivity(settingsIntent);
        } else {
//        loading notEndedCzasPracy
            loadData(czasPracyApi);
        }
    }

    /**
     * For loading data from server
     * @param czasPracyApi
     */
    private void loadData(CzasPracyApi czasPracyApi) {
        startTime.setText(beginningOfWorkLabel);
        endTime.setText(endOfWorkLabel);

        czasPracyApi.getNotEndedCzasPracy(activeEmployee.getId()).enqueue(new Callback<CzasPracy>() {

            @Override
            public void onResponse(Call<CzasPracy> call, Response<CzasPracy> response) {
//                if a body of a response is null no data was found
                if (response.body() != null) {
                    clicked = true;
                    LocalDateTime dateTimeBeginning = response.body().getBeginningOfWork();
                    startTime.setText(getTime(dateTimeBeginning));
                    endTime.setText(endOfWorkLabel);
                    startButton.setText(stopButtonText);
                } else {
//                    code 404 can be returned when there is something wrong but it can also be returned when all shifts are ended
                    makeToast("Http: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CzasPracy> call, Throwable t) {
                Log.d(MainActivity.class.getName(), t.getMessage());
                makeToast(t.getMessage());
            }
        });
    }

    /**
     * action on start of a shift button being pressed
     * @param czasPracyApi
     */
    private void onStartAction(CzasPracyApi czasPracyApi) {
        activeEmployee = FileUtil.readFromFile(this, gson, FileUtil.FILE_NAME);
//        behavior for start of work time
//        setting up a flag
        clicked = true;
//        setting text for the stop button
        startButton.setText(stopButtonText);
//        creating CzasPracy
        CzasPracy czasPracyToPost = new CzasPracy();
        czasPracyToPost.setPracownikId(activeEmployee.getId());
        czasPracyToPost.setBeginningOfWork(LocalDateTime.now());
//        sending czas pracy to server
        czasPracyApi.getNewCzasPracy(activeEmployee.getId()).enqueue(new Callback<CzasPracy>() {
            @Override
            public void onResponse(Call<CzasPracy> call, Response<CzasPracy> response) {
                if (response.body() != null) {
//                getting time from response
                    LocalDateTime dateTime = response.body().getBeginningOfWork();
//                setting that time in textView
                    startTime.setText(getTime(dateTime));
                    endTime.setText(endOfWorkLabel);
                } else {
                    makeToast("Http: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CzasPracy> call, Throwable t) {
                Log.d(MainActivity.class.getName(), t.getMessage());
                makeToast(t.getMessage());
            }
        });
    }

    /**
     * action on end of a shift being pressed
     * @param czasPracyApi
     */
    private void onStopAction(CzasPracyApi czasPracyApi) {
//      behavior for end of work time
//        loading active employee from file, double checking if everything is alright before ending shift
        activeEmployee = FileUtil.readFromFile(this, gson, FileUtil.FILE_NAME);
//        setting proper flag and setting button to proper vaule
        clicked = false;
        startButton.setText(startButtonText);
//        loading data form data base and ending it(data is being receded for a situation when app is closed and started shift is forgotten)
        czasPracyApi.getNotEndedCzasPracyAndEndIt(activeEmployee.getId()).enqueue(new Callback<CzasPracy>() {
            @Override
            public void onResponse(Call<CzasPracy> call, Response<CzasPracy> response) {
                if (response.body() != null) {
                    CzasPracy czasPracyEmployeeJustEnded = response.body();
                    LocalDateTime endOfWork = czasPracyEmployeeJustEnded.getEndOfWork();
                    endTime.setText(getTime(endOfWork));
                } else {
                    makeToast("Http: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CzasPracy> call, Throwable t) {
                Log.d(MainActivity.class.getName(), t.getMessage());
                makeToast(t.getMessage());
            }
        });
    }

    /**
     * gets time from object in to string for textView
     * @param dateTime
     * @return
     */
    public String getTime(LocalDateTime dateTime) {
        String dateTimeString = Integer.toString(dateTime.getHour());
        dateTimeString += ":" + Integer.toString(dateTime.getMinute());
        return dateTimeString;
    }

    /**
     * Method for starting settings activity
     * this method is called in activity layout with onClick
     * @param view
     */
    public void openSettingsActivity(View view) {
        startActivity(settingsIntent);
    }

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if employye is null it means it is not selected or loaded yet and data can not be loaded yet
        activeEmployee = FileUtil.readFromFile(this, gson, FileUtil.FILE_NAME);
        if (activeEmployee != null) {
            loadData(czasPracyApi);
        }
    }
}