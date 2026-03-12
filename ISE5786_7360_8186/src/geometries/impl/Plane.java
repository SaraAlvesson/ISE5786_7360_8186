package geometries.impl;

import geometries.api.Geometry;
import primitives.*;
import java.util.Objects;

/**
 * Class Plane represents a 2D plane in 3D space, defined by a point and a normal vector.
 */
public final class Plane implements Geometry {
    /** A point on the plane */
    private final Point _point;
    /** The normal vector to the plane */
    private final Vector _normal;

    /**
     * Constructor to initialize Plane with a point and a normal vector.
     * @param point  a point on the plane
     * @param normal the normal vector (will be normalized)
     */
    public Plane(Point point, Vector normal) {
        _point = point;
        _normal = normal.normalize();
    }

    /**
     * Constructor to initialize Plane with three points.
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        _point = p1;
        // Calculation logic to be added in later stages
        _normal = null;
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Plane other) {
            // Handle null for _normal due to the placeholder constructor
            return _point.equals(other._point) &&
                    (_normal == null ? other._normal == null : _normal.equals(other._normal));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_point, _normal);
    }

    @Override
    public String toString() {
        return "Plane: point=" + _point + ", normal=" + _normal;
    }
}