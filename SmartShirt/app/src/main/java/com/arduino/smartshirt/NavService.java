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
    /* CONSTANTS */
    private final static double FT_IN_MILE = 5280.0;
    private final static int MIN_DISTANCE_ARDUINO_TURN = 50;  //in feet
    private final static int MIN_DISTANCE_PEBBLE_TURN = 400;  //in feet
    /* END CONSTANTS */

    /* LOCAL VARIABLES */
    private String prevSent = " ";
    private SmartShirt app;
    private ArduinoController ac;
    private PebbleController pc;
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
    @Override
    public void onCreate() {
        super.onCreate();

        //Need to have local reference to app, arduino controller, and pebble controller
        app = ((SmartShirt) this.getApplication());
        ac = app.arduino_controller;
        pc = app.pebble_controller;
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
            sendLocationBasedText(extraText);
        } else if (extraText.charAt(0) == 'H') {  //If a dash does not exist, check for location unknown case
            sendNoLocationBasedText(extraText);
        } else if (extraText.charAt(0) == 'R') {   //If no dash and no period, must be at destination
            sendLostText(extraText);
        } else {  //Exhausted all other cases, you must be at destination
            sendDestinationText(extraText);
        }

    }

    private double distanceInFeet(String s) {
        String[] parts = s.split("\\s+");
        double dist = Integer.parseInt(parts[0]);  //get distance from string parts

        if (parts[1].equals("mi")) {  //need to convert to mi
            dist = dist * FT_IN_MILE;
        }

        return dist;
    }

    private boolean isRight(String s) {
        String[] parts = s.split("\\s+");
        return (parts[4].equals("right"));
    }

    private String parseTurn(String s) {
        

    }

    private String parseTurnSupplement(String s) {
        int posOfDash = s.indexOf('-');


    }

    /* END PARSING FROM MAP METHODS */


    /* SEND METHODS */

    //sending a turn command - arduino and pebble
    private void sendLocationBasedText(String et) {
        if (prevSent.equals(et)) {   //Dont send a second lost message in a row if this case happens
            return;
        }
        //Parse distance in feet from message
        double dist = distanceInFeet(et);

        //Make arduino choose proper method if the distance to turn is below limit
        if (dist < MIN_DISTANCE_ARDUINO_TURN) {
            //Parse turn
            boolean right = isRight(et);
            if (right) {
                ac.turnRight();
            } else {
                ac.turnLeft();
            }
        }

        //Make pebble show message, no distance limit
        if (dist < MIN_DISTANCE_PEBBLE_TURN) {
            //Parse turn string
            String title = parseTurn(et);
            //Parse supplement string
            String body = parseTurnSupplement(et);

            pc.sendNotification(title, body);

        }

        prevSent = et;
    }

    //sending a no location message - arduino and pebble
    private void sendNoLocationBasedText(String et) {
        if (prevSent.equals(et)) {   //Dont send a second lost message in a row if this case happens
            return;
        }

        //Make arduino blip only if coming from blank state
        if (prevSent.equals(" ")) {
            ac.blip();
        }

        //Make pebble show complete string
        String title = et.substring(0, 33);
        String body = et.substring(35);
        pc.sendNotification(title, body);

        prevSent = et;
    }

    //sending a Lost message - arduino and pebble
    private void sendLostText(String et) {
        if (prevSent.equals(et)) {   //Dont send a second lost message in a row if this case happens
            return;
        }

        //Make alternate buzz on arduino
        ac.reroute();

        //Display Lost message on pebble
        pc.sendNotification("Direction unavailable:", et);

        prevSent = et;
    }

    //sending (no need to parse) a Destination - arduino and pebble
    private void sendDestinationText(String et) {
        //No need to check for double request, destination note ends the nav activity
        //Make all motors buzz on arduino
        ac.arrived();

        //Display final location on pebble
        pc.sendNotification("Arrived at:", et);

        prevSent = " ";  //set prevSent back to blank state to check for begin nav activity
    }
    /* END SEND METHODS */


    /* GENERAL STRING METHODS CUSTOM */

    /* END STRING METHODS */

}
