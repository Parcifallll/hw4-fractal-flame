package academy.transformation;

import academy.model.Point;

public class Disc implements Transform {
    @Override
    public Point apply(Point point) {
        double theta = Math.atan2(point.y(), point.x());
        double r = Math.sqrt(point.x() * point.x() + point.y() * point.y());
        // Transform: (x, y) -> (θ/π * sin(πr), θ/π * cos(πr))
        double factor = theta / Math.PI;
        double x = factor * Math.sin(Math.PI * r);
        double y = factor * Math.cos(Math.PI * r);
        return new Point(x, y, point.r(), point.g(), point.b());
    }
}
