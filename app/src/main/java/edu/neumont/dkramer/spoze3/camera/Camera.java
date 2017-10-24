package edu.neumont.dkramer.spoze3.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.NonNull;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dkramer on 9/18/17.
 */

public class Camera {
    public static final int PERMISSION_CODE = 1623;

    // Constant indicating the format that this camera initially captures in
    public static final int CAPTURE_FORMAT = ImageFormat.YUV_420_888;

    // Constant indicating a device's front-facing camera
    public static final int CAM_FRONT = CameraCharacteristics.LENS_FACING_FRONT;

    // Constant indicating a device's rear-facing camera
    public static final int CAM_REAR = CameraCharacteristics.LENS_FACING_BACK;



    // has this camera been opened yet?
    private boolean mOpenFlag;

    // is this camera currently capturing?
    private boolean mCapturingFlag;

    // has an error occurred with this camera
    private boolean mErrorFlag;

    // ID of our physical camera device
    private String mId;

    // underlying physical camera device that this class is wrapping
    private CameraDevice mCameraDevice;

    // manager that handles accessing our physical camera device
    private CameraManager mCameraManager;

    // all possible outputs that we can be sending our preview frames to
    private List<Surface> mOutputTargets = new ArrayList<>();

    // map containing configuration flags for our camera
    private Map<CaptureRequest.Key<Integer>, Integer> mPreviewOptions = new HashMap<>();

    // our current camera capture session
    private CameraCaptureSession mCaptureSession;



    // don't instantiate directly, instead acquire using static method
    private Camera() { }

    public static Camera acquire(Context ctx, int cameraType) throws CameraAccessException {
        Camera camera = new Camera();

        CameraManager cameraManager =
                (CameraManager)ctx.getSystemService(Context.CAMERA_SERVICE);

        camera.mCameraManager = cameraManager;
        camera.mId = getCorrectCameraId(cameraManager, cameraType);
        return camera;
    }

    /**
     * Adds a surface target to display image data to
     * @param surface
     */
    public void addTarget(Surface surface) {
        mOutputTargets.add(surface);
    }

    /**
     * Creates a new capture session for our camera device.
     */
    private void createCaptureSession() {
        if (mCameraDevice == null) {
            throw new IllegalStateException("Cannot create a capture session with a null camera!");
        }
        try {
            mCameraDevice.createCaptureSession(mOutputTargets, new CameraCaptureSessionCallback(), null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the camera.
     * @throws CameraAccessException
     * @throws SecurityException
     */
    public void open() throws CameraAccessException, SecurityException {
        if (mOpenFlag) {
            throw new IllegalStateException("Camera is already open!");
        }
        mCameraManager.openCamera(mId, new CameraStateCallback(), null);
    }

    /**
     * Closes out an active camera
     * @throws CameraAccessException
     */
    public void close() throws CameraAccessException {
        if (mOpenFlag) {
            if (mCaptureSession != null) {
                mCaptureSession.stopRepeating();
                mCaptureSession.close();
                mCapturingFlag = false;
            }
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
                mOpenFlag = false;
            }
            mOutputTargets.clear();
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

    /**
     * Returns an array of all possible preview sizes for our active camera
     * @return
     */
    public Size[] getAvailablePreviewSizes() {
        Size[] sizes = null;
        try {
            CameraCharacteristics cameraCharacteristics =
                    mCameraManager.getCameraCharacteristics(mId);
            StreamConfigurationMap configMap =
                    cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            sizes = configMap.getOutputSizes(SurfaceHolder.class);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return sizes;
    }

    /**
     * Finds the optimal preview size that best matches the specified width and height
     * @param width target width
     * @param height target height
     * @param availableSizes all possible sizes
     * @return best possible size, otherwise the first size in the array
     */
    public static Size getOptimalPreviewSize(int width, int height, Size[] availableSizes) {
        if (availableSizes == null) {
            throw new NullPointerException("Sizes array must not be null!");
        }

        List<Size> sizeCandidates = new ArrayList<>();
        for(Size size : availableSizes) {
            // check for exact match, as it will be the most optimal
            if (size.getWidth() == width && size.getHeight() == height ||
                    size.getWidth() == height && size.getHeight() == width) {
                return size;
            }

            if (width > height) {
                if (size.getWidth() > width && size.getHeight() > height) {
                    sizeCandidates.add(size);
                }
            } else {
                if (size.getWidth() > height && size.getHeight() > width) {
                    sizeCandidates.add(size);
                }
            }
        }
        // find best match in size candidates if there are some
        if (!sizeCandidates.isEmpty()) {
            return Collections.min(sizeCandidates, (a, b) ->
                    Long.signum(a.getWidth() * a.getHeight() - b.getWidth() * b.getHeight())
            );
        }
        // otherwise just return first available preview size
        return availableSizes[0];
    }

    /**
     * Helper method for finding the correct ID of the camera that we want to use for
     * our CameraStream
     * @param cameraManager
     * @param cameraToUse
     * @return String containing ID of desired camera if found, otherwise null
     * @throws CameraAccessException
     */
    private static String getCorrectCameraId(CameraManager cameraManager, int cameraToUse) throws CameraAccessException {
        String[] cameraIds = cameraManager.getCameraIdList();
        String cameraId = null;

        for (String id : cameraIds) {
            CameraCharacteristics cameraCharacteristics =
                    cameraManager.getCameraCharacteristics(id);

            int lensFacing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);

            if (lensFacing == cameraToUse) {
                cameraId = id;
                break;
            }
        }
        return cameraId;
    }




    private class CameraStateCallback extends CameraDevice.StateCallback {

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            mOpenFlag = true;
            createCaptureSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            try {
                close();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            mErrorFlag = true;
            try {
                close();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }




    private class CameraCaptureSessionCallback extends CameraCaptureSession.StateCallback {

        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            mCaptureSession = session;
            mCapturingFlag = true;
            try {
                buildCaptureRequest();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }

        @SuppressLint("NewApi")
        private void buildCaptureRequest() throws CameraAccessException {
            CaptureRequest.Builder previewRequestBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            // add output targets to capture session
            mOutputTargets.forEach((t) -> previewRequestBuilder.addTarget(t));

            // add all the preview option flags
            mPreviewOptions.forEach((k, v) -> {
                previewRequestBuilder.set(k, v);
            });
            CaptureRequest request = previewRequestBuilder.build();
            mCaptureSession.setRepeatingRequest(request, null, null);
            mCapturingFlag = true;
        }
    }
}
