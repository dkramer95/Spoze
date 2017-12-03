package edu.neumont.dkramer.spoze3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import edu.neumont.dkramer.spoze3.gesture.DeviceShake;
import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLCameraActivity;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLModel;
import edu.neumont.dkramer.spoze3.gl.GLProgram;
import edu.neumont.dkramer.spoze3.gl.GLScene;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLRotationVectorInfo;
import edu.neumont.dkramer.spoze3.models.SignModel2;
import edu.neumont.dkramer.spoze3.scene.SignScene;
import edu.neumont.dkramer.spoze3.util.Preferences;

import static edu.neumont.dkramer.spoze3.VisualizationActivity.ToolbarManager.TOOLBAR_NORMAL;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ACCELEROMETER;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ROTATION_VECTOR;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.TOUCH_INPUT;
import static edu.neumont.dkramer.spoze3.util.Preferences.Key.SHAKE_ACTION;

/**
 * Created by dkramer on 11/14/17.
 */

public class VisualizationActivity extends GLCameraActivity {
	private static final String TAG = "VisualizationActivity";
	protected DeviceShake mDeviceShake;
	protected GalleryFragment mGalleryFragment;
	protected ModelFragment mModelFragment;
	protected ToolbarManager mToolbarManager;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadFromPreferences();
		loadToolbar();
		loadFragments();

		// potential incoming image from a "share"
		checkSharedImage();
	}

	protected void checkSharedImage() {
		Intent intent = getIntent();

		if (intent.hasExtra("imageURI")) {
			Uri imageURI = intent.getParcelableExtra("imageURI");
			Glide.with(this).load(imageURI).asBitmap().into(new SimpleTarget<Bitmap>() {
				@Override
				public void onResourceReady(Bitmap bmp, GlideAnimation animation) {
					GLWorld world = getGLContext().getGLView().getScene().getWorld();

					getGLContext().queueEvent(() -> {
						world.addModel(SignModel2.fromBitmap(getGLContext(), bmp, world.getWidth(), world.getHeight()));
					});
					calibrate();
				}
			});
		}
	}


	protected void loadFromPreferences() {
		initShakeAction();
	}

	protected void loadToolbar() {
		mToolbarManager = new ToolbarManager(findViewById(R.id.toolbarFlipper)).showToolbar(TOOLBAR_NORMAL);
	}

	public void closeGalleryButtonClicked(View view) {
		mGalleryFragment.hide();
		getToolbarManager().showToolbar(TOOLBAR_NORMAL);
	}

	public void showDirectorySpinnerButtonClicked(View view) {
		mGalleryFragment.showDirectorySpinner();
	}

	public void loadGalleryItemsButtonClicked(View view) {
		resumeGLRender();
		getToolbarManager().showToolbar(TOOLBAR_NORMAL);
		importGalleryItems(mGalleryFragment.getSelectedItems());

        mGalleryFragment.clearSelectedItems();
        mGalleryFragment.hide();
	}

	protected void importGalleryItems(List<GalleryItemView> galleryItems) {
		GLWorld world = getGLContext().getGLView().getScene().getWorld();

		for (GalleryItemView item : galleryItems) {
			Bitmap bmp = BitmapFactory.decodeFile(item.getResourceString());
			getGLContext().queueEvent(() -> {
				world.addModel(SignModel2.fromBitmap(getGLContext(), bmp, world.getWidth(), world.getHeight()));
			});
		}
		mModelFragment.setModelData(world.getModels());
	}

	public void calibrateButtonClicked(View view) {
	    calibrate();
		Toast.makeText(this, "Calibrated!", Toast.LENGTH_SHORT).show();
	}

	protected void calibrate() {
		GLRotationVectorInfo rotationVectorInfo
				= (GLRotationVectorInfo)getGLContext().getDeviceInfo(ROTATION_VECTOR);
		rotationVectorInfo.calibrate();
	}

	public void rotateClockwiseButtonClicked(View view) {
		rotateModel(-90);
	}

	public void rotateCounterClockwiseButtonClicked(View view) {
		rotateModel(90);
	}

	protected void rotateModel(float angle) {
		SignScene scene = (SignScene)getGLContext().getGLView().getScene();
		getGLContext().queueEvent(() -> {
			scene.getSelectedModel().rotate(angle);
		});
	}

	public void deleteModelButtonClicked(View view) {
		SignScene scene = (SignScene)getGLContext().getGLView().getScene();
		getGLContext().queueEvent(() -> {
			scene.deleteSelectedModel();
		});
		Toast.makeText(this, "Deleted Model", Toast.LENGTH_SHORT).show();
		getToolbarManager().showToolbar(TOOLBAR_NORMAL);
		mModelFragment.setModelData(scene.getWorld().getModels());
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
		getToolbarManager().hideToolbar();
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

			mModelFragment = new ModelFragment();
			getFragmentManager()
					.beginTransaction()
					.add(R.id.fragment_container, mModelFragment)
					.hide(mModelFragment)
					.commit();
			// testing -- should load from world
		}
	}

	public void TEST_LOAD_DATA() {
		// testing -- should load from world
		List<GLModel> modelTest = new ArrayList<>();

		for (int j = 0; j < 100; ++j) {
			modelTest.add(new GLModel(getGLContext(), new float[] { 1.0f, 1.0f, 1.0f }) {
				@Override
				protected GLProgram createGLProgram() {
					return null;
				}

				@Override
				protected void draw(GLCamera camera) {

				}

				@Override
				protected void bindHandles() {

				}

				@Override
				protected void enableAttributes() {

				}
			});
		}

		mModelFragment.setModelData(modelTest);
		mModelFragment.show();
//		showModelFragment();
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
				mDeviceShake = new DeviceShake(this, () -> clearScene());
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

	protected void clearScene() {
		getScene().getWorld().removeAllModels();
		Toast.makeText(this, "Models cleared from shake", Toast.LENGTH_SHORT).show();
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

	protected ToolbarManager getToolbarManager() {
		return mToolbarManager;
	}

	public void showModelFragment() {
		mModelFragment.show();
	}


	/* Class that handles switching between different toolbars */
	class ToolbarManager {
		public static final int TOOLBAR_OBJECT = 0;
		public static final int TOOLBAR_NORMAL = 1;


		private ViewFlipper mToolbarFlipper;

		public ToolbarManager(ViewFlipper flipper) {
			mToolbarFlipper = flipper;
		}

		public void hideToolbar() {
			mToolbarFlipper.animate().alpha(0).setDuration(250)
					.withEndAction(() -> mToolbarFlipper.setVisibility(View.INVISIBLE))
					.start();
		}

		public ToolbarManager showToolbar(int toolbar) {
			// quickly make visible, but fade out
			mToolbarFlipper.setVisibility(View.VISIBLE);
			mToolbarFlipper.animate().alpha(0).start();

			// fade in
			mToolbarFlipper.animate().alpha(1).setDuration(250)
					.withEndAction(() -> mToolbarFlipper.setDisplayedChild(toolbar))
					.start();
			return this;
		}
	}

}
