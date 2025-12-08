package academy.transformation;

import academy.model.TransformationType;

public class TransformFactory {
    public static Transform create(TransformationType type) {
        return switch (type) {
            case LINEAR -> new Linear();
            case SINUSOIDAL -> new Sinusoidal();
            case SPHERICAL -> new Spherical();
            case SWIRL -> new Swirl();
            case HORSESHOE -> new Horseshoe();
            case POLAR -> new Polar();
            case DISC -> new Disc();
            case HEART -> new Heart();
            case SPIRAL -> new Spiral();
            case HYPERBOLIC -> new Hyperbolic();
        };
    }
}
