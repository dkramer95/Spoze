package edu.neumont.dkramer.spoze3.gl;

/**
 * Base class for GLObjects that utilize a GLContext for communication back and forth.
 * Created by dkramer on 10/25/17.
 */

public class GLObject {
	protected GLContext mGLContext;



	public GLObject(GLContext ctx) {
		mGLContext = ctx;
	}

	public GLContext getGLContext() {
		return mGLContext;
	}
}
