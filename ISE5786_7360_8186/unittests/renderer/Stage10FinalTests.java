package renderer;

import geometries.impl.Cylinder;
import geometries.impl.Geometries;
import geometries.impl.Polygon;
import geometries.impl.Sphere;
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
 * Final compliance test suite for Stage 10 (Mini-Project 2).
 * Runs the comprehensive 12-configuration performance benchmarking matrix
 * to analyze multi-threading and Bounding Volume Hierarchy (BVH/CBR) speedup.
 */
public class Stage10FinalTests {

    private static final int RESOLUTION = 1000;
    private static final double VP_SIZE = 740;
    private static final double VP_DISTANCE = 900;

    private static final int ANTI_ALIASING_SAMPLES = 5;
    private static final int SOFT_SHADOW_SAMPLES = 8;

    /**
     * Enumeration representing the different spatial scene structures
     * required for proper control benchmarking.
     */
    private enum SceneStructure {
        FLATTENED, MANUAL_HIERARCHY, AUTOMATIC_HIERARCHY
    }

    // =========================================================================
    // CONFIGURATIONS 1-6: NO ACCELERATION (CBR DISABLED)
    // =========================================================================

    @Test
    void stage10_1_noAcceleration_flattened_noThreads() {
        renderStage10Image("stage10_1_noAcceleration_flattened_noThreads", SceneStructure.FLATTENED, false, 0);
    }

    @Test
    void stage10_2_noAcceleration_flattened_withThreads() {
        renderStage10Image("stage10_2_noAcceleration_flattened_withThreads", SceneStructure.FLATTENED, false, -2);
    }

    @Test
    void stage10_3_noAcceleration_manualHierarchy_noThreads() {
        renderStage10Image("stage10_3_noAcceleration_manualHierarchy_noThreads", SceneStructure.MANUAL_HIERARCHY, false, 0);
    }

    @Test
    void stage10_4_noAcceleration_manualHierarchy_withThreads() {
        renderStage10Image("stage10_4_noAcceleration_manualHierarchy_withThreads", SceneStructure.MANUAL_HIERARCHY, false, -2);
    }

    @Test
    void stage10_5_noAcceleration_automaticHierarchy_noThreads() {
        renderStage10Image("stage10_5_noAcceleration_automaticHierarchy_noThreads", SceneStructure.AUTOMATIC_HIERARCHY, false, 0);
    }

    @Test
    void stage10_6_noAcceleration_automaticHierarchy_withThreads() {
        renderStage10Image("stage10_6_noAcceleration_automaticHierarchy_withThreads", SceneStructure.AUTOMATIC_HIERARCHY, false, -2);
    }

    // =========================================================================
    // CONFIGURATIONS 7-12: WITH ACCELERATION (CBR ENABLED)
    // =========================================================================

    @Test
    void stage10_7_withCBR_flattened_noThreads() {
        renderStage10Image("stage10_7_withCBR_flattened_noThreads", SceneStructure.FLATTENED, true, 0);
    }

    @Test
    void stage10_8_withCBR_flattened_withThreads() {
        renderStage10Image("stage10_8_withCBR_flattened_withThreads", SceneStructure.FLATTENED, true, -2);
    }

    @Test
    void stage10_9_withCBR_manualHierarchy_noThreads() {
        renderStage10Image("stage10_9_withCBR_manualHierarchy_noThreads", SceneStructure.MANUAL_HIERARCHY, true, 0);
    }

    @Test
    void stage10_10_withCBR_manualHierarchy_withThreads() {
        renderStage10Image("stage10_10_withCBR_manualHierarchy_withThreads", SceneStructure.MANUAL_HIERARCHY, true, -2);
    }

    @Test
    void stage10_11_withCBR_automaticHierarchy_noThreads() {
        renderStage10Image("stage10_11_withCBR_automaticHierarchy_noThreads", SceneStructure.AUTOMATIC_HIERARCHY, true, 0);
    }

