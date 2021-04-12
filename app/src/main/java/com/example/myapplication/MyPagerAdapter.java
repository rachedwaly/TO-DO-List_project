package com.example.myapplication;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentStatePagerAdapter {
    private static int NUM_ITEMS = 3;

    private String mTasksFilePath;
    private String mPresetsFilePath;
    private ArrayList<String> mCategories;


    public MyPagerAdapter(FragmentManager fm, String tasksFilePath, String presetsFilePath, ArrayList<String> categories){
        super(fm);
        mTasksFilePath = tasksFilePath;
        mPresetsFilePath = presetsFilePath;
        mCategories = categories;

        Log.d("Receive task path", mTasksFilePath);
        Log.d("Receive preset path", mPresetsFilePath);
    }    @Override

    public Fragment getItem(int position) {
        switch (position){
            case 0: return new Calendar();
            case 1: return Main.newInstance(mTasksFilePath, mPresetsFilePath, mCategories);
            case 2: return new Graph();
        }
        return null;
    }      @Override

    public int getCount() {
        return NUM_ITEMS;
    }     @Override

    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "calendar";
            case 1: return "Main";
            case 2: return "graph";
            default: return null;
        }
    }
}
