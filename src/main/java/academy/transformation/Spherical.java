package academy.transformation;

import academy.model.Point;

public class Spherical implements Transform {
    @Override
    public Point apply(Point point) {
        double r2 = point.x() * point.x() + point.y() * point.y();
        if (r2 < 1e-10) {
            return point; // Avoid division by zero
        }
        // Transform: (x, y) -> (x/r², y/r²)
        double x = point.x() / r2;
        double y = point.y() / r2;
        return new Point(x, y, point.r(), point.g(), point.b());
    }
}
