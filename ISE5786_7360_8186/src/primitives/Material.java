package primitives;

/**
 * Material class representing the material properties of a geometry.
 * Currently supports only the ambient light attenuation factor (kA).
 * This is a Passive Data Structure (PDS).
 */
public class Material {
    /**
     * Ambient light attenuation factor (kA).
     * Initialized to Double3.ONE by default to maintain backward compatibility.
     */
    public Double3 kA = Double3.ONE;

    /**
     * Setter for kA using a Double3 value - Method Chaining.
     *
     * @param kA the attenuation factor
     * @return the material object itself
     */
    public Material setKa(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Setter for kA using a double value (creates a Double3) - Method Chaining.
     *
     * @param kA the attenuation factor as a double
     * @return the material object itself
     */
    public Material setKa(double kA) {
        this.kA = new Double3(kA);
        return this;
    }
}