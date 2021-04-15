package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private File taskFile;
    private File presetFile;
    private ArrayList<String> categories;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeJSONTasks();
        initializeJSONPresets();

        categories = new ArrayList<String>();
        categories.add("No category");
        categories.add("Exam");
        categories.add("Project");
        categories.add("Meeting");
        categories.add("New category");

        // Setup navigation
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        Log.d("Send task path", taskFile.getAbsolutePath().toString());
        Log.d("Send preset path", presetFile.getAbsolutePath().toString());
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), taskFile.getAbsolutePath().toString(), presetFile.getAbsolutePath().toString(), categories);
        viewPager.setAdapter(myPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        // Deactivating changing menus by swiping
        viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeJSONTasks() {
        taskFile = new File(this.getFilesDir(), "tasks.json");
        Log.d("Task File Path", taskFile.getAbsolutePath());

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

            // add premade tasks
            Task newTask1 = new Task("Exam", "My exam", "Exam", "06-07-2021", "Don't repeat",
                    "12:13", "My description", 3, 4, new String[]{"exam", "urgent"}, true);
            Task.ID_COUNT += 1;
            Task newTask2 = new Task("Meeting", "My meeting", "Meeting", "20-04-2021", "Repeat every week",
                    "16:00", "My description", 1, 1, new String[]{"meeting"}, false);
            Task.ID_COUNT += 1;
            Task newTask3 = new Task("Project", "My project", "Project", "03-05-2021", "Don't repeat",
                    "23:59", "My description", 4, 2, new String[]{"project", "hard"}, true);
            Task.ID_COUNT += 1;


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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeJSONPresets() {
        presetFile = new File(this.getFilesDir(), "presets.json");
        Log.d("Preset File Path", presetFile.getAbsolutePath());

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
}