package edu.neumont.dkramer.spoze3.gl;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_ACCEL_Z;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.get;

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
//		float yaw = GLDeviceInfo.get(CURRENT_YAW);
//		float pitch = GLDeviceInfo.get(CURRENT_PITCH);
//		float roll = GLDeviceInfo.get(CURRENT_ROLL);
//
//		Log.i("GLMotionCamera", String.format("Yaw = %f, Pitch = %f, Roll = %f", yaw, pitch, roll));

//		mEye.x = (mLook.x + (get(CURRENT_ACCEL_Z) * -1f));

		super.update();
	}

	public static GLCamera getDefault(GLContext ctx) {
		GLCamera camera = new GLMotionCamera(ctx);
		camera.setDefaults();
		camera.updateViewMatrix();
		return camera;
	}
}
