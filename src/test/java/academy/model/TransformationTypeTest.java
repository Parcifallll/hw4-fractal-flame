package academy.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class TransformationTypeTest {

    @Test
    void fromString_shouldParseLowerCase() {
        // Act
        TransformationType type = TransformationType.fromString("linear");

        // Assert
        assertThat(type).isEqualTo(TransformationType.LINEAR);
    }

    @Test
    void fromString_shouldParseUpperCase() {
        // Act
        TransformationType type = TransformationType.fromString("LINEAR");

        // Assert
        assertThat(type).isEqualTo(TransformationType.LINEAR);
    }

    @Test
    void fromString_shouldParseMixedCase() {
        // Act
        TransformationType type = TransformationType.fromString("LiNeAr");

        // Assert
        assertThat(type).isEqualTo(TransformationType.LINEAR);
    }

    @Test
    void fromString_shouldParseAllTypes() {
        // Act & Assert
        assertThat(TransformationType.fromString("linear")).isEqualTo(TransformationType.LINEAR);
        assertThat(TransformationType.fromString("sinusoidal")).isEqualTo(TransformationType.SINUSOIDAL);
        assertThat(TransformationType.fromString("spherical")).isEqualTo(TransformationType.SPHERICAL);
        assertThat(TransformationType.fromString("swirl")).isEqualTo(TransformationType.SWIRL);
        assertThat(TransformationType.fromString("horseshoe")).isEqualTo(TransformationType.HORSESHOE);
        assertThat(TransformationType.fromString("polar")).isEqualTo(TransformationType.POLAR);
        assertThat(TransformationType.fromString("disc")).isEqualTo(TransformationType.DISC);
        assertThat(TransformationType.fromString("heart")).isEqualTo(TransformationType.HEART);
        assertThat(TransformationType.fromString("spiral")).isEqualTo(TransformationType.SPIRAL);
        assertThat(TransformationType.fromString("hyperbolic")).isEqualTo(TransformationType.HYPERBOLIC);
    }

    @Test
    void fromString_shouldThrowExceptionForInvalidType() {
        // Act & Assert
        assertThatThrownBy(() -> TransformationType.fromString("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown transformation type");
    }
}
