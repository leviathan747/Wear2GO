package com.arduino.smartshirt;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by levistarrett on 10/18/14.
 */
public class PebbleController implements NavInterface {

    // parent app pointer
    private Application parentApp;

    // constructor
    public PebbleController(Application parentApp) {
        this.parentApp = parentApp;
    }

    // turn left signals
    public void turnLeft(String details) {
        sendAlertToPebble("Turn Left", details);
    }

    // turn right signals
    public void turnRight(String details) {
        sendAlertToPebble("Turn Right", details);
    }

    // send alert to pebble
    private void sendAlertToPebble(String title, String body) {
        final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

        final Map data = new HashMap();
        data.put("title", title);
        data.put("body", body);
        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();

        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", "SmartShirt");
        i.putExtra("notificationData", notificationData);

        Log.d("LOG", "About to send a modal alert to Pebble: " + notificationData);
        ((SmartShirt) this.parentApp).sendBroadcast(i);
    }
}
