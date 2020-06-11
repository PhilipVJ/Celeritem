package com.example.celeritem.Managers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.celeritem.ExerciseActivity;
import com.example.celeritem.R;

/**
 * Handles notifications to the notification drawer
 */
public class NotifyManager {

    private final Context context;
    private final String appTitle = "Celeritem";
    private static final String CHANNEL_ID = "MyServiceChannel";

    public NotifyManager(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    /**
     * Sets up the notification channel for later use
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    /**
     * Creates a Notification object based on the text argument
     * @param text
     * @return an Notification object
     */
    public Notification createNotification(String text) {
        Intent notificationIntent = new Intent(context, ExerciseActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, 0);
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(appTitle)
                .setContentText(text)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.icon)
                .setContentIntent(pendingIntent)
                .build();
    }

    /**
     * Updates the notification drawer with the given text argument
     * @param text
     */
    public void updateNotification(String text) {
        Notification notification = createNotification(text);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);
    }
}
