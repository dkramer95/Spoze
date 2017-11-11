package edu.neumont.dkramer.spoze3.gl;

import android.opengl.Matrix;

import edu.neumont.dkramer.spoze3.util.FPSCounter;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DITHER;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

/**
 * Created by dkramer on 10/20/17.
 */

public abstract class GLScene extends GLObject {
    private static final String TAG = "GLScene";

    protected final float[] mInvertedViewProjectionMatrix = new float[16];

    protected FPSCounter mFPSCounter;
    protected GLCamera mGLCamera;
    protected GLWorld mWorld;
    protected int mWidth;
    protected int mHeight;



    public GLScene(GLContext ctx) {
    	super(ctx);
        mGLCamera = createGLCamera();
        mWorld = createWorld();
        mFPSCounter = new FPSCounter();
    }

    public void init(int viewWidth, int viewHeight) {
        getWorld().setSize(viewWidth, viewHeight);
        getWorld().create();
    }

    public abstract GLWorld createWorld();

    public void render() {
        clearScreen();
        updateCamera();
        renderWorld();
        logFrame();
        Matrix.invertM(mInvertedViewProjectionMatrix, 0, mGLCamera.getProjectionMatrix(), 0);
    }

    protected void updateCamera() {
        mGLCamera.update();
    }

    protected void renderWorld() {
        mWorld.render(mGLCamera);
    }

    protected void logFrame() {
        mFPSCounter.logFrame();
    }

    public void refreshSize(int width, int height) {
        getWorld().setSize(width, height);
        getCamera().refreshSize(width, height);
    }

    protected void clearScreen() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DITHER);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0f, 0f, 0f, 0f);
        glDisable(GL_BLEND);
    }

    public GLWorld getWorld() {
        return mWorld;
    }

    protected GLCamera createGLCamera() {
        return GLCamera.getDefault(getGLContext());
    }

    public GLCamera getCamera() {
        return mGLCamera;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }


    public void stop() {
        getWorld().removeAllModels();
    }
}
