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
     * Bounding box used for CBR / BVH acceleration.
     * If the value is null, this object is treated as an object without a finite bounding box.
     */
    protected BoundingBox boundingBox = null;

    /**
     * Indicates whether CBR acceleration is enabled for this object.
     */
    private boolean cbrEnabled = false;

    /**
     * Helper class to store intersection data: the geometry, the point, and its material.
     * Defined as a Passive Data Structure (PDS).
     */
    public static class Intersection {
        /**
         * The geometry that was intersected.
         */
        public final Geometry geometry;

        /**
         * The point of intersection.
         */
        public final Point point;

        /**
         * The material of the intersected geometry.
         */
        public final Material material;

        /**
         * The normal vector at the intersection point.
         */
        public Vector normal;

        /**
         * The vector from the camera to the point.
         */
        public Vector v;

        /**
         * The vector from the light source to the point.
         */
        public Vector l;

        /**
         * The dot product of the normal and the light vector.
         */
        public double nl;

        /**
         * The dot product of the normal and the view vector.
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
     * Delegates to the maxDistance version with POSITIVE_INFINITY.
     *
     * @param ray the ray
     * @return list of Intersections
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersections(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Public overloaded method to calculate intersections within a maximum distance.
     * This is also the central place where CBR early rejection is applied.
     *
     * @param ray         the ray
     * @param maxDistance the maximum allowed distance from the ray origin
     * @return list of Intersections within the maximum distance
     */
    public final List<Intersection> calcIntersections(Ray ray, double maxDistance) {
        if (cbrEnabled && boundingBox != null && !boundingBox.intersects(ray)) {
            return null;
        }

        return calcIntersectionsHelper(ray, maxDistance);
    }

    /**
     * Enables CBR acceleration for this object.
     *
     * @return this object
     */
    public Intersectable enableCBR() {
        this.cbrEnabled = true;
        return this;
    }

    /**
     * Disables CBR acceleration for this object.
     *
     * @return this object
     */
    public Intersectable disableCBR() {
        this.cbrEnabled = false;
        return this;
    }

    /**
     * Checks whether CBR is enabled.
     *
     * @return true if CBR is enabled
     */
    public boolean isCBREnabled() {
        return cbrEnabled;
    }

    /**
     * Gets the bounding box of this object.
     *
     * @return bounding box, or null if this object has no finite bounding box
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    /**
     * Sets the bounding box of this object.
     *
     * @param boundingBox bounding box to set
     */
    protected void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * Creates or updates the bounding box of this object.
     * Subclasses may override createBoundingBox if they have a finite box.
     *
     * @return this object
     */
    public Intersectable updateBoundingBox() {
        this.boundingBox = createBoundingBox();
        return this;
    }

    /**
     * Creates the bounding box for this object.
     * Infinite geometries may keep the default implementation and return null.
     *
     * @return bounding box, or null if no finite bounding box exists
     */
    protected BoundingBox createBoundingBox() {
        return null;
    }

    /**
     * Internal helper method for intersection calculation to be implemented by geometries.
     *
     * @param ray         the ray
     * @param maxDistance the maximum allowed distance from the ray origin
     * @return list of Intersections
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance);
}