package lighting;

import primitives.Color;

/**
 * Abstract class for all light types, storing the original intensity.
 * No access modifier (package-private) as per instructions.
 */
abstract class Light {
    /**
     * The intensity (color) of the light source
     */
    protected final Color _intensity;

    /**
     * Protected constructor to initialize the light's intensity.
     *
     * @param intensity the initial color intensity
     */
    protected Light(Color intensity) {
        this._intensity = intensity;
    }

    /**
     * Getter for the original light intensity.
     *
     * @return the light intensity
     */
    public Color getIntensity() {
        return _intensity;
    }
}