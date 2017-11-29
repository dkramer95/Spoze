package edu.neumont.dkramer.spoze3.gl;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Size;
import android.widget.Toast;

import edu.neumont.dkramer.spoze3.R;
import edu.neumont.dkramer.spoze3.camera.Camera;
import edu.neumont.dkramer.spoze3.camera.CameraPreview;

import static android.hardware.camera2.CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE;
import static android.hardware.camera2.CaptureRequest.CONTROL_AF_MODE;

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
		return findViewById(R.id.glView);
	}

	protected void init() {
		mCameraPreview = findViewById(R.id.cameraPreview);
		mCameraPreview.setCameraType(Camera.CAM_REAR);
//		mCameraPreview.setPreferredSize(getDisplaySize());
		mCameraPreview.setPreviewOption(CONTROL_AF_MODE, CONTROL_AF_MODE_CONTINUOUS_PICTURE);

		if (!hasGrantedCameraPermission()) {
			requestCameraPermission();
			//TODO try to make it so that activity can continue after permission is granted
			// rather than going back... Currently this does fix crash on first use w/ permission
			Toast.makeText(this, "You must grant permission to use the camera!", Toast.LENGTH_LONG).show();
			finish();
		}
		mCameraPreview.startPreviewing();
	}

	/**
	 * @return size of physical device display in pixels
	 */
	protected Size getDisplaySize() {
		Point outSize = new Point();
		getWindowManager().getDefaultDisplay().getSize(outSize);
		return new Size(outSize.x, outSize.y);
	}

	protected CameraPreview getCameraPreview() {
		return mCameraPreview;
	}

	/**
	 * Checks to see if we have been granted permission to use the camera
	 * @return true if we have permission, false otherwise
	 */
	protected boolean hasGrantedCameraPermission() {
		return ContextCompat.checkSelfPermission(this,
				Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
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
