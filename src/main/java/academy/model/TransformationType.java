package academy.model;

public enum TransformationType {
    LINEAR,
    SINUSOIDAL,
    SPHERICAL,
    SWIRL,
    HORSESHOE,
    POLAR,
    DISC;

    public static TransformationType fromString(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown transformation type: " + name);
        }
    }
}
