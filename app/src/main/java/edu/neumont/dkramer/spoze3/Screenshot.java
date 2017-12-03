package edu.neumont.dkramer.spoze3;

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

import java.nio.Buffer;

/**
 * Created by dkramer on 12/3/17.
 */

public class Screenshot {
    protected static Screenshot sInstance;


    protected int mWidth;
    protected int mHeight;
    protected ImageReader mImageReader;
    protected MediaProjection mMediaProjection;
    protected VirtualDisplay mVirtualDisplay;
    protected int mFrames;


    private Screenshot() { }

    public static Screenshot getInstance() {
        if (sInstance == null) {
            sInstance = new Screenshot();
        }
        return sInstance;
    }

    public Screenshot setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        return this;
    }

    public void capture(Context ctx, int resultCode, Intent data, ScreenshotCallback cb) {
        mFrames = 0;
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mImageReader.setOnImageAvailableListener((r) -> {
            Image img = r.acquireLatestImage();
            if (img != null) {
                ++mFrames;
                final Image.Plane[] planes = img.getPlanes();
                final Buffer buffer = planes[0].getBuffer().rewind();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * mWidth;

                // create bitmap
                Bitmap bitmap = Bitmap.createBitmap(mWidth+rowPadding/pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                img.close();

                if (mFrames > 24) {
                    cb.onScreenshot(bitmap);
                    dispose();
                }
            }
        }, null);

        MediaProjectionManager manager = (MediaProjectionManager)ctx.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mMediaProjection == null) {
            mMediaProjection = manager.getMediaProjection(resultCode, data);
        }
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("Screenshot", mWidth, mHeight, 72, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
    }

    public void dispose() {
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }

        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }

        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    public interface ScreenshotCallback {
        void onScreenshot(Bitmap bmp);
    }
}
