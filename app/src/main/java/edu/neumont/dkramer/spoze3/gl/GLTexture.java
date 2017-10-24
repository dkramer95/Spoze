package edu.neumont.dkramer.spoze3.gl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

/**
 * Created by dkramer on 10/21/17.
 */

public final class GLTexture {
    private static final int GL_TEXTURE_ERROR = 0;

    private int mTextureId;

    private GLTexture() { }

    public static GLTexture createFromResource(Context ctx, int resourceId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap bmp = BitmapFactory.decodeResource(ctx.getResources(), resourceId, options);
        GLTexture texture = createFromBitmap(bmp);
        return texture;
    }

    public static GLTexture createFromBitmap(Bitmap bmp) {
        GLTexture texture = new GLTexture();
        int textureId = createTextureId();

        glBindTexture(GL_TEXTURE_2D, textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        texImage2D(GL_TEXTURE_2D, 0, bmp, 0);

        bmp.recycle();
//        glGenerateMipmap(GL_TEXTURE_2D);
        texture.mTextureId = textureId;

        return texture;
    }

    public static Bitmap getFittedBitmap(Bitmap bmp, float maxWidth, float maxHeight) {
        float imgWidth = bmp.getWidth();
        float imgHeight = bmp.getHeight();
        float ratio = Math.min((maxWidth / imgWidth), (maxHeight / imgHeight));

        int newWidth = (int)(imgWidth * ratio);
        int newHeight = (int)(imgHeight * ratio);

        Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
        return scaledBmp;
    }

    private static int createTextureId() {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        int textureId = textureObjectIds[0];
        return textureId;
    }

    public int getTextureId() {
        return mTextureId;
    }
}
