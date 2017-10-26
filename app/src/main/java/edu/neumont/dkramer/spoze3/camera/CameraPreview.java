package edu.neumont.dkramer.spoze3.camera;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.util.AttributeSet;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by dkramer on 10/24/17.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    protected static final int CAMERA_TYPE_UNDEFINED = -1;

    protected Camera mCamera;
    protected int mCameraType = CAMERA_TYPE_UNDEFINED;




    public CameraPreview(Context context) {
        super(context);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCameraType(int cameraType) {
        mCameraType = cameraType;
    }

    public void startPreviewing() {
        if (mCameraType == -1) {
            throw new IllegalStateException("Must call setCameraType() before previewing!");
        }
        getHolder().addCallback(this);
    }

    public void stopPreviewing() {
        if (mCamera != null) {
            try {
                mCamera.close();
                mCamera = null;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        getHolder().removeCallback(this);
    }

    protected int getCameraType() {
        return mCameraType;
    }

    protected Size getOptimalPreviewSize() {
        Size[] sizes = mCamera.getAvailablePreviewSizes();
        return Camera.getOptimalPreviewSize(getWidth(), getHeight(), sizes);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            attachCamera(holder);
            openCamera();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void attachCamera(SurfaceHolder holder) throws CameraAccessException {
        mCamera = Camera.acquire(getContext(), getCameraType());
        Size size = getOptimalPreviewSize();
        holder.setFixedSize(size.getWidth(), size.getHeight());
        mCamera.addTarget(holder.getSurface());
    }

    protected void openCamera() throws CameraAccessException {
        mCamera.open();
    }

    // unused
    @Override
    public void surfaceChanged(SurfaceHolder holder, int i, int i1, int i2) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { }
}
