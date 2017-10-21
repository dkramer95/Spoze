package com.example.dkramer.spoze3.gl;

import static android.opengl.GLES20.glViewport;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLCamera {

    public void refreshSize(int viewWidth, int viewHeight) {
        glViewport(0, 0, viewWidth, viewHeight);
    }
}
