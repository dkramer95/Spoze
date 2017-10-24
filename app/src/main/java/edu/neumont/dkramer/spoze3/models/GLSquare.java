package edu.neumont.dkramer.spoze3.models;

import edu.neumont.dkramer.spoze3.R;
import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLModel;
import edu.neumont.dkramer.spoze3.gl.GLProgram;
import edu.neumont.dkramer.spoze3.gl.GLVertexArray;
import edu.neumont.dkramer.spoze3.util.TextResourceReader;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLSquare extends GLModel {
    private static final int VERTEX_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 4;
    private static final int STRIDE =
            ((VERTEX_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * GLVertexArray.BYTES_PER_FLOAT);

    private static final int POSITION_OFFSET = 0;
    private static final int COLOR_OFFSET = 3;

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


    public GLSquare(GLContext glContext) {
        super(glContext);
    }

    @Override
    protected GLProgram createGLProgram() {
        String vertexShaderCode =
                TextResourceReader.readTextFileFromResource(getGLContext(), R.raw.simple_vertex_shader);

        String fragmentShaderCode =
                TextResourceReader.readTextFileFromResource(getGLContext(), R.raw.simple_fragment_shader);

        GLProgram glProgram = GLProgram.create(vertexShaderCode, fragmentShaderCode);

        mPositionHandle = glProgram.bindAttribute("a_Position");
        mColorHandle = glProgram.bindAttribute("a_Color");
        mMVPMatrixHandle = glProgram.defineUniform("u_MVPMatrix");

        mVertexArray.setVertexAttribPointer(POSITION_OFFSET, mPositionHandle, VERTEX_COMPONENT_COUNT, STRIDE);
        mVertexArray.setVertexAttribPointer(COLOR_OFFSET, mColorHandle, COLOR_COMPONENT_COUNT, STRIDE);

        return glProgram;
    }

    @Override
    public void render(GLCamera camera) {
        mGLProgram.use();
        applyTransformations();

        camera.applyToModel(this);
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
    }

    @Override
    public float[] getVertexData() {
        return VERTEX_DATA;
    }
}
