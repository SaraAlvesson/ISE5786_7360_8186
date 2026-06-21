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

    public enum SamplePattern {
        GRID, STOCHASTIC, JITTERED
    }

    private AreaShape shape = AreaShape.SQUARE;
    private SamplePattern pattern = SamplePattern.GRID; // Default to ensure backward compatibility with old tests
    private int numOfSamples = 1;

    /**
     * Default constructor.
     */
    public Blackboard() {
        // Default constructor
    }

    /**
     * Sets the shape of the sampling area.
     *
     * @param shape The shape of the area (SQUARE or CIRCLE).
     * @return This Blackboard instance for method chaining.
     */
    public Blackboard setShape(AreaShape shape) {
        this.shape = shape;
        return this;
    }

    /**
     * Sets the sampling pattern to be used.
     *
     * @param pattern The sampling pattern (GRID, STOCHASTIC, or JITTERED).
     * @return This Blackboard instance for method chaining.
     */
    public Blackboard setPattern(SamplePattern pattern) {
        this.pattern = pattern;
        return this;
    }

    /**
     * Sets the number of samples (grid resolution) to generate.
     *
     * @param numOfSamples The base number of samples. Must be at least 1.
     * @return This Blackboard instance for method chaining.
     * @throws IllegalArgumentException if numOfSamples is less than 1.
     */
    public Blackboard setNumOfSamples(int numOfSamples) {
        if (numOfSamples < 1) {
            throw new IllegalArgumentException("Number of samples must be at least 1");
        }
        this.numOfSamples = numOfSamples;
        return this;
    }

    /**
     * Generates a list of 2D points based on the configured shape, pattern, and number of samples.
     *
     * @return A list of generated Point2D offsets.
     */
    public List<Point2D> generate2DPoints() {
        List<Point2D> points = new ArrayList<>();

        if (numOfSamples == 1) {
            points.add(new Point2D(0, 0));
            return points;
        }

        // Mathematical compensation for a circle: bounded by a square and filtered.
        // To maintain the correct number of samples in the beam, the grid dimension
        // is increased by the square root of (4 / pi), which is approximately 1.1284.
        int adjustedSamples = (shape == AreaShape.CIRCLE) ? (int) Math.ceil(numOfSamples * 1.1284) : numOfSamples;
        double step = 1.0 / adjustedSamples;

        // 1. Stochastic sampling pattern (completely random within the area)
        if (pattern == SamplePattern.STOCHASTIC) {
            int totalSamples = adjustedSamples * adjustedSamples;
            for (int k = 0; k < totalSamples; k++) {
                double xOffset = Math.random() - 0.5;
                double yOffset = Math.random() - 0.5;

                if (shape == AreaShape.CIRCLE) {
                    if ((xOffset * xOffset + yOffset * yOffset) <= 0.25) {
                        points.add(new Point2D(xOffset, yOffset));
                    }
                } else {
                    points.add(new Point2D(xOffset, yOffset));
                }
            }
            return points;
        }

        // 2. Grid and Jittered patterns
        for (int i = 0; i < adjustedSamples; i++) {
            for (int j = 0; j < adjustedSamples; j++) {
                double xOffset;
                double yOffset;

                if (pattern == SamplePattern.JITTERED) {
                    // Add a small random offset bounded within the current grid cell
                    double randX = Math.random() - 0.5;
                    double randY = Math.random() - 0.5;
                    xOffset = -0.5 + (j + 0.5 + randX) * step;
                    yOffset = -0.5 + (i + 0.5 + randY) * step;
                } else {
                    // Regular and fixed grid pattern
                    xOffset = -0.5 + (j + 0.5) * step;
                    yOffset = -0.5 + (i + 0.5) * step;
                }

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