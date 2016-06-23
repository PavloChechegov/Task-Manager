package com.pavlochechegov.taskmanager.model;


import android.os.Parcel;
import android.os.Parcelable;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmList;
import io.realm.annotations.Ignore;
import java.util.Comparator;


public class Task extends RealmObject implements Parcelable {

    private String mTaskTitle;
    private String mTaskComment;
    private long mTaskStartTime;
    private long mTaskEndTime;
    private int mTaskColor;
    private int mIdTitle;
    private int mIdComment;

    public Task() {
    }

    public Task(String taskTitle, String taskComment, long taskStartTime, long taskEndTime, int color) {
        mTaskTitle = taskTitle;
        mTaskComment = taskComment;
        mTaskStartTime = taskStartTime;
        mTaskEndTime = taskEndTime;
        mTaskColor = color;
    }

    //getter and setter
    public int getIdTitle() {
        return mIdTitle;
    }

    public void setIdTitle(int idTitle) {
        mIdTitle = idTitle;
    }

    public int getIdComment() {
        return mIdComment;
    }

    public void setIdComment(int idComment) {
        mIdComment = idComment;
    }

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
        mIdTitle = in.readInt();
        mIdComment = in.readInt();
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
        dest.writeInt(mIdTitle);
        dest.writeInt(mIdComment);
    }

    public static Comparator<Task> AscendingTaskTitleComparator = new Comparator<Task>() {

        @Override
        public int compare(Task taskTitle1, Task taskTitle2) {
            return taskTitle1.getTaskTitle().compareToIgnoreCase(taskTitle2.getTaskTitle());
        }
    };

    public static Comparator<Task> DescendingTaskTitleComparator = new Comparator<Task>() {

        @Override
        public int compare(Task taskTitle1, Task taskTitle2) {
            return taskTitle2.getTaskTitle().compareToIgnoreCase(taskTitle1.getTaskTitle());
        }
    };

    public static Comparator<Task> AscendingTaskTimeComparator = new Comparator<Task>() {
        @Override
        public int compare(Task taskTime1, Task taskTime2) {
            return (int) (taskTime1.getTaskStartTime() - taskTime2.getTaskStartTime());
        }
    };

    public static Comparator<Task> DescendingTaskTimeComparator = new Comparator<Task>() {

        @Override
        public int compare(Task taskTime1, Task taskTime2) {
            return (int) (taskTime2.getTaskStartTime() - taskTime1.getTaskStartTime());
        }
    };
}

