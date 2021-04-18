package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Calendar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Calendar extends Fragment implements MyFragmentListener , CardDetailedFragment.EditTaskListener{


    private View calendarFragmentView;
    private TimetableView timetable;
    private MyTaskListListener listener;
    private List<Task> tasks;
    ArrayList<Schedule> schedules = new ArrayList<Schedule>();

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

        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                CardDetailedFragment cardDetailedFragment=new CardDetailedFragment();
                cardDetailedFragment.fillDialogFragment(idx);
                cardDetailedFragment.setTargetFragment(Calendar.this,300);
                cardDetailedFragment.show(getFragmentManager(),"TaskDetailed");
            }
        });

        readData();

        return calendarFragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (MyTaskListListener) context;
            listener.registerFragmentListener(((MyFragmentListener)this), 0);
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */
        }
    }

    @Override
    public void updateView() {
        timetable.removeAll();
        readData();
    }

    private void readData(){
        tasks = listener.getFilteredTaskList();
        for(int i = 0 ; i < tasks.size(); i++){
            createTask(tasks.get(i).getId(), tasks.get(i).getDueDate(), tasks.get(i).getDueTime(), tasks.get(i).getName(), tasks.get(i).getCategory());
        }
    }

    private void createTask(int index, String date, String time, String name, String category) {

        schedules.clear();
        Schedule newTask = new Schedule();

        String[] timeArray = time.split(":");
        int HH = Integer.parseInt(timeArray[0]);
        int mm = Integer.parseInt(timeArray[1]);
        newTask.setClassTitle(name); // sets subject
        newTask.setClassPlace(category); // sets place
        newTask.setStartTime(new Time(HH,mm)); // sets the beginning of class time (hour,minute)
        newTask.setEndTime(new Time(HH+1,mm)); // sets the end of class time (hour,minute)
        newTask.setDay(index);
        schedules.add(newTask);
        timetable.add(schedules);
    }

    public void openAddTaskActivity() {
        Intent intent = new Intent(getActivity(), CreateActivity.class);
        intent.putExtra("tasksPath", mTasksFilePath);
        intent.putExtra("presetsPath", mPresetsFilePath);
        intent.putExtra("categories", mCategories);
        intent.putExtra("tagList", listener.getTagList());
        intent.putExtra("requestCode", 1);
        Task newTask = new Task();
        /*Task newTask = new Task("Exam", "My exam", "Exam", "06-07-2021", "Don't repeat",
                "12:13", "My description", 3, 4, new String[]{"exam", "urgent"}, true);*/
        Task.ID_COUNT += 1;

        // TODO : move this to onActivity result
        intent.putExtra("task", newTask);
        startActivityForResult(intent, 1);
    }

    @Override
    public void openEditTaskActivity(int i) {
        Intent intent = new Intent(getActivity(), CreateActivity.class);
        intent.putExtra("tasksPath", mTasksFilePath);
        intent.putExtra("presetsPath", mPresetsFilePath);
        intent.putExtra("categories", mCategories);
        intent.putExtra("task", listener.getTask(i));
        intent.putExtra("position", i);
        intent.putExtra("tagList", listener.getTagList());
        intent.putExtra("requestCode", 2);
        startActivityForResult(intent, 2);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                mCategories = data.getStringArrayListExtra("categories");
                Task newTask=(Task) data.getSerializableExtra("task");
                listener.addTask(newTask);
                listener.updateTagList(newTask.getTags());
                listener.updateFragments();
                //mCategories.forEach(System.out::println);
            }
        }
        if (requestCode == 2) {

            if (resultCode == RESULT_OK) {
                mCategories = data.getStringArrayListExtra("categories");
                int position=data.getIntExtra("position",-1);

                listener.remove(position);
                Task newTask=(Task) data.getSerializableExtra("task");
                listener.addTask(position,newTask);
                listener.updateTagList(newTask.getTags());
                listener.updateFragments();
            }
        }
    }
}