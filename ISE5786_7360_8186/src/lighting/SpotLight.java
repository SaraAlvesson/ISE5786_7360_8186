package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static primitives.Util.alignZero;

/**
 * Class representing a spotlight source (like a flashlight).
 */
public class SpotLight extends PointLight {
    private final Vector direction;
    /**
     * Focus factor for narrow beam (bonus)
     */
    private double _narrowBeam = 1;

    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }

    /**
     * Setter for narrow beam focus factor.
     * * @param narrowBeam exponent for narrow beam focus
     *
     * @return the spotlight object itself
     */
    public SpotLight setNarrowBeam(double narrowBeam) {
        this._narrowBeam = narrowBeam;
        return this;
    }

    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        Vector l = getL(p);
        double dirL = alignZero(direction.dotProduct(l));

        // If the point is outside the light cone, there is no light
        if (dirL <= 0) return Color.BLACK;

        // Base intensity from PointLight multiplied by (cos(theta) ^ narrowBeam)
        double factor = _narrowBeam == 1 ? dirL : Math.pow(dirL, _narrowBeam);
        return super.getIntensity(p).scale(factor);
    }
}