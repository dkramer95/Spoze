package com.example.dkramer.spoze3.gl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.dkramer.spoze3.MyGLView;
import com.example.dkramer.spoze3.R;
import com.example.dkramer.spoze3.TestGLView2;
import com.example.dkramer.spoze3.camera.Camera;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by dkramer on 10/23/17.
 */

public abstract class GLCameraActivity extends GLActivity {
	protected Camera mCamera;
	protected GLView mTestView;
	protected SurfaceView mCameraPreview;

	@Override
	protected void onStop() {
		super.onStop();
		closeCameraPreview();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_layout);
		mTestView = findViewById(R.id.glView2);
		mTestView.init(getGLContext());
		init();
		mGLContext.onStart();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.test_layout;
//		return R.layout.activity_camera_opengl;
	}

	protected void initViewDisplay() {
		mGLContext = new GLContext(this);
//		setContentView(getLayoutId());

//		GLView glView = (GLView)findViewById(R.id.glView);
//		glView.init();
//		mGLContext.setGLView(glView);

	}

	protected void init() {
		if (!hasGrantedCameraPermission()) {
			requestCameraPermission();
			return;
		}
		openCameraPreview();
	}

	protected void openCameraPreview() {
		final Context ctx = this;

		mCameraPreview = findViewById(R.id.cameraPreview);
		mCameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				try {
					mCamera = Camera.acquire(ctx, Camera.CAM_REAR);
					mCamera.addTarget(holder.getSurface());
					mCamera.open();
				} catch (CameraAccessException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) { }

			@Override
			public void surfaceDestroyed(SurfaceHolder surfaceHolder) { }
		});
	}

	protected void closeCameraPreview() {
		if (mCamera != null) {
			try {
				mCamera.close();
				mCamera = null;
			} catch (CameraAccessException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Checks to see if we have been granted permission to use the camera
	 * @return true if we have permission, false otherwise
	 */
	protected boolean hasGrantedCameraPermission() {
		boolean result = ContextCompat.checkSelfPermission(this,
				Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
		return result;
	}

	/**
	 * Displays popup to user to request permission to use camera
	 */
	protected void requestCameraPermission() {
		ActivityCompat.requestPermissions(this,
				new String[] { Manifest.permission.CAMERA }, Camera.PERMISSION_CODE);
	}

	/**
	 * Handles receiving a permission result
	 * @param requestCode - Code for the permission we're requesting
	 * @param permissions
	 * @param grantResults
	 */
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case Camera.PERMISSION_CODE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					closeCameraPreview();
					openCameraPreview();
				}
				break;
		}
	}
}
