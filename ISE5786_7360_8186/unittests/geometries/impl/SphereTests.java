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

    // הנקודות המחושבות עבור EP02 - מסודרות לפי מרחק מראשית הקרן P01
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
        assertEquals(expected1, result1, ERROR_SPHERE_INTERSECTION);

        // EP03: Ray starts inside the sphere (1 point)
        var resultEP03 = sphere.findIntersections(new Ray(new Point(1, 0.5, 0), new Vector(0, 1, 0)));
        assertEquals(1, resultEP03.size(), ERROR_SPHERE_INTERSECTION);
        assertEquals(List.of(new Point(1, 1, 0)), resultEP03, ERROR_SPHERE_INTERSECTION);

        // EP04: Ray starts after the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(3, 0, 0), new Vector(1, 0, 0))),
                ERROR_SPHERE_INTERSECTION);

        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray's line crosses the sphere, but not the center

        // BV11: Ray starts at sphere and goes inside (1 point)
        assertEquals(List.of(new Point(1, -1, 0)),
                sphere.findIntersections(new Ray(new Point(2, 0, 0), new Vector(-1, -1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // BV12: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(2, 0, 0), new Vector(1, 1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // **** Group 2: Ray's line goes through the center

        // BV21: Ray starts before the sphere (2 points)
        var resultBV21 = sphere.findIntersections(new Ray(new Point(1, -2, 0), new Vector(0, 1, 0)));
        assertEquals(2, resultBV21.size(), ERROR_SPHERE_INTERSECTION);
        assertEquals(List.of(new Point(1, -1, 0), new Point(1, 1, 0)), resultBV21, ERROR_SPHERE_INTERSECTION);

        // BV22: Ray starts at sphere and goes inside (1 point)
        assertEquals(List.of(new Point(1, 1, 0)),
                sphere.findIntersections(new Ray(new Point(1, -1, 0), new Vector(0, 1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // BV23: Ray starts inside (1 point)
        assertEquals(List.of(new Point(1, 1, 0)),
                sphere.findIntersections(new Ray(new Point(1, 0.5, 0), new Vector(0, 1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // BV24: Ray starts at the center (1 point)
        assertEquals(List.of(new Point(1, 1, 0)),
                sphere.findIntersections(new Ray(p100, new Vector(0, 1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // BV25: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, 1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // BV26: Ray starts after sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1, 2, 0), new Vector(0, 1, 0))),
                ERROR_SPHERE_INTERSECTION);

        // **** Group 3: Ray's line is tangent to the sphere

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

        // BV42: Ray starts inside, ray is orthogonal to ray start to sphere's center line
        var resultBV42 = sphere.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(0, 1, 0)));
        assertNotNull(resultBV42, ERROR_SPHERE_INTERSECTION);
        assertEquals(1, resultBV42.size(), ERROR_SPHERE_INTERSECTION);
    }

    /**
     * Test method for {@link geometries.impl.Sphere#calcIntersections(primitives.Ray, double)}.
     * Checks maxDistance filtering for sphere intersections.
     */
    @Test
    void testCalcIntersectionsMaxDistance() {
        Sphere sphere = new Sphere(new Point(1, 0, 0), 1d);
        Ray ray = new Ray(new Point(-1, 0, 0), new Vector(1, 0, 0));

        // The ray intersects the sphere at:
        // P1 = (0,0,0), distance 1 from ray origin
        // P2 = (2,0,0), distance 3 from ray origin

        // BV01: maxDistance is before the first intersection -> 0 points
        assertNull(sphere.calcIntersections(ray, 0.5),
                "Sphere maxDistance: should not return intersections before maxDistance");

        // BV02: maxDistance is exactly at the first intersection -> 1 point
        var result1 = sphere.calcIntersections(ray, 1);
        assertNotNull(result1, "Sphere maxDistance: expected one intersection");
        assertEquals(1, result1.size(), "Sphere maxDistance: wrong number of intersections");
        assertEquals(new Point(0, 0, 0), result1.get(0).point,
                "Sphere maxDistance: wrong first intersection point");

        // EP01: maxDistance is between the two intersections -> 1 point
        var result2 = sphere.calcIntersections(ray, 2);
        assertNotNull(result2, "Sphere maxDistance: expected one intersection");
        assertEquals(1, result2.size(), "Sphere maxDistance: wrong number of intersections");
        assertEquals(new Point(0, 0, 0), result2.get(0).point,
                "Sphere maxDistance: wrong first intersection point");

        // BV03: maxDistance is exactly at the second intersection -> 2 points
        var result3 = sphere.calcIntersections(ray, 3);
        assertNotNull(result3, "Sphere maxDistance: expected two intersections");
        assertEquals(2, result3.size(), "Sphere maxDistance: wrong number of intersections");
        assertEquals(new Point(0, 0, 0), result3.get(0).point,
                "Sphere maxDistance: wrong first intersection point");
        assertEquals(new Point(2, 0, 0), result3.get(1).point,
                "Sphere maxDistance: wrong second intersection point");

        // EP02: maxDistance is after both intersections -> 2 points
        var result4 = sphere.calcIntersections(ray, 5);
        assertNotNull(result4, "Sphere maxDistance: expected two intersections");
        assertEquals(2, result4.size(), "Sphere maxDistance: wrong number of intersections");
        assertEquals(new Point(0, 0, 0), result4.get(0).point,
                "Sphere maxDistance: wrong first intersection point");
        assertEquals(new Point(2, 0, 0), result4.get(1).point,
                "Sphere maxDistance: wrong second intersection point");

        // BV04: ray starts at sphere center, maxDistance before radius -> 0 points
        assertNull(sphere.calcIntersections(new Ray(new Point(1, 0, 0), new Vector(1, 0, 0)), 0.5),
                "Sphere maxDistance: center ray should not intersect before radius");

        // BV05: ray starts at sphere center, maxDistance exactly radius -> 1 point
        var result5 = sphere.calcIntersections(new Ray(new Point(1, 0, 0), new Vector(1, 0, 0)), 1);
        assertNotNull(result5, "Sphere maxDistance: expected one intersection from center");
        assertEquals(1, result5.size(), "Sphere maxDistance: wrong number of intersections from center");
        assertEquals(new Point(2, 0, 0), result5.get(0).point,
                "Sphere maxDistance: wrong intersection point from center");
    }
}