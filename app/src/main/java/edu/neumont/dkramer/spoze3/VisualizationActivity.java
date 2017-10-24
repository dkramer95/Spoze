package edu.neumont.dkramer.spoze3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.IOException;

import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLCameraActivity;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLScene;
import edu.neumont.dkramer.spoze3.gl.GLView;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.models.GLTexturedRect;

public class VisualizationActivity extends GLCameraActivity {
    protected Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        boolean errorOccurred = false;
        Bitmap bmp = null;

        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), intent.getData());
        } catch (IOException e) {
            errorOccurred = true;
            e.printStackTrace();
        }

        //TODO handle error better
        if (errorOccurred || bmp == null) {
            Toast.makeText(this, "Failed to open image", Toast.LENGTH_LONG).show();
            finish();
        } else {
            // create our view!!
            mBitmap = bmp;
            createGLView();
        }
    }

    @Override
    protected GLView createGLView() {
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
        //TODO fix view scene initialization... had to move these out of the ctor of the GLView
        mTestView.setScene(scene);
		mTestView.init(getGLContext());
		mGLContext.onStart();
	    return null;
    }
}
