package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Class representing directional light (like the sun).
 * Inherits from Light and implements LightSource.
 */
public class DirectionalLight extends Light implements LightSource {
    private final Vector direction;

    /**
     * Constructor for DirectionalLight.
     *
     * @param intensity the intensity of the light
     * @param direction the direction of the light rays
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        this.direction = direction.normalize();
    }

    @Override
    public Color getIntensity(Point p) {
        return _intensity;
    }

    @Override
    public Vector getL(Point p) {
        return direction;
    }

    @Override
    public double getDistance(Point point) {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public java.util.List<Vector> getBeamL(Point p, int numSamples) {
        java.util.List<Vector> singleBeam = new java.util.ArrayList<>(1);
        singleBeam.add(getL(p));
        return singleBeam;
    }
}