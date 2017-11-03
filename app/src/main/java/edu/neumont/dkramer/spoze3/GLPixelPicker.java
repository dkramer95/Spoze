package edu.neumont.dkramer.spoze3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glReadPixels;

/**
 * Created by dkramer on 11/2/17.
 */

public class GLPixelPicker {
    private static final int VALUES_PER_PIXEL = 4;

    private ByteBuffer mColorBuffer;
    private byte[] mColorArray;
    private OnPixelReadListener mPixelReadListener;

    public GLPixelPicker() {
        init();
    }

    protected void init() {
        mColorBuffer = ByteBuffer.allocate(VALUES_PER_PIXEL).order(ByteOrder.nativeOrder());
        mColorArray = new byte[VALUES_PER_PIXEL];

        // default just does nothing
        mPixelReadListener = (p) -> { };
    }

    public int readPixel(int x, int y, int width, int height) {
        mColorBuffer.rewind();

        // y needs to be inverted to be in proper coordinate space
        y = height - y;

        glReadPixels(x, y, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, mColorBuffer);
        mColorBuffer.get(mColorArray);

        // extract pixel values
        int r = ((int)mColorArray[0]) & 0xFF;
        int g = ((int)mColorArray[1]) & 0xFF;
        int b = ((int)mColorArray[2]) & 0xFF;
        int a = ((int)mColorArray[3]) & 0xFF;

        int pixel = (a << 24) + (r << 16) + (g << 8) + b;

        // notify pixel read listener
        mPixelReadListener.onPixelRead(pixel);

        // put each byte value in correct position to create ARGB pixel
        return pixel;
    }

    public void setOnPixelReadListener(OnPixelReadListener listener) {
        mPixelReadListener = listener;
    }

    public interface OnPixelReadListener {
        void onPixelRead(int pixel);
    }


}
