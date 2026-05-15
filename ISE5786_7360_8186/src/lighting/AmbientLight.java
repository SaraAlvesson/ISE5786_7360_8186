package lighting;

import primitives.Color;
import primitives.Double3;

/**
 * Ambient light for the scene to represent indirect light.
 * Inherits from Light to store the calculated intensity.
 */
public class AmbientLight extends Light {

    /**
     * Static constant for no ambient light (black color)
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK, Double3.ZERO);

    /**
     * Constructor for ambient light with default attenuation factor (1.0).
     * This supports backward compatibility with tests from previous stages.
     * * @param intensity the original color intensity
     */
    public AmbientLight(Color intensity) {
        super(intensity);
    }

    /**
     * Constructor for ambient light.
     * Activates the parent constructor with the final intensity (I_A * kA).
     *
     * @param IA the original color intensity
     * @param kA the attenuation factor
     */
    public AmbientLight(Color IA, Double3 kA) {
        super(IA.scale(kA));
    }

    /**
     * Constructor for ambient light with a scalar kA.
     *
     * @param IA the original color intensity
     * @param kA the attenuation factor as a double
     */
    public AmbientLight(Color IA, double kA) {
        super(IA.scale(kA));
    }
}