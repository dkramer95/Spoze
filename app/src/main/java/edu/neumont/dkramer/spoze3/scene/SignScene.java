package edu.neumont.dkramer.spoze3.scene;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import edu.neumont.dkramer.spoze3.R;
import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLMotionCamera;
import edu.neumont.dkramer.spoze3.gl.GLScene;
import edu.neumont.dkramer.spoze3.gl.GLWorld;
import edu.neumont.dkramer.spoze3.gl.deviceinfo.GLTouchInfo;
import edu.neumont.dkramer.spoze3.models.SignModel2;

import static edu.neumont.dkramer.spoze3.gl.deviceinfo.GLDeviceInfo.Type.TOUCH_INPUT;

/**
 * Created by dkramer on 11/10/17.
 */

public class SignScene extends GLScene implements View.OnTouchListener {
    private static final String TAG = "SignScene";



    public SignScene(GLContext ctx) {
        super(ctx);
        init();
    }

    protected void init() {
        GLTouchInfo touchInfo = (GLTouchInfo)getGLContext().getDeviceInfo(TOUCH_INPUT);
        touchInfo.addOnTouchListener(this);
    }

    @Override
    public GLWorld createWorld() {
        return new GLWorld(getGLContext()) {
            @Override
            public void create() {
                addModel(SignModel2.createFromResource(getGLContext(), R.drawable.banner_texture, getWidth(), getHeight()));
            }
        };
    }

    public GLCamera createGLCamera() {
        return GLMotionCamera.getDefault(getGLContext());
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        Log.i(TAG, "SignScene touched");
        return false;
    }
}
