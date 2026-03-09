package geometries.impl;

import primitives.*;

public class Tube extends RadialGeometry {
    protected final Ray _axisRay;
    public Tube(double radius, Ray axisRay) {
        super(radius);
        _axisRay = axisRay;
    }
    @Override
    public Vector getNormal(Point point) { return null; }
}