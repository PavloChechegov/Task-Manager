package com.pavlochechegov.taskmanager.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.model.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class TaskAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Task> mTaskArrayList;
    private Resources mResources;
    DateFormat mDFTaskTime, mDFDifferenceTime;
    public TaskAdapter (Context context, ArrayList<Task> taskArrayList, Resources resources) {
        mContext = context;
        mTaskArrayList = taskArrayList;
        mResources = resources;
        mDFTaskTime = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        mDFDifferenceTime = new SimpleDateFormat("HH:mm:ss");
        mDFDifferenceTime.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    static class ViewHolder {
        TextView mTextViewTaskTitle, mTextViewTaskComment, mTextViewTaskTime;
    }

    @Override
    public int getCount() {
        return mTaskArrayList.size();
    }

    @Override
    public Task getItem(int position) {
        return mTaskArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        Task task = getItem(position);

        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_task, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mTextViewTaskTitle = (TextView) convertView.findViewById(R.id.tvTaskTitle);
            viewHolder.mTextViewTaskComment = (TextView) convertView.findViewById(R.id.tvTaskComment);
            viewHolder.mTextViewTaskTime = (TextView) convertView.findViewById(R.id.tvTaskTime);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        convertView.setBackgroundColor(mResources.getColor(task.getTaskColor()));

        viewHolder.mTextViewTaskTitle.setText(task.getTaskTitle());
        viewHolder.mTextViewTaskComment.setText(task.getTaskComment());
        viewHolder.mTextViewTaskTime.setText(mDFTaskTime.format(task.getTaskStartTime()));

        if(mTaskArrayList.get(position).getTaskStartTime() == 0){
            viewHolder.mTextViewTaskTime.setVisibility(View.GONE);
        } else if(mTaskArrayList.get(position).getTaskEndTime() == 0) {
            viewHolder.mTextViewTaskTime.setVisibility(View.VISIBLE);
            viewHolder.mTextViewTaskTime.setText(mDFTaskTime.format(task.getTaskStartTime()));
        } else {
            viewHolder.mTextViewTaskTime.setVisibility(View.VISIBLE);
            viewHolder.mTextViewTaskTime.setText(
                            mDFTaskTime.format(task.getTaskStartTime()) + " - " +
                            mDFTaskTime.format(task.getTaskEndTime()) + ": " +
                            mDFDifferenceTime.format(task.getTaskEndTime() - task.getTaskStartTime()));
        }
        return convertView;
    }
}
