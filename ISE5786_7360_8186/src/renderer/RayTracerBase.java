package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

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
}