package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link geometries.impl.Tube} class.
 *
 * @author Sara
 */
class TubeTests {
    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for when the normal vector length is not 1
     */
    private static final String ERROR_NORMAL_LENGTH = "ERROR: Tube normal length is not 1";

    /**
     * Error message for wrong normal calculation result
     */
    private static final String ERROR_NORMAL_VALUE = "ERROR: getNormal() wrong result";

    /**
     * Error message for valid tube construction
     */
    private static final String ERROR_CONSTRUCTION = "ERROR: Failed to construct a valid tube";

    /**
     * Test method for {@link geometries.impl.Tube#Tube(double, primitives.Ray)}.
     */
    @Test
    void testConstructor() {
        Ray axis = new Ray(new Point(0, 0, 1), new Vector(0, 0, 1));

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that a valid tube is constructed correctly
        assertDoesNotThrow(() -> new Tube(1.0, axis), ERROR_CONSTRUCTION);

        // =============== Boundary Values Tests ==================
        // BV01: Test construction with radius 0
        assertDoesNotThrow(() -> new Tube(0.0, axis), ERROR_CONSTRUCTION);
    }

    /**
     * Test method for {@link geometries.impl.Tube#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Ray axis = new Ray(new Point(0, 0, 1), new Vector(0, 0, 1));
        Tube tube = new Tube(1.0, axis);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test getNormal with a point on the tube surface in front of the ray head
        Vector result1 = tube.getNormal(new Point(1, 0, 2));
        assertEquals(1d, result1.length(), DELTA, ERROR_NORMAL_LENGTH);
        assertEquals(new Vector(1, 0, 0), result1, ERROR_NORMAL_VALUE);

        // EP02: Test getNormal with a point on the tube surface "behind" the ray head
        // The point (1,0,0.5) is at Z=0.5 while the ray head is at Z=1
        Vector result2 = tube.getNormal(new Point(1, 0, 0.5));
        assertEquals(1d, result2.length(), DELTA, ERROR_NORMAL_LENGTH);
        assertEquals(new Vector(1, 0, 0), result2, ERROR_NORMAL_VALUE);

        // =============== Boundary Values Tests ==================
        // BV01: Test getNormal at a point that is at 90 degrees to the ray head (Z=1)
        Vector result3 = tube.getNormal(new Point(1, 0, 1));
        assertEquals(new Vector(1, 0, 0), result3, ERROR_NORMAL_VALUE);
    }
}