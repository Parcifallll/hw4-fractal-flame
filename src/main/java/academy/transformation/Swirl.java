package academy.transformation;

import academy.model.Point;

public class Swirl implements Transform {
    @Override
    public Point apply(Point point) {
        double r2 = point.x() * point.x() + point.y() * point.y();
        double sinR2 = Math.sin(r2);
        double cosR2 = Math.cos(r2);
        double x = point.x() * sinR2 - point.y() * cosR2;
        double y = point.x() * cosR2 + point.y() * sinR2;
        return new Point(x, y, point.r(), point.g(), point.b());
    }
}
