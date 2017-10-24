package edu.neumont.dkramer.spoze3.util;

import android.util.Log;

/**
 * Created by dkramer on 10/20/17.
 */

public class FPSCounter {
    protected static final int NANOS_PER_SECOND = 1000000000;
    protected long startTime = System.nanoTime();
    protected int frames = 0;

    public void logFrame() {
        ++frames;
        long currentTime = System.nanoTime();

        if ((currentTime - startTime) >= NANOS_PER_SECOND) {
            Log.d("FPSCounter", "fps: " + frames);
            frames = 0;
            startTime = currentTime;
        }
    }
}
