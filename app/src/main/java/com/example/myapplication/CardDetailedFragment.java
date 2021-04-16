package com.example.myapplication;

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

import com.google.android.material.chip.ChipGroup;

public class CardDetailedFragment extends DialogFragment {
    private TextView description;
    private ChipGroup tags;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.card_detailed_popup,container,false);

        description=view.findViewById(R.id.DescriptionOfTask);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.TOP| Gravity.LEFT);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 600;
        window.setAttributes(params);

        return view;
    }

    public void fillDialogFragment(Task task){
        //description.setText(task.getDescription());

    }

}
