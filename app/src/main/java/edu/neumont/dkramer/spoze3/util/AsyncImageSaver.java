package edu.neumont.dkramer.spoze3.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by dkramer on 12/13/17.
 */

public class AsyncImageSaver extends AsyncTask<ImageSave, Void, Void> {

    @Override
    protected Void doInBackground(ImageSave... imageSaves) {
        ImageSave imageSave = imageSaves[0];
        File dir = new File(imageSave.path);
        dir.mkdirs();

        File file = new File(imageSave.path, imageSave.filename);
        try {
            Bitmap bmp = imageSave.bitmap;
            OutputStream outputStream = new FileOutputStream(file);
            Bitmap.CompressFormat format = getCompressFormat(imageSave.ext);

            bmp.compress(format, imageSave.quality, outputStream);
            outputStream.flush();
            outputStream.close();
            bmp.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap.CompressFormat getCompressFormat(String ext) {
        switch (ext.toLowerCase()) {
            case "jpeg":
            case "jpg":
                return Bitmap.CompressFormat.JPEG;
            case "png":
                return Bitmap.CompressFormat.PNG;
            default:
                throw new IllegalArgumentException(String.format("Unsupported extension: [%s]", ext));
        }
    }
}
