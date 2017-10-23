package com.example.dkramer.spoze3.gl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by dkramer on 10/20/17.
 */

public abstract class GLActivity extends AppCompatActivity {
    /**
     * Constant specifying that no layout will be used for this activity
     * and that our content view will simply only contain our GLView.
     */
    protected static final int NULL_LAYOUT = -1234;

    protected GLContext mGLContext;




    public GLActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewDisplay();
        mGLContext.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGLContext.onStop();
    }

    /**
     * This method initializes the main content view to be used in this activity.
     */
    protected void initViewDisplay() {
        mGLContext = new GLContext(this);
        GLView glView = createGLView();
        mGLContext.setGLView(glView);
        glView.init(getGLContext());

        int layoutId = getLayoutId();

        if (layoutId == NULL_LAYOUT) {
            setContentView(getGLContext().getGLView());
        } else {
            setContentView(layoutId);
        }
    }

    /**
     * Method to be implemented that creates our GLView that will be
     * the primary GLView that draws the scene to the screen.
     * @return
     */
    protected abstract GLView createGLView();

    /**
     * Method that should be overwritten by subclasses if they wish to use a layout as defined
     * in R.layout.xxx, rather than simply using a single GLView that they create. The default
     * implementation uses only the GLView and returns the NULL_LAYOUT constant indicating so.
     * @return layout id
     */
    protected int getLayoutId() {
        return NULL_LAYOUT;
    }

    protected GLContext getGLContext() {
        return mGLContext;
    }
}
