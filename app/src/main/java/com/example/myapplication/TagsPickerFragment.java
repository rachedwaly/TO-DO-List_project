package com.example.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashSet;

public class TagsPickerFragment extends DialogFragment {

    private static final String ARG_TAGS = "tags";
    private static final String ARG_SELECTED_TAGS = "selectedTags";

    private HashSet<String> mTagsList;
    private HashSet<String> mSelectedTags;

    private EditText newTagName;
    private ImageButton addTagsButton;
    private ChipGroup tagsGroup;

    public interface onTagsEventListener {
        public void addTags(String s);
        public void addNewTags(String s);
        public void removeTags(String s);
    }

    onTagsEventListener tagsEventListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            tagsEventListener = (onTagsEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    public static TagsPickerFragment newInstance(HashSet<String> tagsList, HashSet<String> selectedTags) {
        TagsPickerFragment frag = new TagsPickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TAGS, tagsList);
        args.putSerializable(ARG_SELECTED_TAGS, selectedTags);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTagsList = (HashSet<String>) getArguments().getSerializable(ARG_TAGS);
            mSelectedTags = (HashSet<String>) getArguments().getSerializable(ARG_SELECTED_TAGS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tagspicker, container);

        //Load widgets
        newTagName = (EditText) view.findViewById(R.id.newTagName);
        tagsGroup = (ChipGroup) view.findViewById(R.id.tagsGroup);
        addTagsButton = (ImageButton) view.findViewById(R.id.addTag);
        addTagsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String newTag = newTagName.getText().toString();
                if (!newTag.equals("")) {
                    tagsEventListener.addNewTags(newTag);

                    mTagsList.add(newTag);
                    mSelectedTags.add(newTag);

                    Chip chip = new Chip(getContext());
                    chip.setText(newTag);
                    chip.setCheckable(true);
                    chip.setChecked(true);

                    tagsGroup.addView(chip);
                }
            }
        });

        //Initialize chip group
        for (String tag : mTagsList) {
            Chip chip = new Chip(getContext());
            chip.setText(tag);
            chip.setCheckable(true);
            if (mSelectedTags.contains(tag)) {
                chip.setChecked(true);
            }

            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mSelectedTags.add(tag);
                        tagsEventListener.addTags(tag);
                    } else {
                        mSelectedTags.remove(tag);
                        tagsEventListener.removeTags(tag);
                    }
                }
            });

            tagsGroup.addView(chip);

        }

        return view;
    }



}
