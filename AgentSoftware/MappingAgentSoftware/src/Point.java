public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point subtract(Point other) {
        return new Point(
                x - other.getX(),
                y - other.getY()
        );
    }

    public Point add(Point other) {
        return new Point(
                x + other.getX(),
                y + other.getY()
        );
    }
}
