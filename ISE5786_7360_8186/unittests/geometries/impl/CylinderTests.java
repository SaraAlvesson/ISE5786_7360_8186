package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link geometries.impl.Cylinder} class.
 */
class CylinderTests {
    private static final String ERROR_SIDE = "ERROR: getNormal() wrong result on side";
    private static final String ERROR_LOW_BASE = "ERROR: getNormal() wrong result on lower base";
    private static final String ERROR_UP_BASE = "ERROR: getNormal() wrong result on upper base";
    private static final String ERROR_BASE_CENTER = "ERROR: fails at base center";
    private static final String ERROR_BASE_EDGE = "ERROR: fails at base edge";
    private static final String ERROR_CONSTRUCTION = "ERROR: Failed to construct a valid cylinder";
    private static final String ERROR_FIND_INTERSECTIONS = "ERROR: findIntersections() wrong result";

    @Test
    void testConstructor() {
        Ray axis = new Ray(new Point(0, 0, 1), new Vector(0, 0, 1));
        assertDoesNotThrow(() -> new Cylinder(1.0, axis, 2.0), ERROR_CONSTRUCTION);
        assertDoesNotThrow(() -> new Cylinder(0.0, axis, 2.0), ERROR_CONSTRUCTION);
        assertDoesNotThrow(() -> new Cylinder(1.0, axis, 0.0), ERROR_CONSTRUCTION);
    }

