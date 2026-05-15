package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Interface representing external light sources.
 */
public interface LightSource {
    /**
     * Calculates the intensity of the light at a specific point.
     *
     * @param p the point on the geometry
     * @return the color intensity at that point
     */
    public Color getIntensity(Point p);

    /**
     * Calculates the normalized direction vector from the light source to a point.
     *
     * @param p the point on the geometry
     * @return normalized direction vector (L)
     */
    public Vector getL(Point p);
}