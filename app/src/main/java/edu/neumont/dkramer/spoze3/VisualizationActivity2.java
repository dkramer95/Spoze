package edu.neumont.dkramer.spoze3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.List;

import edu.neumont.dkramer.spoze3.gesture.DeviceShake;
import edu.neumont.dkramer.spoze3.gl.GLCameraActivity;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLScene;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLRotationVectorInfo;
import edu.neumont.dkramer.spoze3.models.SignModel2;
import edu.neumont.dkramer.spoze3.scene.SignScene;
import edu.neumont.dkramer.spoze3.util.Preferences;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ACCELEROMETER;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ROTATION_VECTOR;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.TOUCH_INPUT;
import static edu.neumont.dkramer.spoze3.util.Preferences.Key.SHAKE_ACTION;

/**
 * Created by dkramer on 11/14/17.
 */

public class VisualizationActivity2 extends GLCameraActivity {
	private static final String TAG = "VisualizationActivity";
	protected DeviceShake mDeviceShake;
	protected GalleryFragment mGalleryFragment;
	protected ViewFlipper mToolbarFlipper;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadFromPreferences();
		loadToolbar();
		loadFragments();
	}

	protected void loadFromPreferences() {
		initShakeAction();
	}

	protected void loadToolbar() {
		mToolbarFlipper = findViewById(R.id.toolbarFlipper);
		mToolbarFlipper.setDisplayedChild(1);
	}

	public void closeGalleryButtonClicked(View view) {
		mGalleryFragment.hide();
		mToolbarFlipper.setDisplayedChild(1);
		mToolbarFlipper.setVisibility(View.VISIBLE);
	}

	public void loadGalleryItemsButtonClicked(View view) {
		resumeGLRender();
		mToolbarFlipper.setVisibility(View.VISIBLE);
		mToolbarFlipper.setDisplayedChild(1);

        List<GalleryItemView> items = mGalleryFragment.getSelectedItems();
        GLWorld world = getGLContext().getGLView().getScene().getWorld();

        for (GalleryItemView item : items) {
            Bitmap bmp = BitmapFactory.decodeFile(item.getResourceString());
            getGLContext().queueEvent(() -> {
                world.addModel(SignModel2.fromBitmap(getGLContext(), bmp, world.getWidth(), world.getHeight()));
            });
        }
        mGalleryFragment.clearSelectedItems();
        mGalleryFragment.hide();
	}

	public void calibrateButtonClicked(View view) {
		GLRotationVectorInfo rotationVectorInfo
				= (GLRotationVectorInfo)getGLContext().getDeviceInfo(ROTATION_VECTOR);
		rotationVectorInfo.calibrate();

		Toast.makeText(this, "Calibrated!", Toast.LENGTH_SHORT).show();
	}

	public void rotateClockwiseButtonClicked(View view) {
		SignScene scene = (SignScene)getGLContext().getGLView().getScene();
		getGLContext().queueEvent(() -> {
			scene.getSelectedModel().rotate(-90);
		});
	}

	public void rotateCounterClockwiseButtonClicked(View view) {
		SignScene scene = (SignScene)getGLContext().getGLView().getScene();
		getGLContext().queueEvent(() -> {
			scene.getSelectedModel().rotate(90);
		});
	}

	public void deleteModelButtonClicked(View view) {
		SignScene scene = (SignScene)getGLContext().getGLView().getScene();
		getGLContext().queueEvent(() -> {
			scene.deleteSelectedModel();
		});
		Toast.makeText(this, "Deleted Model", Toast.LENGTH_SHORT).show();
		mToolbarFlipper.setDisplayedChild(1);
	}

	public void deleteItemButtonClicked(View view) {
		mGalleryFragment.delete(view);
		Log.i(TAG, "Should delete item");
	}

	protected void pauseGLRender() {
		getGLContext().getGLView().setVisibility(View.GONE);
		getGLContext().getGLView().setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	protected void resumeGLRender() {
		getGLContext().getGLView().setVisibility(View.VISIBLE);
		getGLContext().getGLView().setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

	public void importButtonClicked(View view) {
		pauseGLRender();
		mToolbarFlipper.setVisibility(View.GONE);
		showGalleryFragment();
	}

	protected void showGalleryFragment() {
		getFragmentManager().beginTransaction()
				.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
				.show(mGalleryFragment)
				.commit();
	}

	protected void loadFragments() {
		if (findViewById(R.id.fragment_container) != null) {
			mGalleryFragment = (GalleryFragment) getFragmentManager().findFragmentById(R.id.gallery);
			getFragmentManager()
					.beginTransaction()
					.hide(mGalleryFragment)
					.commit();
		}
	}

	public void initShakeAction() {
		// clear any previous action if exists
		if (mDeviceShake != null) {
			mDeviceShake.stopListening();
		}

		String shakeAction = Preferences.getString(SHAKE_ACTION, "Show Help").toUpperCase();
		switch (shakeAction) {
			case "SHOW HELP":
				mDeviceShake = new DeviceShake(this, loadHelpFragment());
				break;
			case "CLEAR SCENE":
				mDeviceShake = new DeviceShake(this, () -> getScene().getWorld().removeAllModels());
				break;
			case "NONE":
				default:
				mDeviceShake = new DeviceShake(this, () -> { });
				break;
		}
	}

	protected HelpFragment loadHelpFragment() {
		HelpFragment helpFragment = new HelpFragment();
		getFragmentManager()
				.beginTransaction()
				.add(R.id.fragment_container, helpFragment)
				.hide(helpFragment)
				.commit();
		return helpFragment;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.gl_debug_motion_camera_layout_overlay;
	}

	@Override
	protected GLContext createGLContext() {
		GLContext ctx = new GLContext(this);
		ctx.enableDeviceInfo(ROTATION_VECTOR);
		ctx.enableDeviceInfo(ACCELEROMETER);
		ctx.enableDeviceInfo(TOUCH_INPUT);
		return ctx;
	}

	@Override
	protected GLScene createGLScene() {
		return new SignScene(getGLContext());
	}
}
