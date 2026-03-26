package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * Abstract class for all geometric shapes.
 * Inherits from Intersectable to allow intersection calculations.
 */
public abstract class Geometry extends Intersectable {
    /**
     * Get the normal vector to the shape at a specific point.
     *
     * @param point point on the surface
     * @return normal vector
     */
    public abstract Vector getNormal(Point point);
}