package com.example.myapplication;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MyPagerAdapter extends FragmentStatePagerAdapter {
    private static int NUM_ITEMS = 3;
    public MyPagerAdapter(FragmentManager fm){
        super(fm);
    }    @Override

    public Fragment getItem(int position) {
        switch (position){
            case 0: return new Calendar();
            case 1: return new Main();
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
