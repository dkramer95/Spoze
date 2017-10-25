package edu.neumont.dkramer.spoze3.converter;

import android.graphics.ImageFormat;
import android.media.Image;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Created by dkramer on 9/16/17.
 */

public class YUVImage2 {
    private int mWidth;
    private int mHeight;
    private byte[] mBuffer;



    public YUVImage2(Image image) {
        init(image);
    }

    private void init(Image image) {
        if (image.getFormat() != ImageFormat.YUV_420_888) {
            throw new UnsupportedOperationException("Only YUV_420_888 is supported!");
        }
        mWidth = image.getWidth();
        mHeight = image.getHeight();
        createBuffer(image);
    }

    private void createBuffer(Image image) {
        Image.Plane[] planes = image.getPlanes();
        int bufferSize = 0;
        for (int j = 0; j < planes.length; ++j) {
            Buffer b = planes[j].getBuffer();
            bufferSize += b.remaining();
        }
        mBuffer = new byte[bufferSize];
    }

    public void updateFromImage(Image image) {
        updateBuffer(image);
    }

    public void updateFromImage(YUVImage2 image) {
        System.arraycopy(image.mBuffer, 0, mBuffer, 0, mBuffer.length);
    }

    private void updateBuffer(Image image) {
        Image.Plane[] planes = image.getPlanes();

        ByteBuffer Y_buffer = planes[0].getBuffer();
        ByteBuffer U_buffer = planes[1].getBuffer();
        ByteBuffer V_buffer = planes[2].getBuffer();

        int Yb = Y_buffer.remaining();
        int Ub = U_buffer.remaining();
        int Vb = V_buffer.remaining();

        Y_buffer.get(mBuffer, 0, Yb);
        U_buffer.get(mBuffer, Yb + Ub, Vb);
        V_buffer.get(mBuffer, Yb, Ub);
    }

    public void destroy() {
        mBuffer = null;
    }

    public byte[] getBuffer() {
        return mBuffer;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }
}
