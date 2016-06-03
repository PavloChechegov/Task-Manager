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

    private String mTaskTitle;
    private String mTaskComment;
    private String mTaskStartTime;
    private String mTaskEndTime;

    public Task() {}

    public Task(String taskTitle, String taskComment, String taskStartTime, String taskEndTime) {
        mTaskTitle = taskTitle;
        mTaskComment = taskComment;
        mTaskStartTime = taskStartTime;
        mTaskEndTime = taskEndTime;
    }

    //getter and setter
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

    public String getTaskEndTime() {
        return mTaskEndTime;
    }

    public void setTaskEndTime(String taskEndTime) {
        mTaskEndTime = taskEndTime;
    }

    public String getTaskStartTime() {
        return mTaskStartTime;
    }

    public void setTaskStartTime(String taskStartTime) {
        mTaskStartTime = taskStartTime;
    }
    //getter and setter

    protected Task(Parcel in) {
        mTaskTitle = in.readString();
        mTaskComment = in.readString();
        mTaskStartTime = in.readString();
        mTaskEndTime = in.readString();
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
        dest.writeString(mTaskStartTime);
        dest.writeString(mTaskEndTime);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        //JSONArray jsonArray = new JSONArray();

        try {

            jsonObject.put(TITLE_TASK, mTaskTitle);
            jsonObject.put(COMMENT_TASK, mTaskComment);
            jsonObject.put(START_DATE_TASK, mTaskStartTime);
            jsonObject.put(END_DATE_TASK, mTaskEndTime);
//            jsonObject.getJSONArray("task");
            //   jsonArray.put(jsonObject);

            return jsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Task(JSONObject jsonObject) throws JSONException {
        mTaskTitle = jsonObject.getString(TITLE_TASK);
        Log.i("mStringTitle: ____", mTaskTitle);
        mTaskComment = jsonObject.getString(COMMENT_TASK);
        mTaskStartTime = jsonObject.getString(START_DATE_TASK);
        mTaskEndTime = jsonObject.getString(END_DATE_TASK);

    }
}
