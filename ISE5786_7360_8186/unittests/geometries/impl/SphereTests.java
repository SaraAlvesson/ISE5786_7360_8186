package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link geometries.impl.Sphere} class.
 *
 * @author Sara
 */
class SphereTests {
    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for when the normal vector length is not 1
     */
    private static final String ERROR_NORMAL_LENGTH = "ERROR: Sphere normal length is not 1";

    /**
     * Error message for wrong normal calculation result
     */
    private static final String ERROR_NORMAL_VALUE = "ERROR: Sphere getNormal() wrong result";

    /**
     * Test method for {@link geometries.impl.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Test getNormal with a point on the sphere surface
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1d);
        Vector result = sphere.getNormal(new Point(1, 0, 0));

        // Ensure |n| = 1
        assertEquals(1d, result.length(), DELTA, ERROR_NORMAL_LENGTH);

        // Ensure the normal is correct (point - center)
        assertEquals(new Vector(1, 0, 0), result, ERROR_NORMAL_VALUE);
    }
}