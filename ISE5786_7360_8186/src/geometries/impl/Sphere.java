package geometries.impl;

import java.util.List;
import java.util.Objects;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Class Sphere represents a sphere in 3D space, defined by a center point and a radius.
 * @author Sara
 */
public final class Sphere extends RadialGeometry {
    /**
     * The center point of the sphere
     */
    private final Point _center;

    /**
     * Constructor to initialize Sphere with a center point and a radius.
     * @param center the center point of the sphere
     * @param radius the radius of the sphere
     */
    public Sphere(Point center, double radius) {
        super(radius);
        _center = center;
    }

    @Override
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Vector u = O - P0
        Vector u;
        try {
            u = _center.subtract(p0);
        } catch (IllegalArgumentException ignore) {
            // If p0 is at the center of the sphere, return the point at radius distance [cite: 1, 150]
            return List.of(ray.getPoint(_radius));
        }

        // tm = v * u
        double tm = alignZero(v.dotProduct(u));

        // d^2 = |u|^2 - tm^2
        double dSquared = alignZero(u.lengthSquared() - tm * tm);

        // If distance squared is greater than or equal to radius squared, no intersections [cite: 1, 132, 153]
        if (dSquared >= _radiusSquared) {
            return null;
        }

        // th = sqrt(r^2 - d^2)
        double th = alignZero(Math.sqrt(_radiusSquared - dSquared));

        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        // We only return intersections where t > 0 (in the direction of the ray) [cite: 1, 54]
        if (t1 <= 0 && t2 <= 0) {
            return null;
        }

        // Case: Two intersection points
        if (t1 > 0 && t2 > 0) {
            // Sorting: points must be ordered by distance from ray origin
            return t1 < t2 ? List.of(ray.getPoint(t1), ray.getPoint(t2))
                    : List.of(ray.getPoint(t2), ray.getPoint(t1));
        }

        // Case: One intersection point
        return t1 > 0 ? List.of(ray.getPoint(t1)) : List.of(ray.getPoint(t2));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Sphere other) {
            return super.equals(obj) && _center.equals(other._center);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _center);
    }

    @Override
    public String toString() {
        return "Sphere: center=" + _center + ", " + super.toString();
    }
}