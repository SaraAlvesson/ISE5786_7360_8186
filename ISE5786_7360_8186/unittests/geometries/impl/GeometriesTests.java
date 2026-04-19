package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

class GeometriesTests {
    @Test
    void testFindIntersections() {
        Sphere sphere = new Sphere(new Point(1, 0, 0), 1d);

        Triangle triangle = new Triangle(
                new Point(1, 0, 0),
                new Point(2, 0, 0),
                new Point(1.5, 1, 0)
        );

        Plane plane = new Plane(new Point(0, 0, 5), new Vector(0, 0, 1));

        Geometries geometries = new Geometries(sphere, triangle, plane);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Some geometries intersect (sphere + plane = 3 points)
        Ray raySome = new Ray(new Point(1, 0, -2), new Vector(0, 0, 1));
        var resultSome = geometries.findIntersections(raySome);
        assertNotNull(resultSome, "Should have intersections");
        assertEquals(3, resultSome.size(), "Some geometries should intersect");

        // =============== Boundary Values Tests ==================

        // BV01: Empty collection
        assertNull(
                new Geometries().findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 1, 1))),
                "Empty collection should return null"
        );

        // BV02: No geometry intersects
        assertNull(
                geometries.findIntersections(new Ray(new Point(10, 10, 10), new Vector(1, 1, 1))),
                "No intersections should return null"
        );

        // BV03: Only one geometry intersects (only plane)
        Ray rayOne = new Ray(new Point(5, 5, 0), new Vector(0, 0, 1));
        var resultOne = geometries.findIntersections(rayOne);
        assertNotNull(resultOne, "Should have one intersection");
        assertEquals(1, resultOne.size(), "Only one geometry should intersect");

        // BV04: All geometries intersect (sphere + triangle + plane = 4 points)
        Ray rayAll = new Ray(new Point(1.5, 0.5, -1), new Vector(0, 0, 1));
        var resultAll = geometries.findIntersections(rayAll);
        assertNotNull(resultAll, "Should have intersections with all geometries");
        assertEquals(4, resultAll.size(), "All geometries should intersect");
    }
}