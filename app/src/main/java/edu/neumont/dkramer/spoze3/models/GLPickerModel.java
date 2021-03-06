package edu.neumont.dkramer.spoze3.models;

import java.nio.FloatBuffer;
import java.util.Random;

import edu.neumont.dkramer.spoze3.R;
import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLModel;
import edu.neumont.dkramer.spoze3.gl.GLProgram;
import edu.neumont.dkramer.spoze3.util.TextResourceReader;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform1fv;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by dkramer on 11/10/17.
 */

public class GLPickerModel extends GLModel {
    // random number generator for creating pixel id's
    private static final Random rng = new Random();
    private static final float SELECTION_COLOR = 1.0f;



    protected int mPickerHandle;
    protected int mPickingMVPMatrixHandle;
    protected int mPickingPositionHandle;
    protected GLModel mSourceModel;
    protected final FloatBuffer mColorBuffer;
    protected float mRenderValue;
    protected final int mPixelId;





    public GLPickerModel(GLContext glContext, GLModel sourceModel) {
        super(glContext, sourceModel.getVertexData());
        mPixelId = rng.nextInt(255);
        mRenderValue = mPixelId / 255.0f;

        mColorBuffer = FloatBuffer.wrap(new float[]{ mRenderValue });
        mSourceModel = sourceModel;
    }

    @Override
    protected GLProgram createGLProgram() {
        final GLContext ctx = getGLContext();

        String vertexShaderCode =
                TextResourceReader.readTextFileFromResource(ctx, R.raw.picking_vertex_shader);

        String fragmentShaderCode =
                TextResourceReader.readTextFileFromResource(ctx, R.raw.picking_fragment_shader);

        return GLProgram.create(vertexShaderCode, fragmentShaderCode);
    }

    public void enableSelected() {
        updateBuffColor(SELECTION_COLOR);
    }

    public void reset() {
        updateBuffColor(mRenderValue);
    }

    protected void updateBuffColor(float value) {
        mColorBuffer.rewind();
        mColorBuffer.put(0, value);
    }

    @Override
    protected void draw(GLCamera camera) {
        // using just 1 float value, but still allows for plenty of variations for our use case
        glUniform1fv(mPickerHandle, 1, mColorBuffer);
        glUniformMatrix4fv(mPickingMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        //TODO size "could" change w/ other models... add method to get vertex count in GLModel
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
    }

    @Override
    protected void bindHandles() {
        mPickingPositionHandle = mGLProgram.bindAttribute("a_Position");
        mPickingMVPMatrixHandle = mGLProgram.defineUniform("u_MVPMatrix");
        mPickerHandle = mGLProgram.defineUniform("u_Color");
    }

    @Override
    protected void enableAttributes() {
        mVertexArray.setVertexAttribPointer(0, mPickingPositionHandle, 2, mSourceModel.getStride());
    }

    public int getPixelId() {
        return mPixelId;
    }
}
