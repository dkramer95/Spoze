package edu.neumont.dkramer.spoze3;

import android.icu.util.VersionInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ViewFlipper;

import edu.neumont.dkramer.spoze3.gl.GLActivity;
import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLModel;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLTouchInfo;
import edu.neumont.dkramer.spoze3.models.SignModel;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.TOUCH_INPUT;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_NORMALIZED_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_NORMALIZED_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.get;

/**
 * Created by dkramer on 11/3/17.
 */

public class TouchableWorld extends GLWorld implements View.OnTouchListener, GLPixelPicker.OnPixelReadListener {
	protected GLPixelPicker mPixelPicker;

	// the model that has been touched most recently
	protected SignModel mActiveModel;




	public TouchableWorld(GLContext glContext) {
		super(glContext);
		init();
	}

	protected void init() {
		mPixelPicker = new GLPixelPicker();
		mPixelPicker.setOnPixelReadListener(this);
	}

	@Override
	public void create() {
		GLTouchInfo info = (GLTouchInfo)getGLContext().getDeviceInfo(TOUCH_INPUT);
		info.addOnTouchListener(this);
	}

	protected void checkModelSelection() {
		getGLContext().queueEvent(() -> {
			mPixelPicker.readPixel((int)get(CURRENT_TOUCH_X), (int)get(CURRENT_TOUCH_Y), getWidth(), getHeight());
		});
	}

	protected void moveModel() {
		Log.i("INFO", "Moving model: " + mActiveModel.getId());
		mActiveModel.setTranslate(get(CURRENT_TOUCH_NORMALIZED_X), get(CURRENT_TOUCH_NORMALIZED_Y));
	}

	public void onPixelRead(int pixel) {
		Log.i("INFO", "Pixel value = " + pixel);
		for (GLModel model : mModels) {
			SignModel signModel = (SignModel)model;
			if (signModel.didTouch(pixel)) {
				sendModelToFront(signModel);
				updateToolbar(0);
				mActiveModel = signModel;
				break;
			}
		}
	}

	protected void updateToolbar(int num) {
		GLActivity activity = getGLContext().getActivity();
		activity.runOnUiThread(() -> {
			ViewFlipper flipper = activity.findViewById(R.id.toolbarFlipper);
			flipper.setDisplayedChild(num);
		});
	}

	float angle = 0.0f;
	@Override
	public void render(GLCamera camera) {
//		camera.rotate(angle, 1, 1, 0);
//		++angle;
		super.render(camera);
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		final int action = e.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				checkModelSelection();
				break;
			case MotionEvent.ACTION_MOVE:
				if (mActiveModel != null) {
					moveModel();
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mActiveModel != null) {
					mActiveModel = null;
					updateToolbar(1);
				}
				break;
		}
		return true;
	}
}
