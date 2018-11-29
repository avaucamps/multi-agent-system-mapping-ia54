import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageUtils {
	public static BufferedImage toBufferedImage(Image img) {
	    if (img instanceof BufferedImage) {
	        return (BufferedImage) img;
	    }

	    BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    
	    Graphics2D graphics = bufferedImage.createGraphics();
	    graphics.drawImage(img, 0, 0, null);
	    graphics.dispose();

	    return bufferedImage;
	}
}
