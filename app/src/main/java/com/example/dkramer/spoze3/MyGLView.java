package com.example.dkramer.spoze3;

import android.content.Context;
import android.util.AttributeSet;

import com.example.dkramer.spoze3.gl.GLScene;
import com.example.dkramer.spoze3.gl.GLView;
import com.example.dkramer.spoze3.gl.GLWorld;

/**
 * Created by dkramer on 10/20/17.
 */

public class MyGLView extends GLView {

    public MyGLView(Context context) {
        super(context);
    }

    public MyGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public GLScene createScene() {
        GLScene scene = new GLScene(new GLWorld() {
            @Override
            public void create() {
                //TODO add model creation here
            }
        });
        return scene;
    }
}
