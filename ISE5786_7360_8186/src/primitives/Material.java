package primitives;

/**
 * Material class representing the material properties of a geometry.
 * This is a Passive Data Structure (PDS).
 */
public class Material {
    /**
     * Ambient light attenuation factor (kA).
     */
    public Double3 kA = Double3.ONE;

    /**
     * Diffuse reflection coefficient (kD).
     */
    public Double3 kD = Double3.ZERO;

    /**
     * Specular reflection coefficient (kS).
     */
    public Double3 kS = Double3.ZERO;

    /**
     * Transparency attenuation factor (kT).
     */
    public Double3 kT = Double3.ZERO;

    /**
     * Reflection attenuation factor (kR).
     */
    public Double3 kR = Double3.ZERO;

    /**
     * Shininess factor.
     */
    public int nShininess = 0;

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
     * Setter for kA using a double value - Method Chaining.
     *
     * @param kA the attenuation factor as a double
     * @return the material object itself
     */
    public Material setKa(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    /**
     * Setter for kD using a Double3 value - Method Chaining.
     *
     * @param kD the diffuse reflection coefficient
     * @return the material object itself
     */
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Setter for kD using a double value - Method Chaining.
     *
     * @param kD the diffuse reflection coefficient as a double
     * @return the material object itself
     */
    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Setter for kS using a Double3 value - Method Chaining.
     *
     * @param kS the specular reflection coefficient
     * @return the material object itself
     */
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Setter for kS using a double value - Method Chaining.
     *
     * @param kS the specular reflection coefficient as a double
     * @return the material object itself
     */
    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Setter for kT using a Double3 value - Method Chaining.
     *
     * @param kT the transparency attenuation factor
     * @return the material object itself
     */
    public Material setKT(Double3 kT) {
        this.kT = kT;
        return this;
    }

    /**
     * Setter for kT using a double value - Method Chaining.
     *
     * @param kT the transparency attenuation factor as a double
     * @return the material object itself
     */
    public Material setKT(double kT) {
        this.kT = new Double3(kT);
        return this;
    }

    /**
     * Setter for kR using a Double3 value - Method Chaining.
     *
     * @param kR the reflection attenuation factor
     * @return the material object itself
     */
    public Material setKR(Double3 kR) {
        this.kR = kR;
        return this;
    }

    /**
     * Setter for kR using a double value - Method Chaining.
     *
     * @param kR the reflection attenuation factor as a double
     * @return the material object itself
     */
    public Material setKR(double kR) {
        this.kR = new Double3(kR);
        return this;
    }

    /**
     * Setter for shininess - Method Chaining.
     *
     * @param nShininess the shininess factor
     * @return the material object itself
     */
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }
}