package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link geometries.impl.Cylinder} class.
 *
 * @author Sara
 */
class CylinderTests {
    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for wrong normal result on the side surface
     */
    private static final String ERROR_SIDE = "ERROR: getNormal() wrong result on side";

    /**
     * Error message for wrong normal result on the lower base
     */
    private static final String ERROR_LOW_BASE = "ERROR: getNormal() wrong result on lower base";

    /**
     * Error message for wrong normal result on the upper base
     */
    private static final String ERROR_UP_BASE = "ERROR: getNormal() wrong result on upper base";

    /**
     * Error message for wrong normal result at the center of the base
     */
    private static final String ERROR_BASE_CENTER = "ERROR: fails at base center";

    /**
     * Error message for wrong normal result at the edge of the base
     */
    private static final String ERROR_BASE_EDGE = "ERROR: fails at base edge";

    /**
     * Error message for valid cylinder construction
     */
    private static final String ERROR_CONSTRUCTION = "ERROR: Failed to construct a valid cylinder";

    /**
     * Test method for {@link geometries.impl.Cylinder#Cylinder(double, primitives.Ray, double)}.
     */
    @Test
    void testConstructor() {
        Ray axis = new Ray(new Point(0, 0, 1), new Vector(0, 0, 1));

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that a valid cylinder is constructed correctly
        assertDoesNotThrow(() -> new Cylinder(1.0, axis, 2.0), ERROR_CONSTRUCTION);

        // =============== Boundary Values Tests ==================
        // BV01: Test construction with radius 0
        assertDoesNotThrow(() -> new Cylinder(0.0, axis, 2.0), ERROR_CONSTRUCTION);

        // BV02: Test construction with height 0
        assertDoesNotThrow(() -> new Cylinder(1.0, axis, 0.0), ERROR_CONSTRUCTION);
    }

    /**
     * Test method for {@link geometries.impl.Cylinder#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Ray axis = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Cylinder cylinder = new Cylinder(1.0, axis, 2.0);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test getNormal with a point on the round surface (side)
        Vector resSide = cylinder.getNormal(new Point(1, 0, 1));
        assertEquals(new Vector(1, 0, 0), resSide, ERROR_SIDE);

        // EP02: Test getNormal with a point on the lower base
        Vector resLow = cylinder.getNormal(new Point(0.5, 0, 0));
        assertEquals(new Vector(0, 0, -1), resLow, ERROR_LOW_BASE);

        // EP03: Test getNormal with a point on the upper base
        Vector resUp = cylinder.getNormal(new Point(0.5, 0, 2));
        assertEquals(new Vector(0, 0, 1), resUp, ERROR_UP_BASE);

        // =============== Boundary Values Tests ==================
        // BV01: Test getNormal at the center of the lower base
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(0, 0, 0)), ERROR_BASE_CENTER);

        // BV02: Test getNormal at the center of the upper base
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(0, 0, 2)), ERROR_BASE_CENTER);

        // BV03: Test getNormal at the edge of the lower base
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(1, 0, 0)), ERROR_BASE_EDGE);

        // BV04: Test getNormal at the edge of the upper base
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(1, 0, 2)), ERROR_BASE_EDGE);
    }
}