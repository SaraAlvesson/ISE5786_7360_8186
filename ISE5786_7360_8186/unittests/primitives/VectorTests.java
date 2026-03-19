package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link primitives.Vector} class.
 *
 * @author Sara
 */
class VectorTests {
    /**
     * Delta value for accuracy when comparing double values.
     */
    private static final double DELTA = 1e-6;

    /**
     * Error message for zero vector construction
     */
    private static final String ERROR_ZERO_VECTOR = "ERROR: Vector(0,0,0) should throw an exception";

    /**
     * Error message for valid vector construction
     */
    private static final String ERROR_VECTOR_CONSTRUCTION = "ERROR: Vector construction should not throw an exception";

    /**
     * Error message for length squared calculation
     */
    private static final String ERROR_LENGTH_SQ = "ERROR: lengthSquared() wrong value";

    /**
     * Error message for length calculation
     */
    private static final String ERROR_LENGTH = "ERROR: length() wrong value";

    /**
     * Error message for vector addition logic
     */
    private static final String ERROR_ADD = "ERROR: Vector addition does not work correctly";

    /**
     * Error message for addition resulting in zero vector
     */
    private static final String ERROR_ADD_ZERO = "ERROR: Vector + -Vector does not throw an exception";

    /**
     * Error message for vector subtraction logic
     */
    private static final String ERROR_SUBTRACT = "ERROR: Vector subtraction does not work correctly";

    /**
     * Error message for subtraction resulting in zero vector
     */
    private static final String ERROR_SUBTRACT_ZERO = "ERROR: Vector - Vector does not throw an exception";

    /**
     * Error message for scaling logic
     */
    private static final String ERROR_SCALE = "ERROR: Vector scale() does not work correctly";

    /**
     * Error message for scaling by zero
     */
    private static final String ERROR_SCALE_ZERO = "ERROR: scale by 0 does not throw an exception";

    /**
     * Error message for dot product calculation
     */
    private static final String ERROR_DOT_PRODUCT = "ERROR: dotProduct() wrong value";

    /**
     * Error message for cross product calculation
     */
    private static final String ERROR_CROSS_PRODUCT = "ERROR: crossProduct() wrong result";

    /**
     * Error message for cross product of parallel vectors
     */
    private static final String ERROR_CROSS_PRODUCT_PARALLEL = "ERROR: crossProduct() for parallel vectors does not throw exception";

    /**
     * Error message for normalization logic
     */
    private static final String ERROR_NORMALIZE = "ERROR: normalize() does not work correctly";

    /**
     * Test method for {@link primitives.Vector#Vector(double, double, double)}.
     */
    @Test
    void testVector() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that a valid vector is constructed correctly
        assertDoesNotThrow(() -> new Vector(1, 2, 3), ERROR_VECTOR_CONSTRUCTION);

