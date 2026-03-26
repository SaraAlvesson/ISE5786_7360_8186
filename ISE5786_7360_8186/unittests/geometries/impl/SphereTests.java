package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link geometries.impl.Sphere} class.
 * @author Sara
 */
class SphereTests {
    private final Point  p100 = new Point(1, 0, 0);
    private final Sphere sphere = new Sphere(p100, 1d);
    private final Point  p01  = new Point(-1, 0, 0);
    private final Vector v310 = new Vector(3, 1, 0);
    private final Vector v110 = new Vector(1, 1, 0);
    private final Point  inter1 = new Point(0.0651530771650466, 0.355051025721682, 0);
    private final Point  inter2 = new Point(1.53484692283495, 0.844948974278318, 0);
    private final List<Point> expected1 = List.of(inter1, inter2);

    private final String ERROR_SPHERE_INTERSECTION = "Wrong sphere intersection result";

    /**
     * Test method for {@link geometries.impl.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Test getNormal with a point on the sphere surface
        Point p = new Point(2, 0, 0);
        Vector result = sphere.getNormal(p);
        assertEquals(1d, result.length(), 1e-6, "Sphere normal length is not 1");
        assertEquals(new Vector(1, 0, 0), result, "Sphere getNormal() wrong result");
    }

    /**
     * Test method for {@link geometries.impl.Sphere#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray's line is outside the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(p01, v110)), ERROR_SPHERE_INTERSECTION);

        // EP02: Ray starts before and crosses the sphere (2 points)
        var result1 = sphere.findIntersections(new Ray(p01, v310));
        assertNotNull(result1, ERROR_SPHERE_INTERSECTION);
        assertEquals(2, result1.size(), ERROR_SPHERE_INTERSECTION);
        if (result1.get(0).getX() > result1.get(1).getX())
            result1 = List.of(result1.get(1), result1.get(0));
        assertEquals(expected1, result1, ERROR_SPHERE_INTERSECTION);

        // EP03: Ray starts inside the sphere (1 point)
        assertEquals(List.of(new Point(1.5, 0.8660254037844, 0)),
                sphere.findIntersections(new Ray(new Point(1, 0.5, 0), new Vector(1, 0.73205081, 0))),
                ERROR_SPHERE_INTERSECTION);

        // EP04: Ray starts after the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(3, 0, 0), new Vector(1, 0, 0))),
                ERROR_SPHERE_INTERSECTION);

        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray's line crosses the sphere (but not the center)
        // BV11: Ray starts at sphere and goes inside (1 points)
        assertEquals(List.of(new Point(2, 0, 0)),
                sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, -1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // BV12: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(2, 0, 0), new Vector(1, 0, 0))),
                ERROR_SPHERE_INTERSECTION);

        // **** Group 2: Ray's line goes through the center
        // BV21: Ray starts before the sphere (2 points)
        assertEquals(2, sphere.findIntersections(new Ray(new Point(1, -2, 0), new Vector(0, 1, 0))).size(),
                ERROR_SPHERE_INTERSECTION);

        // BV22: Ray starts at sphere and goes inside (1 points)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(1, -1, 0), new Vector(0, 1, 0))).size(),
                ERROR_SPHERE_INTERSECTION);

        // BV23: Ray starts inside (1 points)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(1, 0.5, 0), new Vector(0, 1, 0))).size(),
                ERROR_SPHERE_INTERSECTION);

        // BV24: Ray starts at the center (1 points)
        assertEquals(1, sphere.findIntersections(new Ray(p100, new Vector(0, 1, 0))).size(),
                ERROR_SPHERE_INTERSECTION);

        // BV25: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, 1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // BV26: Ray starts after sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1, 2, 0), new Vector(0, 1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // **** Group 3: Ray's line is tangent to the sphere (all tests 0 points)
        // BV31: Ray starts before the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(0, 1, 0), new Vector(1, 0, 0))),
                ERROR_SPHERE_INTERSECTION);

        // BV32: Ray starts at the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 0, 0))),
                ERROR_SPHERE_INTERSECTION);

        // BV33: Ray starts after the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(2, 1, 0), new Vector(1, 0, 0))),
                ERROR_SPHERE_INTERSECTION);

        // **** Group 4: Special cases
        // BV41: Ray's line is outside sphere, ray is orthogonal to ray start to sphere's center line
        assertNull(sphere.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(0, 1, 0))),
                ERROR_SPHERE_INTERSECTION);
    }
}