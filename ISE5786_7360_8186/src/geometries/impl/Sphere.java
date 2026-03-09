package geometries.impl;

import primitives.*;

public class Sphere extends RadialGeometry {
    private final Point _center;

    public Sphere(Point center, double radius) {
        super(radius);
        _center = center;
    }

    @Override
    public Vector getNormal(Point point) {
        return null; // Temporary
    }
}