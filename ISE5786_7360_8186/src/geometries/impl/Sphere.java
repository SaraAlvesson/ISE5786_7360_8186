package geometries.impl;

import java.util.Objects;
import primitives.Point;
import primitives.Vector;

/**
 * Class Sphere represents a sphere in 3D space, defined by a center point and a radius.
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