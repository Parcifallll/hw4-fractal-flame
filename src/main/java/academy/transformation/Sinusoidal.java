package academy.transformation;

import academy.model.Point;

public class Sinusoidal implements Transform {
    @Override
    public Point apply(Point point) {
        double x = Math.sin(point.x());
        double y = Math.sin(point.y());
        return new Point(x, y, point.r(), point.g(), point.b());
    }
}
