package com.pavlochechegov.taskmanager.model;


import android.os.Parcel;
import android.os.Parcelable;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.Comparator;


public class Task extends RealmObject implements Parcelable {

    private static final int TASK_STOP = 4;
    @PrimaryKey
    private String mId;
    private String mTaskTitle;
    private String mTaskComment;
    private long mTaskStartTime;
    private long mTaskEndTime;
    private int mTaskColor;
    private boolean isPause;
    private boolean isStop;
    private long mTimePause;

    public static final int TASK_START = 1;
    public static final int TASK_PAUSE = 2;
    public static final int TASK_RESUME = 3;


    public Task() {
    }

    public Task(String id, String taskTitle, String taskComment, long taskStartTime, long taskEndTime, int color) {
        mId = id;
        mTaskTitle = taskTitle;
        mTaskComment = taskComment;
        mTaskStartTime = taskStartTime;
        mTaskEndTime = taskEndTime;
        mTaskColor = color;
    }

    //getter and setter

    //get status of task
    public int getTaskStatus() {
        if (getTaskStartTime() == 0 && !isPause()) {
            return TASK_START;
        } else if (isPause()) {
            return TASK_PAUSE;
        } else {
            return TASK_RESUME;
        }

    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
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
        mId = in.readString();

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
        dest.writeString(mId);

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

    public long getTimePause() {
        return mTimePause;
    }

    public void setTimePause(long timePause) {
        mTimePause = timePause;
    }

    public long getTimeDifference() {
        return getTimePause() != 0 ? getTimePause() - getTaskStartTime() : 0;
    }

}

