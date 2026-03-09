package geometries.impl;

import geometries.api.Geometry;

/**
 * Abstract class for shapes with a radius
 */
public abstract class RadialGeometry implements Geometry {
    protected final double _radius;

    public RadialGeometry(double radius) {
        _radius = radius;
    }
}