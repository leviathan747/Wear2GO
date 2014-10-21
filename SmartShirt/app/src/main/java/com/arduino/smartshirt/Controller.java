package com.arduino.smartshirt;

/**
 * Created by levistarrett on 10/21/14.
 */
public abstract class Controller {

    // if the controller can send signals or not
    private boolean enabled = false;

    // enable controller
    public void enable() {
        enabled = true;
    }

    // disable controller
    public void disable() {
        enabled = false;
    }

    // get enabled
    public boolean enabled() {
        return enabled;
    }
}
