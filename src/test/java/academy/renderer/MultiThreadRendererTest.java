package academy.renderer;

import static org.assertj.core.api.Assertions.assertThat;

import academy.model.AffineTransformation;
import academy.model.FractalConfig;
import academy.model.FractalImage;
import academy.model.TransformationType;
import academy.model.WeightedFunction;
import java.util.List;
import org.junit.jupiter.api.Test;

class MultiThreadRendererTest {

    @Test
    void renderer_shouldGenerateImageWithCorrectDimensions() {
        // Arrange
        Renderer renderer = new MultiThreadRenderer();
        FractalConfig config = createTestConfig(100, 50, 4);

        // Act
        FractalImage image = renderer.render(config);

        // Assert
        assertThat(image.width()).isEqualTo(100);
        assertThat(image.height()).isEqualTo(50);
    }

    @Test
    void renderer_shouldGenerateNonEmptyImage() {
        // Arrange
        Renderer renderer = new MultiThreadRenderer();
        FractalConfig config = createTestConfig(100, 100, 4);

        // Act
        FractalImage image = renderer.render(config);

        // Assert
        boolean hasHits = false;
        for (int y = 0; y < image.height(); y++) {
            for (int x = 0; x < image.width(); x++) {
                if (image.pixel(x, y).isHit()) {
                    hasHits = true;
                    break;
                }
            }
            if (hasHits) {
                break;
            }
        }
        assertThat(hasHits).isTrue();
    }

    @Test
    void renderer_shouldWorkWithSingleThread() {
        // Arrange
        Renderer renderer = new MultiThreadRenderer();
        FractalConfig config = createTestConfig(50, 50, 1);

        // Act
        FractalImage image = renderer.render(config);

        // Assert
        assertThat(image).isNotNull();
        assertThat(image.width()).isEqualTo(50);
        assertThat(image.height()).isEqualTo(50);
    }

    @Test
    void renderer_shouldWorkWithManyThreads() {
        // Arrange
        Renderer renderer = new MultiThreadRenderer();
        FractalConfig config = createTestConfig(50, 50, 8);

        // Act
        FractalImage image = renderer.render(config);

        // Assert
        assertThat(image).isNotNull();
    }

    @Test
    void renderer_shouldAccumulateHitsFromAllThreads() {
        // Arrange
        Renderer renderer = new MultiThreadRenderer();
        FractalConfig config = createTestConfig(100, 100, 4);

        // Act
        FractalImage image = renderer.render(config);

        // Assert
        int totalHits = 0;
        for (int y = 0; y < image.height(); y++) {
            for (int x = 0; x < image.width(); x++) {
                totalHits += image.pixel(x, y).getHitCount();
            }
        }
        assertThat(totalHits).isPositive();
    }

    private FractalConfig createTestConfig(int width, int height, int threads) {
        List<AffineTransformation> affineTransformations = List.of(
                new AffineTransformation(0.5, 0.0, 0.0, 0.0, 0.5, 0.0, 255, 0, 0),
                new AffineTransformation(-0.5, 0.0, 0.5, 0.0, 0.5, 0.5, 0, 255, 0));

        List<WeightedFunction> functions = List.of(new WeightedFunction(TransformationType.LINEAR, 1.0));

        return new FractalConfig(
                width, height, 123L, 1000, "test.png", threads, affineTransformations, functions, true, 2.2);
    }
}
