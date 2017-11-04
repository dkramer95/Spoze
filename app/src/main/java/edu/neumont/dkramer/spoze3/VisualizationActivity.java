package edu.neumont.dkramer.spoze3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.IOException;

import edu.neumont.dkramer.spoze3.gl.GLCameraActivity;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLScene;
import edu.neumont.dkramer.spoze3.models.SignModel;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ROTATION_VECTOR;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.TOUCH_INPUT;

public class VisualizationActivity extends GLCameraActivity {
    protected Bitmap mBitmap;
//    protected Bitmap mTestBitmap;
    protected Bitmap mTest2Bitmap;
    protected Bitmap mTest3Bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mTestBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.texture_2);
        mTest2Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.texture_3);
        mTest3Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.texture_4);

//        mBitmap = loadBitmap();
    }

    protected Bitmap loadBitmap() {
        Uri uri = getIntent().getData();
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to open image", Toast.LENGTH_LONG).show();
            finish();
        }
        return bmp;
    }

    @Override
    protected GLContext createGLContext() {
        GLContext ctx = new GLContext(this);
        ctx.enableDeviceInfo(ROTATION_VECTOR);
//        ctx.enableDeviceInfo(ACCELEROMETER);
        ctx.enableDeviceInfo(TOUCH_INPUT);
        return ctx;
    }

    @Override
    protected GLScene createGLScene() {
        final GLContext ctx = getGLContext();

        TouchableWorld world = new TouchableWorld(ctx) {
        	@Override
            public void create() {
                super.create();
//                SignModel.createInBackground(this, mBitmap, getWidth(), getHeight());

                // testing additional models
//                SignModel.createInBackground(this, mTestBitmap, getWidth(), getHeight());
//                SignModel.createInBackground(this, mTest2Bitmap, getWidth(), getHeight());
                // testing same image
                SignModel.createInBackground(this, mTest2Bitmap, getWidth(), getHeight());
                SignModel.createInBackground(this, mTest3Bitmap, getWidth(), getHeight());
                SignModel.createInBackground(this, mTest3Bitmap, getWidth(), getHeight());
            }
        };

        return new GLScene.Builder(ctx)
                .setWorld(world)
		        .build();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.gl_camera_layout;
//        return R.layout.gl_motion_camera_layout;
    }
}