    @Test
    void stage10_12_withCBR_automaticHierarchy_withThreads() {
        renderStage10Image("stage10_12_withCBR_automaticHierarchy_withThreads", SceneStructure.AUTOMATIC_HIERARCHY, true, -2);
    }

    /**
     * Master benchmarking driver method that configures the requested structural mode,
     * CBR settings, and multi-threading variables fairly across a consistent rich scene.
     */
    private void renderStage10Image(String imageName, SceneStructure structure, boolean useCBR, int multithreading) {
        Scene scene = buildLivingRoomScene(); // Built as a Manual Hierarchy initially

        double overheadSeconds = 0.0;

        // Reconstruct spatial composition according to requested benchmark setup
        if (structure == SceneStructure.FLATTENED) {
            scene.geometries = scene.geometries.flatten();
        } else if (structure == SceneStructure.AUTOMATIC_HIERARCHY) {
            scene.geometries = scene.geometries.flatten(); // Re-build automatically from a clean flat base

            long overheadStart = System.nanoTime();
            scene.geometries.buildBVH();
            long overheadEnd = System.nanoTime();
            overheadSeconds = (overheadEnd - overheadStart) / 1_000_000_000.0;
        }

        // Explicitly set acceleration feature visibility state
        if (useCBR) {
            scene.geometries.enableCBR();
        } else {
            scene.geometries.disableCBR();
        }

        Camera.Builder builder = Camera.getBuilder()
                .setResolution(RESOLUTION, RESOLUTION)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(260, 132, 905))
                .setDirection(new Point(-430, 82, -505), Vector.AXIS_Y)
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
        double renderSeconds = (end - start) / 1_000_000_000.0;

