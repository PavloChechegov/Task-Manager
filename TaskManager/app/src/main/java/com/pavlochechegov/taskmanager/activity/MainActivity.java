package com.pavlochechegov.taskmanager.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.model.Task;
import com.pavlochechegov.taskmanager.adapter.TaskBaseAdapter;
import com.pavlochechegov.taskmanager.utils.SaveTask;

import java.util.ArrayList;

import static com.pavlochechegov.taskmanager.activity.TaskActivity.KEY_TASK_EXTRA;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_SAVE_STATE = "save_instance_state";
    public static final String KEY_ITEM_LONG_CLICK = "long_click_item";
    public static final String KEY_ITEM_POSITION = "item_position";
    private ListView mTaskListView;
    private ArrayList<Task> mTaskArrayList;
    private TaskBaseAdapter mTaskBaseAdapter;
    private Task mTask;
    private long mTaskTimeStart, mTaskTimeEnd;
    private int mIntItemPosition;
    private SaveTask mSaveTask;
    CoordinatorLayout coordinatorLayout;
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
        initUI();
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
                default:
                    break;
            }
        }
        saveArrayList(mTaskArrayList);
    }


    // TODO: initialize all widget on screen
    private void initUI() {
        mTaskListView = (ListView) findViewById(R.id.listViewTask);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddTask);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTaskArrayList = mSaveTask.loadData();
                mTaskBaseAdapter = new TaskBaseAdapter(getBaseContext(), mTaskArrayList);
                mTaskListView.setAdapter(mTaskBaseAdapter);
            }
        }, 500);

        //change items in ListView
        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Task task = mTaskBaseAdapter.getItem(position);

                if (task.getTaskStartTime() == 0) {

                    mTaskTimeStart = System.currentTimeMillis();
                    task.setTaskStartTime(mTaskTimeStart);
                    task.setTaskColor(R.color.start_task_color);
                    mTaskArrayList.set(position, task);
                    Snackbar.make(view, R.string.task_started, Snackbar.LENGTH_SHORT).show();

                } else if (task.getTaskEndTime() == 0) {

                    mTaskTimeEnd = System.currentTimeMillis();
                    task.setTaskEndTime(mTaskTimeEnd);
                    task.setTaskColor(R.color.finish_task_color);
                    mTaskArrayList.set(position, task);
                    Snackbar.make(view, R.string.task_finished, Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(R.color.start_task_color))
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    task.setTaskEndTime(0);
                                    task.setTaskColor(R.color.start_task_color);
                                    mTaskArrayList.set(position, task);
                                    mTaskBaseAdapter.notifyDataSetChanged();
                                }
                            }).show();
                }
                saveArrayList(mTaskArrayList);
            }
        });

        mTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent longClickIntent = new Intent(MainActivity.this, TaskActivity.class);
                longClickIntent.putExtra(KEY_ITEM_LONG_CLICK, mTaskBaseAdapter.getItem(position));
                longClickIntent.putExtra(KEY_ITEM_POSITION, position);
                startActivityForResult(longClickIntent, 2);
                return true;
            }
        });
    }

    public void saveArrayList(ArrayList<Task> taskArrayList) {
        mSaveTask.saveData(taskArrayList);
        mTaskBaseAdapter.notifyDataSetChanged();
    }


    //TODO: go to TaskActivity and create Task object
    public void addTaskToListView(View v) {
        Intent intent = new Intent(this, TaskActivity.class);
        startActivityForResult(intent, 1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //add button on toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                mTaskArrayList.add(0, new Task(getString(R.string.task) + 1, getString(R.string.comment) + 1, 0, 0, R.color.default_task_color));
                addThreeScreenTasks();
                return true;
            case R.id.action_delete_elements:
                mTaskArrayList.clear();
                mSaveTask.clearData();
                mTaskBaseAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //add 3 screen item in listView
    public void addThreeScreenTasks() {
        float o = getHeightListViewItem(mTaskListView, mTaskBaseAdapter);
        float y = mTaskListView.getHeight();
        int k = ((int) (y / o)) * 3;
        for (int i = 2; i <= k; i++) {
            mTaskArrayList.add(0, new Task(getString(R.string.task) + i, getString(R.string.comment) + i, 0, 0, R.color.default_task_color));
        }
        Snackbar.make(coordinatorLayout, R.string.new_task_added, Snackbar.LENGTH_SHORT).show();
        saveArrayList(mTaskArrayList);
    }

    //total height of 1 element of listvew
    private int getHeightListViewItem(ListView listView, TaskBaseAdapter taskBaseAdapter) {
        View mView = taskBaseAdapter.getView(0, null, listView);
        mView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int listViewElementsHeight = mView.getMeasuredHeight();
        ;
        return listViewElementsHeight;
    }
}
