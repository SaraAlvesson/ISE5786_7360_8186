package renderer;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.PointLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

/**
 * Official Rich Scene Tests for Stage 9 Mini-Project.
 * Contains a comprehensive scene structured specifically to display Soft Shadows penumbra blur.
 */
class PerformanceTests {

    static final double VP_SIZE = 200;
    static final double VP_DISTANCE = 100;
    static final int RESOLUTION = 600; // Sharp and clear image resolution

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
                        .setEmission(new Color(0, 128, 255)),
                new Triangle(new Point(-80, -80, -100), new Point(80, -80, -100), new Point(0, 80, -100))
                        .setEmission(new Color(40, 40, 40))
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
                .setMultiThreading(4, 0.5)
                .build();

        long startTime = System.currentTimeMillis();
        camera.renderImage();
        long endTime = System.currentTimeMillis();

        System.out.println("\n-> Multi-threaded (4 threads) execution time: " + (endTime - startTime) + " ms");
        camera.writeToImage("performance_multi_thread");
    }

    @Test
    void testSoftShadows() {
        // בניית סצנה עשירה וגדולה שתופסת את כל שטח הפנים מאחור למניעת בריחת צללים
        Scene scene = new Scene("Rich Soft Shadows Demonstration")
                .setBackground(Color.BLACK)
                .setAmbientLight(new AmbientLight(new Color(15, 15, 15)));

        // רצפת רקע ענקית המורכבת משני משולשים (ריבוע ענק במרחב) כדי לקלוט את כל הצללים בצורה יציבה
        scene.geometries.add(
                new Triangle(new Point(-300, -300, -200), new Point(300, -300, -200), new Point(-300, 300, -200))
                        .setEmission(new Color(100, 60, 20)), // משטח בצבע חום-אדמה עשיר
                new Triangle(new Point(300, 300, -200), new Point(300, -300, -200), new Point(-300, 300, -200))
                        .setEmission(new Color(100, 60, 20))
        );

        // הוספת 3 כדורים בגדלים ובמיקומים שונים לקבלת קומפוזיציה עשירה המטילה צללים מרובים
        scene.geometries.add(
                new Sphere(new Point(-40, 20, -50), 25)
                        .setEmission(new Color(0, 0, 220)), // כדור כחול
                new Sphere(new Point(30, -10, -30), 20)
                        .setEmission(new Color(0, 220, 0)), // כדור ירוק
                new Sphere(new Point(-10, -50, -10), 15)
                        .setEmission(new Color(220, 0, 0))  // כדור אדום
        );

        // מקור אור שטח חזק הממוקם למעלה וקדימה, עם רדיוס גדול (20 יחידות) ליצירת צל רך במיוחד
        PointLight areaLight = new PointLight(new Color(1000, 900, 800), new Point(80, 100, 120))
                .setRadius(20.0) // רדיוס גדול מבטיח שולי צל (Penumbra) רחבים ומטושטשים בעין
                .setKc(1).setKl(0.0001).setKq(0.00001);

        scene.lights.add(areaLight);

        // בניית המצלמה המיושרת קדימה עם 6x6 דגימות באלומה וריבוי תהליכונים
        Camera camera = Camera.getBuilder()
                .setLocation(new Point(0, 0, 300))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(VP_DISTANCE)
                .setVpSize(VP_SIZE, VP_SIZE)
                .setResolution(RESOLUTION, RESOLUTION)
                .setSoftShadowsSamples(6)  // 6x6 אלומת קרני צל לטשטוש מושלם וחלק
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setMultiThreading(4, 0.5)
                .build();

        camera.renderImage()
                .writeToImage("soft_shadows_render_test");
    }
}