package com.pavlochechegov.taskmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pavlochechegov.taskmanager.model.Task;
import com.pavlochechegov.taskmanager.model.TaskColors;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ManagerControlTask {
    public static final String KEY_DATA_SAVE = "key_data_save";
    private static final String APP_PREFERENCES = "app_preferences";
    private static final String KEY_ITEM_CHECKED = "key_item_checked";
    private static final String KEY_THEME_COLOR = "key_theme_color";
    public static final String PREFERENCE_KEY_DEFAULT_COLOR = "preference_key_default_color";
    public static final String PREFERENCE_KEY_START_TASK_COLOR = "preference_key_start_task_color";
    public static final String PREFERENCE_KEY_END_TASK_COLOR = "preference_key_end_task_color";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private int mMenuItemId;
    private int mThemeColor;
    private static ManagerControlTask sManagerControlTask;
    private int mDefaultTaskColor, mStartTaskColor, mEndTaskColor;
    private SharedPreferences mDefaultSetting;
    private Context mContext;
    private TaskColors mTaskColors;
    RealmConfiguration mRealmConfiguration;
    private Realm mRealm;
    private int mItemId;

//    private static ManagerControlTask getSaveTask(Context context){
//        if(sManagerControlTask == null){
//            sManagerControlTask = new ManagerControlTask(context.getApplicationContext());
//
//        }
//        return sManagerControlTask;
//    }


    public ManagerControlTask() {
    }

    public ManagerControlTask(Context context) {
        mContext = context;
        mRealmConfiguration = new RealmConfiguration.Builder(mContext).build();

//        // Clear the realm from last time
//        Realm.deleteRealm(mRealmConfiguration);

        // Create a new empty instance of Realm
        mRealm = Realm.getInstance(mRealmConfiguration);
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();
    }

    //save data
    public void saveArrayList(ArrayList<Task> taskArrayList) {
        String jsonStr = gson.toJson(taskArrayList);
        editor.putString(KEY_DATA_SAVE, jsonStr);
        editor.apply();
    }

    //loading data
    public ArrayList<Task> loadArrayList() {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        Type collectionType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        if (preferences.contains(KEY_DATA_SAVE)) {
            taskArrayList = gson.fromJson(preferences.getString(KEY_DATA_SAVE, ""), collectionType);
        }
        return taskArrayList;
    }

    //delete SharedPreferences file
    public void clearDataInSharedPreferences() {
        editor.remove(KEY_DATA_SAVE);
        editor.clear();
        editor.commit();
    }

    public void saveMenuItemChecked(int itemId) {
        editor.putInt(KEY_ITEM_CHECKED, itemId);
        editor.apply();
    }

    public int loadMenuItemChecked() {
        if (preferences.contains(KEY_ITEM_CHECKED)) {
            mMenuItemId = preferences.getInt(KEY_ITEM_CHECKED, 0);
        }
        return mMenuItemId;
    }

    public void saveThemeColor(int color){
        editor.putInt(KEY_THEME_COLOR, color);
        editor.apply();
    }

    public int loadThemeColor(){
        if (preferences.contains(KEY_THEME_COLOR)){
            mThemeColor = preferences.getInt(KEY_THEME_COLOR, 0);
        }
        return mThemeColor;
    }

    //initialize default colors
    public TaskColors initTaskItemColor() {
        mDefaultSetting = PreferenceManager.getDefaultSharedPreferences(mContext);
        mTaskColors = new TaskColors(mDefaultSetting.getInt(PREFERENCE_KEY_DEFAULT_COLOR, 0),
                mDefaultSetting.getInt(PREFERENCE_KEY_START_TASK_COLOR, 0),
                mDefaultSetting.getInt(PREFERENCE_KEY_END_TASK_COLOR, 0));
        return mTaskColors;
    }

    //update colors in Tasks
    public void updateTaskColor(ArrayList<Task> arrayList) {
        for (Task anArrayList : arrayList) {
            if (anArrayList.getTaskStartTime() == 0) {
                anArrayList.setTaskColor(mTaskColors.getDefaultColor());
            } else if (anArrayList.getTaskEndTime() == 0) {
                anArrayList.setTaskColor(mTaskColors.getStartColor());
            } else {
                anArrayList.setTaskColor(mTaskColors.getEndColor());
            }
        }
    }


}
