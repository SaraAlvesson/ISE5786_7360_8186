package primitives;

/**
 * Class Vector represents a direction and magnitude in 3D space.
 * Inherits from Point.
 * @author Your Name
 */
public final class Vector extends Point {

    /** Static Z axis vector for tests */
    public static final Vector AXIS_Z = new Vector(0,0,1);

    /**
     * Constructor to initialize Vector based on three double values
     * @param x first number
     * @param y second number
     * @param z third number
     * @throws IllegalArgumentException if it's a zero vector
     */
    public Vector(double x, double y, double z) {
        this(new Double3(x, y, z));
    }

    /**
     * Constructor to initialize Vector based on Double3 object
     * @param xyz Double3 object
     * @throws IllegalArgumentException if it's a zero vector
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (_xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector(0,0,0) is not allowed");
        }
    }

    @Override
    public String toString() {
        return "Vector: " + _xyz;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Scalar product of two vectors
     * @param other other vector
     * @return dot product result
     */
    public double dotProduct(Vector other) {
        return _xyz._d1() * other._xyz._d1() +
                _xyz._d2() * other._xyz._d2() +
                _xyz._d3() * other._xyz._d3();
    }

    /**
     * Vector product of two vectors
     * @param other other vector
     * @return new Vector orthogonal to both
     */
    public Vector crossProduct(Vector other) {
        double x = _xyz._d2() * other._xyz._d3() - _xyz._d3() * other._xyz._d2();
        double y = _xyz._d3() * other._xyz._d1() - _xyz._d1() * other._xyz._d3();
        double z = _xyz._d1() * other._xyz._d2() - _xyz._d2() * other._xyz._d1();
        return new Vector(x, y, z);
    }

    public double lengthSquared() {
        return dotProduct(this);
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public Vector normalize() {
        return scale(1 / length());
    }

    public Vector scale(double scalar) {
        return new Vector(_xyz.scale(scalar));
    }
}
