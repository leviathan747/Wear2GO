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
    /* LOCAL VARIABLES */
    private String prevSent = " ";
    /* END LOCAL VARIABLES */


    /* ABSTRACT METHODS SECTION */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            if (sbn.getPackageName().equals("com.google.android.apps.maps")) {    //Only saving map package notifications
                Log.d("LOG", "*****NOTIFICATION-INFO: "+ sbn.getId() + "---" + sbn.getNotification().tickerText + "---" + sbn.getPackageName());  //Logging all maps notifications
                CharSequence extraTextChar = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT);
                Log.d("LOG", "*****NOTIFICATION: " + extraTextChar);  //Logging all maps notification info text

                String extraText = extraTextChar.toString();
                CreateExternalLogFile("\n*" + extraText + "<");
                parseFromMap(extraText);
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


    /* PARSING FROM MAP METHODS and CONSTANTS */
    private void parseFromMap(String extraText) {
        //Check to see if there is a '-' in the string
        if (extraText.indexOf('-') != -1) {  //If a dash exists, notification must be location based
            parseLocationBasedText(extraText);
        } else if (extraText.charAt(0) == 'H') {  //If a dash does not exist, check for location unknown case
            parseNoLocationBasedText(extraText);
        } else if (extraText.charAt(0) == 'R') {   //If no dash and no period, must be at destination
            sendLostText(extraText);
        } else {  //Exhausted all other cases, you must be at destination
            sendDestinationText(extraText);
        }

    }

    private void parseLocationBasedText(String et) {

    }

    private void parseNoLocationBasedText(String et) {

    }

    /* END PARSING FROM MAP METHODS */


    /* SEND METHODS */

    //sending a Lost message - calling methods to send messages to arduino
    private void sendLostText(String et) {
        if (prevSent.equals(et)) {   //Dont send a second lost message in a row if this case happens
            return;
        }

        //Make alternate buzz on arduino

        //Display Lost message on pebble

        prevSent = et;
    }

    //sending (no need to parse) a Destination - calling methods to send messages to arduino and pebble
    private void sendDestinationText(String et) {
        //No need to check for double request, destination note ends the nav activity
        //Make all motors buzz on arduino

        //Display final location on pebble

        prevSent = " ";  //set prevSent back to blank state to check for begin nav activity
    }
    /* END SEND METHODS */


    /* GENERAL STRING METHODS CUSTOM */

    /* END STRING METHODS */

}
