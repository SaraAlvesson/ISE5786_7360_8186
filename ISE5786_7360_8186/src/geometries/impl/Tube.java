package geometries.impl;

import java.util.List;
import java.util.Objects;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Class Tube represents an infinite tube in 3D space, defined by an axis ray and a radius.
 */
public class Tube extends RadialGeometry {
    /**
     * The axis ray of the tube
     */
    protected final Ray _axis;

    /**
     * Constructor to initialize Tube with radius and axis ray.
     *
     * @param radius the radius of the tube
     * @param axis   the axis ray of the tube
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        _axis = axis;
    }

    @Override
    public Vector getNormal(Point point) {
        Point p0 = _axis.origin();
        Vector v = _axis.direction();

        double t = v.dotProduct(point.subtract(p0));

        if (isZero(t)) {
            return point.subtract(p0).normalize();
        }

        Point o = p0.add(v.scale(t));
        return point.subtract(o).normalize();
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Vector v = ray.direction();
        Vector va = _axis.direction();
        Point p0 = ray.origin();
        Point pa = _axis.origin();

        // Vector orthogonal to axis: v - (v * va) * va
        double vva = v.dotProduct(va);
        Vector vMinusVvaVa = v;
        if (!isZero(vva)) {
            vMinusVvaVa = v.subtract(va.scale(vva));
        }

        // a = (v - (v*va)va)^2
        double a = vMinusVvaVa.lengthSquared();

        // Ray is parallel to axis
        if (isZero(a)) return null;

        Vector deltaP = null;
        try {
            deltaP = p0.subtract(pa);
        } catch (IllegalArgumentException e) {
            // Ray starts at axis origin
            double b = 0;
            double c = -_radius * _radius;
            double disc = -4 * a * c;
            double t = Math.sqrt(disc) / (2 * a);
            return List.of(ray.getPoint(t));
        }

        // Vector orthogonal to axis: deltaP - (deltaP * va) * va
        double dpva = deltaP.dotProduct(va);
        Vector dpMinusDpvaVa = deltaP;
        if (!isZero(dpva)) {
            dpMinusDpvaVa = deltaP.subtract(va.scale(dpva));
        }

        // b = 2 * (v - (v*va)va) * (deltaP - (deltaP*va)va)
        double b = 2 * vMinusVvaVa.dotProduct(dpMinusDpvaVa);
        // c = (deltaP - (deltaP*va)va)^2 - r^2
        double c = dpMinusDpvaVa.lengthSquared() - _radius * _radius;

        // Solve quadratic equation: at^2 + bt + c = 0
        double disc = b * b - 4 * a * c;
        if (disc <= 0) return null; // No real roots

        double sqrtDisc = Math.sqrt(disc);
        double t1 = alignZero((-b - sqrtDisc) / (2 * a));
        double t2 = alignZero((-b + sqrtDisc) / (2 * a));

        if (t1 > 0 && t2 > 0) return List.of(ray.getPoint(t1), ray.getPoint(t2));
        if (t1 > 0) return List.of(ray.getPoint(t1));
        if (t2 > 0) return List.of(ray.getPoint(t2));

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Tube other) {
            return super.equals(obj) && _axis.equals(other._axis);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _axis);
    }

    @Override
    public String toString() {
        return "Tube: axis=" + _axis + ", " + super.toString();
    }
}