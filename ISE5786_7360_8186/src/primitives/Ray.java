package primitives;

import java.util.List;
import java.util.Objects;

import static primitives.Util.isZero;

/**
 * Class Ray represents a line segment starting from a point in a specific direction.
 * The direction vector is always kept normalized.
 */
public final class Ray {
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
     *
     * @param points list of points to check
     * @return the closest point, or null if the list is empty
     */
    public Point findClosestPoint(List<Point> points) {
        if (points == null || points.isEmpty()) return null; //

        Point result = null;
        double closestDistance = Double.POSITIVE_INFINITY; //[cite: 3]

        for (Point p : points) {
            // Using distanceSquared for better performance[cite: 3]
            double distanceSq = _origin.distanceSquared(p);
            if (distanceSq < closestDistance) {
                closestDistance = distanceSq;
                result = p;
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