package academy.model;

public record Point(double x, double y, int r, int g, int b) {
    public Point(double x, double y) {
        this(x, y, 0, 0, 0);
    }

    public Point withColor(int r, int g, int b) {
        return new Point(x, y, r, g, b);
    }
}
