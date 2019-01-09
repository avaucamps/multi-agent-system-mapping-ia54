import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Agent {
	
	private String id;
	private String imagePath;
	private EnvironmentMessage envMessage;
	private HashMap<Agent, Vector3> neighbors;
	private ArrayList<FeaturePoint> featurePoints;
	
	public Agent(String id, EnvironmentMessage envMessage) {
		this.id = id;
		this.envMessage = envMessage;
		neighbors = new HashMap<Agent, Vector3>();
		featurePoints = new ArrayList<FeaturePoint>();
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

		sendEvent(AgentEvent.Event.askNeighbors);
	}
	
	public HashMap<Agent, Vector3> getNeighbors() {
		return neighbors;
	}
	
	public void addNeighbors(HashMap<Agent,Vector3> neighbors) {
		for(Map.Entry<Agent, Vector3> entry: neighbors.entrySet()) {
			addNeighbor(entry.getKey(), entry.getValue());
		}

		sendEvent(AgentEvent.Event.getMatchingPoints, AgentUtils.getAgentInformation(this));
	}

	public void addFeaturePoint(FeaturePoint p) {
		featurePoints.add(p);
	}

	public ArrayList<FeaturePoint> getFeaturePoints() { return featurePoints; }

	private void addNeighbor(Agent agent, Vector3 relativePosition) {
		neighbors.put(agent, relativePosition);
		System.out.println("[" + id + "]Neighbor added: " + agent.getId());
	}

	private void sendEvent(AgentEvent.Event event) {
		envMessage.onAgentEvent(
				new AgentEvent(this, event)
		);
	}

	private void sendEvent(AgentEvent.Event event, String message) {
		envMessage.onAgentEvent(
				new AgentEvent(this, event, message)
		);
	}
}