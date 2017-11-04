package edu.neumont.dkramer.spoze3.gl.deviceinfo;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import edu.neumont.dkramer.spoze3.gl.GLContext;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_NORMALIZED_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_NORMALIZED_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.LAST_TOUCH_NORMALIZED_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.LAST_TOUCH_NORMALIZED_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.LAST_TOUCH_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.LAST_TOUCH_Y;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLTouchInfo extends GLDeviceInfo implements View.OnTouchListener {
    /*
     * Work around limitation of single touch listener for a view when
     * when this class is used.
     */
    protected List<View.OnTouchListener> mTouchListeners;



    public GLTouchInfo(GLContext glContext) {
        super(glContext);
        mTouchListeners = new ArrayList<>();
    }

    @Override
    public void start() {
        getGLContext().getGLView().setOnTouchListener(this);
    }

    @Override
    public void stop() {
        getGLContext().getGLView().setOnTouchListener(null);
        mTouchListeners.clear();
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        updatePreviousValues();
        updateCurrentValues(v, e);

        //TODO this might not be necessary since we are composing OnTouchListeners instead..
        //TODO Maybe remove from superclass
	    notifyUpdateListeners();

	    notifyTouchListeners(v, e);
        return true;
    }

    public void addOnTouchListener(View.OnTouchListener listener) {
        mTouchListeners.add(listener);
    }

    protected void notifyTouchListeners(View v, MotionEvent e) {
        for (View.OnTouchListener listener : mTouchListeners) {
            listener.onTouch(v, e);
        }
    }

    protected void updatePreviousValues() {
        set(LAST_TOUCH_X, getf(CURRENT_TOUCH_X));
        set(LAST_TOUCH_Y, getf(CURRENT_TOUCH_Y));

        set(LAST_TOUCH_NORMALIZED_X, getf(CURRENT_TOUCH_NORMALIZED_X));
        set(LAST_TOUCH_NORMALIZED_Y, getf(CURRENT_TOUCH_NORMALIZED_Y));
    }

    protected void updateCurrentValues(View v, MotionEvent e) {
        final float x = e.getX();
        final float y = e.getY();

        set(CURRENT_TOUCH_X, x);
        set(CURRENT_TOUCH_Y, y);

        final float normalizedX =   (x / (float) v.getWidth()) * 2 - 1;
        final float normalizedY = -((y / (float) v.getHeight()) * 2 - 1);

        set(CURRENT_TOUCH_NORMALIZED_X, normalizedX);
        set(CURRENT_TOUCH_NORMALIZED_Y, normalizedY);
    }
}
