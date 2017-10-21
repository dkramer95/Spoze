package com.example.dkramer.spoze3.gl;

import android.opengl.Matrix;

import static android.opengl.GLES20.glViewport;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLCamera {
    // what our eye sees
    private float[] mViewMatrix = new float[16];

    // what the end user will see in the 2D viewport
    private float[] mProjectionMatrix = new float[16];

    // convert 3D back to 2D
    private float[] mInvertedViewProjectionMatrix = new float[16];

    // Eye position values
    private float mEyeX;
    private float mEyeY;
    private float mEyeZ;

    // where eye is looking in the distance
    private float mLookX;
    private float mLookY;
    private float mLookZ;

    // Where our eye is pointing
    private float mUpX;
    private float mUpY;
    private float mUpZ;

    // instantiate using static method presets
    private GLCamera() { }

    public static GLCamera getDefault() {
        GLCamera camera = new GLCamera();

        camera.mEyeX = 0.0f;
        camera.mEyeY = 0.0f;
        camera.mEyeZ = 1.5f;

        camera.mLookX = 0.0f;
        camera.mLookY = 1.0f;
        camera.mLookZ = -100.0f;

        camera.mUpX = 0.0f;
        camera.mUpY = 1.0f;
        camera.mUpZ = 0.0f;

        camera.updateViewMatrix();
        return camera;
    }


    public void translate(float x, float y, float z) {
        Matrix.translateM(mViewMatrix, 0, x, y, z);
    }

    private void updateViewMatrix() {
        Matrix.setLookAtM(
                mViewMatrix, 0,
                mEyeX, mEyeY, mEyeZ,
                mLookX, mLookY, mLookZ,
                mUpX, mUpY, mUpZ
        );
    }

    public void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(mViewMatrix, 0, angle, x, y, z);
    }

    public void zoom(float factor) {
        mEyeZ += factor;
        mLookZ += factor;
        updateViewMatrix();
    }

    public void refreshSize(int viewWidth, int viewHeight) {
        glViewport(0, 0, viewWidth, viewHeight);

        final float aspectRatio = (float) viewWidth / viewHeight;
        final float left = -aspectRatio;
        final float right = aspectRatio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 100.0f;

        Matrix.frustumM(
                mProjectionMatrix, 0,
                left, right, bottom, top,
                near, far
        );
    }

    public void update() {
//        mEyeX = mLookX + ((MotionData.calibratedYaw - MotionData.yaw) * -5f);
//		mEyeY = mLookY + ((MotionData.calibratedPitch - MotionData.pitch) * 5f);
        updateViewMatrix();
    }

//    private Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
//        // Convert normalized device coordinates into 3D world space coordinates
//        final float[] nearPointNdc = { normalizedX, normalizedY, -1, 1 };
//        final float[] farPointNdc = { normalizedX, normalizedY, 1, 1 };
//        final float[] nearPointWorld = new float[4];
//        final float[] farPointWorld = new float[4];
//
//        Matrix.multiplyMV(nearPointWorld, 0, mInvertedViewProjectionMatrix, 0, nearPointNdc, 0);
//        Matrix.multiplyMV(farPointWorld, 0, mInvertedViewProjectionMatrix, 0, farPointNdc, 0);
//
//        Point nearPointRay =
//                new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
//
//        Point farPointRay =
//                new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
//
//        return new Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
//    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    public void applyToModel(float[] modelMVPMatrix, float[] modelMatrix) {
        Matrix.multiplyMM(modelMVPMatrix, 0, mViewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelMVPMatrix, 0, mProjectionMatrix, 0, modelMVPMatrix, 0);
        Matrix.invertM(mInvertedViewProjectionMatrix, 0, mProjectionMatrix, 0);
    }

    public float[] getViewMatrix() {
        return mViewMatrix;
    }

    public float[] getProjectionMatrix() {
        return mProjectionMatrix;
    }

    public float[] getInvertedViewProjectionMatrix() {
        return mInvertedViewProjectionMatrix;
    }

    public float getEyeX() {
        return mEyeX;
    }

    public float getEyeY() {
        return mEyeY;
    }

    public float getEyeZ() {
        return mEyeZ;
    }

    public float getLookX() {
        return mLookX;
    }

    public float getLookY() {
        return mLookY;
    }

    public float getLookZ() {
        return mLookZ;
    }

    public float getUpX() {
        return mUpX;
    }

    public float getUpY() {
        return mUpY;
    }

    public float getUpZ() {
        return mUpZ;
    }
}
