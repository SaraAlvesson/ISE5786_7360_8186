package primitives;

import java.util.Objects;

/**
 * Class Ray represents a line segment starting from a point in a direction.
 * @author Your Name
 */
public final class Ray {
    private final Point _head;
    private final Vector _direction;

    /**
     * Constructor for Ray
     * @param head origin point
     * @param direction direction vector (will be normalized)
     */
    public Ray(Point head, Vector direction) {
        _head = head;
        _direction = direction.normalize();
    }

    public Point head() { return _head; }
    public Vector direction() { return _direction; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ray other = (Ray) obj;
        return _head.equals(other._head) && _direction.equals(other._direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_head, _direction);
    }

    @Override
    public String toString() {
        return "Ray: head=" + _head + ", direction=" + _direction;
    }
}