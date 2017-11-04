package edu.neumont.dkramer.spoze3.models;

import android.graphics.Bitmap;
import android.opengl.Matrix;
import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLProgram;
import edu.neumont.dkramer.spoze3.gl.GLWorld;

/**
 * Created by dkramer on 11/2/17.
 */

public abstract class SignModel extends GLTexturedRect {
    /*
     * Special bits that targets the last bits of RED, GREEN, and BLUE to
     * allow us to set a limited number of patterns that can be used when
     * reading pixels to determine if we have been touched or not.
     *
     * This is a work around to creating a more complex touch system
     * such as a raycaster, or additional color buffer.
     */
    protected static final int SPECIAL_BITS = 0x01010101;

    /*
     * Bit Patterns that will allow us to uniquely identify models
     * NOTE: Each instance must have a different pattern to be able to be
     * distinguished... This is very limited as modifying too many bits would
     * result in textures appearing incorrect to the user.
     */
    protected static final int[] BIT_PATTERNS =
    {
            0x01000000, 0x01010000, 0x01000100,
            0x01000001, 0x01010100, 0x01000100,
            0x01000101, 0x01010001, 0x00010000,
            0x00000100, 0x00000001, 0x00010100,
            0x00010001, 0x00000101,
    };

    /*
     * Each time a model is created, a pattern will be popped off this stack and
     * assigned to the model. If the model is deleted, it's pattern will be pushed
     * back onto the stack, to be used for future models, allowing for safe reuse.
     */
    protected static final Stack<Integer> AVAILABLE_BIT_PATTERNS = new Stack<>();
    static {
        // load everything onto the stack
        for (int pattern : BIT_PATTERNS) {
            AVAILABLE_BIT_PATTERNS.add(pattern);
        }
        // sort by "least destructive"
        Collections.sort(AVAILABLE_BIT_PATTERNS, (i1, i2) -> i2 - i1);
    }


    protected final int mBitPattern;




    protected SignModel(GLContext glContext, float[] vertexData, int bitPattern) {
        super(glContext, vertexData);
        mBitPattern = bitPattern;
    }

    //TODO background creation should be handled elsewhere
    public static void createInBackground(GLWorld world, Bitmap src, float maxWidth, float maxHeight) {
        new Thread(() -> {
            final GLContext ctx = world.getGLContext();
            final Bitmap bmp = getFittedBitmap(src, maxWidth, maxHeight);
            final int bitPattern = getNextBitPattern();
            applyBitPatternToBitmap(bmp, bitPattern);

            final float[] scaledSizes = getScaledSizes(bmp.getWidth(), bmp.getHeight(), maxWidth, maxHeight);
            final float width = scaledSizes[0];
            final float height = scaledSizes[1];

            final float[] vertexData = createScaledVertexData(width, height);

            ctx.queueEvent(() -> {
                SignModel model = new SignModel(ctx, vertexData, bitPattern) {
                    @Override
                    protected GLProgram createGLProgram() {
                        GLProgram glProgram = loadProgram(ctx);
                        loadTexture(bmp);
                        return glProgram;
                    }
                };
                model.setSize(width, height);
                world.addModel(model);
            });
        }).start();
    }

    protected void applyTransformations() {
        super.applyTransformations();
        Matrix.translateM(mModelMatrix, 0, mTransX, mTransY, 0);
    }

    public void render(GLCamera camera) {
        super.render(camera);
    }

//    public void translate(float x, float y) {
//        Matrix.translateM(mModelMatrix, 0, x, y, 0);
//    }

    public void delete() {
        super.delete();
        AVAILABLE_BIT_PATTERNS.push(getBitPattern());
    }

    public boolean didTouch(int pixel) {
        return ((pixel ^ mBitPattern) & SPECIAL_BITS) == 0;
    }

    public static SignModel createFromBitmap(GLContext ctx, Bitmap src, float maxWidth, float maxHeight) {
        final Bitmap bmp = getFittedBitmap(src, maxWidth, maxHeight);
        final int bitPattern = getNextBitPattern();
        applyBitPatternToBitmap(bmp, bitPattern);

        final float[] VERTEX_DATA =
                createScaledVertexData(bmp.getWidth(), bmp.getHeight(), maxWidth, maxHeight);

        return new SignModel(ctx, VERTEX_DATA, bitPattern) {
            @Override
            protected GLProgram createGLProgram() {
                GLProgram glProgram = loadProgram(ctx);
                loadTexture(bmp);
                return glProgram;
            }
        };
    }

    protected static void applyBitPatternToBitmap(Bitmap bmp, int bitPattern) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int j = 0, len = pixels.length; j < len; ++j) {
            pixels[j] = (~(~pixels[j] | SPECIAL_BITS) | bitPattern);
        }
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
    }

    /**
     * Advance through our bit pattern array and return next available pattern.
     * If we have used all of them already, an exception will be thrown.
     * @return bit pattern value
     */
    protected static int getNextBitPattern() {
        if (AVAILABLE_BIT_PATTERNS.isEmpty()) {
            throw new IllegalStateException("Usable bit pattern limit reached! :( " +
                    "Too many instances created!");
        }
        return AVAILABLE_BIT_PATTERNS.pop();
    }

    protected int getBitPattern() {
        return mBitPattern;
    }

    @Override
    protected GLProgram createGLProgram() {
        return null;
    }

    // testing
    protected float mTransX;
    protected float mTransY;

    public void setTranslate(float transX, float transY) {
        //TODO this works fine in portrait mode, but in landscape it is off!!
        //TODO::: determine the orientation and determine the scale factor to apply
        //TODO:: also, need to compensate for the offset of the GLMotionCamera position
        mTransX = transX + (mWidth / 2);

        // this works well
        mTransY = (mHeight / 2) + (transY * 2f);

        Log.i("SIGN_MODEL", "TransX: " + mTransX + ", TransY: " + mTransY);
    }
}
