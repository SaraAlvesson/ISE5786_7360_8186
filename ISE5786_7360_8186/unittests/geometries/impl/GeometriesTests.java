package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GeometriesTests {
    @Test
    void testFindIntersections() {
        Geometries geometries = new Geometries(
                new Sphere(new Point(1, 0, 0), 1d),
                new Plane(new Point(0, 0, 2), new Vector(0, 0, 1)),
                new Triangle(new Point(1, 1, 3), new Point(2, 1, 3), new Point(1, 2, 3))
        );

        // ============ Equivalence Partitions Tests ==============
        // EP01: Some geometries intersect (but not all)
        assertEquals(3, geometries.findIntersections(new Ray(new Point(1, 0, -1), new Vector(0, 0, 1))).size(),
                "Some geometries should intersect");

        // =============== Boundary Values Tests ==================
        // BV01: Empty list
        assertNull(new Geometries().findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 0, 0))),
                "Empty geometries list");

        // BV02: No geometry intersects
        assertNull(geometries.findIntersections(new Ray(new Point(0, 0, -1), new Vector(1, 0, 0))),
                "No geometry intersects");

        // BV03: Only one geometry intersects
        assertEquals(1, geometries.findIntersections(new Ray(new Point(1, 0.5, -1), new Vector(0, 0, 1))).size(),
                "Only one geometry should intersect");

        // BV04: All geometries intersect
        // (Note: For this test, the ray should be carefully chosen to pierce all 3)
        Geometries allGeoms = new Geometries(
                new Sphere(new Point(0, 0, 2), 0.5d),
                new Plane(new Point(0, 0, 5), new Vector(0, 0, 1))
        );
        assertEquals(3, allGeoms.findIntersections(new Ray(new Point(0, 0, 0), new Vector(0, 0, 1))).size(),
                "All geometries should intersect");
    }
}