package edu.neumont.dkramer.spoze3.fragments;

import android.app.DialogFragment;

import edu.neumont.dkramer.spoze3.R;

/**
 * Created by dkramer on 12/13/17.
 */

public class OverlayFragment extends DialogFragment {

    public void fadeOut() {
        OverlayManager.setVisibleFragment(null);
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .hide(this)
                .commit();
    }

    public void fadeIn() {
        OverlayManager.setVisibleFragment(this);
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .show(this)
                .commit();
    }

    public void hide() {
        fadeOut();
    }

    public void show() {
        fadeIn();
    }
}
