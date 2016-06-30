package com.pavlochechegov.taskmanager.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.activities.MainActivity;

import static com.pavlochechegov.taskmanager.activities.MainActivity.KEY_ITEM_POSITION;

/**
 * Created by pasha on 6/29/16.
 */
public class FinishReceiver extends BroadcastReceiver {

    private static final String KEY_TITLE = "key_title";

    private NotificationManager mNotificationManager;
    private String mTitle;
    private int mPosition;

    @Override
    public void onReceive(final Context mContext, Intent mParentIntent) {

        mTitle = mParentIntent.getStringExtra(KEY_TITLE);
        mPosition = mParentIntent.getIntExtra(KEY_ITEM_POSITION, 0);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent mIntent = new Intent(mContext, MainActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPosition, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder mNotificationBuilder =
                new Notification.Builder(mContext)
                .setAutoCancel(true)
                .setContentTitle(mTitle)
                .setContentText(mContext.getResources().getString(R.string.task_finished))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(mPendingIntent);

        MainActivity.sSwipeRecyclerViewAdapter.stopTask(mPosition);

        mNotificationManager.notify(mPosition, mNotificationBuilder.build());

    }

}
