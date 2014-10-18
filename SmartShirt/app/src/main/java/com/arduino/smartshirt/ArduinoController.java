package com.arduino.smartshirt;

/**
 * Created by levistarrett on 10/18/14.
 */
public class ArduinoController implements NavInterface {

    // CONSTANTS
    private static final String ARDUINO_HOST = "192.168.240.1/arduino/digital";
    private static final int LEFT_PIN = 12;
    private static final int RIGHT_PIN = 11;


    // send turn left signal to Arduino
    public void turnLeft() {
        // http interface
        AsyncHTTPGetter getter = new AsyncHTTPGetter();

        StringBuilder url = new StringBuilder();
        url.append("http://");                         // add the protocol
        url.append(ARDUINO_HOST);                        // add the host
        url.append("/");                               // add separator
        url.append(LEFT_PIN);                          // add pin specifier
        url.append("/1");                              // write a 1

        getter.execute(url.toString());
    }

    // send turn right signal to Arduino
    public void turnRight() {
        // http interface
        AsyncHTTPGetter getter = new AsyncHTTPGetter();

        StringBuilder url = new StringBuilder();
        url.append("http://");                         // add the protocol
        url.append(ARDUINO_HOST);                        // add the host
        url.append("/");                               // add separator
        url.append(RIGHT_PIN);                         // add pin specifier
        url.append("/1");                              // write a 1

        getter.execute(url.toString());
    }

}
