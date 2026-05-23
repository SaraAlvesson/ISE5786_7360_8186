package geometries.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;

/**
 * Class Sphere represents a sphere in 3D space, defined by a center point and a radius.
 *
 * @author Sara
 */
public final class Sphere extends RadialGeometry {
    /**
     * The center point of the sphere
     */
    private final Point _center;

    /**
     * Constructor to initialize Sphere with a center point and a radius.
     *
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
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Vector u = O - P0
        Vector u;
        try {
            u = _center.subtract(p0);
        } catch (IllegalArgumentException ignore) {
            // If p0 is at the center of the sphere, return the point at radius distance if within maxDistance
            if (alignZero(_radius - maxDistance) <= 0) {
                return List.of(new Intersection(this, ray.getPoint(_radius)));
            }
            return null;
        }

        // tm = v * u
        double tm = alignZero(v.dotProduct(u));

        // d^2 = |u|^2 - tm^2
        double dSquared = alignZero(u.lengthSquared() - tm * tm);

        // If distance squared is greater than or equal to radius squared, no intersections
        if (dSquared >= _radiusSquared) {
            return null;
        }

        // th = sqrt(r^2 - d^2)
        double th = alignZero(Math.sqrt(_radiusSquared - dSquared));

        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        // We only return intersections where t > 0 (in the direction of the ray)
        if (t1 <= 0 && t2 <= 0) {
            return null;
        }

        List<Intersection> result = null;

        // Check first intersection point t1
        if (t1 > 0 && alignZero(t1 - maxDistance) <= 0) {
            if (result == null) result = new ArrayList<>();
            result.add(new Intersection(this, ray.getPoint(t1)));
        }

        // Check second intersection point t2
        if (t2 > 0 && alignZero(t2 - maxDistance) <= 0) {
            if (result == null) result = new ArrayList<>();
            result.add(new Intersection(this, ray.getPoint(t2)));
        }

        return result;
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