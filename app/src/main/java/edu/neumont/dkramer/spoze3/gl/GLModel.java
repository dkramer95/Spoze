package edu.neumont.dkramer.spoze3.gl;

import android.opengl.Matrix;

/**
 * Created by dkramer on 10/20/17.
 */

public abstract class GLModel {
    protected GLContext mGLContext;
    protected GLProgram mGLProgram;
    protected final GLVertexArray mVertexArray;
//    protected final GLTransformation mTransformation;

    // move this model into world space
    protected final float[] mModelMatrix = new float[16];

    // where this model exists and will be projected in the viewport
    protected final float[] mMVPMatrix = new float[16];


//    public GLModel() {
//        mVertexArray = new GLVertexArray(getVertexData());
//        mGLProgram = createGLProgram();
////        mTransformation = new GLTransformation();
//    }

    public GLModel(GLContext glContext) {
        mGLContext = glContext;
        mVertexArray = new GLVertexArray(getVertexData());
        mGLProgram = createGLProgram();
//        mTransformation = new GLTransformation();
    }

    protected abstract GLProgram createGLProgram();

    public abstract void render(GLCamera camera);

    public abstract float[] getVertexData();

    protected void applyTransformations() {
        Matrix.setIdentityM(mModelMatrix, 0);

//        Matrix.translateM(mModelMatrix, 0,
//                mTransformation.translateX,
//                mTransformation.translateY,
//                mTransformation.translateZ
//        );

//        Matrix.rotateM(mModelMatrix, 0,
//                mTransformation.rotationAngle,
//                mTransformation.rotateX,
//                mTransformation.rotateY,
//                mTransformation.rotateZ
//        );

//        Matrix.scaleM(mModelMatrix, 0,
//                mTransformation.scaleX,
//                mTransformation.scaleY,
//                mTransformation.scaleZ
//        );
    }

    public void translate(float x, float y, float z) {
//        mTransformation.translateX = x;
//        mTransformation.translateY = y;
//        mTransformation.translateZ = z;
    }

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

    public GLProgram getGLProgram() {
        return mGLProgram;
    }
}
