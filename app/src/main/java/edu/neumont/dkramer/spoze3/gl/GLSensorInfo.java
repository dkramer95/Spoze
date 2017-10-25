package edu.neumont.dkramer.spoze3.gl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLSensorInfo extends GLDeviceInfo implements SensorEventListener {
    public static final int LAST_YAW = 0;
    public static final int LAST_PITCH = 1;
    public static final int LAST_ROLL = 2;

    public static final int CURRENT_YAW = 10;
    public static final int CURRENT_PITCH = 11;
    public static final int CURRENT_ROLL = 12;



    private SensorManager mSensorManager;
    private Sensor mSensor;



    public GLSensorInfo(GLContext glContext) {
        super(glContext);
        initSensor();
        initValues();
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

    protected void initValues() {
        // init starting values so that updates don't receive null
    	setValue(CURRENT_YAW, 0f);
        setValue(CURRENT_ROLL, 0f);
        setValue(CURRENT_PITCH, 0f);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        float[] orientation = new float[3];
        float[] rotationMatrix = new float[9];

        SensorManager.getRotationMatrixFromVector(rotationMatrix, values);
        SensorManager.getOrientation(rotationMatrix, orientation);

        convertToDegrees(orientation);

        updatePreviousValues();
        updateCurrentValues(orientation);
    }

    protected void updatePreviousValues() {
        setValue(LAST_YAW, getValue(CURRENT_YAW));
        setValue(LAST_PITCH, getValue(CURRENT_PITCH));
        setValue(LAST_ROLL, getValue(CURRENT_ROLL));
    }

    protected void updateCurrentValues(float[] values) {
        // update current values
        setValue(CURRENT_YAW, values[0]);
        setValue(CURRENT_PITCH, values[1]);
        setValue(CURRENT_ROLL, values[2]);
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
