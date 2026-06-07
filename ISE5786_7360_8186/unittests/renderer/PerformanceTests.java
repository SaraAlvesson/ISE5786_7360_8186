package renderer;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.PointLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

/**
 * Performance tests and focused Soft Shadows tests for Stage 9 Mini-Project.
 */
class PerformanceTests {

    static final double VP_SIZE = 200;
    static final double VP_DISTANCE = 100;
    static final int RESOLUTION = 600;

    /**
     * Helper method to build a rich scene that requires heavy calculations
     * (perfect for testing multi-threading speedup).
     */
    private Scene createPerformanceScene() {
        Scene scene = new Scene("Performance Test Scene")
                .setBackground(new Color(25, 25, 35))
                .setAmbientLight(new AmbientLight(new Color(30, 30, 30)));

        scene.geometries.add(
                new Sphere(new Point(0, 0, -50), 50)
                        .setEmission(new Color(0, 128, 255))
                        .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(80)),
                new Triangle(new Point(-80, -80, -100), new Point(80, -80, -100), new Point(0, 80, -100))
                        .setEmission(new Color(40, 40, 40))
                        .setMaterial(new Material().setKD(0.8).setKS(0.2).setShininess(30))
        );

        scene.lights.add(new PointLight(new Color(800, 500, 500), new Point(60, 60, 100))
                .setKc(1).setKl(0.0005).setKq(0.00005));

        return scene;
    }

    @Test
    void testSingleThreadPerformance() {
        Scene scene = createPerformanceScene();

        Camera camera = Camera.getBuilder()
                .setLocation(new Point(0, 0, 250))
                .setDirection(new Point(0, 0, 0))
                .setVpDistance(VP_DISTANCE)
                .setVpSize(VP_SIZE, VP_SIZE)
                .setResolution(RESOLUTION, RESOLUTION)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setAntiAliasingSamples(6)
                .setMultiThreading(1, 0)
                .build();

        long startTime = System.currentTimeMillis();
        camera.renderImage();
        long endTime = System.currentTimeMillis();

        System.out.println("-> Single-threaded execution time: " + (endTime - startTime) + " ms");
        camera.writeToImage("performance_single_thread");
    }

    @Test
    void testMultiThreadPerformance() {
        Scene scene = createPerformanceScene();

        Camera camera = Camera.getBuilder()
                .setLocation(new Point(0, 0, 250))
                .setDirection(new Point(0, 0, 0))
                .setVpDistance(VP_DISTANCE)
                .setVpSize(VP_SIZE, VP_SIZE)
                .setResolution(RESOLUTION, RESOLUTION)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setAntiAliasingSamples(6)

                // תיקון: הגדרת מרווח הדפסה ל-0 על פי ההנחיות למניעת חסימת תהליכונים
                .setMultiThreading(4, 0.0)

                .build();

        long startTime = System.currentTimeMillis();
        camera.renderImage();
        long endTime = System.currentTimeMillis();

        System.out.println("\n-> Multi-threaded (4 threads) execution time: " + (endTime - startTime) + " ms");
        camera.writeToImage("performance_multi_thread");
    }
}