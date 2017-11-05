package edu.neumont.dkramer.spoze3.gl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dkramer on 10/20/17.
 */

public abstract class GLWorld extends GLObject {
    protected int mWidth;
    protected int mHeight;
    protected List<GLModel> mModels;
    protected GLCamera mCamera;



    public GLWorld(GLContext glContext) {
        super(glContext);
        mModels = new ArrayList<>();
    }

    /**
     * Method to be implemented that creates all necessary models
     * for this world
     */
    public void create() {
        mCamera = getGLContext().getGLView().getScene().getCamera();
    }

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void addModel(GLModel model) {
        mModels.add(model);
    }

    public void removeModel(GLModel model) {
        mModels.remove(model);
        model.delete();
    }

    /**
     * Sends the specified model to the front, by ensuring that it is
     * rendered last, appearing over any other models.
     * @param model Model to send to front
     */
    protected void sendModelToFront(GLModel model) {
        int frontIndex = (mModels.size() - 1);
        GLModel frontModel = mModels.get(frontIndex);

        if (model != frontModel) {
            int modelIndex = mModels.indexOf(model);

            // swap model position rendering order
            mModels.set(modelIndex, frontModel);
            mModels.set(frontIndex, model);
        }
    }

    public Iterator<GLModel> getModelIterator() {
        return mModels.iterator();
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void render(GLCamera camera) {
        for (GLModel m : mModels) {
            m.render(camera);
        }
    }

    public void removeAllModels() {
        Iterator<GLModel> iter = getModelIterator();
        while (iter.hasNext()) {
            GLModel model = iter.next();
            iter.remove();
            model.delete();
        }
    }

    public GLCamera getCamera() {
        return mCamera;
    }
}
