package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static primitives.Util.alignZero;

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
        preprocessIntersection(closestIntersection, ray);
        return calcColor(closestIntersection);
    }

    /**
     * Calculates the color at a specific intersection point.
     *
     * @param intersection the intersection data
     * @return the color at the intersection
     */
    private Color calcColor(Intersection intersection) {
        return _scene.ambientLight.getIntensity()
                .scale(intersection.material.kA)
                .add(intersection.geometry.getEmission())
                .add(calcLocalEffects(intersection));
    }

    /**
     * Calculates the local effects (diffuse and specular) of all light sources.
     *
     * @param intersection the intersection data
     * @return the total color from local effects
     */
    private Color calcLocalEffects(Intersection intersection) {
        Color color = Color.BLACK;
        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {
                Color intensity = lightSource.getIntensity(intersection.point);
                color = color.add(
                        intensity.scale(calcDiffuse(intersection.material.kD, intersection.nl)),
                        intensity.scale(calcSpecular(intersection.material.kS, intersection.l, intersection.normal, intersection.nl, intersection.v, intersection.material.nShininess))
                );
            }
        }
        return color;
    }

    /**
     * Calculates the diffuse component of the light.
     *
     * @param kd the diffuse reflection coefficient
     * @param nl dot product of normal and light vector
     * @return the diffuse component scaling factor
     */
    private Double3 calcDiffuse(Double3 kd, double nl) {
        return kd.scale(Math.abs(nl));
    }

    /**
     * Calculates the specular component of the light.
     *
     * @param ks         the specular reflection coefficient
     * @param l          vector from light to point
     * @param n          normal vector
     * @param nl         dot product of n and l
     * @param v          direction of the ray
     * @param nShininess shininess factor
     * @return the specular component scaling factor
     */
    private Double3 calcSpecular(Double3 ks, Vector l, Vector n, double nl, Vector v, int nShininess) {
        Vector r = l.subtract(n.scale(2 * nl));
        double minusVR = alignZero(v.dotProduct(r) * -1);
        if (minusVR <= 0) return Double3.ZERO;
        return ks.scale(Math.pow(minusVR, nShininess));
    }
}