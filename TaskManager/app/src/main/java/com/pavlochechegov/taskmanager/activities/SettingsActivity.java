package com.pavlochechegov.taskmanager.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import com.afollestad.materialdialogs.color.CircleView;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.pavlochechegov.taskmanager.R;

import com.pavlochechegov.taskmanager.pref_widget.TimePreference;
import com.pavlochechegov.taskmanager.utils.ManagerControlTask;
import yuku.ambilwarna.widget.AmbilWarnaPreference;

import java.util.Calendar;
import java.util.Date;

public class SettingsActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {
    private Toolbar toolbar;
    private int mColor;
    private ManagerControlTask mManagerControlTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mManagerControlTask = ManagerControlTask.getSingletonControl(this);
        //mColor = mManagerControlTask.loadThemeColor();
        //if (mColor != 0) initTheme(mColor);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(R.id.frame_layout, new SettingPreferenceFragment()).commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorChooserDialog.Builder(SettingsActivity.this, R.string.color_palette)
                        .allowUserColorInput(true)
                        .customButton(R.string.md_custom_label)
                        .presetsButton(R.string.md_presets_label)
                        .show();
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int color) {
        //initTheme(color);
        //mManagerControlTask.saveThemeColor(color);
    }

    public void initTheme(int color) {
        if (getSupportActionBar() != null) {
            toolbar.setBackgroundColor(color);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CircleView.shiftColorDown(color));
            getWindow().setNavigationBarColor(color);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, getIntent());
        super.onBackPressed();
    }


    public static class SettingPreferenceFragment extends PreferenceFragment {
        private static final String KEY_ALARM_TIME = "pref_task_time_dialog";
        Context mContext;
        AmbilWarnaPreference defaultColorTask, startColorTask, endColorTask;
        TimePreference mTimePreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

            defaultColorTask = (AmbilWarnaPreference) findPreference("preference_key_default_color");
            startColorTask = (AmbilWarnaPreference) findPreference("preference_key_start_task_color");
            endColorTask = (AmbilWarnaPreference) findPreference("preference_key_end_task_color");

            mTimePreference = (TimePreference) findPreference(KEY_ALARM_TIME);

            Preference preferenceButton = findPreference("default_settings");

            preferenceButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    defaultColorTask.forceSetValue(getResources().getColor(R.color.default_task_color));
                    startColorTask.forceSetValue(getResources().getColor(R.color.start_task_color));
                    endColorTask.forceSetValue(getResources().getColor(R.color.finish_task_color));

                    mTimePreference.setDefaultTime(1, 0);
                    return true;
                }
            });
        }
    }


}

