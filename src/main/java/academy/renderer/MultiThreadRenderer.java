package academy.renderer;

import academy.model.AffineTransformation;
import academy.model.FractalConfig;
import academy.model.FractalImage;
import academy.model.Pixel;
import academy.model.Point;
import academy.model.Rect;
import academy.model.WeightedFunction;
import academy.transformation.Transform;
import academy.transformation.TransformFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiThreadRenderer implements Renderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiThreadRenderer.class);
    private static final int SKIP_ITERATIONS = 20;
    private static final Rect WORLD = new Rect(-1.5, -1.5, 3.0, 3.0);

    @Override
    public FractalImage render(FractalConfig config) {
        LOGGER.info("Starting multi-threaded rendering with {} threads", config.threads());
        long startTime = System.currentTimeMillis();

        FractalImage image = new FractalImage(config.width(), config.height());

        // Pre-calculate data for all threads
        double totalWeight = config.totalWeight();
        double[] cumulativeWeights = buildCumulativeWeights(config, totalWeight);
        Transform[] transforms = buildTransforms(config);

        // Split work between threads
        int samplesPerThread = config.iterationCount() / config.threads();
        int remainingSamples = config.iterationCount() % config.threads();

        ExecutorService executor = Executors.newFixedThreadPool(config.threads());
        AtomicInteger completedSamples = new AtomicInteger(0);
        AtomicInteger lastLoggedPercent = new AtomicInteger(0);

        List<RenderTask> tasks = new ArrayList<>();
        long baseSeed = config.seed();

        for (int threadId = 0; threadId < config.threads(); threadId++) {
            int samples = samplesPerThread + (threadId < remainingSamples ? 1 : 0);
            long threadSeed = baseSeed + threadId;

            RenderTask task = new RenderTask(
                    threadId,
                    samples,
                    threadSeed,
                    image,
                    config,
                    cumulativeWeights,
                    transforms,
                    totalWeight,
                    completedSamples,
                    lastLoggedPercent);

            tasks.add(task);
            executor.submit(task);
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Rendering interrupted", e);
            Thread.currentThread().interrupt();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        LOGGER.info("Rendering completed in {} ms", elapsed);

        return image;
    }

    private double[] buildCumulativeWeights(FractalConfig config, double totalWeight) {
        double[] weights = new double[config.weightedFunctions().size()];
        double cumulative = 0;
        for (int i = 0; i < config.weightedFunctions().size(); i++) {
            cumulative += config.weightedFunctions().get(i).weight();
            weights[i] = cumulative;
        }
        return weights;
    }

    private Transform[] buildTransforms(FractalConfig config) {
        Transform[] transforms = new Transform[config.weightedFunctions().size()];
        for (int i = 0; i < config.weightedFunctions().size(); i++) {
            WeightedFunction wf = config.weightedFunctions().get(i);
            transforms[i] = TransformFactory.create(wf.type());
        }
        return transforms;
    }

    private int selectTransform(double value, double[] cumulativeWeights) {
        for (int i = 0; i < cumulativeWeights.length; i++) {
            if (value <= cumulativeWeights[i]) {
                return i;
            }
        }
        return cumulativeWeights.length - 1;
    }

    private class RenderTask implements Runnable {
        private final int threadId;
        private final int samples;
        private final long seed;
        private final FractalImage image;
        private final FractalConfig config;
        private final double[] cumulativeWeights;
        private final Transform[] transforms;
        private final double totalWeight;
        private final AtomicInteger completedSamples;
        private final AtomicInteger lastLoggedPercent;

        RenderTask(
                int threadId,
                int samples,
                long seed,
                FractalImage image,
                FractalConfig config,
                double[] cumulativeWeights,
                Transform[] transforms,
                double totalWeight,
                AtomicInteger completedSamples,
                AtomicInteger lastLoggedPercent) {
            this.threadId = threadId;
            this.samples = samples;
            this.seed = seed;
            this.image = image;
            this.config = config;
            this.cumulativeWeights = cumulativeWeights;
            this.transforms = transforms;
            this.totalWeight = totalWeight;
            this.completedSamples = completedSamples;
            this.lastLoggedPercent = lastLoggedPercent;
        }

        @Override
        public void run() {
            Random random = new Random(seed);

            for (int sample = 0; sample < samples; sample++) {
                // Start with random point in [-1, 1] range
                Point point = new Point(random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1);

                // Skip first iterations to let the point "settle" into the attractor
                for (int step = -SKIP_ITERATIONS; step < config.iterationCount(); step++) {
                    // Apply random affine transformation
                    int affineIndex = random.nextInt(config.affineTransformations().size());
                    AffineTransformation affine = config.affineTransformations().get(affineIndex);
                    point = affine.apply(point);

                    // Apply random non-linear transformation based on weights
                    int transformIndex = selectTransform(random.nextDouble() * totalWeight, cumulativeWeights);
                    point = transforms[transformIndex].apply(point);

                    // Only plot points after warm-up and if they're within bounds
                    if (step >= 0 && WORLD.contains(point.x(), point.y())) {
                        // Map world coordinates to pixel coordinates
                        int x = (int) ((point.x() - WORLD.x()) / WORLD.width() * config.width());
                        int y = (int) ((point.y() - WORLD.y()) / WORLD.height() * config.height());

                        Pixel pixel = image.pixel(x, y);
                        if (pixel != null) {
                            pixel.hit(affine.red(), affine.green(), affine.blue());
                        }
                    }
                }

                // Update progress
                int total = completedSamples.incrementAndGet();
                int percent = (int) ((double) total / config.iterationCount() * 100);
                int lastPercent = lastLoggedPercent.get();

                if (percent >= lastPercent + 10 && lastLoggedPercent.compareAndSet(lastPercent, percent)) {
                    LOGGER.info("Progress: {}%", percent);
                }
            }
        }
    }
}
