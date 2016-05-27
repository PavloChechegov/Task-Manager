package com.pavlochechegov.taskmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView mTaskListView;
    Button mButton;
    ArrayList<Task> mTaskArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

    }

    public void initUI(){
        mTaskListView = (ListView) findViewById(R.id.listViewTask);
        mButton = (Button) findViewById(R.id.btnAddTask);
        mTaskArrayList = new ArrayList<>();
    }

    public void addTask(){
        Intent intent = new Intent(this, TaskActivity.class);
        startActivityForResult(intent, 1);
    }
}
