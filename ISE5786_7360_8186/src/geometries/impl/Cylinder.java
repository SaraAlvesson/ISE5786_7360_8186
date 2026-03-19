package geometries.impl;

import java.util.Objects;
import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import static primitives.Util.isZero;


/**
 * Class Cylinder represents a finite cylinder in 3D space, defined by a radius,
 * an axis ray, and a height.
 */
public final class Cylinder extends Tube {
    /**
     * The height of the cylinder
     */
    private final double _height;

    /**
     * Constructor to initialize Cylinder with radius, axis ray, and height.
     *
     * @param radius  the radius of the cylinder
     * @param axisRay the axis ray of the cylinder
     * @param height  the height of the cylinder
     */
    public Cylinder(double radius, Ray axisRay, double height) {
        super(radius, axisRay);
        _height = height;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        if (point.equals(p0)) {
            return v.scale(-1);
        }

        double t = v.dotProduct(point.subtract(p0));

        if (Util.isZero(t)) {
            return v.scale(-1);
        }

        if (Util.isZero(t - _height)) {
            return v;
        }

        Point o = p0.add(v.scale(t));

        if (point.equals(o)) {
            return v;
        }

        return point.subtract(o).normalize();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Cylinder other) {
            return super.equals(obj) && isZero(_height - other._height);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _height);
    }

    @Override
    public String toString() {
        return "Cylinder: height=" + _height + ", " + super.toString();
    }
}