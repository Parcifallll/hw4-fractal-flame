package academy.model;

public record AffineTransformation(
    double a,
    double b,
    double c,
    double d,
    double e,
    double f,
    int red,
    int green,
    int blue) {

    public AffineTransformation(double a, double b, double c, double d, double e, double f) {
        this(a, b, c, d, e, f, 0, 0, 0);
    }

    public Point apply(Point point) {
        double newX = a * point.x() + b * point.y() + c;
        double newY = d * point.x() + e * point.y() + f;

        int newR = (point.r() + red) / 2;
        int newG = (point.g() + green) / 2;
        int newB = (point.b() + blue) / 2;

        return new Point(newX, newY, newR, newG, newB);
    }
}
