package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link geometries.impl.Plane} class.
 *
 * @author Sara
 */
class PlaneTests {
    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for normal calculation logic
     */
    private static final String ERROR_NORMAL = "ERROR: Plane getNormal() wrong result";

    /**
     * Error message for valid plane construction
     */
    private static final String ERROR_CONSTRUCTION = "ERROR: Failed to construct a valid plane";

    /**
     * Error message for identical or co-linear points in constructor
     */
    private static final String ERROR_POINTS = "ERROR: Identical or co-linear points should throw exception";

    /**
     * Test method for {@link geometries.impl.Plane#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Point p0 = new Point(0, 0, 1);
        Plane plane = new Plane(p0, new Vector(0, 0, 1));

        // ============ Equivalence Partitions Tests ==============
        // EP01: Point on the plane that is not the reference point
        Vector result = plane.getNormal(new Point(1, 0, 1));
        assertEquals(1d, result.length(), DELTA, ERROR_NORMAL);
        assertEquals(new Vector(0, 0, 1), result, ERROR_NORMAL);

        // =============== Boundary Values Tests ==================
        // BV01: Point is the reference point (p0)
        assertEquals(new Vector(0, 0, 1), plane.getNormal(p0), ERROR_NORMAL);
    }

    /**
     * Test method for {@link geometries.impl.Plane#Plane(primitives.Point, primitives.Vector)}.
     */
    @Test
    void testPlanePointVector() {
        Point p = new Point(0, 0, 1);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that the normal is normalized during construction
        assertDoesNotThrow(() -> {
            Plane plane = new Plane(p, new Vector(0, 0, 2));
            assertEquals(1d, plane.getNormal(p).length(), DELTA, ERROR_NORMAL);
        }, ERROR_CONSTRUCTION);

        // =============== Boundary Values Tests ==================
        // BV01: Test construction with an already normalized vector
        assertDoesNotThrow(() -> new Plane(p, new Vector(0, 0, 1)), ERROR_CONSTRUCTION);
    }

    /**
     * Test method for {@link geometries.impl.Plane#Plane(primitives.Point, primitives.Point, primitives.Point)}.
     */
    @Test
    void testPlanePointPointPoint() {
        Point p1 = new Point(1, 0, 0);
        Point p2 = new Point(0, 1, 0);
        Point p3 = new Point(0, 0, 1);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Three non-collinear points
        assertDoesNotThrow(() -> new Plane(p1, p2, p3), ERROR_CONSTRUCTION);

        // =============== Boundary Values Tests ==================
        // BV01: Two points are the same (1/2)
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p1, p3), ERROR_POINTS);

        // BV02: Two points are the same (1/3)
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p2, p1), ERROR_POINTS);

        // BV03: Two points are the same (2/3)
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p2, p2), ERROR_POINTS);

        // BV04: Three points are on the same line (co-linear)
        assertThrows(IllegalArgumentException.class, () ->
                        new Plane(new Point(1, 1, 1), new Point(2, 2, 2), new Point(3, 3, 3)),
                ERROR_POINTS);

        // BV05: Three points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p1, p1), ERROR_POINTS);
    }
}