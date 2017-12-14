package edu.neumont.dkramer.spoze3.util;

import android.graphics.Bitmap;

/**
 * Simple pojo that stores information about persisting a bitmap to disk
 * Created by dkramer on 12/13/17.
 */

public class ImageSave {
    public Bitmap bitmap;
    public String path;
    public String filename;
    public String ext;
    public int quality;

    public ImageSave(Bitmap bmp, String path, String filename, String ext, int quality) {
        this.bitmap = bmp;
        this.path = path;
        this.filename = filename;
        this.ext = ext;
        this.quality = quality;
    }
}
