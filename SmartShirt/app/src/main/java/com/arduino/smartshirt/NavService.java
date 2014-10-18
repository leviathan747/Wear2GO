package com.arduino.smartshirt;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by kylekrynski on 10/18/14.
 */
public class NavService extends NotificationListenerService {

    /* ABSTRACT METHODS SECTION */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d("LOG", "*****NOTIFICATION: "+ sbn.getId() + "---" + sbn.getNotification().tickerText + "---" + sbn.getPackageName());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }
    /* END ABSTRACT METHOD*/

}
