package com.pavlochechegov.taskmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.activities.MainActivity;
import com.pavlochechegov.taskmanager.activities.TaskActivity;
import com.pavlochechegov.taskmanager.model.Task;
import com.pavlochechegov.taskmanager.model.TaskColors;
import com.pavlochechegov.taskmanager.utils.ManagerControlTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import static com.pavlochechegov.taskmanager.activities.MainActivity.KEY_ITEM_LONG_CLICK;
import static com.pavlochechegov.taskmanager.activities.MainActivity.KEY_ITEM_POSITION;
import static com.pavlochechegov.taskmanager.activities.MainActivity.REQUEST_CODE_CHANGE_TASK;

public class SwipeRecyclerViewAdapter extends RecyclerSwipeAdapter<SwipeRecyclerViewAdapter.TaskViewHolder> {
    private Context mContext;
    private ArrayList<Task> mTaskArrayList;
    private DateFormat mDFTaskTime, mDFDifferenceTime;
    private long mTaskTimeStart, mTaskTimeEnd;
    private ManagerControlTask mManagerControlTask;

    public SwipeRecyclerViewAdapter(Context context, ArrayList<Task> taskArrayList) {
        mContext = context;
        mTaskArrayList = taskArrayList;
        mDFTaskTime = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        mDFDifferenceTime = new SimpleDateFormat("HH:mm:ss");
        mDFDifferenceTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        mManagerControlTask = new ManagerControlTask(context);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewTaskTitle, mTextViewTaskComment, mTextViewTaskTime;
        Button mButtonDeleteTask, mButtonEditTask;
        ImageButton mImageButtonRestoreTask, mImageButtonResetEndTask;
        SwipeLayout mSwipeLayout;

        public TaskViewHolder(View itemView) {
            super(itemView);
            mSwipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            mImageButtonRestoreTask = (ImageButton) itemView.findViewById(R.id.imgBtnRestoreTask);
            //mImageButtonResetEndTask = (ImageButton) itemView.findViewById(R.id.imgBtnResetEndTask);
            mTextViewTaskTitle = (TextView) itemView.findViewById(R.id.tvTaskTitle);
            mTextViewTaskComment = (TextView) itemView.findViewById(R.id.tvTaskComment);
            mTextViewTaskTime = (TextView) itemView.findViewById(R.id.tvTaskTime);
            mButtonDeleteTask = (Button) itemView.findViewById(R.id.btnDeleteTaskItem);
            mButtonEditTask = (Button) itemView.findViewById(R.id.btnEditTaskItem);
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
        holder.mSwipeLayout.setBackgroundColor(task.getTaskColor());

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

//        holder.mSwipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
//            @Override
//            public void onClose(SwipeLayout layout) {
//                //   YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
//            }
//
//            @Override
//            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
//                //you are swiping.
//            }
//
//            @Override
//            public void onStartOpen(SwipeLayout layout) {
//
//            }
//
//            @Override
//            public void onOpen(SwipeLayout layout) {
//
//            }
//
//            @Override
//            public void onStartClose(SwipeLayout layout) {
//
//            }
//
//            @Override
//            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
//
//            }
//        });

        //change color  and time in swipe item
        //add TimeStart and TimeEnd task in items

        final TaskColors taskColors = mManagerControlTask.initTaskItemColor();
        holder.mSwipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task.getTaskStartTime() == 0) {
                    mTaskTimeStart = System.currentTimeMillis();
                    task.setTaskStartTime(mTaskTimeStart);
                    task.setTaskColor(taskColors.getStartColor());
                    mTaskArrayList.set(position, task);
                    Snackbar.make(v, R.string.task_started, Snackbar.LENGTH_SHORT).show();

                } else if (task.getTaskEndTime() == 0) {

                    mTaskTimeEnd = System.currentTimeMillis();
                    task.setTaskEndTime(mTaskTimeEnd);
                    task.setTaskColor(taskColors.getEndColor());
                    mTaskArrayList.set(position, task);

                    Snackbar.make(v, R.string.task_finished, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    task.setTaskEndTime(0);
                                    task.setTaskColor(taskColors.getStartColor());
                                    mTaskArrayList.set(position, task);
                                    notifyDataSetChanged();
                                }
                            }).show();
                }
                mManagerControlTask.saveArrayList(mTaskArrayList);
                notifyDataSetChanged();
            }
        });

        holder.mButtonDeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.removeShownLayouts(holder.mSwipeLayout);
                mTaskArrayList.remove(position);
                notifyItemRemoved(position);
                mItemManger.closeAllItems();
                mManagerControlTask.saveArrayList(mTaskArrayList);
            }
        });

        holder.mImageButtonRestoreTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(task.getTaskEndTime() != 0){
                    task.setTaskEndTime(0);
                    task.setTaskColor(taskColors.getStartColor());
                    mTaskArrayList.set(position, task);
                    notifyDataSetChanged();
                } else if(task.getTaskStartTime() != 0){
                    task.setTaskStartTime(0);
                    task.setTaskColor(taskColors.getDefaultColor());
                    mTaskArrayList.set(position, task);
                    notifyDataSetChanged();
                }

            }
        });

//        holder.mImageButtonResetEndTask.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });



        //without this method swipe menu disappeared from swipe item. Be careful
        mItemManger.bindView(holder.itemView, position);

    }

        public interface ClickListener {
        void editTask(View v, int position);
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
