package geometries.api;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Axis Aligned Bounding Box for BVH / CBR acceleration.
 * The box is defined by minimum and maximum x, y, z values.
 */
public class BoundingBox {
    private static final double EPS = 1e-10;

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;
    private final double minZ;
    private final double maxZ;

    /**
     * Creates a bounding box from min and max values.
     *
     * @param minX minimum x
     * @param maxX maximum x
     * @param minY minimum y
     * @param maxY maximum y
     * @param minZ minimum z
     * @param maxZ maximum z
     */
    public BoundingBox(double minX, double maxX,
                       double minY, double maxY,
                       double minZ, double maxZ) {
        this.minX = Math.min(minX, maxX);
        this.maxX = Math.max(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.maxY = Math.max(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxZ = Math.max(minZ, maxZ);
    }

    /**
     * Creates a bounding box that contains one point.
     *
     * @param point point to contain
     * @return bounding box around the point
     */
    public static BoundingBox fromPoint(Point point) {
        return new BoundingBox(
                point.getX(), point.getX(),
                point.getY(), point.getY(),
                point.getZ(), point.getZ()
        );
    }

    /**
     * Creates a bounding box that contains all given boxes.
     *
     * @param boxes boxes to combine
     * @return combined bounding box
     */
    public static BoundingBox combine(BoundingBox... boxes) {
        BoundingBox result = null;

        for (BoundingBox box : boxes) {
            if (box == null) {
                continue;
            }

            if (result == null) {
                result = box;
            } else {
                result = result.combine(box);
            }
        }

        return result;
    }

    /**
     * Combines this box with another box.
     *
     * @param other another bounding box
     * @return combined bounding box
     */
    public BoundingBox combine(BoundingBox other) {
        if (other == null) {
            return this;
        }

        return new BoundingBox(
                Math.min(this.minX, other.minX),
                Math.max(this.maxX, other.maxX),
                Math.min(this.minY, other.minY),
                Math.max(this.maxY, other.maxY),
                Math.min(this.minZ, other.minZ),
                Math.max(this.maxZ, other.maxZ)
        );
    }

    /**
     * Checks whether a ray intersects this bounding box.
     * This method returns only true / false.
     *
     * @param ray ray to test
     * @return true if the ray intersects the box
     */
    public boolean intersects(Ray ray) {
        Point origin = ray.origin();
        Vector direction = ray.direction();

        double tMin = Double.NEGATIVE_INFINITY;
        double tMax = Double.POSITIVE_INFINITY;

        double[] originValues = {
                origin.getX(),
                origin.getY(),
                origin.getZ()
        };

        double[] directionValues = {
                direction.getX(),
                direction.getY(),
                direction.getZ()
        };

        double[] minValues = {
                minX,
                minY,
                minZ
        };

        double[] maxValues = {
                maxX,
                maxY,
                maxZ
        };

        for (int axis = 0; axis < 3; axis++) {
            double originValue = originValues[axis];
            double directionValue = directionValues[axis];
            double minValue = minValues[axis];
            double maxValue = maxValues[axis];

            if (Math.abs(directionValue) < EPS) {
                if (originValue < minValue || originValue > maxValue) {
                    return false;
                }
            } else {
                double t1 = (minValue - originValue) / directionValue;
                double t2 = (maxValue - originValue) / directionValue;

                if (t1 > t2) {
                    double temp = t1;
                    t1 = t2;
                    t2 = temp;
                }

                tMin = Math.max(tMin, t1);
                tMax = Math.min(tMax, t2);

                if (tMin > tMax) {
                    return false;
                }
            }
        }

        return tMax >= 0;
    }

    /**
     * @return minimum x value
     */
    public double getMinX() {
        return minX;
    }

    /**
     * @return maximum x value
     */
    public double getMaxX() {
        return maxX;
    }

    /**
     * @return minimum y value
     */
    public double getMinY() {
        return minY;
    }

    /**
     * @return maximum y value
     */
    public double getMaxY() {
        return maxY;
    }

    /**
     * @return minimum z value
     */
    public double getMinZ() {
        return minZ;
    }

    /**
     * @return maximum z value
     */
    public double getMaxZ() {
        return maxZ;
    }

    /**
     * @return center x value
     */
    public double getCenterX() {
        return (minX + maxX) / 2.0;
    }

    /**
     * @return center y value
     */
    public double getCenterY() {
        return (minY + maxY) / 2.0;
    }

    /**
     * @return center z value
     */
    public double getCenterZ() {
        return (minZ + maxZ) / 2.0;
    }
}