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
 * Class Cylinder represents a finite cylinder in 3D space, defined by a radius,
 * an axis ray, and a height.
 *
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

        if (point.equals(p0)) {
            return v.scale(-1);
        }

        double t = alignZero(v.dotProduct(point.subtract(p0)));

        if (isZero(t)) {
            return v.scale(-1);
        }

        if (isZero(t - _height)) {
            return v;
        }

        Point o = _axis.getPoint(t);
        return point.subtract(o).normalize();
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> result = null;

        Point baseCenter1 = _axis.origin();
        Point baseCenter2 = _axis.getPoint(_height);
        Vector axisDir = _axis.direction();

        Point rayP0 = ray.origin();
        Vector rayDir = ray.direction();

        // Special case: ray parallel to cylinder axis
        double dirDotAxis = alignZero(rayDir.dotProduct(axisDir));
        if (isZero(Math.abs(dirDotAxis) - 1)) {
            double dist2;
            try {
                Vector delta = rayP0.subtract(baseCenter1);
                Vector radial = delta.subtract(axisDir.scale(delta.dotProduct(axisDir)));
                dist2 = radial.lengthSquared();
            } catch (IllegalArgumentException ex) {
                dist2 = 0;
            }

            // Outside or tangent to side -> no intersections
            if (alignZero(dist2 - _radiusSquared) >= 0) {
                return null;
            }

            double tBottom = alignZero(axisDir.dotProduct(baseCenter1.subtract(rayP0)) / dirDotAxis);
            double tTop = alignZero(axisDir.dotProduct(baseCenter2.subtract(rayP0)) / dirDotAxis);

            if (tBottom > 0 && alignZero(tBottom - maxDistance) <= 0) {
                result = new ArrayList<>();
                result.add(new Intersection(this, ray.getPoint(tBottom)));
            }
            if (tTop > 0 && alignZero(tTop - maxDistance) <= 0) {
                if (result == null) result = new ArrayList<>();
                result.add(new Intersection(this, ray.getPoint(tTop)));
            }

            if (result != null && result.size() == 2) {
                if (rayP0.distanceSquared(result.get(0).point) > rayP0.distanceSquared(result.get(1).point)) {
                    Intersection temp = result.get(0);
                    result.set(0, result.get(1));
                    result.set(1, temp);
                }
            }

            return result;
        }

        // Intersections with the two bases
        double nv = alignZero(axisDir.dotProduct(rayDir));

        if (!isZero(nv)) {
            // Bottom base
            double t1 = alignZero(axisDir.dotProduct(baseCenter1.subtract(rayP0)) / nv);
            if (t1 > 0 && alignZero(t1 - maxDistance) <= 0) {
                Point p1 = ray.getPoint(t1);
                if (alignZero(p1.distanceSquared(baseCenter1) - _radiusSquared) <= 0) {
                    result = new ArrayList<>();
                    result.add(new Intersection(this, p1));
                }
            }

            // Top base
            double t2 = alignZero(axisDir.dotProduct(baseCenter2.subtract(rayP0)) / nv);
            if (t2 > 0 && alignZero(t2 - maxDistance) <= 0) {
                Point p2 = ray.getPoint(t2);
                if (alignZero(p2.distanceSquared(baseCenter2) - _radiusSquared) <= 0) {
                    Intersection inter2 = new Intersection(this, p2);
                    if (result == null) {
                        result = new ArrayList<>();
                        result.add(inter2);
                    } else if (!result.contains(inter2)) {
                        result.add(inter2);
                    }
                }
            }
        }

        // Intersections with the side surface
        if (result == null || result.size() < 2) {
            var tubeIntersections = super.calcIntersectionsHelper(ray, maxDistance);
            if (tubeIntersections != null) {
                for (var inter : tubeIntersections) {
                    Point p = inter.point;
                    double projection = alignZero(axisDir.dotProduct(p.subtract(baseCenter1)));

                    // Include joint points with bases as valid intersections
                    if (projection >= 0 && projection <= _height) {
                        if (result == null) {
                            result = new ArrayList<>();
                        }
                        Intersection newInter = new Intersection(this, p);
                        if (!result.contains(newInter)) {
                            result.add(newInter);
                        }
                    }
                }
            }
        }

        if (result == null || result.isEmpty()) {
            return null;
        }

        if (result.size() == 2 &&
                rayP0.distanceSquared(result.get(0).point) > rayP0.distanceSquared(result.get(1).point)) {
            Intersection temp = result.get(0);
            result.set(0, result.get(1));
            result.set(1, temp);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Cylinder other)) return false;
        return super.equals(other) && isZero(_height - other._height);
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