package academy.model;

public class Pixel {
    private int hitCount;
    private double r;
    private double g;
    private double b;

    public Pixel() {
        this.hitCount = 0;
        this.r = 0;
        this.g = 0;
        this.b = 0;
    }

    public synchronized void hit(int red, int green, int blue) {
        hitCount++;
        r += red;
        g += green;
        b += blue;
    }

    public int getHitCount() {
        return hitCount;
    }

    public double getR() {
        return r;
    }

    public double getG() {
        return g;
    }

    public double getB() {
        return b;
    }

    public boolean isHit() {
        return hitCount > 0;
    }
}
