package academy.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import academy.model.AffineTransformation;
import academy.model.TransformationType;
import academy.model.WeightedFunction;
import java.util.List;
import org.junit.jupiter.api.Test;

class CliParserTest {

    @Test
    void parseAffineParams_shouldParseValidInput() {
        // Arrange
        String input = "1.0,0.0,0.0,0.0,1.0,0.0";

        // Act
        List<AffineTransformation> result = CliParser.parseAffineParams(input, 123L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).a()).isCloseTo(1.0, within(0.001));
        assertThat(result.get(0).b()).isCloseTo(0.0, within(0.001));
        assertThat(result.get(0).c()).isCloseTo(0.0, within(0.001));
        assertThat(result.get(0).d()).isCloseTo(0.0, within(0.001));
        assertThat(result.get(0).e()).isCloseTo(1.0, within(0.001));
        assertThat(result.get(0).f()).isCloseTo(0.0, within(0.001));
    }

    @Test
    void parseAffineParams_shouldParseMultipleTransformations() {
        // Arrange
        String input = "1.0,0.0,0.0,0.0,1.0,0.0/0.5,0.5,0.5,0.5,0.5,0.5";

        // Act
        List<AffineTransformation> result = CliParser.parseAffineParams(input, 123L);

        // Assert
        assertThat(result).hasSize(2);
    }

    @Test
    void parseAffineParams_shouldGenerateDefaultWhenNull() {
        // Act
        List<AffineTransformation> result = CliParser.parseAffineParams(null, 123L);

        // Assert
        assertThat(result).hasSize(3);
    }

    @Test
    void parseAffineParams_shouldGenerateDefaultWhenBlank() {
        // Act
        List<AffineTransformation> result = CliParser.parseAffineParams("", 123L);

        // Assert
        assertThat(result).hasSize(3);
    }

    @Test
    void parseAffineParams_shouldThrowExceptionForInvalidCoeffCount() {
        // Arrange
        String input = "1.0,0.0,0.0";

        // Act & Assert
        assertThatThrownBy(() -> CliParser.parseAffineParams(input, 123L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must have 6 coefficients");
    }

    @Test
    void parseAffineParams_shouldThrowExceptionForInvalidNumber() {
        // Arrange
        String input = "abc,0.0,0.0,0.0,1.0,0.0";

        // Act & Assert
        assertThatThrownBy(() -> CliParser.parseAffineParams(input, 123L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid affine coefficient");
    }

    @Test
    void parseFunctions_shouldParseValidInput() {
        // Arrange
        String input = "linear:1.0,sinusoidal:0.5";

        // Act
        List<WeightedFunction> result = CliParser.parseFunctions(input);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).type()).isEqualTo(TransformationType.LINEAR);
        assertThat(result.get(0).weight()).isCloseTo(1.0, within(0.001));
        assertThat(result.get(1).type()).isEqualTo(TransformationType.SINUSOIDAL);
        assertThat(result.get(1).weight()).isCloseTo(0.5, within(0.001));
    }

    @Test
    void parseFunctions_shouldReturnDefaultWhenNull() {
        // Act
        List<WeightedFunction> result = CliParser.parseFunctions(null);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).type()).isEqualTo(TransformationType.SINUSOIDAL);
        assertThat(result.get(1).type()).isEqualTo(TransformationType.SPHERICAL);
    }

    @Test
    void parseFunctions_shouldThrowExceptionForInvalidFormat() {
        // Arrange
        String input = "linear";

        // Act & Assert
        assertThatThrownBy(() -> CliParser.parseFunctions(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be in format 'name:weight'");
    }

    @Test
    void parseFunctions_shouldThrowExceptionForInvalidWeight() {
        // Arrange
        String input = "linear:abc";

        // Act & Assert
        assertThatThrownBy(() -> CliParser.parseFunctions(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid weight");
    }
}
