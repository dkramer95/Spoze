package edu.neumont.dkramer.spoze3;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.IOException;

import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLCameraActivity;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLDeviceInfo;
import edu.neumont.dkramer.spoze3.gl.GLScene;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.models.GLTexturedRect;

public class VisualizationActivity extends GLCameraActivity {
    protected Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBitmap = loadBitmap(getIntent().getData());
    }

    protected Bitmap loadBitmap(Uri uri) {
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
        ctx.enableDeviceInfo(GLDeviceInfo.TYPE__ROTATION_VECTOR);
        return ctx;
    }

    @Override
    protected GLScene createGLScene() {
        final GLContext ctx = getGLContext();
        GLScene scene = new GLScene(new GLWorld(ctx) {
            float angle = 0f;
            @Override
            public void create() {
                addModel(GLTexturedRect.createFromBitmap(ctx, mBitmap, getWidth(), getHeight()));
            }

            @Override
            public void render(GLCamera camera) {
                ++angle;
                camera.rotate(angle, 1f, 1f, 0f);
                super.render(camera);
            }
        });
        return scene;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.gl_motion_camera_layout;
    }
}
