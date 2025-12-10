package academy.processor;

import static org.assertj.core.api.Assertions.assertThat;

import academy.model.FractalImage;
import academy.model.Pixel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ImageProcessorTest {

    @TempDir
    Path tempDir;

    @Test
    void imageProcessor_shouldSaveImageFile() throws IOException {
        // Arrange
        ImageProcessor processor = new ImageProcessor();
        FractalImage image = createTestImage(100, 100);
        Path outputPath = tempDir.resolve("test.png");

        // Act
        processor.save(image, outputPath, false, 2.2);

        // Assert
        assertThat(outputPath).exists();
        assertThat(Files.size(outputPath)).isGreaterThan(0);
    }

    @Test
    void imageProcessor_shouldSaveValidPngImage() throws IOException {
        // Arrange
        ImageProcessor processor = new ImageProcessor();
        FractalImage image = createTestImage(50, 50);
        Path outputPath = tempDir.resolve("valid.png");

        // Act
        processor.save(image, outputPath, false, 2.2);

        // Assert
        BufferedImage loadedImage = ImageIO.read(outputPath.toFile());
        assertThat(loadedImage).isNotNull();
        assertThat(loadedImage.getWidth()).isEqualTo(50);
        assertThat(loadedImage.getHeight()).isEqualTo(50);
    }

    @Test
    void imageProcessor_shouldApplyGammaCorrection() throws IOException {
        // Arrange
        ImageProcessor processor = new ImageProcessor();
        FractalImage image = createTestImage(50, 50);
        Path outputPath = tempDir.resolve("gamma.png");

        // Act
        processor.save(image, outputPath, true, 2.2);

        // Assert
        assertThat(outputPath).exists();
        BufferedImage loadedImage = ImageIO.read(outputPath.toFile());
        assertThat(loadedImage).isNotNull();
    }

    @Test
    void imageProcessor_shouldHandleEmptyImage() throws IOException {
        // Arrange
        ImageProcessor processor = new ImageProcessor();
        FractalImage image = new FractalImage(50, 50);
        Path outputPath = tempDir.resolve("empty.png");

        // Act
        processor.save(image, outputPath, false, 2.2);

        // Assert
        assertThat(outputPath).exists();
        BufferedImage loadedImage = ImageIO.read(outputPath.toFile());
        assertThat(loadedImage).isNotNull();
    }

    @Test
    void imageProcessor_shouldHandleDifferentGammaValues() throws IOException {
        // Arrange
        ImageProcessor processor = new ImageProcessor();
        FractalImage image = createTestImage(50, 50);
        Path outputPath1 = tempDir.resolve("gamma1.png");
        Path outputPath2 = tempDir.resolve("gamma2.png");

        // Act
        processor.save(image, outputPath1, true, 1.5);
        processor.save(image, outputPath2, true, 3.0);

        // Assert
        assertThat(outputPath1).exists();
        assertThat(outputPath2).exists();
    }

    @Test
    void imageProcessor_shouldClampColorValues() throws IOException {
        // Arrange
        ImageProcessor processor = new ImageProcessor();
        FractalImage image = new FractalImage(10, 10);

        // Add pixel with high color values
        Pixel pixel = image.pixel(5, 5);
        pixel.hit(300, 400, 500); // Values exceeding 255

        Path outputPath = tempDir.resolve("clamped.png");

        // Act
        processor.save(image, outputPath, false, 2.2);

        // Assert
        assertThat(outputPath).exists();
        BufferedImage loadedImage = ImageIO.read(outputPath.toFile());
        int rgb = loadedImage.getRGB(5, 5);

        // Extract RGB components
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        // Should be clamped to 255
        assertThat(r).isLessThanOrEqualTo(255);
        assertThat(g).isLessThanOrEqualTo(255);
        assertThat(b).isLessThanOrEqualTo(255);
    }

    private FractalImage createTestImage(int width, int height) {
        FractalImage image = new FractalImage(width, height);

        // Add some sample hits
        for (int y = 0; y < height; y += 10) {
            for (int x = 0; x < width; x += 10) {
                Pixel pixel = image.pixel(x, y);
                if (pixel != null) {
                    pixel.hit(255, 128, 64);
                    pixel.hit(100, 200, 150);
                }
            }
        }

        return image;
    }
}
