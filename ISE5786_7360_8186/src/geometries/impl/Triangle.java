package geometries.impl;

import java.util.List;
import primitives.Point;
import primitives.Ray;

/**
 * Class Triangle represents a triangle in 3D space, defined by three vertices.
 * This class inherits from Polygon.
 */
public final class Triangle extends Polygon {

    /**
     * Constructor to initialize Triangle with three points.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        return super.calcIntersectionsHelper(ray);
    }

    @Override
    public String toString() {
        return "Triangle: " + super.toString();
    }
}