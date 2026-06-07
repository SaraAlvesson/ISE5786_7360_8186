package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link Polygon}.
 * The tests verify:
 * <ul>
 * <li>Polygon constructor validity</li>
 * <li>{@link Polygon#getNormal(Point)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class PolygonTests {
    /**
     * Default constructor to satisfy JavaDoc generator
     */
    PolygonTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Vertex (1,0,0) used in polygon tests
     */
    private static final Point POINT_X = new Point(1, 0, 0);
    /**
     * Vertex (0,1,0) used in polygon tests
     */
    private static final Point POINT_Y = new Point(0, 1, 0);
    /**
     * Vertex (0,0,1) used in polygon tests
     */
    private static final Point POINT_Z = new Point(0, 0, 1);

    /**
     * Additional vertex used for valid polygon construction
     */
    private static final Point POINT1 = new Point(-1, 1, 1);
    /**
     * Point not in the polygon plane
     */
    private static final Point POINT2 = new Point(0, 2, 2);
    /**
     * Point that creates a concave polygon
     */
    private static final Point POINT3 = new Point(0.5, 0.25, 0.5);
    /**
     * Point located on one of the polygon edges
     */
    private static final Point POINT4 = new Point(0, 0.5, 0.5);

    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for wrong polygon intersection
     */
    private static final String ERROR_POLYGON = "ERROR: wrong polygon intersection";

    /**
     * Test method for {@link Polygon#Polygon(Point...)}.
     * Verifies correct and incorrect polygon constructions.
     */
    @Test
    void testConstructor() {

        // ============ Equivalence Partitions Tests ==============

        // TC01: Correct convex quadrilateral with vertices in correct order
        assertDoesNotThrow(() -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT1),
                "Failed constructing a correct polygon");

        // TC02: Wrong vertices order
        assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_Y, POINT_X, POINT1),
                "Constructed a polygon with wrong order of vertices");

        // TC03: Vertices not in the same plane
        assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT2),
                "Constructed a polygon with vertices that are not in the same plane");

        // TC04: Concave quadrilateral
        assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT3),
                "Constructed a concave polygon");

        // =============== Boundary Values Tests ==================

        // TC11: Vertex on a side
        assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT4),
                "Constructed a polygon with a vertex on a side");

        // TC12: Last point equals first point
        assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT_Z),
                "Constructed a polygon with duplicate first/last vertex");

        // TC13: Co-located points
        assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT_Y),
                "Constructed a polygon with co-located vertices");
    }

    /**
     * Test method for {@link Polygon#getNormal(Point)}.
     * Verifies that the returned normal vector is unit length and orthogonal
     * to all polygon edges.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        Point[] pts =
                {POINT_Z, POINT_X, POINT_Y, POINT1};
        Polygon polygon = new Polygon(pts);
        // Ensure method does not throw exception
        assertDoesNotThrow(() -> polygon.getNormal(POINT_Z), "getNormal() threw unexpected exception");
        Vector result = polygon.getNormal(POINT_Z);
        // Ensure |n| = 1
        assertEquals(1, result.length(), DELTA, "Polygon normal is not a unit vector");
        // Ensure normal is orthogonal to all edges
        for (int i = 0; i < pts.length; ++i) {
            Vector edge = pts[i].subtract(pts[i == 0 ? pts.length - 1 : i - 1]);
            assertEquals(0d, result.dotProduct(edge), DELTA, "Polygon normal is not orthogonal to an edge");
        }
    }

    /**
     * Test method for {@link geometries.impl.Polygon#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Polygon poly = new Polygon(
                new Point(0, 0, 1),
                new Point(2, 0, 1),
                new Point(2, 2, 1),
                new Point(0, 2, 1)
        );
        Ray ray;

        // ============ Equivalence Partitions Tests ==============

        // EP01: Inside polygon (1 point)
        ray = new Ray(new Point(1, 1, 0), new Vector(0, 0, 1));
        List<Point> result = poly.findIntersections(ray);
        assertNotNull(result, ERROR_POLYGON);
        assertEquals(1, result.size(), ERROR_POLYGON);
        assertEquals(new Point(1, 1, 1), result.get(0), ERROR_POLYGON);

        // EP02: Outside against edge (0 points)
        ray = new Ray(new Point(3, 1, 0), new Vector(0, 0, 1));
        assertNull(poly.findIntersections(ray), ERROR_POLYGON);

        // EP03: Outside against vertex (0 points)
        ray = new Ray(new Point(-1, -1, 0), new Vector(0, 0, 1));
        assertNull(poly.findIntersections(ray), ERROR_POLYGON);

        // =============== Boundary Values Tests ==================

        // BV01: On edge (0 points)
        ray = new Ray(new Point(1, 0, 0), new Vector(0, 0, 1));
        assertNull(poly.findIntersections(ray), "Point on edge should return null");

        // BV02: In vertex (0 points)
        ray = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        assertNull(poly.findIntersections(ray), "Point in vertex should return null");

        // BV03: On edge continuation (0 points)
        ray = new Ray(new Point(3, 0, 0), new Vector(0, 0, 1));
        assertNull(poly.findIntersections(ray), "Point on edge continuation should return null");
    }
    /**
     * Test method for {@link geometries.impl.Polygon#calcIntersections(primitives.Ray, double)}.
     * Checks maxDistance filtering for polygon intersections.
     */
    @Test
    void testCalcIntersectionsMaxDistance() {
        Polygon polygon = new Polygon(
                new Point(0, 0, 1),
                new Point(2, 0, 1),
                new Point(2, 2, 1),
                new Point(0, 2, 1)
        );

        Ray ray = new Ray(new Point(1, 1, 0), new Vector(0, 0, 1));

        // The ray intersects the polygon at (1,1,1),
        // distance 1 from ray origin.

        // BV01: maxDistance is before the intersection -> 0 points
        assertNull(polygon.calcIntersections(ray, 0.5),
                "Polygon maxDistance: should not return intersection beyond maxDistance");

        // BV02: maxDistance is exactly at the intersection -> 1 point
        var result1 = polygon.calcIntersections(ray, 1);
        assertNotNull(result1, "Polygon maxDistance: expected one intersection");
        assertEquals(1, result1.size(), "Polygon maxDistance: wrong number of intersections");
        assertEquals(new Point(1, 1, 1), result1.get(0).point,
                "Polygon maxDistance: wrong intersection point");

        // EP01: maxDistance is after the intersection -> 1 point
        var result2 = polygon.calcIntersections(ray, 2);
        assertNotNull(result2, "Polygon maxDistance: expected one intersection");
        assertEquals(1, result2.size(), "Polygon maxDistance: wrong number of intersections");
        assertEquals(new Point(1, 1, 1), result2.get(0).point,
                "Polygon maxDistance: wrong intersection point");

        // EP02: intersection with the plane but outside the polygon -> 0 points
        assertNull(polygon.calcIntersections(
                        new Ray(new Point(3, 1, 0), new Vector(0, 0, 1)), 2),
                "Polygon maxDistance: point outside polygon should return null");

        // BV03: intersection point is on polygon edge -> 0 points
        assertNull(polygon.calcIntersections(
                        new Ray(new Point(1, 0, 0), new Vector(0, 0, 1)), 2),
                "Polygon maxDistance: point on edge should return null");

        // BV04: ray starts on the polygon plane -> 0 points
        assertNull(polygon.calcIntersections(
                        new Ray(new Point(1, 1, 1), new Vector(0, 0, 1)), 2),
                "Polygon maxDistance: ray starting on polygon plane should return null");
    }
}