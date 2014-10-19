package com.arduino.smartshirt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//This activity is simply for the purposes of getting the app to start
public class StartUp extends Activity {

    /* GLOBAL VARIABLES */
    private final Activity this_activity = this;

    SmartShirt app;

    Button left;
    Button right;
    Button reroute;
    Button arrived;
    Button blip;

    EditText head;
    EditText body;
    Button send;

    Button test;
    /* END GLOBAL VAR*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, NavService.class);
        startService(intent);

        // render the content view
        setContentView(R.layout.test_platform);

        // define app
        app = (SmartShirt) this.getApplication();

        // define buttons
        left = (Button) findViewById(R.id.left_btn);
        right = (Button) findViewById(R.id.right_btn);
        reroute = (Button) findViewById(R.id.reroute_btn);
        arrived = (Button) findViewById(R.id.arrived_btn);
        blip = (Button) findViewById(R.id.blip_btn);

        head = (EditText) findViewById(R.id.pebble_head);
        body = (EditText) findViewById(R.id.pebble_body);
        send = (Button) findViewById(R.id.send_btn);

        test = (Button) findViewById(R.id.test_btn);

        // set button listeners
        left.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                app.arduino_controller.turnLeft();
            }
        });
        right.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                app.arduino_controller.turnRight();
            }
        });
        reroute.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                app.arduino_controller.reroute();
            }
        });
        arrived.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                app.arduino_controller.arrived();
            }
        });
        blip.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                app.arduino_controller.blip();
            }
        });

        send.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String peb_head = head.getText().toString();
                String peb_body = body.getText().toString();
                app.pebble_controller.sendNotification(peb_head, peb_body);
            }
        });


        test.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(this_activity, TestPlatform.class);
                startActivity(intent);
            }
        });


        //finish();
    }

}
