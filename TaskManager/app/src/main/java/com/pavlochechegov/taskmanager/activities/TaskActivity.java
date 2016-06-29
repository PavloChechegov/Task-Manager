package com.pavlochechegov.taskmanager.activities;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.model.Task;
import com.pavlochechegov.taskmanager.model.TaskColors;
import com.pavlochechegov.taskmanager.utils.ManagerControlTask;

import java.util.UUID;

import static com.pavlochechegov.taskmanager.activities.MainActivity.KEY_ITEM_EDIT_TASK;
import static com.pavlochechegov.taskmanager.activities.MainActivity.KEY_ITEM_POSITION;

public class TaskActivity extends AppCompatActivity {

    public static final String KEY_TASK_EXTRA = "key_task_extra";
    private static final int REQUEST_CODE_VOICE = 4;
    private EditText mEditTextTaskTitle, mEditTextTaskComment;
    private Task mTask;
    protected int positionOfItem;
    private CoordinatorLayout mCoordinatorLayout;
    private ManagerControlTask managerControlTask;
    private TaskColors taskColors;
    private TextInputLayout mInputLayoutTitle, mInputLayoutComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        initUI();
        managerControlTask = ManagerControlTask.getSingletonControl(this);
        taskColors = managerControlTask.initTaskItemColor();

        Intent intent = getIntent();
        if (intent.hasExtra(KEY_ITEM_EDIT_TASK)) {
            mTask = intent.getParcelableExtra(KEY_ITEM_EDIT_TASK);
            positionOfItem = intent.getIntExtra(KEY_ITEM_POSITION, DEFAULT_KEYS_DIALER);
            mEditTextTaskTitle.setText(mTask.getTaskTitle());
            mEditTextTaskComment.setText(mTask.getTaskComment());

        }
    }

    // TODO: initialize all widget on screen
    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.CoordLayoutActivityTask);
        mEditTextTaskTitle = (EditText) findViewById(R.id.edit_txt_task_title);
        mEditTextTaskComment = (EditText) findViewById(R.id.edit_txt_task_comment);
        mInputLayoutTitle = (TextInputLayout) findViewById(R.id.txt_input_layout_title);
        mInputLayoutComment = (TextInputLayout) findViewById(R.id.txt_input_layout_comment);
        mEditTextTaskTitle.addTextChangedListener(new MyTextWatcher(mEditTextTaskTitle));
        mEditTextTaskComment.addTextChangedListener(new MyTextWatcher(mEditTextTaskComment));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }

    // TODO: exit from TaskActivity and return to MainActivity without saving Task object
    public void cancelTask(View view) {
        finish();
    }

    // TODO: create Task object and send to MainActivity
    public void createTask(View view) {
        Intent intent = new Intent();

        if (mTask == null) {
            if (validateTitle() && validateComment()) {
                mTask = new Task(UUID.randomUUID().toString(), mEditTextTaskTitle.getText().toString(),
                        mEditTextTaskComment.getText().toString(), 0, 0, taskColors.getDefaultColor());

                intent.putExtra(KEY_TASK_EXTRA, mTask);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Snackbar.make(mCoordinatorLayout, R.string.empty_fields, Snackbar.LENGTH_SHORT).show();
            }

        } else {
            if (validateTitle() && validateComment()) {
                mTask.setTaskTitle(mEditTextTaskTitle.getText().toString());
                mTask.setTaskComment(mEditTextTaskComment.getText().toString());
                intent.putExtra(KEY_TASK_EXTRA, mTask);
                intent.putExtra(KEY_ITEM_POSITION, positionOfItem);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Snackbar.make(mCoordinatorLayout, R.string.empty_fields, Snackbar.LENGTH_SHORT).show();
            }
        }

    }


    private boolean validateTitle() {
        mInputLayoutComment.setError(null);
        if (mEditTextTaskTitle.getText().toString().length() <= 5) {
            mInputLayoutTitle.setError(getString(R.string.err_msg_title));
            mInputLayoutTitle.setErrorEnabled(true);
            requestFocus(mEditTextTaskTitle);

            return false;
        } else {
            mInputLayoutTitle.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateComment() {
        mInputLayoutComment.setError(null);
        if (mEditTextTaskComment.getText().toString().isEmpty()) {
            mInputLayoutComment.setError(getString(R.string.err_msg_comment));
            mInputLayoutComment.setErrorEnabled(true);
            requestFocus(mEditTextTaskComment);

            return false;
        } else {
            mInputLayoutComment.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edit_txt_task_title:
                    validateTitle();
                    break;
                case R.id.edit_txt_task_comment:
                    validateComment();
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_task:
                createTask(item.getActionView());
                return true;
            case R.id.action_cancel:
                finish();
                return true;
            case R.id.action_voice:
                createTaskWithVoice();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createTaskWithVoice() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        startActivityForResult(intent, REQUEST_CODE_VOICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_activity_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_VOICE && resultCode == RESULT_OK) {
            if (mEditTextTaskTitle.isFocused()) {
                mEditTextTaskTitle.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
            } else {
                mEditTextTaskComment.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
