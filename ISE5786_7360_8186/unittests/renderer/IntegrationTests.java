package renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import geometries.api.*;
import primitives.*;
import geometries.impl.*; // חשוב! כאן נמצאים Sphere, Plane ו-Triangle לפי התמונה
import java.util.List;

/**
 * Integration tests for Camera and Geometries intersections.
 */
public class IntegrationTests {

    /**
     * Helper method to count intersections between a camera and a geometry.
     * @param camera The camera to shoot rays from
     * @param geo    The geometry to check intersections with
     * @return Total number of intersection points
     */
    private int countIntersections(Camera camera,Intersectable geo) {        int count = 0;
        // Integration tests use a 3x3 view plane [cite: 228, 242]
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                Ray ray = camera.constructRay(j, i);
                var results = geo.findIntersections(ray);
                if (results != null) {
                    count += results.size();
                }
            }
        }
        return count;
    }

    /** Helper for building a standard camera for these tests */
    private Camera.Builder baseCam() {
        return Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3)
                .setVpDistance(1)
                .setResolution(3, 3);
    }

    // ============ Sphere Integration Tests ==============

    @Test
    public void testSphereIntegration() {
        // TC01: Sphere r=1 (2 points) [cite: 267, 281]
        Camera cam1 = baseCam().build();
        Sphere sph1 = new Sphere(new Point(0, 0, -3), 1d);
        assertEquals(2, countIntersections(cam1, sph1), "TC01: Sphere r=1 fails");

        // TC02: Sphere r=2.5 (18 points) [cite: 250, 254]
        Camera cam2 = baseCam().setLocation(new Point(0, 0, 0.5)).build();
        Sphere sph2 = new Sphere(new Point(0, 0, -2.5), 2.5d);
        assertEquals(18, countIntersections(cam2, sph2), "TC02: Sphere r=2.5 fails");

        // TC03: Sphere r=2 (10 points) [cite: 295, 313]
        Camera cam3 = baseCam().setLocation(new Point(0, 0, 0.5)).build();
        Sphere sph3 = new Sphere(new Point(0, 0, -2), 2d);
        assertEquals(10, countIntersections(cam3, sph3), "TC03: Sphere r=2 fails");

        // TC04: Sphere r=4 (9 points - camera inside) [cite: 294, 312]
        Camera cam4 = baseCam().build();
        Sphere sph4 = new Sphere(new Point(0, 0, -1), 4d);
        assertEquals(9, countIntersections(cam4, sph4), "TC04: Sphere r=4 fails");

        // TC05: Sphere r=0.5 (0 points - sphere behind) [cite: 289, 299]
        Camera cam5 = baseCam().build();
        Sphere sph5 = new Sphere(new Point(0, 0, 1), 0.5d);
        assertEquals(0, countIntersections(cam5, sph5), "TC05: Sphere r=0.5 fails");
    }

    // ============ Plane Integration Tests ==============

    @Test
    public void testPlaneIntegration() {
        Camera cam = baseCam().build();

        // TC01: Plane parallel to VP (9 points) [cite: 328]
        Plane pl1 = new Plane(new Point(0, 0, -5), new Vector(0, 0, 1));
        assertEquals(9, countIntersections(cam, pl1), "TC01: Parallel plane fails");

        // TC02: Plane with slight slope (9 points) [cite: 322]
        Plane pl2 = new Plane(new Point(0, 0, -5), new Vector(0, 1, 2));
        assertEquals(9, countIntersections(cam, pl2), "TC02: Sloped plane fails");

        // TC03: Plane with sharp slope (6 points) [cite: 324]
        Plane pl3 = new Plane(new Point(0, 0, -5), new Vector(0, 5, 1));
        assertEquals(6, countIntersections(cam, pl3), "TC03: Sharp slope plane fails");
    }

    // ============ Triangle Integration Tests ==============

    @Test
    public void testTriangleIntegration() {
        Camera cam = baseCam().build();

        // TC01: Small triangle (1 point) [cite: 345]
        Triangle tr1 = new Triangle(new Point(0, 1, -2), new Point(1, -1, -2), new Point(-1, -1, -2));
        assertEquals(1, countIntersections(cam, tr1), "TC01: Small triangle fails");

        // TC02: Large/Tall triangle (2 points) [cite: 343]
        Triangle tr2 = new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2));
        assertEquals(2, countIntersections(cam, tr2), "TC02: Tall triangle fails");
    }
}