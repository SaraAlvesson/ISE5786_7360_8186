package renderer;

import geometries.impl.Cylinder;
import geometries.impl.Polygon;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.sampling.Blackboard;
import scene.Scene;

/**
 * Stage 10 final tests.
 * Final scene: warm modern living room inspired by the given reference image.
 */
public class Stage10FinalTests {

    private static final int RESOLUTION = 1000;
    private static final double VP_SIZE = 770;
    private static final double VP_DISTANCE = 860;

    private static final int ANTI_ALIASING_SAMPLES = 5;
    private static final int SOFT_SHADOW_SAMPLES = 8;

    @Test
    void stage10_noAcceleration_noThreads() {
        renderStage10Image("stage10_1_noAcceleration_noThreads", false, 0);
    }

    @Test
    void stage10_noAcceleration_withThreads() {
        renderStage10Image("stage10_2_noAcceleration_withThreads", false, -2);
    }

    @Test
    void stage10_withAcceleration_noThreads() {
        renderStage10Image("stage10_3_withBVH_noThreads", true, 0);
    }

    @Test
    void stage10_withAcceleration_withThreads() {
        renderStage10Image("stage10_4_withBVH_withThreads", true, -2);
    }

    private void renderStage10Image(String imageName, boolean useBVH, int multithreading) {
        Scene scene = buildLivingRoomScene();

        if (useBVH) {
            scene.geometries.enableBVH();
        }

        Camera.Builder builder = Camera.getBuilder()
                .setResolution(RESOLUTION, RESOLUTION)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(0, 118, 835))
                .setDirection(new Point(35, 78, -500), Vector.AXIS_Y)
                .setVpDistance(VP_DISTANCE)
                .setVpSize(VP_SIZE, VP_SIZE)
                .setAntiAliasingSamples(ANTI_ALIASING_SAMPLES)
                .setAntiAliasingShape(Blackboard.AreaShape.SQUARE)
                .setAntiAliasingPattern(Blackboard.SamplePattern.JITTERED)
                .setSoftShadowsSamples(SOFT_SHADOW_SAMPLES)
                .setMultithreading(multithreading)
                .setDebugPrint(1.0);

        long start = System.nanoTime();

        builder.build()
                .renderImage()
                .writeToImage(imageName);

        long end = System.nanoTime();
        double seconds = (end - start) / 1_000_000_000.0;

