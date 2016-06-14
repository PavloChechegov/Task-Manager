package com.pavlochechegov.taskmanager.compareTask;

import com.pavlochechegov.taskmanager.model.Task;

import java.util.Comparator;


public class CompareTaskByTitle implements Comparator<Task> {

    @Override
    public int compare(Task taskTitle1, Task taskTitle2) {
        return taskTitle1.getTaskTitle().compareToIgnoreCase(taskTitle2.getTaskTitle());
    }
}
