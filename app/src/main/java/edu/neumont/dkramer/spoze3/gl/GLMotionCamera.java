package edu.neumont.dkramer.spoze3.gl;

import android.util.Log;

import edu.neumont.dkramer.spoze3.util.Preferences;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CALIBRATED_PITCH;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CALIBRATED_YAW;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_PITCH;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_YAW;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.getf;

/**
 * Created by dkramer on 10/25/17.
 */

public class GLMotionCamera extends GLCamera {
	protected boolean mUpdateMotion;

	protected GLMotionCamera(GLContext ctx) {
		super(ctx);
		mUpdateMotion = true;
	}

	@Override
	public void update() {
		if (mUpdateMotion) {
			mEye.x = (mLook.x + (getf(CALIBRATED_YAW) - getf(CURRENT_YAW)) * -3f);
			mEye.y = (mLook.y + (getf(CALIBRATED_PITCH) - getf(CURRENT_PITCH)) * 3f);
			Log.i("GL_MOTION", String.format("EyeX: %f, EyeY: %f, EyeZ: %f\n", mEye.x, mEye.y, mEye.z));
		}
		super.update();
	}

	public static GLCamera getDefault(GLContext ctx) {
		GLCamera camera = new GLMotionCamera(ctx);
		camera.setDefaults();
		camera.updateViewMatrix();
		return camera;
	}

	public void setUpdateMotion(boolean value) {
		mUpdateMotion = value;
	}
}
