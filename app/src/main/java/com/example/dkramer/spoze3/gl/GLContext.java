package com.example.dkramer.spoze3.gl;

/**
 * Created by dkramer on 10/20/17.
 */

public final class GLContext {
    private GLActivity mActivity;
    private GLView mGLView;

    public GLContext(GLActivity activity, GLView glView) {
        mActivity = activity;
        mGLView = glView;
    }

    public GLView getGLView() {
        return mGLView;
    }
}
