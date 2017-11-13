package edu.neumont.dkramer.spoze3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.List;

import edu.neumont.dkramer.spoze3.gesture.DeviceShake;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLRotationVectorInfo;
import edu.neumont.dkramer.spoze3.models.SignModel2;
import edu.neumont.dkramer.spoze3.scene.SignScene;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ACCELEROMETER;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ROTATION_VECTOR;

/**
 * Created by dkramer on 10/25/17.
 */

public class DebugVisualizationActivity extends VisualizationActivity implements DeviceShake.OnShakeListener {
    private static final String TAG = "VisualizationActivity";

    protected SeekBar mThresholdSeekBar;
    protected TextView mThresholdTextView;
    protected GalleryFragment mGalleryFragment;
    protected ViewFlipper mToolbarFlipper;
    protected DeviceShake mDeviceShake;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        findViewById(R.id.hiddenOverlay).setVisibility(View.INVISIBLE);
        mToolbarFlipper = findViewById(R.id.toolbarFlipper);
        mToolbarFlipper.setDisplayedChild(1);
        mGalleryFragment = (GalleryFragment) getFragmentManager().findFragmentById(R.id.gallery);

        getFragmentManager().beginTransaction()
                .hide(mGalleryFragment)
                .commit();

        mDeviceShake = new DeviceShake(this);

        getGLContext().getDeviceInfo(ACCELEROMETER).addOnUpdateListener(mDeviceShake);

//        mDeviceShake = new DeviceShake(this, () -> {
////            Log.i("SHAKE", "Device Shake Detected");
//            foo();
//        }).register();
//        initThresholdSeekbar();
    }

//    protected void initThresholdSeekbar() {
//        mThresholdSeekBar = (SeekBar)findViewById(R.id.thresholdSeekBar);
//        mThresholdTextView = (TextView)findViewById(R.id.thresholdTextView);
//
//        mThresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
//                YUVToRGBAConverter.setThreshold(value);
//                mThresholdTextView.setText("" + value);
//            }
//
//            // unused
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) { }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) { }
//        });
//    }


    @Override
    protected Bitmap loadBitmap() {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.debug_texture);
        return bmp;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.gl_debug_motion_camera_layout_overlay;
    }


    public void calibrateButtonClicked(View view) {
        //TODO perform calibration
        GLRotationVectorInfo rotationVectorInfo
                = (GLRotationVectorInfo)getGLContext().getDeviceInfo(ROTATION_VECTOR);
        rotationVectorInfo.calibrate();

        Toast.makeText(this, "Calibrated!", Toast.LENGTH_SHORT).show();
    }

    public void closeButtonClicked(View view) {
        mToolbarFlipper.setVisibility(View.VISIBLE);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .hide(mGalleryFragment)
                .commit();
        getGLContext().getGLView().setVisibility(View.VISIBLE);
        getGLContext().getGLView().setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void deleteModelButtonClicked(View view) {
        SignScene scene = (SignScene)getGLContext().getGLView().getScene();
        getGLContext().queueEvent(() -> {
            scene.deleteSelectedModel();
        });
        Toast.makeText(this, "Deleted Model", Toast.LENGTH_SHORT).show();
        mToolbarFlipper.setDisplayedChild(1);
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

    public void deleteItemButtonClicked(View view) {
        mGalleryFragment.delete(view);
        Log.i(TAG, "Should delete item");
    }

    public void deleteSelectedButtonClicked(View view) {
        mGalleryFragment.deleteSelected();
    }

    public void loadSelectedButtonClicked(View view) {
        closeButtonClicked(view);

        List<GalleryItemView> items = mGalleryFragment.getSelectedItems();
        GLWorld world = getGLContext().getGLView().getScene().getWorld();


        for (GalleryItemView item : items) {
            Bitmap bmp = BitmapFactory.decodeFile(item.getResourceString());
            getGLContext().queueEvent(() -> {
                world.addModel(SignModel2.fromBitmap(getGLContext(), bmp, world.getWidth(), world.getHeight()));
            });
        }
        mGalleryFragment.clearSelectedItems();
    }

    public void hiddenButtonClicked(View view) {
        getGLContext().getGLView().setVisibility(View.VISIBLE);
        getGLContext().getGLView().setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//        findViewById(R.id.hiddenOverlay).setVisibility(View.INVISIBLE);
    }


    /** USING FOR EXPERIMENTAL FEATURES such as SHAKE, and SWIPE... just so that something happens **/
    /* TODO this should be better named, and used properly after experimental phase is over! :) */
    public void foo() {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .show(mGalleryFragment)
                .commit();
    }

    public void importButtonClicked(View view) {
        getGLContext().getGLView().setVisibility(View.GONE);

        // pause rendering
        getGLContext().getGLView().setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mToolbarFlipper.setVisibility(View.GONE);
        foo();
//        getFragmentManager().beginTransaction()
//                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
//                .show(mGalleryFragment)
//                .commit();
    }

    @Override
    public void onShake() {
        foo();
    }
}
