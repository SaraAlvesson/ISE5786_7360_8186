package renderer;

import primitives.Color;
import primitives.Point;
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
        var intersections = _scene.geometries.findIntersections(ray);
        if (intersections == null) return _scene.background;

        Point closestPoint = ray.findClosestPoint(intersections);
        return calcColor(closestPoint);
    }

    /**
     * Calculates the color at a specific point.
     * At this stage, it only returns the ambient light intensity.
     *
     * @param intersection the point
     * @return the color at the point
     */
    private Color calcColor(Point intersection) {
        return _scene.ambientLight.getIntensity();
    }
}