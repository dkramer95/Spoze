package com.example.dkramer.spoze3.geometry;

/**
 * Created by dkramer on 10/20/17.
 */

public class Point3f {
    public float x;
    public float y;
    public float z;

    public Point3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3f translate(Vector3f v) {
        Point3f p = new Point3f((this.x + v.x), (this.y + v.y), (this.z + v.z));
        return p;
    }
}
