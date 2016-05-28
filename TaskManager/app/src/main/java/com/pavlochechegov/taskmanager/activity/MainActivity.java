package com.pavlochechegov.taskmanager.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.task.Task;
import com.pavlochechegov.taskmanager.adapter.TaskAdapter;

import java.util.ArrayList;

import static com.pavlochechegov.taskmanager.activity.TaskActivity.KEY_TASK_EXTRA;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_SAVE_STATE = "save_instance_state";
    private ListView mTaskListView;
    private ArrayList<Task> mTaskArrayList;
    private TaskAdapter mTaskAdapter;
    private Task mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            mTaskArrayList = savedInstanceState.getParcelableArrayList(KEY_SAVE_STATE);
        } else {
            mTaskArrayList = new ArrayList<>();
        }
        initUI();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTaskArrayList = savedInstanceState.getParcelableArrayList(KEY_SAVE_STATE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_SAVE_STATE, mTaskArrayList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            mTask = data.getParcelableExtra(KEY_TASK_EXTRA);
            mTaskArrayList.add(mTask);
        }
        initUI();
    }

    // TODO: initialize all widget on screen
    private void initUI(){
        mTaskListView = (ListView) findViewById(R.id.listViewTask);
        mTaskAdapter = new TaskAdapter(this, mTaskArrayList);
        mTaskListView.setAdapter(mTaskAdapter);
    }


    //TODO: go to TaskActivity and create Task object
    public void addTaskToListView(View v){
        Intent intent = new Intent(this, TaskActivity.class);
        startActivityForResult(intent, 1);
    }
}
