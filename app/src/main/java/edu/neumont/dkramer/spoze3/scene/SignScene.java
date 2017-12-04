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
    private static final int TOOLBAR_OBJECT = 0;
    private static final int TOOLBAR_NORMAL = 1;

    private static final String TAG = "SignScene";
    private static final int EVENT_QUEUE_LIMIT = 8;


    // queue of special events that should be executing, aside from normal rendering
    protected ArrayBlockingQueue<GLEvent> mEventQueue;
    protected GLPixelPicker mPicker;
    protected TouchHandler mTouchHandler;
    protected GLModel mSelectedModel;



    public SignScene(GLContext ctx) {
        super(ctx);
    }

    @Override
    public void init(int viewWidth, int viewHeight) {
        super.init(viewWidth, viewHeight);
        mPicker = new GLPixelPicker(viewWidth, viewHeight);
        mEventQueue = new ArrayBlockingQueue<>(EVENT_QUEUE_LIMIT);
        mTouchHandler = new TouchHandler();
    }

    @Override
    public GLWorld createWorld() {
//        Bitmap bmp1 = BitmapFactory.decodeResource(getGLContext().getResources(), R.drawable.vim_texture);
        return new GLWorld(getGLContext()) {
            @Override
            public void create() {
//                addModel(SignModel2.fromBitmap(getGLContext(), bmp1, getWidth(), getHeight()));
            }
        };
    }

    @Override
    public void onResume() {
        if (getWidth() != 0 && getHeight() != 0) {
            init(getWidth(), getHeight());
        }
    }

    public void deleteSelectedModel() {
        if (mSelectedModel != null) {
            getWorld().removeModel(mSelectedModel);
            mSelectedModel = null;
        }
    }

    protected SignModel2 checkModelSelection() {
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

    public void setUIToolbar(int type) {
        GLActivity activity = getGLContext().getActivity();
        activity.runOnUiThread(() -> {
            ViewFlipper flipper = activity.findViewById(R.id.toolbarFlipper);
            flipper.setDisplayedChild(type);
        });
    }

    protected void queueEvent(GLEvent e) {
        mEventQueue.add(e);
    }

    public GLCamera createGLCamera() {
        return GLMotionCamera.getDefault(getGLContext());
    }


    public GLModel getSelectedModel() {
        return mSelectedModel;
    }


    /* Touch Handler for our scene */

    class TouchHandler implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener, GestureDetectorAdapter {
        private static final String TAG = "TouchHandler";

        // number of ACTION_MOVE events that need to be called before we
        // actually allow movement to occur
        private static final int ACTION_MOVE_THRESHOLD = 3;

        // help prevent jittery, unintended movements
        private boolean mReleaseFlag;
        private int mMoveCount;
        private boolean mScalingFlag;

        private ScaleGestureDetector mScaleGestureDetector;
        private GestureDetector mGestureDetector;



        public TouchHandler() {
            GLTouchInfo touchInfo = (GLTouchInfo)getGLContext().getDeviceInfo(TOUCH_INPUT);

            // clear out any previous
            touchInfo.removeOnTouchListener(this);

            touchInfo.addOnTouchListener(this);
            final TouchHandler touchHandler = this;

            getGLContext().runOnUiThread(() -> {
                mScaleGestureDetector = new ScaleGestureDetector(getGLContext().getActivity(), touchHandler);
                mGestureDetector = new GestureDetector(getGLContext().getActivity(), touchHandler);
            });
        }

        @Override
        public boolean onTouch(View view, MotionEvent e) {
            final int action = e.getAction();

            switch (action) {
                case ACTION_DOWN: onTouchDown(); break;
                case ACTION_MOVE: onTouchMove(); break;
                case ACTION_UP: onTouchRelease(); break;
            }
            mGestureDetector.onTouchEvent(e);
            mScaleGestureDetector.onTouchEvent(e);
            return false;
        }

        protected boolean canMove() {
            return (!mReleaseFlag && !mScalingFlag && mMoveCount > ACTION_MOVE_THRESHOLD);
        }

        protected void onTouchDown() {
            queueEvent(() -> {
                SignModel2 model = checkModelSelection();

                if (mSelectedModel != null) {
                    // deselect
                    if (model == mSelectedModel || model == null) {
                        mSelectedModel = null;
                        BlendFuncHelper.nextBlendFunc();
                        setUIToolbar(TOOLBAR_NORMAL);
                    } else {
                        mSelectedModel = model;
                        setUIToolbar(TOOLBAR_OBJECT);
                    }
                } else {
                    if (model != null) {
                        mSelectedModel = model;
                        setUIToolbar(TOOLBAR_OBJECT);
                    }
                }
            });
            mMoveCount = 0;
        }

        protected void onTouchMove() {
            if (mSelectedModel != null && canMove()) {
                // movement needs to take into account where our eye is looking
                Point3f eye = getCamera().getEye();
                mSelectedModel.handleTouchMove(getf(CURRENT_TOUCH_NORMALIZED_X),
                        getf(CURRENT_TOUCH_NORMALIZED_Y), 0, eye.x, eye.y, eye.z);
            }
            ++mMoveCount;
            mReleaseFlag = false;
        }

        protected void onTouchRelease() {
            mReleaseFlag = true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // pinch to zoom is backwards, so we invert it here
            float scaleFactor = (1.0f - detector.getScaleFactor()) * -1.25f;
            if (mSelectedModel != null) {
                mSelectedModel.scale(scaleFactor);
            }
            Log.i(TAG, "OnScale --> Factor => " + scaleFactor);
            return mScalingFlag = true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return mScalingFlag = true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mScalingFlag = false;
        }

        /* TODO i think this should be moved elsewhere, if time permits */
        @Override
        public boolean onFling(MotionEvent e, MotionEvent e1, float v, float v1) {
            float deltaX = Math.abs(e1.getX() - e.getX());

            Log.i("OnFling", "DeltaX => " + deltaX);
            Log.i("OnFling", String.format("X1: %f, Y1: %f, X2: %f, Y2: %f\n", e.getX(), e.getY(), e1.getX(), e1.getY()));

            float startY = e.getY();

            if (startY <= getHeight() && startY >= (getHeight() - (getHeight() * .15f)) && deltaX < 75) {
                float endY = e1.getY();
                float velocity = v1 - v;
                Log.i("OnFling", String.format("StartY: %f, EndY: %f, Vel: %f, Height: %d\n", startY, endY, velocity, getHeight()));

                if (Math.abs(velocity) > 3000) {
//                    Log.i("OnFling", String.format("StartY: %f, EndY: %f, Vel: %f\n", startY, endY, velocity));
                    Log.i("OnFling", "Swipe up detected");

//                    GLActivity activity = getGLContext().getActivity();
                    VisualizationActivity activity = (VisualizationActivity)getGLContext().getActivity();
                    activity.runOnUiThread(() -> {
//                        Toast.makeText(activity, "Swipe up action", Toast.LENGTH_SHORT).show();
//                        activity.showModelFragment();
//                        activity.TEST_LOAD_DATA();
                        activity.showModelFragment();
//                        activity.foo();
//                        activity.getFragmentManager().beginTransaction();
//                        ViewFlipper flipper = activity.findViewById(R.id.toolbarFlipper);
//                        flipper.setDisplayedChild(type);
                    });
                    return true;
                }
            }
            return false;
        }
    }
}
