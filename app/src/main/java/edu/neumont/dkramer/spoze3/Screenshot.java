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
 * Created by dkramer on 12/4/17.
 */

public class Screenshot implements ImageReader.OnImageAvailableListener {
    protected static final int WAIT_FRAMES = 8;

    protected static Screenshot sInstance;
    protected static boolean sInitialized = false;

    protected ImageReader mImageReader;
    protected MediaProjection mMediaProjection;
    protected VirtualDisplay mVirtualDisplay;
    protected int mFrames;
    protected boolean mDidCapture;

    protected Context mContext;
    protected int mResultCode;
    protected Intent mData;
    protected ScreenshotCallback mScreenshotCallback = (bmp -> {});

    protected int mWidth;
    protected int mHeight;


    private Screenshot() { }

    public static Screenshot getInstance() {
        return sInstance == null ? sInstance = new Screenshot() : sInstance;
    }

    public Screenshot setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        return this;
    }

    public Screenshot init(Context ctx, int resultCode, Intent data, ScreenshotCallback cb) {
        mContext = ctx;
        mResultCode = resultCode;
        mData = data;
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mImageReader.setOnImageAvailableListener(this, null);

        MediaProjectionManager manager =
                (MediaProjectionManager)ctx.getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        if (mMediaProjection == null) {
            mMediaProjection = manager.getMediaProjection(resultCode, data);
        }

        mVirtualDisplay = mMediaProjection.createVirtualDisplay("Screenshot", mWidth, mHeight, 72,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);

        sInitialized = true;
        return this;
    }

    public void capture(ScreenshotCallback cb) {
        if (!sInitialized) {
            throw new IllegalStateException("Must call Screenshot.init() before capturing!");
        }

        if (cb == null) {
            throw new NullPointerException("Screenshot callback cannot be null!");
        }

        // reset back to enable capture to occur in onImageAvailable()
        mFrames = 0;
        mDidCapture = false;
        mScreenshotCallback = cb;
    }


    @Override
    public void onImageAvailable(ImageReader reader) {
        Image img = reader.acquireLatestImage();

        if (img != null) {
            if (++mFrames >= WAIT_FRAMES && !mDidCapture) {
                final Image.Plane[] planes = img.getPlanes();
                final Buffer buffer = planes[0].getBuffer().rewind();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * mWidth;

                // create bitmap
                Bitmap bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                mScreenshotCallback.onScreenshot(bitmap);
                mDidCapture = true;
            }
            img.close();
        }
    }

    public void destroy() {
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
        sInitialized = false;
    }



    public interface ScreenshotCallback {
        void onScreenshot(Bitmap bmp);
    }
}
