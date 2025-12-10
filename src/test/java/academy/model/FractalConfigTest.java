package academy.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import org.junit.jupiter.api.Test;

class FractalConfigTest {

    @Test
    void config_shouldCreateWithValidParameters() {
        // Arrange
        List<AffineTransformation> affines = List.of(new AffineTransformation(1.0, 0.0, 0.0, 0.0, 1.0, 0.0));
        List<WeightedFunction> functions = List.of(new WeightedFunction(TransformationType.LINEAR, 1.0));

        // Act
        FractalConfig config =
                new FractalConfig(1920, 1080, 123L, 2500, "output.png", 4, affines, functions, true, 2.2);

        // Assert
        assertThat(config.width()).isEqualTo(1920);
        assertThat(config.height()).isEqualTo(1080);
        assertThat(config.seed()).isEqualTo(123L);
        assertThat(config.iterationCount()).isEqualTo(2500);
        assertThat(config.outputPath()).isEqualTo("output.png");
        assertThat(config.threads()).isEqualTo(4);
        assertThat(config.gammaCorrection()).isTrue();
        assertThat(config.gamma()).isEqualTo(2.2);
    }

    @Test
    void config_shouldCalculateTotalWeight() {
        // Arrange
        List<AffineTransformation> affines = List.of(new AffineTransformation(1.0, 0.0, 0.0, 0.0, 1.0, 0.0));
        List<WeightedFunction> functions = List.of(
                new WeightedFunction(TransformationType.LINEAR, 1.0),
                new WeightedFunction(TransformationType.SINUSOIDAL, 0.5),
                new WeightedFunction(TransformationType.SPHERICAL, 0.3));

        // Act
        FractalConfig config = new FractalConfig(100, 100, 123L, 1000, "output.png", 1, affines, functions, false, 2.2);

        // Assert
        assertThat(config.totalWeight()).isCloseTo(1.8, within(0.001));
    }

    @Test
    void config_shouldThrowExceptionForNegativeWidth() {
        // Arrange
        List<AffineTransformation> affines = List.of(new AffineTransformation(1.0, 0.0, 0.0, 0.0, 1.0, 0.0));
        List<WeightedFunction> functions = List.of(new WeightedFunction(TransformationType.LINEAR, 1.0));

        // Act & Assert
        assertThatThrownBy(
                        () -> new FractalConfig(-100, 100, 123L, 1000, "output.png", 1, affines, functions, true, 2.2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Width must be positive");
    }

    @Test
    void config_shouldThrowExceptionForNegativeHeight() {
        // Arrange
        List<AffineTransformation> affines = List.of(new AffineTransformation(1.0, 0.0, 0.0, 0.0, 1.0, 0.0));
        List<WeightedFunction> functions = List.of(new WeightedFunction(TransformationType.LINEAR, 1.0));

        // Act & Assert
        assertThatThrownBy(
                        () -> new FractalConfig(100, -100, 123L, 1000, "output.png", 1, affines, functions, true, 2.2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Height must be positive");
    }

    @Test
    void config_shouldThrowExceptionForNegativeIterationCount() {
        // Arrange
        List<AffineTransformation> affines = List.of(new AffineTransformation(1.0, 0.0, 0.0, 0.0, 1.0, 0.0));
        List<WeightedFunction> functions = List.of(new WeightedFunction(TransformationType.LINEAR, 1.0));

        // Act & Assert
        assertThatThrownBy(
                        () -> new FractalConfig(100, 100, 123L, -1000, "output.png", 1, affines, functions, true, 2.2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Iteration count must be positive");
    }

    @Test
    void config_shouldThrowExceptionForNegativeThreads() {
        // Arrange
        List<AffineTransformation> affines = List.of(new AffineTransformation(1.0, 0.0, 0.0, 0.0, 1.0, 0.0));
        List<WeightedFunction> functions = List.of(new WeightedFunction(TransformationType.LINEAR, 1.0));

        // Act & Assert
        assertThatThrownBy(
                        () -> new FractalConfig(100, 100, 123L, 1000, "output.png", -1, affines, functions, true, 2.2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Thread count must be positive");
    }

    @Test
    void config_shouldThrowExceptionForEmptyOutputPath() {
        // Arrange
        List<AffineTransformation> affines = List.of(new AffineTransformation(1.0, 0.0, 0.0, 0.0, 1.0, 0.0));
        List<WeightedFunction> functions = List.of(new WeightedFunction(TransformationType.LINEAR, 1.0));

        // Act & Assert
        assertThatThrownBy(() -> new FractalConfig(100, 100, 123L, 1000, "", 1, affines, functions, true, 2.2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Output path cannot be null or empty");
    }

    @Test
    void config_shouldThrowExceptionForEmptyAffineTransformations() {
        // Arrange
        List<WeightedFunction> functions = List.of(new WeightedFunction(TransformationType.LINEAR, 1.0));

        // Act & Assert
        assertThatThrownBy(
                        () -> new FractalConfig(100, 100, 123L, 1000, "output.png", 1, List.of(), functions, true, 2.2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("At least one affine transformation is required");
    }

    @Test
    void config_shouldThrowExceptionForEmptyFunctions() {
        // Arrange
        List<AffineTransformation> affines = List.of(new AffineTransformation(1.0, 0.0, 0.0, 0.0, 1.0, 0.0));

        // Act & Assert
        assertThatThrownBy(
                        () -> new FractalConfig(100, 100, 123L, 1000, "output.png", 1, affines, List.of(), true, 2.2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("At least one transformation function is required");
    }

    @Test
    void config_shouldThrowExceptionForNegativeGamma() {
        // Arrange
        List<AffineTransformation> affines = List.of(new AffineTransformation(1.0, 0.0, 0.0, 0.0, 1.0, 0.0));
        List<WeightedFunction> functions = List.of(new WeightedFunction(TransformationType.LINEAR, 1.0));

        // Act & Assert
        assertThatThrownBy(
                        () -> new FractalConfig(100, 100, 123L, 1000, "output.png", 1, affines, functions, true, -2.2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Gamma must be positive");
    }
}
