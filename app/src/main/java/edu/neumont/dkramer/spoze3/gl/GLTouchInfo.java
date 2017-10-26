package edu.neumont.dkramer.spoze3.gl;

import android.view.MotionEvent;
import android.view.View;

import static edu.neumont.dkramer.spoze3.gl.GLDeviceInfo.Value.CURRENT_TOUCH_X;
import static edu.neumont.dkramer.spoze3.gl.GLDeviceInfo.Value.CURRENT_TOUCH_Y;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLTouchInfo extends GLDeviceInfo implements GLView.OnTouchListener {

    public GLTouchInfo(GLContext glContext) {
        super(glContext);
        initValues();
    }

    @Override
    public void start() {
        getGLContext().getGLView().setOnTouchListener(this);
    }

    @Override
    public void stop() {
        getGLContext().getGLView().setOnTouchListener(null);
    }

    protected void initValues() {
    	// initialize values that will not exist the first time they are updated
        set(CURRENT_TOUCH_X, 0);
        set(CURRENT_TOUCH_Y, 0);
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
        set(Value.LAST_TOUCH_X, get(CURRENT_TOUCH_X));
        set(Value.LAST_TOUCH_Y, get(CURRENT_TOUCH_Y));
    }

    protected void updateCurrentValues(View v, MotionEvent e) {
        final float x = e.getX();
        final float y = e.getY();

        // TODO MAYBE move this to be handled elsewhere and derive it from stored x, y values
//        final float normalizedX =   (x / (float) v.getWidth()) * 2 - 1;
//        final float normalizedY = -((y / (float) v.getHeight()) * 2 - 1);

        set(CURRENT_TOUCH_X, x);
        set(CURRENT_TOUCH_Y, y);
    }
}
