package academy.model;

public enum TransformationType {
    LINEAR,
    SINUSOIDAL,
    SPHERICAL,
    SWIRL,
    HORSESHOE,
    POLAR,
    DISC,
    HEART,
    SPIRAL,
    HYPERBOLIC;

    public static TransformationType fromString(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown transformation type: " + name);
        }
    }
}
