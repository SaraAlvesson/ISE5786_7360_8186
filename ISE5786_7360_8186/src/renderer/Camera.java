package renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.IntStream;
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
     * Resolution of the image.
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
     * Super-Sampling Anti-Aliasing number of samples per dimension.
     * Default value is 1, which means a single central ray is used.
     */
    private int antiAliasingSamples = 1;

    /**
     * Anti-Aliasing Blackboard shape.
     */
    private Blackboard.AreaShape antiAliasingShape = Blackboard.AreaShape.SQUARE;

    /**
     * Anti-Aliasing Blackboard sample pattern.
     */
    private Blackboard.SamplePattern antiAliasingPattern = Blackboard.SamplePattern.GRID;

    /**
     * Stage 10 multi-threading mode:
     * 0  = no threads
     * -1 = parallel streams
     * -2 = automatic raw threads, converted in Builder
     * 1+ = exact raw threads count
     */
    private int threadsCount = 0;

    /**
     * Spare threads when automatic raw threads are requested.
     */
    private static final int SPARE_THREADS = 2;

    /**
     * Progress print interval.
     * 0 means no progress printing.
     */
    private double printInterval = 0.0;

    /**
     * Pixel manager for thread-safe pixel allocation and progress printing.
     */
    private PixelManager pixelManager;

    /**
     * Default private constructor for Builder use only.
     */
    private Camera() {
    }

    /**
     * Renders the image according to the selected multi-threading mode.
     *
     * @return current camera object for chaining
     */
    public Camera renderImage() {
        pixelManager = new PixelManager(nY, nX, printInterval);

        return switch (threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    /**
     * Rendering without multi-threading.
     *
     * @return current camera object for chaining
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                castRay(j, i);
            }
        }
        return this;
    }

    /**
     * Rendering with clever raw threads.
     *
     * @return current camera object for chaining
     */
    private Camera renderImageRawThreads() {
        List<Thread> threads = new ArrayList<>();

        for (int t = 0; t < threadsCount; t++) {
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null) {
                    castRay(pixel.row(), pixel.col());
                }
            }));
        }

        for (Thread thread : threads) {
            thread.start();
        }

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
     * Rendering with Java parallel streams.
     *
     * @return current camera object for chaining
     */
    private Camera renderImageStream() {
        IntStream.range(0, nY).parallel()
                .forEach(i -> IntStream.range(0, nX).parallel()
                        .forEach(j -> castRay(j, i)));

        return this;
    }

    /**
     * Private helper method to cast a ray, or a beam of rays if Anti-Aliasing is enabled,
     * through a specific pixel, trace its color, and write it to the ImageWriter.
     *
     * @param j column index in the pixel grid
     * @param i row index in the pixel grid
     */
    private void castRay(int j, int i) {
        try {
            if (antiAliasingSamples == 1) {
                Ray ray = constructRay(j, i);
                Color color = rayTracer.traceRay(ray);
                imageWriter.writePixel(j, i, color);
                return;
            }

            double xJ = (j - (nX - 1) / 2.0) * pixelWidth;
            double yI = -(i - (nY - 1) / 2.0) * pixelHeight;

            Point pixelCenter = vpCenter;

            if (!isZero(xJ)) {
                pixelCenter = pixelCenter.add(vRight.scale(xJ));
            }

            if (!isZero(yI)) {
                pixelCenter = pixelCenter.add(vUp.scale(yI));
            }

            Blackboard blackboard = new Blackboard()
                    .setShape(antiAliasingShape)
                    .setPattern(antiAliasingPattern)
                    .setNumOfSamples(antiAliasingSamples);

            List<Point> targetPoints = TargetArea.convertTo3DPoints(
                    pixelCenter,
                    vTo,
                    pixelWidth,
                    blackboard.generate2DPoints()
            );

            List<Ray> beam = TargetArea.generateBeam(p0, targetPoints);

            Color colorSum = Color.BLACK;
            for (Ray beamRay : beam) {
                colorSum = colorSum.add(rayTracer.traceRay(beamRay));
            }

            Color finalColor = colorSum.reduce(beam.size());
            imageWriter.writePixel(j, i, finalColor);
        } finally {
            if (pixelManager != null) {
                pixelManager.pixelDone();
            }
        }
    }

    /**
     * Adds a grid of lines to the existing image buffer.
     *
     * @param interval the distance between grid lines in pixels
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
     * @param fileName name of the output image file without extension
     */
    public void writeToImage(String fileName) {
        imageWriter.writeToImage(fileName);
    }

    /**
     * Constructs a ray from the camera location through the center of a specific pixel.
     *
     * @param j column index
     * @param i row index
     * @return Ray starting from camera location through pixel center
     */
    public Ray constructRay(int j, int i) {
        double xJ = (j - (nX - 1) / 2.0) * pixelWidth;
        double yI = -(i - (nY - 1) / 2.0) * pixelHeight;

        Point pIJ = vpCenter;

        if (!isZero(xJ)) {
            pIJ = pIJ.add(vRight.scale(xJ));
        }

        if (!isZero(yI)) {
            pIJ = pIJ.add(vUp.scale(yI));
        }

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
     */
    public static class Builder {
        /**
         * Camera instance being built.
         */
        private final Camera camera = new Camera();

        /**
         * Temporary storage for up vector.
         */
        private Vector tempUp = null;

        /**
         * Temporary storage for target point.
         */
        private Point tempTarget = null;

        /**
         * Rotation angle for bonus implementation.
         */
        private double rotationAngle = 0;

        /**
         * Soft shadows samples across one dimension.
         */
        private int softShadowsSamples = 1;

        /**
         * Builder constructor.
         */
        public Builder() {
            camera.nX = 1;
            camera.nY = 1;
        }

        /**
         * Sets the camera location.
         *
         * @param p0 origin point
         * @return current builder
         */
        public Builder setLocation(Point p0) {
            camera.p0 = p0;
            return this;
        }

        /**
         * Sets the camera direction and up vector.
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
         * Sets the camera direction by pointing at a target and setting an up vector.
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
         * Sets the camera direction by pointing at a target point.
         *
         * @param target point to look at
         * @return current builder
         */
        public Builder setDirection(Point target) {
            this.tempTarget = target;
            return this;
        }

        /**
         * Sets the view plane size.
         *
         * @param width  view plane width
         * @param height view plane height
         * @return current builder
         */
        public Builder setVpSize(double width, double height) {
            camera.width = width;
            camera.height = height;
            return this;
        }

        /**
         * Sets the view plane distance.
         *
         * @param distance distance from camera
         * @return current builder
         */
        public Builder setVpDistance(double distance) {
            camera.distance = distance;
            return this;
        }

        /**
         * Sets the image resolution.
         *
         * @param nX number of pixels in width
         * @param nY number of pixels in height
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
         * @param scene scene to render
         * @param type  ray tracer type
         * @return current builder
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                SimpleRayTracer tracer = new SimpleRayTracer(scene);
                tracer.setSoftShadowsSamples(this.softShadowsSamples);
                camera.rayTracer = tracer;
            } else {
                throw new IllegalArgumentException("Unsupported ray tracer type");
            }
            return this;
        }

        /**
         * Sets the number of Anti-Aliasing samples.
         *
         * @param samples number of samples per row and column
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
         * Sets the Anti-Aliasing target area shape.
         *
         * @param shape target area shape
         * @return current builder
         */
        public Builder setAntiAliasingShape(Blackboard.AreaShape shape) {
            camera.antiAliasingShape = shape;
            return this;
        }

        /**
         * Sets the Anti-Aliasing sample pattern.
         *
         * @param pattern sample pattern
         * @return current builder
         */
        public Builder setAntiAliasingPattern(Blackboard.SamplePattern pattern) {
            camera.antiAliasingPattern = pattern;
            return this;
        }

        /**
         * Sets the number of soft shadow samples.
         *
         * @param samples number of samples per row and column
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
         * Stage 10 multi-threading configuration.
         *
         * @param threads 0 means no threads, -1 means parallel streams,
         *                -2 means automatic raw threads, 1+ means exact raw threads count
         * @return current builder
         */
        public Builder setMultithreading(int threads) {
            if (threads < -2) {
                throw new IllegalArgumentException("Multithreading must be -2 or higher");
            }

            if (threads >= -1) {
                camera.threadsCount = threads;
            } else {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                camera.threadsCount = cores <= 2 ? 1 : cores;
            }

            return this;
        }

        /**
         * Sets debug progress print interval.
         *
         * @param interval progress interval, 0 means disabled
         * @return current builder
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0) {
                throw new IllegalArgumentException("Interval value must be non-negative");
            }

            camera.printInterval = interval;
            return this;
        }

        /**
         * Old method kept for backward compatibility with previous tests.
         *
         * @param threads  number of raw threads. 1 means one raw thread
         * @param interval progress print interval
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
         * @param angle rotation angle in degrees
         * @return current builder
         */
        public Builder rotate(double angle) {
            this.rotationAngle = angle;
            return this;
        }

        /**
         * Validates all parameters and builds the Camera object.
         *
         * @return fully initialized camera clone
         */
        public Camera build() {
            if (camera.rayTracer == null) {
                setRayTracer(new Scene("test"), RayTracerType.SIMPLE);
            }

            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            Camera builtCamera = camera.clone();
            if (builtCamera == null) {
                throw new RuntimeException("Clone failed");
            }

            return builtCamera;
        }

        /**
         * Validates resolution and initializes the ImageWriter.
         */
        private void checkResolution() {
            if (camera.nX <= 0 || camera.nY <= 0) {
                throw new IllegalArgumentException("Resolution must be positive");
            }

            camera.imageWriter = new ImageWriter(camera.nX, camera.nY);
        }

        /**
         * Validates location and direction, calculates camera coordinate system,
         * and applies rotation if needed.
         */
        private void checkLocationAndDirection() {
            if (camera.p0 == null) {
                throw new MissingResourceException("Missing location", "Camera", "p0");
            }

            if (camera.vTo == null) {
                if (tempTarget == null) {
                    throw new MissingResourceException("Missing direction", "Camera", "vTo");
                }
                camera.vTo = tempTarget.subtract(camera.p0);
            }

            camera.vTo = camera.vTo.normalize();

            Vector vUpGeneral = tempUp == null ? Vector.AXIS_Y : tempUp;

            try {
                camera.vRight = camera.vTo.crossProduct(vUpGeneral).normalize();
                camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("vTo and vUp cannot be parallel");
            }

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
         * Validates view plane parameters and calculates pixel dimensions and VP center.
         */
        private void checkViewPlane() {
            if (isZero(camera.distance) || camera.distance < 0) {
                throw new IllegalArgumentException("Distance must be positive");
            }

            if (isZero(camera.width) || isZero(camera.height) || camera.width < 0 || camera.height < 0) {
                throw new IllegalArgumentException("VP Size must be positive");
            }

            camera.pixelWidth = camera.width / camera.nX;
            camera.pixelHeight = camera.height / camera.nY;
            camera.vpCenter = camera.p0.add(camera.vTo.scale(camera.distance));
        }
    }

    /**
     * Factory method for creating a new Camera Builder.
     *
     * @return new Builder instance
     */
    public static Builder getBuilder() {
        return new Builder();
    }
}