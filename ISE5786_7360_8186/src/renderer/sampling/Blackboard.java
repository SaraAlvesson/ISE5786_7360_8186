package renderer.sampling;

import java.util.ArrayList;
import java.util.List;

/**
 * Object representing a 2D point offset for sampling.
 */
record Point2D(double x, double y) {
}

/**
 * Blackboard class responsible for generating 2D sample points within a target area.
 */
public class Blackboard {

    public enum AreaShape {
        SQUARE, CIRCLE
    }

    private AreaShape shape = AreaShape.SQUARE;
    private int numOfSamples = 1;

    public Blackboard() {
        // Default constructor
    }

    public Blackboard setShape(AreaShape shape) {
        this.shape = shape;
        return this;
    }

    public Blackboard setNumOfSamples(int numOfSamples) {
        if (numOfSamples < 1) {
            throw new IllegalArgumentException("Number of samples must be at least 1");
        }
        this.numOfSamples = numOfSamples;
        return this;
    }

    public List<Point2D> generate2DPoints() {
        List<Point2D> points = new ArrayList<>();

        if (numOfSamples == 1) {
            points.add(new Point2D(0, 0));
            return points;
        }

        double step = 1.0 / numOfSamples;

        for (int i = 0; i < numOfSamples; i++) {
            for (int j = 0; j < numOfSamples; j++) {
                double xOffset = -0.5 + (j + 0.5) * step;
                double yOffset = -0.5 + (i + 0.5) * step;

                if (shape == AreaShape.CIRCLE) {
                    if ((xOffset * xOffset + yOffset * yOffset) <= 0.25) {
                        points.add(new Point2D(xOffset, yOffset));
                    }
                } else {
                    points.add(new Point2D(xOffset, yOffset));
                }
            }
        }

        return points;
    }
}