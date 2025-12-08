package academy.model;

public record Rect(double x, double y, double width, double height) {
    public boolean contains(double px, double py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }
}
