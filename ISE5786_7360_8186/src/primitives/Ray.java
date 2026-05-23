package primitives;

import geometries.api.Intersectable.Intersection;
import java.util.List;
import java.util.Objects;

import static primitives.Util.isZero;

/**
 * Class Ray represents a line segment starting from a point in a specific direction.
 * The direction vector is always kept normalized.
 */
public final class Ray {
    /**
     * Constant for shifting the ray origin to prevent self-intersections.
     */
    private static final double DELTA = 0.1;

    /**
     * The origin point of the ray
     */
    private final Point _origin;
    /**
     * The normalized direction vector of the ray
     */
    private final Vector _direction;

    /**
     * Constructor to initialize Ray with an origin point and a direction vector.
     * The direction vector is normalized automatically.
     *
     * @param origin    the starting point
     * @param direction the direction vector
     */
    public Ray(Point origin, Vector direction) {
        _origin = origin;
        _direction = direction.normalize();
    }

    /**
     * Constructor for generating a shifted ray to avoid self-intersection (Refactoring via RDD).
     * The origin point is moved slightly along the normal vector by DELTA,
     * in the direction of the new ray.
     *
     * @param origin    the original intersection point
     * @param direction the direction of the secondary ray
     * @param normal    the normal vector at the intersection point
     */
    public Ray(Point origin, Vector direction, Vector normal) {
        _direction = direction.normalize();

        // Check the sign of the displacement by looking at the dot product of direction and normal
        double nv = normal.dotProduct(_direction);

        if (isZero(nv)) {
            _origin = origin;
        } else {
            Vector deltaVector = normal.scale(nv > 0 ? DELTA : -DELTA);
            _origin = origin.add(deltaVector);
        }
    }

    /**
     * @return the normalized direction vector
     */
    public Vector direction() {
        return _direction;
    }

    /**
     * @return the starting point (origin) of the ray
     */
    public Point origin() {
        return _origin;
    }

    /**
     * Calculates a point on the ray at distance t from the origin.
     *
     * @param t distance from origin
     * @return the point P = P0 + t*v
     */
    public Point getPoint(double t) {
        if (isZero(t)) return _origin;
        return _origin.add(_direction.scale(t));
    }

    /**
     * Finds the closest point to the ray origin from a list of points.
     * Refactored to use findClosestIntersection for DRY principle.
     *
     * @param points list of points to check
     * @return the closest point, or null if the list is empty
     */
    public Point findClosestPoint(List<Point> points) {
        return points == null || points.isEmpty() ? null
                : findClosestIntersection(points.stream()
                .map(point -> new Intersection(null, point))
                .toList())
                .point;
    }

    /**
     * Finds the closest intersection to the ray origin from a list of intersections.
     *
     * @param intersections list of intersections to check
     * @return the closest intersection, or null if the list is empty
     */
    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections == null || intersections.isEmpty()) return null;

        Intersection result = null;
        double closestDistance = Double.POSITIVE_INFINITY;

        for (Intersection intersection : intersections) {
            double distanceSq = _origin.distanceSquared(intersection.point);
            if (distanceSq < closestDistance) {
                closestDistance = distanceSq;
                result = intersection;
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Ray other)
                && _origin.equals(other._origin)
                && _direction.equals(other._direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_origin, _direction);
    }

    @Override
    public String toString() {
        return "Ray: " + _origin + " " + _direction;
    }
}