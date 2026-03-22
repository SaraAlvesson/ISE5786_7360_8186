package primitives;

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
        _direction = direction.normalize(); // Ensure direction is normalized
    }

    /**
     * Getter for the direction vector.
     * Note: This method is required by the provided Main.java test.
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