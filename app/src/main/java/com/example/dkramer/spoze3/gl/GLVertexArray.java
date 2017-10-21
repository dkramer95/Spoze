package com.example.dkramer.spoze3.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by dkramer on 10/20/17.
 */

public class GLVertexArray {
    public static final int BYTES_PER_FLOAT = 4;

    protected float[] mData;
    protected final FloatBuffer mBuffer;

    protected GLVertexArray(float[] data) {
        mData = data;
        mBuffer = ByteBuffer
                .allocateDirect(data.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(data);
        mBuffer.rewind();
    }

    public void setVertexAttribPointer(int offset, int attribLocation, int componentCount, int stride) {
        mBuffer.position(offset);
        glVertexAttribPointer(attribLocation, componentCount, GL_FLOAT, false, stride, mBuffer);
        glEnableVertexAttribArray(attribLocation);
        mBuffer.rewind();
    }

    public float[] getData() {
        return mData;
    }

    public FloatBuffer getBuffer() {
        return mBuffer;
    }
}
