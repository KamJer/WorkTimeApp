package com.my.WorkTimeApp;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.my.WorkTimeApp.entity.Pracownik;
import com.my.WorkTimeApp.fileUtil.FileUtil;
import com.my.WorkTimeApp.retrofit.RetrofitService;
import com.my.WorkTimeApp.retrofit.api.PracownikApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeSettingsActivity extends AppCompatActivity {
    private Button saveButton;
    private Spinner pracownikSpinner;

    private String fileName = "pracownik_name.json";
    ArrayAdapter<Pracownik> pracownikSpinnerAdapter;
    List<Pracownik> employees;
    private Pracownik savedPracownik;

    private Toast errorToast;

    private Gson gson = new Gson();
    private boolean onCreatSpinner = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_settings_activity);

        saveButton = findViewById(R.id.submit_button);
        pracownikSpinner = findViewById(R.id.employee_name_spinner);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        errorToast = new Toast(this);

        RetrofitService retrofitService = new RetrofitService();
        PracownikApi pracownikApi = retrofitService.getRetrofit().create(PracownikApi.class);

        pracownikApi.getAllPracownicy().enqueue(new Callback<List<Pracownik>>() {
            @Override
            public void onResponse(Call<List<Pracownik>> call, Response<List<Pracownik>> response) {
                Log.d("OnEmployeeLoad", String.valueOf(response.code()));
                if (response.body() != null) {
                    employees = response.body();
                    makeAdapterAndAddAdapter(pracownikSpinner, employees);
                    savedPracownik = FileUtil.readFromFile(EmployeeSettingsActivity.this, gson, fileName);
                    if (savedPracownik != null) {
                        int indexInSpinner = findInSpinnerId(pracownikSpinner, savedPracownik);
                        pracownikSpinner.setSelection(indexInSpinner);
                    }
                } else {
                    errorToast.setDuration(Toast.LENGTH_LONG);
                    errorToast.setText("Htpp code: " + response.code());
                    errorToast.show();
                }
            }

            @Override
            public void onFailure(Call<List<Pracownik>> call, Throwable t) {
                Log.d(EmployeeSettingsActivity.class.getName(), t.getMessage());
            }
        });



        pracownikSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!onCreatSpinner) {
                    onSaveButton(pracownikApi);
                }
                onCreatSpinner = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        saveButton.setOnClickListener(v -> {
            onSaveButton(pracownikApi);
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSaveButton(PracownikApi pracownikApi) {
        FileUtil.saveToFile(this, gson, fileName, ((Pracownik) pracownikSpinner.getSelectedItem()));
    }

    /**
     * looks for an pracownik in a spinner that is the same with a passed pracownik
     * @param spinner in witch a employee is looked for
     * @param pracownikToFind
     * @return index in a spinner for passed employee, -1 if there is no match
     */
    private int findInSpinnerId(Spinner spinner, Pracownik pracownikToFind) {
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            Pracownik pracownik = (Pracownik) spinner.getAdapter().getItem(i);
            if (pracownik.equals(pracownikToFind)){
                return i;
            }
        }
        return -1;
    }

    private void makeAdapterAndAddAdapter(Spinner spinner, List<Pracownik> elements) {
        ArrayAdapter<Pracownik> adapter = new ArrayAdapter<Pracownik>(EmployeeSettingsActivity.this, R.layout.support_simple_spinner_dropdown_item, elements);
        spinner.setAdapter(adapter);
    }
}
