package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

/**
 * Unit tests for ImageWriter class.
 * Demonstrates basic image creation with a grid pattern.
 */
class ImageWriterTests {

    /**
     * Test method for creating a simple grid image.
     * Generates an 800x500 image with 16x10 squares (50x50 pixels each).
     */
    @Test
    void testImageWriter() {
        // Constants for the image configuration
        final int nX = 800;
        final int nY = 500;
        final int step = 50;

        // Colors for background and grid lines (high contrast)
        Color background = new Color(255, 255, 0); // Yellow
        Color gridColor = new Color(255, 0, 0);    // Red

        // Create the ImageWriter object
        ImageWriter imageWriter = new ImageWriter(nX, nY);

        // Loop over all pixels to color them
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                // Color grid lines every 'step' pixels, otherwise use background
                if (i % step == 0 || j % step == 0) {
                    imageWriter.writePixel(j, i, gridColor);
                } else {
                    imageWriter.writePixel(j, i, background);
                }
            }
        }

        // Write the matrix of pixels to a PNG file in the images folder
        imageWriter.writeToImage("yellow_red_grid");
    }
}