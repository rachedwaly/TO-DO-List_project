package com.example.myapplication;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyPagerAdapter extends FragmentStateAdapter {
    private static int NUM_ITEMS = 3;

    public MyPagerAdapter(FragmentActivity fa){
        super(fa);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new Calendar();
            case 1: return new Main();
            case 2: return new Graph();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return NUM_ITEMS;
    }
}
