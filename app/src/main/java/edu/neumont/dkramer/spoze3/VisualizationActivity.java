package edu.neumont.dkramer.spoze3;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import edu.neumont.dkramer.spoze3.converter.MotionCameraPreview;
import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLCameraActivity;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLModel;
import edu.neumont.dkramer.spoze3.gl.GLMotionCamera;
import edu.neumont.dkramer.spoze3.gl.GLScene;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.models.GLLiveTexturedRect;
import edu.neumont.dkramer.spoze3.models.GLSquare;
import edu.neumont.dkramer.spoze3.models.GLTexturedRect;
import edu.neumont.dkramer.spoze3.models.GLTrackPoints;

import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glReadPixels;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.ROTATION_VECTOR;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.TOUCH_INPUT;
import static edu.neumont.dkramer.spoze3.models.GLTrackPoints.fromCoords;

public class VisualizationActivity extends GLCameraActivity {
    protected Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBitmap = loadBitmap();
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
        return new MyScene(getGLContext());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.gl_camera_layout;
//        return R.layout.gl_motion_camera_layout;
    }

    private static final Random rng = new Random();


    private class MyScene extends GLScene {

        public MyScene(GLContext ctx) {
            super(ctx);
        }

        @Override
        public GLWorld createWorld() {
            final GLContext ctx = getGLContext();
        	return new GLWorld(ctx) {
        		float angle = 0f;
                @Override
                public void create() {
//                    GLTrackPoints.addPoint(fromCoords(0, 0, 0, 1, 0, 0, 1));

                    addModel(GLLiveTexturedRect.createFromBitmap(ctx, mBitmap, getWidth(), getHeight()));

//                    addModel(GLSquare.create(ctx));
//                    addModel(GLPoint.create(ctx, 1, 1, 0));

//                    for (int j = 0; j < 30; ++j) {
//                        float x = -1f + (rng.nextFloat() * 2f);
//                        float y = -1f + (rng.nextFloat() * 2f);
//                        float z = -1f + (rng.nextFloat() * 2f);
//                        float r = rng.nextFloat();
//                        float g = rng.nextFloat();
//                        float b = rng.nextFloat();
//                        float a = rng.nextFloat();
//                        GLTrackPoints.addPoint(fromCoords(x, y, z, r, g, b, a));
//                    }
//                    GLTrackPoints trackPoints = GLTrackPoints.getInstance(ctx);
//                    addModel(trackPoints);

//                    for (int j = 0; j < 20; ++j) {
//                    	float randX = rng.nextFloat();
//                        float randY = rng.nextFloat();
//
//                        addModel(GLPoint.create(ctx, randX, randY, 0));
//                    }

//                    addModel(new GLPoint(ctx));
                }
                @Override
                public void render(GLCamera camera) {
                    angle += 0.5f;
                    camera.rotate(angle, 1f, 0f, 1f);
                    super.render(camera);

//                    // color test
//                    ByteBuffer colorBuffer = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder());
//                    glReadPixels(getWidth() / 2, getHeight() / 2, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, colorBuffer);
//                    byte[] byteArray = new byte[4];
//                    colorBuffer.get(byteArray);
//
//                    int r = ((int)byteArray[0]) & 0xFF;
//                    int g = ((int)byteArray[1]) & 0xFF;
//                    int b = ((int)byteArray[2]) & 0xFF;
//                    Log.i("COLOR_RENDER", String.format("Red = %d, Green = %d, Blue = %d\n", r, g, b));

                    generateTest(mModels.get(0));
                }
                float counter = 0;

                protected void generateTest(GLModel model) {
                    if (counter > 2) {
                        if (MotionCameraPreview.sImage != null) {
                            // do stuff
                            counter = 0;
                            GLLiveTexturedRect rect = (GLLiveTexturedRect)model;
                            rect.update(MotionCameraPreview.sImage);
                        }
//                            removeModel(model);
//                            model = GLTexturedRect.createFromBitmap(ctx, MotionCameraPreview.sImage, getWidth(), getHeight());
//                            addModel(model);
                    }
                    ++counter;
                }
            };
        }

        public GLCamera createGLCamera() {
        	return GLMotionCamera.getDefault(getGLContext());
        }
    }
}
