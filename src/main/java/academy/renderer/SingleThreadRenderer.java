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
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleThreadRenderer implements Renderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleThreadRenderer.class);
    private static final int SKIP_ITERATIONS = 20;
    private static final Rect WORLD = new Rect(-1.5, -1.5, 3.0, 3.0);

    @Override
    public FractalImage render(FractalConfig config) {
        LOGGER.info("Starting single-threaded rendering");
        long startTime = System.currentTimeMillis();

        FractalImage image = new FractalImage(config.width(), config.height());
        Random random = new Random(config.seed());

        double totalWeight = config.totalWeight();
        // Pre-calculate cumulative weights for weighted random selection
        double[] cumulativeWeights = buildCumulativeWeights(config, totalWeight);
        Transform[] transforms = buildTransforms(config);

        for (int sample = 0; sample < config.iterationCount(); sample++) {
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

            if (sample > 0 && sample % (config.iterationCount() / 10) == 0) {
                int percent = (int) ((double) sample / config.iterationCount() * 100);
                LOGGER.info("Progress: {}%", percent);
            }
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
        // Binary search would be faster, but linear is fine for small arrays
        for (int i = 0; i < cumulativeWeights.length; i++) {
            if (value <= cumulativeWeights[i]) {
                return i;
            }
        }
        return cumulativeWeights.length - 1;
    }
}
