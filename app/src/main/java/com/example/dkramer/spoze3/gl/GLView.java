package com.example.dkramer.spoze3.gl;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.example.dkramer.spoze3.MyGLWorld2;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLView extends GLSurfaceView {
    protected GLContext mGLContext;
    protected GLRenderer mRenderer;
    protected GLScene mScene;

    public GLView(Context context) {
        super(context);
//        mGLContext = (GLContext)context;
    }

    public GLView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        mGLContext = (GLContext)context;
    }

    public void init(GLContext glContext) {
        mGLContext = glContext;
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
    public GLScene createScene() {
        GLScene scene = new GLScene(new MyGLWorld2(getGLContext()));
        return scene;
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
}
