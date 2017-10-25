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
	/* TYPE_ROTATION_VECTOR prepended to prevent integer key collision in map */

    /*
     * Constants indicating previous values
     */
    public static final int VALUE_LAST_YAW = (TYPE_ROTATION_VECTOR + 0x00);
    public static final int VALUE_LAST_PITCH = (TYPE_ROTATION_VECTOR + 0x01);
    public static final int VALUE_LAST_ROLL = (TYPE_ROTATION_VECTOR + 0x02);

    /*
     * Constants indicating current values
     */
    public static final int VALUE_CURRENT_YAW = (TYPE_ROTATION_VECTOR + 0x03);
    public static final int VALUE_CURRENT_PITCH = (TYPE_ROTATION_VECTOR + 0x04);
    public static final int VALUE_CURRENT_ROLL = (TYPE_ROTATION_VECTOR + 0x05);

    /*
     * Constants indicating calibrated values
     */
    public static final int VALUE_CALIBRATED_YAW =  (TYPE_ROTATION_VECTOR + 0x06);
    public static final int VALUE_CALIBRATED_PITCH = (TYPE_ROTATION_VECTOR + 0x07);
    public static final int VALUE_CALIBRATED_ROLL = (TYPE_ROTATION_VECTOR + 0x08);



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
