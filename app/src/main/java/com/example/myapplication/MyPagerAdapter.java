package com.example.myapplication;

import android.util.Log;
import java.util.ArrayList;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class MyPagerAdapter extends FragmentStateAdapter {
    private static int NUM_ITEMS = 3;
    private String mTasksFilePath;
    private String mPresetsFilePath;
    private ArrayList<String> mCategories;

    public MyPagerAdapter(FragmentActivity fa, String tasksFilePath, String presetsFilePath, ArrayList<String> categories){
        super(fa);
        mTasksFilePath = tasksFilePath;
        mPresetsFilePath = presetsFilePath;
        mCategories = categories;

        Log.d("Receive task path", mTasksFilePath);
        Log.d("Receive preset path", mPresetsFilePath);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new Calendar();
            case 1: return Main.newInstance(mTasksFilePath, mPresetsFilePath, mCategories);
            case 2: return Graph.newInstance(mTasksFilePath, mPresetsFilePath, mCategories);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return NUM_ITEMS;
    }
}
