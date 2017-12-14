
package edu.neumont.dkramer.spoze3;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import edu.neumont.dkramer.spoze3.fragments.GalleryFragment;
import edu.neumont.dkramer.spoze3.fragments.HelpFragment;
import edu.neumont.dkramer.spoze3.fragments.ImportFragment;
import edu.neumont.dkramer.spoze3.fragments.OverlayManager;
import edu.neumont.dkramer.spoze3.gesture.DeviceShake;
import edu.neumont.dkramer.spoze3.gl.GLCameraActivity;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLMotionCamera;
import edu.neumont.dkramer.spoze3.gl.GLScene;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLRotationVectorInfo;
import edu.neumont.dkramer.spoze3.models.SignModel2;
import edu.neumont.dkramer.spoze3.scene.SignScene;
import edu.neumont.dkramer.spoze3.toolbar.ToolbarManager;
import edu.neumont.dkramer.spoze3.toolbar.VisualizeToolbar;
import edu.neumont.dkramer.spoze3.util.AsyncImageSaver;
import edu.neumont.dkramer.spoze3.util.ImageSave;
import edu.neumont.dkramer.spoze3.util.Preferences;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ACCELEROMETER;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ROTATION_VECTOR;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.TOUCH_INPUT;
import static edu.neumont.dkramer.spoze3.util.Preferences.Key.SCREENSHOT_FORMAT;
import static edu.neumont.dkramer.spoze3.util.Preferences.Key.SCREENSHOT_QUALITY;
import static edu.neumont.dkramer.spoze3.util.Preferences.Key.SHAKE_ACTION;
import static edu.neumont.dkramer.spoze3.util.Preferences.Key.SHUTTER_SOUND_ENABLED;

/**
 * Created by dkramer on 11/14/17.
 */

public class VisualizationActivity extends GLCameraActivity implements Screenshot.ScreenshotCallback {
	private static final int REQUEST_MEDIA_PROJECTION = 1;

	private static final String TAG = "VisualizationActivity";
	protected DeviceShake mDeviceShake;

	// fragments
	protected GalleryFragment mGalleryFragment;
	protected ModelFragment mModelFragment;
	protected HelpFragment mHelpFragment;
	protected ImportFragment mImportFragment;

