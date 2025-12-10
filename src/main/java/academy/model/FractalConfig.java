package academy.model;

import java.util.List;

public record FractalConfig(
        int width,
        int height,
        long seed,
        int iterationCount,
        String outputPath,
        int threads,
        List<AffineTransformation> affineTransformations,
        List<WeightedFunction> weightedFunctions,
        boolean gammaCorrection,
        double gamma) {

    public FractalConfig {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be positive, got: " + width);
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive, got: " + height);
        }
        if (iterationCount <= 0) {
            throw new IllegalArgumentException("Iteration count must be positive, got: " + iterationCount);
        }
        if (threads <= 0) {
            throw new IllegalArgumentException("Thread count must be positive, got: " + threads);
        }
        if (outputPath == null || outputPath.isBlank()) {
            throw new IllegalArgumentException("Output path cannot be null or empty");
        }
        if (affineTransformations == null || affineTransformations.isEmpty()) {
            throw new IllegalArgumentException("At least one affine transformation is required");
        }
        if (weightedFunctions == null || weightedFunctions.isEmpty()) {
            throw new IllegalArgumentException("At least one transformation function is required");
        }
        if (gamma <= 0) {
            throw new IllegalArgumentException("Gamma must be positive, got: " + gamma);
        }
    }

    public double totalWeight() {
        return weightedFunctions.stream().mapToDouble(WeightedFunction::weight).sum();
    }
}
