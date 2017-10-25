package edu.neumont.dkramer.spoze3.gl;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLTouchInfo extends GLDeviceInfo implements GLView.OnTouchListener {
    /* TYPE_TOUCH_INPUT prepended to prevent integer key collision in map */

    /*
     * Constants indicating previous raw values
     */
    public static final int VALUE_LAST_TOUCH_X = (TYPE_TOUCH_INPUT + 0x00);
    public static final int VALUE_LAST_TOUCH_Y = (TYPE_TOUCH_INPUT + 0x01);

    /*
     * Constants indicating previous normalized values
     */
    public static final int VALUE_LAST_TOUCH_NORMALIZED_X = (TYPE_TOUCH_INPUT + 0x03);
    public static final int VALUE_LAST_TOUCH_NORMALIZED_Y = (TYPE_TOUCH_INPUT + 0x04);

    /*
     * Constants indicating current raw values
     */
    public static final int VALUE_CURRENT_TOUCH_X = (TYPE_TOUCH_INPUT + 0x05);
    public static final int VALUE_CURRENT_TOUCH_Y = (TYPE_TOUCH_INPUT + 0x06);

    /*
     * Constants indicating current normalized values
     */
    public static final int VALUE_CURRENT_TOUCH_NORMALIZED_X = (TYPE_TOUCH_INPUT + 0x07);
    public static final int VALUE_CURRENT_TOUCH_NORMALIZED_Y = (TYPE_TOUCH_INPUT + 0x08);




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
        set(VALUE_CURRENT_TOUCH_NORMALIZED_X, 0f);
        set(VALUE_CURRENT_TOUCH_NORMALIZED_Y, 0f);
        set(VALUE_CURRENT_TOUCH_X, 0f);
        set(VALUE_CURRENT_TOUCH_Y, 0f);
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
        set(VALUE_LAST_TOUCH_X, get(VALUE_CURRENT_TOUCH_X));
        set(VALUE_LAST_TOUCH_Y, get(VALUE_CURRENT_TOUCH_Y));
        set(VALUE_LAST_TOUCH_NORMALIZED_X, get(VALUE_CURRENT_TOUCH_NORMALIZED_X));
        set(VALUE_LAST_TOUCH_NORMALIZED_Y, get(VALUE_CURRENT_TOUCH_NORMALIZED_Y));
    }

    protected void updateCurrentValues(View v, MotionEvent e) {
        final float x = e.getX();
        final float y = e.getY();

        final float normalizedX =   (x / (float) v.getWidth()) * 2 - 1;
        final float normalizedY = -((y / (float) v.getHeight()) * 2 - 1);

        set(VALUE_CURRENT_TOUCH_X, x);
        set(VALUE_CURRENT_TOUCH_Y, y);
        set(VALUE_CURRENT_TOUCH_NORMALIZED_X, normalizedX);
        set(VALUE_CURRENT_TOUCH_NORMALIZED_Y, normalizedY);
    }
}