	protected ToolbarManager mToolbarManager;
	protected View mScreenshotView;
	protected static boolean sCanTakeScreenshot = true;
	protected static boolean sMotionEnabled;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// potential incoming image from a "share"
		checkSharedImage();
	}

	protected void init() {
		super.init();
		initToolbar();
		loadFragments();
		loadFromPreferences();
		initScreenshot();
		initMotion();
	}

	protected void initMotion() {
		ImageButton button = findViewById(R.id.motionButton);
		sMotionEnabled = true;
		button.setColorFilter(getResources().getColor(R.color.yellow));
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();
		Screenshot.getInstance().destroy();
	}

	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}

	protected void onResume() {
		super.onResume();
		checkSharedImage();
	}

	protected void initScreenshot() {
		MediaProjectionManager mediaProjectionManager =
				(MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE);
		startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
	}

	protected void checkSharedImage() {
		Intent intent = getIntent();

		if (intent.hasExtra("imageURI")) {
			Uri imageURI = intent.getParcelableExtra("imageURI");
			mImportFragment.setResource(imageURI);

			new Handler().postDelayed(() -> {
			    mImportFragment.fadeIn();
			}, 1000);
		}
	}

	public void importBitmap(Bitmap bmp) {
		GLWorld world = getGLContext().getGLView().getScene().getWorld();

		getGLContext().queueEvent(() -> {
			world.addModel(SignModel2.fromBitmap(getGLContext(), bmp, world.getWidth(), world.getHeight()));
		});
		calibrate();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            takeScreenshot();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void takeScreenshot() {
		if (canTakeScreenshot()) {
			captureScreenshot();
		}
	}

	protected void captureScreenshot() {
		// ensure user has granted permission so we can capture screen
		if (!Screenshot.isInitialized()) {
			initScreenshot();
			return;
		}

		// preserve old visibility of nav to restore after screenshot
		int oldVisibility = getWindow().getDecorView().getSystemUiVisibility();

		// preserve state of motion
		boolean oldMotion = sMotionEnabled;
		hideSoftNavButtons();

		getToolbarManager().fadeOutToolbar(() -> {
			sCanTakeScreenshot = false;
			setMotionEnabled(false);
			Screenshot.getInstance().capture(this);

			new Handler().postDelayed(() -> {
				// fade in screenshot flash
				mScreenshotView.setVisibility(View.VISIBLE);
				mScreenshotView.setAlpha(0f);
				mScreenshotView.animate().alpha(1f).setDuration(250).withEndAction(() -> {
					if (Preferences.getBoolean(SHUTTER_SOUND_ENABLED, true)) {
						// play shutter sound
						MediaPlayer player = MediaPlayer.create(this, R.raw.shutter);
						player.start();
					}

					// fade out flash
					mScreenshotView.animate().alpha(0).setDuration(250).withEndAction(() -> {
						mScreenshotView.setVisibility(View.GONE);

						// fade in rest of UI
						getToolbarManager().fadeInToolbar();
						getWindow().getDecorView().setSystemUiVisibility(oldVisibility);

						// ensure we allow future screenshots since we're done
						sCanTakeScreenshot = true;
						setMotionEnabled(oldMotion);
					});
				}).start();
			}, 500);
		});
	}


	protected boolean canTakeScreenshot() {
		return sCanTakeScreenshot && !OverlayManager.isOverlayShowing();
	}

	protected void hideSoftNavButtons() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_MEDIA_PROJECTION) {
			if (resultCode == RESULT_OK) {
				int width = getWindow().getDecorView().getRootView().getWidth();
				int height = getWindow().getDecorView().getRootView().getHeight();
				Screenshot.getInstance().setSize(width, height).init(this, resultCode, data, this);
				mScreenshotView = findViewById(R.id.screenshotView);

			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Please grant permission to enable screenshot capturing", Toast.LENGTH_LONG).show();
			}
		}
	}

	// TODO allow custom save directory
	public void saveBitmap(Bitmap bmp, String path, String ext, int quality) {
        String filename = String.format("%d.%s", System.currentTimeMillis(), ext);
		ImageSave imgSave = new ImageSave(bmp, path, filename, ext, quality);
	    new AsyncImageSaver().execute(imgSave);
	}


	@Override
	public void onScreenshot(Bitmap bmp) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "SpozeCaptures";
	    saveBitmap(bmp, path, Preferences.getString(SCREENSHOT_FORMAT, "jpeg"), Preferences.getInt(SCREENSHOT_QUALITY, 75));
	}

	public String getSavedImportsDir() {
		String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "SpozeImported";
		File f = new File(path);
		f.mkdirs();
	    return path;
	}

	protected void loadFromPreferences() {
		initShakeAction();
	}

	protected void initToolbar() {
		mToolbarManager = findViewById(R.id.toolbarManager);
		mToolbarManager.loadToolbars(VisualizeToolbar.values());
		mToolbarManager.setToolbar(VisualizeToolbar.NORMAL);
	}

	public void closeGalleryButtonClicked(View view) {
		mGalleryFragment.hide();
		getToolbarManager().setToolbar(VisualizeToolbar.NORMAL);
	}

	public void blankAreaClicked(View view) {
	    // unused... this is a workaround for clicking around in toolbar blank area
        // to consume touch event so that selection doesn't cancel inadvertently
	}

	public void loadGalleryItemsButtonClicked(View view) {
		resumeGLRender();
		view.setEnabled(false);
		getToolbarManager().setToolbar(VisualizeToolbar.NORMAL);
		importGalleryItems(mGalleryFragment.getSelectedItems());

        mGalleryFragment.clearSelectedItems();
        mGalleryFragment.hide();
	}

	protected void importGalleryItems(List<GalleryItemView> galleryItems) {
		GLWorld world = getGLContext().getGLView().getScene().getWorld();

		for (GalleryItemView item : galleryItems) {
            Bitmap bmp = BitmapFactory.decodeFile(item.getResourceString());

            if (bmp != null) {
				getGLContext().queueEvent(() -> {
					world.addModel(SignModel2.fromBitmap(getGLContext(), bmp, world.getWidth(), world.getHeight()));
				});
			}
		}
		mModelFragment.setModelData(world.getModels());
	}

	public void calibrateButtonClicked(View view) {
	    calibrate();
		Toast.makeText(this, "Calibrated!", Toast.LENGTH_SHORT).show();
	}

	public void motionButtonClicked(View view) {
		ImageButton button = (ImageButton)view;
		// toggle
		if (sMotionEnabled) {
			sMotionEnabled = false;
			button.setColorFilter(getResources().getColor(R.color.disabled_gray));
			Toast.makeText(this, "Motion disabled!", Toast.LENGTH_SHORT).show();
		} else {
			sMotionEnabled = true;
			button.setColorFilter(getResources().getColor(R.color.yellow));
			Toast.makeText(this, "Motion enabled!", Toast.LENGTH_SHORT).show();
		}
		setMotionEnabled(sMotionEnabled);
	}

	public void setMotionEnabled(boolean value) {
		sMotionEnabled = value;
		GLMotionCamera camera = (GLMotionCamera)getGLView().getScene().getCamera();
		camera.setUpdateMotion(sMotionEnabled);
	}

	public void screenshotButtonClicked(View view) {
		takeScreenshot();
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
		getToolbarManager().setToolbar(VisualizeToolbar.NORMAL);
		mModelFragment.setModelData(scene.getWorld().getModels());
	}

	public void deleteItemButtonClicked(View view) {
		mGalleryFragment.delete(view);
		Log.i(TAG, "Should delete item");
	}

	public void importButtonClicked(View view) {
		pauseGLRender();
		getToolbarManager().setToolbar(VisualizeToolbar.GALLERY);
		showGalleryFragment();
	}

	protected void showGalleryFragment() {
	    mGalleryFragment.fadeIn();
		mGalleryFragment.initGalleryView();
	}

	protected void loadFragments() {
		if (findViewById(R.id.fragment_container) != null) {
			mGalleryFragment = new GalleryFragment();
			addFragment(mGalleryFragment);

			mModelFragment = new ModelFragment();
			addFragment(mModelFragment);

			mImportFragment = new ImportFragment();
			addFragment(mImportFragment);

			mHelpFragment = new HelpFragment();
			addFragment(mHelpFragment);
		}
	}

	protected void addFragment(Fragment fragment) {
		getFragmentManager()
				.beginTransaction()
				.add(R.id.fragment_container, fragment)
				.hide(fragment)
				.commit();
	}

	public void initShakeAction() {
		// clear any previous action if exists
		if (mDeviceShake != null) {
			mDeviceShake.stopListening();
		}
		mDeviceShake = getShakeAction();
	}

	protected DeviceShake getShakeAction() {
		String shakeAction = Preferences.getString(SHAKE_ACTION, "Show Help").toUpperCase();
		switch (shakeAction) {
			case "SHOW HELP":
				return new DeviceShake(this, mHelpFragment);
			case "CLEAR SCENE":
				return new DeviceShake(this, () -> clearScene());
			case "NONE":
			default:
				return new DeviceShake(this, () -> { });
		}
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

}
