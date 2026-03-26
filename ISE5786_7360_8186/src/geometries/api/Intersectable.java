package geometries.api;

import java.util.List;
import primitives.Point;
import primitives.Ray;

/**
 * Abstract class representing a geometry that can be intersected by a ray.
 */
public abstract class Intersectable {
    /**
     * Finds all intersection points between a given ray and the geometry.
     *
     * @param ray the ray to intersect with the geometry
     * @return a list of intersection points, or null if there are no intersections
     */
    public abstract List<Point> findIntersections(Ray ray);
}