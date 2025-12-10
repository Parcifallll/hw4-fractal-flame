package academy.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FractalImageTest {

    @Test
    void fractalImage_shouldCreateWithCorrectDimensions() {
        // Act
        FractalImage image = new FractalImage(1920, 1080);

        // Assert
        assertThat(image.width()).isEqualTo(1920);
        assertThat(image.height()).isEqualTo(1080);
    }

    @Test
    void fractalImage_shouldReturnValidPixelWithinBounds() {
        // Arrange
        FractalImage image = new FractalImage(100, 100);

        // Act
        Pixel pixel = image.pixel(50, 50);

        // Assert
        assertThat(pixel).isNotNull();
    }

    @Test
    void fractalImage_shouldReturnNullForPixelOutOfBounds() {
        // Arrange
        FractalImage image = new FractalImage(100, 100);

        // Act & Assert
        assertThat(image.pixel(-1, 50)).isNull();
        assertThat(image.pixel(50, -1)).isNull();
        assertThat(image.pixel(100, 50)).isNull();
        assertThat(image.pixel(50, 100)).isNull();
    }

    @Test
    void fractalImage_shouldReturnSamePixelInstance() {
        // Arrange
        FractalImage image = new FractalImage(100, 100);

        // Act
        Pixel pixel1 = image.pixel(50, 50);
        Pixel pixel2 = image.pixel(50, 50);

        // Assert
        assertThat(pixel1).isSameAs(pixel2);
    }

    @Test
    void fractalImage_shouldReturnAllPixelsViaData() {
        // Arrange
        FractalImage image = new FractalImage(10, 10);

        // Act
        Pixel[] data = image.getData();

        // Assert
        assertThat(data).hasSize(100); // 10 * 10 = 100
        assertThat(data[0]).isNotNull();
        assertThat(data[55]).isNotNull(); // row 5, col 5 = 5*10 + 5 = 55
    }
}
