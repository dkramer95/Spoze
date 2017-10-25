package edu.neumont.dkramer.spoze3.converter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.renderscript.RenderScript;

/**
 * Created by dkramer on 9/15/17.
 */

public abstract class ImageConverter {
    protected RenderScript mRs;

    // default
    protected ImageConverter() {}

    public ImageConverter(Context ctx) {
        mRs = RenderScript.create(ctx);
    }

    public abstract Bitmap convert(Image image);
    public abstract void destroy();
}
