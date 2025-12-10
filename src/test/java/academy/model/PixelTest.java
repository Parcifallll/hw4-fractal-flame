package academy.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PixelTest {

    @Test
    void pixel_shouldStartWithZeroHits() {
        // Arrange & Act
        Pixel pixel = new Pixel();

        // Assert
        assertThat(pixel.getHitCount()).isEqualTo(0);
        assertThat(pixel.isHit()).isFalse();
    }

    @Test
    void pixel_shouldAccumulateHits() {
        // Arrange
        Pixel pixel = new Pixel();

        // Act
        pixel.hit(255, 128, 64);
        pixel.hit(100, 50, 25);

        // Assert
        assertThat(pixel.getHitCount()).isEqualTo(2);
        assertThat(pixel.isHit()).isTrue();
    }

    @Test
    void pixel_shouldAccumulateColors() {
        // Arrange
        Pixel pixel = new Pixel();

        // Act
        pixel.hit(255, 128, 64);
        pixel.hit(100, 50, 25);

        // Assert
        assertThat(pixel.getR()).isEqualTo(355.0);
        assertThat(pixel.getG()).isEqualTo(178.0);
        assertThat(pixel.getB()).isEqualTo(89.0);
    }

    @Test
    void pixel_shouldBeThreadSafe() throws InterruptedException {
        // Arrange
        Pixel pixel = new Pixel();
        int threadCount = 10;
        int hitsPerThread = 100;
        Thread[] threads = new Thread[threadCount];

        // Act
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < hitsPerThread; j++) {
                    pixel.hit(1, 1, 1);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        assertThat(pixel.getHitCount()).isEqualTo(threadCount * hitsPerThread);
    }
}
