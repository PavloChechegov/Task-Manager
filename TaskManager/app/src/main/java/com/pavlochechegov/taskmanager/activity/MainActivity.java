package com.pavlochechegov.taskmanager.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        mTaskJSON = new TaskJSON(this, FILENAME);

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
        if(!mTaskArrayList.isEmpty()){
            saveToJSONFile(mTaskArrayList);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            switch (requestCode) {
                case 1:
                    mTask = data.getParcelableExtra(KEY_TASK_EXTRA);
                    mTaskArrayList.add(0, mTask);
                    mTaskAdapter.notifyDataSetChanged();
                    break;

                case 2:
                    mTask = data.getParcelableExtra(KEY_TASK_EXTRA);
                    mIntItemPosition = data.getIntExtra(KEY_ITEM_POSITION, DEFAULT_KEYS_DIALER);
                    mTaskArrayList.set(mIntItemPosition, mTask);
                    mTaskAdapter.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
        }
        saveToJSONFile(mTaskArrayList);
        initUI();
    }

    // TODO: initialize all widget on screen
    private void initUI() {
        if(mTaskJSON == null){
            mTaskJSON = new TaskJSON(this, FILENAME);
        } else {
            try {
                mTaskArrayList = mTaskJSON.loadTask();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mTaskListView = (ListView) findViewById(R.id.listViewTask);
        mTaskAdapter = new TaskAdapter(this, mTaskArrayList, getResources());
        mTaskListView.setAdapter(mTaskAdapter);

        //change items in ListView
        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = mTaskAdapter.getItem(position);

                if (task.getTaskStartTime() == 0) {

                    mTaskTimeStart = System.currentTimeMillis();
                    task.setTaskStartTime(mTaskTimeStart);
                    task.setTaskColor(R.color.start_task_color);
                    mTaskArrayList.set(position, task);
                    saveToJSONFile(mTaskArrayList);
                    Toast.makeText(getApplicationContext(), "Task started", Toast.LENGTH_SHORT).show();
                    mTaskAdapter.notifyDataSetChanged();

                } else if (task.getTaskEndTime() == 0) {

                    mTaskTimeEnd = System.currentTimeMillis();
                    mTaskTimeDifference = mTaskTimeEnd - mTaskTimeStart;
                    task.setTaskEndTime(mTaskTimeEnd);
                    task.setTaskColor(R.color.finish_task_color);
                    mTaskArrayList.set(position, task);
                    saveToJSONFile(mTaskArrayList);
                    Toast.makeText(getApplicationContext(), "Task finished", Toast.LENGTH_SHORT).show();
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

    public void saveToJSONFile(ArrayList<Task> taskArrayList) {
        try {
            mTaskJSON.saveTask(taskArrayList);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            addThreeScreenTasks();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void addThreeScreenTasks() {

        float o = getTotalHeightListView(mTaskListView, mTaskAdapter);
        float y = mTaskListView.getHeight();
        int k = (int) ((y / o) - 1) * 3;
        Log.d("ADD", o + " " + y + " " + k);
        for (int i = 0; i <= k; i++) {
            mTaskArrayList.add(0, new Task("Task #" + i, "Comment #" + i, 0, 0, R.color.default_task_color));
            Log.d("ADD", i + "");
        }
        //saveToJSONFile(mTaskArrayList);
        mTaskAdapter.notifyDataSetChanged();
    }


    private int getTotalHeightListView(ListView lv, TaskAdapter mAdapter) {

        int listViewElementsHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, lv);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            listViewElementsHeight += mView.getMeasuredHeight();
        }
        return listViewElementsHeight;
    }
}
