package renderer.sampling;

import java.util.ArrayList;
import java.util.List;
import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

/**
 * TargetArea class responsible for mapping 2D sample points into 3D space
 * using a local orthonormal coordinate system.
 */
public class TargetArea {

    private TargetArea() {
        // Utility class containing static methods
    }

    /**
     * Maps a list of 2D offset points into 3D space points on a specific plane.
     * * @param center          the 3D center point of the target area
     *
     * @param direction      the normal direction vector of the target area
     * @param size           the width/height (or radius) of the target area
     * @param offsetPoints2D the list of 2D normalized offsets from Blackboard
     * @return list of 3D points in the target area
     */
    public static List<Point> convertTo3DPoints(Point center, Vector direction, double size, List<Point2D> offsetPoints2D) {
        List<Point> points3D = new ArrayList<>();

        // 1. Find the first orthogonal vector to the direction
        Vector vUp;
        double x = direction.getX();
        double y = direction.getY();
        double z = direction.getZ();

        // Standard way to find an orthogonal vector by swapping coordinates
        if (!Util.isZero(x) || !Util.isZero(y)) {
            vUp = new Vector(-y, x, 0).normalize();
        } else {
            vUp = new Vector(0, -z, y).normalize();
        }

        // 2. Find the second orthogonal vector using cross product
        Vector vRight = direction.crossProduct(vUp).normalize();

        // 3. Map each 2D offset into a 3D point on this local coordinate system
        for (Point2D point2D : offsetPoints2D) {
            Point p = center;

            // Apply horizontal shift if not zero
            if (!Util.isZero(point2D.x())) {
                p = p.add(vRight.scale(point2D.x() * size));
            }

            // Apply vertical shift if not zero
            if (!Util.isZero(point2D.y())) {
                p = p.add(vUp.scale(point2D.y() * size));
            }

            points3D.add(p);
        }

        return points3D;
    }

    /**
     * Generates a beam of rays from a single starting point to a list of 3D target points.
     * * @param startPoint   the 3D starting point of all rays in the beam
     *
     * @param targetPoints the list of 3D target points
     * @return list of rays forming the beam
     */
    public static List<Ray> generateBeam(Point startPoint, List<Point> targetPoints) {
        List<Ray> beam = new ArrayList<>();

        for (Point target : targetPoints) {
            // Calculate the direction vector from startPoint to the target point
            Vector direction = target.subtract(startPoint);
            beam.add(new Ray(startPoint, direction));
        }

        return beam;
    }
}