package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateActivity extends AppCompatActivity {
    private static final String FILE_NAME = "tasks.json";
    //UI References
    private Spinner preset;
    private String selectedPreset;
    private EditText name;
    private Spinner category;
    private String selectedCategory;
    private TextView dueDate;
    private TextView dueTime;
    private EditText description;
    private TextView effort;
    private TextView urgency;
    private ChipGroup tags;
    private CheckBox calendar;
    private CheckBox newPreset;
    private EditText newPresetName;

    private DatePickerDialog dueDatePickerDialog;
    private TimePickerDialog dueTimePickerDialog;

    private SimpleDateFormat dateFormatter;

    private File taskFile;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        /*ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/

        findViewsById();
        initializeJSONTasks();

        preset.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                selectedPreset = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                selectedCategory = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void findViewsById() {
        preset = (Spinner) findViewById(R.id.presets);
        dueDate = (TextView) findViewById(R.id.due_date);
        dueTime = (TextView) findViewById(R.id.due_time);
        name = (EditText) findViewById(R.id.name);
        category = (Spinner) findViewById(R.id.category);
        description = (EditText) findViewById(R.id.description);
        effort = (TextView) findViewById(R.id.effort);
        urgency = (TextView) findViewById(R.id.urgency);
        tags = (ChipGroup) findViewById(R.id.tags);
        calendar = (CheckBox) findViewById(R.id.calendar);
        newPreset = (CheckBox) findViewById(R.id.newPreset);
        newPresetName = (EditText) findViewById(R.id.newPresetName);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeJSONTasks() {
        taskFile = new File(this.getFilesDir(), "tasks.json");
        Log.d("path", taskFile.getAbsolutePath());

        FileWriter fileWriter = null;

        if (!taskFile.exists()) {
            Log.d("taskFile", "created");
            try {
                taskFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fileWriter = new FileWriter(taskFile.getAbsoluteFile());
            Log.d("path 2", taskFile.getAbsolutePath());

            // add premade tasks
            Task newTask1 = new Task("Exam", "My exam", "IGR203", "06-07-2021",
                    "12:13", "My description", 3, 1, new String[]{"exam", "igr203"}, true);
            Task newTask2 = new Task("Meeting", "My meeting", "IGR203", "20-04-2021",
                    "16:00", "My description", 1, 4, new String[]{"meeting", "igr203"}, true);
            Task newTask3 = new Task("Project", "My project", "IGR203", "03-05-2021",
                    "23:59", "My description", 4, 4, new String[]{"project", "igr203"}, true);

            List<Task> tasks = Arrays.asList(newTask1, newTask2, newTask3);

            Writer writer = Files.newBufferedWriter(Paths.get(taskFile.getPath()));

            // create Gson instance
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // convert user object to JSON file
            gson.toJson(tasks, writer);

            // close writer
            writer.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(dueDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(dueTime);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void onPresetCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = newPreset.isChecked();

        TableRow addPreset = (TableRow) findViewById(R.id.addPreset);
        if (checked) {
            addPreset.setVisibility(View.VISIBLE);
        } else {
            addPreset.setVisibility(View.GONE);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCreateClicked(View view) {

        String effortText = effort.getText().toString().split(" ")[1];
        String urgencyText = urgency.getText().toString().split(" ")[1];
        Task newTask = new Task(selectedPreset, name.getText().toString(), selectedCategory, dueDate.getText().toString(),
                dueTime.getText().toString(), description.getText().toString(), Integer.parseInt(effortText), Integer.parseInt(urgencyText), new String[0], calendar.isChecked());

        // create Gson instance
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // create a writer
        Writer writer = null;
        Reader reader = null;
        try {
            Log.d("Write", "try");
            reader = Files.newBufferedReader(Paths.get(taskFile.getPath()));

            // convert JSON string to Book object
            List<Task> tasks = new ArrayList<Task>(Arrays.asList(gson.fromJson(reader, Task[].class)));
            tasks.add(newTask);

            writer = Files.newBufferedWriter(Paths.get(taskFile.getPath()));

            // convert user object to JSON file
            gson.toJson(tasks, writer);

            // close writer
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Test to read the JSON file
        // create a reader
        try {
            Log.d("Read", "try");
            reader = Files.newBufferedReader(Paths.get(taskFile.getPath()));

            // convert JSON string to Book object
            List<Task> readTasks = Arrays.asList(gson.fromJson(reader, Task[].class));

            // print book
            readTasks.forEach(System.out::println);

            // close reader
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //finish();
    }
}
