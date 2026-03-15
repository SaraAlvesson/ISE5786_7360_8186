package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * Interface for all geometric shapes
 */
public interface Geometry {
    /**
     * Get the normal vector to the shape at a specific point
     *
     * @param point point on the surface
     * @return normal vector
     */
    Vector getNormal(Point point);
}
