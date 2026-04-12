package primitives;
import static primitives.Util.*;
import java.util.Objects;

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
     * Getter for the direction vector.
     *
     * @return the normalized direction vector
     */
    public Vector direction() {
        return _direction;
    }

    /**
     * Getter for the origin point of the ray.
     *
     * @return the starting point (origin) of the ray
     */
    public Point origin() {
        return _origin;
    }

    /**
     * Calculates a point on the ray at a given distance t from the origin.
     *
     * @param t the distance from the origin
     * @return the point on the ray
     */
    /**
     * Calculates a point on the ray at distance t from the origin.
     * Handles t=0 safely to avoid zero vector creation.
     * * @param t distance from origin (positive, negative or zero)
     * @return the point P = P0 + t*v
     */
    public Point getPoint(double t) {
        return isZero(t) ? _origin : _origin.add(_direction.scale(t));
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Ray other) {
            return _origin.equals(other._origin) && _direction.equals(other._direction);
        }
        return false;
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