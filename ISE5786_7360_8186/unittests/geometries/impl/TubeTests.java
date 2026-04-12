package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
     * Error message for wrong findIntersections result
     */
    private static final String ERROR_FIND_INTERSECTIONS = "ERROR: findIntersections() wrong result";

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
        Vector result2 = tube.getNormal(new Point(1, 0, 0.5));
        assertEquals(1d, result2.length(), DELTA, ERROR_NORMAL_LENGTH);
        assertEquals(new Vector(1, 0, 0), result2, ERROR_NORMAL_VALUE);

        // =============== Boundary Values Tests ==================
        // BV01: Test getNormal at a point that is at 90 degrees to the ray head (Z=1)
        Vector result3 = tube.getNormal(new Point(1, 0, 1));
        assertEquals(new Vector(1, 0, 0), result3, ERROR_NORMAL_VALUE);
    }

    /**
     * Test method for {@link geometries.impl.Tube#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Tube tube = new Tube(1.0, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)));
        List<Point> result;

        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray intersects the tube twice (2 points)
        result = tube.findIntersections(new Ray(new Point(2, 0, 1), new Vector(-1, 0, 0)));
        assertNotNull(result, ERROR_FIND_INTERSECTIONS);
        assertEquals(2, result.size(), ERROR_FIND_INTERSECTIONS);

        // EP02: Ray starts inside the tube (1 point)
        result = tube.findIntersections(new Ray(new Point(0.5, 0, 1), new Vector(1, 0, 0)));
        assertNotNull(result, ERROR_FIND_INTERSECTIONS);
        assertEquals(1, result.size(), ERROR_FIND_INTERSECTIONS);

        // EP03: Ray starts after the tube (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, 0, 1), new Vector(1, 0, 0))),
                ERROR_FIND_INTERSECTIONS);

        // =============== Boundary Values Tests ==================

        // **** Group: Ray is parallel to the axis
        // BV01: Ray is parallel and inside the tube (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(0.5, 0, 1), new Vector(0, 0, 1))),
                "Parallel ray inside tube should return null");

        // BV02: Ray is parallel and outside the tube (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, 0, 1), new Vector(0, 0, 1))),
                "Parallel ray outside tube should return null");

        // **** Group: Ray starts on the axis
        // BV03: Ray starts on the axis and goes through the surface (1 point)
        result = tube.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 0, 0)));
        assertNotNull(result, ERROR_FIND_INTERSECTIONS);
        assertEquals(1, result.size(), "Ray starting on axis should have 1 intersection");

        // **** Group: Special cases
        // BV04: Ray is tangent to the tube (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1, 0, 1), new Vector(0, 1, 0))),
                "Tangent ray should return null");

        // BV05: Ray starts on the surface and goes outside (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1, 0, 1), new Vector(1, 0, 0))),
                "Ray starting on surface heading out should return null");

        // BV06: Ray starts on the surface and goes inside (1 point)
        result = tube.findIntersections(new Ray(new Point(1, 0, 1), new Vector(-1, 0, 0)));
        assertNotNull(result, ERROR_FIND_INTERSECTIONS);
        assertEquals(1, result.size(), "Ray starting on surface heading in should have 1 intersection");
    }
}