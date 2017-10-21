package com.example.dkramer.spoze3.util;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by dkramer on 10/20/17.
 */

public class TextResourceReader {

    public static String readTextFileFromResource(Context ctx, int resourceId) {
        String result = null;
        InputStream inputStream = ctx.getResources().openRawResource(resourceId);
        try {
            result = IOUtils.toString(inputStream, Charset.defaultCharset());
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
