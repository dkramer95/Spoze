package edu.neumont.dkramer.spoze3.gl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLSensorInfo extends GLDeviceInfo implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;



    public GLSensorInfo(GLContext glContext) {
        super(glContext);
        initSensor();
    }

    protected void initSensor() {
        mSensorManager =
                (SensorManager)getGLContext().getSystemService(Context.SENSOR_SERVICE);

        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    @Override
    public void start() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void stop() {
        mSensorManager.unregisterListener(this, mSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        float[] orientation = new float[3];
        float[] rotationMatrix = new float[9];

        SensorManager.getRotationMatrixFromVector(rotationMatrix, values);
        SensorManager.getOrientation(rotationMatrix, orientation);

        convertToDegrees(orientation);

        float yaw   = orientation[0];   // x
        float pitch = orientation[1];   // y
        float roll  = orientation[2];   // z

        Log.i("GLSensorInfo", String.format("yaw: %f, pitch: %f, roll: %f", yaw, pitch, roll));
    }

    private void convertToDegrees(float[] vector) {
        for (int j = 0; j < vector.length; ++j) {
            vector[j] = Math.round(Math.toDegrees(vector[j]));
        }
    }

    // unused
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

}
