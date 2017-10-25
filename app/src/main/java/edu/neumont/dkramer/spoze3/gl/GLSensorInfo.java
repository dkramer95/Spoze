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
    public static final int VALUE_LAST_YAW = 0;
    public static final int VALUE_LAST_PITCH = 1;
    public static final int VALUE_LAST_ROLL = 2;

    public static final int VALUE_CURRENT_YAW = 10;
    public static final int VALUE_CURRENT_PITCH = 11;
    public static final int VALUE_CURRENT_ROLL = 12;

    public static final int VALUE_CALIBRATED_YAW = 20;
    public static final int VALUE_CALIBRATED_PITCH = 21;
    public static final int VALUE_CALIBRATED_ROLL = 22;



    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientation = new float[3];
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
    	set(VALUE_CURRENT_YAW, 0f);
        set(VALUE_CURRENT_ROLL, 0f);
        set(VALUE_CURRENT_PITCH, 0f);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;

        SensorManager.getRotationMatrixFromVector(mRotationMatrix, values);
        SensorManager.getOrientation(mRotationMatrix, mOrientation);

        convertToDegrees(mOrientation);
        updatePreviousValues();
        updateCurrentValues(mOrientation);
    }

    protected void updatePreviousValues() {
        set(VALUE_LAST_YAW, get(VALUE_CURRENT_YAW));
        set(VALUE_LAST_PITCH, get(VALUE_CURRENT_PITCH));
        set(VALUE_LAST_ROLL, get(VALUE_CURRENT_ROLL));
    }

    protected void updateCurrentValues(float[] values) {
        // update current values
        set(VALUE_CURRENT_YAW, values[0]);
        set(VALUE_CURRENT_PITCH, values[1]);
        set(VALUE_CURRENT_ROLL, values[2]);
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
