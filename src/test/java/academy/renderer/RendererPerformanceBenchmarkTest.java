package academy.renderer;

import static org.assertj.core.api.Assertions.assertThat;

import academy.model.AffineTransformation;
import academy.model.FractalConfig;
import academy.model.FractalImage;
import academy.model.TransformationType;
import academy.model.WeightedFunction;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

/**
 * Performance benchmark test for comparing single-threaded and multi-threaded renderer implementations. Tests
 * performance across different thread counts (1, 2, 4, 8) to verify scalability.
 */
class RendererPerformanceBenchmarkTest {

    private static final Logger LOGGER = LogManager.getLogger(RendererPerformanceBenchmarkTest.class);

    private static final int BENCHMARK_WIDTH = 1920;
    private static final int BENCHMARK_HEIGHT = 1080;
    private static final int BENCHMARK_ITERATIONS = 3_000;
    private static final int BENCHMARK_RUNS = 2;

    @Test
    void benchmark_singleThreadRendererPerformance() {
        LOGGER.info("=== Single-Thread Renderer Benchmark ===");

        Renderer renderer = new SingleThreadRenderer();
        FractalConfig config = createBenchmarkConfig(1);

        // Benchmark
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < BENCHMARK_RUNS; i++) {
            long startTime = System.nanoTime();
            FractalImage image = renderer.render(config);
            long endTime = System.nanoTime();

            assertThat(image).isNotNull();
            long durationMs = (endTime - startTime) / 1_000_000;
            times.add(durationMs);
            LOGGER.info("Run {}: {} ms", i + 1, durationMs);
        }

        double averageMs = times.stream().mapToLong(Long::longValue).average().orElse(0.0);
        LOGGER.info("Average time (single-thread): {} ms", String.format("%.2f", averageMs));
    }

    @Test
    void benchmark_multiThreadRendererWithDifferentThreadCounts() {
        LOGGER.info("=== Multi-Thread Renderer Benchmark ===");

        int[] threadCounts = {1, 2, 4, 8};
        List<BenchmarkResult> results = new ArrayList<>();

        for (int threads : threadCounts) {
            LOGGER.info("--- Testing with {} thread(s) ---", threads);

            Renderer renderer = new MultiThreadRenderer();
            FractalConfig config = createBenchmarkConfig(threads);

            // Benchmark
            List<Long> times = new ArrayList<>();
            for (int i = 0; i < BENCHMARK_RUNS; i++) {
                long startTime = System.nanoTime();
                FractalImage image = renderer.render(config);
                long endTime = System.nanoTime();

                assertThat(image).isNotNull();
                long durationMs = (endTime - startTime) / 1_000_000;
                times.add(durationMs);
                LOGGER.info("Run {}: {} ms", i + 1, durationMs);
            }

            double averageMs =
                    times.stream().mapToLong(Long::longValue).average().orElse(0.0);
            results.add(new BenchmarkResult(threads, averageMs));
            LOGGER.info("Average time ({} thread(s)): {} ms", threads, String.format("%.2f", averageMs));
        }

        // Print summary
        LOGGER.info("\n=== Performance Summary ===");
        LOGGER.info("Threads | Avg Time (ms) | Speedup vs 1 thread");
        LOGGER.info("--------|---------------|---------------------");

        double baselineTime = results.get(0).averageTimeMs();
        for (BenchmarkResult result : results) {
            double speedup = baselineTime / result.averageTimeMs();
            LOGGER.info(String.format(
                    "%-12d %-15.2f %-10.2f %.2fx", result.threads(), result.averageTimeMs(), speedup, speedup));
        }

        // Verify that multi-threading provides performance improvement
        if (results.size() >= 2) {
            double singleThreadTime = results.get(0).averageTimeMs();
            double multiThreadTime = results.get(results.size() - 1).averageTimeMs();
            LOGGER.info("\nMulti-threading speedup: {}", String.format("%.2fx", singleThreadTime / multiThreadTime));

            // Assert that 8 threads should be faster than 1 thread
            assertThat(multiThreadTime)
                    .as("Multi-threaded version should be faster than single-threaded")
                    .isLessThan(singleThreadTime);
        }
    }

    @Test
    void benchmark_compareRendererImplementations() {
        LOGGER.info("=== Comparing Single-Thread vs Multi-Thread Renderer (4 threads) ===");

        int threads = 4;

        // Benchmark SingleThreadRenderer
        Renderer singleThreadRenderer = new SingleThreadRenderer();
        FractalConfig singleConfig = createBenchmarkConfig(1);

        // Benchmark
        List<Long> singleThreadTimes = new ArrayList<>();
        for (int i = 0; i < BENCHMARK_RUNS; i++) {
            long startTime = System.nanoTime();
            singleThreadRenderer.render(singleConfig);
            long endTime = System.nanoTime();
            singleThreadTimes.add((endTime - startTime) / 1_000_000);
        }
        double avgSingleThread =
                singleThreadTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

        // Benchmark MultiThreadRenderer
        Renderer multiThreadRenderer = new MultiThreadRenderer();
        FractalConfig multiConfig = createBenchmarkConfig(threads);

        // Benchmark
        List<Long> multiThreadTimes = new ArrayList<>();
        for (int i = 0; i < BENCHMARK_RUNS; i++) {
            long startTime = System.nanoTime();
            multiThreadRenderer.render(multiConfig);
            long endTime = System.nanoTime();
            multiThreadTimes.add((endTime - startTime) / 1_000_000);
        }
        double avgMultiThread =
                multiThreadTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

        // Report
        LOGGER.info("SingleThreadRenderer average: {} ms", String.format("%.2f", avgSingleThread));
        LOGGER.info("MultiThreadRenderer ({} threads) average: {} ms", threads, String.format("%.2f", avgMultiThread));
        LOGGER.info("Speedup: {}", String.format("%.2fx", avgSingleThread / avgMultiThread));
        LOGGER.info(
                "Performance improvement: {}%",
                String.format("%.2f", ((avgSingleThread - avgMultiThread) / avgSingleThread) * 100));

        // Assert multi-threaded is faster
        assertThat(avgMultiThread)
                .as("Multi-threaded renderer should be faster than single-threaded")
                .isLessThan(avgSingleThread);
    }

    private FractalConfig createBenchmarkConfig(int threads) {
        List<AffineTransformation> affineTransformations = List.of(
                new AffineTransformation(0.5, 0.0, 0.0, 0.0, 0.5, 0.0, 255, 0, 0),
                new AffineTransformation(-0.5, 0.0, 0.5, 0.0, 0.5, 0.5, 0, 255, 0),
                new AffineTransformation(0.0, 0.5, 0.0, -0.5, 0.0, 0.5, 0, 0, 255));

        List<WeightedFunction> functions = List.of(
                new WeightedFunction(TransformationType.SINUSOIDAL, 1.0),
                new WeightedFunction(TransformationType.SPHERICAL, 0.8),
                new WeightedFunction(TransformationType.SWIRL, 0.6));

        return new FractalConfig(
                BENCHMARK_WIDTH,
                BENCHMARK_HEIGHT,
                123489L,
                BENCHMARK_ITERATIONS,
                "benchmark.png",
                threads,
                affineTransformations,
                functions,
                true,
                2.2);
    }

    private record BenchmarkResult(int threads, double averageTimeMs) {}
}
