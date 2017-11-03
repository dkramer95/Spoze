package edu.neumont.dkramer.spoze3.models;

import android.graphics.Bitmap;

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
    protected static final int SPECIAL_BITS = 0x00010101;

    /*
     * Bit Patterns that will allow us to uniquely identify models
     * NOTE: Each instance must have a different pattern to be able to be
     * distinguished... This is very limited as modifying too many bits would
     * result in textures appearing incorrect to the user.
     */
    protected static final int[] BIT_PATTERNS =
    {
            0x00010000,
            0x00000100,
            0x00000001,
    };

    // where we are in BIT_PATTERNS for modifying bits of our texture
    protected static int sPatternIndex = 0;

    protected final int mBitPattern;


    protected SignModel(GLContext glContext, float[] vertexData, int bitPattern) {
        super(glContext, vertexData);
        mBitPattern = bitPattern;
    }

    public static void createInBackground(GLWorld world, Bitmap src, float maxWidth, float maxHeight) {
        new Thread(() -> {
            final GLContext ctx = world.getGLContext();
            final Bitmap bmp = getFittedBitmap(src, maxWidth, maxHeight);
            final int bitPattern = getNextBitPattern();
            applyBitPatternToBitmap(bmp, bitPattern);

            final float[] VERTEX_DATA =
                    createScaledVertexData(bmp.getWidth(), bmp.getHeight(), maxWidth, maxHeight);

            ctx.queueEvent(() -> {
                SignModel model = new SignModel(ctx, VERTEX_DATA, bitPattern) {
                    @Override
                    protected GLProgram createGLProgram() {
                        GLProgram glProgram = loadProgram(ctx);
                        loadTexture(bmp);
                        return glProgram;
                    }
                };
                world.addModel(model);
            });
        }).start();
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
        if (sPatternIndex >= BIT_PATTERNS.length) {
            throw new IllegalStateException("Usable bit pattern limit reached! :( " +
                    "Too many instances created!");
        }
        return BIT_PATTERNS[sPatternIndex++];
    }

    public int getBitPattern() {
        return mBitPattern;
    }

    @Override
    protected GLProgram createGLProgram() {
        return null;
    }
}
