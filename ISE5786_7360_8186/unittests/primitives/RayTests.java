package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link primitives.Ray} class.
 *
 */
class RayTests {
    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for ray direction normalization
     */
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
}