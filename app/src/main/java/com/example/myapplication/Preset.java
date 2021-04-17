package com.example.myapplication;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;

public class Preset implements Serializable {
    static int ID_COUNT = 0;

    int id;
    String name;
    String category;
    int effort;
    int urgency;
    HashSet<String> tags;
    boolean calendar;

    public Preset(String name, String category, int effort, int urgency, HashSet<String> tags, boolean calendar) {
        this.id = ID_COUNT;
        ID_COUNT += 1;
        this.name = name;
        this.category = category;
        this.effort = effort;
        this.urgency = urgency;
        this.tags = tags;
        this.calendar = calendar;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setEffort(int effort) {
        this.effort = effort;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }

    public void setTags(HashSet<String> tags) {
        this.tags = tags;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getEffort() {
        return effort;
    }

    public int getUrgency() {
        return urgency;
    }

    public HashSet<String> getTags() {
        return tags;
    }

    public void setCalendar(boolean calendar) {
        this.calendar = calendar;
    }

    public boolean isCalendar() {
        return calendar;
    }

    @Override
    public String toString() {
        return "Preset {" +
                "id=" + id + ", " +
                "name='" + name + "', " +
                "category='" + category + "', " +
                "effort=" + effort + ", " +
                "urgency=" + urgency + ", " +
                "tags=" + Arrays.toString(new HashSet[]{tags}) + ", " +
                "calendar=" + calendar +
                '}';
    }
}
