package edu.neumont.dkramer.spoze3.gl;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import edu.neumont.dkramer.spoze3.R;
import edu.neumont.dkramer.spoze3.camera.Camera;
import edu.neumont.dkramer.spoze3.camera.CameraPreview;

/**
 * Created by dkramer on 10/23/17.
 */

public abstract class GLCameraActivity extends GLActivity {
	protected CameraPreview mCameraPreview;



	@Override
	protected void onStop() {
		super.onStop();
		mCameraPreview.stopPreviewing();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCameraPreview.startPreviewing();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mCameraPreview.stopPreviewing();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.gl_camera_layout;
	}

	@Override
	protected GLView createGLView() {
		GLView glView = findViewById(R.id.glView);
		return glView;
	}

	protected void init() {
		mCameraPreview = findViewById(R.id.cameraPreview);
		mCameraPreview.setCameraType(Camera.CAM_REAR);

		if (!hasGrantedCameraPermission()) {
			requestCameraPermission();
			return;
		}
		mCameraPreview.startPreviewing();
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
					mCameraPreview.stopPreviewing();
				    mCameraPreview.startPreviewing();
				}
				break;
		}
	}
}
