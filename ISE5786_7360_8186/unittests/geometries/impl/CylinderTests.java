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
}