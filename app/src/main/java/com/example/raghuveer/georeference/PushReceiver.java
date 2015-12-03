package com.example.raghuveer.georeference;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by raghuveer on 11/28/2015.
 */
public class PushReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        String task_id = intent.getStringExtra("task_id");
        String task_name = intent.getStringExtra("task_name");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, PlacesActivity.class);
        notificationIntent.putExtra("task_id", task_id);
        Log.d("id", task_id);
        notificationIntent.putExtra("task_name", task_name);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentTitle("Reminder").setContentText(task_name).setSmallIcon(R.drawable.ic_9czeggdri).setSound(Settings.System.DEFAULT_ALARM_ALERT_URI).setContentIntent(pendingIntent).setAutoCancel(true).build();
        notificationManager.notify(1, notification);
    }
}
