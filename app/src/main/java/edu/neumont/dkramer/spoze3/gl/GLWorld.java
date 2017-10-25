package edu.neumont.dkramer.spoze3.gl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dkramer on 10/20/17.
 */

public abstract class GLWorld extends GLObject {
    protected int mWidth;
    protected int mHeight;
    protected List<GLModel> mModels;



    public GLWorld(GLContext glContext) {
        super(glContext);
        mModels = new ArrayList<>();
    }

    /**
     * Method to be implemented that creates all necessary models
     * for this world
     */
    public abstract void create();

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void addModel(GLModel model) {
        mModels.add(model);
    }

    public void removeModel(GLModel model) {
        mModels.remove(model);
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

}
