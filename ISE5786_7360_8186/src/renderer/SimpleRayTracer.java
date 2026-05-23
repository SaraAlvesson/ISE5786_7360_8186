package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static primitives.Util.alignZero;

/**
 * A simple implementation of a ray tracer.
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constant for shifting the origin of the shadow ray to prevent self-shadowing.
     */
    private static final double DELTA = 0.1;

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
                // Check if the point is unshaded by this light source
                if (unshaded(intersection, lightSource)) {
                    Color intensity = lightSource.getIntensity(intersection.point);
                    color = color.add(
                            intensity.scale(calcDiffuse(intersection.material.kD, intersection.nl)),
                            intensity.scale(calcSpecular(intersection.material.kS, intersection.l, intersection.normal, intersection.nl, intersection.v, intersection.material.nShininess))
                    );
                }
            }
        }
        return color;
    }

    /**
     * Checks if a point is unshaded by a specific light source.
     *
     * @param intersection the intersection point data
     * @param lightSource  the light source
     * @return true if the point is unshaded, false otherwise
     */
    private boolean unshaded(Intersection intersection, LightSource lightSource) {
        // The shadow ray direction is opposite to the vector from light source to point (getL)
        Vector shadowDirection = lightSource.getL(intersection.point).scale(-1);

        // Determine the sign of the shift along the normal based on the direction towards the light
        double cosTheta = shadowDirection.dotProduct(intersection.normal);

        Vector delta = intersection.normal.scale(cosTheta > 0 ? DELTA : -DELTA);
        Point shadowOrigin = intersection.point.add(delta);
        Ray shadowRay = new Ray(shadowOrigin, shadowDirection);

        // Find all intersections of the shadow ray with the scene geometries
        var intersections = _scene.geometries.calcIntersections(shadowRay);
        if (intersections == null) return true;

        // Filter intersections that are closer than the light source distance
        double lightDistance = lightSource.getDistance(intersection.point);
        for (Intersection intersect : intersections) {
            if (alignZero(intersect.point.distance(intersection.point) - lightDistance) < 0) {
                return false; // Found an object blocking the light source
            }
        }
        return true;
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