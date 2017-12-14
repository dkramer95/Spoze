package edu.neumont.dkramer.spoze3.gl;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import edu.neumont.dkramer.spoze3.util.Preferences;

import static android.view.View.GONE;

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
    protected void onResume() {
        super.onResume();
        mGLContext.onResume();
        resumeGLRender();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLContext.onStop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGLContext.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGLContext.onDestroy();
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
        return new GLContext(this);
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
        return new GLView(getGLContext());
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

    protected void pauseGLRender() {
        getGLContext().getGLView().setVisibility(View.GONE);
        getGLContext().getGLView().setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    protected void resumeGLRender() {
        getGLContext().getGLView().setVisibility(View.VISIBLE);
        getGLContext().getGLView().setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public GLContext getGLContext() {
        return mGLContext;
    }

    public GLView getGLView() {
        return mGLContext.getGLView();
    }

    public GLScene getScene() {
        return mGLContext.getGLView().getScene();
    }
}
