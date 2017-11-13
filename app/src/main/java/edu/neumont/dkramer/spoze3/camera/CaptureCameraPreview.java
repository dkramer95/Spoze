package edu.neumont.dkramer.spoze3.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.camera2.CameraAccessException;
import android.media.Image;
import android.media.ImageReader;
import android.util.AttributeSet;
import android.util.Size;
import android.view.SurfaceHolder;

import edu.neumont.dkramer.spoze3.converter.ImageConverter;
import edu.neumont.dkramer.spoze3.converter.YUVToRGBAConverter;

/**
 * Created by dkramer on 11/13/17.
 */

public class CaptureCameraPreview extends CameraPreview {
    protected ImageConverter mImageConverter;
    protected ImageReader mImageReader;
    protected Bitmap mCaptureBitmap;


    public CaptureCameraPreview(Context context) {
        super(context);
        init();
    }

    public CaptureCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CaptureCameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        setWillNotDraw(false);
    }

    protected void initImageConverter() {
        // create our image converter that will track pixel motion
        mImageConverter = new YUVToRGBAConverter(getContext());
        Size size = getOptimalPreviewSize();
        mImageReader = createImageReader(size.getWidth(), size.getHeight());
        mCamera.addTarget(mImageReader.getSurface());
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera = Camera.acquire(getContext(), getCameraType())
                    .setOnCaptureRequestListener(this);
            initImageConverter();
            openCamera();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCaptureBitmap != null) {
            canvas.drawBitmap(mCaptureBitmap, 0, 0, new Paint());
        }
    }

    protected ImageReader createImageReader(int width, int height) {
        ImageReader imageReader = ImageReader.newInstance(width, height, Camera.CAPTURE_FORMAT, 2);
        imageReader.setOnImageAvailableListener((r -> {
            Image img = r.acquireLatestImage();
            if (img != null) {
                mCaptureBitmap = mImageConverter.convert(img);
                invalidate();
                img.close();
            }
        }), null);
        return imageReader;
    }


    public Bitmap getBitmap() {
        return mCaptureBitmap;
    }
}
