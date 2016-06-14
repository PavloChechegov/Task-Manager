package com.pavlochechegov.taskmanager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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

public class MainActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteAllItem{

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
    private Random mRandom;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;

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

        //create Dialog window for deleting all task from ArrayList<Task> and memory
        mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Delete tasks")
                .setMessage("Do you want to delete all tasks?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(coordinatorLayout, "Tasks were deleted", Snackbar.LENGTH_SHORT).show();
                        mTaskArrayList.clear();
                        mSaveTask.clearData();
                        mTaskBaseAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        mDialog = mBuilder.create();

        mTaskListView = (ListView) findViewById(R.id.listViewTask);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddTask);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        //loading ArrayList<Task> from memory and showing in ListView
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTaskArrayList = mSaveTask.loadData();
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

    private void saveArrayList(ArrayList<Task> taskArrayList) {
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
        item.setChecked(!item.isChecked());
        switch (item.getItemId()) {
            case R.id.action_add:
                addTaskToListView(item.getActionView());
                return true;

            case R.id.action_exit:
                finish();
                return true;

            //generate random Task and Comment
            case R.id.action_generate_tasks:
                mTaskArrayList.add(0, new Task(
                        getString(R.string.task) + generateRandomInt(),
                        getString(R.string.comment) + generateRandomInt(),
                        0,
                        0,
                        R.color.default_task_color));
                addThreeScreenTasks();
                return true;

            //delete all elements from ArrayList<Task> and memory
            case R.id.action_delete_elements:
                if(!mTaskArrayList.isEmpty()){
                    new DeleteDialogFragment().show(getSupportFragmentManager(), "tag");
                }else {
                    Snackbar.make(coordinatorLayout, "List is empty now, please add task", Snackbar.LENGTH_SHORT).show();
                }
                return true;

            //go to SettingsActivity
            case R.id.action_setting:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            //sorting item in ListView
            case R.id.sort_item:
                item.setChecked(!item.isChecked());
                return true;

            case R.id.sort_a_z:
                Collections.sort(mTaskArrayList, new Comparator<Task>() {
                    @Override
                    public int compare(Task taskTitle1, Task taskTitle2) {
                        return taskTitle1.getTaskTitle().compareToIgnoreCase(taskTitle2.getTaskTitle());
                    }
                });
                saveArrayList(mTaskArrayList);
                return true;

            case R.id.sort_z_a:
                Collections.sort(mTaskArrayList, new Comparator<Task>() {
                    @Override
                    public int compare(Task taskTitle1, Task taskTitle2) {
                        return taskTitle2.getTaskTitle().compareToIgnoreCase(taskTitle1.getTaskTitle());
                    }
                });
                saveArrayList(mTaskArrayList);
                return true;

            case R.id.sort_time_start_end:
                Collections.sort(mTaskArrayList, new Comparator<Task>() {
                    @Override
                    public int compare(Task taskTime1, Task taskTime2) {
                        return (int) (taskTime1.getTaskStartTime() - taskTime2.getTaskStartTime());
                    }
                });
                saveArrayList(mTaskArrayList);
                return true;

            case R.id.sort_time_end_start:
                Collections.sort(mTaskArrayList, new Comparator<Task>() {
                    @Override
                    public int compare(Task taskTime1, Task taskTime2) {
                        return (int) (taskTime2.getTaskStartTime() - taskTime1.getTaskStartTime());
                    }
                });
                saveArrayList(mTaskArrayList);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //generate Random int to Title and Comment fields
    public int generateRandomInt() {
        return mRandom.nextInt(150);
    }

    //add 3 screen item in listView
    public void addThreeScreenTasks() {
        float o = getHeightListViewItem(mTaskListView, mTaskBaseAdapter);
        float y = mTaskListView.getHeight();
        int k = ((int) (y / o)) * 3;
        for (int i = 2; i <= k; i++) {
            mTaskArrayList.add(0, new Task(
                    getString(R.string.task) + generateRandomInt(),
                    getString(R.string.comment) + generateRandomInt(),
                    0,
                    0,
                    R.color.default_task_color));
        }
        Snackbar.make(coordinatorLayout, R.string.new_task_added, Snackbar.LENGTH_SHORT).show();
        saveArrayList(mTaskArrayList);
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

    @Override
    public void delete() {
        mTaskArrayList.clear();
        mSaveTask.clearData();
        mTaskBaseAdapter.notifyDataSetChanged();
    }
}
