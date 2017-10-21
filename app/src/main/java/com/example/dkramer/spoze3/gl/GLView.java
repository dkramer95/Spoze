package com.example.dkramer.spoze3.gl;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by dkramer on 10/20/17.
 */

public abstract class GLView extends GLSurfaceView {
    protected GLRenderer mRenderer;
    protected GLScene mScene;

    public GLView(Context context) {
        super(context);
    }

    public GLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        mScene = createScene();
        mRenderer = new GLRenderer(this);

        setEGLContextClientVersion(2);
        setZOrderOnTop(true);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        setRenderer(mRenderer);
    }

    /**
     * Method to be implemented that performs all the initialization work
     * to create a scene.
     */
    public abstract GLScene createScene();

    public GLScene getScene() {
        return mScene;
    }

    public GLRenderer getRenderer() {
        return mRenderer;
    }
}
