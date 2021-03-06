package edu.neumont.dkramer.spoze3.converter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import static android.renderscript.Element.DataKind.PIXEL_YUV;
import static android.renderscript.Element.DataType.UNSIGNED_8;

/**
 * Created by dkramer on 11/13/17.
 */

public class YUVToRGBAConverter extends ImageConverter {
    private Allocation mAllocIn;
    private Allocation mAllocOut;
    private ScriptIntrinsicYuvToRGB mConvertScript;
    private Bitmap mOutputBitmap;
    private YUVImage2 mCurrentImage;
    private boolean mIsInitialized;

    public YUVToRGBAConverter(Context ctx) {
        super(ctx);
    }


    private void init(Image image) {
        if (image.getFormat() != ImageFormat.YUV_420_888) {
            throw new UnsupportedOperationException("Only supported format is YUV_420_888");
        }
        int width = image.getWidth();
        int height = image.getHeight();

        width = 720;
        height = 1280;

        mCurrentImage = new YUVImage2(image);
        createAllocations(width, height);

        mOutputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mAllocOut = Allocation.createFromBitmap(mRs, mOutputBitmap);

        mConvertScript = ScriptIntrinsicYuvToRGB.create(mRs,
                Element.createPixel(mRs, UNSIGNED_8, PIXEL_YUV));
        mConvertScript.setInput(mAllocIn);
    }

    private void createAllocations(int width, int height) {
        Element elemYUV = Element.createPixel(mRs, Element.DataType.UNSIGNED_8, Element.DataKind.PIXEL_YUV);
        Type yuvType = new Type.Builder(mRs, elemYUV).setX(width).setY(height).setYuvFormat(ImageFormat.NV21).create();
        mAllocIn = Allocation.createTyped(mRs, yuvType);

        // output allocation
//        Type outType = new Type.Builder(mRs, Element.U8_4(mRs)).setX(width).setY(height).create();
//        mAllocOut = Allocation.createTyped(mRs, outType);
    }

    @Override
    public Bitmap convert(Image image) {
        if (!mIsInitialized) {
            init(image);
            mIsInitialized = true;
        }
        mCurrentImage.updateFromImage(image);
        updateAllocation();
        convertImage();
        return mOutputBitmap;
    }

    private void updateAllocation() {
        mAllocIn.copyFrom(mCurrentImage.getBuffer());
    }

    private void convertImage() {
        mConvertScript.forEach(mAllocOut);
        mAllocOut.copyTo(mOutputBitmap);
    }

    @Override
    public void destroy() {
        if (mAllocIn != null) {
            mAllocIn.destroy();
            mAllocIn = null;
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

        if (mCurrentImage != null) {
            mCurrentImage.destroy();
            mCurrentImage = null;
        }

        if (mOutputBitmap != null && mOutputBitmap.isRecycled()) {
            mOutputBitmap.recycle();
            mOutputBitmap = null;
        }
        mIsInitialized = false;
    }
}
