package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link geometries.impl.Plane} class.
 * @author Sara
 */
class PlaneTests {
    private final double DELTA = 1e-6;
    private final String ERROR_NORMAL = "ERROR: Plane getNormal() wrong result";
    private final String ERROR_CONSTRUCTION = "ERROR: Failed to construct a valid plane";
    private final String ERROR_POINTS = "ERROR: Identical or co-linear points should throw exception";
    private final String ERROR_FIND_INTERSECTIONS = "ERROR: Plane findIntersections() wrong result";

    /**
     * Test method for {@link geometries.impl.Plane#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Point p0 = new Point(0, 0, 1);
        Plane plane = new Plane(p0, new Vector(0, 0, 1));
        // ============ Equivalence Partitions Tests ==============
        // EP01: Point on the plane that is not the reference point
        assertEquals(new Vector(0, 0, 1), plane.getNormal(new Point(1, 0, 1)), ERROR_NORMAL);
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
        // BV01: Two points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(p1, p1, p3), ERROR_POINTS);
        // BV02: Three points are on the same line (co-linear)
        assertThrows(IllegalArgumentException.class, () ->
                        new Plane(new Point(1, 1, 1), new Point(2, 2, 2), new Point(3, 3, 3)),
                ERROR_POINTS);
    }

    /**
     * Test method for {@link geometries.impl.Plane#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Plane plane = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));

        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray intersects the plane (1 point)
        List<Point> result = plane.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 1, 1)));
        assertEquals(1, result.size(), ERROR_FIND_INTERSECTIONS);
        assertEquals(new Point(1, 1, 1), result.get(0), ERROR_FIND_INTERSECTIONS);

        // EP02: Ray does not intersect the plane (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(0, 0, 2), new Vector(1, 1, 1))),
                ERROR_FIND_INTERSECTIONS);

        // =============== Boundary Values Tests ==================

        // **** Group: Ray is parallel to the plane
        // BV01: Ray included in the plane (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 0, 0))),
                ERROR_FIND_INTERSECTIONS);
        // BV02: Ray not included in the plane (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 2), new Vector(1, 0, 0))),
                ERROR_FIND_INTERSECTIONS);

        // **** Group: Ray is orthogonal to the plane
        // BV03: Ray starts before the plane (1 point)
        result = plane.findIntersections(new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)));
        assertEquals(1, result.size(), ERROR_FIND_INTERSECTIONS);
        assertEquals(new Point(0, 0, 1), result.get(0), ERROR_FIND_INTERSECTIONS);
        // BV04: Ray starts in the plane (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 1), new Vector(0, 0, 1))),
                ERROR_FIND_INTERSECTIONS);
        // BV05: Ray starts after the plane (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(0, 0, 2), new Vector(0, 0, 1))),
                ERROR_FIND_INTERSECTIONS);

        // **** Group: Special cases
        // BV06: Ray is neither orthogonal nor parallel and begins at the plane (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 1, 1))),
                ERROR_FIND_INTERSECTIONS);
        // BV07: Ray begins at the reference point of the plane (Q) (0 points)
        assertNull(plane.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 1, 1))),
                ERROR_FIND_INTERSECTIONS);
    }
    /**
     * Test method for {@link geometries.impl.Plane#calcIntersections(primitives.Ray, double)}.
     * Checks maxDistance filtering for plane intersections.
     */
    @Test
    void testCalcIntersectionsMaxDistance() {
        Plane plane = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));

        // The ray intersects the plane at (0,0,1), distance 1 from ray origin.

        // BV01: maxDistance is before the intersection -> 0 points
        assertNull(plane.calcIntersections(ray, 0.5),
                "Plane maxDistance: should not return intersection beyond maxDistance");

        // BV02: maxDistance is exactly at the intersection -> 1 point
        var result1 = plane.calcIntersections(ray, 1);
        assertNotNull(result1, "Plane maxDistance: expected one intersection");
        assertEquals(1, result1.size(), "Plane maxDistance: wrong number of intersections");
        assertEquals(new Point(0, 0, 1), result1.get(0).point,
                "Plane maxDistance: wrong intersection point");

        // EP01: maxDistance is after the intersection -> 1 point
        var result2 = plane.calcIntersections(ray, 2);
        assertNotNull(result2, "Plane maxDistance: expected one intersection");
        assertEquals(1, result2.size(), "Plane maxDistance: wrong number of intersections");
        assertEquals(new Point(0, 0, 1), result2.get(0).point,
                "Plane maxDistance: wrong intersection point");

        // EP02: ray intersects the plane behind the ray origin -> 0 points
        assertNull(plane.calcIntersections(
                        new Ray(new Point(0, 0, 2), new Vector(0, 0, 1)), 10),
                "Plane maxDistance: should not return intersection behind ray origin");

        // BV03: ray starts on the plane -> 0 points
        assertNull(plane.calcIntersections(
                        new Ray(new Point(0, 0, 1), new Vector(0, 0, 1)), 10),
                "Plane maxDistance: ray starting on plane should not return intersection");

        // BV04: ray is parallel to the plane -> 0 points
        assertNull(plane.calcIntersections(
                        new Ray(new Point(0, 0, 0), new Vector(1, 0, 0)), 10),
                "Plane maxDistance: parallel ray should not return intersection");
    }
}