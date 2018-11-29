import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Agent {
	
	private String id;
	private String imagePath;
	private String edgeImagePath;
	private HashMap<Agent, Vector3> neighbors;
	
	public Agent(String id) {
		this.id = id;
		neighbors = new HashMap<Agent, Vector3>();
		System.out.println("[" + id + "]Agent has been created.");
	}
	
	public String getId() {
		return id;
	}
	
	public String getImagePath(String path) {
		return imagePath;
	}
	
	public void setImagePath(String path) {
		imagePath = path;
		System.out.println("[" + id + "]Path set: " + path);
	}
	
	public void addNeighbors(HashMap<Agent,Vector3> neighbors) {
		for(Map.Entry<Agent, Vector3> entry: neighbors.entrySet()) {
			addNeighbor(entry.getKey(), entry.getValue());
		}
	}
	
	public void addNeighbor(Agent agent, Vector3 relativePosition) {
		neighbors.put(agent, relativePosition);
		System.out.println("[" + id + "]Neighbor added: " + agent.getId());
	}
	
	public void setEdgeImage() {
		Image edgeImage = EdgeDetector.getEdgeImage(Constants.projectBasePath + imagePath);
		String pathDirectory = Paths.get(imagePath).getParent().toString();
		
		try {
			edgeImagePath = pathDirectory + "\\" + id + "_edge_image.png";
			String filePath = Constants.projectBasePath + "\\" + edgeImagePath;
			File file = new File(filePath);
			BufferedImage bufferedImage = ImageUtils.toBufferedImage(edgeImage);
			ImageIO.write(bufferedImage, "png", file);
			
			System.out.println("[" + id + "]" + "Edge image saved: " + edgeImagePath);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}