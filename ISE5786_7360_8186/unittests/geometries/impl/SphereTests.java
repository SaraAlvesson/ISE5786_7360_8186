package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
     * Error message for valid sphere construction
     */
    private static final String ERROR_CONSTRUCTION = "ERROR: Failed to construct a valid sphere";

    /**
     * Test method for {@link geometries.impl.Sphere#Sphere(Point, double)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that a valid sphere is constructed correctly
        assertDoesNotThrow(() -> new Sphere(new Point(0, 0, 0), 1d), ERROR_CONSTRUCTION);

        // =============== Boundary Values Tests ==================
        // BV01: Test construction with radius 0
        assertDoesNotThrow(() -> new Sphere(new Point(0, 0, 0), 0d), ERROR_CONSTRUCTION);
    }

    /**
     * Test method for {@link geometries.impl.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1d);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test getNormal with a point on the sphere surface
        Vector result = sphere.getNormal(new Point(1, 0, 0));
        assertEquals(1d, result.length(), DELTA, ERROR_NORMAL_LENGTH);
        assertEquals(new Vector(1, 0, 0), result, ERROR_NORMAL_VALUE);

        // =============== Boundary Values Tests ==================
        // BV01: Test getNormal with a point on the sphere surface on a different axis
        assertEquals(new Vector(0, 1, 0), sphere.getNormal(new Point(0, 1, 0)), ERROR_NORMAL_VALUE);
    }
}