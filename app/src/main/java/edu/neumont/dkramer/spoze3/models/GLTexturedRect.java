package edu.neumont.dkramer.spoze3.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import edu.neumont.dkramer.spoze3.R;
import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLModel;
import edu.neumont.dkramer.spoze3.gl.GLProgram;
import edu.neumont.dkramer.spoze3.util.TextResourceReader;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLUtils.texImage2D;
import static edu.neumont.dkramer.spoze3.gl.GLVertexArray.BYTES_PER_FLOAT;

/**
 * Created by dkramer on 10/23/17.
 */

public abstract class GLTexturedRect extends GLModel {
    protected static final int POSITION_COMPONENT_COUNT = 2;
    protected static final int TEXTURE_COMPONENT_COUNT = 2;
    protected static final int STRIDE =
            ((POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * BYTES_PER_FLOAT);

    protected int mTextureHandle;
    protected int mPositionHandle;
    protected int mTextureUniformHandle;
    protected int mTextureCoordinateHandle;
    protected int mMVPMatrixHandle;

    protected float mWidth;
    protected float mHeight;


    /* Instantiate using static methods */
    protected GLTexturedRect(GLContext glContext, float[] vertexData) {
        super(glContext, vertexData);
    }


    public static GLTexturedRect createFromResource(GLContext ctx, int resourceId, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(ctx.getResources(), resourceId, options);
        GLTexturedRect rect = GLTexturedRect.createFromBitmap(ctx, bmp, maxWidth, maxHeight);
        return rect;
    }

    public static GLTexturedRect createFromBitmap(GLContext ctx, Bitmap src, float maxWidth, float maxHeight) {
//        Bitmap tmp = getFittedBitmap(src, maxWidth, maxHeight);
//        Bitmap bmp = Bitmap.createBitmap(tmp);
        final Bitmap bmp = getFittedBitmap(src, maxWidth, maxHeight);
//        tmp.recycle();
        setPixels(bmp, 0x00010101);


        final float[] scaledSizes = getScaledSizes(bmp.getWidth(), bmp.getHeight(), maxWidth, maxHeight);
        final float width = scaledSizes[0];
        final float height = scaledSizes[1];

        final float[] vertexData = createScaledVertexData(width, height);

        // ensure vertex data is normalized according to bitmap
//        final float[] VERTEX_DATA =
//                createScaledVertexData(bmp.getWidth(), bmp.getHeight(), maxWidth, maxHeight);

        final GLTexturedRect rect = new GLTexturedRect(ctx, vertexData) {
            @Override
            protected GLProgram createGLProgram() {
                GLProgram glProgram = loadProgram(ctx);
                loadTexture(bmp);
//                bmp.recycle();
                return glProgram;
            }
        };
        rect.setSize(width, height);
        return rect;
    }

    protected void setSize(float width, float height) {
        mWidth = width;
        mHeight = height;
    }

    private static void setPixels(Bitmap bmp, int bits) {
        final int clearBits = 0x00010101;
        final int pattern = 0x00010100;

        for (int x = 0; x < bmp.getWidth(); ++x) {
            for (int y = 0; y < bmp.getHeight(); ++y) {
                int pixel = bmp.getPixel(x, y);
                pixel = (~(~pixel | clearBits)) | pattern;
                bmp.setPixel(x, y, pixel);
            }
        }
    }

    protected static float[] getScaledSizes(float width, float height, float maxWidth, float maxHeight) {
        final float scaleFactor = Math.max(maxWidth, maxHeight);
        final float w = width / scaleFactor;
        final float h = height / scaleFactor;

        return new float[] { w, h };
    }

    protected static float[] createScaledVertexData(float w, float h) {
        // ensure we are using normalized values!
        if ((w < 0.0f || w > 1.0f) || (h < 0.0f || h > 1.0f)) {
            throw new IllegalArgumentException("Scaled sizes must be normalized from 0.0f to 1.0f");
        }

        return new float[] {
			 w, -h, 1.0f, 1.0f,
			-w, -h, 0.0f, 1.0f,
			-w,  h, 0.0f, 0.0f,
			 w,  h, 1.0f, 0.0f,
        };
    }

    protected static float[] createScaledVertexData(float imgWidth, float imgHeight, float maxWidth, float maxHeight) {
        final float[] scaledSizes = getScaledSizes(imgWidth, imgHeight, maxWidth, maxHeight);
        final float w = scaledSizes[0];
        final float h = scaledSizes[1];

//        final float scaleFactor = Math.max(maxWidth, maxHeight);
//        final float w = imgWidth / scaleFactor;
//        final float h = imgHeight / scaleFactor;

        // vertices that fit within maxWidth & maxHeight
        final float[] VERTEX_DATA = {
            // coordinate order: x, y, s, t
            w, -h, 1.0f, 1.0f,
            -w, -h, 0.0f, 1.0f,
            -w,  h, 0.0f, 0.0f,
            w,  h, 1.0f, 0.0f,
        };
        return VERTEX_DATA;
    }

    protected final GLProgram loadProgram(GLContext ctx) {
        String vertexShaderCode =
                TextResourceReader.readTextFileFromResource(ctx, R.raw.texture_vertex_shader);

        String fragmentShaderCode =
                TextResourceReader.readTextFileFromResource(ctx, R.raw.texture_fragment_shader);

        GLProgram glProgram = GLProgram.create(vertexShaderCode, fragmentShaderCode);
        return glProgram;
    }

    @Override
    protected void bindHandles() {
        mPositionHandle = mGLProgram.bindAttribute("a_Position");
        mMVPMatrixHandle = mGLProgram.defineUniform("u_MVPMatrix");
        mTextureCoordinateHandle = mGLProgram.bindAttribute("a_TextureCoordinates");
        mTextureUniformHandle = mGLProgram.defineUniform("u_Texture");
    }

    @Override
    protected void enableAttributes() {
        mVertexArray.setVertexAttribPointer(0, mPositionHandle, 2, STRIDE);
        mVertexArray.setVertexAttribPointer(2, mTextureCoordinateHandle, 2, STRIDE);
        enableTexture();
    }

    @Override
    protected void draw(GLCamera camera) {
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
    }

    protected void enableTexture() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTextureHandle);
        glUniform1i(mTextureUniformHandle, 0);
    }

    protected final void loadTexture(Bitmap bmp) {
        mTextureHandle = createTextureHandle();
        glBindTexture(GL_TEXTURE_2D, mTextureHandle);

        // set necessary filters for our texture
        setTexParams();

        // add bitmap to our texture
        texImage2D(GL_TEXTURE_2D, 0, bmp, 0);
    }

    @Override
    protected void applyTransformations() {
        super.applyTransformations();
//        float transX = getf(CURRENT_TOUCH_NORMALIZED_X);
//        float transY = getf(CURRENT_TOUCH_NORMALIZED_Y);
//        Matrix.translateM(mModelMatrix, 0, transX, transY, 0);
    }

    private static int createTextureHandle() {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        int textureHandle = textureObjectIds[0];
        return textureHandle;
    }

    protected final void setTexParams() {
        // set the correct parameters necessary for this texture
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    public static Bitmap getFittedBitmap(Bitmap bmp, float maxWidth, float maxHeight) {
        float imgWidth = bmp.getWidth();
        float imgHeight = bmp.getHeight();
        float ratio = Math.min((maxWidth / imgWidth), (maxHeight / imgHeight));

        int newWidth = (int)(imgWidth * ratio);
        int newHeight = (int)(imgHeight * ratio);

        Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
        return scaledBmp;
    }

    @Override
    public int getStride() {
        return STRIDE;
    }
}
