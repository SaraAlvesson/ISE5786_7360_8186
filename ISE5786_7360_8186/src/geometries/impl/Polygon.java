package geometries.impl;

import geometries.api.BoundingBox;
import geometries.api.Geometry;
import java.util.List;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.isZero;

/**
 * Represents a convex polygon in a 3D Cartesian coordinate system.
 *
 * @author Dan Zilberstein
 */
public class Polygon extends Geometry {
    /**
     * Ordered list of polygon vertices.
     */
    protected final List<Point> _vertices;

    /**
     * Plane containing the polygon.
     */
    protected final Plane _plane;

    /**
     * Number of vertices.
     */
    private final int _size;

    /**
     * Constructs a convex polygon from ordered vertices.
     *
     * @param vertices polygon vertices in edge order
     * @throws IllegalArgumentException if the vertices do not form a valid convex polygon
     */
    public Polygon(Point... vertices) {
        if (vertices.length < 3) {
            throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
        }

        _vertices = List.of(vertices);
        _size = vertices.length;

        _plane = new Plane(vertices[0], vertices[1], vertices[2]);

        if (_size > 3) {
            Vector n = _plane.getNormal(vertices[0]);
            Vector edge1 = vertices[_size - 1].subtract(vertices[_size - 2]);
            Vector edge2 = vertices[0].subtract(vertices[_size - 1]);

            boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;

            for (var i = 1; i < _size; ++i) {
                if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n))) {
                    throw new IllegalArgumentException("All vertices must lay in the same plane");
                }

                edge1 = edge2;
                edge2 = vertices[i].subtract(vertices[i - 1]);

                if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0)) {
                    throw new IllegalArgumentException("Polygon must be convex and vertices ordered");
                }
            }
        }

        updateBoundingBox();
    }

    /**
     * Creates the bounding box for the polygon.
     * The box is calculated from the minimum and maximum x, y, z values
     * of all polygon vertices.
     *
     * @return bounding box of the polygon
     */
    @Override
    protected BoundingBox createBoundingBox() {
        double minX = _vertices.get(0).getX();
        double maxX = _vertices.get(0).getX();
        double minY = _vertices.get(0).getY();
        double maxY = _vertices.get(0).getY();
        double minZ = _vertices.get(0).getZ();
        double maxZ = _vertices.get(0).getZ();

        for (Point point : _vertices) {
            minX = Math.min(minX, point.getX());
            maxX = Math.max(maxX, point.getX());

            minY = Math.min(minY, point.getY());
            maxY = Math.max(maxY, point.getY());

            minZ = Math.min(minZ, point.getZ());
            maxZ = Math.max(maxZ, point.getZ());
        }

        return new BoundingBox(minX, maxX, minY, maxY, minZ, maxZ);
    }

    @Override
    public Vector getNormal(Point point) {
        return _plane.getNormal(point);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        var intersections = _plane.calcIntersections(ray, maxDistance);
        if (intersections == null) {
            return null;
        }

        Point p0 = ray.origin();
        Vector v = ray.direction();

        Vector v1 = _vertices.get(0).subtract(p0);
        Vector v2 = _vertices.get(1).subtract(p0);

        double s1 = v.dotProduct(v1.crossProduct(v2));
        if (isZero(s1)) {
            return null;
        }

        boolean positive = s1 > 0;

        for (int i = 1; i < _size; i++) {
            v1 = v2;
            v2 = _vertices.get((i + 1) % _size).subtract(p0);

            double s = v.dotProduct(v1.crossProduct(v2));

            if (isZero(s)) {
                return null;
            }

            if ((s > 0) != positive) {
                return null;
            }
        }

        return intersections.stream()
                .map(intersection -> new Intersection(this, intersection.point))
                .toList();
    }

    @Override
    public String toString() {
        return "vertices=" + _vertices;
    }
}