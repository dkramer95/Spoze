package edu.neumont.dkramer.spoze3.gl;

/**
 * Created by dkramer on 10/25/17.
 */

public class GLMotionCamera extends GLCamera {

	protected GLMotionCamera(GLContext ctx) {
		super(ctx);
	}

	@Override
	public void update() {
		super.update();
		//apply gyroscope readings here
	}
}
