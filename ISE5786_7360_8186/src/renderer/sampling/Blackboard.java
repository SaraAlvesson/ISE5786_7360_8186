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
    private SamplePattern pattern = SamplePattern.GRID; // ברירת מחדל שמבטיחה ששום טסט ישן לא ישתנה
    private int numOfSamples = 1;

    public Blackboard() {
        // Default constructor
    }

    public Blackboard setShape(AreaShape shape) {
        this.shape = shape;
        return this;
    }

    public Blackboard setPattern(SamplePattern pattern) {
        this.pattern = pattern;
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

        // פיצוי מתמטי לעיגול (עמוד 10): חוסמים בריבוע ומסננים. כדי לשמור על כמות דגימות נכונה באלומה
        // אנו מגדילים את ממד הרשת פי שורש של (4 חלקי פאי) שזה בערך 1.1284
        int adjustedSamples = (shape == AreaShape.CIRCLE) ? (int) Math.ceil(numOfSamples * 1.1284) : numOfSamples;
        double step = 1.0 / adjustedSamples;

        // 1. תבנית דגימה סטוכסטית (אקראית לחלוטין באזור)
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

        // 2. תבנית רשת (Grid) ורשת מורעדת (Jittered)
        for (int i = 0; i < adjustedSamples; i++) {
            for (int j = 0; j < adjustedSamples; j++) {
                double xOffset;
                double yOffset;

                if (pattern == SamplePattern.JITTERED) {
                    // הוספת הזזה אקראית קטנה מוגבלת בתוך גבולות תא הרשת הנוכחי
                    double randX = Math.random() - 0.5;
                    double randY = Math.random() - 0.5;
                    xOffset = -0.5 + (j + 0.5 + randX) * step;
                    yOffset = -0.5 + (i + 0.5 + randY) * step;
                } else {
                    // תבנית רשת רגילה וקבועה
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