    @Test
    void testGetNormal() {
        Ray axis = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Cylinder cylinder = new Cylinder(1.0, axis, 2.0);

        // EP: Side, Lower base, Upper base
        assertEquals(new Vector(1, 0, 0), cylinder.getNormal(new Point(1, 0, 1)), ERROR_SIDE);
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(0.5, 0, 0)), ERROR_LOW_BASE);
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(0.5, 0, 2)), ERROR_UP_BASE);

        // BVA: Centers and Edges
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(0, 0, 0)), ERROR_BASE_CENTER);
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(0, 0, 2)), ERROR_BASE_CENTER);
        assertEquals(new Vector(0, 0, -1), cylinder.getNormal(new Point(1, 0, 0)), ERROR_BASE_EDGE);
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(1, 0, 2)), ERROR_BASE_EDGE);
    }

    @Test
    void testFindIntersections() {
        Cylinder cylinder = new Cylinder(1.0, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), 2.0);
        List<Point> result;

        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray hits the cylinder side twice (2 points)
        result = cylinder.findIntersections(new Ray(new Point(2, 0, 1), new Vector(-1, 0, 0)));
        assertNotNull(result, ERROR_FIND_INTERSECTIONS);
        assertEquals(2, result.size(), ERROR_FIND_INTERSECTIONS);

        // EP02: Ray hits the cylinder at the joint point of base and side (1 point)
        result = cylinder.findIntersections(new Ray(new Point(0.5, 0, -1), new Vector(1, 0, 2)));
        assertNotNull(result, ERROR_FIND_INTERSECTIONS);
        assertEquals(1, result.size(), ERROR_FIND_INTERSECTIONS);

        // EP03: Ray hits both bases (2 points)
        result = cylinder.findIntersections(new Ray(new Point(0.5, 0, -1), new Vector(0, 0, 1)));
        assertNotNull(result, ERROR_FIND_INTERSECTIONS);
        assertEquals(2, result.size(), ERROR_FIND_INTERSECTIONS);

        // =============== Boundary Values Tests ==================

        // BV01: Ray is parallel to axis and outside (0 points)
        assertNull(
                cylinder.findIntersections(new Ray(new Point(2, 0, -1), new Vector(0, 0, 1))),
                "Parallel ray outside"
        );

        // BV02: Ray is tangent to the cylinder side (0 points)
        assertNull(
                cylinder.findIntersections(new Ray(new Point(1, 0, -1), new Vector(0, 0, 1))),
                "Tangent ray to side"
        );

        // BV03: Ray starts at the top base and goes out (0 points)
        assertNull(
                cylinder.findIntersections(new Ray(new Point(0.5, 0, 2), new Vector(0, 0, 1))),
                "Ray starts at top base heading out"
        );
    }
    /**
     * Test method for {@link geometries.impl.Cylinder#calcIntersections(primitives.Ray, double)}.
     * Checks maxDistance filtering for cylinder intersections.
     */
    @Test
    void testCalcIntersectionsMaxDistance() {
        Cylinder cylinder = new Cylinder(
                1.0,
                new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)),
                2.0
        );

        Ray sideRay = new Ray(new Point(2, 0, 1), new Vector(-1, 0, 0));

        // The side ray intersects the cylinder at:
        // P1 = (1,0,1), distance 1 from ray origin
        // P2 = (-1,0,1), distance 3 from ray origin

        // BV01: maxDistance is before the first side intersection -> 0 points
        assertNull(cylinder.calcIntersections(sideRay, 0.5),
                "Cylinder maxDistance: should not return side intersections before maxDistance");

        // BV02: maxDistance is exactly at the first side intersection -> 1 point
        var result1 = cylinder.calcIntersections(sideRay, 1);
        assertNotNull(result1, "Cylinder maxDistance: expected one side intersection");
        assertEquals(1, result1.size(), "Cylinder maxDistance: wrong number of side intersections");
        assertEquals(new Point(1, 0, 1), result1.get(0).point,
                "Cylinder maxDistance: wrong first side intersection point");

        // EP01: maxDistance is between the two side intersections -> 1 point
        var result2 = cylinder.calcIntersections(sideRay, 2);
        assertNotNull(result2, "Cylinder maxDistance: expected one side intersection");
        assertEquals(1, result2.size(), "Cylinder maxDistance: wrong number of side intersections");
        assertEquals(new Point(1, 0, 1), result2.get(0).point,
                "Cylinder maxDistance: wrong first side intersection point");

        // BV03: maxDistance is exactly at the second side intersection -> 2 points
        var result3 = cylinder.calcIntersections(sideRay, 3);
        assertNotNull(result3, "Cylinder maxDistance: expected two side intersections");
        assertEquals(2, result3.size(), "Cylinder maxDistance: wrong number of side intersections");
        assertEquals(new Point(1, 0, 1), result3.get(0).point,
                "Cylinder maxDistance: wrong first side intersection point");
        assertEquals(new Point(-1, 0, 1), result3.get(1).point,
                "Cylinder maxDistance: wrong second side intersection point");

        // EP02: maxDistance is after both side intersections -> 2 points
        var result4 = cylinder.calcIntersections(sideRay, 5);
        assertNotNull(result4, "Cylinder maxDistance: expected two side intersections");
        assertEquals(2, result4.size(), "Cylinder maxDistance: wrong number of side intersections");

        Ray baseRay = new Ray(new Point(0.5, 0, -1), new Vector(0, 0, 1));

        // The base ray intersects the finite cylinder bases at:
        // P1 = (0.5,0,0), distance 1
        // P2 = (0.5,0,2), distance 3

        // BV04: maxDistance is before the first base intersection -> 0 points
        assertNull(cylinder.calcIntersections(baseRay, 0.5),
                "Cylinder maxDistance: should not return base intersections before maxDistance");

        // BV05: maxDistance is exactly at the first base intersection -> 1 point
        var result5 = cylinder.calcIntersections(baseRay, 1);
        assertNotNull(result5, "Cylinder maxDistance: expected one base intersection");
        assertEquals(1, result5.size(), "Cylinder maxDistance: wrong number of base intersections");
        assertEquals(new Point(0.5, 0, 0), result5.get(0).point,
                "Cylinder maxDistance: wrong first base intersection point");

        // EP03: maxDistance is between the two base intersections -> 1 point
        var result6 = cylinder.calcIntersections(baseRay, 2);
        assertNotNull(result6, "Cylinder maxDistance: expected one base intersection");
        assertEquals(1, result6.size(), "Cylinder maxDistance: wrong number of base intersections");
        assertEquals(new Point(0.5, 0, 0), result6.get(0).point,
                "Cylinder maxDistance: wrong first base intersection point");

        // BV06: maxDistance is exactly at the second base intersection -> 2 points
        var result7 = cylinder.calcIntersections(baseRay, 3);
        assertNotNull(result7, "Cylinder maxDistance: expected two base intersections");
        assertEquals(2, result7.size(), "Cylinder maxDistance: wrong number of base intersections");
        assertEquals(new Point(0.5, 0, 0), result7.get(0).point,
                "Cylinder maxDistance: wrong first base intersection point");
        assertEquals(new Point(0.5, 0, 2), result7.get(1).point,
                "Cylinder maxDistance: wrong second base intersection point");
    }
}