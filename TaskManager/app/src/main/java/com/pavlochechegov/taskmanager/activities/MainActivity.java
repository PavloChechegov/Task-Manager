package com.pavlochechegov.taskmanager.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.pavlochechegov.taskmanager.fragment.DeleteDialogFragment;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.model.Task;
import com.pavlochechegov.taskmanager.adapter.TaskBaseAdapter;
import com.pavlochechegov.taskmanager.utils.SaveTask;

import java.util.*;

import static com.pavlochechegov.taskmanager.activities.TaskActivity.KEY_TASK_EXTRA;

public class MainActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteAllItem {

    public static final String KEY_SAVE_STATE = "save_instance_state";
    public static final String KEY_ITEM_LONG_CLICK = "long_click_item";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final String PREFERENCE_KEY_DEFAULT_COLOR = "preference_key_default_color";
    public static final String PREFERENCE_KEY_START_TASK_COLOR = "preference_key_start_task_color";
    public static final String PREFERENCE_KEY_END_TASK_COLOR = "preference_key_end_task_color";
    public static final String TAG_ALERT_DIALOG = "alert_dialog";
    private static final int REQUEST_CODE_ADD_TASK = 1;
    private static final int REQUEST_CODE_CHANGE_TASK = 2;
    private static final int REQUEST_CODE_SETTING = 3;
    private ListView mTaskListView;
    private ArrayList<Task> mTaskArrayList;
    private TaskBaseAdapter mTaskBaseAdapter;
    private Task mTask;
    private long mTaskTimeStart, mTaskTimeEnd;
    private int mIntItemPosition;
    private SaveTask mSaveTask;
    private CoordinatorLayout coordinatorLayout;
    private Random mRandom;
    private int defaultTaskColor, startColor, endColor;
    private SharedPreferences mDefaultSetting;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSaveTask = new SaveTask(MainActivity.this);
        if (savedInstanceState != null) {
            mTaskArrayList = savedInstanceState.getParcelableArrayList(KEY_SAVE_STATE);
        } else {
            mTaskArrayList = new ArrayList<>();
        }
        initColor();
        initUI();
    }

    //initialize default colors
    public void initColor() {
        mDefaultSetting = PreferenceManager.getDefaultSharedPreferences(this);
        defaultTaskColor = mDefaultSetting.getInt(PREFERENCE_KEY_DEFAULT_COLOR, 0);
        startColor = mDefaultSetting.getInt(PREFERENCE_KEY_START_TASK_COLOR, 0);
        endColor = mDefaultSetting.getInt(PREFERENCE_KEY_END_TASK_COLOR, 0);
    }

    //update colors in Tasks
    public void updateColor(ArrayList<Task> arrayList) {
        for (Task anArrayList : arrayList) {
            if (anArrayList.getTaskStartTime() == 0) {
                anArrayList.setTaskColor(defaultTaskColor);
            } else if (anArrayList.getTaskEndTime() == 0) {
                anArrayList.setTaskColor(startColor);
            } else {
                anArrayList.setTaskColor(endColor);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_SAVE_STATE, mTaskArrayList);
        if (!mTaskArrayList.isEmpty()) {
            mSaveTask.saveData(mTaskArrayList);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            switch (requestCode) {
                case 1:
                    mTask = data.getParcelableExtra(KEY_TASK_EXTRA);
                    mTaskArrayList.add(0, mTask);
                    break;
                case 2:
                    mTask = data.getParcelableExtra(KEY_TASK_EXTRA);
                    mIntItemPosition = data.getIntExtra(KEY_ITEM_POSITION, DEFAULT_KEYS_DIALER);
                    mTaskArrayList.set(mIntItemPosition, mTask);
                    break;
                case 3:
                    initColor();
                    updateColor(mTaskArrayList);
                    break;
                default:
                    break;
            }
        }
        saveSortedTask(mTaskArrayList);
    }

    // TODO: initialize all widget on screen
    private void initUI() {

        //create Dialog window for deleting all task from ArrayList<Task> and memory
        mTaskListView = (ListView) findViewById(R.id.listViewTask);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.label_main_activity_current_tasks);


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        //loading ArrayList<Task> from Sharedpref and showing in ListView
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTaskArrayList = mSaveTask.loadData();
                updateColor(mTaskArrayList);
                mTaskBaseAdapter = new TaskBaseAdapter(getBaseContext(), mTaskArrayList);
                mTaskListView.setAdapter(mTaskBaseAdapter);
            }
        }, 500);

        //create random number for Task and Comment
        mRandom = new Random();

        //add TimeStart and TimeEnd task in items of ListView
        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Task task = mTaskBaseAdapter.getItem(position);

                if (task.getTaskStartTime() == 0) {

                    mTaskTimeStart = System.currentTimeMillis();
                    task.setTaskStartTime(mTaskTimeStart);
                    task.setTaskColor(startColor);
                    mTaskArrayList.set(position, task);
                    Snackbar.make(view, R.string.task_started, Snackbar.LENGTH_SHORT).show();

                } else if (task.getTaskEndTime() == 0) {

                    mTaskTimeEnd = System.currentTimeMillis();
                    task.setTaskEndTime(mTaskTimeEnd);
                    task.setTaskColor(endColor);
                    mTaskArrayList.set(position, task);
                    Snackbar.make(view, R.string.task_finished, Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(R.color.start_task_color))
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    task.setTaskEndTime(0);
                                    task.setTaskColor(startColor);
                                    mTaskArrayList.set(position, task);
                                    mTaskBaseAdapter.notifyDataSetChanged();
                                }
                            }).show();
                }
                saveSortedTask(mTaskArrayList);
            }
        });

        mTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent longClickIntent = new Intent(MainActivity.this, TaskActivity.class);
                longClickIntent.putExtra(KEY_ITEM_LONG_CLICK, mTaskBaseAdapter.getItem(position));
                longClickIntent.putExtra(KEY_ITEM_POSITION, position);
                startActivityForResult(longClickIntent, REQUEST_CODE_CHANGE_TASK);
                return true;
            }
        });
    }

    // save Arralist to SharedPreference
    private void saveSortedTask(ArrayList<Task> taskArrayList) {
        mSaveTask.saveData(taskArrayList);
        mTaskBaseAdapter.notifyDataSetChanged();
    }


    //TODO: go to TaskActivity and create Task object
    public void addTaskToListView(View v) {
        Intent intent = new Intent(this, TaskActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_TASK);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem;
        int id = mSaveTask.loadItemChecked();

        //restore checkbox in Sort item menu
        switch (id){
            case R.id.sort_a_z:
                menuItem = menu.findItem(id);
                menuItem.setChecked(true);
                break;
            case R.id.sort_z_a:
                menuItem = menu.findItem(id);
                menuItem.setChecked(true);
                break;
            case R.id.sort_time_start_end:
                menuItem = menu.findItem(id);
                menuItem.setChecked(true);
                break;
            case R.id.sort_time_end_start:
                menuItem = menu.findItem(id);
                menuItem.setChecked(true);
                break;
        }
        return true;
    }

    //add button on toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:
                addTaskToListView(item.getActionView());
                return true;

            case R.id.action_exit:
                finish();
                return true;

            //generate random Task and Comment
            case R.id.action_generate_tasks:
                mTaskArrayList.add(0, generateRandomTask());
                addThreeScreenTasks();
                return true;

            //delete all elements from ArrayList<Task> and memory
            case R.id.action_delete_elements:
                if (!mTaskArrayList.isEmpty()) {
                    new DeleteDialogFragment().show(getSupportFragmentManager(), TAG_ALERT_DIALOG);
                } else {
                    Snackbar.make(coordinatorLayout, R.string.empty_list, Snackbar.LENGTH_SHORT).show();
                }
                return true;

            //go to SettingsActivity
            case R.id.action_setting:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SETTING);
                return true;

            //sorting item in ListView
            case R.id.sort_item:
                return true;

            case R.id.sort_a_z:
                saveMenuItemChecked(item);
                Collections.sort(mTaskArrayList, Task.AscendingTaskTitleComparator);
                saveSortedTask(mTaskArrayList);
                return true;

            case R.id.sort_z_a:
                saveMenuItemChecked(item);
                Collections.sort(mTaskArrayList, Task.DescendingTaskTitleComparator);
                saveSortedTask(mTaskArrayList);
                return true;

            case R.id.sort_time_start_end:
                saveMenuItemChecked(item);
                Collections.sort(mTaskArrayList, Task.AscendingTaskTimeComparator);
                saveSortedTask(mTaskArrayList);
                return true;

            case R.id.sort_time_end_start:
                saveMenuItemChecked(item);
                Collections.sort(mTaskArrayList, Task.DescendingTaskTimeComparator);
                saveSortedTask(mTaskArrayList);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveMenuItemChecked(MenuItem item){
        item.setChecked(true);
        mSaveTask.saveItemChecked(item.getItemId());
    }

    //generate Random Task to Title and Comment fields
    public Task generateRandomTask() {
        return new Task(
                getString(R.string.task) + mRandom.nextInt(150),
                getString(R.string.comment) +  mRandom.nextInt(150),
                0,
                0,
                defaultTaskColor);
    }

    //add 3 screen item in listView
    public void addThreeScreenTasks() {
        float o = getHeightListViewItem(mTaskListView, mTaskBaseAdapter);
        float y = mTaskListView.getHeight();
        int k = ((int) (y / o)) * 3;
        for (int i = 2; i <= k; i++) {
            mTaskArrayList.add(0, generateRandomTask());
        }
        Snackbar.make(coordinatorLayout, R.string.new_task_added, Snackbar.LENGTH_SHORT).show();
        saveSortedTask(mTaskArrayList);
    }

    //total height of 1 element of listView
    private int getHeightListViewItem(ListView listView, TaskBaseAdapter taskBaseAdapter) {
        View mView = taskBaseAdapter.getView(0, null, listView);
        mView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int listViewElementsHeight = mView.getMeasuredHeight();
        return listViewElementsHeight;
    }

    //delete ArrayList and SharedPreference data
    @Override
    public void delete() {
        mTaskArrayList.clear();
        mSaveTask.clearData();
        mTaskBaseAdapter.notifyDataSetChanged();
    }

    //double press back button to exit
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Snackbar.make(coordinatorLayout, R.string.double_on_back_pressed, Snackbar.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
