package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Calendar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Calendar extends Fragment {


    private View calendarFragmentView;
    private TimetableView timetable;

    private static final String ARG_TASKS = "tasksPath";
    private static final String ARG_PRESETS = "presetsPath";
    private static final String ARG_CATEGORIES = "categories";

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        calendarFragmentView = inflater.inflate(R.layout.fragment_calendar, container, false);
        timetable = (TimetableView) calendarFragmentView.findViewById(R.id.timetable);
        ArrayList<Schedule> schedules = new ArrayList<Schedule>();
        Schedule schedule = new Schedule();
        schedule.setClassTitle("Test"); // sets subject
        schedule.setClassPlace("IGR"); // sets place
        schedule.setStartTime(new Time(10,0)); // sets the beginning of class time (hour,minute)
        schedule.setEndTime(new Time(13,30)); // sets the end of class time (hour,minute)
        schedules.add(schedule);
//.. add one or more schedule
        timetable.add(schedules);
        return calendarFragmentView;
    }
}