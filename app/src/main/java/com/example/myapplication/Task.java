package com.example.myapplication;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class Task implements Serializable {
    static int ID_COUNT = 0;

    int id;
    String preset;
    String name;
    String category;
    String dueDate;
    String repeater;
    String dueTime;
    String description;
    int effort;
    int urgency;
    String[] tags;
    boolean calendar;

    public Task() {
        this.id = ID_COUNT;
        this.preset = "No preset";
        this.name = "";
        this.category = "No category";
        this.dueDate = "";
        this.repeater = "Don't repeat";
        this.dueTime = "";
        this.description = "";
        this.effort = 1;
        this.urgency = 1;
        this.tags = new String[0];
        this.calendar = false;
    }

    public Task(String preset, String name, String category, String dueDate, String repeater, String dueTime, String description, int effort, int urgency, String[] tags, boolean calendar) {
        this.id = ID_COUNT;
        this.preset = preset;
        this.name = name;
        this.category = category;
        this.dueDate = dueDate;
        this.repeater = repeater;
        this.dueTime = dueTime;
        this.description = description;
        this.effort = effort;
        this.urgency = urgency;
        this.tags = tags;
        this.calendar = calendar;
    }

    public String getPreset() {
        return this.preset;
    }

    public void setPreset(String preset) {
        this.preset = preset;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setRepeater(String repeater) {
        this.repeater = repeater;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEffort(int effort) {
        this.effort = effort;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setCalendar(boolean calendar) {
        this.calendar = calendar;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getRepeater() {
        return repeater;
    }

    public String getDueTime() {
        return dueTime;
    }

    public String getDescription() {
        return description;
    }

    public int getEffort() {
        return effort;
    }

    public int getUrgency() {
        return urgency;
    }

    public String[] getTags() {
        return tags;
    }

    public boolean isCalendar() {
        return calendar;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Task {" +
                "id=" + id + ", " +
                "preset='" + preset + "', " +
                "name='" + name + "', " +
                "category='" + category + "', " +
                "due date='" + dueDate + "', " +
                "repeat='" + repeater + "'," +
                "due time='" + dueTime + "', " +
                "description='" + description + "', " +
                "effort=" + effort + ", " +
                "urgency=" + urgency + ", " +
                "tags=" + Arrays.toString(tags) + ", " +
                "calendar=" + calendar +
                '}';
    }
}
