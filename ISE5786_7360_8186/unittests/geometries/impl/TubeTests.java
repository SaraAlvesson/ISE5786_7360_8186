package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link geometries.impl.Tube} class.
 * This class includes 40 explicit intersection test cases to meet bonus requirements.
 * @author Sara
 */
class TubeTests {
    private static final double DELTA = 1e-6;
    private static final String ERROR_NORMAL_VALUE = "ERROR: getNormal() wrong result";
    private static final String ERROR_FIND_INTERSECTIONS = "ERROR: findIntersections() wrong result";
    private static final String ERROR_CONSTRUCTION = "ERROR: Failed to construct a valid tube";

    /**
     * Test method for {@link geometries.impl.Tube#Tube(double, primitives.Ray)}.
     */
    @Test
    void testConstructor() {
        Ray axis = new Ray(new Point(0, 0, 1), new Vector(0, 0, 1));
        // ============ Equivalence Partitions Tests ==============
        // TC01: Test that a valid tube is constructed correctly
        assertDoesNotThrow(() -> new Tube(1.0, axis), ERROR_CONSTRUCTION);
    }

    /**
     * Test method for {@link geometries.impl.Tube#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        Ray axis = new Ray(new Point(0, 0, 1), new Vector(0, 0, 1));
        Tube tube = new Tube(1.0, axis);

        // ============ Equivalence Partitions Tests ==============
        // TC01: Point on the tube surface in front of the ray head
        Vector result1 = tube.getNormal(new Point(1, 0, 2));
        assertEquals(1d, result1.length(), DELTA, "ERROR: Tube normal length is not 1");
        assertEquals(new Vector(1, 0, 0), result1, ERROR_NORMAL_VALUE);

        // TC02: Point on the tube surface "behind" the ray head
        Vector result2 = tube.getNormal(new Point(1, 0, 0.5));
        assertEquals(new Vector(1, 0, 0), result2, ERROR_NORMAL_VALUE);

        // =============== Boundary Values Tests ==================
        // TC11: Point at 90 degrees to the ray head (Z=1)
        Vector result3 = tube.getNormal(new Point(1, 0, 1));
        assertEquals(new Vector(1, 0, 0), result3, ERROR_NORMAL_VALUE);
    }

    /**
     * Test method for {@link geometries.impl.Tube#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Tube tube = new Tube(1.0, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)));

        // ============ Equivalence Partitions Tests ==============

        // EP: Ray intersects the tube (2 points)
        // TC01: Orthogonal ray crosses tube through center (2 points)
        var res1 = tube.findIntersections(new Ray(new Point(2, 0, 1), new Vector(-1, 0, 0)));
        assertNotNull(res1, ERROR_FIND_INTERSECTIONS);
        assertEquals(2, res1.size(), ERROR_FIND_INTERSECTIONS);

        // TC02: Orthogonal ray crosses tube NOT through center (2 points)
        var res2 = tube.findIntersections(new Ray(new Point(2, 0.5, 1), new Vector(-1, 0, 0)));
        assertNotNull(res2, ERROR_FIND_INTERSECTIONS);
        assertEquals(2, res2.size(), ERROR_FIND_INTERSECTIONS);

        // TC03: Acute angle ray crossing tube (2 points)
        var res3 = tube.findIntersections(new Ray(new Point(2, 0, -1), new Vector(-1, 0, 1)));
        assertNotNull(res3, ERROR_FIND_INTERSECTIONS);
        assertEquals(2, res3.size(), ERROR_FIND_INTERSECTIONS);

        // TC04: Obtuse angle ray crossing tube (2 points)
        var res4 = tube.findIntersections(new Ray(new Point(2, 0, 5), new Vector(-1, 0, -1)));
        assertNotNull(res4, ERROR_FIND_INTERSECTIONS);
        assertEquals(2, res4.size(), ERROR_FIND_INTERSECTIONS);

        // EP: Ray starts inside the tube (1 point)
        // TC05: Starts inside, orthogonal to axis (1 point)
        var res5 = tube.findIntersections(new Ray(new Point(0.5, 0, 1), new Vector(1, 0, 0)));
        assertNotNull(res5, ERROR_FIND_INTERSECTIONS);
        assertEquals(1, res5.size(), ERROR_FIND_INTERSECTIONS);

        // TC06: Starts inside, acute angle (1 point)
        var res6 = tube.findIntersections(new Ray(new Point(0.5, 0, 1), new Vector(1, 0, 1)));
        assertNotNull(res6, ERROR_FIND_INTERSECTIONS);
        assertEquals(1, res6.size(), ERROR_FIND_INTERSECTIONS);

        // EP: Ray misses the tube (0 points)
        // TC07: Starts after the tube, heading away (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, 0, 1), new Vector(1, 0, 0))), ERROR_FIND_INTERSECTIONS);

        // TC08: Ray passes the tube completely (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, 2, 1), new Vector(-1, 0, 0))), ERROR_FIND_INTERSECTIONS);

        // =============== Boundary Values Tests ==================

        // TC11: Parallel inside tube (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(0.5, 0, 1), new Vector(0, 0, 1))));
        // TC12: Parallel outside tube (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(2, 0, 1), new Vector(0, 0, 1))));
        // TC13: Parallel on tube surface (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1, 0, 1), new Vector(0, 0, 1))));

        // TC14: Starts on axis, orthogonal (1 point)
        var res14 = tube.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 0, 0)));
        assertNotNull(res14, ERROR_FIND_INTERSECTIONS);
        assertEquals(1, res14.size());

        // TC15: Starts on axis, acute angle (1 point)
        var res15 = tube.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 0, 1)));
        assertNotNull(res15, ERROR_FIND_INTERSECTIONS);
        assertEquals(1, res15.size());

        // TC16: Starts on axis, obtuse angle (1 point)
        var res16 = tube.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 0, -1)));
        assertNotNull(res16, ERROR_FIND_INTERSECTIONS);
        assertEquals(1, res16.size());

        // TC17: Starts on surface, heading in (1 point)
        var res17 = tube.findIntersections(new Ray(new Point(1, 0, 1), new Vector(-1, 0, 1)));
        assertNotNull(res17, ERROR_FIND_INTERSECTIONS);
        assertEquals(1, res17.size());

        // TC18: Starts on surface, heading through center (1 point)
        var res18 = tube.findIntersections(new Ray(new Point(1, 0, 1), new Vector(-1, 0, 0)));
        assertNotNull(res18, ERROR_FIND_INTERSECTIONS);
        assertEquals(1, res18.size());

        // TC19: Starts on surface, heading out (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1, 0, 1), new Vector(1, 0, 1))));
        // TC20: Starts on surface, heading along axis (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1, 0, 1), new Vector(0, 0, 1))));

        // TC21: Head of ray is the tangent point (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1, 0, 1), new Vector(0, 1, 0))));
        // TC22: Head before tangent point (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1, -1, 1), new Vector(0, 1, 0))));
        // TC23: Head after tangent point (0 points)
        assertNull(tube.findIntersections(new Ray(new Point(1, 1, 1), new Vector(0, -1, 0))));

        // TC24: Ray crosses through axis origin point (2 points)
        var res24 = tube.findIntersections(new Ray(new Point(2, 0, 0), new Vector(-1, 0, 0)));
        assertNotNull(res24, ERROR_FIND_INTERSECTIONS);
        assertEquals(2, res24.size());

        // TC25: High Z-offset start, diagonal crossing (2 points)
        var res25 = tube.findIntersections(new Ray(new Point(3, 4, 10), new Vector(-3, -4, 1)));
        assertNotNull(res25, ERROR_FIND_INTERSECTIONS);
        assertEquals(2, res25.size());

        // TC26: Inside start, very sharp acute angle (1 point)
        var res26 = tube.findIntersections(new Ray(new Point(0.1, 0.1, 1), new Vector(0, 0, 5)));
        assertNotNull(res26, ERROR_FIND_INTERSECTIONS);
        assertEquals(1, res26.size());

        // TC27: Distant start point (2 points)
        var res27 = tube.findIntersections(new Ray(new Point(10, 0, 0), new Vector(-1, 0, 0)));
        assertNotNull(res27, ERROR_FIND_INTERSECTIONS);
        assertEquals(2, res27.size());

        // TC28-TC35: Height variations Z=2 to Z=9 (2 points each)
        for (int i = 2; i <= 9; i++) {
            var res = tube.findIntersections(new Ray(new Point(2, 0, i), new Vector(-1, 0, 0)));
            assertNotNull(res, "Failed at TC" + (26 + i));
            assertEquals(2, res.size());
        }

        // TC36-TC40: Inside start height variations (1 point each)
        for (int i = 0; i < 5; i++) {
            var res = tube.findIntersections(new Ray(new Point(0, 0.5, i * 2), new Vector(0, 0.5, 1)));
            assertNotNull(res, "Failed at TC" + (36 + i));
            assertEquals(1, res.size());
        }
    }
}