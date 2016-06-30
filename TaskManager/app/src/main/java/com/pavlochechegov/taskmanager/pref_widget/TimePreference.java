package com.pavlochechegov.taskmanager.pref_widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;



import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimePreference extends DialogPreference {
    private Calendar mCalendar;
    private TimePicker mTimePicker = null;

    public TimePreference(Context context) {

        this(context, null);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPositiveButtonText("Ok");
        setNegativeButtonText("Cancel");
        mCalendar = new GregorianCalendar();
    }

    @Override
    protected View onCreateDialogView() {
        mTimePicker = new TimePicker(getContext());
        mTimePicker.setIs24HourView(true);
        return (mTimePicker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        mTimePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTimePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            mCalendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
            mCalendar.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());

            setSummary(getSummary());
            if (callChangeListener(mCalendar.getTimeInMillis())) {
                persistLong(mCalendar.getTimeInMillis());
                notifyChanged();
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (restoreValue) {
            if (defaultValue == null) {
                mCalendar.setTimeInMillis(getPersistedLong(System.currentTimeMillis()));
            } else {
                mCalendar.setTimeInMillis((Long) defaultValue);
            }
        } else {
            if (defaultValue == null) {
                mCalendar.setTimeInMillis(System.currentTimeMillis());
            } else {
                mCalendar.setTimeInMillis(Long.parseLong((String) defaultValue));
            }
        }
        setSummary(getSummary());
    }

    public void setDefaultTime(int hour, int minutes) {

        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minutes);

        long mHour = TimeUnit.HOURS.toMillis(hour);
        long mMinute = TimeUnit.MINUTES.toMillis(minutes);
        long sumTime = mHour + mMinute;

        persistLong(sumTime);
        setSummary(getSummary());
        notifyChanged();
    }

    @Override
    public CharSequence getSummary() {
        if (mCalendar == null) {
            return null;
        }
        return DateFormat.getTimeFormat(getContext()).format(new Date(mCalendar.getTimeInMillis()));
    }
}
