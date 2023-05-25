package com.my.WorkTimeApp.fileUtil;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.my.WorkTimeApp.EmployeeSettingsActivity;
import com.my.WorkTimeApp.MainActivity;
import com.my.WorkTimeApp.entity.Employee;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
    public static final String FILE_NAME = "Employee_name.json";

    public static void saveToFile(Context context, Gson gson, String fileName, Employee content) {
        try {

            FileWriter writer = new FileWriter(context.getFilesDir() + "/" + fileName);
            gson.toJson(content, writer);
            Log.d(EmployeeSettingsActivity.class.getName(), (context.getFilesDir().getPath()));
            writer.close();
        } catch (IOException e) {
            Log.d(EmployeeSettingsActivity.class.getName(), e.getMessage());
        }
    }

    public static Employee readFromFile(Context context, Gson gson, String fileName) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(context.getFilesDir() + "/" + fileName));
            Employee Employee = gson.fromJson(reader, Employee.class);
            return Employee;
        } catch (Exception e) {
            Log.d(MainActivity.class.getName(), e.getMessage());
        }
        return null;
    }

}
