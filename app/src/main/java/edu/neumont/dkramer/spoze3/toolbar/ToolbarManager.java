package edu.neumont.dkramer.spoze3.toolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewFlipper;

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages transitioning between different toolbars by providing
 * a variety of ways to alter the content of a toolbar. Toolbars can be
 * hidden immediately, or animated to give a smoother visual aesthetic.
 * Created by dkramer on 12/13/17.
 */

public class ToolbarManager extends ViewFlipper {
    private Map<IToolbar, Integer> mToolbars;


    public ToolbarManager(Context context) {
        super(context);
    }

    public ToolbarManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void loadToolbars(IToolbar[] toolbars) {
        mToolbars = new HashMap<>();
        LayoutInflater inflater = getLayoutInflater();

        for (IToolbar t : toolbars) {
            addToolbar(t, inflater);
        }
    }

    public void setToolbar(IToolbar toolbar) {
        setDisplayedChild(getViewIndex(toolbar));
    }

    public void hide() {
        setVisibility(View.INVISIBLE);
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    public void fadeInToolbar(int x) {

    }

    public void fadeOutToolbar() {

    }

    public void fadeOutToolbar(Runnable r) {

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
