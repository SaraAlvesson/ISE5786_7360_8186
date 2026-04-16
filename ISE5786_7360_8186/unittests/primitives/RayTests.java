package primitives;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link primitives.Ray} class.
 * @author Sara
 */
class RayTests {
    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;

    /** Error message for ray direction normalization */
    private static final String ERROR_DIRECTION_NORM = "ERROR: Ray direction vector must be normalized";

    /**
     * Test method for {@link primitives.Ray#Ray(Point, Vector)}.
     */
    @Test
    void testRay() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that the direction vector is normalized during construction
        Vector dir = new Vector(1, 1, 1);
        Ray ray = new Ray(new Point(1, 2, 3), dir);
        assertEquals(1d, ray.direction().length(), DELTA, ERROR_DIRECTION_NORM);
    }

    /**
     * Test method for {@link primitives.Ray#getPoint(double)}.
     */
    @Test
    void testGetPoint() {
        Ray ray = new Ray(new Point(1, 1, 1), new Vector(1, 0, 0));

        // ============ Equivalence Partitions Tests ==============
        // EP01: Positive distance t > 0
        assertEquals(new Point(2, 1, 1), ray.getPoint(1), "getPoint() wrong result for t > 0");

        // EP02: Another positive distance t > 0
        assertEquals(new Point(3.5, 1, 1), ray.getPoint(2.5), "getPoint() wrong result for t > 0");

        // =============== Boundary Values Tests ==================
        // BV01: Distance is zero t = 0
        assertEquals(new Point(1, 1, 1), ray.getPoint(0), "getPoint() should return the ray origin for t = 0");
    }
}