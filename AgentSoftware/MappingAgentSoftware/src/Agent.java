import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Agent {
	
	String id;
	ArrayList<String> imagePath = new ArrayList<>();
	Vector3 position;
	
	public Agent(String id) {
		this.id = id;
		position = new Vector3(0,0,0);
		System.out.println("[" + id + "]Agent has been created.");
	}
	
	public void setPosition(Vector3 position) {
		this.position = position;
		System.out.println("[" + id + "]Position set: " + position.toString());
	}
	
	public void setImagePath(String path) {
		imagePath.add(path);
		System.out.println("[" + id + "]Path set: " + path);
	}
	
	private void showEdgeImage() {
		EdgeDetector.toEdgeImage("C:\\Users\\Antoine\\Desktop\\multi-agent-system-mapping-ia54\\" + imagePath.get(0));
	}
}