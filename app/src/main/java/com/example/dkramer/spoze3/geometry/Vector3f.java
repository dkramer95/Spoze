package com.example.dkramer.spoze3.geometry;

/**
 * Created by dkramer on 10/20/17.
 */

public class Vector3f {
    public final float x;
    public final float y;
    public final float z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vector3f between(Point3f from, Point3f to) {
        Vector3f vector = new Vector3f(
            to.x - from.x,
            to.y - from.y,
            to.z - from.z
        );
        return vector;
    }

    public float length() {
        float result = (float)Math.sqrt((x * x) + (y * y) + (z * z));
        return result;
    }

    public Vector3f crossProduct(Vector3f other) {
        Vector3f vector = new Vector3f(
            (y * other.z) - (z * other.y),
            (z * other.x) - (x * other.z),
            (x * other.y) - (y * other.x)
        );
        return vector;
    }
}
