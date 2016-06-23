package com.pavlochechegov.taskmanager.model;

/**
 * Created by pasha on 6/19/16.
 */
public class TaskColors {
    private int mDefaultColor, mStartColor, mEndColor;

    public TaskColors(int defaultColor, int startColor, int endColor) {
        mDefaultColor = defaultColor;
        mStartColor = startColor;
        mEndColor = endColor;
    }

    public int getDefaultColor() {
        return mDefaultColor;
    }

    public void setDefaultColor(int defaultColor) {
        mDefaultColor = defaultColor;
    }

    public int getStartColor() {
        return mStartColor;
    }

    public void setStartColor(int startColor) {
        mStartColor = startColor;
    }

    public int getEndColor() {
        return mEndColor;
    }

    public void setEndColor(int endColor) {
        mEndColor = endColor;
    }
}
