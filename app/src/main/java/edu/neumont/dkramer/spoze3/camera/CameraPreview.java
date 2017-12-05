package edu.neumont.dkramer.spoze3.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CaptureRequest;
import android.util.AttributeSet;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dkramer on 10/24/17.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.OnCaptureRequestConfigure {
    protected static final int CAMERA_TYPE_UNDEFINED = -1;



    // map containing configuration flags for our camera
    protected Map<CaptureRequest.Key<Integer>, Integer> mPreviewOptions = new HashMap<>();

    protected Camera mCamera;
    protected int mCameraType = CAMERA_TYPE_UNDEFINED;

    // preferred size, not necessary the size obtained from this view
    protected Size mPreferredSize;




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
        if (mCameraType == CAMERA_TYPE_UNDEFINED) {
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

    public Bitmap getBitmap() {
        return null;
    }

    protected int getCameraType() {
        return mCameraType;
    }

    protected Size getOptimalPreviewSize() {
        Size[] sizes = mCamera.getAvailablePreviewSizes();
        return Camera.getOptimalPreviewSize(getPreviewWidth(), getPreviewHeight(), sizes);
    }

    protected void loadCamera(SurfaceHolder holder) {
        try {
            attachCamera(holder);
            openCamera();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set a specific CaptureRequest option
     * @param key
     * @param value
     */
    public void setPreviewOption(CaptureRequest.Key<Integer> key, Integer value) {
        mPreviewOptions.put(key, value);
    }

    public void setPreferredSize(Size size) {
        mPreferredSize = size;
    }

    public int getPreviewWidth() {
        return mPreferredSize != null ? mPreferredSize.getWidth() : super.getWidth();
    }

    public int getPreviewHeight() {
        return mPreferredSize != null ? mPreferredSize.getHeight() : super.getHeight();
    }

    @SuppressLint("NewApi")
    @Override
    public void onConfigure(CaptureRequest.Builder requestBuilder) {
        mPreviewOptions.forEach((k, v) -> {
            requestBuilder.set(k, v);
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        loadCamera(holder);
    }

    protected void attachCamera(SurfaceHolder holder) throws CameraAccessException {
        mCamera = Camera.acquire(getContext(), getCameraType())
                .setOnCaptureRequestListener(this)
                .addTarget(holder.getSurface());

        Size size = getOptimalPreviewSize();
        holder.setFixedSize(size.getWidth(), size.getHeight());
    }

    protected void openCamera() throws CameraAccessException {
        mCamera.open();
    }

    // unused
    @Override
    public void surfaceChanged(SurfaceHolder holder, int i, int i1, int i2) {
//        if (!mCamera.isOpen()) {
//            loadCamera(holder);
//        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { }
}
