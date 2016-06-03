package com.pavlochechegov.taskmanager.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.TaskJSON;
import com.pavlochechegov.taskmanager.model.Task;
import com.pavlochechegov.taskmanager.adapter.TaskAdapter;
import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import static com.pavlochechegov.taskmanager.activity.TaskActivity.KEY_TASK_EXTRA;

public class MainActivity extends AppCompatActivity {

    public static final String FILENAME = "tasks.json";
    public static final String KEY_SAVE_STATE = "save_instance_state";
    public static final String KEY_ITEM_LONG_CLICK = "long_click_item";
    public static final String KEY_ITEM_POSITION = "item_position";
    private ListView mTaskListView;
    private ArrayList<Task> mTaskArrayList;
    private TaskAdapter mTaskAdapter;
    private Task mTask;
    private long mTaskTimeStart, mTaskTimeEnd, mTaskTimeDifference;
    private int mIntItemPosition;
    private TaskJSON mTaskJSON;
    DateFormat mDFTaskTime, mDFDifferenceTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mTaskArrayList = savedInstanceState.getParcelableArrayList(KEY_SAVE_STATE);
        } else if(mTaskJSON != null){
            // loading data(JsonObject ArrayList) from phone
            try {
                mTaskArrayList = mTaskJSON.loadTask();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mTaskArrayList = new ArrayList<>();
        }
        initUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_SAVE_STATE, mTaskArrayList);
        saveToJSONfile(mTaskArrayList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(data != null) {
            switch (requestCode) {
                case 1:
                    mTask = data.getParcelableExtra(KEY_TASK_EXTRA);
                    mTaskArrayList.add(0, mTask);
                    saveToJSONfile(mTaskArrayList);
                    mTaskAdapter.notifyDataSetChanged();
                    break;

                case 2:
                    mTask = data.getParcelableExtra(KEY_TASK_EXTRA);
                    mIntItemPosition = data.getIntExtra(KEY_ITEM_POSITION, DEFAULT_KEYS_DIALER);
                    mTaskArrayList.set(mIntItemPosition, mTask);
                    saveToJSONfile(mTaskArrayList);
                    mTaskAdapter.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
        }

        initUI();
    }

    // TODO: initialize all widget on screen
    private void initUI() {

        if (mTaskJSON == null) {
            mTaskJSON = new TaskJSON(MainActivity.this, FILENAME);
        }

        mDFTaskTime = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        mDFDifferenceTime = new SimpleDateFormat("HH:mm:ss");
        mDFDifferenceTime.setTimeZone(TimeZone.getTimeZone("GMT"));


        mTaskListView = (ListView) findViewById(R.id.listViewTask);
        mTaskAdapter = new TaskAdapter(this, mTaskArrayList);
        mTaskListView.setAdapter(mTaskAdapter);

        //change items in ListView
        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = mTaskAdapter.getItem(position);

                if (task.getTaskStartTime().isEmpty()) {

                    mTaskTimeStart = System.currentTimeMillis();
                    task.setTaskStartTime(mDFTaskTime.format(mTaskTimeStart));
                    Toast.makeText(getApplicationContext(), "Task added", Toast.LENGTH_SHORT).show();

                    mTaskAdapter.notifyDataSetChanged();


                } else if (task.getTaskEndTime().isEmpty()) {

                    mTaskTimeEnd = System.currentTimeMillis();
                    mTaskTimeDifference = mTaskTimeEnd - mTaskTimeStart;
                    task.setTaskEndTime(" - "
                            + mDFTaskTime.format(System.currentTimeMillis()) + ": "
                            + mDFDifferenceTime.format(mTaskTimeDifference));

                    mTaskAdapter.notifyDataSetChanged();

                }

            }
        });

        mTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Intent longClickIntent = new Intent(MainActivity.this, TaskActivity.class);
                longClickIntent.putExtra(KEY_ITEM_LONG_CLICK, mTaskAdapter.getItem(position));
                longClickIntent.putExtra(KEY_ITEM_POSITION, position);
                startActivityForResult(longClickIntent, 2);

                return true;
            }
        });
    }


    //TODO: go to TaskActivity and create Task object
    public void addTaskToListView(View v) {
        Intent intent = new Intent(this, TaskActivity.class);
        startActivityForResult(intent, 1);
    }

    public void saveToJSONfile(ArrayList<Task> tasks){
        try {
            mTaskJSON.saveTask(tasks);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
