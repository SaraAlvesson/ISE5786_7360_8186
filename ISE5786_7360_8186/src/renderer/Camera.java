package renderer;

import primitives.*;
import java.util.MissingResourceException;
import static primitives.Util.*;

/**
 * Camera class representing a camera in a 3D scene.
 * The camera is built using a Builder pattern and generates rays through a View Plane.
 */
public class Camera implements Cloneable {
    private Point p0;
    private Vector vUp, vTo, vRight;
    private double width, height, distance;
    private int nX, nY;

    // Auxiliary calculated fields for efficiency [cite: 44, 217]
    private Point vpCenter;
    private double pixelWidth, pixelHeight;

    /** Default private constructor for Builder [cite: 46] */
    private Camera() {}

    /**
     * Constructs a ray through the center of a pixel (j, i) in the View Plane.
     *
     * @param j column index (x index) [cite: 90]
     * @param i row index (y index) [cite: 90]
     * @return Ray starting from camera location through pixel center
     */
    public Ray constructRay(int j, int i) {
        // Pixel coordinates calculation [cite: 317-318, 325]
        double xJ = (j - (nX - 1) / 2.0) * pixelWidth;
        double yI = -(i - (nY - 1) / 2.0) * pixelHeight;

        Point pIJ = vpCenter;

        // Add displacement only if non-zero to avoid Vector Zero exception [cite: 324, 331]
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

    /** Builder static nested class for Camera [cite: 33, 38] */
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

        /** Bonus: Rotation around viewing direction in degrees clockwise [cite: 237-238] */
        public Builder rotate(double angle) {
            this.rotationAngle = angle;
            return this;
        }

        /** Build the camera and return its clone [cite: 65-73, 220] */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            Camera builtCamera = camera.clone();
            if (builtCamera == null) throw new RuntimeException("Clone failed");
            return builtCamera;
        }

        private void checkResolution() {
            if (camera.nX <= 0 || camera.nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive [cite: 74-75]");
        }

        private void checkLocationAndDirection() {
            if (camera.p0 == null)
                throw new MissingResourceException("Missing location", "Camera", "p0");

            if (camera.vTo == null) {
                if (tempTarget == null) throw new MissingResourceException("Missing direction", "Camera", "vTo");
                camera.vTo = tempTarget.subtract(camera.p0);
            }
            camera.vTo = camera.vTo.normalize();

            Vector vUpGeneral = (tempUp == null) ? new Vector(0, 1, 0) : tempUp;

            try {
                camera.vRight = camera.vTo.crossProduct(vUpGeneral).normalize();
                camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("vTo and vUp cannot be parallel [cite: 78]");
            }

            // Bonus rotation implementation
            if (!isZero(rotationAngle)) {
                double rad = Math.toRadians(rotationAngle);
                double cos = Math.cos(rad);
                double sin = Math.sin(rad);
                Vector vRightOld = camera.vRight;
                Vector vUpOld = camera.vUp;
                camera.vRight = vRightOld.scale(cos).add(vUpOld.scale(sin)).normalize();
                camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();
            }
        }

        private void checkViewPlane() {
            if (isZero(camera.distance) || camera.distance < 0)
                throw new IllegalArgumentException("Distance must be positive [cite: 79]");
            if (isZero(camera.width) || isZero(camera.height) || camera.width < 0 || camera.height < 0)
                throw new IllegalArgumentException("VP Size must be positive [cite: 79]");

            camera.pixelWidth = camera.width / camera.nX;
            camera.pixelHeight = camera.height / camera.nY;
            camera.vpCenter = camera.p0.add(camera.vTo.scale(camera.distance));
        }
    }

    public static Builder getBuilder() {
        return new Builder();
    }
}