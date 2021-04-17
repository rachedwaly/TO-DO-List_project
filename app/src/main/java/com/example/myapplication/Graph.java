package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Graph#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Graph extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //smt i did
    private List<Task> tasks;
    private ArrayList<String> data_name = new ArrayList<String>();
    private ArrayList<Integer> data_effort = new ArrayList<>();
    private ArrayList<Integer> data_urgent = new ArrayList<>();
    private View myFragmentView;
    private RelativeLayout llTouch;//llTouch --> 绘图区域
    private int llTouchwidth;//llTouch --> 绘图区域
    private int llTouchheight;//llTouch --> 绘图区域
    private LinearLayout popup;
    private TextView taskname, taskeffort, taskurgent;
    private int index = 0;


    private static final String ARG_TASKS = "tasksPath";
    private static final String ARG_PRESETS = "presetsPath";
    private static final String ARG_CATEGORIES = "categories";
    private String mTasksFilePath;
    private String mPresetsFilePath;
    private ArrayList<String> mCategories;

    public Graph() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Graph.
     */
    // TODO: Rename and change types and number of parameters
    public static Graph newInstance(String tasksFilePath, String presetsFilePath, ArrayList<String> categories) {
        Graph fragment = new Graph();
        Bundle args = new Bundle();
        args.putString(ARG_TASKS, tasksFilePath);
        args.putString(ARG_PRESETS, presetsFilePath);
        args.putStringArrayList(ARG_CATEGORIES, categories);
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mTasksFilePath = getArguments().getString(ARG_TASKS);
            mPresetsFilePath = getArguments().getString(ARG_PRESETS);
            mCategories = getArguments().getStringArrayList(ARG_CATEGORIES);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_graph, container, false);
        init();
        return myFragmentView;
    }

    private void readData(){
//        for(int i = 0 ; i < data_effort.size(); i++){
//            createTask(data_effort.get(i), data_urgent.get(i));
//        }
        for(int i = 0 ; i < tasks.size(); i++){
            Log.d("create task: ", String.valueOf(tasks.get(i).id) + String.valueOf(tasks.get(i).effort) + String.valueOf(tasks.get(i).urgency));
            createTask(tasks.get(i).id, tasks.get(i).effort, tasks.get(i).urgency);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        popup = (LinearLayout) myFragmentView.findViewById(R.id.popup);
        llTouch = (RelativeLayout) myFragmentView.findViewById(R.id.ll_touch);
        taskname = (TextView) myFragmentView.findViewById(R.id.taskname);
        taskeffort = (TextView) myFragmentView.findViewById(R.id.taskeffort);
        taskurgent = (TextView) myFragmentView.findViewById(R.id.taskurgent);

        llTouch.measure(0,0);

        llTouchheight = llTouch.getMeasuredHeight();
        llTouchwidth = llTouch.getMeasuredWidth();
        Log.d("tst","height :" + llTouchheight + "width: " + llTouchwidth);

        int count = llTouch.getChildCount();
        for(int i= 0; i < count; i++){
            View view = llTouch.getChildAt(i);
            if(view instanceof ImageView){
                Log.d("set touch listener on", String.valueOf(view.getId()));
                view.setOnTouchListener(movingEventListener);
            }
        }

        //添加一个新的任务
        Button add_btn = myFragmentView.findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText1 =(EditText) myFragmentView.findViewById(R.id.edit_effort);
                int effort = Integer.parseInt(editText1.getText().toString());

                EditText editText2 =(EditText) myFragmentView.findViewById(R.id.edit_urgency);
                int urgency = Integer.parseInt(editText2.getText().toString());

                Log.d("da","effort :" + effort + "urgency: " + urgency);

//                createTask(effort, urgency);
                popup.setVisibility(View.INVISIBLE);
            }
        });

        FloatingActionButton add_btn_general = myFragmentView.findViewById(R.id.add_btn_general);
        add_btn_general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showAddPopup();
                openAddTaskActivity();
            }
        });


