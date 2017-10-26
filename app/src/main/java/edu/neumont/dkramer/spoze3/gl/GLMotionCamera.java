package edu.neumont.dkramer.spoze3.gl;

import android.util.Log;

import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_PITCH;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_ROLL;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_YAW;

/**
 * Created by dkramer on 10/25/17.
 */

public class GLMotionCamera extends GLCamera {

	protected GLMotionCamera(GLContext ctx) {
		super(ctx);
	}

	@Override
	public void update() {
		//TODO apply gyro values to camera matrix
		float yaw = GLDeviceInfo.get(CURRENT_YAW);
		float pitch = GLDeviceInfo.get(CURRENT_PITCH);
		float roll = GLDeviceInfo.get(CURRENT_ROLL);

		Log.i("GLMotionCamera", String.format("Yaw = %f, Pitch = %f, Roll = %f", yaw, pitch, roll));

		super.update();
	}

	public static GLCamera getDefault(GLContext ctx) {
		GLCamera camera = new GLMotionCamera(ctx);
		camera.setDefaults();
		camera.updateViewMatrix();
		return camera;
	}
}
