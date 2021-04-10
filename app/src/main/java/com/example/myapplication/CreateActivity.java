package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private List<Task> tasks;

    private File presetFile;
    private List<Preset> presets;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        /*ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/

        findViewsById();

        // Remove from this file after linking with main activity
        initializeJSONTasks();
        initializeJSONPresets();

        readTasks();
        readPresets();

        //Initialize display regarding the given task (new task or modifying a task?)
        // TODO

        preset.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                selectedPreset = parent.getItemAtPosition(pos).toString();

                if (pos != 0) {
                    for (Preset p : presets) {
                        if (p.getName().equals(selectedPreset)) {
                            selectedCategory = p.getCategory();
                            effort.setText("Effort: " + p.getEffort());
                            urgency.setText("Urgency: " + p.getUrgency());
                            calendar.setChecked(p.isCalendar());
                        }
                    }

                    category.setSelection(((ArrayAdapter) category.getAdapter()).getPosition(selectedCategory));
                    TableRow categoryRow = (TableRow) findViewById(R.id.newCategory);
                    categoryRow.setVisibility(View.GONE);
                }

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

                TableRow categoryRow = (TableRow) findViewById(R.id.newCategory);
                if (selectedCategory.equals("New category")) {
                    categoryRow.setVisibility(View.VISIBLE);
                } else {
                    categoryRow.setVisibility(View.GONE);
                }
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
    private void readTasks() {
        // create a reader
        Reader reader = null;
        try {
            Log.d("Read", "try");
            reader = Files.newBufferedReader(Paths.get(taskFile.getPath()));

            // create Gson instance
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // convert JSON string to Book object
            tasks = Arrays.asList(gson.fromJson(reader, Task[].class));

            // print book
            tasks.forEach(System.out::println);

            // close reader
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void readPresets() {
        // create a reader
        Reader reader = null;
        try {
            Log.d("Read", "try");
            reader = Files.newBufferedReader(Paths.get(presetFile.getPath()));

            // create Gson instance
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // convert JSON string to Book object
            presets = Arrays.asList(gson.fromJson(reader, Preset[].class));

            // print book
            presets.forEach(System.out::println);

            // close reader
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Remove from this file after linking with main activity
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
            Task newTask1 = new Task("Exam", "My exam", "Exam", "06-07-2021",
                    "12:13", "My description", 3, 4, new String[]{"exam", "urgent"}, true);
            Task newTask2 = new Task("Meeting", "My meeting", "Meeting", "20-04-2021",
                    "16:00", "My description", 1, 1, new String[]{"meeting"}, false);
            Task newTask3 = new Task("Project", "My project", "Project", "03-05-2021",
                    "23:59", "My description", 4, 2, new String[]{"project", "hard"}, true);

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

    // Remove from this file after linking with main activity
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeJSONPresets() {
        presetFile = new File(this.getFilesDir(), "presets.json");
        Log.d("path", presetFile.getAbsolutePath());

        FileWriter fileWriter = null;

        if (!presetFile.exists()) {
            Log.d("presetFile", "created");
            try {
                presetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fileWriter = new FileWriter(presetFile.getAbsoluteFile());
            Log.d("path 2", presetFile.getAbsolutePath());

            // add premade presets
            Preset newPreset1 = new Preset("Exam", "Exam", 3, 4, new String[]{"exam", "urgent"}, true);
            Preset newPreset2 = new Preset("Project", "Project", 4, 2, new String[]{"project", "hard"}, true);
            Preset newPreset3 = new Preset("Meeting", "Meeting", 1, 1, new String[]{"meeting"}, false);

            List<Preset> presets = Arrays.asList(newPreset1, newPreset2, newPreset3);

            Writer writer = Files.newBufferedWriter(Paths.get(presetFile.getPath()));

            // create Gson instance
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // convert user object to JSON file
            gson.toJson(presets, writer);

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

        Log.d("Begin", "try");

        String effortText = effort.getText().toString().split(" ")[1];
        String urgencyText = urgency.getText().toString().split(" ")[1];

        // Adapt --> you receive a task and actualize it
        // TODO
        Task newTask = new Task(selectedPreset, name.getText().toString(), selectedCategory, dueDate.getText().toString(),
                dueTime.getText().toString(), description.getText().toString(), Integer.parseInt(effortText), Integer.parseInt(urgencyText), new String[0], calendar.isChecked());

        tasks.add(newTask);

        // create Gson instance
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // create a writer
        Writer writer = null;
        try {
            Log.d("Write", "Add/Modify the new task in the JSON file");

            writer = Files.newBufferedWriter(Paths.get(taskFile.getPath()));

            // convert user object to JSON file
            gson.toJson(tasks, writer);

            // close writer
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the category if it's a new one
        if (category.getSelectedItem().toString().equals("New category")) {
            EditText newCategory = (EditText) findViewById(R.id.newCategoryName);
            ArrayAdapter categoryAdapter = (ArrayAdapter) category.getAdapter();
            categoryAdapter.add(newCategory.getText().toString());
        }

        // Add the preset if needed
        if (newPreset.isChecked()) {
            // Create the preset
            String c = category.getSelectedItem().toString(); // Category name
            if (c.equals("New category")) {
                EditText newCategory = (EditText) findViewById(R.id.newCategoryName);
                c = newCategory.getText().toString();
            }
            Preset p = new Preset(newPresetName.getText().toString(), c,
                    Integer.parseInt(effortText), Integer.parseInt(urgencyText), new String[0], calendar.isChecked());

            presets.add(p);

            try {
                Log.d("Write", "Add the new preset in the JSON file");
                writer = Files.newBufferedWriter(Paths.get(presetFile.getPath()));

                // convert user object to JSON file
                gson.toJson(presets, writer);

                // close writer
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Test to read the two JSON files
        // create a reader
        Reader reader = null;
        try {
            Log.d("Read", "Read tasks file");
            reader = Files.newBufferedReader(Paths.get(taskFile.getPath()));

            // convert JSON string to Book object
            List<Task> readTasks = Arrays.asList(gson.fromJson(reader, Task[].class));

            // print book
            readTasks.forEach(System.out::println);

            Log.d("Read", "Read presets file");
            reader = Files.newBufferedReader(Paths.get(presetFile.getPath()));

            // convert JSON string to Book object
            List<Preset> readPresets = Arrays.asList(gson.fromJson(reader, Preset[].class));

            // print book
            readPresets.forEach(System.out::println);

            // close reader
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //finish();
    }
}
