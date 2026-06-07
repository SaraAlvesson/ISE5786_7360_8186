package renderer;

import geometries.impl.Cylinder;
import geometries.impl.Polygon;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

class MiniProjectImagesTests {

    static final double VP_SIZE = 360;
    static final double VP_DISTANCE = 220;
    static final int RESOLUTION = 600;

    private void addBox(Scene scene, double x1, double x2, double y1, double y2,
                        double z1, double z2, Color color, Material material) {
        scene.geometries.add(
                new Polygon(new Point(x1, y1, z1), new Point(x2, y1, z1), new Point(x2, y1, z2), new Point(x1, y1, z2))
                        .setEmission(color).setMaterial(material),
                new Polygon(new Point(x1, y2, z1), new Point(x1, y2, z2), new Point(x2, y2, z2), new Point(x2, y2, z1))
                        .setEmission(color).setMaterial(material),
                new Polygon(new Point(x1, y1, z1), new Point(x1, y1, z2), new Point(x1, y2, z2), new Point(x1, y2, z1))
                        .setEmission(color).setMaterial(material),
                new Polygon(new Point(x2, y1, z1), new Point(x2, y2, z1), new Point(x2, y2, z2), new Point(x2, y1, z2))
                        .setEmission(color).setMaterial(material),
                new Polygon(new Point(x1, y1, z1), new Point(x1, y2, z1), new Point(x2, y2, z1), new Point(x2, y1, z1))
                        .setEmission(color).setMaterial(material),
                new Polygon(new Point(x1, y1, z2), new Point(x2, y1, z2), new Point(x2, y2, z2), new Point(x1, y2, z2))
                        .setEmission(color).setMaterial(material)
        );
    }

    private Scene buildLivingRoomScene(double mainLightRadius) {
        Scene scene = new Scene("Elegant Living Room Scene")
                .setBackground(new Color(8, 10, 16))
                .setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

        Material wood = new Material().setKD(0.72).setKS(0.28).setShininess(120);
        Material wall = new Material().setKD(0.86).setKS(0.10).setShininess(40);
        Material glass = new Material().setKD(0.45).setKS(0.75).setShininess(250);
        Material metal = new Material().setKD(0.55).setKS(0.60).setShininess(200);
        Material paper = new Material().setKD(0.80).setKS(0.14).setShininess(55);
        Material shade = new Material().setKD(0.76).setKS(0.14).setShininess(60);

        // Floor + back wall
        scene.geometries.add(
                new Polygon(
                        new Point(-450, -100, -120),
                        new Point(450, -100, -120),
                        new Point(450, -100, -760),
                        new Point(-450, -100, -760)
                ).setEmission(new Color(66, 56, 46)).setMaterial(wood),

                new Polygon(
                        new Point(-450, -100, -760),
                        new Point(450, -100, -760),
                        new Point(450, 260, -760),
                        new Point(-450, 260, -760)
                ).setEmission(new Color(42, 37, 42)).setMaterial(wall)
        );

        // Table top
        addBox(scene, -170, 190, -55, -35, -310, -520, new Color(120, 78, 42), wood);

        // Table legs moved slightly inward so their shadows merge better
        addBox(scene, -142, -124, -100, -55, -340, -360, new Color(70, 42, 24), wood);
        addBox(scene, 124, 142, -100, -55, -340, -360, new Color(70, 42, 24), wood);
        addBox(scene, -142, -124, -100, -55, -470, -490, new Color(70, 42, 24), wood);
        addBox(scene, 124, 142, -100, -55, -470, -490, new Color(70, 42, 24), wood);

        // Lamp base and pole
        scene.geometries.add(
                new Cylinder(34, new Ray(new Point(-285, -100, -360), new Vector(0, 1, 0)), 8)
                        .setEmission(new Color(155, 118, 58)).setMaterial(metal),
                new Cylinder(7, new Ray(new Point(-285, -92, -360), new Vector(0, 1, 0)), 134)
                        .setEmission(new Color(185, 145, 72)).setMaterial(metal),
                new Sphere(new Point(-285, 48, -360), 8)
                        .setEmission(new Color(205, 165, 85)).setMaterial(metal)
        );

        // Lamp shade made a little smaller and calmer,
        // so the shadow under/near the lamp looks natural
        scene.geometries.add(
                new Triangle(new Point(-342, 44, -334), new Point(-228, 44, -334), new Point(-285, 94, -360))
                        .setEmission(new Color(236, 200, 128)).setMaterial(shade),
                new Triangle(new Point(-342, 44, -386), new Point(-228, 44, -386), new Point(-285, 94, -360))
                        .setEmission(new Color(205, 166, 98)).setMaterial(shade),
                new Triangle(new Point(-342, 44, -334), new Point(-342, 44, -386), new Point(-285, 94, -360))
                        .setEmission(new Color(220, 178, 105)).setMaterial(shade),
                new Triangle(new Point(-228, 44, -334), new Point(-228, 44, -386), new Point(-285, 94, -360))
                        .setEmission(new Color(242, 206, 132)).setMaterial(shade)
        );

        // Books moved a bit left/back and lowered,
        // to avoid the detached "book shadow" on the right floor
        addBox(scene, 0, 88, -32, -23, -338, -368, new Color(150, 45, 45), paper);
        addBox(scene, 10, 102, -21, -12, -345, -375, new Color(45, 115, 150), paper);
        addBox(scene, 22, 118, -10, -1, -352, -382, new Color(210, 175, 80), paper);

        // Vase
        scene.geometries.add(
                new Cylinder(10, new Ray(new Point(-55, -35, -395), new Vector(0, 1, 0)), 48)
                        .setEmission(new Color(35, 95, 75)).setMaterial(glass),
                new Cylinder(5, new Ray(new Point(-55, 13, -395), new Vector(0, 1, 0)), 28)
                        .setEmission(new Color(25, 80, 60)).setMaterial(glass),
                new Sphere(new Point(-55, 45, -395), 7)
                        .setEmission(new Color(45, 110, 85)).setMaterial(glass)
        );

        // Decorative spheres
        scene.geometries.add(
                new Sphere(new Point(-185, -76, -300), 25)
                        .setEmission(new Color(80, 190, 220)).setMaterial(glass),
                new Sphere(new Point(-70, -72, -275), 29)
                        .setEmission(new Color(55, 100, 170)).setMaterial(glass)
        );

        // Picture frame
        addBox(scene, -210, 210, 80, 230, -752, -748, new Color(22, 22, 24), wall);

        // Flower picture
        scene.geometries.add(
                new Sphere(new Point(0, 160, -742), 22).setEmission(new Color(245, 205, 65)).setMaterial(paper),
                new Sphere(new Point(0, 202, -741), 18).setEmission(new Color(220, 85, 145)).setMaterial(paper),
                new Sphere(new Point(0, 118, -741), 18).setEmission(new Color(220, 85, 145)).setMaterial(paper),
                new Sphere(new Point(-42, 160, -741), 18).setEmission(new Color(220, 85, 145)).setMaterial(paper),
                new Sphere(new Point(42, 160, -741), 18).setEmission(new Color(220, 85, 145)).setMaterial(paper),
                new Sphere(new Point(-30, 190, -740), 16).setEmission(new Color(235, 120, 170)).setMaterial(paper),
                new Sphere(new Point(30, 190, -740), 16).setEmission(new Color(235, 120, 170)).setMaterial(paper),
                new Sphere(new Point(-30, 130, -740), 16).setEmission(new Color(235, 120, 170)).setMaterial(paper),
                new Sphere(new Point(30, 130, -740), 16).setEmission(new Color(235, 120, 170)).setMaterial(paper)
        );

        /*
         * LIGHTING:
         * 1) Main light is higher and a bit more centered than before:
         *    - shorter, cleaner table shadow
         *    - natural lamp grounding shadow
         *    - soft shadows still visible
         */
        scene.lights.add(
                new PointLight(new Color(900, 760, 610), new Point(-215, 285, -285))
                        .setRadius(mainLightRadius)
                        .setKc(1)
                        .setKl(0.00023)
                        .setKq(0.000012)
        );

        /*
         * 2) Weak right fill:
         *    kept weak on purpose so it doesn't create a second obvious shadow system.
         */
        scene.lights.add(
                new SpotLight(
                        new Color(85, 62, 48),
                        new Point(250, 205, -240),
                        new Vector(-1.9, -1.2, -3.4)
                )
                        .setKc(1)
                        .setKl(0.00075)
                        .setKq(0.00007)
        );

        /*
         * 3) Very soft front/camera fill:
         *    only to open dark areas a little, without creating visible extra shadows.
         */
        scene.lights.add(
                new PointLight(new Color(32, 42, 62), new Point(10, 130, 140))
                        .setKc(1)
                        .setKl(0.0011)
                        .setKq(0.00011)
        );

        return scene;
    }

