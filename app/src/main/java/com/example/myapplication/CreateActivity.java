package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
    private TableRow repeaterRow;
    private Spinner repeater;
    private String selectedRepeater;
    private TextView dueTime;
    private EditText description;
    private Slider effortSlider;
    private Slider urgencySlider;
    private ChipGroup tags;
    private CheckBox calendar;
    private CheckBox newPreset;
    private EditText newPresetName;

    private DatePickerDialog dueDatePickerDialog;
    private TimePickerDialog dueTimePickerDialog;

    private SimpleDateFormat dateFormatter;

    private String taskFilePath;
    private List<Task> tasks;

    private String presetFilePath;
    private List<Preset> presets;

    private Task currentTask;

    private HashSet<String> tagsList;
    private ArrayList<String> categories;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        tagsList = (HashSet<String>) (getIntent().getSerializableExtra("tagList"));

        /*ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/

        //Load widgets
        findViewsById();

        //Load received objects
        taskFilePath = getIntent().getStringExtra("tasksPath");
        Log.d("CreateActivity task", taskFilePath);
        readTasks();

        presetFilePath = getIntent().getStringExtra("presetsPath");
        Log.d("CreateActivity preset", presetFilePath);
        readPresets();

        currentTask = (Task) getIntent().getSerializableExtra("task");

        int requestcode = getIntent().getIntExtra("requestCode", 1);
        if (requestcode == 2) {
            Button button = (Button) findViewById(R.id.createButton);
            button.setText("Modify");
        }

        //Add the task in the list if it's a new task
        Boolean isNew = true;
        for (Task task : tasks) {
            if (task.getId() == currentTask.getId()) {
                isNew = false;
                currentTask = task;
            }
        }

        if (isNew) {
            tasks.add(currentTask);
        }

        //Initialize preset spinner
        ArrayList<String> presetList = new ArrayList<String>();
        presetList.add("No preset");
        for (Preset preset : presets) {
            presetList.add(preset.getName());
        }
        ArrayAdapter<String> presetAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, presetList);
        presetAdapter.setNotifyOnChange(true);
        preset.setAdapter(presetAdapter);
        preset.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                selectedPreset = parent.getItemAtPosition(pos).toString();

                if (pos != 0) {
                    for (Preset p : presets) {
                        if (p.getName().equals(selectedPreset)) {
                            selectedCategory = p.getCategory();
                            effortSlider.setValue(p.getEffort());
                            urgencySlider.setValue(p.getUrgency());
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

        //Initialize category spinner
        categories = (ArrayList<String>) getIntent().getSerializableExtra("categories");
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, categories);
        categoryAdapter.setNotifyOnChange(true);
        category.setAdapter(categoryAdapter);
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

        //Initialize the repeat spinner
        ArrayList<String> repeaterList = new ArrayList<String>();
        repeaterList.add("Don't repeat");
        repeaterList.add("Repeat every day");
        repeaterList.add("Repeat every week");
        repeaterList.add("Repeat every month");
        repeaterList.add("Repeat every year");
        ArrayAdapter<String> repeaterAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, repeaterList);
        repeaterAdapter.setNotifyOnChange(true);
        repeater.setAdapter(repeaterAdapter);
        repeater.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                selectedRepeater = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        if (currentTask.getDueDate().equals("")) {
            repeaterRow.setVisibility(View.GONE);
        } else {
            repeaterRow.setVisibility(View.VISIBLE);
        }


        //Initialize display regarding the given task (new task or modifying a task?)
        preset.setSelection(0);
        selectedPreset = currentTask.getPreset();
        name.setText(currentTask.getName());
        category.setSelection(categoryAdapter.getPosition(currentTask.getCategory()));
        selectedCategory = currentTask.getCategory();
        dueDate.setText(currentTask.getDueDate());
        repeater.setSelection(repeaterAdapter.getPosition(currentTask.getRepeater()));
        selectedRepeater = currentTask.getRepeater();
        dueTime.setText(currentTask.getDueTime());
        description.setText(currentTask.getDescription());
        effortSlider.setValue(currentTask.getEffort());
        urgencySlider.setValue(currentTask.getUrgency());
        calendar.setChecked(currentTask.isCalendar());

        //Initialize the tags chip group
        for (String tag : currentTask.getTags()) {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chip.setVisibility(View.GONE);
                }
            });

            tags.addView(chip);
        }
    }

    private void findViewsById() {
        preset = (Spinner) findViewById(R.id.presets);
        dueDate = (TextView) findViewById(R.id.due_date);
        repeaterRow = (TableRow) findViewById(R.id.repeatRow);
        repeater = (Spinner) findViewById(R.id.repeater);
        dueTime = (TextView) findViewById(R.id.due_time);
        name = (EditText) findViewById(R.id.name);
        category = (Spinner) findViewById(R.id.category);
        description = (EditText) findViewById(R.id.description);
        effortSlider = (Slider) findViewById(R.id.effort);
        urgencySlider = (Slider) findViewById(R.id.urgency);
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
            reader = Files.newBufferedReader(Paths.get(taskFilePath));

            // create Gson instance
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // convert JSON string to Book object
            tasks = new ArrayList<Task>(Arrays.asList(gson.fromJson(reader, Task[].class)));

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
            reader = Files.newBufferedReader(Paths.get(presetFilePath));

            // create Gson instance
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // convert JSON string to Book object
            presets = new ArrayList<Preset>(Arrays.asList(gson.fromJson(reader, Preset[].class)));

            // print book
            presets.forEach(System.out::println);

            // close reader
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(dueDate, repeaterRow);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(dueTime);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showTagsDialog(View v) {
        // Create the fragment and show it as a dialog.
        DialogFragment newFragment = TagsPickerFragment.newInstance(tagsList, currentTask.getTags());
        FragmentManager fm = getSupportFragmentManager();
        newFragment.show(fm, "fragment_tags");
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

        // Add the category if it's a new one
        if (category.getSelectedItem().toString().equals("New category")) {
            EditText newCategory = (EditText) findViewById(R.id.newCategoryName);
            categories.add(1, newCategory.getText().toString());
            selectedCategory = newCategory.getText().toString();
        }

        // Send back the category array
        Intent data = new Intent();
        data.putExtra("categories",categories);

        data.putExtra("position",getIntent().getIntExtra("position",-1));

        //Modify the current task with the new entries
        currentTask.setPreset(selectedPreset);
        currentTask.setName(name.getText().toString());
        currentTask.setCategory(selectedCategory);
        currentTask.setDueDate(dueDate.getText().toString());
        currentTask.setRepeater(selectedRepeater);
        currentTask.setDueTime(dueTime.getText().toString());
        currentTask.setDescription(description.getText().toString());
        currentTask.setEffort((int) effortSlider.getValue());
        Log.d("Effort", String.valueOf(currentTask.getEffort()));
        currentTask.setUrgency((int) urgencySlider.getValue());
        currentTask.setTags(new HashSet<String>());
        currentTask.setCalendar(calendar.isChecked());
        data.putExtra("task", currentTask);
        // create Gson instance
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // create a writer
        Writer writer = null;
        try {
            Log.d("Write", "Add/Modify the new task in the JSON file");

            writer = Files.newBufferedWriter(Paths.get(taskFilePath));

            // convert user object to JSON file
            gson.toJson(tasks, writer);

            // close writer
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the preset if needed
        if (newPreset.isChecked()) {
            // Add in the spinner
            ArrayAdapter presetAdapter = (ArrayAdapter) preset.getAdapter();
            presetAdapter.add(newPresetName.getText().toString());

            // Create the preset
            Preset p = new Preset(newPresetName.getText().toString(), selectedCategory,
                    (int) effortSlider.getValue(), (int) urgencySlider.getValue(), new String[0], calendar.isChecked());

            presets.add(p);

            try {
                Log.d("Write", "Add the new preset in the JSON file");
                writer = Files.newBufferedWriter(Paths.get(presetFilePath));

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
            reader = Files.newBufferedReader(Paths.get(taskFilePath));

            // convert JSON string to Book object
            List<Task> readTasks = Arrays.asList(gson.fromJson(reader, Task[].class));

            // print book
            readTasks.forEach(System.out::println);

            Log.d("Read", "Read presets file");
            reader = Files.newBufferedReader(Paths.get(presetFilePath));

            // convert JSON string to Book object
            List<Preset> readPresets = Arrays.asList(gson.fromJson(reader, Preset[].class));

            // print book
            readPresets.forEach(System.out::println);

            // close reader
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setResult(RESULT_OK, data);
        finish();
    }
}
