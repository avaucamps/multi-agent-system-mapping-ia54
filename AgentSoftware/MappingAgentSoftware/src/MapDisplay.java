import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MapDisplay {

    private ArrayList<Point> points;
    private JFrame frame;
    private CustomPanel panel;

    public MapDisplay(FeatureMatchingType type) {
        points = new ArrayList<>();
        showFrame();
        frame.setTitle("Detection with " + type.toString() + " features.");
    }

    public void AddPoint(Point p) {
        points.add(p);
        setPoints();
    }

    private void showFrame() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000,1000);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(
                dim.width/2-frame.getSize().width/2,
                dim.height/2-frame.getSize().height/2
        );
        frame.setTitle("Environment map");

        panel = new CustomPanel();
        frame.add(panel);

        frame.setVisible(true);
    }

    private void setPoints() {
        panel.setPoints(points);
        frame.repaint();
    }
}