    private Camera buildCamera(Scene scene, int antiAliasingSamples, int softShadowSamples) {
        return Camera.getBuilder()
                .setLocation(new Point(0, 35, 280))
                .setDirection(new Point(0, -20, -460), new Vector(0, 1, 0))
                .setVpDistance(VP_DISTANCE)
                .setVpSize(VP_SIZE, VP_SIZE)
                .setResolution(RESOLUTION, RESOLUTION)
                .setAntiAliasingSamples(antiAliasingSamples)
                .setSoftShadowsSamples(softShadowSamples)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setMultiThreading(4, 0.5)
                .build();
    }

    private void renderWithTiming(Camera camera, String fileName) {
        long start = System.currentTimeMillis();
        camera.renderImage();
        long end = System.currentTimeMillis();

        System.out.println("-> " + fileName + " render time: " + (end - start) + " ms");
        camera.writeToImage(fileName);
    }

    @Test
    void testLivingRoomWithoutImprovements() {
        renderWithTiming(
                buildCamera(buildLivingRoomScene(0.0), 1, 1),
                "living_room_without_improvements"
        );
    }

    @Test
    void testLivingRoomWithAntiAliasing() {
        renderWithTiming(
                buildCamera(buildLivingRoomScene(0.0), 9, 1),
                "living_room_with_anti_aliasing"
        );
    }

    @Test
    void testLivingRoomWithSoftShadows() {
        renderWithTiming(
                buildCamera(buildLivingRoomScene(30.0), 1, 25),
                "living_room_with_soft_shadows"
        );
    }

    @Test
    void testLivingRoomWithBothImprovements() {
        renderWithTiming(
                buildCamera(buildLivingRoomScene(30.0), 9, 25),
                "living_room_with_both_improvements"
        );
    }
}