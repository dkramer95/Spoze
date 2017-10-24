package com.example.dkramer.spoze3;

import com.example.dkramer.spoze3.gl.GLContext;
import com.example.dkramer.spoze3.gl.GLWorld;
import com.example.dkramer.spoze3.models.GLTexturedRect;

/**
 * Created by dkramer on 10/23/17.
 */

public class MyGLWorld extends GLWorld {

	public MyGLWorld(GLContext glContext) {
		super(glContext);
	}

	@Override
	public void create() {
		final GLContext ctx = getGLContext();
		addModel(GLTexturedRect.createFromResource(ctx, R.drawable.texture4, getWidth(), getHeight()));
	}
}
