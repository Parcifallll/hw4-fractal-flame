package academy.transformation;

import academy.model.Point;

public class Hyperbolic implements Transform {
    @Override
    public Point apply(Point point) {
        double r = Math.sqrt(point.x() * point.x() + point.y() * point.y());
        double theta = Math.atan2(point.y(), point.x());
        if (r < 1e-10) {
            return point; // Avoid division by zero
        }
        // Hyperbolic transformation
        double x = Math.sin(theta) / r;
        double y = r * Math.cos(theta);
        return new Point(x, y, point.r(), point.g(), point.b());
    }
}
