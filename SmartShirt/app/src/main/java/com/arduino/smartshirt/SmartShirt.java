package com.arduino.smartshirt;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by kylekrynski on 10/18/14.
 */
public class SmartShirt extends Application {

    public NavInterface arduino_controller = new ArduinoController();

    /* STANDARD OVERRIDE METHODS */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("LOG", "**APPLICATION CREATED OK**");

    }

    /* END STANDARD OVERRIDE*/

}
