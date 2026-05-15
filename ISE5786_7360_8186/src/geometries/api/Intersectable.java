package geometries.api;

import java.util.List;
import java.util.Objects;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Abstract class representing a geometry that can be intersected by a ray.
 */
public abstract class Intersectable {

    /**
     * Helper class to store intersection data: the geometry, the point, and its material.
     * Defined as a Passive Data Structure (PDS).
     */
    public static class Intersection {
        /**
         * The geometry that was intersected
         */
        public final Geometry geometry;
        /**
         * The point of intersection
         */
        public final Point point;
        /**
         * The material of the intersected geometry
         */
        public final Material material;

        /**
         * The normal vector at the intersection point
         */
        public Vector normal;

        /**
         * The vector from the camera to the point
         */
        public Vector v;

        /**
         * The vector from the light source to the point
         */
        public Vector l;

        /**
         * The dot product of the normal and the light vector (n dot l)
         */
        public double nl;

        /**
         * The dot product of the normal and the view vector (n dot v)
         */
        public double nv;

        /**
         * Constructor for Intersection.
         *
         * @param geometry the intersected geometry
         * @param point    the intersection point
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            this.material = geometry == null ? new Material() : geometry.getMaterial();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj instanceof Intersection other) {
                return this.geometry == other.geometry &&
                        this.point.equals(other.point) &&
                        this.material.equals(other.material);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(System.identityHashCode(geometry), point, material);
        }

        @Override
        public String toString() {
            return "Intersection: geometry=" + geometry + ", point=" + point + ", material=" + material;
        }
    }

    /**
     * Finds all intersection points between a given ray and the geometry.
     *
     * @param ray the ray to intersect with the geometry
     * @return a list of intersection points, or null if there are no intersections
     */
    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream()
                .map(intersection -> intersection.point)
                .toList();
    }

    /**
     * Public method to calculate intersections, returning Intersection objects.
     *
     * @param ray the ray
     * @return list of Intersections
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }

    /**
     * Internal helper method for intersection calculation to be implemented by geometries.
     *
     * @param ray the ray
     * @return list of Intersections
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);
}