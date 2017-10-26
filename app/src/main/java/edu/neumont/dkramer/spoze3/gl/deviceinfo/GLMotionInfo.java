package edu.neumont.dkramer.spoze3.gl.deviceinfo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import edu.neumont.dkramer.spoze3.gl.GLContext;

/**
 * Created by dkramer on 10/25/17.
 */

public abstract class GLMotionInfo extends GLDeviceInfo implements SensorEventListener {
    protected SensorManager mSensorManager;
    protected Sensor mSensor;


    public GLMotionInfo(GLContext ctx) {
        super(ctx);
        mSensorManager = (SensorManager)getGLContext().getSystemService(Context.SENSOR_SERVICE);
        mSensor = acquireSensor();
    }

    /**
     * Method to be implemented that handles acquiring the proper sensor
     */
    protected abstract Sensor acquireSensor();

    /**
     * Method to be implemented that should copy all current values to their previous counterpart
     */
    protected abstract void updatePreviousValues();

    /**
     * Method that should be implemented to update the current values based on a new SensorEvent
     * @param event SensorEvent containing latest values
     */
    protected abstract void updateCurrentValues(SensorEvent event);


    @Override
    public void start() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void stop() {
        mSensorManager.unregisterListener(this, mSensor);
    }


    protected SensorManager getSensorManager() {
        return mSensorManager;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        updatePreviousValues();
        updateCurrentValues(sensorEvent);
    }

    // unused
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }
}
