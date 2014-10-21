package com.arduino.smartshirt;

import android.util.Log;

/**
 * Created by levistarrett on 10/18/14.
 */
public class ArduinoController extends Controller {

    // CONSTANTS
    private static final String ARDUINO_HOST = "192.168.240.1/arduino/command";
    private static final char LEFT_PIN = 'r';
    private static final char RIGHT_PIN = 'l';
    private static final char REROUTE_PIN = 'c';
    private static final char ARRIVED_PIN = 's';
    private static final char BLIP_PIN = 's';


    // send blip signal to Arduino
    public void blip() {
        if (!enabled()) {
            Log.d("LOG", "**ARDUINO CONTROLLER DISABLED**");
            return;
        }

        Log.d("LOG", "**CALLING: blip() **");

        // http interface
        AsyncHTTPGetter getter = new AsyncHTTPGetter();

        // format url string
        String url = String.format("http://%s/%c", ARDUINO_HOST, BLIP_PIN);

        Log.d("LOG", "HTTP/GET: " + url);
        getter.execute(url.toString());
    }

    // send turn left signal to Arduino
    public void turnLeft() {
        if (!enabled()) {
            Log.d("LOG", "**ARDUINO CONTROLLER DISABLED**");
            return;
        }

        Log.d("LOG", "**CALLING: turnLeft() **");

        // http interface
        AsyncHTTPGetter getter = new AsyncHTTPGetter();

        // format url string
        String url = String.format("http://%s/%c", ARDUINO_HOST, LEFT_PIN);

        Log.d("LOG", "HTTP/GET: " + url);
        getter.execute(url.toString());
    }

    // send turn right signal to Arduino
    public void turnRight() {
        if (!enabled()) {
            Log.d("LOG", "**ARDUINO CONTROLLER DISABLED**");
            return;
        }

        Log.d("LOG", "**CALLING: turnRight() **");

        // http interface
        AsyncHTTPGetter getter = new AsyncHTTPGetter();

        // format url string
        String url = String.format("http://%s/%c", ARDUINO_HOST, RIGHT_PIN);

        Log.d("LOG", "HTTP/GET: " + url);
        getter.execute(url.toString());
    }

    // send reroute Arduino
    public void reroute() {
        if (!enabled()) {
            Log.d("LOG", "**ARDUINO CONTROLLER DISABLED**");
            return;
        }

        Log.d("LOG", "**CALLING: reroute() **");

        // http interface
        AsyncHTTPGetter getter = new AsyncHTTPGetter();

        // format url string
        String url = String.format("http://%s/%c", ARDUINO_HOST, REROUTE_PIN);

        Log.d("LOG", "HTTP/GET: " + url);
        getter.execute(url.toString());
    }

    // send arrived Arduino
    public void arrived() {
        if (!enabled()) {
            Log.d("LOG", "**ARDUINO CONTROLLER DISABLED**");
            return;
        }

        Log.d("LOG", "**CALLING: arrived() **");

        // http interface
        AsyncHTTPGetter getter = new AsyncHTTPGetter();

        // format url string
        String url = String.format("http://%s/%c", ARDUINO_HOST, ARRIVED_PIN);

        Log.d("LOG", "HTTP/GET: " + url);
        getter.execute(url.toString());
    }

}
