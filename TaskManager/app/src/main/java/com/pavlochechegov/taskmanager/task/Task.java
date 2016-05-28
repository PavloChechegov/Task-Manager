package com.pavlochechegov.taskmanager.task;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;


public class Task implements Parcelable{

    private String mTaskTitle;
    private String mTaskComment;

    public Task() {}

    public Task(String taskTitle, String taskComment) {
        mTaskTitle = taskTitle;
        mTaskComment = taskComment;
    }

    public String getTaskTitle() {
        return mTaskTitle;
    }

    public void setTaskTitle(String taskTitle) {

        mTaskTitle = taskTitle;
    }

    public String getTaskComment() {
        return mTaskComment;
    }

    public void setTaskComment(String taskComment) {
        mTaskComment = taskComment;
    }

    protected Task(Parcel in) {
        mTaskTitle = in.readString();
        mTaskComment = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTaskTitle);
        dest.writeString(mTaskComment);
    }
}
