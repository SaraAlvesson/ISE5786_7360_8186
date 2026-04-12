package geometries.impl;

import geometries.api.Geometry;
import java.util.List;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.isZero;

/**
 * Represents a convex polygon in a 3D Cartesian coordinate system.
 * * @author Dan Zilberstein
 */
public class Polygon extends Geometry {
    /** Ordered list of polygon vertices */
    protected final List<Point> _vertices;
    /** Plane containing the polygon */
    protected final Plane _plane;
    /** Number of vertices */
    private final int _size;

    /**
     * Constructs a convex polygon from ordered vertices.
     * * @param vertices polygon vertices in edge order
     * @throws IllegalArgumentException if the vertices do not form a valid convex polygon
     */
    public Polygon(Point... vertices) {
        if (vertices.length < 3)
            throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
        _vertices = List.of(vertices);
        _size = vertices.length;

        // Create the supporting plane
        _plane = new Plane(vertices[0], vertices[1], vertices[2]);
        if (_size == 3) return;

        Vector n = _plane.getNormal(vertices[0]);
        Vector edge1 = vertices[_size - 1].subtract(vertices[_size - 2]);
        Vector edge2 = vertices[0].subtract(vertices[_size - 1]);

        boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
        for (var i = 1; i < _size; ++i) {
            if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
                throw new IllegalArgumentException("All vertices must lay in the same plane");
            edge1 = edge2;
            edge2 = vertices[i].subtract(vertices[i - 1]);
            if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
                throw new IllegalArgumentException("Polygon must be convex and vertices ordered");
        }
    }

    @Override
    public Vector getNormal(Point point) {
        return _plane.getNormal(point);
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        // Step 1: Find intersection with the plane
        var intersections = _plane.findIntersections(ray);
        if (intersections == null) return null;

        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Step 2: Check if point is inside the polygon edges
        Vector v1 = _vertices.get(0).subtract(p0);
        Vector v2 = _vertices.get(1).subtract(p0);

        // Calculate sign of the first dot product
        double s1 = v.dotProduct(v1.crossProduct(v2));
        if (isZero(s1)) return null; // Point is on an edge/vertex
        boolean positive = s1 > 0;

        // Check all other edges
        for (int i = 1; i < _size; i++) {
            v1 = v2;
            v2 = _vertices.get((i + 1) % _size).subtract(p0);
            double s = v.dotProduct(v1.crossProduct(v2));

            if (isZero(s)) return null; // Point is on an edge/vertex
            if ((s > 0) != positive) return null; // Point is outside
        }

        return intersections;
    }

    @Override
    public String toString() {
        return "vertices=" + _vertices;
    }
}