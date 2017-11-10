package edu.neumont.dkramer.spoze3;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLScene;
import edu.neumont.dkramer.spoze3.gl.GLView;
import edu.neumont.dkramer.spoze3.gl.GLWorld;

/**
 * Created by dkramer on 11/9/17.
 */

public class SignView extends GLView implements View.OnTouchListener {

    public SignView(Context context) {
        super(context);
        init();
    }

    public SignView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    protected GLScene createGLScene() {
        return null;
    }

    class SignWorld extends GLWorld {

        public SignWorld(GLContext glContext) {
            super(glContext);
        }
    }
}
