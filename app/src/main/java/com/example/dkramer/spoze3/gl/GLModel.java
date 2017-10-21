package com.example.dkramer.spoze3.gl;

/**
 * Created by dkramer on 10/20/17.
 */

public abstract class GLModel {
    protected GLContext mGLContext;
    protected GLProgram mGLProgram;
    protected final GLVertexArray mVertexArray;

    // move this model into world space
    protected final float[] mModelMatrix = new float[16];

    // where this model exists and will be projected in the viewport
    protected final float[] mMVPMatrix = new float[16];


    public GLModel(GLContext glContext) {
        mGLContext = glContext;
        mVertexArray = new GLVertexArray(getVertexData());
        mGLProgram = createGLProgram();
    }

    protected abstract GLProgram createGLProgram();

    public abstract void render(GLCamera camera);

    public abstract float[] getVertexData();


    public float[] getModelMatrix() {
        return mModelMatrix;
    }

    public float[] getMVPMatrix() {
        return mMVPMatrix;
    }

    public GLVertexArray getVertexArray() {
        return mVertexArray;
    }

    public GLContext getGLContext() {
        return mGLContext;
    }
}
