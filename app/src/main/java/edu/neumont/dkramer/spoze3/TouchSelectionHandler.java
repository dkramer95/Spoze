package edu.neumont.dkramer.spoze3;

import android.view.MotionEvent;
import android.view.View;

import java.util.Iterator;

import edu.neumont.dkramer.spoze3.gl.GLModel;
import edu.neumont.dkramer.spoze3.gl.GLScene;
import edu.neumont.dkramer.spoze3.gl.GLView;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLTouchInfo;
import edu.neumont.dkramer.spoze3.models.SignModel;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.TOUCH_INPUT;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.geti;

/**
 * Created by dkramer on 11/4/17.
 */

public class TouchSelectionHandler implements View.OnTouchListener, GLPixelPicker.OnPixelReadListener {
    // number of ACTION_MOVE events that need to be called before we
    // actually allow movement to occur
    private static final int ACTION_MOVE_THRESHOLD = 3;



    protected GLView mView;
    protected GLWorld mWorld;
    protected SignModel mSelectedModel;
    protected GLPixelPicker mPixelPicker;
    protected OnModelSelectionListener mModelSelectionListener;

    // helps prevent jittery, unintended movements
    protected boolean mReleaseFlag;
    protected int mMoveCount;



    public TouchSelectionHandler(GLView view) {
        mView = view;
        mView.setOnTouchListener(this);

        GLTouchInfo info = (GLTouchInfo)view.getGLContext().getDeviceInfo(TOUCH_INPUT);
        info.addOnTouchListener(this);

        // default.. does nothing
        mModelSelectionListener = new OnModelSelectionListener() { };
    }


    public TouchSelectionHandler(GLWorld world) {
        mWorld = world;
        mView = world.getGLContext().getGLView();
        mPixelPicker = new GLPixelPicker();
        mPixelPicker.setOnPixelReadListener(this);

        GLTouchInfo info = (GLTouchInfo)world.getGLContext().getDeviceInfo(TOUCH_INPUT);
        info.addOnTouchListener(this);

        // default.. does nothing
        mModelSelectionListener = new OnModelSelectionListener() { };
    }

    public void setOnModelSelectionListener(OnModelSelectionListener listener) {
        mModelSelectionListener = listener;
    }

    protected void checkModelSelection() {
        GLScene scene = mView.getScene();

        scene.addGLEvent(() -> {
            scene.readPixel(geti(CURRENT_TOUCH_X), geti(CURRENT_TOUCH_Y));
        });

//        mWorld.getGLContext().queueEvent(() -> {
//            mPixelPicker.readPixel(geti(CURRENT_TOUCH_X), geti(CURRENT_TOUCH_Y),
//                    mWorld.getWidth(), mWorld.getHeight());
//        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        final int action = e.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                checkModelSelection();
                mMoveCount = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mSelectedModel != null && canMove()) {
                    mModelSelectionListener.onContinuousSelect(mSelectedModel);
                }
                ++mMoveCount;
                mReleaseFlag = false;
                break;
            case MotionEvent.ACTION_UP:
                mReleaseFlag = true;
                break;
        }

        return false;
    }

    protected boolean canMove() {
        return (!mReleaseFlag && mMoveCount > ACTION_MOVE_THRESHOLD);
    }

    @Override
    public void onPixelRead(int pixel) {
        if (mSelectedModel != null) {
            // check to see if we still tapped on our selected model
            if (!mSelectedModel.didTouch(pixel)) {
                mModelSelectionListener.onDeselect(mSelectedModel);
                mSelectedModel = null;
            }
        }
        Iterator<GLModel> iter = mWorld.getModelIterator();

        while (iter.hasNext() && mSelectedModel == null) {
            SignModel model = (SignModel)iter.next();
            if (model.didTouch(pixel)) {
                mSelectedModel = model;
                mModelSelectionListener.onFirstSelect(mSelectedModel);
            }
        }
    }

    /*
     * Callback interface for handling what we should do when a model is selected / deselected
     */
    interface OnModelSelectionListener {
        /**
         * Represents state when a model is first initially tapped
         * @param model
         */
        default void onFirstSelect(GLModel model) { }

        /**
         * Represents state when user has already tapped on model and is moving their
         * finger around the screen
         * @param model
         */
        default void onContinuousSelect(GLModel model) { }

        /**
         * Represents state when user has tapped outside model
         * @param model
         */
        default void onDeselect(GLModel model) { }
    }
}
