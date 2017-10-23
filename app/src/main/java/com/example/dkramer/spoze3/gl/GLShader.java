package com.example.dkramer.spoze3.gl;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glShaderSource;

/**
 * Created by dkramer on 10/20/17.
 */

public final class GLShader {
    private static final int GL_COMPILE_ERROR = 0;

    private final int mType;
    private int mShaderId;
    private String mShaderCode;


    private GLShader(int type, String shaderCode) {
        mShaderCode = shaderCode;
        mType = type;
    }

    public static GLShader newVertexShader(String vertexShaderCode) {
        GLShader vertexShader = new GLShader(GL_VERTEX_SHADER, vertexShaderCode);
        return vertexShader;
    }

    public static GLShader newFragmentShader(String fragmentShaderCode) {
        GLShader fragmentShader = new GLShader(GL_FRAGMENT_SHADER, fragmentShaderCode);
        return fragmentShader;
    }

    public GLShader compile() {
        final int shaderObjectId = glCreateShader(getType());

        glShaderSource(shaderObjectId, getShaderCode());
        glCompileShader(shaderObjectId);

        if (getCompileStatus(shaderObjectId) == GL_COMPILE_ERROR) {
            int error = glGetError();
            Log.e("GLShader", "Shader compile error = " + error);
            glDeleteShader(shaderObjectId);
            return null;
        }
        mShaderId = shaderObjectId;
        return this;
    }

    protected int getCompileStatus(final int shaderObjectId) {
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
        int result = compileStatus[0];
        return result;
    }

    protected String getShaderCode() {
        return mShaderCode;
    }

    protected int getType() {
        return mType;
    }

    protected int getId() {
        return mShaderId;
    }

}
