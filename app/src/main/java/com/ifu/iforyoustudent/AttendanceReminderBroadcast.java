package com.ifu.iforyoustudent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AttendanceReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String extras = intent.getStringExtra("message");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                "notifyTimetable").setSmallIcon(R.drawable.ic_baseline_front_hand_24).setContentTitle("Your " +
                "Attendance").setContentText(extras).setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(200,builder.build());
    }
}
