package edu.neumont.dkramer.spoze3.models;

import android.graphics.Bitmap;

import edu.neumont.dkramer.spoze3.R;
import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLProgram;
import edu.neumont.dkramer.spoze3.util.TextResourceReader;

/**
 * Created by dkramer on 11/9/17.
 */

public abstract class SignModel2 extends GLTexturedRect {
    protected GLPickerModel mPickerModel;


    protected SignModel2(GLContext glContext, float[] vertexData) {
        super(glContext, vertexData);
        mPickerModel = new GLPickerModel(glContext, this);
    }

    public static SignModel2 fromBitmap(GLContext ctx, Bitmap src, float maxWidth, float maxHeight) {
        final Bitmap bmp = getFittedBitmap(src, maxWidth, maxHeight);

        final float[] scaledSizes = getScaledSizes(bmp.getWidth(), bmp.getHeight(), maxWidth, maxHeight);
        final float width = scaledSizes[0];
        final float height = scaledSizes[1];
        final float[] vertexData = createScaledVertexData(width, height);

        return new SignModel2(ctx, vertexData) {
            @Override
            protected GLProgram createGLProgram() {
                GLProgram program = loadTextureProgram(getGLContext());
                loadTexture(bmp);
                bmp.recycle();
                return program;
            }
        };
    }

    @Override
    public void drawSelector(GLCamera camera) {
        mPickerModel.render(camera);
    }

    public boolean didTouch(int pixel) {
        return mPickerModel.getPixelId() == (pixel >> 16);
    }

    protected GLProgram loadTextureProgram(GLContext ctx) {
        String vertexShaderCode =
                TextResourceReader.readTextFileFromResource(ctx, R.raw.texture_vertex_shader);

        String fragmentShaderCode =
                TextResourceReader.readTextFileFromResource(ctx, R.raw.texture_fragment_shader);

        mGLProgram = GLProgram.create(vertexShaderCode, fragmentShaderCode);
        return mGLProgram;
    }

    public void handleTouchMove(float x, float y, float z, float eyeX, float eyeY, float eyeZ) {
        mTransX = x + (mWidth / 2) + eyeX;
        mTransY = (mHeight / 2) + (y * 2f) + eyeY;
        mPickerModel.translate(mTransX, mTransY, 0);
    }

    public void rotate(float angle) {
        super.rotate(angle);
        mPickerModel.rotate(angle);
    }
}
