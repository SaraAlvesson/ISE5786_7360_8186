package primitives;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for primitives.Ray class.
 *
 * @author Sara
 */
class RayTests {
    private static final double DELTA = 1e-6;
    private static final String ERROR_DIRECTION_NORM = "ERROR: Ray direction vector must be normalized";

    @Test
    void testRay() {
        Vector dir = new Vector(1, 1, 1);
        Ray ray = new Ray(new Point(1, 2, 3), dir);
        assertEquals(1d, ray.direction().length(), DELTA, ERROR_DIRECTION_NORM);
    }

    @Test
    void testGetPoint() {
        Ray ray = new Ray(new Point(1, 1, 1), new Vector(1, 0, 0));
        assertEquals(new Point(2, 1, 1), ray.getPoint(1), "getPoint() wrong result for t > 0");
        assertEquals(new Point(1, 1, 1), ray.getPoint(0), "getPoint() should return the ray origin for t = 0");
    }

    /**
     * Test method for {@link primitives.Ray#findClosestPoint(List)}.
     */
    @Test
    void testFindClosestPoint() {
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        Point p1 = new Point(1, 0, 0);
        Point p2 = new Point(2, 0, 0);
        Point p3 = new Point(3, 0, 0);

        // ============ Equivalence Partitions Tests ==============
        // EP01: The closest point is in the middle of the list[cite: 3]
        assertEquals(p1, ray.findClosestPoint(List.of(p2, p1, p3)),
                "Closest point in middle of list fails");

        // =============== Boundary Values Tests ==================
        // BV01: Empty list (null parameter) -> returns null[cite: 3]
        assertNull(ray.findClosestPoint(null), "Empty list should return null");

        // BV02: The closest point is the first in the list[cite: 3]
        assertEquals(p1, ray.findClosestPoint(List.of(p1, p2, p3)),
                "Closest point as first element fails");

        // BV03: The closest point is the last in the list[cite: 3]
        assertEquals(p1, ray.findClosestPoint(List.of(p2, p3, p1)),
                "Closest point as last element fails");
    }
}