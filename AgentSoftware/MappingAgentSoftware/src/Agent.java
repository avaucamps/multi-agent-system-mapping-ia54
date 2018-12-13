import java.util.HashMap;
import java.util.Map;

public class Agent {
	
	private String id;
	private String imagePath;
	private HashMap<Agent, Vector3> neighbors;
	
	public Agent(String id) {
		this.id = id;
		neighbors = new HashMap<Agent, Vector3>();
		System.out.println("[" + id + "]Agent has been created.");
	}
	
	public String getId() {
		return id;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String path) {
		imagePath = path;
		System.out.println("[" + id + "]Path set: " + path);
	}
	
	public HashMap<Agent, Vector3> getNeighbors() {
		return neighbors;
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
}