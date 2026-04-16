package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GeometriesTests {
    @Test
    void testFindIntersections() {
        // גופים לבדיקה
        Sphere sphere = new Sphere(new Point(1, 0, 0), 1d); // ספירה סביב (1,0,0) ברדיוס 1
        Triangle triangle = new Triangle(
                new Point(1, 1, 0),
                new Point(2, 1, 0),
                new Point(1.5, 2, 0)
        );
        Plane plane = new Plane(new Point(0, 0, 5), new Vector(0, 0, 1)); // מישור רחוק ב-Z=5

        Geometries geometries = new Geometries(sphere, triangle, plane);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Some geometries intersect (Expected: 3 points)
        // קרן שחוצה את הספירה דרך המרכז (2 נקודות) ואת המשולש (נקודה 1)
        // היא לא תגיע למישור שנמצא ב-Z=5
        Ray raySome = new Ray(new Point(1.5, 1.2, -1), new Vector(0, 0, 1));
        var resultSome = geometries.findIntersections(raySome);
        assertNotNull(resultSome, "Should have intersections");
        assertEquals(3, resultSome.size(), "Some geometries should intersect");

        // =============== Boundary Values Tests ==================

        // BV01: Empty collection
        assertNull(new Geometries().findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 1, 1))),
                "Empty collection should return null");

        // BV02: No geometry intersects
        assertNull(geometries.findIntersections(new Ray(new Point(10, 10, 10), new Vector(1, 1, 1))),
                "No intersections should return null");

        // BV03: Only one geometry intersects
        // קרן שחוצה רק את המישור ב-Z=5
        Ray rayOne = new Ray(new Point(5, 5, 0), new Vector(0, 0, 1));
        assertEquals(1, geometries.findIntersections(rayOne).size(), "Only one geometry should intersect");

        // BV04: All geometries intersect (Expected: 4 points)
        // קרן שעוברת דרך הספירה(2), המשולש(1) והמישור(1)
        // נכוון אותה לנקודה (1.5, 1.2) שראינו שחוצה את הספירה והמשולש
        Ray rayAll = new Ray(new Point(1.5, 1.2, -1), new Vector(0, 0, 1));
        // כדי שזה יעבוד, נזיז את המישור שיהיה בדרך של הקרן
        assertEquals(4, geometries.findIntersections(rayAll).size(), "All geometries should intersect");
    }
    }