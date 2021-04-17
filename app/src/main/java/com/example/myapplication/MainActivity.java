package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements MyTaskListListener {

    private File taskFile;
    private File presetFile;
    private ArrayList<String> categories;
    private ArrayList<Task> filteredTaskList;
    private ArrayList<Task> completeTaskList;
    private HashSet<String> tagList;
    private HashSet<String> activeTagList;
    MyPagerAdapter myPagerAdapter;
    MyFragmentListener [] fragmentListeners = new MyFragmentListener[3];

    private Button filterButton;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categories = new ArrayList<String>();
        filteredTaskList =  new ArrayList<Task>();
        completeTaskList =  new ArrayList<Task>();
        tagList =  new  HashSet<String>();
        activeTagList =  new  HashSet<String>();

        filterButton =  (Button) findViewById(R.id.filterMenuButton);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FilterMenuFragment filterMenuFragment=new FilterMenuFragment();
                filterMenuFragment.show(fm,"Filter Menu");
            }
        });

        initializeJSONTasks();
        initializeJSONPresets();



        categories.add("No category");
        categories.add("Exam");
        categories.add("Project");
        categories.add("Meeting");
        categories.add("New category");

        // Setup navigation
        ViewPager2 viewPager = (ViewPager2) findViewById(R.id.pager);
        Log.d("Send task path", taskFile.getAbsolutePath().toString());
        Log.d("Send preset path", presetFile.getAbsolutePath().toString());
        myPagerAdapter = new MyPagerAdapter(this, taskFile.getAbsolutePath().toString(), presetFile.getAbsolutePath().toString(), categories);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.setUserInputEnabled(false);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0)   {tab.setText("Calendar");}
                    if (position == 1)   {tab.setText("Main");}
                    if (position == 2)   {tab.setText("Graph");}
                }).attach();
    }

    @Override
    public ArrayList<Task> getFilteredTaskList(){
        return filteredTaskList;
    }

    @Override
    public void addTask(Task t){
        completeTaskList.add(t);
        t.setPosCompleteTaskList(completeTaskList.size()-1);
        if (isFiltered(t)){
            filteredTaskList.add(t);
        }
        updateFragments();
    }

    @Override
    public void addTask(int posInFiltered, Task t){
        completeTaskList.add(t);
        t.setPosCompleteTaskList(completeTaskList.size()-1);
        filteredTaskList.add(posInFiltered, t);
        updateFragments();
    }

    @Override
    public void remove(int posInFiltered){
        Task t = filteredTaskList.get(posInFiltered);
        int posInComplete = t.getPosCompleteTaskList();
        t.setPosCompleteTaskList((completeTaskList.size()-1));
        (completeTaskList.get((completeTaskList.size()-1))).setPosCompleteTaskList(posInComplete);
        Collections.swap(completeTaskList, posInComplete, completeTaskList.size()-1);

        completeTaskList.remove(completeTaskList.size()-1);

        filteredTaskList.remove(posInFiltered);
        updateFragments();
    }

    @Override
    public Task getTask(int posInFiltered){
        return filteredTaskList.get(posInFiltered);
    }

    @Override
    public HashSet<String> getTagList() {
        return tagList;
    }

    @Override
    public HashSet<String> getActiveTagList() {
        return activeTagList;
    }

    @Override
    public void activateTag(String tag) {
        activeTagList.add(tag);
        updateFilteredList();

    }

    @Override
    public void deactivateTag(String tag) {
        activeTagList.remove(tag);
        updateFilteredList();
    }

    @Override
    public void deactivateAllTags() {
        activeTagList.clear();
        updateFilteredList();
    }

    public void updateFilteredList(){
        filteredTaskList.clear();
        for (Task task : completeTaskList){
            if (isFiltered(task)){
                filteredTaskList.add(task);
            }
        }
        updateFragments();
    }

    @Override
    public void updateTagList(HashSet<String> taskTagList) { tagList.addAll(taskTagList); }

    @Override
    public void registerFragmentListener(MyFragmentListener fragmentListener, int position) {
        this.fragmentListeners[position] = fragmentListener;
    }

    @Override
    public void updateFragments() {
        for (int i=0; i<3; i++){
            if (fragmentListeners[i]!=null) { fragmentListeners[i].updateView(); }
        }
    }

    public Boolean isFiltered(Task t){  // true if element should be displayed
        if (activeTagList.isEmpty()) { return true; }
        else {
            Set<String> intersectSet = new HashSet<>(t.getTags());
            intersectSet.retainAll(activeTagList);
            return !(intersectSet.isEmpty());
        }
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
                    "12:13", "My description", 3, 4, new HashSet<>(Arrays.asList("exam", "c++")), true);
            Task.ID_COUNT += 1;
            Task newTask2 = new Task("Meeting", "My meeting", "Meeting", "20-04-2021", "Repeat every week",
                    "16:00", "My description", 1, 1, new HashSet<>(Arrays.asList("exam", "meeting")), false);
            Task.ID_COUNT += 1;
            Task newTask3 = new Task("Project", "My project", "Project", "03-05-2021", "Don't repeat",
                    "23:59", "My description", 4, 2, new HashSet<>(Arrays.asList("project", "hard")), true);
            Task.ID_COUNT += 1;

            tagList.add("exam");
            tagList.add("c++");
            tagList.add("meeting");
            tagList.add("project");
            tagList.add("hard");

            addTask(newTask1);
            addTask(newTask2);
            addTask(newTask3);

            Writer writer = Files.newBufferedWriter(Paths.get(taskFile.getPath()));

            // create Gson instance
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // convert user object to JSON file
            gson.toJson(filteredTaskList, writer);

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
            Preset newPreset1 = new Preset("Exam", "Exam", 3, 4, new HashSet<>(Arrays.asList("exam", "urgent")), true);
            Preset newPreset2 = new Preset("Project", "Project", 4, 2, new HashSet<>(Arrays.asList("project", "hard")), true);
            Preset newPreset3 = new Preset("Meeting", "Meeting", 1, 1, new HashSet<>(Arrays.asList("meeting")), false);

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