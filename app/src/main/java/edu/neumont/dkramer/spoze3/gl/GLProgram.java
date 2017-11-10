package edu.neumont.dkramer.spoze3.gl;

import java.util.HashMap;

import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindAttribLocation;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Created by dkramer on 10/20/17.
 */

public final class GLProgram {
    private static final int GL_PROGRAM_ERROR = 0;
    private static final int GL_LINK_ERROR = 0;
    private static final int GL_VALIDATION_ERROR = 0;


    private GLShader mVertexShader;
    private GLShader mFragmentShader;
    private int mProgramId;

    private HashMap<String, Integer> mAttributes;
    private HashMap<String, Integer> mUniforms;
    private int mAttribIndex = -1;


    private GLProgram(GLShader vertexShader, GLShader fragmentShader) {
        mAttributes = new HashMap<>();
        mUniforms = new HashMap<>();

        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
    }

    public static GLProgram create(String vertexShaderCode, String fragmentShaderCode) {
        GLShader vertexShader = GLShader.newVertexShader(vertexShaderCode).compile();
        GLShader fragmentShader = GLShader.newFragmentShader(fragmentShaderCode).compile();
        return new GLProgram(vertexShader, fragmentShader).build();
    }

    private GLProgram build() {
        mProgramId = link();

        if (validate(mProgramId) == GL_VALIDATION_ERROR) {
            int error = glGetError();
            throw new RuntimeException("GL Program validation error => " + error);
        }
        return this;
    }

    private int validate(final int programId) {
        glValidateProgram(programId);

        final int[] validateStatus = new int[1];
        glGetProgramiv(programId, GL_VALIDATE_STATUS, validateStatus, 0);

        int result = validateStatus[0];
        return result;
    }

    private int link() {
        final int programId = glCreateProgram();
        if (programId == GL_PROGRAM_ERROR) {
            // INVALID program
            throw new RuntimeException("GL Program creation failed");
        }
        glAttachShader(programId, mVertexShader.getId());
        glAttachShader(programId, mFragmentShader.getId());

        // join shaders
        glLinkProgram(programId);

        if (getLinkStatus(programId) == GL_LINK_ERROR) {
            glDeleteProgram(programId);
            throw new RuntimeException("GL Program link failed");
        }
        return programId;
    }

    private int getLinkStatus(final int programId) {
        final int[] linkStatus = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);
        int result = linkStatus[0];
        return result;
    }

    public int bindAttribute(String attributeName) {
        ++mAttribIndex;
        mAttributes.put(attributeName, mAttribIndex);
        glBindAttribLocation(getId(), mAttribIndex, attributeName);
        int handle = glGetAttribLocation(getId(), attributeName);
        return handle;
    }

    public int getAttributeIndex(String attributeName) {
        int index = mAttributes.get(attributeName);
        return index;
    }

    public int defineUniform(String name) {
        int uniformLocation = glGetUniformLocation(getId(), name);
        mUniforms.put(name, uniformLocation);
        return uniformLocation;
    }

    public void use() {
        glUseProgram(getId());
    }


    public int getId() {
        return mProgramId;
    }
}
