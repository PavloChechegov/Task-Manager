package com.pavlochechegov.taskmanager.activities;

import android.content.Intent;
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
import android.widget.TabHost;
import com.pavlochechegov.taskmanager.adapter.SwipeRecyclerViewAdapter;
import com.pavlochechegov.taskmanager.fragment.DeleteDialogFragment;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.model.Task;
import com.pavlochechegov.taskmanager.model.TaskColors;
import com.pavlochechegov.taskmanager.utils.RealmControl;

import java.util.*;

import static com.pavlochechegov.taskmanager.activities.TaskActivity.KEY_TASK_EXTRA;

public class MainActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteAllItem{

    public static final String KEY_SAVE_STATE = "save_instance_state";
    public static final String KEY_ITEM_EDIT_TASK = "key_edit_task";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final String TAG_ALERT_DIALOG = "alert_dialog";

    public static final int REQUEST_CODE_ADD_TASK = 1;
    public static final int REQUEST_CODE_EDIT_TASK = 2;
    public static final int REQUEST_CODE_SETTING = 3;

    private RecyclerView mRecyclerView;
    private ArrayList<Task> mTaskArrayList;
    public static SwipeRecyclerViewAdapter sSwipeRecyclerViewAdapter;
    private Task mTask;
    private int mIntItemPosition;
    private RealmControl mRealmControl;
    private CoordinatorLayout coordinatorLayout;
    private Random mRandom;
    private boolean doubleBackToExitPressedOnce = false;
    private TaskColors mColors;
    Toolbar toolbar;
    TabHost tabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRealmControl = RealmControl.getSingletonControl(MainActivity.this);
        //create random number for Task and Comment
        mRandom = new Random();

        if (savedInstanceState != null) {
            mTaskArrayList = savedInstanceState.getParcelableArrayList(KEY_SAVE_STATE);
        } else {
            mTaskArrayList = new ArrayList<>();
        }
        mColors = mRealmControl.initTaskItemColor();
        initUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_SAVE_STATE, mTaskArrayList);
        if (!mTaskArrayList.isEmpty()) {
            mRealmControl.toRealm(mTaskArrayList);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            switch (requestCode) {
                case 1:
                    mTask = data.getParcelableExtra(KEY_TASK_EXTRA);
                    mTaskArrayList.add(0, mTask);
                    Log.d("myLog", "id:" + mTask.getId());
                    break;
                case 2:
                    mTask = data.getParcelableExtra(KEY_TASK_EXTRA);
                    mIntItemPosition = data.getIntExtra(KEY_ITEM_POSITION, DEFAULT_KEYS_DIALER);
                    mTaskArrayList.set(mIntItemPosition, mTask);
                    break;
                case 3:
                    mRealmControl.initTaskItemColor();
                    mRealmControl.updateTaskColor();
                    break;
                default:
                    break;
            }
        }
        updateToRealm(mTaskArrayList);
    }

/*
    public void initTabHost(){

    }*/

    // TODO: initialize all widget on screen
    private void initUI() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.label_main_activity_current_tasks);
        }
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);

        // init tabHost
        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("tab1");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_task_icon_selector));
        tabSpec.setContent(R.id.tabTaskRecyclerView);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab2");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_statistic_selector));
        tabSpec.setContent(R.id.expandableMonthStatistic);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTabByTag("tag1"); ;

/*        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                Toast.makeText(getBaseContext(), "tabId = " + tabId, Toast.LENGTH_SHORT).show();
            }
        });*/

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTask);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //loading ArrayList<Task> from Sharedpref and showing in ListView
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTaskArrayList = mRealmControl.fromRealm();
                mRealmControl.initTaskItemColor();
                mRealmControl.updateTaskColor();
                sSwipeRecyclerViewAdapter = new SwipeRecyclerViewAdapter(MainActivity.this, mTaskArrayList);
                mRecyclerView.setAdapter(sSwipeRecyclerViewAdapter);
            }
        }, 500);
    }

    //update data in realm file
    private void updateToRealm(ArrayList<Task> taskArrayList) {
        mRealmControl.toRealm(taskArrayList);
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
        startActivityForResult(longClickIntent, REQUEST_CODE_EDIT_TASK);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem;
        int id = mRealmControl.loadMenuItemChecked();

//        restore checkbox in Sort item menu
        switch (id) {
            case R.id.sort_a_z:
                menuItem = menu.findItem(id);
                menuItem.setChecked(true);
                mTaskArrayList = mRealmControl.sortAZ(mRealmControl.fromRealm());
                break;
            case R.id.sort_z_a:
                menuItem = menu.findItem(id);
                menuItem.setChecked(true);
                mTaskArrayList = mRealmControl.sortZA(mRealmControl.fromRealm());
                break;
            case R.id.sort_time_start_end:
                menuItem = menu.findItem(id);
                menuItem.setChecked(true);
                mTaskArrayList = mRealmControl.sortStartToEnd(mRealmControl.fromRealm());
                break;
            case R.id.sort_time_end_start:
                menuItem = menu.findItem(id);
                menuItem.setChecked(true);
                mTaskArrayList = mRealmControl.sortEndToStart(mRealmControl.fromRealm());
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
                mTaskArrayList = mRealmControl.sortAZ(mTaskArrayList);
                sSwipeRecyclerViewAdapter.notifyDataSetChanged();
                return true;

            case R.id.sort_z_a:
                saveMenuItemChecked(item);
//                Collections.sort(mTaskArrayList, Task.DescendingTaskTimeComparator);
//                updateToRealm(mTaskArrayList);
                mTaskArrayList = mRealmControl.sortZA(mTaskArrayList);
                sSwipeRecyclerViewAdapter.notifyDataSetChanged();
                return true;

            case R.id.sort_time_start_end:
                saveMenuItemChecked(item);
//                Collections.sort(mTaskArrayList, Task.AscendingTaskTimeComparator);
//                updateToRealm(mTaskArrayList);
                mTaskArrayList = mRealmControl.sortStartToEnd(mTaskArrayList);
                updateToRealm(mTaskArrayList);
                return true;

            case R.id.sort_time_end_start:
                saveMenuItemChecked(item);
//                Collections.sort(mTaskArrayList, Task.DescendingTaskTimeComparator);
//                updateToRealm(mTaskArrayList);
                mTaskArrayList = mRealmControl.sortEndToStart(mTaskArrayList);
                updateToRealm(mTaskArrayList);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveMenuItemChecked(MenuItem item) {
        item.setChecked(true);
        mRealmControl.saveMenuItemChecked(item.getItemId());
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
        mRealmControl.deleteAllRealm();
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
