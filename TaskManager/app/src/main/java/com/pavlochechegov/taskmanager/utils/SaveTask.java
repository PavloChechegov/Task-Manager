package com.pavlochechegov.taskmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pavlochechegov.taskmanager.model.Task;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SaveTask {
    public static final String KEY_DATA_SAVE = "key_data_save";
    private static final String APP_PREFERENCES = "app_preferences";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    public SaveTask(Context context) {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();
    }

    //save data
    public void saveData(ArrayList<Task> taskArrayList) {
        String jsonStr = gson.toJson(taskArrayList);
        editor.putString(KEY_DATA_SAVE, jsonStr);
        editor.apply();

    }

    //loading data
    public ArrayList<Task> loadData() {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        Type collectionType = new TypeToken<ArrayList<Task>>() {}.getType();
        if (preferences.contains(KEY_DATA_SAVE)) {
                taskArrayList = gson.fromJson(preferences.getString(KEY_DATA_SAVE, ""), collectionType);
        }
        return taskArrayList;
    }

    //delete SharedPreferences file
    public void clearData() {
        editor.remove(KEY_DATA_SAVE);
        editor.clear();
        editor.commit();
    }
}
