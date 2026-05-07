package geometries.impl;

import geometries.api.Geometry;
import java.util.List;
import java.util.Objects;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Class Plane represents a 2D plane in 3D space, defined by a point and a normal vector.
 */
public final class Plane extends Geometry {
    /**
     * A point on the plane
     */
    private final Point _point;
    /**
     * The normal vector to the plane
     */
    private final Vector _normal;

    /**
     * Constructor to initialize Plane with a point and a normal vector.
     *
     * @param point  a point on the plane
     * @param normal the normal vector (will be normalized)
     */
    public Plane(Point point, Vector normal) {
        _point = point;
        _normal = normal.normalize();
    }

    /**
     * Constructor to initialize Plane with three points.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @throws IllegalArgumentException if points are co-linear or overlap
     */
    public Plane(Point p1, Point p2, Point p3) {
        _point = p1;

        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);

        _normal = v1.crossProduct(v2).normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();
        Vector n = _normal;

        // n * v
        double nv = n.dotProduct(v);

        // Ray is parallel to the plane
        if (isZero(nv)) {
            return null;
        }

        // If the ray starts at the plane reference point
        if (_point.equals(p0)) {
            return null;
        }

        // n * (Q0 - P0)
        double nQMinusP0 = n.dotProduct(_point.subtract(p0));

        // t = Numerator / Denominator
        double t = alignZero(nQMinusP0 / nv);

        // Intersection must be in the ray's direction (t > 0)
        if (t <= 0) {
            return null;
        }

        return List.of(new Intersection(this, ray.getPoint(t)));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Plane other) {
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