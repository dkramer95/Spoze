package com.example.dkramer.spoze3.gl;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLTouchInfo extends GLDeviceInfo implements GLView.OnTouchListener {


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
        final float normalizedX =
                (e.getX() / (float) v.getWidth()) * 2 - 1;

        final float normalizedY =
                -((e.getY() / (float) v.getHeight()) * 2 - 1);

//        Log.i("GLTouchInfo", String.format("X: %f, Y: %f\n", normalizedX, normalizedY));

        GLView view = getGLContext().getGLView();
        GLScene scene = view.getScene();
        view.queueEvent(() -> scene.handleTouchPress(normalizedX, normalizedY));
        return true;
    }
}
