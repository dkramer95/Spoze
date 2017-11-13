
package edu.neumont.dkramer.spoze3.converter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.Type;

import edu.neumont.dkramer.spoze3.ScriptC_diff;

/**
 * Created by dkramer on 9/16/17.
 */

public class YUVToRGBAMotionConverter extends ImageConverter {
    //TODO create toggle to change this... this is the motion sensitivity threshold
    private static float sThreshold = 1;

    private Allocation mAllocInPrev;
    private Allocation mAllocInCurrent;
    private Allocation mAllocOut;
    private ScriptC_diff mConvertScript;
    private boolean mIsInitialized;
    private Bitmap mOutputBitmap;
    private YUVImage2 mCurrentImage;
    private YUVImage2 mPrevImage;



    public YUVToRGBAMotionConverter(Context ctx) {
        super(ctx);
    }

    private void init(Image image) {
        if (image.getFormat() != ImageFormat.YUV_420_888) {
            throw new UnsupportedOperationException("Only supported format is YUV_420_888");
        }
        int width = image.getWidth();
        int height = image.getHeight();

        mCurrentImage = new YUVImage2(image);
        mPrevImage = new YUVImage2(image);
        createAllocations(width, height);

        mOutputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mAllocOut = Allocation.createFromBitmap(mRs, mOutputBitmap);

        mConvertScript = new ScriptC_diff(mRs);
        mConvertScript.set_extra_alloc(mAllocInPrev);
        mConvertScript.set_yuv_in(mAllocInCurrent);
    }

    private void createAllocations(int width, int height) {
        // input allocation
        Element elemYUV = Element.createPixel(mRs, Element.DataType.UNSIGNED_8, Element.DataKind.PIXEL_YUV);
        Type yuvType = new Type.Builder(mRs, elemYUV).setX(width).setY(height).setYuvFormat(ImageFormat.NV21).create();
        mAllocInCurrent = Allocation.createTyped(mRs, yuvType);
        mAllocInPrev = Allocation.createTyped(mRs, yuvType);

        // output allocation
        Type outType = new Type.Builder(mRs, Element.U8_4(mRs)).setX(width).setY(height).create();
        mAllocOut = Allocation.createTyped(mRs, outType);
    }

    @Override
    public Bitmap convert(Image image) {
        if (!mIsInitialized) {
            init(image);
            mIsInitialized = true;
        }
        mPrevImage.updateFromImage(mCurrentImage);
        mCurrentImage.updateFromImage(image);
        updateAllocation();
        convertImage();
        return mOutputBitmap;
    }

    private void convertImage() {
        mConvertScript.set_threshold(sThreshold);
        mConvertScript.forEach_convert(mAllocOut);
        mAllocOut.copyTo(mOutputBitmap);
    }

    private void updateAllocation() {
        mAllocInCurrent.copyFrom(mCurrentImage.getBuffer());
        mAllocInPrev.copyFrom(mPrevImage.getBuffer());
    }

    public static void setThreshold(float value) {
        sThreshold = value;
    }

    @Override
    public void destroy() {
        if (mAllocInCurrent != null) {
            mAllocInCurrent.destroy();
            mAllocInCurrent = null;
        }

        if (mAllocInPrev != null) {
            mAllocInPrev.destroy();
            mAllocInPrev = null;
        }

        if (mAllocOut != null) {
            mAllocOut.destroy();
            mAllocOut = null;
        }

        if (mRs != null) {
            mRs.destroy();
            mRs = null;
        }

        if (mConvertScript != null) {
            mConvertScript.destroy();
            mConvertScript = null;
        }

        if (mOutputBitmap != null && mOutputBitmap.isRecycled()) {
            mOutputBitmap.recycle();
            mOutputBitmap = null;
        }

        if (mCurrentImage != null) {
            mCurrentImage.destroy();
            mCurrentImage = null;
        }

        if (mPrevImage != null) {
            mPrevImage.destroy();
            mPrevImage = null;
        }
        mIsInitialized = false;
    }
}
