package com.pavlochechegov.taskmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.pavlochechegov.taskmanager.json.TaskJSON;
import com.pavlochechegov.taskmanager.model.Task;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class SaveTask {
    public static final String KEY_DATA_SAVE = "key_data_save";
    private static final String APP_PREFERENCES = "app_preferences";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SaveTask(Context context) {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    //save data
    public void saveData(ArrayList<Task> taskArrayList) {
        TaskJSON mTaskJSON = new TaskJSON();
        try {
            editor.putString(KEY_DATA_SAVE, mTaskJSON.saveTask(taskArrayList));
            editor.apply();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    //loading data
    public ArrayList<Task> loadData(){
        ArrayList<Task> taskArrayList = new ArrayList<>();
        if (preferences.contains(KEY_DATA_SAVE)){
            TaskJSON taskJSON = new TaskJSON();
            try {
                taskArrayList = taskJSON.loadTask(preferences.getString(KEY_DATA_SAVE, ""));
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        return taskArrayList;
    }

    public void clearData() {
        editor.remove(KEY_DATA_SAVE);
        editor.clear();
        editor.commit();
    }
}
