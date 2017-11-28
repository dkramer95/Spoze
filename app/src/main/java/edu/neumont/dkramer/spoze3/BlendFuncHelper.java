package edu.neumont.dkramer.spoze3;

import android.util.Log;

import static android.opengl.GLES10.GL_ONE_MINUS_DST_COLOR;
import static android.opengl.GLES10.GL_SRC_ALPHA;
import static android.opengl.GLES10.GL_ZERO;
import static android.opengl.GLES20.GL_DST_COLOR;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_COLOR;
import static android.opengl.GLES20.GL_SRC_COLOR;

/**
 * This class is a helper class to easily iterate through different OpenGL blend func modes.
 * It should not be used normally, but rather when trying to test a visual appearance.
 * Created by dkramer on 11/28/17.
 */

public class BlendFuncHelper {
	private static final String TAG = "BlendFunc";

	private static final int[] sBlendFuncs =
	{
		// src factor, destination factor
		GL_ZERO, GL_ZERO,
		GL_ZERO, GL_ONE,
		GL_ZERO, GL_SRC_COLOR,
		GL_ZERO, GL_ONE_MINUS_SRC_COLOR,
		GL_ZERO, GL_SRC_ALPHA,
		GL_ZERO, GL_ONE_MINUS_SRC_ALPHA,

		GL_ONE, GL_ZERO,
		GL_ONE, GL_ONE,
		GL_ONE, GL_SRC_COLOR,
		GL_ONE, GL_ONE_MINUS_SRC_COLOR,
		GL_ONE, GL_SRC_ALPHA,
		GL_ONE, GL_ONE_MINUS_SRC_ALPHA,

		GL_DST_COLOR, GL_ZERO,
		GL_DST_COLOR, GL_ONE,
		GL_DST_COLOR, GL_SRC_COLOR,
		GL_DST_COLOR, GL_ONE_MINUS_SRC_COLOR,
		GL_DST_COLOR, GL_SRC_ALPHA,
		GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA,

		GL_ONE_MINUS_DST_COLOR, GL_ZERO,
		GL_ONE_MINUS_DST_COLOR, GL_ONE,
		GL_ONE_MINUS_DST_COLOR, GL_SRC_COLOR,
		GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR,
		GL_ONE_MINUS_DST_COLOR, GL_SRC_ALPHA,
		GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA,

		GL_SRC_ALPHA, GL_ZERO,
		GL_SRC_ALPHA, GL_ONE,
		GL_SRC_ALPHA, GL_SRC_COLOR,
		GL_SRC_ALPHA, GL_ONE_MINUS_SRC_COLOR,
		GL_SRC_ALPHA, GL_SRC_ALPHA,
		GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA,

		GL_ONE_MINUS_SRC_ALPHA, GL_ZERO,
		GL_ONE_MINUS_SRC_ALPHA, GL_ONE,
		GL_ONE_MINUS_SRC_ALPHA, GL_SRC_COLOR,
		GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_SRC_COLOR,
		GL_ONE_MINUS_SRC_ALPHA, GL_SRC_ALPHA,
		GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA,
	};
	protected static int sIndex;
//	 static int[] sBlendFunc = nextBlendFunc();



	// advance through to the next blend func
	public static int[] nextBlendFunc() {
		if (sIndex + 2 >= sBlendFuncs.length) {
			sIndex = 0;
		}
		int[] blendFunc = new int[] { sBlendFuncs[sIndex], sBlendFuncs[sIndex + 1] };
		Log.i(TAG, String.format("Using blend func index => %d => [%d],[%d]\n", sIndex, blendFunc[0], blendFunc[1]));
		sIndex += 2;
//		sBlendFunc = blendFunc;
		return blendFunc;
	}
}
