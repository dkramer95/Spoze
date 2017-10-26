package edu.neumont.dkramer.spoze3.models;

import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLProgram;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttrib1f;

/**
 * Created by dkramer on 10/26/17.
 */

public class GLTrackPoints extends GLSquare {
	/**
	 * Pointer to the next available index that is based on the STRIDE
	 */
	private static int sIndexPointer;

	private static final int MAX_POINTS = 100;

	private static final int VERTEX_DATA_SIZE =
			((VERTEX_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * MAX_POINTS);

	private static final float[] VERTEX_DATA = new float[VERTEX_DATA_SIZE];

	/*
	 * Total points that exist
	 */
	private static int sPointCount = 0;

	private static float sPointSize = 50f;

	private static int sPointSizeHandle;

	private static GLTrackPoints sInstance;


	private GLTrackPoints(GLContext glContext) {
		super(glContext);
	}

	public static GLTrackPoints getInstance(GLContext glContext) {
		if (sInstance == null) {
			sInstance = new GLTrackPoints(glContext);
		}
		return sInstance;
	}

	@Override
	protected void createHandles(GLProgram glProgram) {
		super.createHandles(glProgram);
		sPointSizeHandle = glProgram.bindAttribute("a_PointSize");
	}

	@Override
	public void render(GLCamera camera) {
		mGLProgram.use();
		applyTransformations();
		camera.applyToModel(this);
		setVertexAttribPointers();
		glVertexAttrib1f(sPointSizeHandle, sPointSize);
		glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		glDrawArrays(GL_POINTS, 0, sPointCount);
	}

	@Override
	public float[] getVertexData() {
		return VERTEX_DATA;
	}

	public static float[] fromCoords(float x, float y, float z, float red, float green, float blue, float alpha) {
		return new float[] {
				x, y, z,
				red, green, blue, alpha
		};
	}

	public static void addPoint(float[] values) {
		if (values.length != (COLOR_COMPONENT_COUNT + VERTEX_COMPONENT_COUNT)) {
			throw new IllegalArgumentException("Values are not correct!");
		}

		for (int j = 0; j < values.length; ++j) {
			int dataIndex = sIndexPointer + j;
			VERTEX_DATA[dataIndex] = values[j];
		}
		sIndexPointer += (COLOR_COMPONENT_COUNT + VERTEX_COMPONENT_COUNT);
		++sPointCount;
	}
}
