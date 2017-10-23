package com.example.dkramer.spoze3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

import com.example.dkramer.spoze3.gl.GLCamera;
import com.example.dkramer.spoze3.gl.GLContext;
import com.example.dkramer.spoze3.gl.GLModel;
import com.example.dkramer.spoze3.gl.GLProgram;
import com.example.dkramer.spoze3.gl.GLScene;
import com.example.dkramer.spoze3.gl.GLTexture;
import com.example.dkramer.spoze3.gl.GLView;
import com.example.dkramer.spoze3.gl.GLWorld;
import com.example.dkramer.spoze3.models.GLSquare;
import com.example.dkramer.spoze3.models.GLTexturedSquare;

/**
 * Created by dkramer on 10/20/17.
 */

public class MyGLView extends GLView {

    public MyGLView(GLContext context) {
        super(context);
    }

    public MyGLView(GLContext context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public GLScene createScene() {
//        final GLContext ctx = getGLContext();
//
//        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.texture);
//        Bitmap textureBitmap = GLTexture.getFittedBitmap(bmp, getWidth(), getHeight());
//
//        final float w = (textureBitmap.getWidth() / (float)getWidth() * 2 - 1);
//        final float h = -((textureBitmap.getHeight() / (float)getWidth()) * 2 - 1);
//
//        GLScene scene = new GLScene(new GLWorld(ctx) {
//            @Override
//            public void create() {
//                GLModel signModel = new GLModel(ctx) {
//                    final float[] VERTEX_DATA = {
//
//                    };
//
//                    @Override
//                    protected GLProgram createGLProgram() {
//                        return null;
//                    }
//
//                    @Override
//                    public void render(GLCamera camera) {
//
//                    }
//
//                    @Override
//                    public float[] getVertexData() {
//                        return VERTEX_DATA;
//                    }
//                };
//            }
//        });
//        return scene;

        GLScene scene = new GLScene(new MyGLWorld(getGLContext()));
        return scene;
    }
}
