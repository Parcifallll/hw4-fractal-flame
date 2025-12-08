package academy.transformation;

import academy.model.Point;

public class Heart implements Transform {
    @Override
    public Point apply(Point point) {
        double r = Math.sqrt(point.x() * point.x() + point.y() * point.y());
        double theta = Math.atan2(point.y(), point.x());
        double x = r * Math.sin(theta * r);
        double y = -r * Math.cos(theta * r);
        return new Point(x, y, point.r(), point.g(), point.b());
    }
}
