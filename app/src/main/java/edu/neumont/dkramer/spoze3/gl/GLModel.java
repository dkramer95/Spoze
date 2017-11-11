package edu.neumont.dkramer.spoze3.gl;

import android.opengl.Matrix;
import android.util.Log;

import java.util.Random;

/**
 * Created by dkramer on 10/20/17.
 */

public abstract class GLModel extends GLObject {
    private static final String TAG = "GLModel";

    // counter that will be used to assign ID's to all created models
	private static int sInstanceCounter;

    // shader program that we're using to render our GLModel
    protected GLProgram mGLProgram;

    // open gl buffer array
    protected final GLVertexArray mVertexArray;

    // world that this model exists in
    protected GLWorld mWorld;

    // move this model into world space
    protected final float[] mModelMatrix = new float[16];

    // where this model exists and will be projected in the viewport
    protected final float[] mMVPMatrix = new float[16];

    // backing array that stores vertices
    protected final float[] mVertexData;

    // the id of our model
    protected final int mId;

    protected float mTransX;
    protected float mTransY;
    protected float mTransZ;



    public GLModel(GLContext glContext, float[] vertexData) {
        super(glContext);
        mVertexData = vertexData;
        mVertexArray = new GLVertexArray(vertexData);
        mGLProgram = createGLProgram();
        mWorld = glContext.getGLView().getScene().getWorld();
        mId = ++sInstanceCounter;
    }

    /*
     * Method to be implemented that requires a GLProgram be created for our GLModel
     * to provide the necessary shaders to draw to the screen
     */
    protected abstract GLProgram createGLProgram();

    /*
     * Method to be implemented that handles drawing vertices to screen.
     */
    protected abstract void draw(GLCamera camera);

    /*
     * Method to be implemented that creates all the necessary handles to various
     * attributes / uniforms of our GLProgram.
     */
    protected abstract void bindHandles();

    /*
     * Method to be implemented that enables the correct attributes used
     * at render time.
     */
    protected abstract void enableAttributes();


    protected void applyTransformations() {
        //TODO implement matrix operations in correct order to transform our GLModel
        Matrix.setIdentityM(mModelMatrix, 0);
    }

    public void translate(float x, float y, float z) {
        mTransX = x;
        mTransY = y;
        mTransZ = z;
    }

    /*
     * Performs a typical render job. Subclasses need to implement specific methods
     * but this is the general flow of how a rendering should go.
     */
    public void render(GLCamera camera) {
        getGLProgram().use();
        applyTransformations();
        camera.applyToModel(this);
        bindHandles();
        enableAttributes();
        draw(camera);
    }

    /*
     * Method to be implemented that handles drawing our pixel identifier.. Default
     * implementation just calls our regular draw method.
     */
    public void drawSelector(GLCamera camera) {
        draw(camera);
        Log.w(TAG, "Warning... Using default draw selector");
    }

    /*
     * Method that can be overwritten to perform any cleanup actions
     * necessary when it has been removed from a world.
     */
    public void delete() {

    }

    /*
     * Method that can be overwritten to active touch move event
     */
    public void handleTouchMove(float touchX, float touchY, float touchZ, float eyeX, float eyeY, float eyeZ) {

    }


    public float[] getModelMatrix() {
        return mModelMatrix;
    }

    public float[] getMVPMatrix() {
        return mMVPMatrix;
    }

    public GLVertexArray getVertexArray() {
        return mVertexArray;
    }

    public float[] getVertexData() {
        return mVertexData;
    }

    public GLWorld getWorld() {
        return mWorld;
    }

    public GLProgram getGLProgram() {
        return mGLProgram;
    }

    public int getId() {
        return mId;
    }

    public int getStride() {
        throw new UnsupportedOperationException(getClass() + "should implement me to return correct value!");
    }
}
