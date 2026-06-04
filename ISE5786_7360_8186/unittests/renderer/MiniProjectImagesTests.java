package renderer;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

/**
 * Image enhancement tests for Stage 9:
 * Anti-Aliasing and Soft Shadows.
 */
class MiniProjectImagesTests {

    static final double VP_SIZE = 260;
    static final double VP_DISTANCE = 150;
    static final int RESOLUTION = 700;

    /**
     * Rich scene:
     * 10+ geometries, 3 light sources.
     * Includes:
     * - curved objects for shadows
     * - diagonal thin triangles for anti-aliasing
     * - large floor for visible soft shadows
     */
    private Scene buildRichImageEnhancementScene(double mainLightRadius) {
        Scene scene = new Scene("Rich Image Enhancement Scene")
                .setBackground(new Color(18, 20, 28))
                .setAmbientLight(new AmbientLight(new Color(18, 18, 18)));

        Material floorMat = new Material().setKD(0.8).setKS(0.15).setShininess(40);
        Material wallMat = new Material().setKD(0.75).setKS(0.2).setShininess(60);
        Material ballMat = new Material().setKD(0.6).setKS(0.45).setShininess(140);
        Material sharpMat = new Material().setKD(0.7).setKS(0.25).setShininess(80);

        // 1-2: large floor for shadows
        scene.geometries.add(
                new Triangle(new Point(-360, -80, -120), new Point(360, -80, -120), new Point(-360, -80, -560))
                        .setEmission(new Color(90, 88, 82))
                        .setMaterial(floorMat),
                new Triangle(new Point(360, -80, -560), new Point(360, -80, -120), new Point(-360, -80, -560))
                        .setEmission(new Color(90, 88, 82))
                        .setMaterial(floorMat)
        );

        // 3-4: back wall
        scene.geometries.add(
                new Triangle(new Point(-360, -80, -560), new Point(360, -80, -560), new Point(-360, 180, -560))
                        .setEmission(new Color(42, 48, 58))
                        .setMaterial(wallMat),
                new Triangle(new Point(360, 180, -560), new Point(360, -80, -560), new Point(-360, 180, -560))
                        .setEmission(new Color(42, 48, 58))
                        .setMaterial(wallMat)
        );

        // 5: main sphere standing on the floor - main soft shadow object
        scene.geometries.add(
                new Sphere(new Point(-45, -35, -250), 45)
                        .setEmission(new Color(35, 50, 95))
                        .setMaterial(ballMat)
        );

        // 6-8: additional spheres for rich scene
        scene.geometries.add(
                new Sphere(new Point(70, -50, -220), 30)
                        .setEmission(new Color(130, 35, 35))
                        .setMaterial(ballMat),
                new Sphere(new Point(20, -58, -330), 22)
                        .setEmission(new Color(35, 120, 70))
                        .setMaterial(ballMat),
                new Sphere(new Point(-120, -62, -360), 18)
                        .setEmission(new Color(135, 105, 30))
                        .setMaterial(ballMat)
        );

        // 9-12: thin diagonal triangles on the back wall.
        // These are here specifically so Anti-Aliasing is visible.
        scene.geometries.add(
                new Triangle(new Point(-210, 120, -555), new Point(-55, 55, -555), new Point(-190, 100, -555))
                        .setEmission(new Color(230, 230, 245))
                        .setMaterial(sharpMat),
                new Triangle(new Point(-40, 130, -554), new Point(130, 45, -554), new Point(-20, 108, -554))
                        .setEmission(new Color(245, 180, 70))
                        .setMaterial(sharpMat),
                new Triangle(new Point(120, 135, -553), new Point(250, 50, -553), new Point(138, 112, -553))
                        .setEmission(new Color(80, 210, 230))
                        .setMaterial(sharpMat),
                new Triangle(new Point(-250, 25, -552), new Point(-80, -45, -552), new Point(-232, 7, -552))
                        .setEmission(new Color(210, 80, 190))
                        .setMaterial(sharpMat)
        );

        // 3 light sources:
        // Main area light - only this one gets radius for Soft Shadows
        scene.lights.add(new PointLight(new Color(950, 850, 700), new Point(-140, 150, 80))
                .setRadius(mainLightRadius)
                .setKc(1)
                .setKl(0.00025)
                .setKq(0.000015));

        // Side spotlight - adds drama but weaker, so it does not destroy the shadow
        scene.lights.add(new SpotLight(new Color(180, 120, 100), new Point(180, 120, 20), new Vector(-1.2, -0.8, -2))
                .setKc(1)
                .setKl(0.0004)
                .setKq(0.00004));

        // Weak fill light - keeps scene visible
        scene.lights.add(new PointLight(new Color(95, 110, 150), new Point(140, 60, 120))
                .setKc(1)
                .setKl(0.0007)
                .setKq(0.00008));

        return scene;
    }

    private Camera buildCamera(Scene scene, int antiAliasingSamples, int softShadowSamples) {
        return Camera.getBuilder()
                .setLocation(new Point(0, 35, 260))
                .setDirection(new Point(0, -20, -330), new Vector(0, 1, 0))
                .setVpDistance(VP_DISTANCE)
                .setVpSize(VP_SIZE, VP_SIZE)
                .setResolution(RESOLUTION, RESOLUTION)
                .setAntiAliasingSamples(antiAliasingSamples)
                .setSoftShadowsSamples(softShadowSamples)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setMultiThreading(4, 0.5)
                .build();
    }

    @Test
    void testRichSceneWithoutImprovements() {
        Scene scene = buildRichImageEnhancementScene(0.0);
        Camera camera = buildCamera(scene, 1, 1);

        camera.renderImage()
                .writeToImage("rich_scene_without_improvements");
    }

    @Test
    void testRichSceneWithAntiAliasing() {
        Scene scene = buildRichImageEnhancementScene(0.0);
        Camera camera = buildCamera(scene, 5, 1);

        camera.renderImage()
                .writeToImage("rich_scene_with_anti_aliasing");
    }

    @Test
    void testRichSceneWithSoftShadows() {
        Scene scene = buildRichImageEnhancementScene(25.0);
        Camera camera = buildCamera(scene, 1, 17);

        camera.renderImage()
                .writeToImage("rich_scene_with_soft_shadows");
    }

    @Test
    void testRichSceneWithBothImprovements() {
        Scene scene = buildRichImageEnhancementScene(25.0);
        Camera camera = buildCamera(scene, 5, 17);

        camera.renderImage()
                .writeToImage("rich_scene_with_both_improvements");
    }
}