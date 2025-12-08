package academy.transformation;

import academy.model.Point;

public class Horseshoe implements Transform {
    @Override
    public Point apply(Point point) {
        double r = Math.sqrt(point.x() * point.x() + point.y() * point.y());
        if (r < 1e-10) {
            return point; // Avoid division by zero
        }
        // Transform: (x, y) -> ((x-y)(x+y)/r, 2xy/r)
        double x = (point.x() - point.y()) * (point.x() + point.y()) / r;
        double y = 2 * point.x() * point.y() / r;
        return new Point(x, y, point.r(), point.g(), point.b());
    }
}
