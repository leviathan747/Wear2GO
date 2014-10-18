package com.arduino.smartshirt;

import android.app.Notification;
import android.os.Environment;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by kylekrynski on 10/18/14.
 */
public class NavService extends NotificationListenerService {

    /* ABSTRACT METHODS SECTION */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            if (sbn.getPackageName().equals("com.google.android.apps.maps")) {    //Only saving map package notifications
                Log.d("LOG", "*****NOTIFICATION-INFO: "+ sbn.getId() + "---" + sbn.getNotification().tickerText + "---" + sbn.getPackageName());  //Logging all maps notifications
                Log.d("LOG", "*****NOTIFICATION: " + sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT));  //Logging all maps notification info text

                CreateExternalLogFile("\n*" + sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString() + "<");
            }
        } catch (NullPointerException e) {    //Making sure no nulls try to save
            Log.d("LOG", "*****FILESAVEFAIL: Null extras.");
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }
    /* END ABSTRACT METHOD*/


    /* LOGGING METHODS */
    private void CreateExternalLogFile(String s){
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath());
        dir.mkdirs();
        File file = new File(dir, "routeLog.txt");
        try {
            FileOutputStream f = new FileOutputStream(file,true); //True = Append to file, false = Overwrite
            PrintStream p = new PrintStream(f);
            p.print(s);
            p.close();
            f.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("LOG", "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /* END LOG SECTION*/

}
