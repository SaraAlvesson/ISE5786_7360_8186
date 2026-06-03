package renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.sampling.Blackboard;
import renderer.sampling.TargetArea;
import scene.Scene;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Camera class representing a camera in a 3D scene.
 * The camera is responsible for generating rays through a View Plane and rendering the image.
 * Built using a Builder pattern to ensure proper initialization.
 */
public class Camera implements Cloneable {
    /**
     * Camera location point (P0)
     */
    private Point p0;
    /**
     * Up vector (Vup), Direction vector (Vto), and Right vector (Vright)
     */
    private Vector vUp, vTo, vRight;
    /**
     * View Plane width and height
     */
    private double width, height;
    /**
     * Distance from camera to View Plane
     */
    private double distance;
    /**
     * Resolution of the image (Number of pixels in X and Y axes)
     */
    private int nX, nY;

    /**
     * Auxiliary calculated field: Center point of the View Plane
     */
    private Point vpCenter;
    /**
     * Auxiliary calculated field: Width of a single pixel
     */
    private double pixelWidth;
    /**
     * Auxiliary calculated field: Height of a single pixel
     */
    private double pixelHeight;

    /**
     * ImageWriter component for pixel management and file output
     */
    private ImageWriter imageWriter;
    /**
     * RayTracer component for calculating color for each ray
     */
    private RayTracerBase rayTracer;

    /**
     * Super-Sampling (Anti-Aliasing) number of samples per dimension (NxN grid).
     * Default value is 1, which means a single central ray is used (feature disabled).
     */
    private int antiAliasingSamples = 1;

    /**
     * Multi-threading: number of threads to use for rendering.
     * Default value is 1, which means single-threaded synchronous execution.
     */
    private int threadsCount = 1;

    /**
     * Multi-threading: progress print interval in seconds.
     * Default value is 0, meaning progress printing is disabled.
     */
    private double printInterval = 0.0;

    /**
     * Default private constructor for Builder use only.
     */
    private Camera() {
    }

