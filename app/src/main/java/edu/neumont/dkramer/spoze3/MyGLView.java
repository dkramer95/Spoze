package edu.neumont.dkramer.spoze3;

import android.util.AttributeSet;

import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLScene;
import edu.neumont.dkramer.spoze3.gl.GLView;

/**
 * Created by dkramer on 10/20/17.
 */

public class MyGLView extends GLView {

    public MyGLView(GLContext context) {
        super(context);
    }

    public MyGLView(GLContext context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public GLScene createScene() {
        GLScene scene = new GLScene(new MyGLWorld(getGLContext()));
        return scene;
    }
}
