package edu.neumont.dkramer.spoze3.gl;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dkramer on 10/20/17.
 */

public abstract class GLDeviceInfo extends GLObject {
    /**
     * Constant values to be used when creating a GLContext and enabling
     * specific GLDeviceInfo types, using GLContext.enableDeviceInfo(type);
     */
    public static final int TYPE_TOUCH_INPUT = 1;
    public static final int TYPE_ROTATION_VECTOR = 100;

    /*
     * This is shared between all GLDeviceInfo classes.. Subclasses should specify
     * constant values so that retrieving a specific type of value is easy between
     * different classes.
     */
    protected static final ConcurrentHashMap<Integer, Float> sValues = new ConcurrentHashMap<>();


    public GLDeviceInfo(GLContext ctx) {
        super(ctx);
    }


    public abstract void start();

    public abstract void stop();


    public static float get(int valueType) {
        return sValues.get(valueType);
    }

    protected static void set(int key, float value) {
        sValues.put(key, value);
    }

}
