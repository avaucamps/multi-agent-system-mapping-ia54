import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Environment {

	private ArrayList<MatchingPoint> matchPoints2D;
	private HashMap<Agent, Vector3> agents = new HashMap<Agent, Vector3>();
	private ArrayList<AgentEvent> agentEvents = new ArrayList<>();
	private EnvironmentUpdate envUpdate;
	private PropertyChangeSupport support;
	private int numberOfAgents = 0;
	private final int numberOfNeighbors = 3;
	private String message = "";
	
	public Environment(EnvironmentUpdate envUpdate) {
		this.envUpdate = envUpdate;
		matchPoints2D = new ArrayList<MatchingPoint>();
		support = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		support.removePropertyChangeListener(pcl);
	}

	public void onNumberOfAgents(int n) {
		numberOfAgents = n;
	}

	public void agentSpawned(String id) {
		agents.put(
				new Agent(id, this::onAgentEvent),
				new Vector3(0,0,0)
		);
	}

	public void onAgentEvent(AgentEvent event) {
		agentEvents.add(event);

		int numberOfCurrentEvent = 0;
		for(AgentEvent agentEvent : agentEvents) {
			if (agentEvent.getEventType().equals(event.getEventType())) {
				numberOfCurrentEvent++;
			}
		}

		if (numberOfCurrentEvent == numberOfAgents) {
			handleAgentEvent(event.getEventType());
		}
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

	public void received2DMatchPoints(ArrayList<MatchingPoint> matchPoints) {
		setAgents2DMatchPoints(matchPoints);
	}

	public void agentWorldFeaturePoint(String id, Point worldPoint, Point screenPoint, FeatureMatchingType type) {
		for(Map.Entry<Agent, Vector3> entry: agents.entrySet()) {
			if (entry.getKey().getId().equals(id)) {
				Point agentPosition = new Point(entry.getValue().getX(), entry.getValue().getY());
				Point pointForAgent = worldPoint.subtract(agentPosition);
				entry.getKey().addFeaturePoint(
						new FeaturePoint(pointForAgent, type)
				);
			}
		}
	}

	public void allWorldFeaturePointsReceived() {
		envUpdate.mapEnvironment(getAllFeaturePoints(FeatureMatchingType.sift), FeatureMatchingType.sift);
		envUpdate.mapEnvironment(getAllFeaturePoints(FeatureMatchingType.harris), FeatureMatchingType.harris);
	}

	private void handleAgentEvent(AgentEvent.Event event) {
		switch (event) {
			case askNeighbors:
				setNeighbors();
				break;
			case getMatchingPoints:
				sendMessagesToImageProcessing(event);
				break;
			default:
				break;
		}
	}

	private void setNeighbors() {
		for(Map.Entry<Agent, Vector3> entry: agents.entrySet()) {
			entry.getKey().addNeighbors(
					AgentUtils.getNeighbors(entry.getKey(), agents, numberOfNeighbors)
			);
		}
	}

	private ArrayList<Point> getAllFeaturePoints(FeatureMatchingType type) {
		ArrayList<Point> points = new ArrayList<>();

		for(Map.Entry<Agent, Vector3> entry: agents.entrySet()) {
			for(FeaturePoint fp : entry.getKey().getFeaturePoints()) {
				if (fp.getFeatureMatchingType().isEqual(type)) {
					points.add(fp.getPoint());
				}
			}
		}

		return points;
	}

	private void sendMessagesToImageProcessing(AgentEvent.Event event) {
		for(AgentEvent agentEvent : agentEvents) {
			if (!event.equals(agentEvent.getEventType())) {
				continue;
			}

			support.firePropertyChange(
					PropertyChangeConstants.IMAGE_PROCESSING_MESSAGE_NAME,
					message,
					agentEvent.getMessage()
			);
			this.message = agentEvent.getMessage();
		}

		support.firePropertyChange(
				PropertyChangeConstants.IMAGE_PROCESSING_MESSAGE_NAME,
				message,
				"AllMessagesSent"
		);

		this.message = "AllMessagesSent";
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