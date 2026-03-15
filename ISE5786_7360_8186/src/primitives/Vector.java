package primitives;

import java.util.Objects;

/**
 * Class Vector represents a direction and magnitude in 3D space.
 * This class inherits from Point and adds vector-specific algebraic operations.
 */
public final class Vector extends Point {

    /** Static Z axis vector for tests */
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

    /**
     * Constructor to initialize Vector based on three double values.
     * @param x first coordinate
     * @param y second coordinate
     * @param z third coordinate
     * @throws IllegalArgumentException if the result is a zero vector
     */
    public Vector(double x, double y, double z) {
        this(new Double3(x, y, z));
    }

    /**
     * Constructor to initialize Vector based on a Double3 object.
     * @param xyz Double3 object containing coordinates
     * @throws IllegalArgumentException if the result is a zero vector
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (_xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
        }
    }

    /**
     * Adds another vector to this vector.
     * This method overrides the add method in Point to ensure a Vector is returned.
     * @param vector the vector to add
     * @return a new Vector representing the sum
     */
    @Override
    public Vector add(Vector vector) {
        return new Vector(_xyz.add(vector._xyz));
    }

    /**
     * Scales the vector by a scalar factor.
     * @param scalar the scaling factor
     * @return a new scaled Vector
     * @throws IllegalArgumentException if the result is a zero vector (e.g., scale by 0)
     */
    public Vector scale(double scalar) {
        return new Vector(_xyz.scale(scalar));
    }

    /**
     * Calculates the dot product of two vectors.
     * @param other the other vector
     * @return the scalar dot product result
     */
    public double dotProduct(Vector other) {
        return _xyz._d1() * other._xyz._d1() +
                _xyz._d2() * other._xyz._d2() +
                _xyz._d3() * other._xyz._d3();
    }

    /**
     * Calculates the cross product of two vectors.
     * @param other the other vector
     * @return a new Vector orthogonal to both operands
     * @throws IllegalArgumentException if vectors are parallel (result is zero vector)
     */
    public Vector crossProduct(Vector other) {
        double x = _xyz._d2() * other._xyz._d3() - _xyz._d3() * other._xyz._d2();
        double y = _xyz._d3() * other._xyz._d1() - _xyz._d1() * other._xyz._d3();
        double z = _xyz._d1() * other._xyz._d2() - _xyz._d2() * other._xyz._d1();
        return new Vector(x, y, z);
    }

    /**
     * Calculates the squared length of the vector.
     * @return the squared length
     */
    public double lengthSquared() {
        return dotProduct(this);
    }

    /**
     * Calculates the length of the vector.
     * @return the magnitude (length)
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes the vector so its length becomes 1.
     * @return a new normalized Vector
     */
    public Vector normalize() {
        return scale(1 / length());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Vector other) {
            return super.equals(other);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Vector: " + _xyz;
    }
}