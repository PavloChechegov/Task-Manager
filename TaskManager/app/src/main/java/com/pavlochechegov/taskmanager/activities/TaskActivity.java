package com.pavlochechegov.taskmanager.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.model.Task;

import static com.pavlochechegov.taskmanager.activities.MainActivity.KEY_ITEM_LONG_CLICK;
import static com.pavlochechegov.taskmanager.activities.MainActivity.KEY_ITEM_POSITION;

public class TaskActivity extends AppCompatActivity {

    public static final String KEY_TASK_EXTRA = "key_task_extra";
    private EditText mEditTextTaskTitle, mEditTextTaskComment;
    private Button mButtonTaskCancel, mButtonTaskSave;
    private Task mTask;
    protected int positionOfItem;
    private CoordinatorLayout mCoordinatorLayout;
    private int defaultColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        initUI();

        Intent intent = getIntent();
        if (intent.hasExtra(KEY_ITEM_LONG_CLICK)){
            mTask = intent.getParcelableExtra(KEY_ITEM_LONG_CLICK);
            positionOfItem = intent.getIntExtra(KEY_ITEM_POSITION, DEFAULT_KEYS_DIALER);
            mEditTextTaskTitle.setText(mTask.getTaskTitle());
            mEditTextTaskComment.setText(mTask.getTaskComment());
        }

        SharedPreferences mDefaultSetting = PreferenceManager.getDefaultSharedPreferences(this);
        defaultColor = mDefaultSetting.getInt("preference_key_default_color", 0);
    }

    // TODO: initialize all widget on screen
    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.CoordLayoutActivityTask);
        mEditTextTaskTitle = (EditText) findViewById(R.id.etTaskTitle);
        mEditTextTaskComment = (EditText) findViewById(R.id.etTaskComment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    // TODO: exit from TaskActivity and return to MainActivity without saving Task object
    public void cancelTask(View view){
        finish();
    }

    // TODO: create Task object and send to MainActivity
    public void createTask(View view){

        if (mEditTextTaskTitle.getText().toString().isEmpty() || mEditTextTaskComment.getText().toString().isEmpty()) {
            Snackbar.make(mCoordinatorLayout, R.string.empty_fields, Snackbar.LENGTH_SHORT).show();
        } else {
            mTask = new Task(mEditTextTaskTitle.getText().toString(),
                    mEditTextTaskComment.getText().toString(), 0, 0, defaultColor);
            Intent intent = new Intent();
            intent.putExtra(KEY_TASK_EXTRA, mTask);
            intent.putExtra(KEY_ITEM_POSITION, positionOfItem);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save_task:createTask(item.getActionView());
                return true;
            case R.id.action_cancel: finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_activity_menu, menu);
        return true;
    }
}
