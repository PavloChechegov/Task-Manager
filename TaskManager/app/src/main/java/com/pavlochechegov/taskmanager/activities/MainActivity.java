package com.pavlochechegov.taskmanager.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.pavlochechegov.taskmanager.adapter.SwipeRecyclerViewAdapter;
import com.pavlochechegov.taskmanager.fragment.DeleteDialogFragment;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.model.Task;
import com.pavlochechegov.taskmanager.model.TaskColors;
import com.pavlochechegov.taskmanager.utils.ManagerControlTask;

import java.util.*;

import static com.pavlochechegov.taskmanager.activities.TaskActivity.KEY_TASK_EXTRA;

public class MainActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteAllItem{

    public static final String KEY_SAVE_STATE = "save_instance_state";
    public static final String KEY_ITEM_EDIT_TASK = "key_edit_task";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final String TAG_ALERT_DIALOG = "alert_dialog";

    public static final int REQUEST_CODE_ADD_TASK = 1;
    public static final int REQUEST_CODE_CHANGE_TASK = 2;
    public static final int REQUEST_CODE_SETTING = 3;

    private RecyclerView mRecyclerView;
    private ArrayList<Task> mTaskArrayList;
    public static SwipeRecyclerViewAdapter sSwipeRecyclerViewAdapter;
    private Task mTask;
    private int mIntItemPosition;
    private ManagerControlTask mManagerControlTask;
    private CoordinatorLayout coordinatorLayout;
    private Random mRandom;
    private boolean doubleBackToExitPressedOnce = false;
    private TaskColors mColors;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mManagerControlTask = ManagerControlTask.getSingletonControl(MainActivity.this);

        if (savedInstanceState != null) {
            mTaskArrayList = savedInstanceState.getParcelableArrayList(KEY_SAVE_STATE);
        } else {
            mTaskArrayList = new ArrayList<>();
        }
        mColors = mManagerControlTask.initTaskItemColor();
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initTheme();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mManagerControlTask.closeRealm();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_SAVE_STATE, mTaskArrayList);
        if (!mTaskArrayList.isEmpty()) {
            mManagerControlTask.toRealm(mTaskArrayList);
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
                    mManagerControlTask.initTaskItemColor();
                    mManagerControlTask.updateTaskColor();
                    break;
                default:
                    break;
            }
        }
        updateToRealm(mTaskArrayList);
    }

    public void initTheme() {
//        mColor = mManagerControlTask.loadThemeColor();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.label_main_activity_current_tasks);
//            toolbar.setBackgroundColor(mColor);
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(CircleView.shiftColorDown(mColor));
//            getWindow().setNavigationBarColor(mColor);
//        }
    }

    // TODO: initialize all widget on screen
    private void initUI() {
        //create Dialog window for deleting all task from ArrayList<Task> and memory
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTask);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initTheme();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        //loading ArrayList<Task> from Sharedpref and showing in ListView
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTaskArrayList = mManagerControlTask.fromRealm();
                mManagerControlTask.initTaskItemColor();
                mManagerControlTask.updateTaskColor();
                sSwipeRecyclerViewAdapter = new SwipeRecyclerViewAdapter(MainActivity.this, mTaskArrayList);
                mRecyclerView.setAdapter(sSwipeRecyclerViewAdapter);
            }
        }, 500);

        //create random number for Task and Comment
        mRandom = new Random();
    }


    //update data in realm file
    private void updateToRealm(ArrayList<Task> taskArrayList) {
        mManagerControlTask.toRealm(taskArrayList);
        sSwipeRecyclerViewAdapter.notifyDataSetChanged();
    }


    //TODO: go to TaskActivity and create new Task object
    public void addNewTask(View v) {
        Intent intent = new Intent(this, TaskActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_TASK);
    }

    public void editTask(View v, int position) {
        Intent longClickIntent = new Intent(MainActivity.this, TaskActivity.class);
        longClickIntent.putExtra(KEY_ITEM_EDIT_TASK, sSwipeRecyclerViewAdapter.getItem(position));
        longClickIntent.putExtra(KEY_ITEM_POSITION, position);
        startActivityForResult(longClickIntent, REQUEST_CODE_CHANGE_TASK);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem;
        int id = mManagerControlTask.loadMenuItemChecked();

//        restore checkbox in Sort item menu
        switch (id) {
            case R.id.sort_a_z:
                menuItem = menu.findItem(id);
                menuItem.setChecked(true);
                mTaskArrayList = mManagerControlTask.sortAZ(mManagerControlTask.fromRealm());
                break;
            case R.id.sort_z_a:
                menuItem = menu.findItem(id);
                menuItem.setChecked(true);
                mTaskArrayList = mManagerControlTask.sortZA(mManagerControlTask.fromRealm());
                break;
            case R.id.sort_time_start_end:
                menuItem = menu.findItem(id);
                menuItem.setChecked(true);
                mTaskArrayList = mManagerControlTask.sortStartToEnd(mManagerControlTask.fromRealm());
                break;
            case R.id.sort_time_end_start:
                menuItem = menu.findItem(id);
                menuItem.setChecked(true);
                mTaskArrayList = mManagerControlTask.sortEndToStart(mManagerControlTask.fromRealm());
                break;
        }
        return true;
    }

    //add button on toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:
                addNewTask(item.getActionView());
                return true;

            case R.id.action_exit:
                finish();
                return true;

            //generate random Task and Comment
            case R.id.action_generate_tasks:
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
//                Collections.sort(mTaskArrayList, Task.AscendingTaskTitleComparator);
//                updateToRealm(mTaskArrayList);
                mTaskArrayList = mManagerControlTask.sortAZ(mTaskArrayList);
                sSwipeRecyclerViewAdapter.notifyDataSetChanged();
                return true;

            case R.id.sort_z_a:
                saveMenuItemChecked(item);
