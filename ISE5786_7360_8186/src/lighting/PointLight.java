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

    @Override
    public List<Vector> getBeamL(Point p, int numSamples) {
        // If Soft Shadows is disabled (radius is 0 or samples == 1), return a single directional vector
        if (isZero(radius) || numSamples <= 1) {
            List<Vector> singleBeam = new ArrayList<>(1);
            singleBeam.add(getL(p));
            return singleBeam;
        }

        // Vector from light center to the target point on geometry
        Vector vToPoint = p.subtract(position);

        // Initialize Blackboard to generate 2D offsets inside a CIRCLE area (representing a sphere light source projection)
        Blackboard blackboard = new Blackboard()
                .setShape(Blackboard.AreaShape.CIRCLE)
                .setNumOfSamples(numSamples);

        // Map the 2D sample points into 3D points on the plane of the light source facing the target point
        List<Point> lightSamples = TargetArea.convertTo3DPoints(
                position,
                vToPoint,
                radius * 2, // The bounding box size is the diameter of the circular area
                blackboard.generate2DPoints()
        );

        // Create normalized direction vectors from each point on the light source area towards the geometry point p
        List<Vector> beam = new ArrayList<>(lightSamples.size());
        for (Point samplePoint : lightSamples) {
            beam.add(p.subtract(samplePoint).normalize());
        }

        return beam;
    }
}