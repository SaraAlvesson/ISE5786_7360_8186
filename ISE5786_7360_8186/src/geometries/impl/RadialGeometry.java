package geometries.impl;

import geometries.api.Geometry;
import java.util.Objects;

import static primitives.Util.isZero;

/**
 * Abstract class for all geometric shapes that have a radius.
 * This class serves as a base for radial geometries like Sphere, Tube, and Cylinder.
 */
public abstract class RadialGeometry extends Geometry {
    /**
     * The radius of the geometry
     */
    protected final double _radius;
    /**
     * The square of the radius, pre-calculated for performance optimization
     */
    protected final double _radiusSquared;

    /**
     * Constructor to initialize the radius and its square.
     *
     * @param radius the radius of the geometry
     */
    public RadialGeometry(double radius) {
        this._radius = radius;
        this._radiusSquared = radius * radius;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RadialGeometry other = (RadialGeometry) obj;
        return isZero(_radius - other._radius);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_radius);
    }

    @Override
    public String toString() {
        return "radius=" + _radius;
    }
}