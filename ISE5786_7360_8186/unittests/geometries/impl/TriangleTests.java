package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link geometries.impl.Triangle} class.
 * @author Sara
 */
class TriangleTests {
    private final double DELTA = 1e-6;
    private final String ERROR_FIND_INTERSECTIONS = "ERROR: Triangle findIntersections() wrong result";

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
        assertEquals(1d, result.length(), DELTA, "Triangle normal length is not 1");

        // Ensure the normal is orthogonal to one of the edges
        Vector v1 = p2.subtract(p1);
        assertEquals(0d, result.dotProduct(v1), DELTA, "getNormal() result is not orthogonal to the edge");
    }

    /**
     * Test method for {@link geometries.impl.Triangle#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Triangle tr = new Triangle(new Point(0, 0, 1), new Point(2, 0, 1), new Point(0, 2, 1));
        Ray ray;

        // ============ Equivalence Partitions Tests ==============

        // EP01: Inside triangle (1 point)
        ray = new Ray(new Point(0.5, 0.5, 0), new Vector(0, 0, 1));
        List<Point> result = tr.findIntersections(ray);
        assertNotNull(result, ERROR_FIND_INTERSECTIONS);
        assertEquals(1, result.size(), ERROR_FIND_INTERSECTIONS);
        assertEquals(new Point(0.5, 0.5, 1), result.get(0), ERROR_FIND_INTERSECTIONS);

        // EP02: Outside against edge (0 points)
        ray = new Ray(new Point(1, -1, 0), new Vector(0, 0, 1));
        assertNull(tr.findIntersections(ray), ERROR_FIND_INTERSECTIONS);

        // EP03: Outside against vertex (0 points)
        ray = new Ray(new Point(-1, -1, 0), new Vector(0, 0, 1));
        assertNull(tr.findIntersections(ray), ERROR_FIND_INTERSECTIONS);

        // =============== Boundary Values Tests ==================

        // BV01: On edge (0 points)
        ray = new Ray(new Point(1, 0, 0), new Vector(0, 0, 1));
        assertNull(tr.findIntersections(ray), "Point on edge should return null");

        // BV02: In vertex (0 points)
        ray = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        assertNull(tr.findIntersections(ray), "Point in vertex should return null");

        // BV03: On edge continuation (0 points)
        ray = new Ray(new Point(3, 0, 0), new Vector(0, 0, 1));
        assertNull(tr.findIntersections(ray), "Point on edge continuation should return null");

        // **** Group: Plane-based cases
        // BV04: Ray parallel to triangle's plane (0 points)
        assertNull(tr.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 0, 0))),
                "Parallel ray should return null");
    }
    /**
     * Test method for {@link geometries.impl.Triangle#calcIntersections(primitives.Ray, double)}.
     * Checks maxDistance filtering for triangle intersections.
     */
    @Test
    void testCalcIntersectionsMaxDistance() {
        Triangle triangle = new Triangle(
                new Point(0, 0, 1),
                new Point(2, 0, 1),
                new Point(0, 2, 1)
        );

        Ray ray = new Ray(new Point(0.5, 0.5, 0), new Vector(0, 0, 1));

        // The ray intersects the triangle at (0.5, 0.5, 1),
        // distance 1 from ray origin.

        // BV01: maxDistance is before the intersection -> 0 points
        assertNull(triangle.calcIntersections(ray, 0.5),
                "Triangle maxDistance: should not return intersection beyond maxDistance");

        // BV02: maxDistance is exactly at the intersection -> 1 point
        var result1 = triangle.calcIntersections(ray, 1);
        assertNotNull(result1, "Triangle maxDistance: expected one intersection");
        assertEquals(1, result1.size(), "Triangle maxDistance: wrong number of intersections");
        assertEquals(new Point(0.5, 0.5, 1), result1.get(0).point,
                "Triangle maxDistance: wrong intersection point");

        // EP01: maxDistance is after the intersection -> 1 point
        var result2 = triangle.calcIntersections(ray, 2);
        assertNotNull(result2, "Triangle maxDistance: expected one intersection");
        assertEquals(1, result2.size(), "Triangle maxDistance: wrong number of intersections");
        assertEquals(new Point(0.5, 0.5, 1), result2.get(0).point,
                "Triangle maxDistance: wrong intersection point");

        // EP02: intersection with the plane but outside the triangle -> 0 points
        assertNull(triangle.calcIntersections(
                        new Ray(new Point(1.5, 1.5, 0), new Vector(0, 0, 1)), 2),
                "Triangle maxDistance: point outside triangle should return null");

        // BV03: intersection point is on triangle edge -> 0 points
        assertNull(triangle.calcIntersections(
                        new Ray(new Point(1, 0, 0), new Vector(0, 0, 1)), 2),
                "Triangle maxDistance: point on edge should return null");

        // BV04: ray starts on the triangle plane -> 0 points
        assertNull(triangle.calcIntersections(
                        new Ray(new Point(0.5, 0.5, 1), new Vector(0, 0, 1)), 2),
                "Triangle maxDistance: ray starting on triangle plane should return null");
    }
}