package com.pavlochechegov.taskmanager.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
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
import android.widget.Toast;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.fragment.ChooseAvatarDialog;
import com.pavlochechegov.taskmanager.model.Task;
import com.pavlochechegov.taskmanager.model.TaskColors;
import com.pavlochechegov.taskmanager.utils.AvatarSaver;
import com.pavlochechegov.taskmanager.utils.RealmControl;
import com.soundcloud.android.crop.Crop;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.pavlochechegov.taskmanager.activities.MainActivity.KEY_ITEM_EDIT_TASK;
import static com.pavlochechegov.taskmanager.activities.MainActivity.KEY_ITEM_POSITION;

public class TaskActivity extends AppCompatActivity implements ChooseAvatarDialog.ChooseAvatar {

    public static final String KEY_TASK_EXTRA = "key_task_extra";
    private static final int REQUEST_CODE_VOICE = 4;
    private static final int REQUEST_GALLERY = 5;
    private static final int REQUEST_CAMERA = 6;

    private static final String TAG_AVATAR_CHOOSE_DIALOG = "avatar_chosen_dialog";

    private EditText mEditTextTaskTitle, mEditTextTaskComment;
    private Task mTask;
    protected int positionOfItem;
    private CoordinatorLayout mCoordinatorLayout;
    private RealmControl mRealmControl;
    private TaskColors taskColors;
    private TextInputLayout mInputLayoutTitle, mInputLayoutComment;
    private CircleImageView mAvatarImage;
    private AvatarSaver mAvatarSaver;
    private static Uri pathFile;
    private Bitmap mBitmapAvatar;
private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        initUI();
        mRealmControl = RealmControl.getSingletonControl(this);
        taskColors = mRealmControl.initTaskItemColor();
        mAvatarSaver = new AvatarSaver(this);

        Intent intent = getIntent();
        if (intent.hasExtra(KEY_ITEM_EDIT_TASK)) {
            mTask = intent.getParcelableExtra(KEY_ITEM_EDIT_TASK);
            positionOfItem = intent.getIntExtra(KEY_ITEM_POSITION, DEFAULT_KEYS_DIALER);
            mEditTextTaskTitle.setText(mTask.getTaskTitle());
            mEditTextTaskComment.setText(mTask.getTaskComment());
            if (loadAvatar(mTask.getId()) != null){
                mAvatarImage.setImageBitmap(loadAvatar(mTask.getId()));
            }

        } else {
            mTask = new Task();
            mTask.setId(UUID.randomUUID().toString());
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
        mAvatarImage = (CircleImageView) findViewById(R.id.imageViewAvatar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }

    // TODO: exit from TaskActivity and return to MainActivity without saving Task object
    public void cancelTask(View view) {
        finish();
    }


    // TODO: create Task object and send to MainActivity
    public void createTask(View view) {
        Intent intent = getIntent();

        if (intent.hasExtra(KEY_ITEM_POSITION)) {

            //edit task
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

        } else {

            //create new task
            if (validateTitle() && validateComment()) {
                mTask.setTaskTitle(mEditTextTaskTitle.getText().toString());
                mTask.setTaskComment(mEditTextTaskComment.getText().toString());
                mTask.setTaskColor(taskColors.getDefaultColor());
                intent.putExtra(KEY_TASK_EXTRA, mTask);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Snackbar.make(mCoordinatorLayout, R.string.empty_fields, Snackbar.LENGTH_SHORT).show();
            }
        }

    }

    public void chooseAvatar(View view) {
        new ChooseAvatarDialog().show(getSupportFragmentManager(), TAG_AVATAR_CHOOSE_DIALOG);
    }


    private boolean validateTitle() {
        mInputLayoutTitle.setError(null);
        if (mEditTextTaskTitle.getText().toString().trim().length() <= 3) {
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
        if (mEditTextTaskComment.getText().toString().trim().isEmpty()) {
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

    @Override
    public void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_VOICE:
                    if (mEditTextTaskTitle.isFocused()) {
                        mEditTextTaskTitle.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                        break;
                    } else {
                        mEditTextTaskComment.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                    }
                    break;

                case REQUEST_GALLERY:
                    Uri uri = data.getData();
                    Uri destination = Uri.fromFile(new File(getCacheDir(), "name"));
                    Crop.of(uri, destination).asSquare().start(this);
                    mUri = destination;
                    break;

                case REQUEST_CAMERA:
                    mBitmapAvatar = (Bitmap) data.getExtras().get("data");
                    avatarUpdate(mBitmapAvatar);
                    break;

                case Crop.REQUEST_CROP:

                    try {
                        mBitmapAvatar = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mUri);
                        avatarUpdate(mBitmapAvatar);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                default:
                    break;
            }

        } else Toast.makeText(getApplicationContext(), "Please pick photo", Toast.LENGTH_SHORT).show();
    }

    private void avatarUpdate(Bitmap bitmap) {
        try {
            saveAvatar(bitmap, mTask.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mAvatarImage.setImageBitmap(loadAvatar(mTask.getId()));
    }


    public Bitmap loadAvatar(String uuidName) {
        File myFile = new File(getFilesDir(), "/avatar/");
        return mAvatarSaver
                .setDirectoryName(myFile)
                .setFileName(uuidName + "avatar.png")
                .load();
    }

    public void saveAvatar(Bitmap bitmap, String uuidName) throws IOException {
        File myFile = new File(getFilesDir(), "/avatar/");
        int width = mAvatarImage.getWidth();
        int height = mAvatarImage.getHeight();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        mAvatarSaver
                .setDirectoryName(myFile)
                .setFileName(uuidName + "avatar.png")
                .save(resizedBitmap);
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
                cancelTask(item.getActionView());
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
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition");
        startActivityForResult(intent, REQUEST_CODE_VOICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_activity_menu, menu);
        return true;
    }
}
