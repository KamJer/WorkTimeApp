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
import com.my.WorkTimeApp.entity.Employee;
import com.my.WorkTimeApp.fileUtil.FileUtil;
import com.my.WorkTimeApp.retrofit.RetrofitService;
import com.my.WorkTimeApp.retrofit.api.EmployeeApi;

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
    private Spinner EmployeeSpinner;

    private String fileName = "Employee_name.json";
    ArrayAdapter<Employee> EmployeeSpinnerAdapter;
    List<Employee> employees;
    private Employee savedEmployee;

    private Toast errorToast;

    private Gson gson = new Gson();
    private boolean onCreatSpinner = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_settings_activity);

        saveButton = findViewById(R.id.submit_button);
        EmployeeSpinner = findViewById(R.id.employee_name_spinner);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        errorToast = new Toast(this);

        RetrofitService retrofitService = new RetrofitService();
        EmployeeApi EmployeeApi = retrofitService.getRetrofit().create(EmployeeApi.class);

        EmployeeApi.getAllEmployees().enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                Log.d("OnEmployeeLoad", String.valueOf(response.code()));
                if (response.body() != null) {
                    employees = response.body();
                    makeAdapterAndAddAdapter(EmployeeSpinner, employees);
                    savedEmployee = FileUtil.readFromFile(EmployeeSettingsActivity.this, gson, fileName);
                    if (savedEmployee != null) {
                        int indexInSpinner = findInSpinnerId(EmployeeSpinner, savedEmployee);
                        EmployeeSpinner.setSelection(indexInSpinner);
                    }
                } else {
                    errorToast.setDuration(Toast.LENGTH_LONG);
                    errorToast.setText("Htpp code: " + response.code());
                    errorToast.show();
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                Log.d(EmployeeSettingsActivity.class.getName(), t.getMessage());
            }
        });



        EmployeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!onCreatSpinner) {
                    onSaveButton(EmployeeApi);
                }
                onCreatSpinner = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        saveButton.setOnClickListener(v -> {
            onSaveButton(EmployeeApi);
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

    private void onSaveButton(EmployeeApi EmployeeApi) {
        FileUtil.saveToFile(this, gson, fileName, ((Employee) EmployeeSpinner.getSelectedItem()));
    }

    /**
     * looks for an Employee in a spinner that is the same with a passed Employee
     * @param spinner in witch a employee is looked for
     * @param EmployeeToFind
     * @return index in a spinner for passed employee, -1 if there is no match
     */
    private int findInSpinnerId(Spinner spinner, Employee EmployeeToFind) {
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            Employee Employee = (Employee) spinner.getAdapter().getItem(i);
            if (Employee.equals(EmployeeToFind)){
                return i;
            }
        }
        return -1;
    }

    private void makeAdapterAndAddAdapter(Spinner spinner, List<Employee> elements) {
        ArrayAdapter<Employee> adapter = new ArrayAdapter<Employee>(EmployeeSettingsActivity.this, R.layout.support_simple_spinner_dropdown_item, elements);
        spinner.setAdapter(adapter);
    }
}
