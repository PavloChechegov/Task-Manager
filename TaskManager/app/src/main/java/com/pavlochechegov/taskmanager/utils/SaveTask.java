package com.pavlochechegov.taskmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.pavlochechegov.taskmanager.json.TaskJSON;
import com.pavlochechegov.taskmanager.model.Task;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class SaveTask {
    private ArrayList<Task> mTaskArrayList;
    public static final String KEY_DATA_SAVE = "key_data_save";

    //save data
    public static void saveData(Context context,
                                   ArrayList<Task> taskArrayList,
                                   SharedPreferences preferences,
                                   String keySetting,
                                   SharedPreferences.Editor editor) {
        preferences = context.getSharedPreferences(keySetting, Context.MODE_PRIVATE);
        TaskJSON mTaskJSON = new TaskJSON();
        editor = preferences.edit();

        try {
            editor.putString(KEY_DATA_SAVE, mTaskJSON.saveTask(taskArrayList));
            editor.apply();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    //loading data
    public static ArrayList<Task> loadData(SharedPreferences preferences){
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

    public static void clearData(SharedPreferences.Editor editor) {
        editor.remove(KEY_DATA_SAVE);
        editor.clear();
        editor.commit();
    }
}
