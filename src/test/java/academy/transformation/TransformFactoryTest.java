package academy.transformation;

import static org.assertj.core.api.Assertions.assertThat;

import academy.model.TransformationType;
import org.junit.jupiter.api.Test;

class TransformFactoryTest {

    @Test
    void factory_shouldCreateLinearTransform() {
        // Act
        Transform transform = TransformFactory.create(TransformationType.LINEAR);

        // Assert
        assertThat(transform).isInstanceOf(Linear.class);
    }

    @Test
    void factory_shouldCreateSinusoidalTransform() {
        // Act
        Transform transform = TransformFactory.create(TransformationType.SINUSOIDAL);

        // Assert
        assertThat(transform).isInstanceOf(Sinusoidal.class);
    }

    @Test
    void factory_shouldCreateSphericalTransform() {
        // Act
        Transform transform = TransformFactory.create(TransformationType.SPHERICAL);

        // Assert
        assertThat(transform).isInstanceOf(Spherical.class);
    }

    @Test
    void factory_shouldCreateSwirlTransform() {
        // Act
        Transform transform = TransformFactory.create(TransformationType.SWIRL);

        // Assert
        assertThat(transform).isInstanceOf(Swirl.class);
    }

    @Test
    void factory_shouldCreateHorseshoeTransform() {
        // Act
        Transform transform = TransformFactory.create(TransformationType.HORSESHOE);

        // Assert
        assertThat(transform).isInstanceOf(Horseshoe.class);
    }

    @Test
    void factory_shouldCreatePolarTransform() {
        // Act
        Transform transform = TransformFactory.create(TransformationType.POLAR);

        // Assert
        assertThat(transform).isInstanceOf(Polar.class);
    }

    @Test
    void factory_shouldCreateDiscTransform() {
        // Act
        Transform transform = TransformFactory.create(TransformationType.DISC);

        // Assert
        assertThat(transform).isInstanceOf(Disc.class);
    }

    @Test
    void factory_shouldCreateHeartTransform() {
        // Act
        Transform transform = TransformFactory.create(TransformationType.HEART);

        // Assert
        assertThat(transform).isInstanceOf(Heart.class);
    }

    @Test
    void factory_shouldCreateSpiralTransform() {
        // Act
        Transform transform = TransformFactory.create(TransformationType.SPIRAL);

        // Assert
        assertThat(transform).isInstanceOf(Spiral.class);
    }

    @Test
    void factory_shouldCreateHyperbolicTransform() {
        // Act
        Transform transform = TransformFactory.create(TransformationType.HYPERBOLIC);

        // Assert
        assertThat(transform).isInstanceOf(Hyperbolic.class);
    }
}
