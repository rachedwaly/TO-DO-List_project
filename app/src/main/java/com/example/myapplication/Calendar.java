package com.example.myapplication;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Calendar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Calendar extends Fragment implements MyFragmentListener  {


    private View calendarFragmentView;
    private TimetableView timetable;
    private MyTaskListListener listener;
    private List<Task> tasks;
    ArrayList<Schedule> shedules = new ArrayList<Schedule>();

    private static final String ARG_TASKS = "tasksPath";
    private static final String ARG_PRESETS = "presetsPath";
    private static final String ARG_CATEGORIES = "categories";
    private String mTasksFilePath;
    private String mPresetsFilePath;
    private ArrayList<String> mCategories;

    public Calendar() {
        // Required empty public constructor
    }

    public static Calendar newInstance(String tasksFilePath, String presetsFilePath, ArrayList<String> categories) {
        Calendar fragment = new Calendar();
        Bundle args = new Bundle();
        args.putString(ARG_TASKS, tasksFilePath);
        args.putString(ARG_PRESETS, presetsFilePath);
        args.putStringArrayList(ARG_CATEGORIES, categories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mTasksFilePath = getArguments().getString(ARG_TASKS);
        mPresetsFilePath = getArguments().getString(ARG_PRESETS);
        mCategories = getArguments().getStringArrayList(ARG_CATEGORIES);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        calendarFragmentView = inflater.inflate(R.layout.fragment_calendar, container, false);
        timetable = (TimetableView) calendarFragmentView.findViewById(R.id.timetable);
        Schedule task = new Schedule();
        task.setClassTitle("Test"); // sets subject
        task.setClassPlace("IGR"); // sets place
        task.setStartTime(new Time(10,0)); // sets the beginning of class time (hour,minute)
        task.setEndTime(new Time(13,30)); // sets the end of class time (hour,minute)
        shedules.add(task);
//.. add one or more schedule
        timetable.add(shedules);

        readTasks();
        readData();

        return calendarFragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (MyTaskListListener) context;
            listener.registerFragmentListener(((MyFragmentListener)this));
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */
        }
    }

    @Override
    public void updateView() {
        readTasks();
        readData();
    }

    private void readData(){
        for(int i = 0 ; i < tasks.size(); i++){
            createTask(tasks.get(i).getId(), tasks.get(i).getDueDate(), tasks.get(i).getDueTime(), tasks.get(i).getName(), tasks.get(i).getCategory());
        }
    }

    private void createTask(int index, String date, String time, String name, String category) {
        Schedule task = new Schedule();
        task.setClassTitle(name); // sets subject
        task.setClassPlace(category); // sets place
        task.setStartTime(new Time(10,0)); // sets the beginning of class time (hour,minute)
        task.setEndTime(new Time(13,30)); // sets the end of class time (hour,minute)
        task.setDay(index);
        shedules.add(task);
        timetable.add(shedules);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void readTasks() {
        // create a reader
        Reader reader = null;
        try {
            Log.d("Read", "try");
            reader = Files.newBufferedReader(Paths.get(mTasksFilePath));

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
}