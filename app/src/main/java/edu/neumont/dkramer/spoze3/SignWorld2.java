package edu.neumont.dkramer.spoze3;

import android.graphics.Bitmap;
import android.util.Log;

import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo;
import edu.neumont.dkramer.spoze3.models.SignModel;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.TOUCH_INPUT;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_ACCEL_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.get;

/**
 * Created by dkramer on 11/3/17.
 */

public class SignWorld2 extends GLWorld implements GLDeviceInfo.OnUpdateListener {
	private GLPixelPicker mPixelPicker;

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

	@Override
	public void create() {
		// do in background because it is too slow to run on GLThread
//		SignModel.createInBackground(this, mBitmap, getWidth(), getHeight());
	}

	@Override
	public void onUpdate(GLDeviceInfo.Type type) {
		getGLContext().queueEvent(() -> {
			if (type == TOUCH_INPUT) {
				mPixelPicker.readPixel((int)get(CURRENT_TOUCH_X), (int)get(CURRENT_ACCEL_Y),
						getWidth(), getHeight());
			}
		});
	}
}
