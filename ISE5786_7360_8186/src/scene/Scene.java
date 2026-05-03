package scene;

import geometries.impl.Geometries;
import lighting.AmbientLight;
import primitives.Color;

/**
 * Scene class representing a 3D scene containing geometries, background color and ambient light.
 * This is a Passive Data Structure (PDS).
 */
public class Scene {
    /**
     * Name of the scene
     */
    public String name;
    /**
     * Background color of the scene, initialized to black
     */
    public Color background = Color.BLACK;
    /**
     * Ambient light of the scene, initialized to NONE
     */
    public AmbientLight ambientLight = AmbientLight.NONE;
    /**
     * Geometries in the scene, initialized to an empty collection
     */
    public Geometries geometries = new Geometries();

    /**
     * Constructor for Scene.
     *
     * @param name the name of the scene
     */
    public Scene(String name) {
        this.name = name;
    }

    /**
     * Setter for the background color - Method Chaining.
     *
     * @param background the background color
     * @return the scene object itself
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Setter for the ambient light - Method Chaining.
     *
     * @param ambientLight the ambient light
     * @return the scene object itself
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Setter for the geometries - Method Chaining.
     *
     * @param geometries the geometries collection
     * @return the scene object itself
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }
}