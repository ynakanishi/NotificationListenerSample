package com.brightechno.android.notificationlistenersample;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

import java.lang.reflect.InvocationTargetException;

/**
 * Copyright(C) 2013 Brightechno Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class MyAccessibilityService extends AccessibilityService {
    private static final String TARGET_APP_PACKAGE = "com.google.android.gm"; // Gmail Application

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return;
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Notification notification = (Notification) event.getParcelableData();

            String packageName = event.getPackageName().toString();
            if (TARGET_APP_PACKAGE.equals(packageName)) {
                Intent intent = new Intent();
                PendingIntent pendingIntent = notification.contentIntent;
                try {
                    pendingIntent.send(this, 0, intent);
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
