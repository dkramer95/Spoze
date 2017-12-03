package edu.neumont.dkramer.spoze3.gl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLView extends GLSurfaceView {
    protected GLContext mGLContext;
    protected GLRenderer mRenderer;
    protected GLScene mScene;

    public GLView(Context context) {
        super(context);
    }

    public GLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(GLContext glContext) {
        mGLContext = glContext;
        mGLContext.setGLView(this);
        mRenderer = new GLRenderer(this);

        setEGLContextClientVersion(2);
        setZOrderMediaOverlay(true);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        setRenderer(mRenderer);
    }

    public GLScene getScene() {
        return mScene;
    }

    public GLRenderer getRenderer() {
        return mRenderer;
    }

    public GLContext getGLContext() {
        return mGLContext;
    }

    public void setScene(GLScene scene) {
        mScene = scene;
    }

    public void stop() {
        // stop continuous rendering!
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        getScene().stop();
    }

    public void destroy() {
        getScene().destroy();
    }
}
