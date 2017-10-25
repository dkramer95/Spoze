package edu.neumont.dkramer.spoze3.gl;

/**
 * Created by dkramer on 10/20/17.
 */

public abstract class GLDeviceInfo extends GLObject {
    /**
     * Constant values to be used when creating a GLContext and enabling
     * specific GLDeviceInfo types, using GLContext.enableDeviceInfo(type);
     */
    public static final int TYPE_TOUCH_INPUT = 1;
    public static final int TYPE__ROTATION_VECTOR = 100;


    public GLDeviceInfo(GLContext ctx) {
        super(ctx);
    }


    public abstract void start();
    public abstract void stop();

}
