package com.example.dkramer.spoze3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.dkramer.spoze3.gl.GLCamera;
import com.example.dkramer.spoze3.gl.GLContext;
import com.example.dkramer.spoze3.gl.GLModel;
import com.example.dkramer.spoze3.gl.GLProgram;
import com.example.dkramer.spoze3.gl.GLTexture;
import com.example.dkramer.spoze3.gl.GLWorld;
import com.example.dkramer.spoze3.util.TextResourceReader;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLUtils.texImage2D;
import static com.example.dkramer.spoze3.gl.GLVertexArray.BYTES_PER_FLOAT;

/**
 * Created by dkramer on 10/23/17.
 */

public class MyGLWorld2 extends GLWorld {

	public MyGLWorld2(GLContext glContext) {
		super(glContext);
	}

	@Override
	public void create() {
		final GLContext ctx = getGLContext();

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		Bitmap bmp = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.texture7, options);
		bmp = GLTexture.getFittedBitmap(bmp, getWidth(), getHeight());

		final int[] textureObjectIds = new int[1];
		glGenTextures(1, textureObjectIds, 0);
		int textureHandle = textureObjectIds[0];

		glBindTexture(GL_TEXTURE_2D, textureHandle);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		texImage2D(GL_TEXTURE_2D, 0, bmp, 0);

		final int STRIDE = ((2 * 2) * BYTES_PER_FLOAT);

		final float factor = Math.max(getWidth(), getHeight());

		final float w = (float)bmp.getWidth() / factor;
		final float h = (float)bmp.getHeight() / factor;

		final float test = (w / h);

		bmp.recycle();

		final float[] VERTEX_DATA = {
			w, -h, 1.0f, 1.0f,
			-w, -h, 0.0f, 1.0f,
			-w, h, 0.0f, 0.0f,
			w, h, 1.0f, 0.0f,
		};


		addModel(new GLModel(ctx) {
			int mPositionHandle;
			int mTextureHandle;
			int mTextureUniformHandle;
			int mTextureCoordinateHandle;
			int mMVPMatrixHandle;

			@Override
			protected GLProgram createGLProgram() {
				String vertexShaderCode =
						TextResourceReader.readTextFileFromResource(getGLContext(), R.raw.texture_vertex_shader);

				String fragmentShaderCode =
						TextResourceReader.readTextFileFromResource(getGLContext(), R.raw.texture_fragment_shader);

				GLProgram glProgram = GLProgram.create(vertexShaderCode, fragmentShaderCode);

				mPositionHandle = glProgram.bindAttribute("a_Position");
				mMVPMatrixHandle = glProgram.defineUniform("u_MVPMatrix");
				mTextureCoordinateHandle = glProgram.bindAttribute("a_TextureCoordinates");
				mTextureUniformHandle = glProgram.defineUniform("u_TextureUnit");

				mVertexArray.setVertexAttribPointer(0, mPositionHandle, 2, STRIDE);
				mVertexArray.setVertexAttribPointer(2, mTextureHandle, 2, STRIDE);

				return glProgram;
			}

			float angle;

			@Override
			public void render(GLCamera camera) {
				mGLProgram.use();
				applyTransformations();
				camera.rotate(angle, 1f, 1f, 1f);
				camera.applyToModel(this);
				++angle;
				glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
				glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
			}

			@Override
			public float[] getVertexData() {
				return VERTEX_DATA;
			}
		});
	}
}
