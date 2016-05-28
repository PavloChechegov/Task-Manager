package com.pavlochechegov.taskmanager.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.task.Task;

public class TaskActivity extends AppCompatActivity {

    public static final String KEY_TASK_EXTRA = "key_task_extra";
    private EditText mEditTextTaskTitle, mEditTextTaskComment;
    //private Button mButtonTaskCancel, mButtonTaskSave;
    private Task mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        initUI();
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

        mTask = new Task();

        if (mEditTextTaskTitle.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Введіть будь ласка назву завдання", Toast.LENGTH_SHORT).show();
        } else if(mEditTextTaskComment.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Введіть опис коментар до завдання", Toast.LENGTH_SHORT).show();
        } else {
            mTask.setTaskTitle(mEditTextTaskTitle.getText().toString());
            mTask.setTaskComment(mEditTextTaskComment.getText().toString());
            Intent intent = new Intent();
            intent.putExtra(KEY_TASK_EXTRA, mTask);
            setResult(RESULT_OK, intent);
            finish();
        }


    }
}
