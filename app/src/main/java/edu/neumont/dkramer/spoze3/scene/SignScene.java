package edu.neumont.dkramer.spoze3.scene;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import edu.neumont.dkramer.spoze3.GLPixelPicker;
import edu.neumont.dkramer.spoze3.R;
import edu.neumont.dkramer.spoze3.geometry.Point3f;
import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLEvent;
import edu.neumont.dkramer.spoze3.gl.GLModel;
import edu.neumont.dkramer.spoze3.gl.GLMotionCamera;
import edu.neumont.dkramer.spoze3.gl.GLScene;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLTouchInfo;
import edu.neumont.dkramer.spoze3.models.SignModel2;

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
        Bitmap bmp = BitmapFactory.decodeResource(getGLContext().getResources(), R.drawable.banner_texture);
        return new GLWorld(getGLContext()) {
            @Override
            public void create() {
                addModel(SignModel2.fromBitmap(getGLContext(), bmp, getWidth(), getHeight()));
            }
        };
    }

    protected SignModel2 checkModelSelection() {
        SignModel2 model = null;

        int pixel = readTouchPixel();
        Iterator<GLModel> models = getWorld().getModelIterator();

        while (models.hasNext()) {
            model = (SignModel2)models.next();
            if (model.didTouch(pixel)) {
                Log.i(TAG, "Model touched!");
                break;
            }
        }
        Log.i(TAG, "Pixel Read => " + Integer.toHexString(pixel));
        return model;
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
    }

    protected void runEventsInQueue() {
        GLEvent e;
        while ((e = mEventQueue.poll()) != null) {
            e.run();
        }
    }

    protected void queueEvent(GLEvent e) {
        mEventQueue.add(e);
    }

    public GLCamera createGLCamera() {
        return GLMotionCamera.getDefault(getGLContext());
    }




    /* Touch Handler for our scene */

    class TouchHandler implements View.OnTouchListener {
        // number of ACTION_MOVE events that need to be called before we
        // actually allow movement to occur
        private static final int ACTION_MOVE_THRESHOLD = 3;

        // help prevent jittery, unintended movements
        protected boolean mReleaseFlag;
        protected int mMoveCount;




        public TouchHandler() {
            GLTouchInfo touchInfo = (GLTouchInfo)getGLContext().getDeviceInfo(TOUCH_INPUT);
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
            return false;
        }

        protected boolean canMove() {
            return (!mReleaseFlag && mMoveCount > ACTION_MOVE_THRESHOLD);
        }

        protected void onTouchDown() {
            queueEvent(() -> {
                SignModel2 model = checkModelSelection();
                if (model != null) {
                    mSelectedModel = model;
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
    }
}
