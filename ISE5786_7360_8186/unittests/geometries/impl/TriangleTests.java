package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
     * Test method for {@link geometries.impl.Triangle#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Test getNormal with a point inside the triangle
        Point p1 = new Point(1, 0, 0);
        Point p2 = new Point(0, 1, 0);
        Point p3 = new Point(0, 0, 1);
        Triangle triangle = new Triangle(p1, p2, p3);

        // Point (0.33, 0.33, 0.33) is approximately on the triangle surface
        Vector result = triangle.getNormal(new Point(0.33, 0.33, 0.33));

        // Ensure |n| = 1
        assertEquals(1d, result.length(), DELTA, ERROR_NORMAL_LENGTH);

        // Ensure the normal is orthogonal to at least one edge (p2-p1)
        Vector v1 = p2.subtract(p1);
        assertEquals(0d, result.dotProduct(v1), DELTA, ERROR_NORMAL_ORTHOGONAL);
    }
}