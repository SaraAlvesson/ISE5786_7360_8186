package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Ray;
import scene.Scene;

import static primitives.Util.alignZero;

/**
 * Abstract base class for ray tracers.
 */
public abstract class RayTracerBase {
    /**
     * The scene to be rendered
     */
    protected Scene _scene;

    /**
     * Constructor for RayTracerBase.
     *
     * @param scene the scene
     */
    public RayTracerBase(Scene scene) {
        this._scene = scene;
    }

    /**
     * Abstract method to trace a ray and return the color of the point it hits.
     *
     * @param ray the ray to trace
     * @return the color detected by the ray
     */
    public abstract Color traceRay(Ray ray);

    /**
     * Prepares the intersection data with normal and view vectors.
     *
     * @param intersection the intersection data
     * @param ray          the ray that caused the intersection
     */
    protected void preprocessIntersection(Intersection intersection, Ray ray) {
        intersection.v = ray.direction();
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.nv = alignZero(intersection.normal.dotProduct(intersection.v));
    }

    /**
     * Prepares the intersection data for a specific light source.
     *
     * @param intersection the intersection data
     * @param light        the light source
     * @return true if the light hits the point from the correct side, false otherwise
     */
    protected boolean preprocessLightSource(Intersection intersection, LightSource light) {
        intersection.l = light.getL(intersection.point);
        intersection.nl = alignZero(intersection.normal.dotProduct(intersection.l));
        return intersection.nl * intersection.nv > 0;
    }
}