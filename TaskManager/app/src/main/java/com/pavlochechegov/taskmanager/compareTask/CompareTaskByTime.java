package com.pavlochechegov.taskmanager.compareTask;

import com.pavlochechegov.taskmanager.model.Task;

import java.util.Comparator;

/**
 * Created by pasha on 6/10/16.
 */
public class CompareTaskByTime implements Comparator<Task>{
    @Override
    public int compare(Task taskTime1, Task taskTime2) {
        return (int) (taskTime1.getTaskStartTime() - taskTime2.getTaskStartTime());
    }
}
