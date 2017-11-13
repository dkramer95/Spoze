package edu.neumont.dkramer.spoze3.converter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.camera2.CameraAccessException;
import android.media.Image;
import android.media.ImageReader;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import edu.neumont.dkramer.spoze3.camera.Camera;
import edu.neumont.dkramer.spoze3.camera.CameraPreview;

/**
 * Created by dkramer on 10/24/17.
 */

public class MotionCameraPreview extends CameraPreview {

    protected YUVToRGBAMotionConverter mImageConverter;
    protected ImageReader mImageReader;
    protected OnBitmapAvailableListener mBitmapAvailableListener;
    protected Bitmap mImageToDraw;
    public static Bitmap sImage;



    public MotionCameraPreview(Context context) {
        super(context);
    }

    public MotionCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MotionCameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MotionCameraPreview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mImageToDraw != null) {
//            canvas.drawBitmap(mImageToDraw, 0, 0, new Paint());
            sImage = mImageToDraw;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            attachCamera(holder);
            initImageConverter();
            openCamera();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        initBitmapAvailableListener();
    }

    protected void initImageConverter() {
        // create our image converter that will track pixel motion
        mImageConverter = new YUVToRGBAMotionConverter(getContext());
        mImageReader = createImageReader(getWidth(), getHeight());
        mCamera.addTarget(mImageReader.getSurface());
    }

    protected ImageReader createImageReader(int width, int height) {
        ImageReader imageReader = ImageReader.newInstance(width, height, Camera.CAPTURE_FORMAT, 2);
        imageReader.setOnImageAvailableListener((r -> {
            Image img = r.acquireLatestImage();
            if (img != null) {
                Bitmap bmp = mImageConverter.convert(img);
                mImageToDraw = bmp;
                mBitmapAvailableListener.bitmapAvailable(bmp);
                img.close();
            }
        }), null);
        return imageReader;
    }

    protected void initBitmapAvailableListener() {
        // enable custom drawing of our camera preview
        setWillNotDraw(false);

        // update our draw image whenever we receive a converted bitmap
        mBitmapAvailableListener = bmp -> {
            mImageToDraw = bmp;
            invalidate();
        };
    }




    /**
     * Interface for handling a new bitmap image, when it becomes available
     */
    public interface OnBitmapAvailableListener {
        void bitmapAvailable(Bitmap bmp);
    }
}
