package academy.transformation;

import academy.model.Point;

public class Linear implements Transform {
    @Override
    public Point apply(Point point) {
        return point;
    }
}
