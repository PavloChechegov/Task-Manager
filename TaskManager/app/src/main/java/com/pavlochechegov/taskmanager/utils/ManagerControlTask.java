package com.pavlochechegov.taskmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.pavlochechegov.taskmanager.model.Task;
import com.pavlochechegov.taskmanager.model.TaskColors;
import io.realm.*;

import java.util.ArrayList;

public class ManagerControlTask {
    private static final String APP_PREFERENCES = "app_preferences";
    private static final String KEY_ITEM_CHECKED = "key_item_checked";
    private static final String KEY_THEME_COLOR = "key_theme_color";
    public static final String PREFERENCE_KEY_DEFAULT_COLOR = "preference_key_default_color";
    public static final String PREFERENCE_KEY_START_TASK_COLOR = "preference_key_start_task_color";
    public static final String PREFERENCE_KEY_END_TASK_COLOR = "preference_key_end_task_color";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int mMenuItemId;
    private int mThemeColor;
    private static ManagerControlTask sManagerControlTask;
    private SharedPreferences mDefaultSetting;
    private Context mContext;
    private TaskColors mTaskColors;
    RealmConfiguration mRealmConfiguration;
    private Realm mRealm;

    public static ManagerControlTask getSingletonControl(Context context) {
        if (sManagerControlTask == null) {
            sManagerControlTask = new ManagerControlTask(context.getApplicationContext());
        }
        return sManagerControlTask;
    }

    public ManagerControlTask(Context context) {
        mContext = context;
        mRealmConfiguration = new RealmConfiguration
                .Builder(mContext)
                .deleteRealmIfMigrationNeeded()
                .name("realm_file")
                .build();

        Realm.setDefaultConfiguration(mRealmConfiguration);
        // Create a new empty instance of Realm
        mRealm = Realm.getDefaultInstance();
//        mRealm.setAutoRefresh(true);
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }


    public void deleteTaskFromRealm(String uuid) {
        mRealm.beginTransaction();
        Task taskDelete = mRealm.where(Task.class).equalTo("mId", uuid).findFirst();
        taskDelete.deleteFromRealm();
        mRealm.commitTransaction();
    }

    public void deleteAllRealm() {
        final RealmResults<Task> tasks = mRealm.where(Task.class).findAll();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                tasks.deleteAllFromRealm();
            }
        });
    }


    public void toRealm(ArrayList<Task> arrayList) {
        mRealm.beginTransaction();
        for (Task task : arrayList) {
            mRealm.copyToRealmOrUpdate(task);
        }
        Log.d("Log: List: ", String.valueOf(arrayList.size()));
        mRealm.commitTransaction();
    }

    //loading data
    public ArrayList<Task> fromRealm() {
        mRealm.beginTransaction();
        ArrayList<Task> taskArrayList = new ArrayList<>();
        RealmResults<Task> realmResults = mRealm.where(Task.class).findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            taskArrayList.add(0, realmResults.get(i));
            Log.i("Log: fromRealm", realmResults.get(i).getTaskTitle());
        }
        mRealm.commitTransaction();
        return taskArrayList;
    }

    public Task startTask(Task task, long timeStart, int color) {
        mRealm.beginTransaction();
        Task taskUpdate = mRealm.where(Task.class).equalTo("mId", task.getId()).findFirst();
        taskUpdate.setTaskColor(color);
        taskUpdate.setTaskStartTime(timeStart);
        mRealm.commitTransaction();
        return taskUpdate;
    }

    public Task stopTask(Task task, long timeEnd, int color) {
        mRealm.beginTransaction();
        Task taskUpdate = mRealm.where(Task.class).equalTo("mId", task.getId()).findFirst();
        taskUpdate.setTaskColor(color);
        taskUpdate.setTaskEndTime(timeEnd);
        mRealm.commitTransaction();
        return taskUpdate;
    }

    public ArrayList<Task> sortAZ (ArrayList<Task> arrayList) {
        mRealm.beginTransaction();
        RealmResults<Task> tasks = mRealm.where(Task.class).findAll();
        tasks = tasks.sort("mTaskTitle", Sort.ASCENDING);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            arrayList.set(i, task);
            Log.i("Log: sortAZ", task.getTaskTitle());
        }
        mRealm.commitTransaction();
        return arrayList;
    }

    public ArrayList<Task> sortZA(ArrayList<Task> arrayList) {
        mRealm.beginTransaction();

        RealmResults<Task> tasks = mRealm.where(Task.class).findAll();
        tasks = tasks.sort("mTaskTitle", Sort.DESCENDING);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            arrayList.set(i, task);
            Log.i("Log: sortZA", task.getTaskTitle());
        }
        mRealm.commitTransaction();
        return arrayList;
    }

    public ArrayList<Task> sortStartToEnd(ArrayList<Task> arrayList) {
        mRealm.beginTransaction();

        RealmResults<Task> tasks = mRealm.where(Task.class).findAll();
        tasks = tasks.sort("mTaskStartTime", Sort.ASCENDING);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            arrayList.set(i, task);
            Log.i("Log: sortStartToEnd", task.getTaskTitle());
        }
        mRealm.commitTransaction();
        return arrayList;
    }

    public ArrayList<Task> sortEndToStart(ArrayList<Task> arrayList) {
        mRealm.beginTransaction();
        RealmResults<Task> tasks = mRealm.where(Task.class).findAll();
        tasks = tasks.sort("mTaskStartTime", Sort.DESCENDING);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            arrayList.set(i, task);
            Log.i("Log: sortEndToStart", task.getTaskTitle());
        }
        mRealm.commitTransaction();
        return arrayList;
    }


    public void closeRealm() {
        if (mRealm != null) {
            mRealm.close();
        }
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

//    public void saveThemeColor(int color){
//        editor.putInt(KEY_THEME_COLOR, color);
//        editor.apply();
//    }
//
//    public int loadThemeColor(){
//        if (preferences.contains(KEY_THEME_COLOR)){
//            mThemeColor = preferences.getInt(KEY_THEME_COLOR, 0);
//        }
//        return mThemeColor;
//    }

    //initialize default colors
    public TaskColors initTaskItemColor() {
        mDefaultSetting = PreferenceManager.getDefaultSharedPreferences(mContext);
        mTaskColors = new TaskColors(mDefaultSetting.getInt(PREFERENCE_KEY_DEFAULT_COLOR, 0),
                mDefaultSetting.getInt(PREFERENCE_KEY_START_TASK_COLOR, 0),
                mDefaultSetting.getInt(PREFERENCE_KEY_END_TASK_COLOR, 0));
        return mTaskColors;
    }

    //update colors in Tasks
    public void updateTaskColor() {
        mRealm.beginTransaction();
        RealmResults<Task> realmResults = mRealm.where(Task.class).findAll();
        for (Task anArrayList : realmResults) {
            if (anArrayList.getTaskStartTime() == 0) {
                anArrayList.setTaskColor(mTaskColors.getDefaultColor());
            } else if (anArrayList.getTaskEndTime() == 0) {
                anArrayList.setTaskColor(mTaskColors.getStartColor());
            } else {
                anArrayList.setTaskColor(mTaskColors.getEndColor());
            }
        }
        mRealm.commitTransaction();
    }
}
