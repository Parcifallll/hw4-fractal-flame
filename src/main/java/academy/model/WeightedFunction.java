package academy.model;

public record WeightedFunction(TransformationType type, double weight) {
    public WeightedFunction {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive, got: " + weight);
        }
    }
}
