package com.pavlochechegov.taskmanager.activities;

import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.pavlochechegov.taskmanager.R;
import yuku.ambilwarna.widget.AmbilWarnaPreference;

public class SettingsActivity extends AppCompatActivity {

    public static class SettingPreferenceFragment extends PreferenceFragment {

        AmbilWarnaPreference defaultColorTask, startColorTask, endColorTask;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

            defaultColorTask = (AmbilWarnaPreference) findPreference("preference_key_default_color");
            startColorTask = (AmbilWarnaPreference) findPreference("preference_key_start_task_color");
            endColorTask = (AmbilWarnaPreference) findPreference("preference_key_end_task_color");

            Preference preferenceButton = findPreference("default_settings");
            preferenceButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    defaultColorTask.forceSetValue(getResources().getColor(R.color.default_task_color));
                    startColorTask.forceSetValue(getResources().getColor(R.color.start_task_color));
                    endColorTask.forceSetValue(getResources().getColor(R.color.finish_task_color));

                    return true;
                }
            });
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.frame_layout, new SettingPreferenceFragment()).commit();
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, getIntent());
        super.onBackPressed();
    }
}
