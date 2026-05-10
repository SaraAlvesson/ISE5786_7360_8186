package renderer;

import geometries.api.Intersectable;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration tests for Camera and Geometries intersections.
 */
public class CameraIntersectionIntegration {

    /**
     * Constants for the view plane resolution and size
     */
    private static final int RESOLUTION = 3;
    private static final double VP_WIDTH = 3d;
    private static final double VP_HEIGHT = 3d;
    private static final double VP_DISTANCE = 1d;

    /**
     * Helper method to count intersections and perform assertion.
     *
     * @param expected The expected total number of intersection points
     * @param camera   The camera to shoot rays from
     * @param geo      The geometry to check intersections with
     * @param testName The name of the test for error reporting
     */
    private void assertIntersectionsCount(int expected, Camera camera,
                                          Intersectable geo, String testName) {

        int count = 0;

        for (int i = 0; i < RESOLUTION; ++i) {
            for (int j = 0; j < RESOLUTION; ++j) {

                Ray ray = camera.constructRay(j, i);
                var results = geo.findIntersections(ray);

                if (results != null) {
                    count += results.size();
                }
            }
        }

        assertEquals(expected, count,
                testName + " fails: wrong number of intersections");
    }

    /**
     * Helper for building a standard base camera for these tests
     */
    private Camera.Builder baseCameraBuilder() {

        return Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1),
                        new Vector(0, 1, 0))
                .setVpSize(VP_WIDTH, VP_HEIGHT)
                .setVpDistance(VP_DISTANCE)
                .setResolution(RESOLUTION, RESOLUTION);
    }

    /**
     * Integration tests for Sphere intersections
     */
    @Test
    public void testCameraRaySphereIntegration() {

        // TC01: Sphere r=1 (2 points expected)
        Camera cam1 = baseCameraBuilder().build();
        Sphere sph1 = new Sphere(new Point(0, 0, -3), 1d);

        assertIntersectionsCount(2, cam1, sph1,
                "TC01: Sphere r=1");

        // TC02: Sphere r=2.5 (18 points expected)
        Camera cam2 = baseCameraBuilder()
                .setLocation(new Point(0, 0, 0.5))
                .build();

        Sphere sph2 = new Sphere(new Point(0, 0, -2.5), 2.5d);

        assertIntersectionsCount(18, cam2, sph2,
                "TC02: Sphere r=2.5");

        // TC03: Sphere r=2 (10 points expected)
        Camera cam3 = baseCameraBuilder()
                .setLocation(new Point(0, 0, 0.5))
                .build();

        Sphere sph3 = new Sphere(new Point(0, 0, -2), 2d);

        assertIntersectionsCount(10, cam3, sph3,
                "TC03: Sphere r=2");

        // TC04: Sphere r=4 (9 points expected - camera inside)
        Camera cam4 = baseCameraBuilder().build();
        Sphere sph4 = new Sphere(new Point(0, 0, -1), 4d);

        assertIntersectionsCount(9, cam4, sph4,
                "TC04: Sphere r=4");

        // TC05: Sphere r=0.5 (0 points expected - sphere behind)
        Camera cam5 = baseCameraBuilder().build();
        Sphere sph5 = new Sphere(new Point(0, 0, 1), 0.5d);

        assertIntersectionsCount(0, cam5, sph5,
                "TC05: Sphere r=0.5");
    }

    /**
     * Integration tests for Plane intersections
     */
    @Test
    public void testCameraRayPlaneIntegration() {

        Camera cam = baseCameraBuilder().build();

        // TC01: Plane parallel to VP (9 points expected)
        Plane pl1 = new Plane(new Point(0, 0, -5),
                new Vector(0, 0, 1));

        assertIntersectionsCount(9, cam, pl1,
                "TC01: Parallel plane");

        // TC02: Plane with slight slope (9 points expected)
        Plane pl2 = new Plane(new Point(0, 0, -5),
                new Vector(0, 1, 2));

        assertIntersectionsCount(9, cam, pl2,
                "TC02: Sloped plane");

        // TC03: Plane with sharp slope (6 points expected)
        Plane pl3 = new Plane(new Point(0, 0, -5),
                new Vector(0, 5, 1));

        assertIntersectionsCount(6, cam, pl3,
                "TC03: Sharp slope plane");
    }

    /**
     * Integration tests for Triangle intersections
     */
    @Test
    public void testCameraRayTriangleIntegration() {

        Camera cam = baseCameraBuilder().build();

        // TC01: Small triangle (1 point expected)
        Triangle tr1 = new Triangle(
                new Point(0, 1, -2),
                new Point(1, -1, -2),
                new Point(-1, -1, -2));

        assertIntersectionsCount(1, cam, tr1,
                "TC01: Small triangle");

        // TC02: Tall triangle (2 points expected)
        Triangle tr2 = new Triangle(
                new Point(0, 20, -2),
                new Point(1, -1, -2),
                new Point(-1, -1, -2));

        assertIntersectionsCount(2, cam, tr2,
                "TC02: Tall triangle");
    }
}