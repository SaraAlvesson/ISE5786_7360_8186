package renderer;

import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.PointLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

import static java.awt.Color.*;

/**
 * Required image test for Stage 8.
 * The image demonstrates the new Stage 8 effects:
 * shadows, transparency, reflection, and partial shadow through transparency.
 */
class Stage8ImageTests {

    /**
     * Required Stage 8 image:
     * 3-4 different objects showing shadow, transparency, reflection,
     * and transparent object affecting the shadow.
     */
    @Test
    void stage8RequiredImage() {
        Scene scene = new Scene("Stage 8 Required Image");

        scene.background = new Color(15, 15, 25);

        scene.geometries.add(
                // Reflective floor - demonstrates reflection
                new Plane(
                        new Point(0, 0, -120),
                        new Vector(0, 0, 1)
                ).setEmission(new Color(35, 35, 40))
                        .setMaterial(new Material()
                                .setKD(0.35)
                                .setKS(0.45)
                                .setShininess(120)
                                .setKR(0.45)),

                // Transparent blue sphere - demonstrates transparency and partial shadow
                new Sphere(new Point(-70, 0, 0), 45)
                        .setEmission(new Color(20, 60, 180))
                        .setMaterial(new Material()
                                .setKD(0.25)
                                .setKS(0.45)
                                .setShininess(100)
                                .setKT(0.55)),

                // Opaque red sphere - creates a clear shadow and reflection
                new Sphere(new Point(55, -10, -20), 35)
                        .setEmission(new Color(180, 35, 35))
                        .setMaterial(new Material()
                                .setKD(0.45)
                                .setKS(0.35)
                                .setShininess(80)),

                // Mirror-like triangle - demonstrates reflection
                new Triangle(
                        new Point(-20, 90, -110),
                        new Point(95, 75, -105),
                        new Point(45, 20, 65)
                ).setEmission(new Color(25, 25, 30))
                        .setMaterial(new Material()
                                .setKD(0.2)
                                .setKS(0.7)
                                .setShininess(180)
                                .setKR(0.7))
        );

        scene.lights.add(
                new PointLight(
                        new Color(850, 650, 450),
                        new Point(-160, -120, 170)
                )
                        .setKl(0.0007)
                        .setKq(0.000004)
        );

        Camera.getBuilder()
                .setLocation(new Point(0, -450, 130))
                .setDirection(new Point(0, 0, -35), new Vector(0, 0, 1))
                .setVpDistance(450)
                .setVpSize(300, 300)
                .setResolution(600, 600)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build()
                .renderImage()
                .writeToImage("stage8_required_image");
    }
}