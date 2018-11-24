import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class EdgeDetector {
	
	private static final int lowThresh = 0;
	private static final int RATIO = 3;
    private static final int KERNEL_SIZE = 3;
	
	public static void toEdgeImage(String imagePath) {
		System.out.println(imagePath);
		Mat src = Imgcodecs.imread(imagePath);
		
		if (src.empty()) {
			System.out.println("Image not found.");
			return;
		}
		
		Mat srcBlur = new Mat();
	    Mat detectedEdges = new Mat();
	    Mat dst = new Mat();
	    Size BLUR_SIZE = new Size(3,3);
	    
		Imgproc.blur(src, srcBlur, BLUR_SIZE);
        Imgproc.Canny(srcBlur, detectedEdges, lowThresh, lowThresh * RATIO, KERNEL_SIZE, false);
        dst = new Mat(src.size(), CvType.CV_8UC3, Scalar.all(0));
        src.copyTo(dst, detectedEdges);
        Image img = HighGui.toBufferedImage(dst);
        
        showEdgeImageInFrame(img);
	}
	
	private static void showEdgeImageInFrame(Image image) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		
		Container pane = frame.getContentPane();
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
        
        JLabel imgLabel = new JLabel(new ImageIcon(image));
        pane.add(imgLabel, BorderLayout.CENTER);
        frame.setVisible(true);
	}
}