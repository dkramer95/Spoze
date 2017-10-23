package com.example.dkramer.spoze3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.dkramer.spoze3.gl.GLCamera;
import com.example.dkramer.spoze3.gl.GLContext;
import com.example.dkramer.spoze3.gl.GLModel;
import com.example.dkramer.spoze3.gl.GLProgram;
import com.example.dkramer.spoze3.gl.GLTexture;
import com.example.dkramer.spoze3.gl.GLView;
import com.example.dkramer.spoze3.gl.GLWorld;
import com.example.dkramer.spoze3.util.TextResourceReader;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static com.example.dkramer.spoze3.gl.GLTexture.createFromBitmap;
import static com.example.dkramer.spoze3.gl.GLVertexArray.BYTES_PER_FLOAT;

/**
 * Created by dkramer on 10/20/17.
 */

public class MyGLWorld extends GLWorld {

    public MyGLWorld(GLContext glContext) {
        super(glContext);
    }

    @Override
    public void create() {
        GLContext ctx = getGLContext();

        Bitmap bmp = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.texture4);
        bmp = GLTexture.getFittedBitmap(bmp, getWidth(), getHeight());
        final Bitmap textureBmp = bmp;

        float factor = Math.max(getWidth(), getHeight());

        final float w = Math.abs((bmp.getWidth() / factor) * 2 - 1);
        final float h = Math.abs((bmp.getHeight() / factor) * 2 - 1);

        final int VERTEX_COMPONENT_COUNT = 2;
        final int TEXTURE_COMPONENT_COUNT = 2;
        final int STRIDE = ((VERTEX_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * BYTES_PER_FLOAT);

        final int POSITION_OFFSET = 0;
        final int TEXTURE_OFFSET = 2;

//        final float[] VERTEX_DATA = new float[] {
//                // X, Y, S, T
//                w, -h, 1.0f, 1.0f,
//                -w, -h, 0.0f, 0.0f,
//                -w, h, 0.0f, 0.0f,
//                w, h, 0.0f, 0.0f,
//        };

        addModel(new GLModel(ctx) {

            GLTexture mTexture;

            int mPositionHandle;
            int mTextureHandle;
            int mMVPMatrixHandle;
            int mTextureUniformHandle;
            int mTextureCoordinateHandle;

            @Override
            protected GLProgram createGLProgram() {
                String vertexShaderCode =
                        TextResourceReader.readTextFileFromResource(ctx, R.raw.texture_vertex_shader);

                String fragmentShaderCode =
                        TextResourceReader.readTextFileFromResource(ctx, R.raw.texture_fragment_shader);

                GLProgram glProgram = GLProgram.create(vertexShaderCode, fragmentShaderCode);
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;

                mTexture = createFromBitmap(textureBmp);
                mTextureHandle = mTexture.getTextureId();

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
                return new float[] {
                    // X, Y, S, T

                    //TODO ?? unsure how S,T coordinate mappings work for vertices not [-1, 1]??
                        w, -h, 1.0f, 1.0f,
                        -w, -h, 0.0f, 1.0f,
                        -w, h, 0.0f, 0.0f,
                        w, h, 0.0f, 1.0f
                };
            }
        });
    }
}
