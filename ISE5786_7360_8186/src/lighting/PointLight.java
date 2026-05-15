package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Class representing a point light source (like a bulb).
 * A point light source emits light in all directions from a specific position in space.
 * It includes attenuation factors to model how light intensity decreases with distance.
 */
public class PointLight extends Light implements LightSource {
    /**
     * The position of the light source in the scene.
     */
    protected final Point position;

    /**
     * Constant attenuation factor (kc). Default value is 1.0 to ensure no division by zero.
     */
    private double kC = 1.0;

    /**
     * Linear attenuation factor (kl).
     */
    private double kL = 0.0;

    /**
     * Quadratic attenuation factor (kq).
     */
    private double kQ = 0.0;

    /**
     * Constructor for PointLight.
     *
     * @param intensity the initial intensity (color) of the light source
     * @param position  the position of the light source in the scene
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    /**
     * Setter for the constant attenuation factor (kc).
     *
     * @param kC the constant attenuation factor
     * @return the PointLight object itself for method chaining
     */
    public PointLight setKc(double kC) {
        this.kC = kC;
        return this;
    }

    /**
     * Setter for the linear attenuation factor (kl).
     *
     * @param kL the linear attenuation factor
     * @return the PointLight object itself for method chaining
     */
    public PointLight setKl(double kL) {
        this.kL = kL;
        return this;
    }

    /**
     * Setter for the quadratic attenuation factor (kq).
     *
     * @param kQ the quadratic attenuation factor
     * @return the PointLight object itself for method chaining
     */
    public PointLight setKq(double kQ) {
        this.kQ = kQ;
        return this;
    }


    @Override
    public Color getIntensity(Point p) {
        double d = position.distance(p);
        double attenuation = kC + kL * d + kQ * d * d;
        return _intensity.scale(1.0 / attenuation);
    }


    @Override
    public Vector getL(Point p) {
        return p.subtract(position).normalize();
    }
}