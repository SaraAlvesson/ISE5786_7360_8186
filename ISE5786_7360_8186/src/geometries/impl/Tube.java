package geometries.impl;

import primitives.*;
import java.util.Objects;

/**
 * Class Tube represents an infinite tube in 3D space, defined by an axis ray and a radius.
 */
public class Tube extends RadialGeometry {
    /** The axis ray of the tube */
    protected final Ray _axis;

    /**
     * Constructor to initialize Tube with radius and axis ray.
     * @param radius the radius of the tube
     * @param axis   the axis ray of the tube
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        _axis = axis;
    }

    @Override
    public Vector getNormal(Point point) {
        return null; // To be implemented in later stages
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Tube other) {
            return super.equals(obj) && _axis.equals(other._axis);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _axis);
    }

    @Override
    public String toString() {
        return "Tube: axis=" + _axis + ", " + super.toString();
    }
}