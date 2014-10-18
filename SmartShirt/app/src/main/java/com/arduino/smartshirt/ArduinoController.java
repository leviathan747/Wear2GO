package com.arduino.smartshirt;

import android.util.Log;

/**
 * Created by levistarrett on 10/18/14.
 */
public class ArduinoController {

    // CONSTANTS
    private static final String ARDUINO_HOST = "192.168.240.1/arduino/digital";
    private static final int LEFT_PIN = 12;
    private static final int RIGHT_PIN = 11;
    private static final int REROUTE_PIN = 10;
    private static final int ARRIVED_PIN = 9;
    private static final int BLIP_PIN = 8;


    // send blip signal to Arduino
    public void blip() {
        Log.d("LOG", "**CALLING: blip() **");

        // http interface
        AsyncHTTPGetter getter = new AsyncHTTPGetter();

        // format url string
        String url = String.format("http://%s/%d/1", ARDUINO_HOST, BLIP_PIN);

        Log.d("LOG", "HTTP/GET: " + url);
        getter.execute(url.toString());
    }

    // send turn left signal to Arduino
    public void turnLeft() {
        Log.d("LOG", "**CALLING: turnLeft() **");

        // http interface
        AsyncHTTPGetter getter = new AsyncHTTPGetter();

        // format url string
        String url = String.format("http://%s/%d/1", ARDUINO_HOST, LEFT_PIN);

        Log.d("LOG", "HTTP/GET: " + url);
        getter.execute(url.toString());
    }

    // send turn right signal to Arduino
    public void turnRight() {
        Log.d("LOG", "**CALLING: turnRight() **");

        // http interface
        AsyncHTTPGetter getter = new AsyncHTTPGetter();

        // format url string
        String url = String.format("http://%s/%d/1", ARDUINO_HOST, RIGHT_PIN);

        Log.d("LOG", "HTTP/GET: " + url);
        getter.execute(url.toString());
    }

    // send reroute Arduino
    public void reroute() {
        Log.d("LOG", "**CALLING: reroute() **");

        // http interface
        AsyncHTTPGetter getter = new AsyncHTTPGetter();

        // format url string
        String url = String.format("http://%s/%d/1", ARDUINO_HOST, REROUTE_PIN);

        Log.d("LOG", "HTTP/GET: " + url);
        getter.execute(url.toString());
    }

    // send arrived Arduino
    public void arrived() {
        Log.d("LOG", "**CALLING: arrived() **");

        // http interface
        AsyncHTTPGetter getter = new AsyncHTTPGetter();

        // format url string
        String url = String.format("http://%s/%d/1", ARDUINO_HOST, ARRIVED_PIN);

        Log.d("LOG", "HTTP/GET: " + url);
        getter.execute(url.toString());
    }

}
