package com.example.dkramer.spoze3;

import com.example.dkramer.spoze3.gl.GLActivity;
import com.example.dkramer.spoze3.gl.GLCameraActivity;
import com.example.dkramer.spoze3.gl.GLView;

public class MainActivity extends GLCameraActivity {

    @Override
    protected GLView createGLView() {
        return new MyGLView(getGLContext());
    }
}