        System.out.println("========================================");
        System.out.println("Image: " + imageName);
        System.out.println("BVH enabled: " + useBVH);
        System.out.println("Multithreading mode: " + multithreading);
        System.out.println("Render time: " + seconds + " seconds");
        System.out.println("========================================");
    }

    private Scene buildLivingRoomScene() {
        Scene scene = new Scene("Stage 10 Living Room")
                .setBackground(new Color(10, 8, 7))
                .setAmbientLight(new AmbientLight(new Color(255, 228, 198), 0.02));

        Material wall = new Material().setKD(0.90).setKS(0.02).setShininess(10);
        Material ceiling = new Material().setKD(0.88).setKS(0.02).setShininess(10);
        Material floor = new Material().setKD(0.82).setKS(0.06).setKR(0.01).setShininess(8);
        Material rug = new Material().setKD(0.95).setKS(0.015).setShininess(6);
        Material fabric = new Material().setKD(0.93).setKS(0.025).setShininess(10);
        Material darkFabric = new Material().setKD(0.88).setKS(0.03).setShininess(12);
        Material wood = new Material().setKD(0.82).setKS(0.06).setKR(0.01).setShininess(8);
        Material blackWood = new Material().setKD(0.76).setKS(0.08).setKR(0.015).setShininess(12);
        Material metal = new Material().setKD(0.42).setKS(0.36).setKR(0.05).setShininess(120);
        Material glass = new Material().setKD(0.24).setKS(0.30).setKT(0.08).setKR(0.02).setShininess(90);
        Material art = new Material().setKD(0.80).setKS(0.10).setKR(0.015).setShininess(25);
        Material matteDark = new Material().setKD(0.80).setKS(0.05).setShininess(10);

        addRoom(scene, wall, ceiling, floor, matteDark);
        addWindowCurtains(scene, glass, fabric);
        addLeftCabinet(scene, blackWood, wood, glass, matteDark);
        addFloorLamp(scene, metal, fabric);
        addArmchair(scene, fabric, darkFabric, metal);
        addSofaLShape(scene, fabric, darkFabric, metal);
        addRug(scene, rug);
        addCoffeeTable(scene, blackWood, metal, matteDark);
        addWallArt(scene, art, metal);
        addCeilingSpots(scene, metal, glass);
        addDecor(scene, glass, blackWood, metal, fabric, matteDark);
        addExtraGeometryLoad(scene, matteDark, fabric, metal);
        addLights(scene);

        return scene;
    }

    private void addRoom(Scene scene, Material wall, Material ceiling, Material floor, Material matteDark) {
        scene.geometries.add(
                new Polygon(
                        new Point(-920, -120, 520),
                        new Point(920, -120, 520),
                        new Point(920, -120, -1120),
                        new Point(-920, -120, -1120)
                ).setEmission(new Color(96, 64, 40)).setMaterial(floor),

                new Polygon(
                        new Point(-920, 405, 520),
                        new Point(-920, 405, -1120),
                        new Point(920, 405, -1120),
                        new Point(920, 405, 520)
                ).setEmission(new Color(88, 69, 58)).setMaterial(ceiling),

                new Polygon(
                        new Point(-920, -120, -865),
                        new Point(920, -120, -865),
                        new Point(920, 405, -865),
                        new Point(-920, 405, -865)
                ).setEmission(new Color(119, 104, 94)).setMaterial(wall),

                new Polygon(
                        new Point(-920, -120, 520),
                        new Point(-920, -120, -1120),
                        new Point(-920, 405, -1120),
                        new Point(-920, 405, 520)
                ).setEmission(new Color(51, 42, 39)).setMaterial(wall),

                new Polygon(
                        new Point(920, -120, -1120),
                        new Point(920, -120, 520),
                        new Point(920, 405, 520),
                        new Point(920, 405, -1120)
                ).setEmission(new Color(52, 43, 40)).setMaterial(wall)
        );

        // floor planks - fewer, longer, more natural
        for (int i = 0; i < 14; i++) {
            double z = 495 - i * 118.0;
            Color plank = i % 2 == 0 ? new Color(108, 71, 45) : new Color(95, 62, 40);
            addBox(scene, -920, 920, -119.4, -118.7, z - 1.3, z + 1.3, plank, matteDark);
        }

        // back skirting
        addBox(scene, -920, 920, -103, -95, -862, -854,
                new Color(116, 88, 62), matteDark);
    }

    private void addWindowCurtains(Scene scene, Material glass, Material fabric) {
        // sheer window light plane
        addBox(scene, -919, -915, -94, 360, -720, 280,
                new Color(150, 185, 235), glass);

        // second sheer layer
        addBox(scene, -914, -910, -94, 360, -720, 280,
                new Color(185, 205, 240), glass);

        // heavy dark drapes
        for (int i = 0; i < 23; i++) {
            double z1 = 285 - i * 42.0;
            double z2 = z1 - 18.0;
            Color c = i % 2 == 0 ? new Color(43, 32, 24) : new Color(74, 56, 40);
            addBox(scene, -905, -865, -120, 386, z2, z1, c, fabric);
        }

        addBox(scene, -908, -860, 362, 378, -736, 295,
                new Color(40, 29, 22), fabric);
    }

    private void addLeftCabinet(Scene scene, Material blackWood, Material wood, Material glass, Material matteDark) {
        addBox(scene, -800, -500, -120, -44, -150, 120,
                new Color(15, 14, 14), blackWood);

        addBox(scene, -786, -640, -86, -56, -134, -36,
                new Color(30, 26, 24), wood);

        addBox(scene, -620, -515, -86, -56, -134, -36,
                new Color(30, 26, 24), wood);

        addBox(scene, -800, -500, -40, -31, -150, 120,
                new Color(11, 10, 10), blackWood);

        // objects on cabinet
        scene.geometries.add(
                new Cylinder(18, new Ray(new Point(-708, -38, -52), new Vector(0, 1, 0)), 74)
                        .setEmission(new Color(14, 32, 43)).setMaterial(glass),
                new Sphere(new Point(-708, 47, -52), 18)
                        .setEmission(new Color(14, 32, 43)).setMaterial(glass),

                new Cylinder(12, new Ray(new Point(-748, -38, -40), new Vector(0, 1, 0)), 44)
                        .setEmission(new Color(18, 19, 17)).setMaterial(matteDark),

                new Cylinder(6, new Ray(new Point(-655, -38, -12), new Vector(0, 1, 0)), 8)
                        .setEmission(new Color(26, 24, 22)).setMaterial(matteDark),
                new Cylinder(28, new Ray(new Point(-655, 5, -12), new Vector(0, 0, 1)), 1)
                        .setEmission(new Color(26, 24, 22)).setMaterial(matteDark),
                new Cylinder(28, new Ray(new Point(-655, 5, -12), new Vector(0, 0, -1)), 1)
                        .setEmission(new Color(26, 24, 22)).setMaterial(matteDark)
        );
    }

    private void addFloorLamp(Scene scene, Material metal, Material shade) {
        // tripod legs
        scene.geometries.add(
                new Cylinder(4.5, new Ray(new Point(-505, -120, -430), new Vector(0.12, 1, -0.06).normalize()), 250)
                        .setEmission(new Color(18, 16, 15)).setMaterial(metal),
                new Cylinder(4.5, new Ray(new Point(-535, -120, -400), new Vector(0.02, 1, 0.10).normalize()), 250)
                        .setEmission(new Color(18, 16, 15)).setMaterial(metal),
                new Cylinder(4.5, new Ray(new Point(-475, -120, -400), new Vector(-0.08, 1, 0.08).normalize()), 250)
                        .setEmission(new Color(18, 16, 15)).setMaterial(metal),

                new Cylinder(5.5, new Ray(new Point(-505, 105, -412), new Vector(0, 1, 0)), 26)
                        .setEmission(new Color(18, 16, 15)).setMaterial(metal),

                new Cylinder(54, new Ray(new Point(-505, 131, -412), new Vector(0, 1, 0)), 58)
                        .setEmission(new Color(200, 160, 105)).setMaterial(shade),
                new Sphere(new Point(-505, 122, -412), 24)
                        .setEmission(new Color(230, 172, 86)).setMaterial(shade)
        );
    }

    private void addArmchair(Scene scene, Material fabric, Material darkFabric, Material metal) {
        Color chair = new Color(147, 124, 100);

        // chair in left corner, angled visually toward center
        addSoftBox(scene, -430, -175, -80, -18, -390, -250, chair, fabric, 12);
        addSoftBox(scene, -448, -162, -18, 98, -420, -374,
                new Color(126, 104, 84), fabric, 10);
        addSoftBox(scene, -448, -400, -72, 60, -388, -256,
                new Color(128, 106, 86), fabric, 10);
        addSoftBox(scene, -280, -160, -72, 60, -355, -230,
                new Color(128, 106, 86), fabric, 10);

        // seat cushion
        addSoftBox(scene, -382, -248, -10, 34, -352, -278,
                new Color(181, 160, 140), fabric, 8);

        // pillow
        addSoftBox(scene, -355, -265, -2, 54, -334, -286,
                new Color(110, 90, 76), darkFabric, 8);

        // legs
        scene.geometries.add(
                new Cylinder(6, new Ray(new Point(-405, -120, -350), new Vector(0, 1, 0)), 70)
                        .setEmission(new Color(25, 23, 22)).setMaterial(metal),
                new Cylinder(6, new Ray(new Point(-200, -120, -332), new Vector(0, 1, 0)), 70)
                        .setEmission(new Color(25, 23, 22)).setMaterial(metal),
                new Cylinder(6, new Ray(new Point(-385, -120, -263), new Vector(0, 1, 0)), 70)
                        .setEmission(new Color(25, 23, 22)).setMaterial(metal),
                new Cylinder(6, new Ray(new Point(-220, -120, -245), new Vector(0, 1, 0)), 70)
                        .setEmission(new Color(25, 23, 22)).setMaterial(metal)
        );
    }

    private void addSofaLShape(Scene scene, Material fabric, Material darkFabric, Material metal) {
        Color sofa = new Color(132, 119, 110);
        Color sofaDark = new Color(103, 92, 84);

        // main long section - along depth on right side, fully visible
        addSoftBox(scene, 360, 760, -74, -8, -590, -260, sofa, fabric, 12);
        addSoftBox(scene, 345, 785, -8, 96, -610, -565, sofaDark, fabric, 10);
        addSoftBox(scene, 730, 785, -74, 100, -610, -240, sofaDark, fabric, 10);

        // inward chaise / L section - clearly visible
        addSoftBox(scene, 465, 760, -74, -8, -250, 18, sofa, fabric, 12);
        addSoftBox(scene, 448, 760, -8, 76, -250, -210, sofaDark, fabric, 10);
        addSoftBox(scene, 730, 785, -74, 90, -250, 20, sofaDark, fabric, 10);

        // seat cushions long section
        for (int i = 0; i < 3; i++) {
            double z1 = -560 + i * 96;
            addSoftBox(scene, 385, 715, -6, 48, z1, z1 + 72,
                    new Color(146, 132, 122), fabric, 8);
        }

        // chaise cushion
        addSoftBox(scene, 490, 735, -6, 48, -225, -12,
                new Color(146, 132, 122), fabric, 8);

        // pillows
        addSoftBox(scene, 430, 535, 6, 88, -530, -486,
                new Color(180, 165, 154), fabric, 8);
        addSoftBox(scene, 535, 655, 4, 86, -515, -470,
                new Color(76, 72, 70), darkFabric, 8);
        addSoftBox(scene, 628, 742, 4, 88, -515, -470,
                new Color(176, 161, 150), fabric, 8);
        addSoftBox(scene, 615, 740, 4, 88, -350, -300,
                new Color(70, 64, 62), darkFabric, 8);

        // throw blanket
        addSoftBox(scene, 320, 430, -2, 12, -320, -180,
                new Color(32, 28, 27), darkFabric, 6);
        addSoftBox(scene, 352, 420, -74, 8, -290, -190,
                new Color(32, 28, 27), darkFabric, 6);

        // legs
        addBox(scene, 385, 410, -120, -74, -565, -540, new Color(22, 20, 19), metal);
        addBox(scene, 720, 745, -120, -74, -565, -540, new Color(22, 20, 19), metal);
        addBox(scene, 488, 513, -120, -74, -20, 5, new Color(22, 20, 19), metal);
        addBox(scene, 720, 745, -120, -74, -20, 5, new Color(22, 20, 19), metal);
    }

    private void addRug(Scene scene, Material rug) {
        addBox(scene, -485, 720, -117.8, -113.8, -585, -118,
                new Color(165, 150, 138), rug);

        // subtle rug pattern only
        for (int i = 0; i < 8; i++) {
            double z = -560 + i * 58;
            addBox(scene, -470, 700, -113.2, -112.6, z, z + 3,
                    new Color(150, 138, 126), rug);
        }
    }

    private void addCoffeeTable(Scene scene, Material blackWood, Material metal, Material matteDark) {
        scene.geometries.add(
                new Cylinder(120, new Ray(new Point(70, -66, -305), new Vector(0, 1, 0)), 14)
                        .setEmission(new Color(34, 26, 21)).setMaterial(blackWood),
                new Cylinder(105, new Ray(new Point(70, -81, -305), new Vector(0, 1, 0)), 10)
                        .setEmission(new Color(21, 16, 14)).setMaterial(blackWood),

                new Cylinder(7, new Ray(new Point(-5, -120, -275), new Vector(0, 1, 0)), 58)
                        .setEmission(new Color(17, 16, 15)).setMaterial(metal),
                new Cylinder(7, new Ray(new Point(145, -120, -275), new Vector(0, 1, 0)), 58)
                        .setEmission(new Color(17, 16, 15)).setMaterial(metal),
                new Cylinder(7, new Ray(new Point(-5, -120, -349), new Vector(0, 1, 0)), 58)
                        .setEmission(new Color(17, 16, 15)).setMaterial(metal),
                new Cylinder(7, new Ray(new Point(145, -120, -349), new Vector(0, 1, 0)), 58)
                        .setEmission(new Color(17, 16, 15)).setMaterial(metal)
        );

        // table objects
        scene.geometries.add(
                new Cylinder(16, new Ray(new Point(18, -50, -292), new Vector(0, 1, 0)), 62)
                        .setEmission(new Color(28, 38, 37)).setMaterial(matteDark),
                new Sphere(new Point(85, -42, -298), 16)
                        .setEmission(new Color(43, 36, 32)).setMaterial(matteDark),
                new Cylinder(10, new Ray(new Point(124, -52, -333), new Vector(0, 1, 0)), 26)
                        .setEmission(new Color(30, 26, 24)).setMaterial(matteDark)
        );

        addBox(scene, 28, 103, -54, -48, -263, -240,
                new Color(23, 21, 19), blackWood);
        addBox(scene, 38, 96, -46, -40, -266, -244,
                new Color(77, 63, 46), matteDark);

        addPlant(scene, matteDark);
    }

    private void addWallArt(Scene scene, Material art, Material metal) {
        addBox(scene, -130, 150, 110, 258, -862, -854,
                new Color(18, 16, 15), metal);
        addBox(scene, -108, 125, 128, 238, -853, -848,
                new Color(221, 216, 207), art);

        scene.geometries.add(
                new Triangle(new Point(-95, 145, -845), new Point(3, 145, -845), new Point(-30, 222, -845))
                        .setEmission(new Color(30, 38, 42)).setMaterial(art),
                new Triangle(new Point(-12, 140, -844), new Point(100, 158, -844), new Point(26, 214, -844))
                        .setEmission(new Color(135, 50, 20)).setMaterial(art),
                new Triangle(new Point(-100, 172, -843), new Point(110, 182, -843), new Point(-10, 194, -843))
                        .setEmission(new Color(179, 127, 28)).setMaterial(art)
        );

        // picture light above
        addBox(scene, -58, 62, 276, 284, -851, -836,
                new Color(42, 38, 34), metal);
        addBox(scene, -10, 14, 258, 276, -846, -831,
                new Color(56, 49, 42), metal);
    }

    private void addCeilingSpots(Scene scene, Material metal, Material glass) {
        double[] xs = {-520, -250, 20, 290, 560};

        for (double x : xs) {
            scene.geometries.add(
                    new Cylinder(20, new Ray(new Point(x, 386, -460), new Vector(0, -1, 0)), 10)
                            .setEmission(new Color(62, 56, 48)).setMaterial(metal),
                    new Sphere(new Point(x, 370, -460), 12)
                            .setEmission(new Color(238, 214, 170)).setMaterial(glass)
            );
        }
    }

    private void addDecor(Scene scene, Material glass, Material blackWood, Material metal, Material fabric, Material matteDark) {
        // extra decor near table and cabinet for richness
        addBox(scene, -785, -760, -82, -50, -125, -90,
                new Color(28, 26, 24), blackWood);
        addBox(scene, -752, -728, -82, -50, -125, -90,
                new Color(28, 26, 24), blackWood);

        addSoftBox(scene, 12, 28, -44, 24, -295, -280,
                new Color(32, 30, 28), matteDark, 4);
        addSoftBox(scene, 24, 36, -10, 44, -316, -304,
                new Color(32, 30, 28), matteDark, 4);
    }

    private void addPlant(Scene scene, Material branchMat) {
        for (int i = 0; i < 18; i++) {
            double angle = Math.toRadians(i * 20.0);
            double x = 18 + Math.cos(angle) * (24 + (i % 4) * 7);
            double y = 18 + (i % 6) * 13;
            double z = -292 + Math.sin(angle) * (22 + (i % 3) * 6);

            scene.geometries.add(
                    new Sphere(new Point(x, y, z), 4)
                            .setEmission(new Color(18, 33, 23)).setMaterial(branchMat)
            );
        }

        for (int i = 0; i < 18; i++) {
            double angle = Math.toRadians(i * 21.0);
            double x = -750 + Math.cos(angle) * (20 + (i % 3) * 6);
            double y = 6 + (i % 5) * 13;
            double z = -32 + Math.sin(angle) * (18 + (i % 4) * 4);

            scene.geometries.add(
                    new Sphere(new Point(x, y, z), 4)
                            .setEmission(new Color(15, 33, 21)).setMaterial(branchMat)
            );
        }
    }

    private void addExtraGeometryLoad(Scene scene, Material matteDark, Material fabric, Material metal) {
        // tiny hidden / subtle geometry to keep scene rich
        for (int i = 0; i < 24; i++) {
            double x = -760 + i * 10;
            scene.geometries.add(
                    new Sphere(new Point(x, -60 + (i % 3), -112 + (i % 5)), 1.6)
                            .setEmission(new Color(22, 22, 20)).setMaterial(matteDark)
            );
        }

        for (int i = 0; i < 30; i++) {
            double z = -560 + i * 15;
            scene.geometries.add(
                    new Cylinder(1.2, new Ray(new Point(100 + (i % 6) * 8, -114, z), new Vector(0, 1, 0)), 2.4)
                            .setEmission(new Color(110, 100, 92)).setMaterial(fabric)
            );
        }

        for (int i = 0; i < 20; i++) {
            double x = 420 + i * 9;
            scene.geometries.add(
                    new Sphere(new Point(x, 2 + (i % 3), -600), 1.4)
                            .setEmission(new Color(95, 88, 82)).setMaterial(matteDark)
            );
        }

        for (int i = 0; i < 12; i++) {
            double x = -180 + i * 18;
            scene.geometries.add(
                    new Cylinder(1.5, new Ray(new Point(x, 278, -847), new Vector(0, 0, 1)), 5)
                            .setEmission(new Color(35, 32, 30)).setMaterial(metal)
            );
        }
    }

    private void addLights(Scene scene) {
        scene.lights.add(
                new DirectionalLight(
                        new Color(6, 8, 12),
                        new Vector(-1, -0.8, -0.55)
                )
        );

        // warm floor lamp
        scene.lights.add(
                new PointLight(
                        new Color(290, 170, 82),
                        new Point(-505, 135, -412)
                ).setKl(0.0017).setKq(0.000010).setRadius(78)
        );

        // picture light on wall art
        scene.lights.add(
                new SpotLight(
                        new Color(360, 330, 265),
                        new Point(5, 336, -710),
                        new Vector(0, -0.28, -1)
                ).setKl(0.0012).setKq(0.000006).setRadius(50).setNarrowBeam(3.4)
        );

        // soft cold light from window
        scene.lights.add(
                new PointLight(
                        new Color(70, 105, 165),
                        new Point(-920, 120, 135)
                ).setKl(0.0018).setKq(0.000010).setRadius(110)
        );

        // ceiling lights - stronger and more directional
        scene.lights.add(
                new SpotLight(
                        new Color(165, 125, 82),
                        new Point(-520, 370, -460),
                        new Vector(0.18, -1, -0.18)
                ).setKl(0.0013).setKq(0.000007).setRadius(45).setNarrowBeam(2.8)
        );

        scene.lights.add(
                new SpotLight(
                        new Color(150, 116, 74),
                        new Point(-250, 370, -460),
                        new Vector(0.08, -1, -0.25)
                ).setKl(0.0013).setKq(0.000007).setRadius(45).setNarrowBeam(2.8)
        );

        scene.lights.add(
                new SpotLight(
                        new Color(148, 112, 72),
                        new Point(20, 370, -460),
                        new Vector(0.02, -1, -0.28)
                ).setKl(0.0013).setKq(0.000007).setRadius(45).setNarrowBeam(2.8)
        );

        scene.lights.add(
                new SpotLight(
                        new Color(144, 108, 69),
                        new Point(290, 370, -460),
                        new Vector(-0.10, -1, -0.24)
                ).setKl(0.0013).setKq(0.000007).setRadius(45).setNarrowBeam(2.8)
        );

        scene.lights.add(
                new SpotLight(
                        new Color(140, 104, 66),
                        new Point(560, 370, -460),
                        new Vector(-0.20, -1, -0.18)
                ).setKl(0.0013).setKq(0.000007).setRadius(45).setNarrowBeam(2.8)
        );
    }

    private void addSoftBox(Scene scene,
                            double x1, double x2,
                            double y1, double y2,
                            double z1, double z2,
                            Color color,
                            Material material,
                            double radius) {
        addBox(scene,
                x1 + radius, x2 - radius,
                y1 + radius, y2 - radius,
                z1 + radius, z2 - radius,
                color,
                material
        );

        // edges along X
        scene.geometries.add(
                new Cylinder(radius, new Ray(new Point(x1, y1, z1), new Vector(1, 0, 0)), x2 - x1)
                        .setEmission(color).setMaterial(material),
                new Cylinder(radius, new Ray(new Point(x1, y1, z2), new Vector(1, 0, 0)), x2 - x1)
                        .setEmission(color).setMaterial(material),
                new Cylinder(radius, new Ray(new Point(x1, y2, z1), new Vector(1, 0, 0)), x2 - x1)
                        .setEmission(color).setMaterial(material),
                new Cylinder(radius, new Ray(new Point(x1, y2, z2), new Vector(1, 0, 0)), x2 - x1)
                        .setEmission(color).setMaterial(material)
        );

        // edges along Y
        scene.geometries.add(
                new Cylinder(radius, new Ray(new Point(x1, y1, z1), new Vector(0, 1, 0)), y2 - y1)
                        .setEmission(color).setMaterial(material),
                new Cylinder(radius, new Ray(new Point(x2, y1, z1), new Vector(0, 1, 0)), y2 - y1)
                        .setEmission(color).setMaterial(material),
                new Cylinder(radius, new Ray(new Point(x1, y1, z2), new Vector(0, 1, 0)), y2 - y1)
                        .setEmission(color).setMaterial(material),
                new Cylinder(radius, new Ray(new Point(x2, y1, z2), new Vector(0, 1, 0)), y2 - y1)
                        .setEmission(color).setMaterial(material)
        );

        // edges along Z
        scene.geometries.add(
                new Cylinder(radius, new Ray(new Point(x1, y1, z1), new Vector(0, 0, 1)), z2 - z1)
                        .setEmission(color).setMaterial(material),
                new Cylinder(radius, new Ray(new Point(x2, y1, z1), new Vector(0, 0, 1)), z2 - z1)
                        .setEmission(color).setMaterial(material),
                new Cylinder(radius, new Ray(new Point(x1, y2, z1), new Vector(0, 0, 1)), z2 - z1)
                        .setEmission(color).setMaterial(material),
                new Cylinder(radius, new Ray(new Point(x2, y2, z1), new Vector(0, 0, 1)), z2 - z1)
                        .setEmission(color).setMaterial(material)
        );

        // corners
        scene.geometries.add(
                new Sphere(new Point(x1, y1, z1), radius).setEmission(color).setMaterial(material),
                new Sphere(new Point(x1, y1, z2), radius).setEmission(color).setMaterial(material),
                new Sphere(new Point(x1, y2, z1), radius).setEmission(color).setMaterial(material),
                new Sphere(new Point(x1, y2, z2), radius).setEmission(color).setMaterial(material),
                new Sphere(new Point(x2, y1, z1), radius).setEmission(color).setMaterial(material),
                new Sphere(new Point(x2, y1, z2), radius).setEmission(color).setMaterial(material),
                new Sphere(new Point(x2, y2, z1), radius).setEmission(color).setMaterial(material),
                new Sphere(new Point(x2, y2, z2), radius).setEmission(color).setMaterial(material)
        );
    }

    private void addBox(Scene scene,
                        double x1, double x2,
                        double y1, double y2,
                        double z1, double z2,
                        Color color,
                        Material material) {
        scene.geometries.add(
                new Polygon(
                        new Point(x1, y1, z1),
                        new Point(x2, y1, z1),
                        new Point(x2, y1, z2),
                        new Point(x1, y1, z2)
                ).setEmission(color).setMaterial(material),

                new Polygon(
                        new Point(x1, y2, z1),
                        new Point(x1, y2, z2),
                        new Point(x2, y2, z2),
                        new Point(x2, y2, z1)
                ).setEmission(color).setMaterial(material),

                new Polygon(
                        new Point(x1, y1, z1),
                        new Point(x1, y1, z2),
                        new Point(x1, y2, z2),
                        new Point(x1, y2, z1)
                ).setEmission(color).setMaterial(material),

                new Polygon(
                        new Point(x2, y1, z1),
                        new Point(x2, y2, z1),
                        new Point(x2, y2, z2),
                        new Point(x2, y1, z2)
                ).setEmission(color).setMaterial(material),

                new Polygon(
                        new Point(x1, y1, z1),
                        new Point(x1, y2, z1),
                        new Point(x2, y2, z1),
                        new Point(x2, y1, z1)
                ).setEmission(color).setMaterial(material),

                new Polygon(
                        new Point(x1, y1, z2),
                        new Point(x2, y1, z2),
                        new Point(x2, y2, z2),
                        new Point(x1, y2, z2)
                ).setEmission(color).setMaterial(material)
        );
    }
}