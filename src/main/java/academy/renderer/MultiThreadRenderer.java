package academy.renderer;

import academy.model.FractalConfig;
import academy.model.FractalImage;
import academy.transformation.Transform;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadRenderer extends AbstractRenderer {

    @Override
    public FractalImage render(FractalConfig config) {
        // Если 1 поток - используем однопоточный рендерер для честного сравнения
        if (config.threads() == 1) {
            LOGGER.info("Using single-threaded approach for 1 thread");
            SingleThreadRenderer single = new SingleThreadRenderer();
            return single.render(config);
        }

        LOGGER.info("Starting multi-threaded rendering with {} threads", config.threads());
        long startTime = System.currentTimeMillis();

        FractalImage image = new FractalImage(config.width(), config.height());

        // Pre-calculate data for all threads
        double totalWeight = config.totalWeight();
        double[] cumulativeWeights = buildCumulativeWeights(config);
        Transform[] transforms = buildTransforms(config);

        // Split work between threads
        int samplesPerThread = config.iterationCount() / config.threads();
        int remainingSamples = config.iterationCount() % config.threads();

        ExecutorService executor = Executors.newFixedThreadPool(config.threads());
        AtomicInteger completedSamples = new AtomicInteger(0);
        AtomicInteger lastLoggedPercent = new AtomicInteger(0);

        int startSample = 0;
        for (int threadId = 0; threadId < config.threads(); threadId++) {
            int samples = samplesPerThread + (threadId < remainingSamples ? 1 : 0);
            int endSample = startSample + samples;

            RenderTask task = new RenderTask(
                    startSample,
                    endSample,
                    image,
                    config,
                    cumulativeWeights,
                    transforms,
                    totalWeight,
                    completedSamples,
                    lastLoggedPercent);

            executor.submit(task);
            startSample = endSample;
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

    private class RenderTask implements Runnable {
        private final int startSample;
        private final int endSample;
        private final FractalImage image;
        private final FractalConfig config;
        private final double[] cumulativeWeights;
        private final Transform[] transforms;
        private final double totalWeight;
        private final AtomicInteger completedSamples;
        private final AtomicInteger lastLoggedPercent;

        RenderTask(
                int startSample,
                int endSample,
                FractalImage image,
                FractalConfig config,
                double[] cumulativeWeights,
                Transform[] transforms,
                double totalWeight,
                AtomicInteger completedSamples,
                AtomicInteger lastLoggedPercent) {
            this.startSample = startSample;
            this.endSample = endSample;
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
            for (int sampleIndex = startSample; sampleIndex < endSample; sampleIndex++) {
                // Use deterministic Random for each sample based on seed + sample index
                Random random = new Random(config.seed() + sampleIndex);
                processSample(random, config, totalWeight, cumulativeWeights, transforms, image);

                // Update progress
                int total = completedSamples.incrementAndGet();
                logProgress(total, config.iterationCount(), lastLoggedPercent);
            }
        }
    }
}
