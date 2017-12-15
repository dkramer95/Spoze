package edu.neumont.dkramer.spoze3.scene;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ViewFlipper;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import edu.neumont.dkramer.spoze3.BlendFuncHelper;
import edu.neumont.dkramer.spoze3.GLPixelPicker;
import edu.neumont.dkramer.spoze3.R;
import edu.neumont.dkramer.spoze3.VisualizationActivity;
import edu.neumont.dkramer.spoze3.geometry.Point3f;
import edu.neumont.dkramer.spoze3.gesture.GestureDetectorAdapter;
import edu.neumont.dkramer.spoze3.gl.GLActivity;
import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLEvent;
import edu.neumont.dkramer.spoze3.gl.GLModel;
import edu.neumont.dkramer.spoze3.gl.GLMotionCamera;
import edu.neumont.dkramer.spoze3.gl.GLScene;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLTouchInfo;
import edu.neumont.dkramer.spoze3.models.GLPickerModel;
import edu.neumont.dkramer.spoze3.models.SignModel2;
import edu.neumont.dkramer.spoze3.toolbar.IToolbar;
import edu.neumont.dkramer.spoze3.toolbar.ToolbarManager;
import edu.neumont.dkramer.spoze3.toolbar.VisualizeToolbar;

import static android.opengl.GLES10.GL_ZERO;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_SRC_COLOR;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.TOUCH_INPUT;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_NORMALIZED_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_NORMALIZED_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.getf;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.geti;

/**
 * Created by dkramer on 11/10/17.
 */

public class SignScene extends GLScene {
    private static final String TAG = "SignScene";
    private static final int EVENT_QUEUE_LIMIT = 8;


    // queue of special events that should be executing, aside from normal rendering
    protected ArrayBlockingQueue<GLEvent> mEventQueue;
    protected GLPixelPicker mPicker;
    protected GLModel mSelectedModel;



    public SignScene(GLContext ctx) {
        super(ctx);
    }

    @Override
    public void init(int viewWidth, int viewHeight) {
        super.init(viewWidth, viewHeight);
        mPicker = new GLPixelPicker(viewWidth, viewHeight);
        mEventQueue = new ArrayBlockingQueue<>(EVENT_QUEUE_LIMIT);
    }

    public void deleteSelectedModel() {
        if (mSelectedModel != null) {
            getWorld().removeModel(mSelectedModel);
            mSelectedModel = null;
        }
    }

    public SignModel2 checkModelSelection() {
        SignModel2 selectedModel = null;

        int pixel = readTouchPixel();
        Iterator<GLModel> models = getWorld().getModelIterator();

        while (models.hasNext()) {
            SignModel2 model = (SignModel2)models.next();
            if (model.didTouch(pixel)) {
                Log.i(TAG, "Model " + model.getId() + " touched!");
                selectedModel = model;
                getWorld().sendModelToFront(selectedModel);
                break;
            }
        }
        Log.i(TAG, "Pixel Read => " + Integer.toHexString(pixel));
        return selectedModel;
    }


    protected void drawModelSelectors() {
        Iterator<GLModel> models = getWorld().getModelIterator();
        while (models.hasNext()) {
            GLModel model = models.next();
            model.drawSelector(getCamera());
        }
    }

    protected int readTouchPixel() {
        mPicker.enable();
        drawModelSelectors();
        int pixel = mPicker.readPixel(geti(CURRENT_TOUCH_X), geti(CURRENT_TOUCH_Y));
        mPicker.disable();
        return pixel;
    }

    @Override
    public void render() {
        clearScreen();
        updateCamera();
        runEventsInQueue();
        renderWorld();

        if (mSelectedModel != null) {
            drawSelection();
        }
    }

    public void setSelectedModel(GLModel model) {
        mSelectedModel = model;
    }

    protected void drawSelection() {
        glEnable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glBlendFunc(GL_ZERO, GL_SRC_COLOR);

        // uncomment for testing different blend mode combinations
//        int[] blendFunc = BlendFuncHelper.nextBlendFunc();
//		glBlendFunc(blendFunc[0], blendFunc[1]);

        // ensure that our selector uses the brightest color (not the random unique) color
        // so that selection is more apparent
        GLPickerModel pickerModel = (GLPickerModel)mSelectedModel.getSelector();
        pickerModel.enableSelected();
        pickerModel.render(getCamera());
        pickerModel.reset();

        glDisable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
    }

    protected void runEventsInQueue() {
        GLEvent e;
        while ((e = mEventQueue.poll()) != null) {
            e.run();
        }
    }

    public void queueEvent(GLEvent e) {
        mEventQueue.add(e);
    }

    public GLCamera createGLCamera() {
        return GLMotionCamera.getDefault(getGLContext());
    }

    public GLModel getSelectedModel() {
        return mSelectedModel;
    }

}
