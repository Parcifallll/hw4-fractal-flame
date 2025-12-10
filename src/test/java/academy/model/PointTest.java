package academy.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PointTest {

    @Test
    void point_shouldCreateWithCoordinatesOnly() {
        // Act
        Point point = new Point(1.5, 2.5);

        // Assert
        assertThat(point.x()).isEqualTo(1.5);
        assertThat(point.y()).isEqualTo(2.5);
        assertThat(point.r()).isEqualTo(0);
        assertThat(point.g()).isEqualTo(0);
        assertThat(point.b()).isEqualTo(0);
    }

    @Test
    void point_shouldCreateWithCoordinatesAndColors() {
        // Act
        Point point = new Point(1.5, 2.5, 255, 128, 64);

        // Assert
        assertThat(point.x()).isEqualTo(1.5);
        assertThat(point.y()).isEqualTo(2.5);
        assertThat(point.r()).isEqualTo(255);
        assertThat(point.g()).isEqualTo(128);
        assertThat(point.b()).isEqualTo(64);
    }

    @Test
    void point_shouldBeImmutable() {
        // Arrange
        Point original = new Point(1.0, 2.0, 100, 150, 200);

        // Act
        Point modified = new Point(3.0, 4.0, 50, 75, 100);

        // Assert
        assertThat(original.x()).isEqualTo(1.0);
        assertThat(original.y()).isEqualTo(2.0);
        assertThat(modified.x()).isEqualTo(3.0);
        assertThat(modified.y()).isEqualTo(4.0);
    }
}
