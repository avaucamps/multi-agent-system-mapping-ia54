import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Environment {

	private ArrayList<String> agentsInformation;
	private PropertyChangeSupport support;
	private HashMap<Agent, Vector3> agents = new HashMap<Agent, Vector3>();
	private final int numberOfNeighbors = 3;
	
	public Environment() {
		agentsInformation = new ArrayList<String>();
		support = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		support.removePropertyChangeListener(pcl);
	}

	public void agentSpawned(String id) {
		agents.put(
				new Agent(id),
				new Vector3(0,0,0)
		);
	}

	public void agentMoved(String id, Vector3 position) {
		Optional<Agent> agent = AgentUtils.getAgent(id, agents);
		agent.ifPresent(a -> {
			agents.get(a).setVector3(
					position.getX(),
					position.getY(),
					position.getZ()
			);
			System.out.println("[" + id + "]Position: " + position.toString());
		});
	}

	public void agentTookScreenshot(String id, String filepath) {
		Optional<Agent> agent = AgentUtils.getAgent(id, agents);
		agent.ifPresent(a -> {
			a.setImagePath(filepath);
		});
	}

	public void allScreenshotsDone() {
		for(Map.Entry<Agent, Vector3> entry: agents.entrySet()) {
			entry.getKey().addNeighbors(
					AgentUtils.getNeighbors(entry.getKey(), agents, numberOfNeighbors)
			);
		}

		ArrayList<String> information = new ArrayList<>();
		for(Map.Entry<Agent, Vector3> entry: agents.entrySet()) {
			information.add(
					AgentUtils.getAgentInformation(entry.getKey())
			);
		}

		setAgentsInformation(information);
	}

	public void addMatchingPoint(MatchingPoint matchingPoint) {
		System.out.println(matchingPoint);
	}

	private void setAgentsInformation(ArrayList<String> information) {
		support.firePropertyChange(
				PropertyChangeConstants.IMAGE_PROCESSING_MESSAGE_NAME,
				this.agentsInformation,
				information
		);
		this.agentsInformation = information;
	}
}