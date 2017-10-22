package com.example.dkramer.spoze3.geometry;

/**
 * Created by dkramer on 10/20/17.
 */

public class Ray {
    public final Point3f point;
    public final Vector3f vector;

    public Ray(Point3f point, Vector3f vector) {
        this.point = point;
        this.vector = vector;
    }
}