        // =============== Boundary Values Tests ==================
        // BV01: Test that zero vector throws exception
        assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0), ERROR_ZERO_VECTOR);
    }

    /**
     * Test method for {@link primitives.Vector#Vector(Double3)}.
     */
    @Test
    void testVectorDouble3() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Test that a valid vector is constructed correctly from Double3
        assertDoesNotThrow(() -> new Vector(new Double3(1, 2, 3)), ERROR_VECTOR_CONSTRUCTION);

        // =============== Boundary Values Tests ==================
        // BV01: Test that zero vector from Double3 throws exception
        assertThrows(IllegalArgumentException.class, () -> new Vector(Double3.ZERO), ERROR_ZERO_VECTOR);
    }

    /**
     * Test method for {@link primitives.Vector#lengthSquared()}.
     */
    @Test
    void testLengthSquared() {
        Vector v = new Vector(1, 2, 3);
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple length squared calculation
        assertEquals(14d, v.lengthSquared(), DELTA, ERROR_LENGTH_SQ);

        // =============== Boundary Values Tests ==================
        // BV01: Test length squared of a unit vector
        assertEquals(1d, new Vector(0, 0, 1).lengthSquared(), DELTA, ERROR_LENGTH_SQ);
    }

    /**
     * Test method for {@link primitives.Vector#length()}.
     */
    @Test
    void testLength() {
        Vector v = new Vector(0, 3, 4);
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple length calculation (Pythagorean triple)
        assertEquals(5d, v.length(), DELTA, ERROR_LENGTH);

        // =============== Boundary Values Tests ==================
        // BV01: Test length of a unit vector
        assertEquals(1d, new Vector(0, 0, 1).length(), DELTA, ERROR_LENGTH);
    }

    /**
     * Test method for {@link primitives.Vector#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        Vector v1 = new Vector(1, 2, 3);
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple vector addition
        assertEquals(new Vector(2, 4, 6), v1.add(new Vector(1, 2, 3)), ERROR_ADD);

        // =============== Boundary Values Tests ==================
        // BV01: Addition that results in zero vector
        assertThrows(IllegalArgumentException.class, () -> v1.add(new Vector(-1, -2, -3)), ERROR_ADD_ZERO);
    }

    /**
     * Test method for {@link primitives.Vector#subtract(primitives.Point)}.
     */
    @Test
    void testSubtract() {
        Vector v1 = new Vector(1, 2, 3);
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple vector subtraction
        assertEquals(new Vector(1, 1, 1), new Vector(2, 3, 4).subtract(v1), ERROR_SUBTRACT);

        // =============== Boundary Values Tests ==================
        // BV01: Subtraction that results in zero vector
        assertThrows(IllegalArgumentException.class, () -> v1.subtract(v1), ERROR_SUBTRACT_ZERO);
    }

    /**
     * Test method for {@link primitives.Vector#scale(double)}.
     */
    @Test
    void testScale() {
        Vector v1 = new Vector(1, 2, 3);
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple scaling
        assertEquals(new Vector(2, 4, 6), v1.scale(2d), ERROR_SCALE);

        // =============== Boundary Values Tests ==================
        // BV01: Scale by 0 should throw exception
        assertThrows(IllegalArgumentException.class, () -> v1.scale(0d), ERROR_SCALE_ZERO);
    }

    /**
     * Test method for {@link primitives.Vector#dotProduct(primitives.Vector)}.
     */
    @Test
    void testDotProduct() {
        Vector v1 = new Vector(1, 2, 3);
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple dot product
        assertEquals(-28d, v1.dotProduct(new Vector(-2, -4, -6)), DELTA, ERROR_DOT_PRODUCT);

        // =============== Boundary Values Tests ==================
        // BV01: Dot product of orthogonal vectors is zero
        assertEquals(0d, v1.dotProduct(new Vector(0, 3, -2)), DELTA, ERROR_DOT_PRODUCT);
    }

    /**
     * Test method for {@link primitives.Vector#crossProduct(primitives.Vector)}.
     */
    @Test
    void testCrossProduct() {
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(0, 3, -2);
        // ============ Equivalence Partitions Tests ==============
        // EP01: Test cross product length and orthogonality
        Vector v3 = v1.crossProduct(v2);
        assertEquals(v1.length() * v2.length(), v3.length(), DELTA, ERROR_CROSS_PRODUCT);
        assertEquals(0d, v3.dotProduct(v1), DELTA, ERROR_CROSS_PRODUCT);

        // =============== Boundary Values Tests ==================
        // BV01: Cross product of parallel vectors should throw exception
        assertThrows(IllegalArgumentException.class, () -> v1.crossProduct(v1.scale(2)), ERROR_CROSS_PRODUCT_PARALLEL);
    }

    /**
     * Test method for {@link primitives.Vector#normalize()}.
     */
    @Test
    void testNormalize() {
        Vector v = new Vector(1, 2, 3);
        // ============ Equivalence Partitions Tests ==============
        // EP01: Normalized vector length is 1
        Vector u = v.normalize();
        assertEquals(1d, u.length(), DELTA, ERROR_NORMALIZE);

        // EP02: Parallel and same direction (cross product is zero, dot product is positive)
        assertThrows(IllegalArgumentException.class, () -> v.crossProduct(u), ERROR_CROSS_PRODUCT_PARALLEL);
        assertEquals(v.length(), v.dotProduct(u), DELTA, ERROR_NORMALIZE);

        // =============== Boundary Values Tests ==================
        // BV01: Test normalizing a vector that is already of length 1
        Vector vUnit = new Vector(0, 0, 1);
        assertEquals(vUnit, vUnit.normalize(), ERROR_NORMALIZE);
    }
}