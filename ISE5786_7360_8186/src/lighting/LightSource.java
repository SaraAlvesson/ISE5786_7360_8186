package lighting;

import java.util.List;
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

    /**
     * Calculates the distance from the light source to a given point.
     *
     * @param point the point on the geometry
     * @return the distance to the light source
     */
    public double getDistance(Point point);

    /**
     * Generates a beam of normalized direction vectors from the light source area
     * to a specific point on a geometry for soft shadows calculations.
     *
     * @param p          the point on the geometry
     * @param numSamples the number of samples across one dimension for the grid (NxN)
     * @return list of normalized direction vectors forming the beam
     */
    public List<Vector> getBeamL(Point p, int numSamples);
}