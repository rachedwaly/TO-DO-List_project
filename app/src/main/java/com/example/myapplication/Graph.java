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
public class Graph extends Fragment implements MyFragmentListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //smt i did
    private List<Task> tasks;
    private View myFragmentView;
    private RelativeLayout llTouch;//llTouch --> 绘图区域
    private int llTouchwidth;//llTouch --> 绘图区域
    private int llTouchheight;//llTouch --> 绘图区域
    private LinearLayout popup;
    private TextView taskname, taskeffort, taskurgent;
    private MyTaskListListener listener;


    private static final String ARG_TASKS = "tasksPath";
    private static final String ARG_PRESETS = "presetsPath";
    private static final String ARG_CATEGORIES = "categories";
    private String mTasksFilePath;
    private String mPresetsFilePath;
    private ArrayList<String> mCategories;

    public Graph() {
        // Required empty public constructor
    }

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (MyTaskListListener) context;
            listener.registerFragmentListener(((MyFragmentListener)this), 2);
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */
        }
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
//        json version
//        for(int i = 0 ; i < tasks.size(); i++){
//            Log.d("create task: ", String.valueOf(tasks.get(i).getId()) + String.valueOf(tasks.get(i).getEffort()) + String.valueOf(tasks.get(i).getUrgency()));
//            createTask(tasks.get(i).getId(), tasks.get(i).getEffort(), tasks.get(i).getUrgency());
//        }

        //filted data
        llTouch.removeAllViews();
        tasks = listener.getFilteredTaskList();
        for(int i = 0 ; i < tasks.size(); i++){
            Log.d("create task: ", String.valueOf(tasks.get(i).getId()) + String.valueOf(tasks.get(i).getEffort()) + String.valueOf(tasks.get(i).getUrgency()));
            createTask(tasks.get(i).getId(), tasks.get(i).getEffort(), tasks.get(i).getUrgency());
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



        FloatingActionButton add_btn_general = myFragmentView.findViewById(R.id.add_btn_general);
        add_btn_general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showAddPopup();
                openAddTaskActivity();
            }
        });

//        readTasks();
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
                    Log.d("x", String.valueOf(Math.round((x - 150) / llTouchwidth / 5) + 1));
                    Log.d("y", String.valueOf(Math.round((1500 - y)/ llTouchwidth / 5) + 1));


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

                    int dx1 =  lastX - x;
                    int dy1 =  lastY - y;


                    //Log.d("x", String.valueOf(lastX));
                    //Log.d("y", String.valueOf(lastY));
                    taskname.setText(tasks.get(v.getId()).getName());
                    int scale = llTouchwidth / 6;
                    int effort = tasks.get(v.getId()).getEffort() + Math.round(dx1 / scale);
                    int urgent = tasks.get(v.getId()).getUrgency() - Math.round((dy1)/ scale);

                    if (effort<1) { effort = 1; }
                    if (urgent<1) { urgent = 1; }
                    if (effort>5) { effort = 5; }
                    if (urgent>5) { urgent = 5; }


                    int position = v.getId();

                    //Modify the current task with the new entries


                    taskeffort.setText("Effort: " + effort);
                    taskurgent.setText("Urgent: "  + urgent);

                    listener.getTask(position).setEffort(effort);
                    listener.getTask(position).setUrgency(urgent);

//                    mAdapter.notifyDataSetChanged();

                    for(int i = 0 ; i < tasks.size(); i++){
                        //Log.d("create task: ", String.valueOf(tasks.get(i).getId()) + String.valueOf(tasks.get(i).getEffort()) + String.valueOf(tasks.get(i).getUrgency()));
                        Log.d("x,y:", String.valueOf(effort)+" "+String.valueOf(urgent));
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                mCategories = data.getStringArrayListExtra("categories");
                Task newTask=(Task) data.getSerializableExtra("task");
                listener.addTask(newTask);
                listener.updateTagList(newTask.getTags());
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
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void updateView() {
        readData();
    }
}