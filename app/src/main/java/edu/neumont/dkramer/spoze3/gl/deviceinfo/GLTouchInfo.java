package edu.neumont.dkramer.spoze3.gl.deviceinfo;

import android.view.MotionEvent;
import android.view.View;

import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.models.GLPoint;

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

    public GLTouchInfo(GLContext glContext) {
        super(glContext);
    }

    @Override
    public void start() {
        getGLContext().getGLView().setOnTouchListener(this);
    }

    @Override
    public void stop() {
        getGLContext().getGLView().setOnTouchListener(null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        updatePreviousValues();
        updateCurrentValues(v, e);
        //TODO notify of touch event... somewhere

//        GLView view = getGLContext().getGLView();
//        GLScene scene = view.getScene();
//        view.queueEvent(() -> scene.handleTouchPress(normalizedX, normalizedY));
        return true;
    }

    protected void updatePreviousValues() {
        set(LAST_TOUCH_X, get(CURRENT_TOUCH_X));
        set(LAST_TOUCH_Y, get(CURRENT_TOUCH_Y));

        set(LAST_TOUCH_NORMALIZED_X, get(CURRENT_TOUCH_NORMALIZED_X));
        set(LAST_TOUCH_NORMALIZED_Y, get(CURRENT_TOUCH_NORMALIZED_Y));
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
