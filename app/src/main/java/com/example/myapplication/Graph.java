package com.example.myapplication;

import android.os.Bundle;

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
    private View myFragmentView;
    private RelativeLayout llTouch;//llTouch --> 绘图区域
    private LinearLayout popup;
    private int index = 0;
    private int screenWidth;
    private int screenHeight;

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
    public static Graph newInstance(String param1, String param2) {
        Graph fragment = new Graph();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_graph, container, false);
        init();
        return myFragmentView;
    }

    private void init() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 50;

        popup = (LinearLayout) myFragmentView.findViewById(R.id.popup);
        llTouch = (RelativeLayout) myFragmentView.findViewById(R.id.ll_touch);

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

                createTask(effort, urgency);
                popup.setVisibility(View.INVISIBLE);
            }
        });

        Button add_btn_general = myFragmentView.findViewById(R.id.add_btn_general);
        add_btn_general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPopup();
            }
        });
    }

    private View.OnTouchListener movingEventListener = new View.OnTouchListener() {
        int lastX, lastY, x, y;

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

                    if (right > screenWidth) {
                        right = screenWidth;
                        left = right - v.getWidth();
                    }

                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }

                    if (bottom > screenHeight) {
                        bottom = screenHeight;
                        top = bottom - v.getHeight();
                    }

                    v.layout(left, top, right, bottom);

                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();

                    break;
                case MotionEvent.ACTION_UP:
//                    Log.d("selectedview", String.valueOf(selectedView.getId()));
                    Log.d("x", String.valueOf(lastX));
                    Log.d("y", String.valueOf(lastY));
                    v = null;

//                    //检测移动的距离，如果很微小可以认为是点击事件
//                    if (Math.abs(event.getRawX() - x) < 10 && Math.abs(event.getRawY() - y) < 10) {
//                        try {
//                            Field field = View.class.getDeclaredField("mListenerInfo");
//                            field.setAccessible(true);
//                            Object object = field.get(v);
//                            field = object.getClass().getDeclaredField("mOnClickListener");
//                            field.setAccessible(true);
//                            object = field.get(object);
//                            if (object != null && object instanceof View.OnClickListener) {
//                                ((View.OnClickListener) object).onClick(v);
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
                    break;
            }
            return true;
        }
    };

    private void createTask(float x, float y) {
        ImageView iv = new ImageView(getActivity());
        iv.setImageResource(R.drawable.task1);
        iv.setId(index++);
        int left = Math.round(x * 50);
        int bottom = Math.round(y * 50);
        int top = 300 - bottom;
        int right = 300 - left;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(left, top, right, bottom);
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
}