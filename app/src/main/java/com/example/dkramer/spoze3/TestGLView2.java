package com.example.dkramer.spoze3;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.example.dkramer.spoze3.gl.GLContext;
import com.example.dkramer.spoze3.gl.GLRenderer;
import com.example.dkramer.spoze3.gl.GLScene;

/**
 * Created by dkramer on 10/23/17.
 */

public class TestGLView2 extends GLSurfaceView {
	protected GLContext mGLContext;
	protected GLRenderer mRenderer;
	protected GLScene mScene;

	public TestGLView2(Context context) {
		super(context);
	}

	public TestGLView2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Method to be implemented that performs all the initialization work
	 * to create a scene.
	 */
	public GLScene createScene() {
		GLScene scene = new GLScene(new MyGLWorld2(getGLContext()));
		return scene;
	}

//	public void init() {
//		mScene = createScene();
//		mRenderer = new GLRenderer(this);
//
//		setEGLContextClientVersion(2);
//		setZOrderOnTop(true);
//		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
//		getHolder().setFormat(PixelFormat.RGBA_8888);
//		setRenderer(mRenderer);
//	}

	public GLScene getScene() {
		return mScene;
	}

	public GLRenderer getRenderer() {
		return mRenderer;
	}

	public GLContext getGLContext() {
		return mGLContext;
	}
}
