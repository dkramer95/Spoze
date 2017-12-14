package edu.neumont.dkramer.spoze3.fragments;

/**
 * Created by dkramer on 12/13/17.
 */

public class OverlayManager {
    private static OverlayFragment sFragment;

    public static void setVisibleFragment(OverlayFragment fragment) {
        sFragment = fragment;
    }

    public static boolean isOverlayShowing() {
        return sFragment != null;
    }

    private OverlayManager() { }
}
