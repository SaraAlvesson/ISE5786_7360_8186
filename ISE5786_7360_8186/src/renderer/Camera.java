package renderer;

import java.util.MissingResourceException;
import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Camera class representing a camera in a 3D scene.
 * The camera is built using a Builder pattern and generates rays through a View Plane.
 */
public class Camera implements Cloneable {
    private Point p0;
    private Vector vUp, vTo, vRight;
    private double width, height, distance;
    private int nX, nY;

    // Auxiliary calculated fields for efficiency
    private Point vpCenter;
    private double pixelWidth, pixelHeight;

    // New fields for Stage 5
    private ImageWriter imageWriter;
    private RayTracerBase rayTracer;

    /**
     * Default private constructor for Builder
     */
    private Camera() {
    }

    /**
     * Renders the image by casting rays through all pixels
     *
     * @return current camera object
     */
    public Camera renderImage() {
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                castRay(j, i);
            }
        }
        return this;
    }

    /**
     * Private helper method to cast a ray through a specific pixel
     *
     * @param j column index
     * @param i row index
     */
    private void castRay(int j, int i) {
        Ray ray = constructRay(j, i);
        Color color = rayTracer.traceRay(ray);
        imageWriter.writePixel(j, i, color);
    }

    /**
     * Adds a grid to the existing image
     *
     * @param interval grid square size
     * @param color    grid line color
     * @return current camera object
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
     * Delegates image writing to the ImageWriter
     *
     * @param fileName name of the output file
     */
    public void writeToImage(String fileName) {
        imageWriter.writeToImage(fileName);
    }

    /**
     * Constructs a ray through the center of a pixel (j, i) in the View Plane.
     *
     * @param j column index (x index)
     * @param i row index (y index)
     * @return Ray starting from camera location through pixel center
     */
    public Ray constructRay(int j, int i) {
        // Pixel coordinates calculation
        double xJ = (j - (nX - 1) / 2.0) * pixelWidth;
        double yI = -(i - (nY - 1) / 2.0) * pixelHeight;

        Point pIJ = vpCenter;

        // Add displacement only if non-zero to avoid Vector Zero exception
        if (!isZero(xJ)) pIJ = pIJ.add(vRight.scale(xJ));
        if (!isZero(yI)) pIJ = pIJ.add(vUp.scale(yI));

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
     * Builder static nested class for Camera
     */
    public static class Builder {
        private final Camera camera = new Camera();
        private Vector tempUp = null;
        private Point tempTarget = null;
        private double rotationAngle = 0;

        public Builder() {
            camera.nX = 1;
            camera.nY = 1;
        }

        public Builder setLocation(Point p0) {
            camera.p0 = p0;
            return this;
        }

        public Builder setDirection(Vector vTo, Vector vUp) {
            camera.vTo = vTo;
            this.tempUp = vUp;
            return this;
        }

        public Builder setDirection(Point target, Vector vUp) {
            this.tempTarget = target;
            this.tempUp = vUp;
            return this;
        }

        public Builder setDirection(Point target) {
            this.tempTarget = target;
            return this;
        }

        public Builder setVpSize(double width, double height) {
            camera.width = width;
            camera.height = height;
            return this;
        }

        public Builder setVpDistance(double distance) {
            camera.distance = distance;
            return this;
        }

        public Builder setResolution(int nX, int nY) {
            camera.nX = nX;
            camera.nY = nY;
            return this;
        }

        /**
         * Sets the ray tracer for the camera
         *
         * @param scene the scene to render
         * @param type  the type of ray tracer to use
         * @return current builder
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                camera.rayTracer = new SimpleRayTracer(scene);
            } else {
                throw new IllegalArgumentException("Unsupported ray tracer type");
            }
            return this;
        }

        /**
         * Bonus: Rotation around viewing direction in degrees clockwise
         */
        public Builder rotate(double angle) {
            this.rotationAngle = angle;
            return this;
        }

        /**
         * Build the camera and return its clone
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

        private void checkResolution() {
            if (camera.nX <= 0 || camera.nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");

            camera.imageWriter = new ImageWriter(camera.nX, camera.nY);
        }

        private void checkLocationAndDirection() {
            if (camera.p0 == null)
                throw new MissingResourceException("Missing location", "Camera", "p0");

            if (camera.vTo == null) {
                if (tempTarget == null) throw new MissingResourceException("Missing direction", "Camera", "vTo");
                camera.vTo = tempTarget.subtract(camera.p0);
            }
            camera.vTo = camera.vTo.normalize();

            // Use the AXIS_Y constant if available, otherwise new Vector(0,1,0)
            Vector vUpGeneral = (tempUp == null) ? Vector.AXIS_Y : tempUp;

            try {
                camera.vRight = camera.vTo.crossProduct(vUpGeneral).normalize();
                camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("vTo and vUp cannot be parallel");
            }

            // Fixed Bonus rotation implementation to be Clockwise (subtract instead of add)
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

    public static Builder getBuilder() {
        return new Builder();
    }
}