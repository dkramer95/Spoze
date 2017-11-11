package edu.neumont.dkramer.spoze3.gl.deviceinfo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import edu.neumont.dkramer.spoze3.gl.GLContext;

import static android.hardware.SensorManager.getOrientation;
import static android.hardware.SensorManager.getRotationMatrixFromVector;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CALIBRATED_PITCH;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CALIBRATED_ROLL;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CALIBRATED_YAW;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_PITCH;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_ROLL;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_YAW;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.LAST_PITCH;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.LAST_ROLL;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.LAST_YAW;
import static java.lang.Math.round;
import static java.lang.Math.toDegrees;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLRotationVectorInfo extends GLMotionInfo implements SensorEventListener {
    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientation = new float[3];



    public GLRotationVectorInfo(GLContext glContext) {
        super(glContext);
    }

    @Override
    protected Sensor acquireSensor() {
        return getSensorManager().getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    @Override
    protected void updatePreviousValues() {
        set(LAST_YAW,   getf(CURRENT_YAW));
        set(LAST_PITCH, getf(CURRENT_PITCH));
        set(LAST_ROLL,  getf(CURRENT_ROLL));
    }

    @Override
    public void calibrate() {
        set(CALIBRATED_YAW,   getf(CURRENT_YAW));
        set(CALIBRATED_PITCH, getf(CURRENT_PITCH));
        set(CALIBRATED_ROLL,  getf(CURRENT_ROLL));
    }

    protected void updateCurrentValues(SensorEvent event) {
        getRotationMatrixFromVector(mRotationMatrix, event.values);
        getOrientation(mRotationMatrix, mOrientation);

        set(CURRENT_YAW,   mOrientation[0]);
        set(CURRENT_PITCH, mOrientation[1]);
        set(CURRENT_ROLL,  mOrientation[2]);
    }

    private void convertToDegrees(float[] vector) {
        for (int j = 0; j < vector.length; ++j) {
            vector[j] = round(toDegrees(vector[j]));
        }
    }
}
