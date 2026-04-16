package geometries.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Class Cylinder represents a finite cylinder in 3D space, defined by a radius,
 * an axis ray, and a height.
 * @author Sara
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

        // If the point is at the center of the bottom base
        if (point.equals(p0)) {
            return v.scale(-1);
        }

        double t = alignZero(v.dotProduct(point.subtract(p0)));

        // If the point is on the bottom base
        if (isZero(t)) {
            return v.scale(-1);
        }

        // If the point is on the top base
        if (isZero(t - _height)) {
            return v;
        }

        // Otherwise, it's on the side surface (using Tube logic)
        Point o = _axis.getPoint(t); // Using getPoint refactoring
        return point.subtract(o).normalize();
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = new ArrayList<>();
        Vector v = ray.direction();
        Point p0 = ray.origin();

        Point p1 = _axis.origin();
        Point p2 = _axis.getPoint(_height); // Refactoring: using getPoint() [cite: 1, 81]
        Vector va = _axis.direction();

        // 1. Check intersections with the two bases
        double nv = va.dotProduct(v);
        if (!isZero(nv)) {
            // Lower base intersection
            double t1 = alignZero(va.dotProduct(p1.subtract(p0)) / nv);
            if (t1 > 0) {
                Point p = ray.getPoint(t1);
                if (alignZero(p.distanceSquared(p1) - _radiusSquared) <= 0) {
                    result.add(p);
                }
            }

            // Upper base intersection
            double t2 = alignZero(va.dotProduct(p2.subtract(p0)) / nv);
            if (t2 > 0) {
                Point p = ray.getPoint(t2);
                if (alignZero(p.distanceSquared(p2) - _radiusSquared) <= 0) {
                    result.add(p);
                }
            }
        }

        // 2. Check intersections with the side surface (delegating to Tube)
        // Only if we haven't already found 2 points on the bases
        if (result.size() < 2) {
            List<Point> tubeIntersections = super.findIntersections(ray);
            if (tubeIntersections != null) {
                for (Point p : tubeIntersections) {
                    double t = alignZero(va.dotProduct(p.subtract(p1)));
                    // Check if the intersection point is within the cylinder height range
                    if (t > 0 && t < _height) {
                        result.add(p);
                    }
                }
            }
        }

        return result.isEmpty() ? null : result; // Return null if no intersections found [cite: 1, 27]
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
        return "Cylinder: height=" + _height + ", axis=" + _axis + ", radius=" + _radius;
    }
}