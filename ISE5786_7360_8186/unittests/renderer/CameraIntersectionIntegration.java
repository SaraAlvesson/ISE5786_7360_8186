package renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import geometries.api.*;
import primitives.*;
import geometries.impl.*;

/**
 * Integration tests for Camera and Geometries intersections.
 * Based on the requirements for Stage 4 [cite: 94-95].
 */
public class CameraIntersectionIntegration {

    /** Constants for the view plane resolution and size [cite: 100, 352] */
    private static final int RESOLUTION = 3;
    private static final double VP_WIDTH = 3d;
    private static final double VP_HEIGHT = 3d;
    private static final double VP_DISTANCE = 1d;

    /**
     * Helper method to count intersections and perform assertion [cite: 103-107].
     * * @param camera   The camera to shoot rays from
     * @param geo      The geometry to check intersections with
     * @param expected The expected total number of intersection points
     * @param testName The name of the test for error reporting
     */
    private void assertIntersectionsCount(Camera camera, Intersectable geo, int expected, String testName) {
        int count = 0;
        // Integration tests use a 3x3 view plane [cite: 338, 346]
        for (int i = 0; i < RESOLUTION; ++i) {
            for (int j = 0; j < RESOLUTION; ++j) {
                Ray ray = camera.constructRay(j, i);
                var results = geo.findIntersections(ray);
                if (results != null) {
                    count += results.size();
                }
            }
        }
        assertEquals(expected, count, testName + " fails: wrong number of intersections [cite: 107]");
    }

    /** Helper for building a standard base camera for these tests [cite: 338] */
    private Camera.Builder baseCam() {
        return Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(VP_WIDTH, VP_HEIGHT)
                .setVpDistance(VP_DISTANCE)
                .setResolution(RESOLUTION, RESOLUTION);
    }

    /**
     * Integration tests for Sphere intersections[cite: 96, 346].
     */
    @Test
    public void testCameraRaySphereIntegration() {
        // TC01: Sphere r=1 (2 points expected) [cite: 340]
        Camera cam1 = baseCam().build();
        Sphere sph1 = new Sphere(new Point(0, 0, -3), 1d);
        assertIntersectionsCount(cam1, sph1, 2, "TC01: Sphere r=1");

        // TC02: Sphere r=2.5 (18 points expected)
        Camera cam2 = baseCam().setLocation(new Point(0, 0, 0.5)).build();
        Sphere sph2 = new Sphere(new Point(0, 0, -2.5), 2.5d);
        assertIntersectionsCount(cam2, sph2, 18, "TC02: Sphere r=2.5");

        // TC03: Sphere r=2 (10 points expected)
        Camera cam3 = baseCam().setLocation(new Point(0, 0, 0.5)).build();
        Sphere sph3 = new Sphere(new Point(0, 0, -2), 2d);
        assertIntersectionsCount(cam3, sph3, 10, "TC03: Sphere r=2");

        // TC04: Sphere r=4 (9 points expected - camera inside)
        Camera cam4 = baseCam().build();
        Sphere sph4 = new Sphere(new Point(0, 0, -1), 4d);
        assertIntersectionsCount(cam4, sph4, 9, "TC04: Sphere r=4");

        // TC05: Sphere r=0.5 (0 points expected - sphere behind)
        Camera cam5 = baseCam().build();
        Sphere sph5 = new Sphere(new Point(0, 0, 1), 0.5d);
        assertIntersectionsCount(cam5, sph5, 0, "TC05: Sphere r=0.5");
    }

    /**
     * Integration tests for Plane intersections[cite: 96, 346].
     */
    @Test
    public void testCameraRayPlaneIntegration() {
        Camera cam = baseCam().build();

        // TC01: Plane parallel to VP (9 points expected)
        Plane pl1 = new Plane(new Point(0, 0, -5), new Vector(0, 0, 1));
        assertIntersectionsCount(cam, pl1, 9, "TC01: Parallel plane");

        // TC02: Plane with slight slope (9 points expected)
        Plane pl2 = new Plane(new Point(0, 0, -5), new Vector(0, 1, 2));
        assertIntersectionsCount(cam, pl2, 9, "TC02: Sloped plane");

        // TC03: Plane with sharp slope (6 points expected)
        Plane pl3 = new Plane(new Point(0, 0, -5), new Vector(0, 5, 1));
        assertIntersectionsCount(cam, pl3, 6, "TC03: Sharp slope plane");
    }

    /**
     * Integration tests for Triangle intersections[cite: 96, 346].
     */
    @Test
    public void testCameraRayTriangleIntegration() {
        Camera cam = baseCam().build();

        // TC01: Small triangle (1 point expected)
        Triangle tr1 = new Triangle(new Point(0, 1, -2), new Point(1, -1, -2), new Point(-1, -1, -2));
        assertIntersectionsCount(cam, tr1, 1, "TC01: Small triangle");

        // TC02: Large/Tall triangle (2 points expected)
        Triangle tr2 = new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2));
        assertIntersectionsCount(cam, tr2, 2, "TC02: Tall triangle");
    }
}