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
    List<Pracownik> employees = new ArrayList<>();

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
//
//        pracownikSpinnerAdapter = new ArrayAdapter<Pracownik>(EmployeeSettingsActivity.this, R.layout.support_simple_spinner_dropdown_item, employees);
//        String pracownik = readFromFile();
        Pracownik savedPracownik = readFromFile();
        Log.d(EmployeeSettingsActivity.class.getName(), savedPracownik.toString());


        pracownikApi.getAllPracownicy().enqueue(new Callback<List<Pracownik>>() {
            @Override
            public void onResponse(Call<List<Pracownik>> call, Response<List<Pracownik>> response) {
                Log.d("OnEmployeeLoad", String.valueOf(response.code()));
                if (response.body() != null) {
                    employees = response.body();
                    pracownikSpinnerAdapter = new ArrayAdapter<Pracownik>(EmployeeSettingsActivity.this, R.layout.support_simple_spinner_dropdown_item, employees);
                    pracownikSpinner.setAdapter(pracownikSpinnerAdapter);
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
        saveToFile(((Pracownik) pracownikSpinner.getSelectedItem()));
    }

    public void saveToFile(Pracownik content) {
        try {

            FileWriter writer = new FileWriter(getApplicationContext().getFilesDir() + "/" + fileName);
            gson.toJson(content, writer);
//            writer.write(content.forSave());
//            writer.close();
            Log.d(EmployeeSettingsActivity.class.getName(), (getApplicationContext().getFilesDir().getPath()));
        } catch (IOException e) {
            Log.d(EmployeeSettingsActivity.class.getName(), e.getMessage());
        }
    }

    private Pracownik readFromFile() {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(getApplicationContext().getFilesDir() + "/" + fileName));
            Pracownik pracownik = gson.fromJson(reader, Pracownik.class);
            Log.d(EmployeeSettingsActivity.class.getName(), "wybór");
            return pracownik;
//            File file = new File(getApplicationContext().getFilesDir(), fileName);
//            FileInputStream inputStream = new FileInputStream(file);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuilder stringBuilder = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                stringBuilder.append(line).append(" ");
//            }
//            reader.close();
//            inputStream.close();
//            readFile = stringBuilder.toString();
//            return stringBuilder.toString();
        } catch (IOException e) {
            Log.d(MainActivity.class.getName(), e.getMessage());
            errorToast.setText(e.getMessage());
            errorToast.show();
        }
        return null;
    }
}
