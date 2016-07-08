package com.pavlochechegov.taskmanager.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.activities.TaskActivity;
import com.pavlochechegov.taskmanager.broadcast.FinishReceiver;
import com.pavlochechegov.taskmanager.model.Task;
import com.pavlochechegov.taskmanager.model.TaskColors;
import com.pavlochechegov.taskmanager.utils.AvatarSaver;
import com.pavlochechegov.taskmanager.utils.RealmControl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.pavlochechegov.taskmanager.activities.MainActivity.KEY_ITEM_EDIT_TASK;
import static com.pavlochechegov.taskmanager.activities.MainActivity.KEY_ITEM_POSITION;
import static com.pavlochechegov.taskmanager.activities.MainActivity.REQUEST_CODE_EDIT_TASK;

public class SwipeRecyclerViewAdapter extends RecyclerSwipeAdapter<SwipeRecyclerViewAdapter.TaskViewHolder> {
    private static final String KEY_ALARM_TIME = "pref_task_time_dialog";
    private static final String KEY_ACTION = "key_action";
    private static final String KEY_TITLE = "key_title";
    private Context mContext;
    private ArrayList<Task> mTaskArrayList;
    private DateFormat mDFTaskTime, mDFDifferenceTime;
    private long mTaskTimeStart, mTaskTimeEnd, mTaskTimePause, mTaskTimeDifference;
    private RealmControl mRealmControl;
    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmSender;
    private long mTimePreference;
    private TaskColors taskColors;
    private AvatarSaver mAvatarSaver;

