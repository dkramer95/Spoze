package edu.neumont.dkramer.spoze3.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Convenience interface to get rid of all the bloat if we don't want to use all these gestures
 * Created by dkramer on 11/12/17.
 */

public interface GestureDetectorAdapter extends GestureDetector.OnGestureListener {

    default boolean onDown(MotionEvent var1) {
        return false;
    }

    default void onShowPress(MotionEvent var1) {

    }

    default boolean onSingleTapUp(MotionEvent var1) {
        return false;
    }

    default boolean onScroll(MotionEvent var1, MotionEvent var2, float var3, float var4) {
        return false;
    }

    default void onLongPress(MotionEvent var1) {

    }

    default boolean onFling(MotionEvent var1, MotionEvent var2, float var3, float var4) {
        return false;
    }
}
