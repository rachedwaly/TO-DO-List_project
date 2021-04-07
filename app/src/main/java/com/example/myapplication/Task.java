package com.example.myapplication;

import java.util.List;

public class Task {
    String preset;
    String name;
    String category;
    String dueDate;
    String dueTime;
    String description;
    int effort;
    int urgency;
    String[] tags;
    boolean calendar;

    public Task(String preset, String name, String category, String dueDate, String dueTime, String description, int effort, int urgency, String[] tags, boolean calendar) {
        this.preset = preset;
        this.name = name;
        this.category = category;
        this.dueDate = dueDate;
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

    @Override
    public String toString() {
        return "Task {" +
                "preset=" + preset +
                "name='" + name + '\'' +
                '}';
    }
}
