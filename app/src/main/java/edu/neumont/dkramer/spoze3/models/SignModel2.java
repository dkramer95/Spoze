package edu.neumont.dkramer.spoze3.models;

import android.graphics.Bitmap;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import edu.neumont.dkramer.spoze3.R;
import edu.neumont.dkramer.spoze3.geometry.Point3f;
import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLProgram;
import edu.neumont.dkramer.spoze3.gl.GLVertexArray;
import edu.neumont.dkramer.spoze3.util.TextResourceReader;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static edu.neumont.dkramer.spoze3.gl.GLVertexArray.BYTES_PER_FLOAT;

/**
 * Created by dkramer on 11/9/17.
 */

public class SignModel2 extends GLTexturedRect {
    protected GLProgram mPickingProgram;
    protected int mPickerHandle;
    protected int mPickingMVPMatrixHandle;
    protected int mPickingPositionHandle;
    protected float mRotation;

    protected final GLVertexArray mPickerVertexArray;


    // TODO make this unique for everything
    protected final float[] TEST_COLOR = { 1.0f, 1.0f, 1.0f, 1.0f };
    protected FloatBuffer colorBuffer = FloatBuffer.wrap(TEST_COLOR);

    protected SignModel2(GLContext glContext, float[] vertexData) {
        super(glContext, vertexData);
        mPickerVertexArray = new GLVertexArray(vertexData);
    }

    public static SignModel2 fromBitmap(GLContext ctx, Bitmap src, float maxWidth, float maxHeight) {
        final Bitmap bmp = getFittedBitmap(src, maxWidth, maxHeight);

        final float[] scaledSizes = getScaledSizes(bmp.getWidth(), bmp.getHeight(), maxWidth, maxHeight);
        final float width = scaledSizes[0];
        final float height = scaledSizes[1];
        final float[] vertexData = createScaledVertexData(width, height);

        return new SignModel2(ctx, vertexData) {
            @Override
            protected GLProgram createGLProgram() {
                GLProgram program = loadTextureProgram(getGLContext());
                loadTexture(bmp);
                bmp.recycle();
                loadPickingProgram(getGLContext());
                return program;
            }
        };
    }

    @Override
    public void drawSelector(GLCamera camera) {
        mPickingProgram.use();
        colorBuffer.rewind();
        mPickerVertexArray.setVertexAttribPointer(0, mPickingPositionHandle, 2, STRIDE);
//        glUniform4fv(mPickingProgram.getId(), mPickerHandle, colorBuffer);
        glUniform4fv(mPickerHandle, 1, colorBuffer);
//        glUniform4f(mPickerHandle, 1f, 0f, 0f, 1f);
        glUniformMatrix4fv(mPickingMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
    }

    @Override
    protected GLProgram createGLProgram() {
        loadPickingProgram(getGLContext());

        //TODO fix implementation detail about having multiple GLprograms...
        return loadTextureProgram(getGLContext());
    }

    protected void applyTransformations() {
        super.applyTransformations();
        Matrix.translateM(mModelMatrix, 0, mTransX, mTransY, 0);
        Matrix.rotateM(mModelMatrix, 0, mRotation, 0, 0, 1);
    }

    protected void bindHandles() {
        super.bindHandles();
        mPickingPositionHandle = mPickingProgram.bindAttribute("a_Position");
        mPickingMVPMatrixHandle = mPickingProgram.defineUniform("u_MVPMatrix");
        mPickerHandle = mPickingProgram.defineUniform("u_Color");
    }

    protected GLProgram loadTextureProgram(GLContext ctx) {
        String vertexShaderCode =
                TextResourceReader.readTextFileFromResource(ctx, R.raw.texture_vertex_shader);

        String fragmentShaderCode =
                TextResourceReader.readTextFileFromResource(ctx, R.raw.texture_fragment_shader);

        mGLProgram = GLProgram.create(vertexShaderCode, fragmentShaderCode);
        return mGLProgram;
    }

    protected GLProgram loadPickingProgram(GLContext ctx) {
        String vertexShaderCode =
                TextResourceReader.readTextFileFromResource(ctx, R.raw.picking_vertex_shader);

        String fragmentShaderCode =
                TextResourceReader.readTextFileFromResource(ctx, R.raw.picking_fragment_shader);

        mPickingProgram = GLProgram.create(vertexShaderCode, fragmentShaderCode);
        return mPickingProgram;
    }

    @Override
    protected void enableAttributes() {
        super.enableAttributes();
    }

    public void translate(float x, float y, float z) {
        Point3f cameraEye = getWorld().getCamera().getEye();

        //TODO this works fine in portrait mode, but in landscape it is off!!
        //TODO::: determine the orientation and determine the scale factor to apply
        mTransX = x + (mWidth / 2) + cameraEye.x;

        // this works well
        mTransY = (mHeight / 2) + (y * 2f) + cameraEye.y;
    }
}
