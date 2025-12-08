package academy.transformation;

import academy.model.Point;

public class Polar implements Transform {
    @Override
    public Point apply(Point point) {
        double theta = Math.atan2(point.y(), point.x());
        double r = Math.sqrt(point.x() * point.x() + point.y() * point.y());
        // Convert to polar coordinates, normalize
        double x = theta / Math.PI;
        double y = r - 1;
        return new Point(x, y, point.r(), point.g(), point.b());
    }
}
