package com.example.dkramer.spoze3.models;

import com.example.dkramer.spoze3.R;
import com.example.dkramer.spoze3.gl.GLCamera;
import com.example.dkramer.spoze3.gl.GLContext;
import com.example.dkramer.spoze3.gl.GLModel;
import com.example.dkramer.spoze3.gl.GLProgram;
import com.example.dkramer.spoze3.util.TextResourceReader;
import com.example.dkramer.spoze3.util.TextureHelper;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static com.example.dkramer.spoze3.gl.GLVertexArray.BYTES_PER_FLOAT;

/**
 * Created by dkramer on 10/22/17.
 */

public class GLTexturedSquare extends GLModel {
    private static final int VERTEX_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COMPONENT_COUNT = 2;
    private static final int STRIDE =
            ((VERTEX_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * BYTES_PER_FLOAT);

    private static final int POSITION_OFFSET = 0;
    private static final int TEXTURE_OFFSET = 2;

    private static final float[] VERTEX_DATA = {
        // X, Y, S, T
         1.0f, -1.0f, 1.0f, 1.0f,
        -1.0f, -1.0f, 0.0f, 1.0f,
        -1.0f,  1.0f, 0.0f, 0.0f,
         1.0f,  1.0f, 0.0f, 1.0f,
    };

    protected int mPositionHandle;
    protected int mTextureHandle;
    protected int mMVPMatrixHandle;
    protected int mTextureUniformHandle;
    protected int mTextureCoordinateHandle;


    public GLTexturedSquare(GLContext glContext) {
        super(glContext);
    }

    @Override
    protected GLProgram createGLProgram() {
        String vertexShaderCode =
                TextResourceReader.readTextFileFromResource(getGLContext(), R.raw.texture_vertex_shader);

        String fragmentShaderCode =
                TextResourceReader.readTextFileFromResource(getGLContext(), R.raw.texture_fragment_shader);

        GLProgram glProgram = GLProgram.create(vertexShaderCode, fragmentShaderCode);

        mTextureHandle = TextureHelper.loadTexture(getGLContext(), R.drawable.texture);
        mPositionHandle = glProgram.bindAttribute("a_Position");
        mMVPMatrixHandle = glProgram.defineUniform("u_MVPMatrix");
        mTextureUniformHandle = glProgram.defineUniform("u_TextureUnit");
        mTextureCoordinateHandle = glProgram.bindAttribute("a_TextureCoordinate");

        mVertexArray.setVertexAttribPointer(POSITION_OFFSET, mPositionHandle, VERTEX_COMPONENT_COUNT, STRIDE);
        mVertexArray.setVertexAttribPointer(TEXTURE_OFFSET, mTextureHandle, TEXTURE_COMPONENT_COUNT, STRIDE);

        return glProgram;
    }

    @Override
    public void render(GLCamera camera) {
        mGLProgram.use();
        applyTransformations();
        glEnableVertexAttribArray(mPositionHandle);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTextureHandle);
        glUniform1i(mTextureUniformHandle, 0);

        glEnableVertexAttribArray(mPositionHandle);
        glEnableVertexAttribArray(mTextureCoordinateHandle);
        camera.applyToModel(this);

        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
    }

    @Override
    public float[] getVertexData() {
        return VERTEX_DATA;
    }
}
