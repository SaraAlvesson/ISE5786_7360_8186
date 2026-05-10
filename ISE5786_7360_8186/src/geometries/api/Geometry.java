package geometries.api;

import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;

/**
 * Abstract class for all geometric shapes.
 * Inherits from Intersectable to allow intersection calculations.
 */
public abstract class Geometry extends Intersectable {
    /**
     * Emission color of the geometry, initialized to black by default.
     */
    private Color _emission = Color.BLACK;

    /**
     * Material of the geometry, initialized to default material.
     */
    private Material _material = new Material();

    /**
     * Get the normal vector to the shape at a specific point.
     *
     * @param point point on the surface
     * @return normal vector
     */
    public abstract Vector getNormal(Point point);

    /**
     * Getter for the emission color.
     *
     * @return the emission color
     */
    public Color getEmission() {
        return _emission;
    }

    /**
     * Setter for the emission color - Method Chaining (Builder-like).
     *
     * @param emission the emission color to set
     * @return the geometry object itself
     */
    public Geometry setEmission(Color emission) {
        this._emission = emission;
        return this;
    }

    /**
     * Getter for the material of the geometry.
     * * @return the material
     */
    public Material getMaterial() {
        return _material;
    }

    /**
     * Setter for the material - Method Chaining (Builder-like).
     * * @param material the material to set
     *
     * @return the geometry object itself
     */
    public Geometry setMaterial(Material material) {
        this._material = material;
        return this;
    }
}