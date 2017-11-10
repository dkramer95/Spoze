package edu.neumont.dkramer.spoze3.gl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.neumont.dkramer.spoze3.GLPixelPicker;
import edu.neumont.dkramer.spoze3.TouchSelectionHandler;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLRenderer implements GLView.Renderer {
    protected GLView mView;
    protected GLScene mScene;


    public GLRenderer(GLView view) {
        mView = view;
        mScene = view.getScene();
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig eglConfig) {
        mScene.refreshSize(mView.getWidth(), mView.getHeight());
        mScene.getWorld().create();
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        mScene.refreshSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        mScene.render();
    }
}
