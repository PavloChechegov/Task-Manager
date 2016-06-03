package com.pavlochechegov.taskmanager.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;


public class Task implements Parcelable{
    private static final String TITLE_TASK = "title_task";
    private static final String COMMENT_TASK = "comment_task";
    private static final String START_DATE_TASK = "start_date_task";
    private static final String END_DATE_TASK = "end_date_task";
    private static final String KEY_COLOR = "key_color";

    private String mTaskTitle;
    private String mTaskComment;
    private long mTaskStartTime;
    private long mTaskEndTime;
    private int mTaskColor;
    private boolean isSelected;
    public Task() {}

    public Task(String taskTitle, String taskComment, long taskStartTime, long taskEndTime, int color) {
        mTaskTitle = taskTitle;
        mTaskComment = taskComment;
        mTaskStartTime = taskStartTime;
        mTaskEndTime = taskEndTime;
        mTaskColor = color;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
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

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TITLE_TASK, mTaskTitle);
            jsonObject.put(COMMENT_TASK, mTaskComment);
            jsonObject.put(START_DATE_TASK, mTaskStartTime);
            jsonObject.put(END_DATE_TASK, mTaskEndTime);
            jsonObject.put(KEY_COLOR, mTaskColor);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public Task(JSONObject jsonObject) throws JSONException {
        mTaskTitle = jsonObject.getString(TITLE_TASK);
        mTaskComment = jsonObject.getString(COMMENT_TASK);
        mTaskStartTime = jsonObject.getLong(START_DATE_TASK);
        mTaskEndTime = jsonObject.getLong(END_DATE_TASK);
        mTaskColor = jsonObject.getInt(KEY_COLOR);
    }
}
