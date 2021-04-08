package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private File taskFile;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup navigation
        ViewPager2 viewPager = (ViewPager2) findViewById(R.id.pager);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(this);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.setUserInputEnabled(false);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0)   {tab.setText("Calendar");}
                    if (position == 1)   {tab.setText("Main");}
                    if (position == 2)   {tab.setText("Graph");}
                }).attach();

        initializeJSONTasks();
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
                    "16:00", "My description", 1, 4, new String[]{"meeting", "igr203"}, false);
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
}