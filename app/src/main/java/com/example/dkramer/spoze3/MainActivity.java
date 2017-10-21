package com.example.dkramer.spoze3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.dkramer.spoze3.gl.GLActivity;
import com.example.dkramer.spoze3.gl.GLView;

public class MainActivity extends GLActivity {

    @Override
    protected GLView createGLView() {
        return new MyGLView(this);
    }
}