        System.out.println("========================================");
        System.out.println("Image: " + imageName);
        System.out.println("Scene Structure Mode: " + structure);
        System.out.println("CBR Acceleration Checks: " + (useCBR ? "ENABLED" : "DISABLED"));
        System.out.println("Multithreading Count Mode: " + multithreading);
        if (structure == SceneStructure.AUTOMATIC_HIERARCHY) {
            System.out.println("Automatic BVH Tree Build Overhead: " + overheadSeconds + " seconds");
        }
        System.out.println("Pure Rendering Execution Time: " + renderSeconds + " seconds");
        System.out.println("========================================");
    }

    /**
     * Builds the underlying rich scene using a Manual Hierarchy pattern.
     * Elements are bundled into spatial subgroups to allow multi-level rejection testing.
     */
    private Scene buildLivingRoomScene() {
        Scene scene = new Scene("Stage 10 Living Room")
                .setBackground(new Color(8, 7, 6))
                .setAmbientLight(new AmbientLight(new Color(255, 225, 195), 0.010));

        Material wall = new Material().setKD(0.85).setKS(0.05).setShininess(10);
        Material ceiling = new Material().setKD(0.85).setKS(0.05).setShininess(10);
        Material floor = new Material().setKD(0.85).setKS(0.15).setShininess(15);
        Material rug = new Material().setKD(0.95).setKS(0.01).setShininess(6);
        Material fabric = new Material().setKD(0.86).setKS(0.05).setShininess(12);
        Material darkFabric = new Material().setKD(0.80).setKS(0.05).setShininess(12);
        Material wood = new Material().setKD(0.80).setKS(0.10).setShininess(10);
        Material blackWood = new Material().setKD(0.72).setKS(0.15).setShininess(18);
        Material metal = new Material().setKD(0.30).setKS(0.70).setKR(0.10).setShininess(100);
        Material glass = new Material().setKD(0.04).setKS(0.22).setKT(0.90).setKR(0.02).setShininess(120);
        Material matteDark = new Material().setKD(0.80).setKS(0.05).setShininess(10);
        Material tvScreen = new Material().setKD(0.06).setKS(0.65).setKR(0.12).setShininess(150);
        Material lampShade = new Material().setKD(0.80).setKS(0.08).setKT(0.04).setShininess(10);
        Material darkGloss = new Material().setKD(0.08).setKS(0.55).setKT(0.16).setKR(0.12).setShininess(180);

        // Instantiate structural composite groups for Manual Hierarchy architecture
        Geometries roomGroup = new Geometries();
        addRoom(roomGroup, wall, ceiling, floor, matteDark);

        Geometries windowCabinetGroup = new Geometries();
        addLeftWallWindowAndCabinet(windowCabinetGroup, glass, blackWood, matteDark);

        Geometries floorLampGroup = new Geometries();
        addFloorLamp(floorLampGroup, metal, lampShade);

        Geometries armchairGroup = new Geometries();
        addArmchairRotated(armchairGroup, fabric, darkFabric, metal, matteDark);

        Geometries rugGroup = new Geometries();
        addRug(rugGroup, rug);

        Geometries coffeeTableGroup = new Geometries();
        addCoffeeTable(coffeeTableGroup, blackWood, metal, matteDark, darkGloss);

        Geometries wallArtGroup = new Geometries();
        addWallArt(wallArtGroup, metal, tvScreen, wood, matteDark);

        Geometries ceilingSpotsGroup = new Geometries();
        addCeilingSpots(ceilingSpotsGroup, metal, glass);

        // Add distinct high-level structures to main scene composite tree
        scene.geometries.add(
                roomGroup,
                windowCabinetGroup,
                floorLampGroup,
                armchairGroup,
                rugGroup,
                coffeeTableGroup,
                wallArtGroup,
                ceilingSpotsGroup
        );

        addLights(scene);

        return scene;
    }

    private void addRoom(Geometries container, Material wall, Material ceiling, Material floor, Material matteDark) {
        container.add(
                new Polygon(new Point(-980, -120, 560), new Point(980, -120, 560), new Point(980, -120, -1150), new Point(-980, -120, -1150)).setEmission(new Color(72, 49, 31)).setMaterial(floor),
                new Polygon(new Point(-980, 410, 560), new Point(-980, 410, -1150), new Point(980, 410, -1150), new Point(980, 410, 560)).setEmission(new Color(84, 65, 54)).setMaterial(ceiling),
                new Polygon(new Point(-980, -120, -880), new Point(980, -120, -880), new Point(980, 410, -880), new Point(-980, 410, -880)).setEmission(new Color(118, 104, 95)).setMaterial(wall),
                new Polygon(new Point(-980, -120, 560), new Point(-980, -120, -1150), new Point(-980, 410, -1150), new Point(-980, 410, 560)).setEmission(new Color(45, 36, 31)).setMaterial(wall)
        );

        addBox(container, -982, -972, -120, 410, -888, -872, new Color(28, 23, 20), matteDark);

        for (int i = 0; i < 42; i++) {
            double z1 = 535 - i * 40.5;
            double z2 = z1 - 31.5;
            Color plank = i % 2 == 0 ? new Color(90, 58, 37) : new Color(77, 49, 31);
            addBox(container, -980, 980, -119.2, -118.45, z2, z1, plank, floor);
        }

        for (int i = 0; i < 41; i++) {
            double z = 519 - i * 40.5;
            addBox(container, -980, 980, -118.44, -118.24, z - 0.9, z + 0.9, new Color(43, 28, 19), matteDark);
        }

        addBox(container, -980, 980, -102, -94, -876, -868, new Color(112, 86, 62), matteDark);
    }

    private void addLeftWallWindowAndCabinet(Geometries container, Material glass, Material blackWood, Material matteDark) {
        container.add(
                new Polygon(new Point(-1200, -500, 1000), new Point(-1200, -500, -1500), new Point(-1200, 800, -1500), new Point(-1200, 800, 1000)).setEmission(new Color(135, 206, 235)).setMaterial(matteDark)
        );

        addBox(container, -979, -971, -88, 325, -720, 380, new Color(5, 10, 15), glass);

        addBox(container, -981, -968, -100, -88, -735, 395, new Color(28, 22, 18), blackWood);
        addBox(container, -981, -968, 325, 337, -735, 395, new Color(28, 22, 18), blackWood);
        addBox(container, -981, -968, -100, 337, -735, -720, new Color(28, 22, 18), blackWood);
        addBox(container, -981, -968, -100, 337, 380, 395, new Color(28, 22, 18), blackWood);

        addBox(container, -982, -966, -88, 325, -370, -355, new Color(58, 51, 44), matteDark);
        addBox(container, -982, -966, -88, 325, -10, 5, new Color(58, 51, 44), matteDark);

        addBox(container, -982, -966, 45, 57, -720, 380, new Color(58, 51, 44), matteDark);
        addBox(container, -982, -966, 180, 192, -720, 380, new Color(58, 51, 44), matteDark);
    }

    private void addFloorLamp(Geometries container, Material metal, Material shade) {
        Color legColor = new Color(17, 15, 14);
        Point hub = new Point(-650, 166, -430);

        container.add(
                new Cylinder(5, new Ray(new Point(-700, -120, -460), new Vector(50, 286, 30).normalize()), 292).setEmission(legColor).setMaterial(metal),
                new Cylinder(5, new Ray(new Point(-650, -120, -390), new Vector(0, 286, -40).normalize()), 289).setEmission(legColor).setMaterial(metal),
                new Cylinder(5, new Ray(new Point(-604, -120, -456), new Vector(-46, 286, 26).normalize()), 291).setEmission(legColor).setMaterial(metal),
                new Sphere(hub, 9).setEmission(legColor).setMaterial(metal),
                new Cylinder(62, new Ray(new Point(-650, 188, -430), new Vector(0, 1, 0)), 66).setEmission(new Color(226, 205, 115)).setMaterial(shade),
                new Sphere(new Point(-650, 188, -430), 17).setEmission(new Color(255, 242, 186)).setMaterial(new Material().setKD(0.2).setKS(0.2).setShininess(10))
        );
    }

    private void addArmchairRotated(Geometries container, Material fabric, Material darkFabric, Material metal, Material matteDark) {
        Color chairMain = new Color(182, 165, 142);
        Color cushion = new Color(226, 216, 202);
        Color frontBand = new Color(150, 132, 112);
        Color seam = new Color(106, 90, 76);
        Color leg = new Color(36, 30, 26);

        double pivotX = -460;
        double pivotZ = -280;
        double angle = -14;

        addRotatedBoxRelative(container, -590, -390, -82, -42, -338, -226, pivotX, pivotZ, angle, chairMain, fabric);
        addRotatedBoxRelative(container, -574, -406, -42, -10, -322, -238, pivotX, pivotZ, angle, cushion, fabric);
        addRotatedBoxRelative(container, -568, -412, -10, -6, -316, -244, pivotX, pivotZ, angle, new Color(234, 224, 210), fabric);
        addRotatedBoxRelative(container, -604, -376, -20, 108, -398, -350, pivotX, pivotZ, angle, chairMain, fabric);
        addRotatedBoxRelative(container, -618, -564, -82, 52, -348, -214, pivotX, pivotZ, angle, chairMain, fabric);
        addRotatedBoxRelative(container, -416, -362, -82, 52, -348, -214, pivotX, pivotZ, angle, chairMain, fabric);
        addRotatedBoxRelative(container, -578, -402, -84, -58, -224, -194, pivotX, pivotZ, angle, frontBand, darkFabric);
        addRotatedBoxRelative(container, -578, -402, -58, -53, -224, -194, pivotX, pivotZ, angle, seam, matteDark);

        addRotatedCylinder(container, 6.5, -585, -120, -324, 0, 1, 0, 42, pivotX, pivotZ, angle, leg, metal);
        addRotatedCylinder(container, 6.5, -394, -120, -324, 0, 1, 0, 42, pivotX, pivotZ, angle, leg, metal);
        addRotatedCylinder(container, 6.5, -574, -120, -216, 0, 1, 0, 42, pivotX, pivotZ, angle, leg, metal);
        addRotatedCylinder(container, 6.5, -398, -120, -204, 0, 1, 0, 42, pivotX, pivotZ, angle, leg, metal);
    }

    private void addRug(Geometries container, Material rug) {
        addBox(container, -430, 760, -119.5, -114.0, -630, 55, new Color(190, 175, 160), rug);
        for (int i = 0; i < 6; i++) {
            double z = -590 + i * 105;
            addBox(container, -400, 730, -113.8, -113.2, z, z + 2, new Color(175, 160, 145), rug);
        }
        for (int i = 0; i < 7; i++) {
            double x = -370 + i * 160;
            addBox(container, x, x + 2, -113.8, -113.2, -605, 35, new Color(175, 160, 145), rug);
        }
    }

    private void addCoffeeTable(Geometries container, Material blackWood, Material metal, Material matteDark, Material topMaterial) {
        double cx = -120;
        double cz = -170;

        container.add(
                new Cylinder(124, new Ray(new Point(cx, -72, cz), new Vector(0, 1, 0)), 12).setEmission(new Color(16, 12, 10)).setMaterial(topMaterial),
                new Cylinder(6.5, new Ray(new Point(cx - 64, -120, cz + 24), new Vector(0, 1, 0)), 42).setEmission(new Color(17, 16, 15)).setMaterial(metal),
                new Cylinder(6.5, new Ray(new Point(cx + 64, -120, cz + 24), new Vector(0, 1, 0)), 42).setEmission(new Color(17, 16, 15)).setMaterial(metal),
                new Cylinder(6.5, new Ray(new Point(cx - 64, -120, cz - 32), new Vector(0, 1, 0)), 42).setEmission(new Color(17, 16, 15)).setMaterial(metal),
                new Cylinder(6.5, new Ray(new Point(cx + 64, -120, cz - 32), new Vector(0, 1, 0)), 42).setEmission(new Color(17, 16, 15)).setMaterial(metal),
                new Cylinder(3.0, new Ray(new Point(cx + 58, -54, cz + 18), new Vector(1, 0, -0.12).normalize()), 54).setEmission(new Color(14, 14, 14)).setMaterial(new Material().setKD(0.55).setKS(0.25).setKR(0.03).setShininess(60))
        );

        addPlant(container, matteDark);
    }

    private void addPlant(Geometries container, Material branchMat) {
        Color stem = new Color(58, 48, 36);
        Color flower = new Color(40, 140, 60);
        Material vaseGlass = new Material().setKD(0.10).setKS(0.55).setKT(0.45).setKR(0.10).setShininess(160);

        double baseX = -135;
        double baseZ = -170;

        container.add(
                new Cylinder(15, new Ray(new Point(baseX, -68, baseZ), new Vector(0, 1, 0)), 34).setEmission(new Color(205, 214, 224)).setMaterial(vaseGlass),
                new Sphere(new Point(baseX + 4, -45, baseZ - 6), 5).setEmission(new Color(235, 242, 248)).setMaterial(vaseGlass)
        );

        container.add(
                new Cylinder(2.3, new Ray(new Point(baseX, -38, baseZ), new Vector(-0.12, 1, -0.05).normalize()), 78).setEmission(stem).setMaterial(branchMat),
                new Cylinder(2.3, new Ray(new Point(baseX, -38, baseZ), new Vector(0.00, 1, -0.10).normalize()), 86).setEmission(stem).setMaterial(branchMat),
                new Cylinder(2.3, new Ray(new Point(baseX, -38, baseZ), new Vector(0.12, 1, -0.03).normalize()), 82).setEmission(stem).setMaterial(branchMat),
                new Cylinder(2.3, new Ray(new Point(baseX, -38, baseZ), new Vector(-0.08, 1, 0.08).normalize()), 74).setEmission(stem).setMaterial(branchMat),
                new Cylinder(2.3, new Ray(new Point(baseX, -38, baseZ), new Vector(0.08, 1, 0.06).normalize()), 78).setEmission(stem).setMaterial(branchMat)
        );

        addLeafCluster(container, baseX - 12, 42, baseZ - 5, 8, flower, branchMat);
        addLeafCluster(container, baseX, 50, baseZ - 12, 9, flower, branchMat);
        addLeafCluster(container, baseX + 13, 46, baseZ - 3, 8, flower, branchMat);
        addLeafCluster(container, baseX - 8, 38, baseZ + 8, 7, flower, branchMat);
        addLeafCluster(container, baseX + 8, 42, baseZ + 6, 7, flower, branchMat);
    }

    private void addWallArt(Geometries container, Material metal, Material tvScreen, Material wood, Material matteDark) {
        addBox(container, -240, 140, 72, 228, -878, -870, new Color(14, 14, 14), metal);
        addBox(container, -224, 124, 86, 214, -869, -865, new Color(3, 3, 3), tvScreen);
        addBox(container, -84, -16, 56, 62, -872, -852, new Color(20, 18, 18), metal);
        addBox(container, -106, 6, 274, 282, -866, -850, new Color(42, 38, 34), metal);
        addBox(container, -58, -42, 250, 274, -862, -846, new Color(56, 49, 42), metal);
        addBox(container, -300, 200, -120, -60, -880, -780, new Color(30, 25, 22), matteDark);
        addBox(container, -305, 205, -60, -55, -885, -775, new Color(45, 38, 32), wood);
        addBox(container, -290, -55, -115, -65, -785, -775, new Color(55, 48, 42), wood);
        addBox(container, -45, 190, -115, -65, -785, -775, new Color(55, 48, 42), wood);
    }

    private void addCeilingSpots(Geometries container, Material metal, Material glass) {
        double[] xs = {-620, -280, 60, 400, 740};
        for (double x : xs) {
            container.add(
                    new Cylinder(20, new Ray(new Point(x, 388, -445), new Vector(0, -1, 0)), 10).setEmission(new Color(60, 55, 47)).setMaterial(metal),
                    new Sphere(new Point(x, 372, -445), 12).setEmission(new Color(240, 220, 180)).setMaterial(glass)
            );
        }
    }

    private void addLights(Scene scene) {
        scene.lights.add(new DirectionalLight(new Color(4, 4, 5), new Vector(0.95, -0.32, -0.18)));
        scene.lights.add(new PointLight(new Color(18, 20, 24), new Point(120, 315, -220)).setKl(0.00025).setKq(0.000003).setRadius(180));
        scene.lights.add(new PointLight(new Color(480, 340, 190), new Point(-650, 196, -430)).setKl(0.0015).setKq(0.000008).setRadius(105));
        scene.lights.add(new SpotLight(new Color(230, 172, 98), new Point(-650, 198, -430), new Vector(0.42, -0.12, -0.90)).setKl(0.0015).setKq(0.000008).setRadius(60).setNarrowBeam(3.0));
        scene.lights.add(new SpotLight(new Color(400, 365, 295), new Point(-50, 304, -810), new Vector(0, -0.24, -1)).setKl(0.0012).setKq(0.000006).setRadius(64).setNarrowBeam(3.0));
        scene.lights.add(new SpotLight(new Color(255, 245, 220), new Point(-975, 230, 160), new Vector(1.0, -0.25, -0.15)).setKl(0.0012).setKq(0.000006).setRadius(150).setNarrowBeam(2.2));
        scene.lights.add(new SpotLight(new Color(188, 142, 94), new Point(-620, 372, -445), new Vector(0.28, -1, -0.26)).setKl(0.0013).setKq(0.000007).setRadius(64).setNarrowBeam(2.5));
        scene.lights.add(new SpotLight(new Color(178, 134, 88), new Point(-280, 372, -445), new Vector(0.16, -1, -0.28)).setKl(0.0013).setKq(0.000007).setRadius(64).setNarrowBeam(2.5));
        scene.lights.add(new SpotLight(new Color(171, 128, 84), new Point(60, 372, -445), new Vector(0.04, -1, -0.30)).setKl(0.0013).setKq(0.000007).setRadius(64).setNarrowBeam(2.5));
        scene.lights.add(new SpotLight(new Color(165, 122, 80), new Point(400, 372, -445), new Vector(-0.08, -1, -0.28)).setKl(0.0013).setKq(0.000007).setRadius(64).setNarrowBeam(2.5));
        scene.lights.add(new SpotLight(new Color(160, 118, 76), new Point(740, 372, -445), new Vector(-0.22, -1, -0.24)).setKl(0.0013).setKq(0.000007).setRadius(64).setNarrowBeam(2.5));
        scene.lights.add(new PointLight(new Color(75, 80, 90), new Point(320, 85, -270)).setKl(0.0001).setKq(0.000001).setRadius(150));
    }

    private void addOutlineBox(Geometries container, double x1, double x2, double y1, double y2, double z1, double z2, double t, Color color, Material material) {
        addBox(container, x1, x2, y1, y1 + t, z1, z1 + t, color, material);
        addBox(container, x1, x2, y2 - t, y2, z1, z1 + t, color, material);
        addBox(container, x1, x1 + t, y1, y2, z1, z1 + t, color, material);
        addBox(container, x2 - t, x2, y1, y2, z1, z1 + t, color, material);
    }

    private void addRotatedBox(Geometries container, double centerX, double centerY, double centerZ, double sizeX, double sizeY, double sizeZ, double angleDegrees, Color color, Material material) {
        double hx = sizeX / 2.0;
        double hy = sizeY / 2.0;
        double hz = sizeZ / 2.0;

        Point p000 = rotateLocal(centerX, centerY, centerZ, -hx, -hy, -hz, angleDegrees);
        Point p001 = rotateLocal(centerX, centerY, centerZ, -hx, -hy, hz, angleDegrees);
        Point p010 = rotateLocal(centerX, centerY, centerZ, -hx, hy, -hz, angleDegrees);
        Point p011 = rotateLocal(centerX, centerY, centerZ, -hx, hy, hz, angleDegrees);
        Point p100 = rotateLocal(centerX, centerY, centerZ, hx, -hy, -hz, angleDegrees);
        Point p101 = rotateLocal(centerX, centerY, centerZ, hx, -hy, hz, angleDegrees);
        Point p110 = rotateLocal(centerX, centerY, centerZ, hx, hy, -hz, angleDegrees);
        Point p111 = rotateLocal(centerX, centerY, centerZ, hx, hy, hz, angleDegrees);

        container.add(
                new Polygon(p000, p100, p101, p001).setEmission(color).setMaterial(material),
                new Polygon(p010, p011, p111, p110).setEmission(color).setMaterial(material),
                new Polygon(p000, p001, p011, p010).setEmission(color).setMaterial(material),
                new Polygon(p100, p110, p111, p101).setEmission(color).setMaterial(material),
                new Polygon(p000, p010, p110, p100).setEmission(color).setMaterial(material),
                new Polygon(p001, p101, p111, p011).setEmission(color).setMaterial(material)
        );
    }

    private Point rotateLocal(double centerX, double centerY, double centerZ, double dx, double dy, double dz, double angleDegrees) {
        double angle = Math.toRadians(angleDegrees);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double rx = dx * cos - dz * sin;
        double rz = dx * sin + dz * cos;
        return new Point(centerX + rx, centerY + dy, centerZ + rz);
    }

    private Point rotatePoint(double x, double y, double z, double centerX, double centerY, double centerZ, double angleDegrees) {
        double dx = x - centerX;
        double dy = y - centerY;
        double dz = z - centerZ;
        double angle = Math.toRadians(angleDegrees);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double rx = dx * cos - dz * sin;
        double rz = dx * sin + dz * cos;
        return new Point(centerX + rx, centerY + dy, centerZ + rz);
    }

    private void addBox(Geometries container, double x1, double x2, double y1, double y2, double z1, double z2, Color color, Material material) {
        container.add(
                new Polygon(new Point(x1, y1, z1), new Point(x2, y1, z1), new Point(x2, y1, z2), new Point(x1, y1, z2)).setEmission(color).setMaterial(material),
                new Polygon(new Point(x1, y2, z1), new Point(x1, y2, z2), new Point(x2, y2, z2), new Point(x2, y2, z1)).setEmission(color).setMaterial(material),
                new Polygon(new Point(x1, y1, z1), new Point(x1, y1, z2), new Point(x1, y2, z2), new Point(x1, y2, z1)).setEmission(color).setMaterial(material),
                new Polygon(new Point(x2, y1, z1), new Point(x2, y2, z1), new Point(x2, y2, z2), new Point(x2, y1, z2)).setEmission(color).setMaterial(material),
                new Polygon(new Point(x1, y1, z1), new Point(x1, y2, z1), new Point(x2, y2, z1), new Point(x2, y1, z1)).setEmission(color).setMaterial(material),
                new Polygon(new Point(x1, y1, z2), new Point(x2, y1, z2), new Point(x2, y2, z2), new Point(x1, y2, z2)).setEmission(color).setMaterial(material)
        );
    }

    private void addRotatedBoxRelative(Geometries container, double x1, double x2, double y1, double y2, double z1, double z2, double pivotX, double pivotZ, double angleDegrees, Color color, Material material) {
        Point p000 = rotatePoint(x1, y1, z1, pivotX, 0, pivotZ, angleDegrees);
        Point p100 = rotatePoint(x2, y1, z1, pivotX, 0, pivotZ, angleDegrees);
        Point p101 = rotatePoint(x2, y1, z2, pivotX, 0, pivotZ, angleDegrees);
        Point p001 = rotatePoint(x1, y1, z2, pivotX, 0, pivotZ, angleDegrees);

        Point p010 = rotatePoint(x1, y2, z1, pivotX, 0, pivotZ, angleDegrees);
        Point p110 = rotatePoint(x2, y2, z1, pivotX, 0, pivotZ, angleDegrees);
        Point p111 = rotatePoint(x2, y2, z2, pivotX, 0, pivotZ, angleDegrees);
        Point p011 = rotatePoint(x1, y2, z2, pivotX, 0, pivotZ, angleDegrees);

        container.add(
                new Polygon(p000, p100, p101, p001).setEmission(color).setMaterial(material),
                new Polygon(p010, p011, p111, p110).setEmission(color).setMaterial(material),
                new Polygon(p000, p001, p011, p010).setEmission(color).setMaterial(material),
                new Polygon(p100, p110, p111, p101).setEmission(color).setMaterial(material),
                new Polygon(p000, p010, p110, p100).setEmission(color).setMaterial(material),
                new Polygon(p001, p101, p111, p011).setEmission(color).setMaterial(material)
        );
    }

    private void addRotatedCylinder(Geometries container, double radius, double x, double y, double z, double dirX, double dirY, double dirZ, double height, double pivotX, double pivotZ, double angleDegrees, Color color, Material material) {
        Point p = rotatePoint(x, y, z, pivotX, 0, pivotZ, angleDegrees);
        double angle = Math.toRadians(angleDegrees);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double rDirX = dirX * cos - dirZ * sin;
        double rDirZ = dirX * sin + dirZ * cos;

        container.add(
                new Cylinder(radius, new Ray(p, new Vector(rDirX, dirY, rDirZ).normalize()), height).setEmission(color).setMaterial(material)
        );
    }

    private void addLeafCluster(Geometries container, double cx, double cy, double cz, double r, Color color, Material mat) {
        container.add(
                new Sphere(new Point(cx, cy, cz), r).setEmission(color).setMaterial(mat),
                new Sphere(new Point(cx + r, cy + r / 2, cz - r / 2), r * 0.9).setEmission(color).setMaterial(mat),
                new Sphere(new Point(cx - r, cy + r / 2, cz + r / 2), r * 0.9).setEmission(color).setMaterial(mat),
                new Sphere(new Point(cx, cy + r, cz + r), r * 0.9).setEmission(color).setMaterial(mat),
                new Sphere(new Point(cx, cy - r / 2, cz - r), r * 0.9).setEmission(color).setMaterial(mat)
        );
    }
}