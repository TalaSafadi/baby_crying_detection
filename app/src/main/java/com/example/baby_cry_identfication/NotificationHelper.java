package com.example.baby_cry_identfication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationHelper {

    private static final String CHANNEL_ID = "BabyCryingAlertChannel";
    private static final CharSequence CHANNEL_NAME = "Baby Crying Alert";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for Baby Crying Alerts");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static String getChannelId() {
        return CHANNEL_ID;
    }
}