//                Collections.sort(mTaskArrayList, Task.DescendingTaskTimeComparator);
//                updateToRealm(mTaskArrayList);
                mTaskArrayList = mManagerControlTask.sortZA(mTaskArrayList);
                sSwipeRecyclerViewAdapter.notifyDataSetChanged();
                return true;

            case R.id.sort_time_start_end:
                saveMenuItemChecked(item);
//                Collections.sort(mTaskArrayList, Task.AscendingTaskTimeComparator);
//                updateToRealm(mTaskArrayList);
                mTaskArrayList = mManagerControlTask.sortStartToEnd(mTaskArrayList);
                updateToRealm(mTaskArrayList);
                return true;

            case R.id.sort_time_end_start:
                saveMenuItemChecked(item);
//                Collections.sort(mTaskArrayList, Task.DescendingTaskTimeComparator);
//                updateToRealm(mTaskArrayList);
                mTaskArrayList = mManagerControlTask.sortEndToStart(mTaskArrayList);
                updateToRealm(mTaskArrayList);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveMenuItemChecked(MenuItem item) {
        item.setChecked(true);
        mManagerControlTask.saveMenuItemChecked(item.getItemId());
    }

    //generate Random Task to Title and Comment fields
    public Task generateRandomTask(int i) {
        return new Task(
                UUID.randomUUID().toString(), getString(R.string.task) + i,
                getString(R.string.comment) + i,
                0,
                0,
                mColors.getDefaultColor());
    }

    //add 3 screen item in listView
    public void addThreeScreenTasks() {
        mTaskArrayList.add(0, generateRandomTask(1));
        float o = mRecyclerView.getChildAt(0).getHeight(); //total height of 1 element of recyclerView
        //float o = getHeightListViewItem(mRecyclerView);
        float y = mRecyclerView.getHeight(); // total height of recyclerView
        Log.i("Log: Height", y + " " + o);
        int k = ((int) (y / o)) * 3;
        for (int i = 2; i <= k; i++) {
            mTaskArrayList.add(0, generateRandomTask(i));
        }
        Snackbar.make(coordinatorLayout, R.string.new_task_added, Snackbar.LENGTH_SHORT).show();
        updateToRealm(mTaskArrayList);
    }

    //total height of 1 element of listView
    private int getHeightListViewItem(RecyclerView recyclerView) {
        View mView = recyclerView.getChildAt(0);
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
        mManagerControlTask.deleteAllRealm();
        sSwipeRecyclerViewAdapter.notifyDataSetChanged();
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
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
