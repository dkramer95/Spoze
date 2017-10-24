package edu.neumont.dkramer.spoze3.gl;

/**
 * Created by dkramer on 10/20/17.
 */

public abstract class GLDeviceInfo {
    protected GLContext mGLContext;

    public GLDeviceInfo(GLContext glContext) {
        mGLContext = glContext;
    }

    public abstract void start();
    public abstract void stop();

    public GLContext getGLContext() {
        return mGLContext;
    }
}
