package edu.neumont.dkramer.spoze3.gl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

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

    // unused
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }
}
