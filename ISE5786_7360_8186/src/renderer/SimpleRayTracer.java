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
 * A simple implementation of a ray tracer supporting shading, shadows,
 * reflections, and partial transparency.
 * Optimized with maxDistance constraint for shadow ray intersections (Bonus 3).
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constant for shifting the origin of the shadow ray to prevent self-shadowing.
     */
    private static final double DELTA = 0.1;

    /**
     * Recursion stop condition - maximum level of color calculations.
     */
    private static final int MAX_CALC_COLOR_LEVEL = 10;

    /**
     * Recursion stop condition - minimum attenuation factor coefficient.
     */
    private static final double MIN_CALC_COLOR_K = 0.001;

    /**
     * Initial global attenuation factor.
     */
    private static final Double3 INITIAL_K = Double3.ONE;

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
        // Find the closest intersection point for the primary ray
        Intersection closestIntersection = findClosestIntersection(ray);
        if (closestIntersection == null) return _scene.background;

        preprocessIntersection(closestIntersection, ray);
        return calcColor(closestIntersection, ray);
    }

    /**
     * Entry point wrapper for calculating the color at an intersection point.
     * Adds ambient light only once to avoid wrong accumulation during recursion.
     *
     * @param intersection the intersection data
     * @param ray          the ray that caused the intersection
     * @return the total color at the intersection point
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        return _scene.ambientLight.getIntensity()
                .scale(intersection.material.kA)
                .add(calcColor(intersection, ray, MAX_CALC_COLOR_LEVEL, INITIAL_K));
    }

    /**
     * Recursive method to calculate color based on local and global effects.
     *
     * @param intersection the intersection data
     * @param ray          the ray that caused the intersection
     * @param level        the current recursion depth level
     * @param k            the cumulative attenuation coefficient factor
     * @return the calculated color
     */
    private Color calcColor(Intersection intersection, Ray ray, int level, Double3 k) {
        Color color = intersection.geometry.getEmission().add(calcLocalEffects(intersection, k));
        return level == 1 ? color : color.add(calcGlobalEffects(intersection, ray, level, k));
    }

    /**
     * Calculates the local effects (diffuse and specular) of all light sources,
     * taking into account partial transparency shadows.
     *
     * @param intersection the intersection data
     * @param k            the cumulative global attenuation coefficient factor
     * @return the total color from local effects
     */
    private Color calcLocalEffects(Intersection intersection, Double3 k) {
        Color color = Color.BLACK;
        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {
                // Calculate the partial transparency factor from objects blocking this light source
                Double3 ktr = transparency(intersection, lightSource);

                // Check if the total light contribution (global k * shadow ktr) is significant
                if (ktr.product(k).isGreaterThan(MIN_CALC_COLOR_K)) {
                    Color intensity = lightSource.getIntensity(intersection.point).scale(ktr);
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
     * Calculates the global effects (reflection and transparency) recursively.
     *
     * @param intersection the intersection data
     * @param ray          the incoming ray
     * @param level        the current recursion depth level
     * @param k            the cumulative attenuation coefficient factor
     * @return the total color from global effects
     */
    private Color calcGlobalEffects(Intersection intersection, Ray ray, int level, Double3 k) {
        Color color = Color.BLACK;
        Vector v = ray.direction();
        Vector n = intersection.normal;

        // 1. Reflection ray calculation using the refactored Ray constructor (with normal shift)
        double nv = alignZero(n.dotProduct(v));
        Vector r = v.subtract(n.scale(2 * nv));
        Ray reflectionRay = new Ray(intersection.point, r, n);
        color = color.add(calcGlobalEffect(reflectionRay, level, k, intersection.material.kR));

        // 2. Transparency ray calculation (same direction v, shifted along normal)
        Ray transparencyRay = new Ray(intersection.point, v, n);
        color = color.add(calcGlobalEffect(transparencyRay, level, k, intersection.material.kT));

        return color;
    }

    /**
     * Helper method to calculate a single global effect ray.
     *
     * @param ray   the secondary ray
     * @param level the current recursion depth level
     * @param k     the cumulative attenuation coefficient factor
     * @param kx    the material attenuation factor (kR or kT)
     * @return the calculated color component
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
        Double3 kkx = k.product(kx);
        if (kkx.isLowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;

        Intersection closestIntersection = findClosestIntersection(ray);
        if (closestIntersection == null) return _scene.background.scale(kx);

        preprocessIntersection(closestIntersection, ray);
        return calcColor(closestIntersection, ray, level - 1, kkx).scale(kx);
    }

    /**
     * Helper method to find the closest intersection point from a given ray.
     *
     * @param ray the ray to trace
     * @return the closest intersection point, or null if none found
     */
    private Intersection findClosestIntersection(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);
        return intersections == null ? null : ray.findClosestIntersection(intersections);
    }

    /**
     * Calculates the cumulative light transparency coefficient factor between a point and a light source.
     * Optimized for Bonus 3 by passing the max distance directly to the composite geometries.
     *
     * @param intersection the intersection point data
     * @param lightSource  the light source
     * @return the cumulative transparency factor (Double3)
     */
    private Double3 transparency(Intersection intersection, LightSource lightSource) {
        Vector shadowDirection = lightSource.getL(intersection.point).scale(-1);

        // Construct the shifted shadow ray to avoid self-shadowing
        Ray shadowRay = new Ray(intersection.point, shadowDirection, intersection.normal);
        double lightDistance = lightSource.getDistance(shadowRay.origin());

        // BONUS 3 OPTIMIZATION: Pass lightDistance as maxDistance directly to calcIntersections!
        var intersections = _scene.geometries.calcIntersections(shadowRay, lightDistance);
        if (intersections == null) return Double3.ONE;

        Double3 ktr = Double3.ONE;

        for (Intersection intersect : intersections) {
            // Re-verifying within the loop to correctly accumulate coefficients
            if (alignZero(intersect.point.distance(shadowRay.origin()) - lightDistance) < 0) {
                ktr = ktr.product(intersect.material.kT);

                // Optimization: Stop if the shadow becomes completely opaque
                if (ktr.isLowerThan(MIN_CALC_COLOR_K)) {
                    return Double3.ZERO;
                }
            }
        }
        return ktr;
    }

    /**
     * Calculates the diffuse component of the light.
     */
    private Double3 calcDiffuse(Double3 kd, double nl) {
        return kd.scale(Math.abs(nl));
    }

    /**
     * Calculates the specular component of the light.
     */
    private Double3 calcSpecular(Double3 ks, Vector l, Vector n, double nl, Vector v, int nShininess) {
        Vector r = l.subtract(n.scale(2 * nl));
        double minusVR = alignZero(v.dotProduct(r) * -1);
        if (minusVR <= 0) return Double3.ZERO;
        return ks.scale(Math.pow(minusVR, nShininess));
    }
}