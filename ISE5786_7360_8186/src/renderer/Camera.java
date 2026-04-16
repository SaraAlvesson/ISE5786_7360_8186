package renderer;

import primitives.*;
import java.util.MissingResourceException;
import static primitives.Util.*;

public class Camera {
    private Point p0;
    private Vector vUp, vTo, vRight;
    private double width, height, distance;
    private int nX, nY;

    private Camera() {}

    public Ray constructRay(int j, int i) {
        Point pc = p0.add(vTo.scale(distance));
        double rX = width / nX;
        double rY = height / nY;

        double xJ = (j - (nX - 1) / 2.0) * rX;
        double yI = -(i - (nY - 1) / 2.0) * rY;

        Point pIJ = pc;
        if (!isZero(xJ)) pIJ = pIJ.add(vRight.scale(xJ));
        if (!isZero(yI)) pIJ = pIJ.add(vUp.scale(yI));

        return new Ray(p0, pIJ.subtract(p0));
    }

    public static class Builder {
        private final Camera camera = new Camera();
        private double angle = 0; // שדה לשמירת זווית הסיבוב (בונוס)

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
            camera.vUp = vUp;
            return this;
        }

        public Builder setDirection(Point target) {
            if (camera.p0 == null) throw new MissingResourceException("p0 missing", "Camera", "p0");
            camera.vTo = target.subtract(camera.p0);
            camera.vUp = new Vector(0, 1, 0);
            return this;
        }

        public Builder setDirection(Point target, Vector vUp) {
            if (camera.p0 == null) throw new MissingResourceException("p0 missing", "Camera", "p0");
            camera.vTo = target.subtract(camera.p0);
            camera.vUp = vUp;
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
         * Rotates the camera around its viewing direction (Bonus)
         *
         * @param angle Rotation angle in degrees (clockwise)
         * @return The builder instance
         */
        public Builder rotate(double angle) {
            this.angle = angle;
            return this;
        }

        public Camera build() {
            // 1. בדיקת חוסרים קריטיים [cite: 121, 156]
            if (camera.p0 == null) throw new MissingResourceException("Missing location", "Camera", "p0");
            if (camera.vTo == null) throw new MissingResourceException("Missing vTo", "Camera", "vTo");
            if (camera.vUp == null) throw new MissingResourceException("Missing vUp", "Camera", "vUp");

            // 2. בדיקת ערכי View Plane - חייבים להיות חיוביים ומוגדרים [cite: 122, 152]
            // אם width או height הם 0, זה אומר שהם לא הוגדרו או הוגדרו לא תקין
            if (isZero(camera.width) || camera.width < 0)
                throw new IllegalArgumentException("View Plane width must be set and positive");
            if (isZero(camera.height) || camera.height < 0)
                throw new IllegalArgumentException("View Plane height must be set and positive");
            if (isZero(camera.distance) || camera.distance < 0)
                throw new IllegalArgumentException("View Plane distance must be set and positive");

            // 3. בדיקת רזולוציה [cite: 122, 123]
            if (camera.nX <= 0 || camera.nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");

            // 4. בדיקת אורתוגונליות [cite: 122]
            if (!isZero(camera.vTo.dotProduct(camera.vUp))) {
                throw new IllegalArgumentException("vTo and vUp must be perpendicular");
            }

            // 5. נרמול וחישוב וקטורים [cite: 72]
            camera.vTo = camera.vTo.normalize();
            camera.vUp = camera.vUp.normalize();
            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();

            // 6. מימוש סיבוב (בונוס) עם הגנה מוקטור אפס
            if (!isZero(angle)) {
                double radians = Math.toRadians(angle);
                double cos = Math.cos(radians);
                double sin = Math.sin(radians);

                Vector newVup;
                if (isZero(cos)) {
                    newVup = camera.vRight.scale(sin);
                } else if (isZero(sin)) {
                    newVup = camera.vUp.scale(cos);
                } else {
                    newVup = camera.vUp.scale(cos).add(camera.vRight.scale(sin));
                }

                camera.vUp = newVup.normalize();
                camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
            }

            return camera;
        }
    }

    public static Builder getBuilder() {
        return new Builder();
    }
}