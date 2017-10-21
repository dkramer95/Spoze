package com.example.dkramer.spoze3;

import android.util.AttributeSet;

import com.example.dkramer.spoze3.gl.GLContext;
import com.example.dkramer.spoze3.gl.GLScene;
import com.example.dkramer.spoze3.gl.GLView;
import com.example.dkramer.spoze3.gl.GLWorld;

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
        GLScene scene = new GLScene(new GLWorld(getGLContext()) {
            @Override
            public void create() {
                //TODO add model creation here
            }
        });
        return scene;
    }
}