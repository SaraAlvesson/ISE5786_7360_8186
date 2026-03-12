package geometries.impl;

import primitives.*;
import java.util.Objects;

/**
 * Class Triangle represents a triangle in 3D space, defined by three vertices.
 * This class inherits from Polygon.
 */
public final class Triangle extends Polygon {

    /**
     * Constructor to initialize Triangle with three points.
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    public Vector getNormal(Point point) {
        return super.getNormal(point);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Triangle: " + super.toString();
    }
}