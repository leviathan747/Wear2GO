package com.arduino.smartshirt;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import junit.framework.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by levistarrett on 10/18/14.
 */
public class TestPlatform extends Activity {

    // pull in test notifications from text file
    private LinkedList<String> loadNotifications(File file) {
        // list of individual notifications
        LinkedList<String> notifications = new LinkedList<String>();

        // read in whole file
        FileReader reader;
        try {
            reader = new FileReader(file);
        }
        catch (FileNotFoundException e) {
            Log.d("LOG", "File not found");
            return null;
        }

        int nextChar;                               // current character parsing
        String currentNotif = "";                   // current notification
        boolean inNotif = false;                    // currently in a notif
        while (true) {
            try {
                nextChar = reader.read();
            }
            catch (IOException e) {
                Log.d("LOG", "I/O Exception");
                return null;
            }
            if (nextChar == -1) break;              // end of stream

            switch (nextChar) {
                case '*':                           // beginning of notif
                    inNotif = true;
                    break;
                case '<':                           // end. add to list and reset currentNotif
                    notifications.add(currentNotif);
                    currentNotif = "";
                    inNotif = false;
                    break;
                default:
                    if (inNotif) currentNotif += (char) nextChar;   // add to current string
            }
        }


        // return list
        return notifications;
    }

    // send a notification to be parsed
    private void dispatchNotification(String notification) {
        Log.d("LOG", notification);
    }

    // run all tests
    public void runTest() {
        // find file
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath());
        File file = new File(dir, "routeLog.txt");

        // load notifications
        LinkedList<String> notifications = loadNotifications(file);

        // for each notification, dispatch and then delay 1 second
        for (String notif : notifications) {
            dispatchNotification(notif);
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
            }
        }

    }

}