//        data_name.add("task1a");
//        data_name.add("task2a");
//        data_name.add("task3a");
//        data_effort.add(4);
//        data_effort.add(1);
//        data_effort.add(5);
//        data_urgent.add(3);
//        data_urgent.add(1);
//        data_urgent.add(1);

        readTasks();
        readData();
    }

    private View.OnTouchListener movingEventListener = new View.OnTouchListener() {
        int lastX, lastY, x, y;

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;

                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom() + dy;
                    // 设置不能出界
                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }

                    if (right > llTouchwidth) {
                        right = llTouchwidth;
                        left = right - v.getWidth();
                    }

                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }

                    if (bottom > llTouchheight) {
                        bottom = llTouchheight;
                        top = bottom - v.getHeight();
                    }

                    v.layout(left, top, right, bottom);

                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();

                    break;
                case MotionEvent.ACTION_UP:
//                    Log.d("selectedview", String.valueOf(selectedView.getId()));

//                    taskname.setText(data_name.get(v.getId()));
                    Log.d("x", String.valueOf(lastX));
                    Log.d("y", String.valueOf(lastY));
                    taskname.setText(tasks.get(v.getId()).name);
                    int scale = llTouchwidth / 5;
                    int effort = Math.round((lastX - 150) / scale) + 1;
                    int urgent = Math.round((1500 - lastY)/ scale) + 1;
                    taskeffort.setText("Effort: " + effort);
                    taskurgent.setText("Urgent: "  + urgent);

                    Task currentTask = tasks.get(v.getId());

                    //Modify the current task with the new entries
                    currentTask.setEffort(effort);
                    currentTask.setUrgency(urgent);

                    // create Gson instance
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();

                    // create a writer
                    Writer writer = null;
                    try {
                        Log.d("Write", "Add/Modify the new task in the JSON file");

                        writer = Files.newBufferedWriter(Paths.get(mTasksFilePath));

                        // convert user object to JSON file
                        gson.toJson(tasks, writer);

                        // close writer
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    v = null;
                    break;
            }
            return true;
        }
    };

    private void createTask(int index, float x, float y) {
        ImageView iv = new ImageView(getActivity());
        switch (index % 3){
            case 0 : iv.setImageResource(R.drawable.task1); break;
            case 1 : iv.setImageResource(R.drawable.task2); break;
            case 2 : iv.setImageResource(R.drawable.task3); break;
        }
//        iv.setId(index++);
        iv.setId(index);
        int param = llTouchheight / 6;
        int left = Math.round(x * param);
        int bottom = Math.round(y * param);
        int top = llTouchheight - bottom;
        int right = llTouchwidth - left;
        Log.d("tst2","left:"  + left + "top:" + top);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(left + 1, top + 10, right +1, bottom + 10);
        iv.setLayoutParams(lp);

        iv.setClickable(true);
        iv.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click on ","click on new added "  + v.getId());
            }
        });
        iv.setOnTouchListener(movingEventListener);
        llTouch.addView(iv);
    }

    private void showAddPopup(){
        popup.setVisibility(View.VISIBLE);
    }

    public void openAddTaskActivity() {
        Intent intent = new Intent(getActivity(), CreateActivity.class);
        intent.putExtra("tasksPath", mTasksFilePath);
        intent.putExtra("presetsPath", mPresetsFilePath);
        intent.putExtra("categories", mCategories);
        Task newTask = new Task();
        /*Task newTask = new Task("Exam", "My exam", "Exam", "06-07-2021", "Don't repeat",
                "12:13", "My description", 3, 4, new String[]{"exam", "urgent"}, true);*/
        Task.ID_COUNT += 1;
        intent.putExtra("task", newTask);
        startActivityForResult(intent, 1);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                mCategories = data.getStringArrayListExtra("categories");

                //For testing
                mCategories.forEach(System.out::println);
            }
        }
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