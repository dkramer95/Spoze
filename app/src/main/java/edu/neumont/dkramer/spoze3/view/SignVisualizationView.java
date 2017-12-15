package edu.neumont.dkramer.spoze3.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import edu.neumont.dkramer.spoze3.BlendFuncHelper;
import edu.neumont.dkramer.spoze3.R;
import edu.neumont.dkramer.spoze3.VisualizationActivity;
import edu.neumont.dkramer.spoze3.geometry.Point3f;
import edu.neumont.dkramer.spoze3.gesture.GestureDetectorAdapter;
import edu.neumont.dkramer.spoze3.gl.GLActivity;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLModel;
import edu.neumont.dkramer.spoze3.gl.GLView;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLTouchInfo;
import edu.neumont.dkramer.spoze3.models.SignModel2;
import edu.neumont.dkramer.spoze3.scene.SignScene;
import edu.neumont.dkramer.spoze3.toolbar.IToolbar;
import edu.neumont.dkramer.spoze3.toolbar.ToolbarManager;
import edu.neumont.dkramer.spoze3.toolbar.VisualizeToolbar;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.TOUCH_INPUT;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_NORMALIZED_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_NORMALIZED_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.getf;

/**
 * Created by dkramer on 12/14/17.
 */

public class SignVisualizationView extends GLView {
    protected TouchHandler mTouchHandler;

    public SignVisualizationView(Context context) {
        super(context);
    }

    public SignVisualizationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(GLContext glContext) {
        super.init(glContext);
        mTouchHandler = new TouchHandler();
    }

    public void setUIToolbar(IToolbar toolbar) {
        GLActivity activity = getGLContext().getActivity();
        activity.runOnUiThread(() -> {
            ToolbarManager toolbarManager = activity.findViewById(R.id.toolbarManager);
            toolbarManager.setToolbar(toolbar);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTouchHandler != null) {
            mTouchHandler.refresh();
        }
    }

    public SignScene getScene() {
        return (SignScene)mScene;
    }




    class TouchHandler implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener, GestureDetectorAdapter {
        private static final String TAG = "TouchHandler";

        // number of ACTION_MOVE events that need to be called before we
        // actually allow movement to occur
        private static final int ACTION_MOVE_THRESHOLD = 3;

        // speed threshold for accepting swipe gesture
        private static final int SWIPE_VELOCITY_THRESHOLD = 3000;

        // help prevent jittery, unintended movements
        private boolean mReleaseFlag;
        private int mMoveCount;
        private boolean mScalingFlag;

        private ScaleGestureDetector mScaleGestureDetector;
        private GestureDetector mGestureDetector;


        public TouchHandler() {
            final TouchHandler touchHandler = this;

            mScaleGestureDetector = new ScaleGestureDetector(getContext(), touchHandler);
            mGestureDetector = new GestureDetector(getContext(), touchHandler);
            refresh();
        }

        public void refresh() {
            GLTouchInfo touchInfo = (GLTouchInfo)getGLContext().getDeviceInfo(TOUCH_INPUT);
            touchInfo.removeOnTouchListener(this);
            touchInfo.addOnTouchListener(this);
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
            getScene().queueEvent(() -> {
                SignModel2 model = getScene().checkModelSelection();
                GLModel selectedModel = getScene().getSelectedModel();


                if (selectedModel != null) {
                    // deselect
                    if (model == selectedModel || model == null) {
                        selectedModel = null;
                        BlendFuncHelper.nextBlendFunc();
                        setUIToolbar(VisualizeToolbar.NORMAL);
                    } else {
                        selectedModel = model;
                        setUIToolbar(VisualizeToolbar.OBJECT);
                    }
                } else {
                    if (model != null) {
                        selectedModel = model;
                        setUIToolbar(VisualizeToolbar.OBJECT);
                    }
                }
                getScene().setSelectedModel(selectedModel);
            });
            mMoveCount = 0;
        }

        protected void onTouchMove() {
            GLModel selectedModel = getScene().getSelectedModel();

            if (selectedModel != null && canMove()) {
                // movement needs to take into account where our eye is looking
                Point3f eye = getScene().getCamera().getEye();
                selectedModel.handleTouchMove(getf(CURRENT_TOUCH_NORMALIZED_X),
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
            GLModel selectedModel = getScene().getSelectedModel();

            if (selectedModel != null) {
                selectedModel.scale(scaleFactor);
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

                if (Math.abs(velocity) > SWIPE_VELOCITY_THRESHOLD) {
                    Log.i("OnFling", "Swipe up detected");

                    VisualizationActivity activity = (VisualizationActivity)getGLContext().getActivity();
                    activity.runOnUiThread(() -> {
                        activity.showModelFragment();
                    });
                    return true;
                }
            }
            return false;
        }
    }
}
