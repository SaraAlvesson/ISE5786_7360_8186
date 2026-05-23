package geometries.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Class Tube represents an infinite tube in 3D space, defined by an axis ray and a radius.
 *
 * @author Sara
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

        // If the projection is exactly at the ray's origin
        if (isZero(t)) {
            return point.subtract(p0).normalize();
        }

        Point o = _axis.getPoint(t);
        return point.subtract(o).normalize();
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        Vector v = ray.direction();
        Vector va = _axis.direction();
        Point p0 = ray.origin();
        Point pa = _axis.origin();

        double vVa = alignZero(v.dotProduct(va));

        Vector vOrth;
        if (isZero(vVa)) {
            vOrth = v;
        } else {
            try {
                vOrth = v.subtract(va.scale(vVa));
            } catch (IllegalArgumentException e) {
                // v parallel to axis
                return null;
            }
        }

        double a = alignZero(vOrth.lengthSquared());
        if (isZero(a)) return null;

        if (p0.equals(pa)) {
            double t = alignZero(_radius / Math.sqrt(a));
            if (t > 0 && alignZero(t - maxDistance) <= 0) {
                return List.of(new Intersection(this, ray.getPoint(t)));
            }
            return null;
        }

        Vector deltaP = p0.subtract(pa);
        double dpVa = alignZero(deltaP.dotProduct(va));

        Vector dpOrth = null;

        if (isZero(dpVa)) {
            dpOrth = deltaP;
        } else {
            Point o = _axis.getPoint(dpVa); // point on axis closest in projection to p0
            if (!p0.equals(o)) {
                dpOrth = p0.subtract(o);
            }
        }

        double b = dpOrth == null ? 0 : alignZero(2 * vOrth.dotProduct(dpOrth));
        double c = alignZero((dpOrth == null ? 0 : dpOrth.lengthSquared()) - _radiusSquared);

        double discriminant = alignZero(b * b - 4 * a * c);
        if (discriminant <= 0) return null;

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t1 = alignZero((-b - sqrtDiscriminant) / (2 * a));
        double t2 = alignZero((-b + sqrtDiscriminant) / (2 * a));

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

        // Sorting: points must be ordered by distance from ray origin
        if (result != null && result.size() == 2 && t2 < t1) {
            return List.of(result.get(1), result.get(0));
        }

        return result;
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
        return "Tube: axis=" + _axis + ", radius=" + _radius;
    }
}