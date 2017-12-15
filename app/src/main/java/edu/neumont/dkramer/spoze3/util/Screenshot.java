package edu.neumont.dkramer.spoze3.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.nio.Buffer;

/**
 * Created by dkramer on 12/15/17.
 */

public class Screenshot implements ImageReader.OnImageAvailableListener {
    public static final int SESSION_REQUEST_CODE = 1422;

    protected static final int WAIT_FRAMES = 10;

    protected static Screenshot sInstance;
    protected static boolean sInitialized;

    protected AppCompatActivity mActivity;
    protected ImageReader mImageReader;
    protected MediaProjection mMediaProjection;
    protected VirtualDisplay mVirtualDisplay;
    protected Callback mCallback;

    protected boolean mDidCapture;
    protected int mWidth;
    protected int mHeight;
    protected int mFrames;



    private Screenshot(AppCompatActivity activity) {
        mActivity = activity;
    }

    public static Screenshot beginSession(AppCompatActivity activity) {
        if (sInstance != null) {
            throw new IllegalStateException("Can only have one active screenshot session at a time");
        }
        sInstance = new Screenshot(activity);
        sInstance.createScreenCaptureIntent();
        return sInstance;
    }

    public static void onResult(int resultCode, Intent data, Callback cb) {
        if (sInstance == null) {
            throw new IllegalStateException("No session exists! Call beginSession() first!");
        }
        sInstance.init(resultCode, data, cb);
    }

    public static void finishSession() {
        if (sInstance == null) {
            throw new IllegalStateException("Session already finished!");
        }
        sInstance.destroy();
    }

    public static boolean isInitialized() {
        return (sInstance != null && sInitialized);
    }

    public static void takeScreenshot() {
        sInstance.capture();
    }

    protected void createScreenCaptureIntent() {
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager)getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        getActivity().startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), SESSION_REQUEST_CODE);
    }

    protected void init(int resultCode, Intent data, Callback cb) {
        View view = getActivity().getWindow().getDecorView().getRootView();
        mWidth = view.getWidth();
        mHeight = view.getHeight();

        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mImageReader.setOnImageAvailableListener(this, null);

        MediaProjectionManager manager =
                (MediaProjectionManager)getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        if (mMediaProjection == null) {
            mMediaProjection = manager.getMediaProjection(resultCode, data);
        }

        mVirtualDisplay = mMediaProjection.createVirtualDisplay("Screenshot", mWidth, mHeight, 72,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);

        // fake out first time so we don't automatically take unintended screenshot
        mDidCapture = true;

        mCallback = cb;
        sInitialized = true;
    }

    protected void capture() {
        if (!isInitialized()) {
            throw new IllegalStateException("Screenshot is not initialized! You must create a session first!");
        }
        // reset so that capture can happen in onImageAvailable callback
        mDidCapture = false;
        mFrames = 0;
    }

    protected void destroy() {
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }

        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }

        if (mImageReader != null) {
            mImageReader.getSurface().release();
            mImageReader.close();
            mImageReader = null;
        }
        sInstance = null;
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image img = reader.acquireLatestImage();
        if (img != null) {
            if (!mDidCapture && ++mFrames >= WAIT_FRAMES) {
                final Image.Plane[] planes = img.getPlanes();
                final Buffer buffer = planes[0].getBuffer().rewind();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * mWidth;

                // create bitmap
                Bitmap bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                mCallback.onScreenshot(bitmap);
                mDidCapture = true;
            }
            img.close();
        }
    }

    protected AppCompatActivity getActivity() {
        return mActivity;
    }




    public interface Callback {
        void onScreenshot(Bitmap bmp);
    }
}
