package com.pavlochechegov.taskmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pasha on 6/19/16.
 */
public class TaskColors implements Parcelable {
    private int mDefaultColor, mStartColor, mEndColor;

    public TaskColors(int defaultColor, int startColor, int endColor) {
        mDefaultColor = defaultColor;
        mStartColor = startColor;
        mEndColor = endColor;
    }

    protected TaskColors(Parcel in) {
        mDefaultColor = in.readInt();
        mStartColor = in.readInt();
        mEndColor = in.readInt();
    }

    public static final Creator<TaskColors> CREATOR = new Creator<TaskColors>() {
        @Override
        public TaskColors createFromParcel(Parcel in) {
            return new TaskColors(in);
        }

        @Override
        public TaskColors[] newArray(int size) {
            return new TaskColors[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mDefaultColor);
        dest.writeInt(mStartColor);
        dest.writeInt(mEndColor);
    }
}
