package edu.neumont.dkramer.spoze3.gl;

import android.opengl.Matrix;

import edu.neumont.dkramer.spoze3.geometry.Point3f;
import edu.neumont.dkramer.spoze3.geometry.Vector3f;

import static android.opengl.GLES20.glViewport;

/**
 * Created by dkramer on 10/21/17.
 */

public class GLCamera {
    // what our eye sees
    protected final float[] mViewMatrix = new float[16];

    // what user sees in 2D device viewport
    protected final float[] mProjectionMatrix = new float[16];

    // where our eye is positioned
    protected Point3f mEye;

    // where our eye is looking in the distance
    protected Point3f mLook;

    // where our eye is pointing
    protected Vector3f mUp;


    // instantiate using static method presets
    private GLCamera() { }

    public static GLCamera getDefault() {
        GLCamera camera = new GLCamera();
        camera.mEye = new Point3f(0.0f, 0.0f, 1.0f);
        camera.mLook = new Point3f(0.0f, 1.0f, -100.0f);
        camera.mUp = new Vector3f(0.0f, 1.0f, 0.0f);
        camera.setZoomFactor(1f);

        camera.updateViewMatrix();
        return camera;
    }

    protected void updateViewMatrix() {
        Matrix.setLookAtM(
            mViewMatrix, 0,
            mEye.x, mEye.y, mEye.z,
            mLook.x, mLook.y, mLook.z,
            mUp.x, mUp.y, mUp.z
        );
    }

    public void setZoomFactor(float zoomFactor) {
        mEye.z += zoomFactor;
        mLook.z += zoomFactor;
    }

    public void refreshSize(final int viewWidth, final int viewHeight) {
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

    public void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(mViewMatrix, 0, angle, x, y, z);
    }

    public void applyToModel(GLModel model) {
        Matrix.multiplyMM(model.getMVPMatrix(), 0, getViewMatrix(), 0, model.getModelMatrix(), 0);
        Matrix.multiplyMM(model.getMVPMatrix(), 0, getProjectionMatrix(), 0, model.getMVPMatrix(), 0);
    }

    public void update() {
        updateViewMatrix();
    }

    public float[] getViewMatrix() {
        return mViewMatrix;
    }

    public float[] getProjectionMatrix() {
        return mProjectionMatrix;
    }

}
