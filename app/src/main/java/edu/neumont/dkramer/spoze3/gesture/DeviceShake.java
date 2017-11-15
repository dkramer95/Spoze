package edu.neumont.dkramer.spoze3.gesture;

import edu.neumont.dkramer.spoze3.gl.GLActivity;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ACCELEROMETER;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_ACCEL_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.getf;

/**
 * Created by dkramer on 11/12/17.
 */

public class DeviceShake implements GLDeviceInfo.OnUpdateListener {
	// TODO make this adjustable in settings
    private static final float SHAKE_THRESHOLD = 15f;

    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private OnShakeListener mOnShakeListener = () -> { };
    private GLActivity mActivity;


    public DeviceShake(GLActivity activity, OnShakeListener listener) {
        activity.getGLContext().getDeviceInfo(ACCELEROMETER).addOnUpdateListener(this);
        mOnShakeListener = listener;
        mActivity = activity;
    }


    @Override
    public void onUpdate(GLDeviceInfo.Type sender) {
        if (sender == ACCELEROMETER) {
            float x = getf(CURRENT_ACCEL_X);
            float y = getf(CURRENT_ACCEL_X);
            float z = getf(CURRENT_ACCEL_X);

            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float)Math.sqrt(((x * x) + (y * y) + (z * z)));

            float delta = (mAccelCurrent - mAccelLast);
            mAccel = (mAccel * 0.9f + delta);

            if (mAccel > SHAKE_THRESHOLD) {
                mOnShakeListener.onShake();
                mAccel = 0;
            }
        }
    }

    public void setOnShakeListener(OnShakeListener listener) {
        mOnShakeListener = listener;
    }

    public void stopListening() {
        mActivity.getGLContext().getDeviceInfo(ACCELEROMETER).removeOnUpdateListener(this);
    }


    /* Callback interface for responding to shake events */
    public interface OnShakeListener {
        void onShake();
    }
}
