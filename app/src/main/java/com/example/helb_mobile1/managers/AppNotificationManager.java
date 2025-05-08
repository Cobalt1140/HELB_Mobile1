package com.example.helb_mobile1.managers;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;


public class AppNotificationManager {
    public static final String CHANNEL_ID = "inPlaineSightNotifications";
    public static final String EXTRA_NOTIFICATION_TYPE = "notification_type";
    public static final String NOTIF_TYPE_DAILY_WORD = "DailyWord";
    public static final String NOTIF_TYPE_DAILY_RESULTS = "ResultTime";
    public static final int REQUEST_CODE_WORD = 1;
    public static final int REQUEST_CODE_RESULTS = 2;

    private final Context context;

    public AppNotificationManager(Context context) {
        this.context = context.getApplicationContext(); // Use app context to avoid leaks
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        //Creates a channel for notifications, so that when the user mutes the app's notifications, it mutes their specified category
        //in this case, we put all our notifications in one channel
        CharSequence name = "In Plaine Sight daily notifications";
        String description = "Reminds the user about the daily word and results of their marker submission";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void scheduleReminder(String type, int hour, int requestCode) {
        //type so that the NotificationReceiver knows what to do with the notification
        //hour for obvious purposes
        //requestCode so Android OS knows if duplicate Notification or not

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(EXTRA_NOTIFICATION_TYPE, type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    /*
    public void cancelDailyReminder() {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

     */
}

