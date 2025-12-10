package academy.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RectTest {

    @Test
    void rect_shouldContainPointInside() {
        // Arrange
        Rect rect = new Rect(-1.0, -1.0, 2.0, 2.0);

        // Act & Assert
        assertThat(rect.contains(0.0, 0.0)).isTrue();
        assertThat(rect.contains(-0.5, 0.5)).isTrue();
        assertThat(rect.contains(0.9, -0.9)).isTrue();
    }

    @Test
    void rect_shouldNotContainPointOutside() {
        // Arrange
        Rect rect = new Rect(-1.0, -1.0, 2.0, 2.0);

        // Act & Assert
        assertThat(rect.contains(-1.5, 0.0)).isFalse();
        assertThat(rect.contains(1.5, 0.0)).isFalse();
        assertThat(rect.contains(0.0, -1.5)).isFalse();
        assertThat(rect.contains(0.0, 1.5)).isFalse();
    }

    @Test
    void rect_shouldContainPointOnBoundary() {
        // Arrange
        Rect rect = new Rect(-1.0, -1.0, 2.0, 2.0);

        // Act & Assert
        assertThat(rect.contains(-1.0, 0.0)).isTrue();
        assertThat(rect.contains(0.0, -1.0)).isTrue();
    }

    @Test
    void rect_shouldProvideCorrectDimensions() {
        // Arrange
        Rect rect = new Rect(1.0, 2.0, 3.0, 4.0);

        // Act & Assert
        assertThat(rect.x()).isEqualTo(1.0);
        assertThat(rect.y()).isEqualTo(2.0);
        assertThat(rect.width()).isEqualTo(3.0);
        assertThat(rect.height()).isEqualTo(4.0);
    }
}
