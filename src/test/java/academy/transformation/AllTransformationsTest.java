package academy.transformation;

import static org.assertj.core.api.Assertions.assertThat;

import academy.model.Point;
import org.junit.jupiter.api.Test;

/** Tests to ensure all transformation implementations can be instantiated and executed without errors. */
class AllTransformationsTest {

    @Test
    void swirl_shouldTransformPoint() {
        // Arrange
        Transform swirl = new Swirl();
        Point point = new Point(1.0, 1.0, 100, 150, 200);

        // Act
        Point result = swirl.apply(point);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.x()).isFinite();
        assertThat(result.y()).isFinite();
        assertThat(result.r()).isEqualTo(100);
        assertThat(result.g()).isEqualTo(150);
        assertThat(result.b()).isEqualTo(200);
    }

    @Test
    void horseshoe_shouldTransformPoint() {
        // Arrange
        Transform horseshoe = new Horseshoe();
        Point point = new Point(1.0, 1.0, 100, 150, 200);

        // Act
        Point result = horseshoe.apply(point);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.x()).isFinite();
        assertThat(result.y()).isFinite();
        assertThat(result.r()).isEqualTo(100);
    }

    @Test
    void polar_shouldTransformPoint() {
        // Arrange
        Transform polar = new Polar();
        Point point = new Point(1.0, 1.0, 100, 150, 200);

        // Act
        Point result = polar.apply(point);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.x()).isFinite();
        assertThat(result.y()).isFinite();
    }

    @Test
    void disc_shouldTransformPoint() {
        // Arrange
        Transform disc = new Disc();
        Point point = new Point(1.0, 1.0, 100, 150, 200);

        // Act
        Point result = disc.apply(point);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.x()).isFinite();
        assertThat(result.y()).isFinite();
    }

    @Test
    void heart_shouldTransformPoint() {
        // Arrange
        Transform heart = new Heart();
        Point point = new Point(1.0, 1.0, 100, 150, 200);

        // Act
        Point result = heart.apply(point);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.x()).isFinite();
        assertThat(result.y()).isFinite();
    }

    @Test
    void spiral_shouldTransformPoint() {
        // Arrange
        Transform spiral = new Spiral();
        Point point = new Point(1.0, 1.0, 100, 150, 200);

        // Act
        Point result = spiral.apply(point);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.x()).isFinite();
        assertThat(result.y()).isFinite();
    }

    @Test
    void hyperbolic_shouldTransformPoint() {
        // Arrange
        Transform hyperbolic = new Hyperbolic();
        Point point = new Point(1.0, 1.0, 100, 150, 200);

        // Act
        Point result = hyperbolic.apply(point);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.x()).isFinite();
        assertThat(result.y()).isFinite();
    }

    @Test
    void allTransformations_shouldHandleOrigin() {
        // Arrange
        Transform[] transforms = {
            new Linear(),
            new Sinusoidal(),
            new Spherical(),
            new Swirl(),
            new Horseshoe(),
            new Polar(),
            new Disc(),
            new Heart(),
            new Spiral(),
            new Hyperbolic()
        };
        Point origin = new Point(0.0, 0.0);

        // Act & Assert
        for (Transform transform : transforms) {
            Point result = transform.apply(origin);
            assertThat(result).isNotNull();
            assertThat(result.x()).isFinite();
            assertThat(result.y()).isFinite();
        }
    }
}
