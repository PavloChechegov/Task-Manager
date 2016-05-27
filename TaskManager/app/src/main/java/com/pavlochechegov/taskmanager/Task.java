package com.pavlochechegov.taskmanager;


public class Task {

    private String mTaskTitle;
    private String mTaskDescription;

    public Task(String taskTitle, String taskDescription) {
        mTaskTitle = taskTitle;
        mTaskDescription = taskDescription;
    }

    public String getTaskTitle() {
        return mTaskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        mTaskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return mTaskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        mTaskDescription = taskDescription;
    }
}
