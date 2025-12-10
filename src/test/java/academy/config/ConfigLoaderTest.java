package academy.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.model.FractalConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConfigLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void configLoader_shouldUseCliParametersOverDefaults() {
        // Act
        FractalConfig config = ConfigLoader.load(
                null, // no config file
                800,
                600,
                999L,
                5000,
                "custom.png",
                2,
                null,
                null,
                false,
                1.5);

        // Assert
        assertThat(config.width()).isEqualTo(800);
        assertThat(config.height()).isEqualTo(600);
        assertThat(config.seed()).isEqualTo(999L);
        assertThat(config.iterationCount()).isEqualTo(5000);
        assertThat(config.outputPath()).isEqualTo("custom.png");
        assertThat(config.threads()).isEqualTo(2);
        assertThat(config.gammaCorrection()).isFalse();
        assertThat(config.gamma()).isEqualTo(1.5);
    }

    @Test
    void configLoader_shouldUseDefaults() {
        // Act
        FractalConfig config = ConfigLoader.load(null, null, null, null, null, null, null, null, null, null, null);

        // Assert
        assertThat(config.width()).isEqualTo(1920);
        assertThat(config.height()).isEqualTo(1080);
        assertThat(config.seed()).isEqualTo(5L);
        assertThat(config.iterationCount()).isEqualTo(2500);
        assertThat(config.outputPath()).isEqualTo("result.png");
        assertThat(config.threads()).isEqualTo(1);
        assertThat(config.gammaCorrection()).isTrue();
        assertThat(config.gamma()).isEqualTo(2.2);
    }

    @Test
    void configLoader_shouldLoadFromJsonFile() throws IOException {
        // Arrange
        String jsonContent =
                """
                {
                    "size": {"width": 640, "height": 480},
                    "iteration_count": 3000,
                    "output_path": "fractal.png",
                    "threads": 4,
                    "seed": 12345,
                    "gamma_correction": true,
                    "gamma": 2.5,
                    "functions": [
                        {"name": "linear", "weight": 1.0}
                    ],
                    "affine_params": [
                        {"a": 1.0, "b": 0.0, "c": 0.0, "d": 0.0, "e": 1.0, "f": 0.0}
                    ]
                }
                """;
        Path configPath = tempDir.resolve("config.json");
        Files.writeString(configPath, jsonContent);

        // Act
        FractalConfig config =
                ConfigLoader.load(configPath.toString(), null, null, null, null, null, null, null, null, null, null);

        // Assert
        assertThat(config.width()).isEqualTo(640);
        assertThat(config.height()).isEqualTo(480);
        assertThat(config.iterationCount()).isEqualTo(3000);
        assertThat(config.seed()).isEqualTo(12345L);
        assertThat(config.threads()).isEqualTo(4);
    }

    @Test
    void configLoader_shouldPrioritizeCliOverJson() throws IOException {
        // Arrange
        String jsonContent =
                """
                {
                    "size": {"width": 640, "height": 480},
                    "iteration_count": 3000,
                    "functions": [
                        {"name": "linear", "weight": 1.0}
                    ],
                    "affine_params": [
                        {"a": 1.0, "b": 0.0, "c": 0.0, "d": 0.0, "e": 1.0, "f": 0.0}
                    ]
                }
                """;
        Path configPath = tempDir.resolve("config.json");
        Files.writeString(configPath, jsonContent);

        // Act - CLI width should override JSON width
        FractalConfig config = ConfigLoader.load(
                configPath.toString(),
                1000, // CLI width (should override)
                null, // height from JSON
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        // Assert
        assertThat(config.width()).isEqualTo(1000); // From CLI
        assertThat(config.height()).isEqualTo(480); // From JSON
    }

    @Test
    void configLoader_shouldThrowExceptionForNonExistentFile() {
        // Act & Assert
        assertThatThrownBy(() -> ConfigLoader.load(
                        "non_existent_file.json", null, null, null, null, null, null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Config file not found");
    }

    @Test
    void configLoader_shouldParseAffineFunctionsFromJson() throws IOException {
        // Arrange
        String jsonContent =
                """
                {
                    "size": {"width": 100, "height": 100},
                    "functions": [
                        {"name": "sinusoidal", "weight": 1.0},
                        {"name": "spherical", "weight": 0.5}
                    ],
                    "affine_params": [
                        {"a": 0.5, "b": 0.3, "c": 0.1, "d": 0.2, "e": 0.6, "f": 0.05},
                        {"a": -0.5, "b": 0.5, "c": 0.2, "d": 0.3, "e": -0.4, "f": 0.1}
                    ]
                }
                """;
        Path configPath = tempDir.resolve("config.json");
        Files.writeString(configPath, jsonContent);

        // Act
        FractalConfig config =
                ConfigLoader.load(configPath.toString(), null, null, null, null, null, null, null, null, null, null);

        // Assert
        assertThat(config.affineTransformations()).hasSize(2);
        assertThat(config.weightedFunctions()).hasSize(2);
    }
}
