package lighting;

import primitives.Color;

/**
 * Ambient light for the scene to represent indirect light.
 * The class is immutable.
 */
public class AmbientLight {
    /**
     * The intensity of the light
     */
    private final Color intensity;

    /**
     * Static constant for no ambient light (black color)
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructor for ambient light.
     *
     * @param intensity the intensity (color) of the light
     */
    public AmbientLight(Color intensity) {
        this.intensity = intensity;
    }

    /**
     * Getter for the intensity of the light.
     *
     * @return the light intensity
     */
    public Color getIntensity() {
        return intensity;
    }
}