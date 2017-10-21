package com.example.dkramer.spoze3.gl;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
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

public class GLScene {
    protected GLCamera mGLCamera;
    protected GLWorld mWorld;



    public GLScene(GLWorld world) {
        mGLCamera = GLCamera.getDefault();
        mWorld = world;
    }

    public void render() {
        clearScreen();
        // update camera
        mWorld.render(mGLCamera);
    }

    public void refreshSize(int width, int height) {
        mGLCamera.refreshSize(width, height);
    }

    protected void clearScreen() {
        glClear(GL_COLOR_BUFFER_BIT);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0f, 0f, 0f, 0f);
        glDisable(GL_BLEND);
    }

    public GLWorld getWorld() {
        return mWorld;
    }
}
