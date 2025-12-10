package academy.renderer;

import academy.model.FractalConfig;
import academy.model.FractalImage;
import academy.transformation.Transform;
import java.util.Random;

public class SingleThreadRenderer extends AbstractRenderer {

    @Override
    public FractalImage render(FractalConfig config) {
        LOGGER.info("Starting single-threaded rendering");
        long startTime = System.currentTimeMillis();

        FractalImage image = new FractalImage(config.width(), config.height());
        Random random = new Random(config.seed());

        double totalWeight = config.totalWeight();
        // Pre-calculate cumulative weights for weighted random selection
        double[] cumulativeWeights = buildCumulativeWeights(config);
        Transform[] transforms = buildTransforms(config);

        for (int sample = 0; sample < config.iterationCount(); sample++) {
            processSample(random, config, totalWeight, cumulativeWeights, transforms, image);
            logSimpleProgress(sample, config.iterationCount());
        }

        long elapsed = System.currentTimeMillis() - startTime;
        LOGGER.info("Rendering completed in {} ms", elapsed);

        return image;
    }
}
