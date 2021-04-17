package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Arrays;

public class FilterMenuFragment extends DialogFragment {
    private ChipGroup tags;
    private Button filterButton;
    private Button deleteButton;

    private MyTaskListListener taskListListener;

    public interface EditTaskListener{
        void openEditTaskActivity(int i);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            //listener of the list of tasks created in the MainActivity
            taskListListener = (MyTaskListListener) context;
        } catch (ClassCastException castException) {

        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.filter_menu_popup,container,false);

        tags = view.findViewById(R.id.tags);
        filterButton=view.findViewById(R.id.filterButton);
        deleteButton=view.findViewById(R.id.undoFilterButton);


        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFilter();
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoFilter();
            }
        });

        for (String s: taskListListener.getTagList()) {
            tags.addView(createChip(s));

        }

        Window window = getDialog().getWindow();
        window.setGravity(Gravity.TOP| Gravity.LEFT);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 600;
        window.setAttributes(params);

        return view;
    }

    public Chip createChip(String nameOfTag){
        Chip chip=new Chip(getContext());
        chip.setText(nameOfTag);
        chip.setCloseIconVisible(false);
        return chip;
    }

    public void launchFilter(){
        taskListListener.activateTag("exam");
        dismiss();
    }

    public void undoFilter(){
        taskListListener.deactivateAllTags();
        dismiss();
    }
}
