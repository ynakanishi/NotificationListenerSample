/**
 * Copyright(C) 2013 Brightechno Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */

package com.brightechno.android.notificationlistenersample;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("ALL")
public class MyListenerService extends NotificationListenerService {
    private static final String ANDROID_APP_ACTIVITY_MANAGER_NATIVE = "android.app.ActivityManagerNative";
    private static final String ANDROID_CONTENT_IINTENT_SENDER = "android.content.IIntentSender";
    private static final String TARGET_APP_PACKAGE = "com.google.android.gm"; // Gmail Application
    private static final String TARGET_TYPE = "application/gmail-ls"; // type of Gmail Intent


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // return if the notification isn't from the target
        if (!TARGET_APP_PACKAGE.equals(sbn.getPackageName())) {
            return;
        }

        // get intent from notification
        Notification notification = sbn.getNotification();
        Intent intent;
        try {
            intent = getIntentForPendingIntent(notification.contentIntent);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (intent == null || !TARGET_TYPE.equals(intent.getType())) {
            return;
        }

        // change class loader to avoid error
        intent.setExtrasClassLoader(this.getClassLoader());

        // start activity
        startActivity(intent);
    }


    /**
     * get Intent object from a PendingIntent object
     * @param pendingIntent instance of PendingIntent
     * @return an Intent instance of pendingIntent
     */
    private Intent getIntentForPendingIntent(PendingIntent pendingIntent)
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException {
        if (pendingIntent == null) {
            return null;
        }

        IntentSender intentSender = pendingIntent.getIntentSender();

        Field f = findDeclaredField(intentSender.getClass(), "mTarget");
        f.setAccessible(true);
        Object targetObj = f.get(intentSender);

        Class iintentSenderInterface = getInterfaceOfObject(targetObj, ANDROID_CONTENT_IINTENT_SENDER);

        Class activityManagerNativeClass = Class.forName(ANDROID_APP_ACTIVITY_MANAGER_NATIVE);

        Method getDefaultMethod = activityManagerNativeClass.getMethod("getDefault", null);
        Method getIntentForIntentSenderMethod = activityManagerNativeClass.getMethod(
                "getIntentForIntentSender",
                iintentSenderInterface);

        Object retIntent;
        Object activityManager = getDefaultMethod.invoke(null, null);
        retIntent = getIntentForIntentSenderMethod.invoke(activityManager, targetObj);

        return (Intent) retIntent;
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        /* nop */
    }


    /**
     * Utility method to get field by reflection
     * This method can find a field derived from ascendant classes
     *
     * @param clazz target class
     * @param fieldName field name
     * @return field
     * @throws NoSuchFieldException
     */
    private Field findDeclaredField(Class clazz, String fieldName) throws NoSuchFieldException {
        Class workClazz = clazz;
        Field field = null;
        while (workClazz != null) {
            try {
                field = workClazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                workClazz = workClazz.getSuperclass();
            }
        }

        if (field == null) {
            throw new NoSuchFieldException();
        }

        return field;
    }


    /**
     * Utility method to get a specific interface of the target object
     *
     * @param target target object
     * @param interfaceName interface name
     * @return interface
     * @throws ClassNotFoundException
     */
    private Class getInterfaceOfObject(Object target, String interfaceName) throws ClassNotFoundException {
        Class clazz = target.getClass();

        Class[] intfs = clazz.getInterfaces();
        for (Class intf : intfs) {
            if (interfaceName.equals(intf.getName())) {
                return intf;
            }
        }

        throw new ClassNotFoundException();
    }
}
