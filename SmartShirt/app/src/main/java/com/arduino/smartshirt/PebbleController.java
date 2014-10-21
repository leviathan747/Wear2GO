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
public class PebbleController extends Controller {

    // parent app pointer
    private Application parentApp;

    // constructor
    public PebbleController(Application parentApp) {
        this.parentApp = parentApp;
    }

    // send alert to pebble
    public void sendNotification(String title, String body) {
        if (!enabled()) {
            Log.d("LOG", "**PEBBLE CONTROLLER DISABLED**");
            return;
        }

        Log.d("LOG", "**CALLING: sendNotification() **");

        // validate arguments
        if (title == null) title = "";
        if (body == null) body = "";

        // create intent to send notif
        final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

        // build notification object
        final Map data = new HashMap();
        data.put("title", title);
        data.put("body", body);
        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();

        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", "SmartShirt");
        i.putExtra("notificationData", notificationData);

        // send it
        Log.d("LOG", "About to send a modal alert to Pebble: " + notificationData);
        ((SmartShirt) this.parentApp).sendBroadcast(i);
    }
}
