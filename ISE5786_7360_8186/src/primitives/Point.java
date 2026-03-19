package primitives;


/**
 * Class Point is the basic object for 3D coordinates.
 *
 * @author Your Name
 */
public class Point {
    /**
     * Coordinates of the point
     */
    protected final Double3 _xyz;

    /**
     * Static zero point (0,0,0)
     */
    public static final Point ZERO = new Point(Double3.ZERO);

    /**
     * Constructor to initialize Point based on three double values
     *
     * @param x first number
     * @param y second number
     * @param z third number
     */
    public Point(double x, double y, double z) {
        _xyz = new Double3(x, y, z);
    }

    /**
     * Constructor to initialize Point based on Double3 object
     *
     * @param xyz Double3 object
     */
    public Point(Double3 xyz) {
        _xyz = xyz;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point other = (Point) obj;
        return _xyz.equals(other._xyz);
    }

    @Override
    public int hashCode() {
        return _xyz.hashCode();
    }

    @Override
    public String toString() {
        return "Point: " + _xyz;
    }

    /**
     * Calculate squared distance between two points
     *
     * @param other the other point
     * @return squared distance
     */
    public double distanceSquared(Point other) {
        double dx = _xyz._d1() - other._xyz._d1();
        double dy = _xyz._d2() - other._xyz._d2();
        double dz = _xyz._d3() - other._xyz._d3();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Calculate distance between two points
     *
     * @param other the other point
     * @return distance
     */
    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Add a vector to the point
     *
     * @param vector the vector to add
     * @return new Point
     */
    public Point add(Vector vector) {
        return new Point(_xyz.add(vector._xyz));
    }

    /**
     * Subtract two points into a vector
     *
     * @param other the other point
     * @return new Vector from other to this
     */
    public Vector subtract(Point other) {
        return new Vector(_xyz.subtract(other._xyz));
    }
}

