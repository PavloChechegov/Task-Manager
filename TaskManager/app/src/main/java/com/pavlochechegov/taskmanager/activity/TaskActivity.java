package com.pavlochechegov.taskmanager.activity;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.model.Task;

import static com.pavlochechegov.taskmanager.activity.MainActivity.KEY_ITEM_LONG_CLICK;
import static com.pavlochechegov.taskmanager.activity.MainActivity.KEY_ITEM_POSITION;

public class TaskActivity extends AppCompatActivity {

    public static final String KEY_TASK_EXTRA = "key_task_extra";
    private EditText mEditTextTaskTitle, mEditTextTaskComment;
    private Button mButtonTaskCancel, mButtonTaskSave;
    private Task mTask;
    protected int positionOfItem;

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
    }

    // TODO: initialize all widget on screen
    private void initUI() {
        mEditTextTaskTitle = (EditText) findViewById(R.id.etTaskTitle);
        mEditTextTaskComment = (EditText) findViewById(R.id.etTaskComment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    // TODO: exit from TaskActivity and return to MainActivity without saving Task object
    public void cancelTask(View view){
        finish();
    }

    // TODO: create Task object and send to MainActivity
    public void createTask(View view){

        if (mEditTextTaskTitle.getText().toString().isEmpty() || mEditTextTaskComment.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "You have empty fields", Toast.LENGTH_SHORT).show();
        } else {
            mTask = new Task(mEditTextTaskTitle.getText().toString(),
                    mEditTextTaskComment.getText().toString(), 0, 0, R.color.default_task_color);
            Intent intent = new Intent();
            intent.putExtra(KEY_TASK_EXTRA, mTask);
            intent.putExtra(KEY_ITEM_POSITION, positionOfItem);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
