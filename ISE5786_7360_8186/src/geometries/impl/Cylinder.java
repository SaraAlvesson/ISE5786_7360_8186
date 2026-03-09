package geometries.impl;

import primitives.*;

public class Cylinder extends Tube {
    private final double _height;
    public Cylinder(double radius, Ray axisRay, double height) {
        super(radius, axisRay);
        _height = height;
    }
    @Override
    public Vector getNormal(Point point) { return null; }
}