package edu.neumont.dkramer.spoze3.gl;

import android.opengl.Matrix;

import edu.neumont.dkramer.spoze3.geometry.Point3f;
import edu.neumont.dkramer.spoze3.geometry.Ray;
import edu.neumont.dkramer.spoze3.geometry.Vector3f;
import edu.neumont.dkramer.spoze3.util.FPSCounter;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DITHER;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

/**
 * Created by dkramer on 10/20/17.
 */

public abstract class GLScene extends GLObject {
    private static final String TAG = "GLScene";

    protected final float[] mInvertedViewProjectionMatrix = new float[16];

    protected FPSCounter mFPSCounter;
    protected GLCamera mGLCamera;
    protected GLWorld mWorld;
    protected int mWidth;
    protected int mHeight;



    public GLScene(GLContext ctx) {
    	super(ctx);
        mGLCamera = createGLCamera();
        mWorld = createWorld();
        mFPSCounter = new FPSCounter();
    }

    public void init(int viewWidth, int viewHeight) {
        getWorld().setSize(viewWidth, viewHeight);
        getWorld().create();
    }

    public abstract GLWorld createWorld();


    public void render() {
        clearScreen();
        updateCamera();
        renderWorld();
        logFrame();
        Matrix.invertM(mInvertedViewProjectionMatrix, 0, mGLCamera.getProjectionMatrix(), 0);
    }

    protected void updateCamera() {
        mGLCamera.update();
    }

    protected void renderWorld() {
        mWorld.render(mGLCamera);
    }

    protected void logFrame() {
        mFPSCounter.logFrame();
    }

    public void refreshSize(int width, int height) {
        getWorld().setSize(width, height);
        getCamera().refreshSize(width, height);
//        mPixelPicker = new GLPixelPicker(width, height);

        // TODO probably shouldn't be here
//        mTouchHandler = new TouchSelectionHandler(getGLContext().getGLView(), mPixelPicker);
    }

    protected void clearScreen() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DITHER);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0f, 0f, 0f, 0f);
        glDisable(GL_BLEND);
    }

//    public void readPixel(int x, int y) {
//        mPixelPicker.enable();
//        Iterator<GLModel> models = getWorld().getModelIterator();
//        while (models.hasNext()) {
//            GLModel model = models.next();
//            model.drawSelector(getCamera());
//        }
//        int pixel = mPixelPicker.readPixel(x, y);
//        Log.i(TAG, "Pixel value readout => " + Integer.toHexString(pixel));
//        mPixelPicker.disable();
//    }

    public GLWorld getWorld() {
        return mWorld;
    }

    protected GLCamera createGLCamera() {
        return GLCamera.getDefault(getGLContext());
    }

    public GLCamera getCamera() {
        return mGLCamera;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }


//    public void addGLEvent(GLEvent e) {
//        mEvents.add(e);
//    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

        // ray exists in 3D world space... we need to check to see if the box containing
        // our model intersects with this ray
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {
        Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
        // TODO check against objects in world to see if ray collides
    }

    protected Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        // Convert normalized device coordinates into 3D world space coordinates
        final float[] nearPointNdc = { normalizedX, normalizedY, -1, 1 };
        final float[] farPointNdc = { normalizedX, normalizedY, 1, 1 };
        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        Matrix.multiplyMV(nearPointWorld, 0, mInvertedViewProjectionMatrix, 0, nearPointNdc, 0);
        Matrix.multiplyMV(farPointWorld, 0, mInvertedViewProjectionMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Point3f nearPointRay =
                new Point3f(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);

        Point3f farPointRay =
                new Point3f(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Ray(nearPointRay, Vector3f.between(nearPointRay, farPointRay));
    }

    protected void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    public void stop() {
        getWorld().removeAllModels();
    }

    public static class Builder {
    	private GLWorld mWorld;
    	private GLCamera mCamera;
    	private GLContext mGLContext;

        public Builder(GLContext ctx) {
            mGLContext = ctx;
        }

        public Builder setWorld(GLWorld world) {
        	mWorld = world;
        	return this;
        }

        public Builder setCamera(GLCamera camera) {
            mCamera = camera;
            return this;
        }

        public GLScene build() {
            final GLWorld world = mWorld;
            final GLCamera camera = mCamera;

            return new GLScene(mGLContext) {
                @Override
                public GLWorld createWorld() {
                	return world;
                }

                @Override
                public GLCamera createGLCamera() {
                	return camera != null ? camera : super.createGLCamera();
                }
            };
        }
    }
}