    public SwipeRecyclerViewAdapter(Context context, ArrayList<Task> taskArrayList) {
        mContext = context;
        mTaskArrayList = taskArrayList;
        mDFTaskTime = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        mDFDifferenceTime = new SimpleDateFormat("HH:mm:ss");
        mDFDifferenceTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        mRealmControl = RealmControl.getSingletonControl(mContext);
        taskColors = mRealmControl.initTaskItemColor();
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mAvatarSaver = new AvatarSaver(mContext);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewTaskTitle, mTextViewTaskComment, mTextViewTaskTime;
        ImageButton mImageButtonRestoreTask, mButtonDeleteTask, mButtonEditTask;
        CircleImageView mImageButtonStartTask, mImageButtonStopTask, mCircleImageViewAvatar;
        SwipeLayout mSwipeLayout;
        Resources mResources;

        public TaskViewHolder(View itemView) {
            super(itemView);
            mSwipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            mImageButtonRestoreTask = (ImageButton) itemView.findViewById(R.id.imgBtnRestoreTask);
            mTextViewTaskTitle = (TextView) itemView.findViewById(R.id.tvTaskTitle);
            mTextViewTaskComment = (TextView) itemView.findViewById(R.id.tvTaskComment);
            mTextViewTaskTime = (TextView) itemView.findViewById(R.id.tvTaskTime);
            mButtonDeleteTask = (ImageButton) itemView.findViewById(R.id.btnDeleteTaskItem);
            mButtonEditTask = (ImageButton) itemView.findViewById(R.id.btnEditTaskItem);
            mImageButtonStartTask = (CircleImageView) itemView.findViewById(R.id.imageButtonPlay);
            mImageButtonStopTask = (CircleImageView) itemView.findViewById(R.id.imageButtonStop);
            mCircleImageViewAvatar = (CircleImageView) itemView.findViewById(R.id.avatarView);
            mResources = itemView.getResources();
        }
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, final int position) {

        Task task = getItem(position);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_item, parent, false);
        v.setBackgroundColor(task.getTaskColor());
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TaskViewHolder holder, final int position) {

        final Task task = getItem(position);
        holder.mTextViewTaskTitle.setText(task.getTaskTitle());
        holder.mTextViewTaskComment.setText(task.getTaskComment());
        holder.mTextViewTaskTime.setText(mDFTaskTime.format(task.getTaskStartTime()));

        holder.mSwipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
//        holder.mSwipeLayout.setBackgroundColor(task.getTaskColor());

        holder.itemView.setBackgroundColor(getItem(position).getTaskColor());
        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
        holder.mSwipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.mSwipeLayout.findViewById(R.id.swipeRightToLeft));
        holder.mSwipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.mSwipeLayout.findViewById(R.id.swipeLeftToRight));

        holder.mImageButtonStopTask.setColorFilter(holder.mResources.getColor(R.color.colorAccent));
        //image and color change in button start/pause
        if (!mTaskArrayList.get(position).isPause()) {
            holder.mImageButtonStartTask.setColorFilter(holder.mResources.getColor(R.color.colorPrimaryDark));
            holder.mImageButtonStartTask.setImageResource(R.drawable.ic_play_circle_filled_white_white_48dp);
        } else {
            holder.mImageButtonStartTask.setColorFilter(holder.mResources.getColor(R.color.button_pause_color));
            holder.mImageButtonStartTask.setImageResource(R.drawable.ic_pause_circle_filled_white_48dp);
        }

        try {
            holder.mCircleImageViewAvatar.setImageBitmap(
                    mAvatarSaver
                    .setDirectoryName(new File(mContext.getFilesDir() + "/avatar/"))
                    .setFileName(task.getId() + "avatar.png")
                    .load());
        } catch (Exception e){
            holder.mCircleImageViewAvatar.setImageResource(R.drawable.icon_logo_2);
            e.printStackTrace();
        }
        if (mAvatarSaver
                .setDirectoryName(new File(mContext.getFilesDir() + "/avatar/"))
                .setFileName(task.getId() + "avatar.png")
                .load() != null) {
            holder.mCircleImageViewAvatar.setImageBitmap(mAvatarSaver.load());

        } else holder.mCircleImageViewAvatar.setImageResource(R.drawable.icon_logo_2);

        // if timeStart of Task doesn't exist widget TextView is invisible
        if (mTaskArrayList.get(position).getTaskStartTime() == 0) {
            holder.mTextViewTaskTime.setVisibility(View.GONE);
        } else if (mTaskArrayList.get(position).getTaskEndTime() == 0) {
            holder.mTextViewTaskTime.setVisibility(View.VISIBLE);
            holder.mTextViewTaskTime.setText(mDFTaskTime.format(task.getTaskStartTime()));
        } else {
            holder.mTextViewTaskTime.setVisibility(View.VISIBLE);
            holder.mTextViewTaskTime.setText(
                    mDFTaskTime.format(task.getTaskStartTime()) + " - " +
                            mDFTaskTime.format(task.getTaskEndTime()) + ": " +
                            mDFDifferenceTime.format(task.getTaskEndTime() - task.getTaskStartTime()));
        }

        //change color and mTimePreference in swipe item
        //add TimeStart and TimeEnd task in items

        //stop task
        holder.mImageButtonStopTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task.getTaskEndTime() == 0 && task.getTaskStartTime() != 0) {
                    stopTask(position);
                    Snackbar.make(v, R.string.task_finished, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        //edit task
        holder.mButtonEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.removeShownLayouts(holder.mSwipeLayout);
                mItemManger.closeAllItems();
                Intent intent = new Intent(mContext, TaskActivity.class);
                intent.putExtra(KEY_ITEM_EDIT_TASK, task);
                intent.putExtra(KEY_ITEM_POSITION, position);
                stopAlarmManager();
                ((AppCompatActivity) mContext).startActivityForResult(intent, REQUEST_CODE_EDIT_TASK);
            }
        });

        //delete 1 task from list
        holder.mButtonDeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.removeShownLayouts(holder.mSwipeLayout);
                mTaskArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                stopAlarmManager();
                mItemManger.closeAllItems();
                mRealmControl.deleteTaskFromRealm(task.getId());
            }
        });

        //restore task to previous stage
        holder.mImageButtonRestoreTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task.getTaskEndTime() != 0) {
                    mTaskArrayList.set(position, mRealmControl.stopTask(task, 0, taskColors.getStartColor()));
                    setNotification(position);
                } else if (task.getTaskStartTime() != 0) {
                    mTaskArrayList.set(position, mRealmControl.startTask(task, 0, taskColors.getDefaultColor()));
                }
                mItemManger.removeShownLayouts(holder.mSwipeLayout);
                mItemManger.closeAllItems();
                notifyDataSetChanged();
            }
        });

        //task'll start working
        holder.mImageButtonStartTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getItem(position).isStop()) {
                    switch (getItem(position).getTaskStatus()) {
                        case Task.TASK_START:
                            startTask(position);
                            break;
                        case Task.TASK_PAUSE:
                            pauseTask(position);
                            break;
                        case Task.TASK_RESUME:
                            resumeTask(position);
                            break;
                    }
                }
            }
        });

        //without this method swipe menu disappeared from swipe item. Be careful
        mItemManger.bindView(holder.itemView, position);
    }

    public void setNotification(int position) {

        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        //mTimePreference = preferences.getLong(KEY_ALARM_TIME, 0);

        Intent intent = createIntent(KEY_ACTION + position, mTaskArrayList.get(position).getTaskTitle(), position);

        mAlarmSender = PendingIntent.getBroadcast(mContext, position, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(AlarmManager.RTC,
                    System.currentTimeMillis() + 5000 - getItem(position).getTimeDifference(),
                    mAlarmSender);
        } else {
            mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 5000 - getItem(position).getTimeDifference(), mAlarmSender);
        }
    }


    public Intent createIntent(String action, String title, int position) {
        return new Intent(mContext, FinishReceiver.class)
                .setAction(action)
                .putExtra(KEY_TITLE, title)
                .putExtra(KEY_ITEM_POSITION, position);
    }

    public void startTask(int position) {
        mTaskTimeStart = System.currentTimeMillis();
        setNotification(position);
        mTaskArrayList.set(position,
                mRealmControl.startTask(getItem(position),
                        mTaskTimeStart,
                        taskColors.getStartColor()));
        mRealmControl.toRealm(mTaskArrayList);
        notifyDataSetChanged();
    }

    public void stopTask(int position) {
        mTaskTimeEnd = System.currentTimeMillis();
        stopAlarmManager();
        mTaskArrayList.set(position,
                mRealmControl.stopTask(
                        getItem(position),
                        mTaskTimeEnd,
                        taskColors.getEndColor()));
        mRealmControl.toRealm(mTaskArrayList);
        notifyDataSetChanged();
    }

    public void pauseTask(int position) {
        mTaskTimePause = System.currentTimeMillis();
        stopAlarmManager();
        mTaskArrayList.set(position,
                mRealmControl.pauseTask(
                        getItem(position),
                        mTaskTimePause));
        mRealmControl.toRealm(mTaskArrayList);
        notifyDataSetChanged();
    }

    public void resumeTask(int position) {
        setNotification(position);
        mTaskArrayList.set(position,
                mRealmControl.resumeTask(
                        getItem(position),
                        getItem(position).getTimeDifference()));
        mRealmControl.toRealm(mTaskArrayList);
        notifyDataSetChanged();
    }

    public void stopAlarmManager() {
        if (mAlarmSender != null) {
            mAlarmManager.cancel(mAlarmSender);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mTaskArrayList.size();
    }

    public Task getItem(int position) {
        return mTaskArrayList.get(position);
    }

}
