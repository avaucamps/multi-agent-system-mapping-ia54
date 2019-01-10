import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static javafx.scene.input.KeyCode.X;

public class Environment {

	private HashMap<Agent, Vector3> agents = new HashMap<Agent, Vector3>();
	private ArrayList<AgentEvent> agentEvents = new ArrayList<>();
	private EnvironmentUpdate envUpdate;
	private PropertyChangeSupport support;
	private int numberOfAgents = 0;
	private final int numberOfNeighbors = 3;
	private String message = "";
	private int matchingPointsNumber = 0;

	public Environment(EnvironmentUpdate envUpdate) {
		this.envUpdate = envUpdate;
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
		if (event.getEventType().equals(AgentEvent.Event.addPointToMap)) {
			Vector3 agentPosition = agents.get(event.getAgent());
			Point agentPoint = new Point(agentPosition.getX(), agentPosition.getY());
			Point pointInWorld = event.getFeaturePoint().getPoint().add(agentPoint);

			envUpdate.addPointToMap(
					pointInWorld,
					event.getFeaturePoint().getFeatureMatchingType()
			);

			return;
		}

		agentEvents.add(event);
		if (event.getEventType().equals(AgentEvent.Event.getWorldPoint)) {
			return;
		}

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

	public void onNewMatchingPoint(MatchingPoint matchingPoint) {
		for(Map.Entry<Agent, Vector3> entry : agents.entrySet()) {
			if (entry.getKey().getId().equals(matchingPoint.getAgent1Id())) {
				entry.getKey().onNewImageProcessingFeaturePoint(
						new FeaturePoint(matchingPoint.getPointAgent1(), matchingPoint.getFeatureMatchingType())
				);
			}
		}
	}

	public void onNewWorldFeaturePoint(String id, Point worldPoint, Point screenPoint, FeatureMatchingType type) {
		for(Map.Entry<Agent, Vector3> entry: agents.entrySet()) {
			if (entry.getKey().getId().equals(id)) {
				Point agentPosition = new Point(entry.getValue().getX(), entry.getValue().getY());
				agentPosition.setX(Math.abs(agentPosition.getX()));
				agentPosition.setY(Math.abs(agentPosition.getY()));
				Point pointForAgent = worldPoint.subtract(agentPosition);
				entry.getKey().onNewWorldFeaturePoint(
						new FeaturePoint(pointForAgent, type)
				);
			}
		}
	}

	public void onMatchingPointsReceived() {
		handleAgentEvent(AgentEvent.Event.getWorldPoint);
	}

	private void handleAgentEvent(AgentEvent.Event event) {
		switch (event) {
			case askNeighbors:
				setNeighbors();
				break;
			case getMatchingPoint:
				sendMessagesToImageProcessing(event);
				break;
			case getWorldPoint:
				sendMessagesToSimulation(event);
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
				"Done"
		);

		this.message = "Done";
	}

	private void sendMessagesToSimulation(AgentEvent.Event event) {
		for(AgentEvent agentEvent : agentEvents) {
			if (!event.equals(agentEvent.getEventType())) {
				continue;
			}

			support.firePropertyChange(
					PropertyChangeConstants.SIMULATION_MESSAGE_NAME,
					message,
					agentEvent.getMessage()
			);
			this.message = agentEvent.getMessage();
		}

		support.firePropertyChange(
				PropertyChangeConstants.SIMULATION_MESSAGE_NAME,
				message,
				"Done"
		);

		this.message = "Done";
	}
}