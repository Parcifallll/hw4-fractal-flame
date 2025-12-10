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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressFBWarnings(
        value = "SLF4J_LOGGER_SHOULD_BE_PRIVATE",
        justification = "Logger is used by subclasses and needs to be protected for inheritance")
public abstract class AbstractRenderer implements Renderer {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractRenderer.class);
    protected static final int SKIP_ITERATIONS = 20;
    protected static final Rect WORLD = new Rect(-1.5, -1.5, 3.0, 3.0);

    protected double[] buildCumulativeWeights(FractalConfig config) {
        double[] weights = new double[config.weightedFunctions().size()];
        double cumulative = 0;
        for (int i = 0; i < config.weightedFunctions().size(); i++) {
            cumulative += config.weightedFunctions().get(i).weight();
            weights[i] = cumulative;
        }
        return weights;
    }

    protected Transform[] buildTransforms(FractalConfig config) {
        Transform[] transforms = new Transform[config.weightedFunctions().size()];
        for (int i = 0; i < config.weightedFunctions().size(); i++) {
            WeightedFunction wf = config.weightedFunctions().get(i);
            transforms[i] = TransformFactory.create(wf.type());
        }
        return transforms;
    }

    protected int selectTransform(double value, double[] cumulativeWeights) {
        for (int i = 0; i < cumulativeWeights.length; i++) {
            if (value <= cumulativeWeights[i]) {
                return i;
            }
        }
        return cumulativeWeights.length - 1;
    }

    protected void logProgress(int sample, int totalSamples, AtomicInteger lastLoggedPercent) {
        int percent = (int) ((double) sample / totalSamples * 100);
        int lastPercent = lastLoggedPercent.get();

        if (percent >= lastPercent + 10 && lastLoggedPercent.compareAndSet(lastPercent, percent)) {
            LOGGER.info("Progress: {}%", percent);
        }
    }

    protected void logSimpleProgress(int sample, int totalSamples) {
        if (sample > 0 && sample % (totalSamples / 10) == 0) {
            int percent = (int) ((double) sample / totalSamples * 100);
            LOGGER.info("Progress: {}%", percent);
        }
    }

    protected void processSample(
            Random random,
            FractalConfig config,
            double totalWeight,
            double[] cumulativeWeights,
            Transform[] transforms,
            FractalImage image) {

        // Start with random point in [-1, 1] range
        double startX = random.nextDouble() * 2 - 1;
        double startY = random.nextDouble() * 2 - 1;
        Point point = new Point(startX, startY);

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
    }
}
