package com.arduino.smartshirt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.ToggleButton;

//This activity is simply for the purposes of getting the app to start
public class SettingsActivity extends Activity {

    /* GLOBAL VARIABLES */

    private Activity this_activity = this;

    private SmartShirt app;

    private Button go_settings;

    private Switch pebble_controller;
    private Switch arduino_controller;

    private Button open_test;

    /* END GLOBAL VAR*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // start the notif parsing service
        Intent intent = new Intent(this, NavService.class);
        startService(intent);

        // render the content view
        setContentView(R.layout.settings);

        // define app
        app = (SmartShirt) this.getApplication();

        // define widgets
        go_settings = (Button) findViewById(R.id.settings_btn);

        pebble_controller = (Switch) findViewById(R.id.pebble_controller);
        arduino_controller = (Switch) findViewById(R.id.arduino_controller);

        open_test = (Button) findViewById(R.id.test_btn);

        // add listeners
        go_settings.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
            }
        });

        pebble_controller.setOnClickListener(new Switch.OnClickListener() {
            public void onClick(View v) {
                // Is the switch on?
                boolean on = ((Switch) v).isChecked();

                if (on) {
                    // Enable pebble
                    Log.d("LOG", "**ENABLING PEBBLE CONTROLLER**");
                    app.pebble_controller.enable();
                } else {
                    // Disable pebble
                    Log.d("LOG", "**DISABLING PEBBLE CONTROLLER**");
                    app.pebble_controller.disable();
                }
            }
        });

        arduino_controller.setOnClickListener(new Switch.OnClickListener() {
            public void onClick(View v) {
                // Is the switch on?
                boolean on = ((Switch) v).isChecked();

                if (on) {
                    // Enable pebble
                    Log.d("LOG", "**ENABLING ARDUINO CONTROLLER**");
                    app.arduino_controller.enable();
                } else {
                    // Disable pebble
                    Log.d("LOG", "**DISABLING ARDUINO CONTROLLER**");
                    app.arduino_controller.disable();
                }
            }
        });

        open_test.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(this_activity, TestPlatformActivity.class);
                startActivity(intent);
            }
        });

    }

}
