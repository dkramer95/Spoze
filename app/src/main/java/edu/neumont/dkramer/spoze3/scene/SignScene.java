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
import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLEvent;
import edu.neumont.dkramer.spoze3.gl.GLModel;
import edu.neumont.dkramer.spoze3.gl.GLMotionCamera;
import edu.neumont.dkramer.spoze3.gl.GLScene;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLTouchInfo;
import edu.neumont.dkramer.spoze3.models.SignModel2;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.TOUCH_INPUT;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_X;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Value.CURRENT_TOUCH_Y;
import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.geti;

/**
 * Created by dkramer on 11/10/17.
 */

public class SignScene extends GLScene implements View.OnTouchListener {
    private static final String TAG = "SignScene";
    private static final int EVENT_QUEUE_LIMIT = 8;

    // queue of special events that should be executing, aside from normal rendering
    protected ArrayBlockingQueue<GLEvent> mEventQueue;
    protected GLPixelPicker mPicker;


    public SignScene(GLContext ctx) {
        super(ctx);
    }

    @Override
    public void init(int viewWidth, int viewHeight) {
        super.init(viewWidth, viewHeight);
        mPicker = new GLPixelPicker(viewWidth, viewHeight);
        mEventQueue = new ArrayBlockingQueue<>(EVENT_QUEUE_LIMIT);
        GLTouchInfo touchInfo = (GLTouchInfo)getGLContext().getDeviceInfo(TOUCH_INPUT);
        touchInfo.addOnTouchListener(this);
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

    protected void checkModelSelection() {
        int pixel = readTouchPixel();
        Log.i(TAG, "Pixel Read => " + Integer.toHexString(pixel));
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


    @Override
    public boolean onTouch(View v, MotionEvent e) {
        Log.i(TAG, "SignScene touched");
        queueEvent(() -> {
            checkModelSelection();
        });
        return false;
    }
}
