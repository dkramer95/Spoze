package edu.neumont.dkramer.spoze3;

import android.app.Fragment;
import android.app.FragmentTransaction;
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

import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLRotationVectorInfo;
import edu.neumont.dkramer.spoze3.scene.SignScene;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ROTATION_VECTOR;

/**
 * Created by dkramer on 10/25/17.
 */

public class DebugVisualizationActivity extends VisualizationActivity {
    private static final String TAG = "VisualizationActivity";

    protected SeekBar mThresholdSeekBar;
    protected TextView mThresholdTextView;
    protected Fragment mGalleryFragment;
    protected ViewFlipper mToolbarFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        findViewById(R.id.hiddenOverlay).setVisibility(View.INVISIBLE);
        mToolbarFlipper = findViewById(R.id.toolbarFlipper);
        mToolbarFlipper.setDisplayedChild(1);
        mGalleryFragment = getFragmentManager().findFragmentById(R.id.gallery);

        getFragmentManager().beginTransaction()
                .hide(mGalleryFragment)
                .commit();
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

    public void hiddenButtonClicked(View view) {
        getGLContext().getGLView().setVisibility(View.VISIBLE);
        getGLContext().getGLView().setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//        findViewById(R.id.hiddenOverlay).setVisibility(View.INVISIBLE);
    }

    public void importButtonClicked(View view) {
        getGLContext().getGLView().setVisibility(View.GONE);

        // pause rendering
        getGLContext().getGLView().setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mToolbarFlipper.setVisibility(View.GONE);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .show(mGalleryFragment)
                .commit();
    }
}
