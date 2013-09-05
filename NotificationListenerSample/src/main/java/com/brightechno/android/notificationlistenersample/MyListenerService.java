

package com.brightechno.android.notificationlistenersample;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MyListenerService extends NotificationListenerService {
    private static final String TARGET_APP_PACKAGE = "com.google.android.gm"; // Gmail Application


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // return if the notification isn't from the target
        if (!TARGET_APP_PACKAGE.equals(sbn.getPackageName())) {
            return;
        }

        // get intent from notification
        Notification notification = sbn.getNotification();

        if (TARGET_APP_PACKAGE.equals(sbn.getPackageName())) {
            Intent intent = new Intent();
            PendingIntent pendingIntent = notification.contentIntent;
            try {
                pendingIntent.send(this, 0, intent);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        /* nop */
    }
}