    /**
     * Renders the image by iterating through all pixels in the resolution.
     * Supports both single-threaded execution and multi-threaded parallel execution
     * via the PixelManager helper component.
     *
     * @return current camera object for chaining
     */
    public Camera renderImage() {
        // If single thread is requested, run synchronously without manager overhead
        if (threadsCount == 1) {
            for (int i = 0; i < nY; i++) {
                for (int j = 0; j < nX; j++) {
                    castRay(j, i);
                }
            }
            return this;
        }

        // Initialize the PixelManager for thread-safe work allocation
        PixelManager pixelManager = new PixelManager(nY, nX, printInterval);

        // Create and populate the list of workers/threads
        List<Thread> threads = new ArrayList<>();
        for (int t = 0; t < threadsCount; t++) {
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                // Keep asking for the next available pixel until none are left
                while ((pixel = pixelManager.nextPixel()) != null) {
                    castRay(pixel.col(), pixel.row());
                    pixelManager.pixelDone();
                }
            }));
        }

        // Start execution for all created threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Main thread waits for all rendering threads to complete their work
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Rendering execution was interrupted", e);
        }

        return this;
    }

    /**
     * Private helper method to cast a ray (or a beam of rays if Anti-Aliasing is enabled)
     * through a specific pixel, trace its color, and write it to the ImageWriter.
     *
     * @param j column index in the pixel grid
     * @param i row index in the pixel grid
     */
    private void castRay(int j, int i) {
        // If Anti-Aliasing is disabled (samples == 1), perform standard single ray tracing
        if (antiAliasingSamples == 1) {
            Ray ray = constructRay(j, i);
            Color color = rayTracer.traceRay(ray);
            imageWriter.writePixel(j, i, color);
            return;
        }

        // Calculate the physical center point of the current pixel on the View Plane
        double xJ = (j - (nX - 1) / 2.0) * pixelWidth;
        double yI = -(i - (nY - 1) / 2.0) * pixelHeight;
        Point pixelCenter = vpCenter;
        if (!isZero(xJ)) {
            pixelCenter = pixelCenter.add(vRight.scale(xJ));
        }
        if (!isZero(yI)) {
            pixelCenter = pixelCenter.add(vUp.scale(yI));
        }

        // Initialize Blackboard to generate 2D offsets inside a SQUARE (the pixel shape)
        Blackboard blackboard = new Blackboard()
                .setShape(Blackboard.AreaShape.SQUARE)
                .setNumOfSamples(antiAliasingSamples);

        // Map the 2D grid offsets into 3D points on the View Plane across the single pixel area
        List<Point> targetPoints = TargetArea.convertTo3DPoints(
                pixelCenter,
                vTo,
                pixelWidth, // Using pixel width as the size of the target area bounding box
                blackboard.generate2DPoints()
        );

        // Generate a beam of rays from the camera's eye position (P0) to all pixel target points
        List<Ray> beam = TargetArea.generateBeam(p0, targetPoints);

        // Accumulate and average the colors traced by all rays in the beam
        Color colorSum = Color.BLACK;
        for (Ray beamRay : beam) {
            colorSum = colorSum.add(rayTracer.traceRay(beamRay));
        }

        Color finalColor = colorSum.reduce(beam.size());
        imageWriter.writePixel(j, i, finalColor);
    }

    /**
     * Adds a grid of lines to the existing image buffer.
     *
     * @param interval the distance between grid lines (in pixels)
     * @param color    the color of the grid lines
     * @return current camera object for chaining
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    imageWriter.writePixel(j, i, color);
                }
            }
        }
        return this;
    }

    /**
     * Delegates the final image file creation to the ImageWriter.
     *
     * @param fileName name of the output image file (without extension)
     */
    public void writeToImage(String fileName) {
        imageWriter.writeToImage(fileName);
    }

    /**
     * Constructs a ray from the camera location (P0) through the center
     * of a specific pixel (j, i) on the View Plane.
     *
     * @param j column index (x index)
     * @param i row index (y index)
     * @return Ray starting from camera location through pixel center
     */
    public Ray constructRay(int j, int i) {
        // Pixel coordinates calculation relative to View Plane center
        double xJ = (j - (nX - 1) / 2.0) * pixelWidth;
        double yI = -(i - (nY - 1) / 2.0) * pixelHeight;

        Point pIJ = vpCenter;

        // Apply horizontal displacement
        if (!isZero(xJ)) {
            pIJ = pIJ.add(vRight.scale(xJ));
        }
        // Apply vertical displacement
        if (!isZero(yI)) {
            pIJ = pIJ.add(vUp.scale(yI));
        }

        // Ray starts from camera location P0 and goes towards calculated point pIJ
        return new Ray(p0, pIJ.subtract(p0));
    }

    @Override
    public Camera clone() {
        try {
            return (Camera) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Builder static nested class for the Camera.
     * Implements the Builder Pattern for safe and flexible Camera creation.
     */
    public static class Builder {
        /**
         * Camera instance being built
         */
        private final Camera camera = new Camera();
        /**
         * Temporary storage for Up vector during construction
         */
        private Vector tempUp = null;
        /**
         * Temporary storage for Target point during construction
         */
        private Point tempTarget = null;
        /**
         * Rotation angle for bonus implementation
         */
        private double rotationAngle = 0;
        /**
         * Soft shadows samples across one dimension (NxN grid)
         */
        private int softShadowsSamples = 1;

        /**
         * Builder constructor, initializes default resolution to 1x1.
         */
        public Builder() {
            camera.nX = 1;
            camera.nY = 1;
        }

        /**
         * Sets the camera's location.
         *
         * @param p0 origin point
         * @return current builder
         */
        public Builder setLocation(Point p0) {
            camera.p0 = p0;
            return this;
        }

        /**
         * Sets the camera's viewing direction and up vector.
         *
         * @param vTo direction vector
         * @param vUp up vector
         * @return current builder
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            camera.vTo = vTo;
            this.tempUp = vUp;
            return this;
        }

        /**
         * Sets the camera's direction by pointing at a target and setting an up vector.
         *
         * @param target point to look at
         * @param vUp    up vector
         * @return current builder
         */
        public Builder setDirection(Point target, Vector vUp) {
            this.tempTarget = target;
            this.tempUp = vUp;
            return this;
        }

        /**
         * Sets the camera's direction by pointing at a target point.
         *
         * @param target point to look at
         * @return current builder
         */
        public Builder setDirection(Point target) {
            this.tempTarget = target;
            return this;
        }

        /**
         * Sets the View Plane dimensions.
         *
         * @param width  total width
         * @param height total height
         * @return current builder
         */
        public Builder setVpSize(double width, double height) {
            camera.width = width;
            camera.height = height;
            return this;
        }

        /**
         * Sets the distance between the camera and the View Plane.
         *
         * @param distance distance from P0
         * @return current builder
         */
        public Builder setVpDistance(double distance) {
            camera.distance = distance;
            return this;
        }

        /**
         * Sets the resolution of the generated image.
         *
         * @param nX number of pixels in the width
         * @param nY number of pixels in the height
         * @return current builder
         */
        public Builder setResolution(int nX, int nY) {
            camera.nX = nX;
            camera.nY = nY;
            return this;
        }

        /**
         * Sets the ray tracer component for the camera.
         *
         * @param scene the scene to be rendered
         * @param type  the algorithm type for ray tracing
         * @return current builder
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                SimpleRayTracer tracer = new SimpleRayTracer(scene);
                tracer.setSoftShadowsSamples(this.softShadowsSamples); // Inject soft shadow samples automatically!
                camera.rayTracer = tracer;
            } else {
                throw new IllegalArgumentException("Unsupported ray tracer type");
            }
            return this;
        }

        /**
         * Sets the number of samples across one dimension (NxN grid) for Anti-Aliasing.
         * A value of 1 signifies that the feature is turned off.
         *
         * @param samples number of samples per row/column
         * @return current builder
         */
        public Builder setAntiAliasingSamples(int samples) {
            if (samples < 1) {
                throw new IllegalArgumentException("Number of samples must be at least 1");
            }
            camera.antiAliasingSamples = samples;
            return this;
        }

        /**
         * Sets the number of soft shadow samples (NxN grid).
         * A value of 1 signifies that the feature is turned off.
         *
         * @param samples number of samples per row/column
         * @return current builder
         */
        public Builder setSoftShadowsSamples(int samples) {
            if (samples < 1) {
                throw new IllegalArgumentException("Number of samples must be at least 1");
            }
            this.softShadowsSamples = samples;
            return this;
        }

        /**
         * Sets the multi-threading configuration.
         *
         * @param threads  number of concurrent threads to spawn. 1 means disabled.
         * @param interval progress updates print interval in seconds (0 means disabled).
         * @return current builder
         */
        public Builder setMultiThreading(int threads, double interval) {
            if (threads < 1) {
                throw new IllegalArgumentException("Number of threads must be at least 1");
            }
            camera.threadsCount = threads;
            camera.printInterval = interval;
            return this;
        }

        /**
         * Rotates the camera coordinate system around the viewing direction.
         *
         * @param angle rotation angle in degrees (clockwise)
         * @return current builder
         */
        public Builder rotate(double angle) {
            this.rotationAngle = angle;
            return this;
        }

        /**
         * Validates all parameters, calculates auxiliary fields, and builds the Camera object.
         *
         * @return a fully initialized clone of the camera
         * @throws RuntimeException if building or cloning fails
         */
        public Camera build() {
            if (camera.rayTracer == null) {
                setRayTracer(new Scene("test"), RayTracerType.SIMPLE);
            }

            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            Camera builtCamera = camera.clone();
            if (builtCamera == null) throw new RuntimeException("Clone failed");
            return builtCamera;
        }

        /**
         * Validates resolution and initializes the ImageWriter.
         */
        private void checkResolution() {
            if (camera.nX <= 0 || camera.nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");

            camera.imageWriter = new ImageWriter(camera.nX, camera.nY);
        }

        /**
         * Validates location, calculates the camera's coordinate system (Vto, Vright, Vup),
         * and applies rotation if necessary.
         */
        private void checkLocationAndDirection() {
            if (camera.p0 == null)
                throw new MissingResourceException("Missing location", "Camera", "p0");

            if (camera.vTo == null) {
                if (tempTarget == null) throw new MissingResourceException("Missing direction", "Camera", "vTo");
                camera.vTo = tempTarget.subtract(camera.p0);
            }
            camera.vTo = camera.vTo.normalize();

            Vector vUpGeneral = (tempUp == null) ? Vector.AXIS_Y : tempUp;

            try {
                camera.vRight = camera.vTo.crossProduct(vUpGeneral).normalize();
                camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("vTo and vUp cannot be parallel");
            }

            // Apply clockwise rotation bonus
            if (!isZero(rotationAngle)) {
                double rad = Math.toRadians(rotationAngle);
                double cos = alignZero(Math.cos(rad));
                double sin = alignZero(Math.sin(rad));

                Vector vRightOld = camera.vRight;
                Vector vUpOld = camera.vUp;

                if (isZero(cos)) {
                    camera.vRight = vUpOld.scale(-sin).normalize();
                } else if (isZero(sin)) {
                    camera.vRight = vRightOld.scale(cos).normalize();
                } else {
                    camera.vRight = vRightOld.scale(cos).subtract(vUpOld.scale(sin)).normalize();
                }

                camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();
            }
        }

        /**
         * Validates View Plane parameters and calculates pixel dimensions and VP center.
         */
        private void checkViewPlane() {
            if (isZero(camera.distance) || camera.distance < 0)
                throw new IllegalArgumentException("Distance must be positive");
            if (isZero(camera.width) || isZero(camera.height) || camera.width < 0 || camera.height < 0)
                throw new IllegalArgumentException("VP Size must be positive");

            camera.pixelWidth = camera.width / camera.nX;
            camera.pixelHeight = camera.height / camera.nY;
            camera.vpCenter = camera.p0.add(camera.vTo.scale(camera.distance));
        }
    }

    /**
     * Factory method for creating a new Camera Builder.
     *
     * @return a new Builder instance
     */
    public static Builder getBuilder() {
        return new Builder();
    }
}