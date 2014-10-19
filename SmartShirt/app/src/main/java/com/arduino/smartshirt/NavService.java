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
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                Log.d("LOG", "**SERVICE***NOTIFICATION-INFO: "+ sbn.getId() + "---" + sbn.getNotification().tickerText + "---" + sbn.getPackageName());  //Logging all maps notifications
                CharSequence extraTextChar = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT);
                Log.d("LOG", "**SERVICE***NOTIFICATION: " + extraTextChar);  //Logging all maps notification info text

                String extraText = extraTextChar.toString();
                CreateExternalLogFile("\n*" + extraText + "<");
                parseFromMap(extraText);
            }
        } catch (NullPointerException e) {    //Making sure no nulls try to save
            Log.d("LOG", "**SERVICE***FILESAVEFAIL: Null extras.");
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (sbn.getPackageName().equals("com.google.android.apps.maps")) {
            Log.d("REM", "**SERVICE***NOTIFICATION-REMOVE-INFO: "+ sbn.getId() + "---" + sbn.getNotification().tickerText + "---" + sbn.getPackageName());  //Logging all maps notifications
            CharSequence extraTextChar = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT);
            Log.d("REM", "**SERVICE***NOTIFICATION-REMOVE: " + extraTextChar);  //Logging all maps notification info text
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();

        //Need to have local reference to app, arduino controller, and pebble controller
        app = ((SmartShirt) this.getApplication());
        ac = app.arduino_controller;
        pc = app.pebble_controller;
        Log.d("LOG", "**SERVICE***LOCAL CONTROLLERS FILLED");
    }

    /* END ABSTRACT METHOD*/


    /* LOGGING METHODS */
    private void CreateExternalLogFile(String s){
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath());
        dir.mkdirs();
        File file = new File(dir, "routeLog.txt");
        try {
            boolean b = true;
            if (prevSent.equals(" ")) {
                b = false; //True = Append to file, false = Overwrite
            }
            FileOutputStream f = new FileOutputStream(file, b);
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
        //Make arduino blip only if coming from blank state
        if (prevSent.equals(" ")) {
            ac.blip();
            Log.d("LOG", "**SERVICE***ARDUINO: Sent blip call to arduino interface.");
        }
        //Remove any estimated time from string
        extraText = withoutTime(extraText);

        //Check to see if there is a '-' in the string
        if (extraText.indexOf('-') != -1) {  //If a dash exists, notification must be location based
            Log.d("LOG", "**SERVICE***PARSE: Found location message.");
            Log.d("LOG", "**SERVICE***MESSAGE: " + extraText);
            sendLocationBasedText(extraText);
        } else if (extraText.charAt(0) == 'H') {  //If start with H, non-location data
            Log.d("LOG", "**SERVICE***PARSE: Found non-location message.");
            Log.d("LOG", "**SERVICE***MESSAGE: " + extraText);
            sendNoLocationBasedText(extraText);
        } else if (extraText.charAt(0) == 'R') {   //If start with R, recalculating
            Log.d("LOG", "**SERVICE***PARSE: Found reroute message.");
            Log.d("LOG", "**SERVICE***MESSAGE: " + extraText);
            sendLostText(extraText);
        } else if (extraText.charAt(0) == 'S') {   //If searching for GPS
            Log.d("LOG", "**SERVICE***PARSE: Found searching for GPS message.");
            Log.d("LOG", "**SERVICE***MESSAGE: " + extraText);
            sendSearchGPS(extraText);

        } else {  //Exhausted all other cases, you must be at destination
            Log.d("LOG", "**SERVICE***PARSE: Found destination message.");
            Log.d("LOG", "**SERVICE***MESSAGE: " + extraText);
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

    //More robust method to detect right or left turn / merge / other, return 1 if right, 0 if left, -1 if neither
    private int isRightOrLeft(String s) {
        if (s.indexOf("right") != -1) {
            return 1;
        } if (s.indexOf("left") != -1) {
            return 0;
        } else {
            return -1;
        }
    }

    //Parse Location to title and body
    private String[] parseTitleandBody(String s, int right) {
        //Parsing up to dash
        int posOfDash = s.indexOf('-');
        String dist = s.substring(0, posOfDash-1);

        //Parsing the part after dash upto first return
        String secondSection = s.substring(posOfDash+2, s.indexOf(s.length()));
        String thirdSection = "";
        String fourthSection = "";
        if (right == 1) {
            secondSection = s.substring(posOfDash+2, s.indexOf("right")+5);
            thirdSection = s.substring(s.indexOf("right")+6, s.indexOf("\n\n"));
            thirdSection = thirdSection + "\n";
            fourthSection = s.substring(s.indexOf("\n\n")+2, s.length());
        } else if (right == 0) {
            secondSection = s.substring(posOfDash+2, s.indexOf("left")+4);
            thirdSection = s.substring(s.indexOf("left")+5, s.indexOf("\n\n"));
            thirdSection = thirdSection + "\n";
            fourthSection = s.substring(s.indexOf("\n\n")+2, s.length());
        } else {
            thirdSection = secondSection;
            secondSection = "";
        }

        //Creating output array
        String[] out = new String[2];
        out[0] = dist + ": " + secondSection;
        out[1] = thirdSection + fourthSection;
        return out;
    }

    /* END PARSING FROM MAP METHODS */


    /* SEND METHODS */

    //sending a turn command - arduino and pebble
    private void sendLocationBasedText(String et) {
        if (prevSent.equals(et)) {   //Dont send a second lost message in a row if this case happens
            Log.d("LOG", "**SERVICE***PREVFOUND: Caught repeated message.");
            return;
        }
        /*  ONLY USE IF WANT TO STOP NOTIFICATION ON AUTO RECALCULATE
        if (compareMessagesWithoutDest(prevSent, et)) {
            Log.d("LOG", "**SERVICE***PREVFOUND: Caught repeated TIME location message.");
            return;
        } */

        //Parse distance in feet from message
        double dist = distanceInFeet(et);
        Log.d("LOG", "**SERVICE***DISTANCE: Calculated distance: " + Double.toString(dist));

        //Parse turn
        int right = isRightOrLeft(et);

        //Make arduino choose proper method if the distance to turn is below limit
        if (dist == MIN_DISTANCE_ARDUINO_TURN) {
            Log.d("LOG", "**SERVICE***TURNTYPE: Is right turn: " + Integer.toString(right));
            if (right == 1) {
                ac.turnRight();
            } else if (right == 0)  {
                ac.turnLeft();
            } else {
                //ARDUINO ON NO TURN BUT DISTANCE TO MANUEVER
            }
            Log.d("LOG", "**SERVICE***ARDUINO: Sent turn call to arduino interface.");
            prevSent = et;
        }
        else {
            Log.d("LOG", "**SERVICE***ABORTCALL: Aborted arduino call because too large distance.");
            Log.d("LOG", "**SERVICE***ABORTDIST: " + Double.toString(dist) + ", PRESET: " + Integer.toString(MIN_DISTANCE_ARDUINO_TURN));
        }

        //Make pebble show message, no distance limit
        if (dist <= MIN_DISTANCE_PEBBLE_TURN) {
            //Make matcher object
            String[] tnb = parseTitleandBody(et, right);

            Log.d("LOG", "**SERVICE***FORMATPCALL: Formatted Pebble interface call, title: " + tnb[0] + " , body: " + tnb[1]);
            pc.sendNotification(tnb[0], tnb[1]);
            Log.d("LOG", "**SERVICE***PEBBLE: Sent turn call to pebble interface.");
            prevSent = et;
        }
        else {
            Log.d("LOG", "**SERVICE***ABORTCALL: Aborted pebble call because too large distance.");
            Log.d("LOG", "**SERVICE***ABORTDIST: " + Double.toString(dist) + ", PRESET: " + Integer.toString(MIN_DISTANCE_PEBBLE_TURN));
        }
    }

    //sending a no location message - arduino and pebble
    private void sendNoLocationBasedText(String et) {
        if (prevSent.equals(et)) {   //Dont send a second lost message in a row if this case happens
            Log.d("LOG", "**SERVICE***PREVFOUND: Caught repeated message.");
            return;
        }
        /*  ONLY USE IF WANT TO STOP NOTIFICATION ON AUTO RECALCULATE
        if (compareMessagesWithoutDest(prevSent, et)) {   //Do not resend no location message if only time has changed.
            Log.d("LOG", "**SERVICE***PREVFOUND: Caught repeated TIME no location message.");
            return;
        } */

        //Make pebble show complete string
        String title = et.substring(0, et.indexOf("\n\n"));
        String body = et.substring(et.indexOf("\n\n")+2, et.length());

        Log.d("LOG", "**SERVICE***FORMATPCALL: Formatted Pebble interface call, title: " + title + " , body: " + body);
        pc.sendNotification(title, body);
        Log.d("LOG", "**SERVICE***PEBBLE: Sent no location call to pebble interface.");

        prevSent = et;
    }

    //sending a Lost message - arduino and pebble
    private void sendLostText(String et) {
        if (prevSent.equals(et)) {   //Dont send a second lost message in a row if this case happens
            Log.d("LOG", "**SERVER***PREVFOUND: Caught repeated message.");
            return;
        }

        //Make alternate buzz on arduino
        ac.reroute();
        Log.d("LOG", "**SERVICE***ARDUINO: Sent reroute call to arduino interface.");

        //Display Lost message on pebble
        pc.sendNotification("Direction unavailable:", et);
        Log.d("LOG", "**SERVICE***PEBBLE: Sent reroute call to pebble interface.");

        prevSent = et;
    }

    //sending a Searching for GPS message - pebble only
    private void sendSearchGPS(String et) {
        if (prevSent.equals(et)) {   //Dont send a second lost message in a row if this case happens
            Log.d("LOG", "**SERVER***PREVFOUND: Caught repeated message.");
            return;
        }

        //Display search text on pebble
        pc.sendNotification("Please Wait", et);
        Log.d("LOG", "**SERVICE***PEBBLE: Sent search GPS call to pebble interface.");

        prevSent = et;
    }

    //sending (no need to parse) a Destination - arduino and pebble
    private void sendDestinationText(String et) {
        //No need to check for double request, destination note ends the nav activity
        //Make all motors buzz on arduino
        ac.arrived();
        Log.d("LOG", "**SERVICE***ARDUINO: Sent reroute call to arduino interface.");

        //Display final location on pebble
        pc.sendNotification("Arrived at:", et);
        Log.d("LOG", "**SERVICE***PEBBLE: Sent destination call to pebble interface.");

        prevSent = " ";  //set prevSent back to blank state to check for begin nav activity
    }
    /* END SEND METHODS */


    /* GENERAL STRING METHODS CUSTOM */
    //compare two messages without their estimated time of arrived, returns null if failure to do different message types
    private boolean compareMessagesWithoutDest(String a, String b) {
        String a2 = withoutDest(a);
        String b2 = withoutDest(b);

        if (a2 == null || b2 == null) {
            return false;
        }

        return a2.equals(b2);
    }

    //return a notification string without its destination distance, returns null if cant find removal point
    private String withoutDest(String a) {
        int end = a.length();
        int curChar = a.charAt(end-1);
        while (a.charAt(curChar) != '\n') {
            curChar = curChar - 1;
            if (curChar < 0) {
                Log.d("LOG", "**SERVICE***REMOVAL: Estimated Destination Removed Unsuccessfully.");
                Log.d("LOG", "**SERVICE***REMOVAL: " + a);
                return null;
            }
        }
        Log.d("LOG", "**SERVICE***REMOVAL: Estimated Destination Removed Successfully.");
        Log.d("LOG", "**SERVICE***REMOVAL: " + a);
        return a.substring(0, curChar-1);
    }

    //return a notification string without its estimated time of arrived, returns original string if cant find removal point
    private String withoutTime (String a) {
        int end = a.length();
        int curChar = a.charAt(end-1);
        while (a.charAt(curChar) != '\n') {
            curChar = curChar - 1;
            if (curChar < 0) {
                Log.d("LOG", "**SERVICE***REMOVAL: Estimated Time Removed Unsuccessfully.");
                Log.d("LOG", "**SERVICE***REMOVAL: " + a);
                return a;
            }
        }
        Log.d("LOG", "**SERVICE***REMOVAL: Estimated Time Removed Successfully.");
        Log.d("LOG", "**SERVICE***REMOVAL: " + a);
        return a.substring(0, curChar);
    }

    /* END STRING METHODS */

}
