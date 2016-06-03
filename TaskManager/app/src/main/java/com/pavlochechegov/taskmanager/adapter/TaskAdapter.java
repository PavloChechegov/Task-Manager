package com.pavlochechegov.taskmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.model.Task;

import java.util.ArrayList;

public class TaskAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Task> mTaskArrayList;

    public TaskAdapter (Context context, ArrayList<Task> taskArrayList) {
        mContext = context;
        mTaskArrayList = taskArrayList;
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

        viewHolder.mTextViewTaskTitle.setText(task.getTaskTitle());
        viewHolder.mTextViewTaskComment.setText(task.getTaskComment());
        viewHolder.mTextViewTaskTime.setText(task.getTaskStartTime() + task.getTaskEndTime());

        return convertView;
    }
}
