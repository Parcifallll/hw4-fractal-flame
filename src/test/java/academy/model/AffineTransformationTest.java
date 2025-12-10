package academy.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;

class AffineTransformationTest {

    @Test
    void affineTransformation_shouldTransformCoordinates() {
        // Arrange
        AffineTransformation affine = new AffineTransformation(1.0, 0.0, 1.0, 0.0, 1.0, 2.0, 255, 128, 64);
        Point point = new Point(2.0, 3.0);

        // Act
        Point result = affine.apply(point);

        // Assert
        // x' = a*x + b*y + c = 1.0*2.0 + 0.0*3.0 + 1.0 = 3.0
        // y' = d*x + e*y + f = 0.0*2.0 + 1.0*3.0 + 2.0 = 5.0
        assertThat(result.x()).isCloseTo(3.0, within(0.001));
        assertThat(result.y()).isCloseTo(5.0, within(0.001));
    }

    @Test
    void affineTransformation_shouldAverageColors() {
        // Arrange
        AffineTransformation affine = new AffineTransformation(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 200, 100, 50);
        Point point = new Point(0.0, 0.0, 100, 50, 25);

        // Act
        Point result = affine.apply(point);

        // Assert
        // Colors should be averaged: (point.color + affine.color) / 2
        assertThat(result.r()).isEqualTo(150); // (100 + 200) / 2
        assertThat(result.g()).isEqualTo(75); // (50 + 100) / 2
        assertThat(result.b()).isEqualTo(37); // (25 + 50) / 2
    }

    @Test
    void affineTransformation_shouldScaleCoordinates() {
        // Arrange
        AffineTransformation affine = new AffineTransformation(2.0, 0.0, 0.0, 0.0, 2.0, 0.0);
        Point point = new Point(1.0, 1.0);

        // Act
        Point result = affine.apply(point);

        // Assert
        assertThat(result.x()).isCloseTo(2.0, within(0.001));
        assertThat(result.y()).isCloseTo(2.0, within(0.001));
    }

    @Test
    void affineTransformation_shouldRotateCoordinates() {
        // Arrange
        // 90 degree rotation: cos(90°) = 0, sin(90°) = 1
        AffineTransformation affine = new AffineTransformation(0.0, -1.0, 0.0, 1.0, 0.0, 0.0);
        Point point = new Point(1.0, 0.0);

        // Act
        Point result = affine.apply(point);

        // Assert
        // After 90° rotation: (1, 0) -> (0, 1)
        assertThat(result.x()).isCloseTo(0.0, within(0.001));
        assertThat(result.y()).isCloseTo(1.0, within(0.001));
    }

    @Test
    void affineTransformation_shouldUseDefaultColors() {
        // Arrange
        AffineTransformation affine = new AffineTransformation(1.0, 0.0, 0.0, 0.0, 1.0, 0.0);

        // Assert
        assertThat(affine.red()).isEqualTo(0);
        assertThat(affine.green()).isEqualTo(0);
        assertThat(affine.blue()).isEqualTo(0);
    }
}
