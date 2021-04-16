package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class CardDetailedFragment extends DialogFragment {
    private TextView description;
    private TextView taskName;
    private TextView dueDate;
    private TextView category;
    private TextView dueTime;
    private ChipGroup tags;

    private int position;
    private MyTaskListListener listener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            //listener of the list of tasks created in the MainActivity
            listener = (MyTaskListListener) context;
        } catch (ClassCastException castException) {

        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.card_detailed_popup,container,false);
        Task currentTask=listener.getTask(position);
        description=view.findViewById(R.id.DescriptionOfTask);
        taskName=view.findViewById(R.id.NameOfTask);
        dueDate=view.findViewById(R.id.DueDate);
        category=view.findViewById(R.id.category);
        dueTime=view.findViewById(R.id.DueTime);
        tags = view.findViewById(R.id.tags);

        for (String s: currentTask.getTags()) {
            tags.addView(createChip(s));

        }




        description.setText(currentTask.getDescription());
        taskName.setText(currentTask.getName());
        dueDate.setText(currentTask.getDueDate());
        category.setText(currentTask.getCategory());
        dueTime.setText(currentTask.getDueTime());


        Window window = getDialog().getWindow();
        window.setGravity(Gravity.TOP| Gravity.LEFT);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 600;
        window.setAttributes(params);

        return view;
    }

    public void fillDialogFragment(int position){
        this.position=position;
    }

    public Chip createChip(String nameOfTag){
        Chip chip=new Chip(getContext());
        chip.setText(nameOfTag);
        chip.setCloseIconVisible(false);
        return chip;
    }

}
