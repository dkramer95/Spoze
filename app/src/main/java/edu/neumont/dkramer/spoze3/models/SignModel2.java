package edu.neumont.dkramer.spoze3.models;

import android.graphics.Bitmap;
import android.opengl.Matrix;

import edu.neumont.dkramer.spoze3.GLPickerModel;
import edu.neumont.dkramer.spoze3.R;
import edu.neumont.dkramer.spoze3.geometry.Point3f;
import edu.neumont.dkramer.spoze3.gl.GLCamera;
import edu.neumont.dkramer.spoze3.gl.GLContext;
import edu.neumont.dkramer.spoze3.gl.GLProgram;
import edu.neumont.dkramer.spoze3.util.TextResourceReader;

/**
 * Created by dkramer on 11/9/17.
 */

public abstract class SignModel2 extends GLTexturedRect {
    protected GLPickerModel mPickerModel;
    protected float mRotation;


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

    protected void applyTransformations() {
        super.applyTransformations();
        Matrix.translateM(mModelMatrix, 0, mTransX, mTransY, 0);
        Matrix.rotateM(mModelMatrix, 0, mRotation, 0, 0, 1);
    }

    protected GLProgram loadTextureProgram(GLContext ctx) {
        String vertexShaderCode =
                TextResourceReader.readTextFileFromResource(ctx, R.raw.texture_vertex_shader);

        String fragmentShaderCode =
                TextResourceReader.readTextFileFromResource(ctx, R.raw.texture_fragment_shader);

        mGLProgram = GLProgram.create(vertexShaderCode, fragmentShaderCode);
        return mGLProgram;
    }

    public void translate(float x, float y, float z) {
        Point3f cameraEye = getWorld().getCamera().getEye();

        //TODO this works fine in portrait mode, but in landscape it is off!!
        //TODO::: determine the orientation and determine the scale factor to apply
        mTransX = x + (mWidth / 2) + cameraEye.x;

        // this works well
        mTransY = (mHeight / 2) + (y * 2f) + cameraEye.y;
    }
}
