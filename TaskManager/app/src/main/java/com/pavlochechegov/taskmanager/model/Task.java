package com.pavlochechegov.taskmanager.model;


import android.os.Parcel;
import android.os.Parcelable;


public class Task implements Parcelable{
    private String mTaskTitle;
    private String mTaskComment;
    private long mTaskStartTime;
    private long mTaskEndTime;
    private int mTaskColor;

    public Task(String taskTitle, String taskComment, long taskStartTime, long taskEndTime, int color) {
        mTaskTitle = taskTitle;
        mTaskComment = taskComment;
        mTaskStartTime = taskStartTime;
        mTaskEndTime = taskEndTime;
        mTaskColor = color;
    }

    //getter and setter
    public int getTaskColor() {
        return mTaskColor;
    }

    public void setTaskColor(int taskColor) {
        mTaskColor = taskColor;
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

    public long getTaskEndTime() {
        return mTaskEndTime;
    }

    public void setTaskEndTime(long taskEndTime) {
        mTaskEndTime = taskEndTime;
    }

    public long getTaskStartTime() {
        return mTaskStartTime;
    }

    public void setTaskStartTime(long taskStartTime) {
        mTaskStartTime = taskStartTime;
    }
    //getter and setter

    protected Task(Parcel in) {
        mTaskTitle = in.readString();
        mTaskComment = in.readString();
        mTaskStartTime = in.readLong();
        mTaskEndTime = in.readLong();
        mTaskColor = in.readInt();
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
        dest.writeLong(mTaskStartTime);
        dest.writeLong(mTaskEndTime);
        dest.writeInt(mTaskColor);
    }
}
