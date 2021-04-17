package com.example.myapplication;

import java.util.ArrayList;

public interface MyTaskListListener {
    public ArrayList<Task> getFilteredTaskList();
    public void addTask(Task t);
    public void addTask(int pos, Task t);
    public void remove(int t);
    public Task getTask(int i);
}
