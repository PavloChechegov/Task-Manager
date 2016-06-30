package com.pavlochechegov.taskmanager.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
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
import com.pavlochechegov.taskmanager.utils.ManagerControlTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import static com.pavlochechegov.taskmanager.activities.MainActivity.KEY_ITEM_EDIT_TASK;
import static com.pavlochechegov.taskmanager.activities.MainActivity.KEY_ITEM_POSITION;
import static com.pavlochechegov.taskmanager.activities.MainActivity.REQUEST_CODE_CHANGE_TASK;

public class SwipeRecyclerViewAdapter extends RecyclerSwipeAdapter<SwipeRecyclerViewAdapter.TaskViewHolder> {
    private static final String KEY_ALARM_TIME = "pref_task_time_dialog";
    private static final String KEY_ACTION = "key_action";
    private static final String KEY_TITLE = "key_title";
    private Context mContext;
    private ArrayList<Task> mTaskArrayList;
    private DateFormat mDFTaskTime, mDFDifferenceTime;
    private long mTaskTimeStart, mTaskTimeEnd;
    private ManagerControlTask mManagerControlTask;
    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmSender;
    private long mTimePreference;
    private TaskColors taskColors;

    public SwipeRecyclerViewAdapter(Context context, ArrayList<Task> taskArrayList) {
        mContext = context;
        mTaskArrayList = taskArrayList;
        mDFTaskTime = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        mDFDifferenceTime = new SimpleDateFormat("HH:mm:ss");
        mDFDifferenceTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        mManagerControlTask = ManagerControlTask.getSingletonControl(mContext);
        taskColors = mManagerControlTask.initTaskItemColor();
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewTaskTitle, mTextViewTaskComment, mTextViewTaskTime;
        Button mButtonDeleteTask, mButtonEditTask;
        ImageButton mImageButtonRestoreTask, mImageButtonStartTask, mImageButtonStopTask;
        SwipeLayout mSwipeLayout;

        public TaskViewHolder(View itemView) {
            super(itemView);
            mSwipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            mImageButtonRestoreTask = (ImageButton) itemView.findViewById(R.id.imgBtnRestoreTask);
            mTextViewTaskTitle = (TextView) itemView.findViewById(R.id.tvTaskTitle);
            mTextViewTaskComment = (TextView) itemView.findViewById(R.id.tvTaskComment);
            mTextViewTaskTime = (TextView) itemView.findViewById(R.id.tvTaskTime);
            mButtonDeleteTask = (Button) itemView.findViewById(R.id.btnDeleteTaskItem);
            mButtonEditTask = (Button) itemView.findViewById(R.id.btnEditTaskItem);
            mImageButtonStartTask = (ImageButton) itemView.findViewById(R.id.imageButtonPlay);
            mImageButtonStopTask = (ImageButton) itemView.findViewById(R.id.imageButtonStop);
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

        holder.mSwipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //   YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });

        //change color and mTimePreference in swipe item
        //add TimeStart and TimeEnd task in items

        //task'll start working
        holder.mImageButtonStartTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task.getTaskStartTime() == 0) {
                    startTask(position);
                    Snackbar.make(v, R.string.task_started, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

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
                ((AppCompatActivity) mContext).startActivityForResult(intent, REQUEST_CODE_CHANGE_TASK);
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
                mManagerControlTask.deleteTaskFromRealm(task.getId());
            }
        });

        //restore task to previous stage
        holder.mImageButtonRestoreTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task.getTaskEndTime() != 0) {
                    mTaskArrayList.set(position, mManagerControlTask.stopTask(task, 0, taskColors.getStartColor()));
                    setNotification(position);
                } else if (task.getTaskStartTime() != 0) {
                    mTaskArrayList.set(position, mManagerControlTask.startTask(task, 0, taskColors.getDefaultColor()));
                }
                mItemManger.removeShownLayouts(holder.mSwipeLayout);
                mItemManger.closeAllItems();
                notifyDataSetChanged();
            }
        });


        //without this method swipe menu disappeared from swipe item. Be careful
        mItemManger.bindView(holder.itemView, position);
    }

    public void setNotification(int position) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mTimePreference = preferences.getLong(KEY_ALARM_TIME, 0);

        Intent intent = createIntent(KEY_ACTION + position, mTaskArrayList.get(position).getTaskTitle(), position);

        mAlarmSender = PendingIntent.getBroadcast(mContext, position, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(AlarmManager.RTC, System.currentTimeMillis() + 5000, mAlarmSender);
        } else {
            mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 5000, mAlarmSender);
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
        mTaskArrayList.set(position, mManagerControlTask.startTask(getItem(position), mTaskTimeStart, taskColors.getStartColor()));
        mManagerControlTask.toRealm(mTaskArrayList);
        notifyDataSetChanged();
    }

    public void stopTask(int position) {
        mTaskTimeEnd = System.currentTimeMillis();
        stopAlarmManager();
        mTaskArrayList.set(position, mManagerControlTask.stopTask(getItem(position), mTaskTimeEnd, taskColors.getEndColor()));
        mManagerControlTask.toRealm(mTaskArrayList);
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
