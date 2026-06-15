package geometries.impl;

import geometries.api.BoundingBox;
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
     * The center point of the sphere.
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
        updateBoundingBox();
    }

    @Override
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }

    /**
     * Creates the bounding box for the sphere.
     * For a sphere, the box is center +/- radius on each axis.
     *
     * @return bounding box of the sphere
     */
    @Override
    protected BoundingBox createBoundingBox() {
        return new BoundingBox(
                _center.getX() - _radius,
                _center.getX() + _radius,
                _center.getY() - _radius,
                _center.getY() + _radius,
                _center.getZ() - _radius,
                _center.getZ() + _radius
        );
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        Vector u;
        try {
            u = _center.subtract(p0);
        } catch (IllegalArgumentException ignore) {
            if (alignZero(_radius - maxDistance) <= 0) {
                return List.of(new Intersection(this, ray.getPoint(_radius)));
            }
            return null;
        }

        double tm = alignZero(v.dotProduct(u));
        double dSquared = alignZero(u.lengthSquared() - tm * tm);

        if (dSquared >= _radiusSquared) {
            return null;
        }

        double th = alignZero(Math.sqrt(_radiusSquared - dSquared));

        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        if (t1 <= 0 && t2 <= 0) {
            return null;
        }

        List<Intersection> result = null;

        if (t1 > 0 && alignZero(t1 - maxDistance) <= 0) {
            if (result == null) {
                result = new ArrayList<>();
            }
            result.add(new Intersection(this, ray.getPoint(t1)));
        }

        if (t2 > 0 && alignZero(t2 - maxDistance) <= 0) {
            if (result == null) {
                result = new ArrayList<>();
            }
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