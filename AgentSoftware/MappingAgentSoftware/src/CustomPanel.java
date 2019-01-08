import java.awt.*;
import java.util.ArrayList;

public class CustomPanel extends Panel {

    ArrayList<Point> points;

    public void setPoints(ArrayList<Point> points) {
        this.points = updatePointsWithAxis(points);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.black);
        for (Point p: points) {
            g.fillOval((int) p.getX(), (int) p.getY(),5,5);
        }
    }

    private ArrayList<Point> updatePointsWithAxis(ArrayList<Point> points) {
        ArrayList<Point> updatedPoints = new ArrayList<>();

        int widthCenter = getWidth() / 2;
        int heightCenter = getHeight() / 2;

        for (Point p: points) {
            double x = widthCenter + (p.getX() * 50);
            double y = heightCenter - (p.getY() * 50);
            updatedPoints.add(
                    new Point(x, y)
            );
        }

        return updatedPoints;
    }
}
