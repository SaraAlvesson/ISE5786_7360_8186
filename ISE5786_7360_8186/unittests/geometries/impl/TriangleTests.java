package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link geometries.impl.Triangle} class.
 *
 * @author Sara
 */
class TriangleTests {
    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for when the normal vector length is not 1
     */
    private static final String ERROR_NORMAL_LENGTH = "ERROR: Triangle normal length is not 1";

    /**
     * Error message for when the normal is not orthogonal to the triangle's edges
     */
    private static final String ERROR_NORMAL_ORTHOGONAL = "ERROR: getNormal() result is not orthogonal to the edge";

    /**
     * Error message for valid triangle construction
     */
    private static final String ERROR_CONSTRUCTION = "ERROR: Failed to construct a valid triangle";

    /**
     * Error message for identical or co-linear points in constructor
     */
    private static final String ERROR_POINTS = "ERROR: Identical or co-linear points should throw exception";

    /**
     * Test method for {@link geometries.impl.Triangle#Triangle(Point, Point, Point)}.
     */
    @Test
    void testConstructor() {
        Point p1 = new Point(1, 0, 0);
        Point p2 = new Point(0, 1, 0);
        Point p3 = new Point(0, 0, 1);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that a valid triangle is constructed correctly
        assertDoesNotThrow(() -> new Triangle(p1, p2, p3), ERROR_CONSTRUCTION);

        // =============== Boundary Values Tests ==================
        // BV01: Test construction with two identical points
        assertThrows(IllegalArgumentException.class, () -> new Triangle(p1, p1, p3), ERROR_POINTS);

        // BV02: Test construction with three collinear points
        assertThrows(IllegalArgumentException.class, () ->
                        new Triangle(new Point(1, 1, 1), new Point(2, 2, 2), new Point(3, 3, 3)),
                ERROR_POINTS);
    }

    /**
     * Test method for {@link geometries.impl.Triangle#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Point p1 = new Point(1, 0, 0);
        Point p2 = new Point(0, 1, 0);
        Point p3 = new Point(0, 0, 1);
        Triangle triangle = new Triangle(p1, p2, p3);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Test getNormal with a point inside the triangle
        Vector result = triangle.getNormal(new Point(0.33, 0.33, 0.33));
        assertEquals(1d, result.length(), DELTA, ERROR_NORMAL_LENGTH);
        Vector v1 = p2.subtract(p1);
        assertEquals(0d, result.dotProduct(v1), DELTA, ERROR_NORMAL_ORTHOGONAL);

        // =============== Boundary Values Tests ==================
        // BV01: Test getNormal with one of the vertices
        assertDoesNotThrow(() -> {
            Vector resVertex = triangle.getNormal(p1);
            assertEquals(1d, resVertex.length(), DELTA, ERROR_NORMAL_LENGTH);
        }, ERROR_CONSTRUCTION);
    }
}