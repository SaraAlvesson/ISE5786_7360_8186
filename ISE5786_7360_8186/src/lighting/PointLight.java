package lighting;

import java.util.ArrayList;
import java.util.List;
import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.sampling.Blackboard;
import renderer.sampling.TargetArea;

import static primitives.Util.isZero;

/**
 * Class representing a point light source (like a bulb).
 * A point light source emits light in all directions from a specific position in space.
 * It includes attenuation factors to model how light intensity decreases with distance.
 * Extended to support spherical/area light source characteristics for Soft Shadows.
 */
public class PointLight extends Light implements LightSource {
    /**
     * The position of the light source in the scene.
     */
    protected final Point position;

    /**
     * The radius of the light source area.
     * Default value is 0.0, representing a purely mathematical point light.
     */
    private double radius = 0.0;

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
     * Setter for the radius of the light source area to enable Soft Shadows.
     *
     * @param radius the radius of the light source area
     * @return the PointLight object itself for method chaining
     */
    public PointLight setRadius(double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius cannot be negative");
        }
        this.radius = radius;
        return this;
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

    @Override
    public double getDistance(Point point) {
        return position.distance(point);
    }


    /**
     * Returns the actual 3D sample points on the light area for soft shadows.
     * If radius is zero or samples <= 1, this returns only the center of the light.
     * This is important because the ray tracer needs the real sample point distance,
     * not only the direction vector.
     *
     * @param p point on the geometry that receives light
     * @param numSamples number of samples per row/column
     * @return list of 3D sample points on the light source area
     */
    public List<Point> getSamplePoints(Point p, int numSamples) {
        if (isZero(radius) || numSamples <= 1) {
            List<Point> single = new ArrayList<>(1);
            single.add(position);
            return single;
        }

        Vector vToPoint = p.subtract(position);

        Blackboard blackboard = new Blackboard()
                .setShape(Blackboard.AreaShape.CIRCLE)
                .setNumOfSamples(numSamples);

        return TargetArea.convertTo3DPoints(
                position,
                vToPoint,
                radius * 2,
                blackboard.generate2DPoints()
        );
    }

    @Override
    public List<Vector> getBeamL(Point p, int numSamples) {
        List<Point> lightSamples = getSamplePoints(p, numSamples);
        List<Vector> beam = new ArrayList<>(lightSamples.size());
        for (Point samplePoint : lightSamples) {
            beam.add(p.subtract(samplePoint).normalize());
        }
        return beam;
    }
}