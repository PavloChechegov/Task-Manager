package com.pavlochechegov.taskmanager.json;

import android.content.Context;
import android.util.Log;
import com.pavlochechegov.taskmanager.model.Task;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;

public class TaskJSON {

    private ArrayList<Task> mTaskArrayList;

    //convert task to string in json format
    public String saveTask(ArrayList<Task> tasks) throws JSONException, IOException {
        JSONArray jsonArray = new JSONArray();
        for (Task task : tasks) {
            jsonArray.put(task.toJSON());
        }
        return jsonArray.toString();
    }

    //convert from stringJSON to ArrayList
    public ArrayList<Task> loadTask(String stringJSON) throws IOException, JSONException {
        mTaskArrayList = new ArrayList<>();
        JSONObject jsonObject;

        JSONArray jsonArray = (JSONArray) new JSONTokener(stringJSON).nextValue();

        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            Task task = new Task(jsonObject);
            mTaskArrayList.add(task);
        }
        return mTaskArrayList;
    }
}
