package edu.neumont.dkramer.spoze3.models;

import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttrib1f;

/**
 * Created by dkramer on 10/26/17.
 */

public class GLPoint extends GLSquare {
	protected int mPointSizeHandle;

	protected GLPoint(GLContext glContext, float[] vertexData) {
		super(glContext, vertexData);
	}

	public static GLPoint create(GLContext glContext, float x, float y, float z) {
		final float[] vertexData = new float[] {
			x, y, z,
			1.0f, 0.0f, 0.0f, 1.0f,
		};
		return new GLPoint(glContext, vertexData);
	}

	@Override
	protected void bindHandles() {
		super.bindHandles();
		mPointSizeHandle = mGLProgram.bindAttribute("a_PointSize");
	}

	@Override
	protected void enableAttributes() {
		super.enableAttributes();
		glVertexAttrib1f(mPointSizeHandle, 100f);
	}

	@Override
	protected void draw(GLCamera camera) {
		glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		glDrawArrays(GL_POINTS, 0, 1);
	}
}
