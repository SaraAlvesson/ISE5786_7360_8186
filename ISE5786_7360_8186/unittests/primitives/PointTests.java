package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link primitives.Point} class.
 *
 * @author Sara
 */
class PointTests {
    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for point subtraction logic
     */
    private static final String ERROR_SUBTRACT = "ERROR: Point subtract Point does not work correctly";

    /**
     * Error message for zero vector exception when subtracting point from itself
     */
    private static final String ERROR_SUBTRACT_SAME = "ERROR: Point subtract itself does not throw an exception";

    /**
     * Error message for point and vector addition logic
     */
    private static final String ERROR_ADD = "ERROR: Point + Vector does not work correctly";

    /**
     * Error message for addition resulting in the zero point
     */
    private static final String ERROR_ADD_ZERO = "ERROR: Point + Vector resulting in ZERO does not work correctly";

    /**
     * Error message for distance squared calculation
     */
    private static final String ERROR_DISTANCE_SQUARED = "ERROR: distanceSquared between points is wrong";

    /**
     * Error message for distance squared to the point itself
     */
    private static final String ERROR_DISTANCE_SQUARED_SAME = "ERROR: distanceSquared to itself is not zero";

    /**
     * Error message for distance calculation
     */
    private static final String ERROR_DISTANCE = "ERROR: distance between points is wrong";

    /**
     * Error message for distance to the point itself
     */
    private static final String ERROR_DISTANCE_SAME = "ERROR: distance to itself is not zero";

    /**
     * Test method for {@link primitives.Point#subtract(primitives.Point)}.
     */
    @Test
    void testSubtract() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 4, 6);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that subtract is correct
        assertEquals(new Vector(1, 2, 3), p2.subtract(p1), ERROR_SUBTRACT);

        // =============== Boundary Values Tests ==================
        // BV01: Test that subtracting a point from itself throws an exception
        assertThrows(IllegalArgumentException.class, () -> p1.subtract(p1), ERROR_SUBTRACT_SAME);
    }

    /**
     * Test method for {@link primitives.Point#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        Point p1 = new Point(1, 2, 3);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that adding a vector to a point is correct
        assertEquals(new Point(2, 4, 6), p1.add(new Vector(1, 2, 3)), ERROR_ADD);

        // =============== Boundary Values Tests ==================
        // BV01: Test adding a vector that results in the center of coordinates
        assertEquals(Point.ZERO, p1.add(new Vector(-1, -2, -3)), ERROR_ADD_ZERO);
    }

    /**
     * Test method for {@link primitives.Point#distanceSquared(primitives.Point)}.
     */
    @Test
    void testDistanceSquared() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(2, 4, 5);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that distance squared is correct
        assertEquals(9d, p1.distanceSquared(p2), DELTA, ERROR_DISTANCE_SQUARED);

        // =============== Boundary Values Tests ==================
        // BV01: Test distance squared to itself
        assertEquals(0d, p1.distanceSquared(p1), DELTA, ERROR_DISTANCE_SQUARED_SAME);
    }

    /**
     * Test method for {@link primitives.Point#distance(primitives.Point)}.
     */
    @Test
    void testDistance() {
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(1, 2, 8);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that distance is correct
        assertEquals(5d, p1.distance(p2), DELTA, ERROR_DISTANCE);

        // =============== Boundary Values Tests ==================
        // BV01: Test distance to itself
        assertEquals(0d, p1.distance(p1), DELTA, ERROR_DISTANCE_SAME);
    }
}