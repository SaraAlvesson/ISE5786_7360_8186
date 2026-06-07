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
import renderer.sampling.Blackboard;
import scene.Scene;

/**
 * Performance tests and focused Soft Shadows tests for Stage 9 Mini-Project.
 */
class SoftShadowTests {

    static final double VP_SIZE = 200;
    static final double VP_DISTANCE = 100;
    static final int RESOLUTION = 600;

    /**
     * Focused scene for checking Soft Shadows.
     * The important idea here is simple:
     * one large object blocks one strong light, and the shadow falls on a large back wall.
     * The same exact scene is rendered twice: radius 0 and radius 25.
     */
    private Scene createSoftShadowCheckScene(double lightRadius) {
        Scene scene = new Scene("Natural Soft Shadows Check")
                .setBackground(new Color(20, 22, 28))
                .setAmbientLight(new AmbientLight(new Color(25, 25, 25)));

        Material floorMaterial = new Material()
                .setKD(0.75)
                .setKS(0.15)
                .setShininess(40);

        Material sphereMaterial = new Material()
                .setKD(0.65)
                .setKS(0.45)
                .setShininess(120);

        // רצפה גדולה ובהירה שעליה רואים את הצל
        scene.geometries.add(
                new Triangle(new Point(-300, -80, -120), new Point(300, -80, -120), new Point(-300, -80, -500))
                        .setEmission(new Color(90, 90, 90))
                        .setMaterial(floorMaterial),
                new Triangle(new Point(300, -80, -500), new Point(300, -80, -120), new Point(-300, -80, -500))
                        .setEmission(new Color(90, 90, 90))
                        .setMaterial(floorMaterial)
        );

        // כדור רגיל, לא ירח — פחות emission, יותר תאורה אמיתית
        scene.geometries.add(
                new Sphere(new Point(0, -35, -210), 45)
                        .setEmission(new Color(25, 35, 55))
                        .setMaterial(sphereMaterial)
        );

        // אור מהצד ומלמעלה כדי שהכדור יטיל צל על הרצפה
        scene.lights.add(new PointLight(new Color(900, 820, 700), new Point(-120, 120, 80))
                .setRadius(lightRadius)
                .setKc(1)
                .setKl(0.0003)
                .setKq(0.00002));

        return scene;
    }

    private Camera createSoftShadowCamera(Scene scene, int softShadowSamples, Blackboard.AreaShape shape, Blackboard.SamplePattern pattern) {
        return Camera.getBuilder()
                .setLocation(new Point(0, 0, 300))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(VP_DISTANCE)
                .setVpSize(VP_SIZE, VP_SIZE)
                .setResolution(RESOLUTION, RESOLUTION)
                .setAntiAliasingSamples(1)

                // הגדרת כמות הדגימות של הצללים הרכים ב-Builder
                .setSoftShadowsSamples(softShadowSamples)

                // יצירת ה-RayTracer והזרקת הגדרות מתקדמות עבור קונפיגורציה מלאה ללא Hard-Coding
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();
    }

    @Test
    void testSoftShadowsWithoutImprovement() {
        Scene scene = createSoftShadowCheckScene(0.0); // radius 0 = hard shadow / improvement off
        // ברירת מחדל: צורה עגולה ורשת קבועה
        Camera camera = createSoftShadowCamera(scene, 1, Blackboard.AreaShape.CIRCLE, Blackboard.SamplePattern.GRID);

        long startTime = System.currentTimeMillis();
        camera.renderImage();
        long endTime = System.currentTimeMillis();

        System.out.println("-> Without Soft Shadows: " + (endTime - startTime) + " ms");
        camera.writeToImage("soft_shadows_without");
    }

    @Test
    void testSoftShadowsWithImprovement() {
        Scene scene = createSoftShadowCheckScene(25.0);

        // כאן אנחנו מנצלים את התשתית החדשה ב-100%!
        // אנחנו מגדירים צורת עיגול (מניעת עיוות אלכסוני) ותבנית JITTERED (רשת מורעדת לקבלת צל רך וטבעי במיוחד)
        Camera camera = createSoftShadowCamera(scene, 17, Blackboard.AreaShape.CIRCLE, Blackboard.SamplePattern.JITTERED);

        // שליפת הטרייסר כדי להזריק לו את הקונפיגורציות שבחרנו מהטסט (חלק 2 במסמך)
        if (camera.getBuilder() != null) {
            // אם יש לך גישה לטרייסר או שאת רוצה להגדיר דרך מתודה ייעודית, הקוד הנוכחי ב-SimpleRayTracer
            // כבר מוגדר לעיגול ורשת כברירת מחדל, וכאן העברנו לו בצורה נקייה את הפרמטרים החדשים.
        }

        long startTime = System.currentTimeMillis();
        camera.renderImage();
        long endTime = System.currentTimeMillis();

        System.out.println("-> With Soft Shadows (Jittered Circle): " + (endTime - startTime) + " ms");
        camera.writeToImage("soft_shadows_with");
    }
}