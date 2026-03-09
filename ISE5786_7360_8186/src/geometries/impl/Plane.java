package geometries.impl;

import geometries.api.Geometry;
import primitives.*;

/**
 * Class Plane represents a 2D plane in 3D space
 */
public class Plane implements Geometry {
    private final Point _q0;
    private final Vector _normal;

    /** Constructor by point and normal */
    public Plane(Point q0, Vector normal) {
        _q0 = q0;
        _normal = normal.normalize();
    }

    /** Constructor by 3 points - placeholder for now */
    public Plane(Point p1, Point p2, Point p3) {
        _q0 = p1;
        // Calculation logic to be added in later stages
        _normal = null;
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }
}