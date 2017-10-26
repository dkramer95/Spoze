package edu.neumont.dkramer.spoze3.gl;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dkramer on 10/20/17.
 */

public abstract class GLDeviceInfo extends GLObject {
    /*
     * This is shared between all GLDeviceInfo classes.. Subclasses should specify
     * constant values so that retrieving a specific type of value is easy between
     * different classes.
     */
    protected static final ConcurrentHashMap<Value, Float> sValues = new ConcurrentHashMap<>();




    public GLDeviceInfo(GLContext ctx) {
        super(ctx);
    }


    public abstract void start();

    public abstract void stop();


    public static float get(Value valueType) {
        return sValues.get(valueType);
    }

    protected static void set(Value key, float value) {
        sValues.put(key, value);
    }



    /**
     * This enum defines all the available GLDeviceInfo types that can be used
     */
    public enum Type {
        TOUCH_INPUT,
        ROTATION_VECTOR,
    }

    /**
     * This enum defines all the values for each of the GLDeviceInfo types
     */
    public enum Value {

        /* Values for Type.ROTATION_VECTOR */

        CALIBRATED_YAW,
        CALIBRATED_PITCH,
        CALIBRATED_ROLL,

        LAST_YAW,
        LAST_PITCH,
        LAST_ROLL,

        CURRENT_YAW,
        CURRENT_PITCH,
        CURRENT_ROLL,

        /* Values for Type.TOUCH_INPUT */

        LAST_TOUCH_X,
        LAST_TOUCH_Y,

        CURRENT_TOUCH_X,
        CURRENT_TOUCH_Y,

    }

}
