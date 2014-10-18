package com.arduino.smartshirt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

//This activity is simply for the purposes of getting the app to start
//Once it starts with a blank screen, it immediately kills itself
//All functionality should be started from the Application class
public class StartUp extends Activity {

    /* GLOBAL VARIABLES */
    //private NavService navService;
    /* END GLOBAL VAR*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Starts an instance of the service in the background
        //navService = new NavService();
        //navService.startService(new Intent(this, NavService.class));
        Intent intent = new Intent(this, NavService.class);
        startService(intent);

        finish();
    }

}
