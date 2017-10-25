package edu.neumont.dkramer.spoze3.gl;

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
        mGLContext = createGLContext();
        GLView glView = null;
        int layoutId = getLayoutId();

        // glView will be created from layout
        if (layoutId != NULL_LAYOUT) {
            // set layout so glView can inflate
            setContentView(layoutId);
            // subclasses should overwrite this method to return GLView from their layout
            glView = createGLView();
        } else {
            glView = createGLView();
            setContentView(glView);
        }
        // create the scene that subclasses define
        glView.setScene(createGLScene());
        glView.init(mGLContext);
    }

    /**
     * This method should be overwritten only if subclasses intend to do additional
     * setup for a GLContext, such as adding additional GLDeviceInfo tracking.
     * @return
     */
    protected GLContext createGLContext() {
        GLContext ctx = new GLContext(this);
        return ctx;
    }

    /**
     * Method to be implemented that creates a GLScene that will be actively
     * rendered.
     * @return
     */
    protected abstract GLScene createGLScene();

    /**
     * This method should be overwritten only if subclasses are using a GLView as
     * part of their layout XML file. This method should then return that GLView
     * through accessing it by findViewById()... This method is called after the
     * content view has been set, so it should guarantee a non-null GLView
     * @return GLView
     */
    protected GLView createGLView() {
        GLView glView = new GLView(getGLContext());
        return glView;
    }

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
