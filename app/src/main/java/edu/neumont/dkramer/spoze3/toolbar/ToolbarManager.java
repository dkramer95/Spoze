package edu.neumont.dkramer.spoze3.toolbar;

import android.content.Context;
import android.transition.Scene;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewFlipper;

import java.util.HashMap;
import java.util.Map;

import edu.neumont.dkramer.spoze3.R;

/**
 * This class manages transitioning between different toolbars by providing
 * a variety of ways to alter the content of a toolbar. Toolbars can be
 * hidden immediately, or animated to give a smoother visual aesthetic.
 * Created by dkramer on 12/13/17.
 */

public class ToolbarManager extends ViewFlipper {
    private Map<IToolbar, Integer> mToolbars;
    private Scene mScene;


    public ToolbarManager(Context context) {
        super(context);
        init();
    }

    public ToolbarManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void loadToolbars(IToolbar[] toolbars) {
        mToolbars = new HashMap<>();
        LayoutInflater inflater = getLayoutInflater();

        for (IToolbar t : toolbars) {
            addToolbar(t, inflater);
        }
    }

    private void init() {
        setInAnimation(getContext(), R.anim.fade_in);
        setOutAnimation(getContext(), R.anim.fade_out);
    }

    public void setToolbar(IToolbar toolbar) {
        setDisplayedChild(getViewIndex(toolbar));
    }


    public void fadeInToolbar() {
        // quickly make visible, but fade out
        setVisibility(View.VISIBLE);
        animate().alpha(0).start();

        // fade in
        animate().alpha(1).setDuration(250).start();
    }

    public void fadeOutToolbar(Runnable endAction) {
        animate().alpha(0).setDuration(250)
                .withEndAction(() -> {
                    setVisibility(View.INVISIBLE);
                    if (endAction != null) {
                        endAction.run();
                    }
                }).start();
    }

    private void addToolbar(IToolbar toolbar, LayoutInflater inflater) {
        View view = inflater.inflate(toolbar.getLayoutId(), null);
        addView(view);
        mToolbars.put(toolbar, indexOfChild(view));
    }

    private int getViewIndex(IToolbar toolbar) {
        return mToolbars.get(toolbar);
    }

    private LayoutInflater getLayoutInflater() {
        return (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

}
