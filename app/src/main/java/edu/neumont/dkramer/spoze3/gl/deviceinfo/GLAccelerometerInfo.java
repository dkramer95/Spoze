package edu.neumont.dkramer.spoze3.gl.deviceinfo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import edu.neumont.dkramer.spoze3.gl.GLContext;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ACCELEROMETER;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CALIBRATED_ACCEL_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CALIBRATED_ACCEL_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CALIBRATED_ACCEL_Z;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_ACCEL_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_ACCEL_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_ACCEL_Z;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.LAST_ACCEL_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.LAST_ACCEL_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.LAST_ACCEL_Z;

/**
 * Created by dkramer on 10/25/17.
 */

public class GLAccelerometerInfo extends GLMotionInfo {

    public GLAccelerometerInfo(GLContext ctx) {
        super(ctx);
    }

    @Override
    protected Sensor acquireSensor() {
        return getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void updatePreviousValues() {
        set(LAST_ACCEL_X, getf(CURRENT_ACCEL_X));
        set(LAST_ACCEL_Y, getf(CURRENT_ACCEL_Y));
        set(LAST_ACCEL_Z, getf(CURRENT_ACCEL_Z));
    }

    @Override
    public void calibrate() {
        set(CALIBRATED_ACCEL_X, getf(CURRENT_ACCEL_X));
        set(CALIBRATED_ACCEL_Y, getf(CURRENT_ACCEL_Y));
        set(CALIBRATED_ACCEL_Z, getf(CURRENT_ACCEL_Z));
    }

    @Override
    protected void updateCurrentValues(SensorEvent event) {
        float[] values = event.values;
        set(CURRENT_ACCEL_X, values[0]);
        set(CURRENT_ACCEL_Y, values[1]);
        set(CURRENT_ACCEL_Z, values[2]);
    }


    @Override
    protected void notifyUpdateListeners() {
        for(OnUpdateListener listener : mUpdateListeners) {
            listener.onUpdate(ACCELEROMETER);
        }
    }
}
