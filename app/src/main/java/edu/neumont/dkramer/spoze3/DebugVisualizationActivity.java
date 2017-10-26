package edu.neumont.dkramer.spoze3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Toast;

import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLRotationVectorInfo;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ROTATION_VECTOR;

/**
 * Created by dkramer on 10/25/17.
 */

public class DebugVisualizationActivity extends VisualizationActivity {


    @Override
    protected Bitmap loadBitmap() {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.debug_texture);
        return bmp;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.gl_debug_motion_camera_layout;
    }

    public void calibrateButtonClicked(View view) {
        //TODO perform calibration
        GLRotationVectorInfo rotationVectorInfo
                = (GLRotationVectorInfo)getGLContext().getDeviceInfo(ROTATION_VECTOR);
        rotationVectorInfo.calibrate();

        Toast.makeText(this, "Calibrated!", Toast.LENGTH_SHORT).show();
    }
}
