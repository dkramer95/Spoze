package edu.neumont.dkramer.spoze3;

import android.widget.ViewFlipper;

import edu.neumont.dkramer.spoze3.gl.GLActivity;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLModel;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.models.SignModel;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_NORMALIZED_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_NORMALIZED_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.getf;

/**
 * Created by dkramer on 11/3/17.
 */

public class TouchableWorld extends GLWorld implements TouchSelectionHandler.OnModelSelectionListener {
    protected static final int TOOLBAR_OBJECT = 0;
	protected static final int TOOLBAR_NORMAL = 1;


	protected TouchSelectionHandler mTouchHandler;
	protected SignModel mSelectedModel;



	public TouchableWorld(GLContext glContext) {
		super(glContext);
	}

	@Override
	public void create() {
		super.create();
		mTouchHandler = new TouchSelectionHandler(this);
		mTouchHandler.setOnModelSelectionListener(this);
	}

	@Override
	public void onFirstSelect(GLModel model) {
		mSelectedModel = (SignModel)model;
		sendModelToFront(model);
		updateToolbar(TOOLBAR_OBJECT);
	}

	@Override
	public void onContinuousSelect(GLModel model) {
		model.translate(getf(CURRENT_TOUCH_NORMALIZED_X), getf(CURRENT_TOUCH_NORMALIZED_Y), 0);
	}

	@Override
	public void onDeselect(GLModel model) {
		mSelectedModel = null;
		updateToolbar(TOOLBAR_NORMAL);
	}

	protected void updateToolbar(int num) {
		GLActivity activity = getGLContext().getActivity();
		activity.runOnUiThread(() -> {
			ViewFlipper flipper = activity.findViewById(R.id.toolbarFlipper);
			flipper.setDisplayedChild(num);
		});
	}

	public SignModel getSelectedModel() {
		return mSelectedModel;
	}
}
