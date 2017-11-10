package edu.neumont.dkramer.spoze3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT16;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_RENDERBUFFER;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindRenderbuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glFramebufferRenderbuffer;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenRenderbuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glReadPixels;
import static android.opengl.GLES20.glRenderbufferStorage;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameterf;

/**
 * This class is used for determining the pixel id value of an object based on a touch
 * position.
 * Created by dkramer on 11/2/17.
 */

public class GLPixelPicker {
    // r, g, b, a
    private static final int VALUES_PER_PIXEL = 4;


    // offscreen buffer stuff
    private int[] frameBufferId = new int[1];
    private int[] renderBufferId = new int[1];
    private int[] textureId = new int[1];

    private int mWidth;
    private int mHeight;

    // buffer to hold single pixel value reading
    private ByteBuffer mColorBuffer;
    private byte[] mColorArray;
    private OnPixelReadListener mPixelReadListener;



    public GLPixelPicker(int viewWidth, int viewHeight) {
        mWidth = viewWidth;
        mHeight = viewHeight;
        init(viewWidth, viewHeight);
    }

    public GLPixelPicker() {
        init(0, 0);
    }

    protected void init(int viewWidth, int viewHeight) {
        mColorBuffer = ByteBuffer.allocate(VALUES_PER_PIXEL).order(ByteOrder.nativeOrder());
        mColorArray = new byte[VALUES_PER_PIXEL];

        createOffScreenBuffer(viewWidth, viewHeight);


        // default just does nothing
        mPixelReadListener = (p) -> { };
    }

    /**
     * A lot of boiler plate code, but this creates an offscreen frame buffer
     * that we can use to render objects to
     * @param width
     * @param height
     */
    private void createOffScreenBuffer(int width, int height) {
        glGenFramebuffers(1, frameBufferId, 0);
        glGenTextures(1, textureId, 0);
        glGenRenderbuffers(1, renderBufferId, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId[0]);
        glBindTexture(GL_TEXTURE_2D, textureId[0]);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glBindRenderbuffer(GL_RENDERBUFFER, renderBufferId[0]);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, width, height);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId[0], 0);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBufferId[0]);
    }

    public void enable() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId[0]);
        //TODO here is where objects should render their pixel id's and we should read pixel
    }

    public void disable() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int readPixel(int x, int y) {
        return readPixel(x, y, mWidth, mHeight);
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
