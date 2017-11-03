package edu.neumont.dkramer.spoze3.gl.deviceinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLObject;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.TOUCH_INPUT;

/**
 * Created by dkramer on 10/20/17.
 */

public abstract class GLDeviceInfo extends GLObject {
    /*
     * Shared between GLDeviceInfo classes, which stores their respective values here. Access
     * to a particular value can be obtained by specifying the Value type. GLDeviceInfo
     * types must be added through a GLContext in order to be enabled.
     */
    protected static final ConcurrentHashMap<Value, Float> sValues = new ConcurrentHashMap<>();

    // ensure keys exist to prevent some issues of accessing missing values the first time
    static {
        for(Value v : Value.values()) {
            set(v, 0f);
        }
    }

    protected List<OnUpdateListener> mUpdateListeners;


    public GLDeviceInfo(GLContext ctx) {
        super(ctx);
        mUpdateListeners = new ArrayList<>();
    }


    public abstract void start();

    public abstract void stop();

    public void addOnUpdateListener(OnUpdateListener listener) {
    	mUpdateListeners.add(listener);
    }

    public void removeOnUpdateListener(OnUpdateListener listenerToRemove) {
        mUpdateListeners.remove(listenerToRemove);
    }

    protected void notifyUpdateListeners() {
        for(OnUpdateListener listener : mUpdateListeners) {
            listener.onUpdate(TOUCH_INPUT);
        }
    }


    public static float get(Value valueType) {
        return sValues.get(valueType);
    }

    protected static void set(Value key, float value) {
        sValues.put(key, value);
    }


    /* Interface to allow users to do things when an update occurs */
    public interface OnUpdateListener {
        void onUpdate(GLDeviceInfo.Type sender);
    }


    /**
     * This enum defines all the available GLDeviceInfo types that can be used
     */
    public enum Type {
        TOUCH_INPUT,
        ROTATION_VECTOR,
        ACCELEROMETER,
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
        LAST_TOUCH_NORMALIZED_X,
        LAST_TOUCH_NORMALIZED_Y,

        CURRENT_TOUCH_X,
        CURRENT_TOUCH_Y,
        CURRENT_TOUCH_NORMALIZED_X,
        CURRENT_TOUCH_NORMALIZED_Y,

        /* Values for Type.ACCELEROMETER */

        CALIBRATED_ACCEL_X,
        CALIBRATED_ACCEL_Y,
        CALIBRATED_ACCEL_Z,

        LAST_ACCEL_X,
        LAST_ACCEL_Y,
        LAST_ACCEL_Z,

        CURRENT_ACCEL_X,
        CURRENT_ACCEL_Y,
        CURRENT_ACCEL_Z,
    }

}
