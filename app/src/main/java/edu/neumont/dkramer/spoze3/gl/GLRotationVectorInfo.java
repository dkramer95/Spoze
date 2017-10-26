package edu.neumont.dkramer.spoze3.gl;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static edu.neumont.dkramer.spoze3.gl.GLDeviceInfo.Value.CURRENT_PITCH;
import static edu.neumont.dkramer.spoze3.gl.GLDeviceInfo.Value.CURRENT_ROLL;
import static edu.neumont.dkramer.spoze3.gl.GLDeviceInfo.Value.CURRENT_YAW;
import static edu.neumont.dkramer.spoze3.gl.GLDeviceInfo.Value.LAST_PITCH;
import static edu.neumont.dkramer.spoze3.gl.GLDeviceInfo.Value.LAST_ROLL;
import static edu.neumont.dkramer.spoze3.gl.GLDeviceInfo.Value.LAST_YAW;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLRotationVectorInfo extends GLMotionInfo implements SensorEventListener {
    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientation = new float[3];



    public GLRotationVectorInfo(GLContext glContext) {
        super(glContext);
        initValues();
    }

    @Override
    protected Sensor acquireSensor() {
        return getSensorManager().getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    protected void initValues() {
        // init starting values so that updates don't receive null
    	set(CURRENT_YAW, 0f);
        set(CURRENT_PITCH, 0f);
        set(CURRENT_ROLL, 0f);
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
        set(LAST_YAW, get(CURRENT_YAW));
        set(LAST_PITCH, get(CURRENT_PITCH));
        set(LAST_ROLL, get(CURRENT_ROLL));
    }

    protected void updateCurrentValues(float[] values) {
        // update current values
        set(CURRENT_YAW, values[0]);
        set(CURRENT_PITCH, values[1]);
        set(CURRENT_ROLL, values[2]);
    }

    private void convertToDegrees(float[] vector) {
        for (int j = 0; j < vector.length; ++j) {
            vector[j] = Math.round(Math.toDegrees(vector[j]));
        }
    }
}
