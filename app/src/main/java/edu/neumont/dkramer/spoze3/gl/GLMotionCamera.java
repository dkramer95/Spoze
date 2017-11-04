package edu.neumont.dkramer.spoze3.gl;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CALIBRATED_PITCH;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CALIBRATED_YAW;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_PITCH;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_YAW;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.getf;

/**
 * Created by dkramer on 10/25/17.
 */

public class GLMotionCamera extends GLCamera {

	protected GLMotionCamera(GLContext ctx) {
		super(ctx);
	}

	@Override
	public void update() {
//		//TODO apply gyro values to camera matrix
//		float yaw = GLDeviceInfo.getf(CURRENT_YAW);
//		float pitch = GLDeviceInfo.getf(CURRENT_PITCH);
//		float roll = GLDeviceInfo.getf(CURRENT_ROLL);
//
//		Log.i("GLMotionCamera", String.format("Yaw = %f, Pitch = %f, Roll = %f", yaw, pitch, roll));

//		mEye.x = (mLook.x + (getf(CURRENT_ACCEL_Z) * -1f));

        //TODO check orientation to determine offset value... instead of magic 5
		mEye.x = (mLook.x + (getf(CALIBRATED_YAW) - getf(CURRENT_YAW)) * -5f);
		mEye.y = (mLook.y + (getf(CALIBRATED_PITCH) - getf(CURRENT_PITCH)) * 5f);

		super.update();
	}

	public static GLCamera getDefault(GLContext ctx) {
		GLCamera camera = new GLMotionCamera(ctx);
		camera.setDefaults();
		camera.updateViewMatrix();
		return camera;
	}
}
