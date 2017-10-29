package edu.neumont.dkramer.spoze3.models;

import android.graphics.Bitmap;

import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLProgram;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLUtils.texImage2D;

/**
 * Created by dkramer on 10/28/17.
 */

public class GLLiveTexturedRect extends GLTexturedRect {
    protected static GLProgram sProgram;

    protected GLLiveTexturedRect(GLContext glContext, float[] vertexData) {
        super(glContext, vertexData);
    }

    public static GLLiveTexturedRect createFromBitmap(GLContext ctx, Bitmap src, float maxWidth, float maxHeight) {
        final Bitmap bmp = getFittedBitmap(src, maxWidth, maxHeight);

        // ensure vertex data is normalized according to bitmap
        final float[] VERTEX_DATA =
                createScaledVertexData(bmp.getWidth(), bmp.getHeight(), maxWidth, maxHeight);

        final GLLiveTexturedRect rect = new GLLiveTexturedRect(ctx, VERTEX_DATA) {
            @Override
            protected GLProgram createGLProgram() {
                GLProgram program = super.createGLProgram();
                loadTexture(bmp);
                return program;
            };
        };
        return rect;
    }

    @Override
    protected GLProgram createGLProgram() {
        if (sProgram == null) {
            sProgram = loadProgram(getGLContext());
        }
        return sProgram;
    }

    public void update(Bitmap bmp) {
        texImage2D(GL_TEXTURE_2D, 0, bmp, 0);
    }

//    public void applyTransformations() {
//        super.applyTransformations();
//        Matrix.rotateM(mModelMatrix, mMVPMatrixHandle, GLDeviceInfo.get(CURRENT_PITCH), 0, 0, 1);
//    }
}
