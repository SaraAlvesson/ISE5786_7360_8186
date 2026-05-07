package renderer;

import geometries.api.Intersectable.Intersection;
import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * A simple implementation of a ray tracer.
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructor for SimpleRayTracer.
     *
     * @param scene the scene
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);
        if (intersections == null) return _scene.background;

        var closestIntersection = ray.findClosestIntersection(intersections);
        return calcColor(closestIntersection);
    }

    /**
     * Calculates the color at a specific intersection point.
     * Factors in the ambient light (scaled by kA) and the emission color.
     *
     * @param intersection the intersection data
     * @return the color at the intersection
     */
    private Color calcColor(Intersection intersection) {
        return _scene.ambientLight.getIntensity()
                .scale(intersection.material.kA) // Scaling ambient light by material attenuation factor
                .add(intersection.geometry.getEmission()); // Adding self-emission of the geometry
    }
}