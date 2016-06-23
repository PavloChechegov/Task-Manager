package com.pavlochechegov.taskmanager.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.afollestad.materialdialogs.color.CircleView;
import com.pavlochechegov.taskmanager.adapter.SwipeRecyclerViewAdapter;
import com.pavlochechegov.taskmanager.fragment.DeleteDialogFragment;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.model.Task;
import com.pavlochechegov.taskmanager.utils.ManagerControlTask;

import java.util.*;

import static com.pavlochechegov.taskmanager.activities.TaskActivity.KEY_TASK_EXTRA;

public class MainActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteAllItem,
        SwipeRecyclerViewAdapter.ClickListener {

    public static final String KEY_SAVE_STATE = "save_instance_state";
    public static final String KEY_ITEM_LONG_CLICK = "long_click_item";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final String PREFERENCE_KEY_DEFAULT_COLOR = "preference_key_default_color";
    public static final String PREFERENCE_KEY_START_TASK_COLOR = "preference_key_start_task_color";
    public static final String PREFERENCE_KEY_END_TASK_COLOR = "preference_key_end_task_color";
    public static final String TAG_ALERT_DIALOG = "alert_dialog";
    public static final int REQUEST_CODE_ADD_TASK = 1;
    public static final int REQUEST_CODE_CHANGE_TASK = 2;
    public static final int REQUEST_CODE_SETTING = 3;
    private RecyclerView mRecyclerView;
    private ArrayList<Task> mTaskArrayList;
    private SwipeRecyclerViewAdapter mSwipeRecyclerViewAdapter;
    private Task mTask;
    private long mTaskTimeStart, mTaskTimeEnd;
    private int mIntItemPosition;
    private ManagerControlTask mManagerControlTask;
    private CoordinatorLayout coordinatorLayout;
    private Random mRandom;
    private int defaultTaskColor, startColor, endColor;
    private SharedPreferences mDefaultSetting;
    private boolean doubleBackToExitPressedOnce = false;
    private int mColor;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mManagerControlTask = new ManagerControlTask(MainActivity.this);

        if (savedInstanceState != null) {
            mTaskArrayList = savedInstanceState.getParcelableArrayList(KEY_SAVE_STATE);
        } else {
            mTaskArrayList = new ArrayList<>();
        }
        mManagerControlTask.initTaskItemColor();
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initTheme();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_SAVE_STATE, mTaskArrayList);
        if (!mTaskArrayList.isEmpty()) {
            mManagerControlTask.saveArrayList(mTaskArrayList);
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
                    mManagerControlTask.updateTaskColor(mTaskArrayList);
                    break;
                default:
                    break;
            }
        }
        saveSortedTask(mTaskArrayList);
    }

    public void initTheme (){
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
        //initTheme();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        //loading ArrayList<Task> from Sharedpref and showing in ListView
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTaskArrayList = mManagerControlTask.loadArrayList();
//                mManagerControlTask.updateTaskColor(mTaskArrayList);
                mSwipeRecyclerViewAdapter = new SwipeRecyclerViewAdapter(MainActivity.this, mTaskArrayList);
                mRecyclerView.setAdapter(mSwipeRecyclerViewAdapter);
            }
        }, 500);

        //create random number for Task and Comment
        mRandom = new Random();
//
//        mRecyclerView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent longClickIntent = new Intent(MainActivity.this, TaskActivity.class);
//                longClickIntent.putExtra(KEY_ITEM_LONG_CLICK, mSwipeRecyclerViewAdapter.getItem(position));
//                longClickIntent.putExtra(KEY_ITEM_POSITION, position);
//                startActivityForResult(longClickIntent, REQUEST_CODE_CHANGE_TASK);
//                return true;
//            }
//        });
//        startActivity;
    }

    // save ArrayList to SharedPreference
    private void saveSortedTask(ArrayList<Task> taskArrayList) {
        mManagerControlTask.saveArrayList(taskArrayList);
        mSwipeRecyclerViewAdapter.notifyDataSetChanged();
    }


    //TODO: go to TaskActivity and create Task object
    public void addTaskToListView(View v) {
        Intent intent = new Intent(this, TaskActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_TASK);
    }

    public void editTask(View v, int position){
        Intent longClickIntent = new Intent(MainActivity.this, TaskActivity.class);
        longClickIntent.putExtra(KEY_ITEM_LONG_CLICK, mSwipeRecyclerViewAdapter.getItem(position));
        longClickIntent.putExtra(KEY_ITEM_POSITION, position);
        startActivityForResult(longClickIntent, REQUEST_CODE_CHANGE_TASK);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem;
        int id = mManagerControlTask.loadMenuItemChecked();

//        restore checkbox in Sort item menu
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
        mManagerControlTask.saveMenuItemChecked(item.getItemId());
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
//        float o = getHeightListViewItem(mRecyclerView, mSwipeRecyclerViewAdapter);
//        float y = mRecyclerView.getHeight();
//        int k = ((int) (y / o)) * 3;
//        for (int i = 2; i <= k; i++) {
//            mTaskArrayList.add(0, generateRandomTask());
//        }
//        Snackbar.make(coordinatorLayout, R.string.new_task_added, Snackbar.LENGTH_SHORT).show();
//        saveSortedTask(mTaskArrayList);
    }

    //total height of 1 element of listView
//    private int getHeightListViewItem(ListView listView, SwipeRecyclerViewAdapter taskBaseAdapter) {
//        View mView = taskBaseAdapter.getView(0, null, listView);
//        mView.measure(
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        int listViewElementsHeight = mView.getMeasuredHeight();
//        return listViewElementsHeight;
//    }

    //delete ArrayList and SharedPreference data
    @Override
    public void delete() {
        mTaskArrayList.clear();
        mManagerControlTask.clearDataInSharedPreferences();
        mSwipeRecyclerViewAdapter.notifyDataSetChanged();
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
