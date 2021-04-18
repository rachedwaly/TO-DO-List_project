package com.example.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
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

public class CreateActivity extends AppCompatActivity implements TagsPickerFragment.onTagsEventListener {

    //UI References
    private Spinner preset;
    private String selectedPreset;
    private EditText name;
    private Spinner category;
    private String selectedCategory;
    private EditText newCategoryName;
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



        // Back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Load widgets
        findViewsById();

        //Get variables
        tagsList = (HashSet<String>) (getIntent().getSerializableExtra("tagList"));

        presetFilePath = getIntent().getStringExtra("presetsPath");
        Log.d("CreateActivity preset", presetFilePath);
        readPresets();

        currentTask = (Task) getIntent().getSerializableExtra("task");

        int requestcode = getIntent().getIntExtra("requestCode", 1);
        if (requestcode == 2) {
            Button button = (Button) findViewById(R.id.createButton);
            button.setText("Modify");
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

                            currentTask.setTags(p.getTags());
                            changeChipVisibility();

                            selectedCategory = p.getCategory();
                            effortSlider.setValue(p.getEffort());
                            urgencySlider.setValue(p.getUrgency());
                            calendar.setChecked(p.isCalendar());
                        }
                    }

                    category.setSelection(((ArrayAdapter) category.getAdapter()).getPosition(selectedCategory));
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
                currentTask.getTags().remove(selectedCategory.toLowerCase());
                selectedCategory = parent.getItemAtPosition(pos).toString();

                TableRow categoryRow = (TableRow) findViewById(R.id.newCategory);
                String newTagName = newCategoryName.getText().toString().toLowerCase();
                if (selectedCategory.equals("New category")) {
                    categoryRow.setVisibility(View.VISIBLE);
                    createChip("");
                    newCategoryName.setText("");
                    currentTask.getTags().add("");
                    tagsList.add("");
                } else {
                    categoryRow.setVisibility(View.GONE);
                    currentTask.getTags().add(selectedCategory.toLowerCase());
                    tagsList.remove(newTagName);
                    deleteChip(newTagName);
                }

                changeChipVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        // Add a listener on the new category name
        newCategoryName.addTextChangedListener(new TextWatcher() {

            Chip myChip;

            public void afterTextChanged(Editable s) {
                String oldTag = myChip.getText().toString();
                currentTask.getTags().remove(oldTag);
                tagsList.remove(oldTag);

                String newTag = s.toString().toLowerCase();
                myChip.setText(newTag);
                currentTask.getTags().add(newTag);
                tagsList.add(newTag);
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                for (int i=0; i < tags.getChildCount();i++){
                    Chip chip = (Chip) tags.getChildAt(i);
                    if (chip.getText().toString().equals(s.toString().toLowerCase())) {
                        myChip = chip;
                    }
                }
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
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

        //Initialize effort slider listener
        effortSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                currentTask.getTags().remove("easy");
                currentTask.getTags().remove("medium effort");
                currentTask.getTags().remove("hard");

                if (value == 1 || value == 2) {
                    currentTask.getTags().add("easy");
                }

                if (value == 3) {
                    currentTask.getTags().add("medium effort");
                }

                if (value == 4 || value == 5) {
                    currentTask.getTags().add("hard");
                }

                changeChipVisibility();
            }
        });

        //Initialize urgency slider listener
        urgencySlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                currentTask.getTags().remove("not urgent");
                currentTask.getTags().remove("low urgency");
                currentTask.getTags().remove("medium urgency");
                currentTask.getTags().remove("urgent");

                if (value == 1) {
                    currentTask.getTags().add("not urgent");
                }

                if (value == 2) {
                    currentTask.getTags().add("low urgency");
                }

                if (value == 3) {
                    currentTask.getTags().add("medium urgency");
                }

                if (value == 4 || value == 5) {
                    currentTask.getTags().add("urgent");
                }

                changeChipVisibility();
            }
        });



        //Initialize display regarding the given task (new task or modifying a task)
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
        for (String tag : tagsList) {
            createChip(tag);
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
        newCategoryName = (EditText) findViewById(R.id.newCategoryName);
        description = (EditText) findViewById(R.id.description);
        effortSlider = (Slider) findViewById(R.id.effort);
        urgencySlider = (Slider) findViewById(R.id.urgency);
        tags = (ChipGroup) findViewById(R.id.tags);
        calendar = (CheckBox) findViewById(R.id.calendar);
        newPreset = (CheckBox) findViewById(R.id.newPreset);
        newPresetName = (EditText) findViewById(R.id.newPresetName);
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
        //Show the date picker
        DialogFragment newFragment = new DatePickerFragment(dueDate, repeaterRow);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        //Show the time picker
        DialogFragment newFragment = new TimePickerFragment(dueTime);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showTagsDialog(View v) {
        // Create the fragment and show it as a dialog.
        DialogFragment newFragment = TagsPickerFragment.newInstance(tagsList, currentTask.getTags());
        FragmentManager fm = getSupportFragmentManager();
        newFragment.show(fm, "fragment_tags");
    }

    // Create a chip widget
    private void createChip(String s) {
        Chip chip = new Chip(this);
        chip.setText(s);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chip.setVisibility(View.GONE);
                currentTask.getTags().remove(s);
            }
        });

        if (currentTask.getTags().contains(s)) {
            chip.setVisibility(View.VISIBLE);
        } else {
            chip.setVisibility(View.GONE);
        }

        tags.addView(chip);
    }

    private void deleteChip(String s) {
        for (int i=0; i < tags.getChildCount();i++){
            Chip chip = (Chip) tags.getChildAt(i);
            if (chip.getText().toString().equals(s)) {
                tags.removeView(chip);
            }
        }
    }

    //Change the visibility of the chip in the chip group
    private void changeChipVisibility () {
        for (int i=0; i < tags.getChildCount();i++){
            Chip chip = (Chip) tags.getChildAt(i);
            if (currentTask.getTags().contains(chip.getText().toString())) {
                chip.setVisibility(View.VISIBLE);
            } else {
                chip.setVisibility(View.GONE);
            }
        }
    }

    //Functions of the listener
    //-----------------------------------------------------

    @Override
    public void addTags(String s) {
        currentTask.getTags().add(s);
        changeChipVisibility();
    }

    @Override
    public void addNewTags(String s) {
        tagsList.add(s);
        currentTask.getTags().add(s);
        createChip(s);
    }

    @Override
    public void removeTags(String s) {
        currentTask.getTags().remove(s);
        changeChipVisibility();
    }

    //-----------------------------------------------------

    //onClick of the new preset checkbox
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

    //onClick of the create button
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCreateClicked(View view) {

        if(calendar.isChecked() && dueDate.getText().toString().equals("")) {
            Context context = getApplicationContext();
            CharSequence text = "A date is required to add to the calendar";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {

            Log.d("Begin", "try");

            // Add the category if it's a new one
            if (category.getSelectedItem().toString().equals("New category")) {
                categories.add(1, newCategoryName.getText().toString());
                selectedCategory = newCategoryName.getText().toString();
            }

            // Send back the category array
            Intent data = new Intent();
            data.putExtra("categories", categories);

            data.putExtra("position", getIntent().getIntExtra("position", -1));

            //Modify the current task with the new entries (tags already updated)
            currentTask.setPreset(selectedPreset);
            currentTask.setName(name.getText().toString());
            currentTask.setCategory(selectedCategory);
            currentTask.setDueDate(dueDate.getText().toString());
            currentTask.setRepeater(selectedRepeater);
            currentTask.setDueTime(dueTime.getText().toString());
            currentTask.setDescription(description.getText().toString());
            currentTask.setEffort((int) effortSlider.getValue());
            currentTask.setUrgency((int) urgencySlider.getValue());
            currentTask.setCalendar(calendar.isChecked());

            // Send back the task
            data.putExtra("task", currentTask);

            // create Gson instance
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // Add the preset if needed
            Writer writer = null;
            if (newPreset.isChecked()) {
                // Add in the spinner
                /*ArrayAdapter presetAdapter = (ArrayAdapter) preset.getAdapter();
                presetAdapter.add(newPresetName.getText().toString());*/

                // Create the preset
                Preset p = new Preset(newPresetName.getText().toString(), selectedCategory,
                        (int) effortSlider.getValue(), (int) urgencySlider.getValue(), currentTask.getTags(), calendar.isChecked());

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

            setResult(RESULT_OK, data);
            finish();
        }
    }
}
