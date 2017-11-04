package edu.neumont.dkramer.spoze3.models;

import android.opengl.Matrix;

import edu.neumont.dkramer.spoze3.R;
import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLModel;
import edu.neumont.dkramer.spoze3.gl.GLProgram;
import edu.neumont.dkramer.spoze3.util.TextResourceReader;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static edu.neumont.dkramer.spoze3.gl.GLVertexArray.BYTES_PER_FLOAT;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_NORMALIZED_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_NORMALIZED_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.getf;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLSquare extends GLModel {
    protected static final int VERTEX_COMPONENT_COUNT = 3;
    protected static final int COLOR_COMPONENT_COUNT = 4;
    protected static final int STRIDE =
            ((VERTEX_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT);

    protected static final int POSITION_OFFSET = 0;
    protected static final int COLOR_OFFSET = 3;

    private static final float[] VERTEX_DATA = {
            // X, Y, Z,
            // R, G, B, A
            1.0f, -1.0f, 0.0f,
            1.0f, 0.0f, 0.0f, 1.0f,

            -1.0f, -1.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f,

            -1.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
    };

    protected int mPositionHandle;
    protected int mColorHandle;
    protected int mMVPMatrixHandle;


    /* Instantiate using static methods */
    protected GLSquare(GLContext glContext, float[] vertexData) {
        super(glContext, vertexData);
    }

    public static GLSquare create(GLContext ctx) {
        return new GLSquare(ctx, VERTEX_DATA);
    }

    @Override
    protected GLProgram createGLProgram() {
        String vertexShaderCode =
                TextResourceReader.readTextFileFromResource(getGLContext(), R.raw.simple_vertex_shader);

        String fragmentShaderCode =
                TextResourceReader.readTextFileFromResource(getGLContext(), R.raw.simple_fragment_shader);

        GLProgram glProgram = GLProgram.create(vertexShaderCode, fragmentShaderCode);
        return glProgram;
    }

    @Override
    protected void applyTransformations() {
        super.applyTransformations();
        float transX = getf(CURRENT_TOUCH_NORMALIZED_X);
        float transY = getf(CURRENT_TOUCH_NORMALIZED_Y);
        Matrix.translateM(mModelMatrix, 0, 0, 0, transX);
    }

    protected void bindHandles() {
        mPositionHandle = mGLProgram.bindAttribute("a_Position");
        mColorHandle = mGLProgram.bindAttribute("a_Color");
        mMVPMatrixHandle = mGLProgram.defineUniform("u_MVPMatrix");
    }

    @Override
    protected void enableAttributes() {
        mVertexArray.setVertexAttribPointer(POSITION_OFFSET, mPositionHandle, VERTEX_COMPONENT_COUNT, STRIDE);
        mVertexArray.setVertexAttribPointer(COLOR_OFFSET, mColorHandle, COLOR_COMPONENT_COUNT, STRIDE);
    }

    @Override
    protected void draw(GLCamera camera) {
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
    }
}
