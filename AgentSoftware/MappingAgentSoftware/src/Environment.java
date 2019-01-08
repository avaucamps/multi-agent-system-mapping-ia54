import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Environment {

	private ArrayList<String> agentsInformation;
	private ArrayList<MatchingPoint> matchPoints2D;
	private PropertyChangeSupport support;
	private HashMap<Agent, Vector3> agents = new HashMap<Agent, Vector3>();
	private ArrayList<Point> featurePointsSift;
	private ArrayList<Point> featurePointsHarris;
	private EnvironmentUpdate envUpdate;
	private final int numberOfNeighbors = 3;
	
	public Environment(EnvironmentUpdate envUpdate) {
		this.envUpdate = envUpdate;
		agentsInformation = new ArrayList<String>();
		matchPoints2D = new ArrayList<MatchingPoint>();
		featurePointsSift = new ArrayList<Point>();
		featurePointsHarris = new ArrayList<>();
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

	public void received2DMatchPoints(ArrayList<MatchingPoint> matchPoints) {
		setAgents2DMatchPoints(matchPoints);
	}

	public void agentWorldFeaturePoint(String id, Point worldPoint, Point screenPoint, FeatureMatchingType type) {
		for(Map.Entry<Agent, Vector3> entry: agents.entrySet()) {
			if (entry.getKey().getId().equals(id)) {
				Point agentPosition = new Point(entry.getValue().getX(), entry.getValue().getY());
				Point pointForAgent = worldPoint.subtract(agentPosition);
				entry.getKey().addFeaturePoint(pointForAgent);

				switch (type) {
					case sift:
						featurePointsSift.add(worldPoint);
						break;
					case harris:
						featurePointsHarris.add(worldPoint);
						break;
				}
			}
		}
	}

	public void allWorldFeaturePointsReceived() {
		envUpdate.mapEnvironment(featurePointsSift, FeatureMatchingType.sift);
		envUpdate.mapEnvironment(featurePointsHarris, FeatureMatchingType.harris);
	}

	private void setAgentsInformation(ArrayList<String> information) {
		support.firePropertyChange(
				PropertyChangeConstants.IMAGE_PROCESSING_MESSAGE_NAME,
				this.agentsInformation,
				information
		);
		this.agentsInformation = information;
	}

	private void setAgents2DMatchPoints(ArrayList<MatchingPoint> matchPoints) {
		support.firePropertyChange(
				PropertyChangeConstants.SIMULATION_MESSAGE_NAME,
				this.matchPoints2D,
				matchPoints
		);
		this.matchPoints2D = matchPoints;
	}
}