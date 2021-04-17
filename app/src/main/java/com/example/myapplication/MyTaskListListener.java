package com.example.myapplication;

import java.util.ArrayList;
import java.util.HashSet;

public interface MyTaskListListener {
    public ArrayList<Task> getFilteredTaskList();
    public void addTask(Task t);
    public void addTask(int pos, Task t);
    public void remove(int t);
    public Task getTask(int i);
    public HashSet<String> getTagList();
    public HashSet<String> getActiveTagList();
    public void activateTag(String tag);
    public void deactivateTag(String tag);
    public void addNewTag(String tag);
}
