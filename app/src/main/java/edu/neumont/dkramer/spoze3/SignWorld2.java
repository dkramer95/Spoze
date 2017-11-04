package edu.neumont.dkramer.spoze3;

import android.util.Log;

import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.TOUCH_INPUT;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.getf;

/**
 * Created by dkramer on 11/3/17.
 */

public abstract class SignWorld2 extends GLWorld implements GLDeviceInfo.OnUpdateListener {
	protected GLPixelPicker mPixelPicker;




	public SignWorld2(GLContext glContext) {
		super(glContext);
		init();
	}

	protected void init() {
		mPixelPicker = new GLPixelPicker();
		mPixelPicker.setOnPixelReadListener((p) -> {
			// check to see if we hit any of our models
			Log.i("PIXEL_PICKER", "Pixel Read: " + Integer.toHexString(p));
		});
		getGLContext().getDeviceInfo(TOUCH_INPUT).addOnUpdateListener(this);
	}

	/* When we receive a touch, check to see if we hit any of our models.
	If we did, that model becomes active. All further touch events are delegated to that model
	Upon touch release, the model is cleared out and the next touch event will read pixels.
	 */

	@Override
	public void onUpdate(GLDeviceInfo.Type type) {
		getGLContext().queueEvent(() -> {
			if (type == TOUCH_INPUT) {
				mPixelPicker.readPixel((int) getf(CURRENT_TOUCH_X), (int) getf(CURRENT_TOUCH_Y),
						getWidth(), getHeight());
			}
		});
	}
}
