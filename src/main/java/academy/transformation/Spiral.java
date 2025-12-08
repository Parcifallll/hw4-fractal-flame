package academy.transformation;

import academy.model.Point;

public class Spiral implements Transform {
    @Override
    public Point apply(Point point) {
        double r = Math.sqrt(point.x() * point.x() + point.y() * point.y());
        double theta = Math.atan2(point.y(), point.x());
        if (r < 1e-10) {
            return point; // Avoid division by zero
        }
        // Spiral outward based on angle and radius
        double x = (Math.cos(theta) + Math.sin(r)) / r;
        double y = (Math.sin(theta) - Math.cos(r)) / r;
        return new Point(x, y, point.r(), point.g(), point.b());
    }
}
