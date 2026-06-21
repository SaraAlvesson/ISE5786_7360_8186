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
    static final int RESOLUTION = 1000; // High resolution for full-screen rendering

    // Creates a box using six polygons
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

    // Builds the complete living room scene
    private Scene buildLivingRoomScene(double mainLightRadius) {
        Scene scene = new Scene("Elegant Living Room Scene")
                .setBackground(new Color(8, 10, 16))
                .setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

        Material wood = new Material().setKD(0.72).setKS(0.28).setShininess(120);
        Material wall = new Material().setKD(0.86).setKS(0.10).setShininess(40);
        Material glass = new Material().setKD(0.45).setKS(0.75).setShininess(250);
        Material metal = new Material().setKD(0.55).setKS(0.60).setShininess(200);
        Material paper = new Material().setKD(0.80).setKS(0.14).setShininess(55);
        Material shade = new Material().setKD(0.80).setKS(0.10).setShininess(40);

        // Build a large enclosed room (floor + 3 walls) to eliminate the black background from all viewing angles
        scene.geometries.add(
                // Floor
                new Polygon(
                        new Point(-2500, -100, 1000),
                        new Point(2500, -100, 1000),
                        new Point(2500, -100, -1500),
                        new Point(-2500, -100, -1500)
                ).setEmission(new Color(66, 56, 46)).setMaterial(wood),

                // Back wall
                new Polygon(
                        new Point(-2500, -100, -760),
                        new Point(2500, -100, -760),
                        new Point(2500, 1200, -760),
                        new Point(-2500, 1200, -760)
                ).setEmission(new Color(42, 37, 42)).setMaterial(wall),

                // Left wall
                new Polygon(
                        new Point(-650, -100, -1500),
                        new Point(-650, -100, 1000),
                        new Point(-650, 1200, 1000),
                        new Point(-650, 1200, -1500)
                ).setEmission(new Color(36, 31, 36)).setMaterial(wall),

                // Right wall
                new Polygon(
                        new Point(650, -100, -1500),
                        new Point(650, -100, 1000),
                        new Point(650, 1200, 1000),
                        new Point(650, 1200, -1500)
                ).setEmission(new Color(36, 31, 36)).setMaterial(wall)
        );

        // Table top
        addBox(scene, -170, 190, -55, -35, -310, -520, new Color(120, 78, 42), wood);

        // Table legs
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

        // Lampshade structure points
        Point apex = new Point(-285, 94, -360);
        Point p1 = new Point(-342, 44, -334); // Front-left
        Point p2 = new Point(-228, 44, -334); // Front-right
        Point p3 = new Point(-342, 44, -386); // Back-left
        Point p4 = new Point(-228, 44, -386); // Back-right

        // Create a clear 3D appearance by using strong color differences between the lampshade faces
        scene.geometries.add(
                new Triangle(p1, p2, apex).setEmission(new Color(200, 160, 90)).setMaterial(shade), // Front face - bright
                new Triangle(p1, p3, apex).setEmission(new Color(110, 85, 45)).setMaterial(shade),  // Left face - dark and shaded
                new Triangle(p2, p4, apex).setEmission(new Color(160, 130, 70)).setMaterial(shade), // Right face
                new Triangle(p3, p4, apex).setEmission(new Color(70, 50, 25)).setMaterial(shade)    // Back face
        );

        // Books
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

        // Position the main light source to cast a long and clear shadow across the floor
        scene.lights.add(
                new PointLight(new Color(900, 760, 610), new Point(-340, 320, -260))
                        .setRadius(mainLightRadius)
                        .setKc(1)
                        .setKl(0.00023)
                        .setKq(0.000012)
        );

        // Add a secondary spotlight for warm ambient illumination
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

        return scene;
    }

    // Creates and configures the camera
    private Camera buildCamera(Scene scene, int antiAliasingSamples, int softShadowSamples) {
        return Camera.getBuilder()
                // Position the camera on the left side at eye level for a natural viewpoint
                .setLocation(new Point(-420, 70, -140))
                // Direct the camera diagonally toward the center of the room
                .setDirection(new Point(-150, -10, -420), new Vector(0, 1, 0))
                .setVpDistance(VP_DISTANCE)
                .setVpSize(VP_SIZE, VP_SIZE)
                .setResolution(RESOLUTION, RESOLUTION)
                .setAntiAliasingSamples(antiAliasingSamples)
                .setSoftShadowsSamples(softShadowSamples)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                // Use multithreading to significantly reduce rendering time
                .setMultiThreading(40, 0.5)
                .build();
    }

    // Measures rendering time and saves the output image
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