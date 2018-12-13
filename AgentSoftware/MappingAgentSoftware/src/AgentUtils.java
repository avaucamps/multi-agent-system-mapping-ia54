import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AgentUtils {
	// Public methods
	
	public static HashMap<Agent, Vector3> getNeighbors(Agent agent, HashMap<Agent, Vector3> agents, double numberOfNeighbors) {
		HashMap<Agent, Vector3> neighbors = new HashMap<Agent, Vector3>();
		Vector3 position = getPosition(
					agent.getId(),
					agents
				).orElseThrow(() -> new RuntimeException("No agent found with specified position"));
		
		for(Map.Entry<Agent, Vector3> entry: agents.entrySet()) {
			if (entry.getKey() == agent) {
				continue;
			}
			
			if (neighbors.size() < numberOfNeighbors) {
				neighbors.put(
					entry.getKey(), 
					entry.getValue().getRelativePosition(position)
				);
				continue;
			}
			
			neighbors = getUpdatedNeighbors(
				position,
				neighbors,
				entry.getKey(),
				entry.getValue()
			);
		}
		
		return neighbors;
	}
	
	public static Optional<Agent> getAgent(String id, HashMap<Agent, Vector3> agents) {
		for (Map.Entry<Agent, Vector3> entry: agents.entrySet()) {
			if (entry.getKey().getId().equals(id)) {
				return Optional.of(entry.getKey());
			}
		}
		
		return Optional.empty();
	}
	
	public static Optional<Vector3> getPosition(String agentId, HashMap<Agent, Vector3> agents) {
		for (Map.Entry<Agent, Vector3> entry: agents.entrySet()) {
			if (entry.getKey().getId().equals(agentId)) {
				return Optional.of(entry.getValue());
			}
		}
		
		return Optional.empty();
	}
	
	public static String getAgentInformation(Agent agent) {
		String information = "";
		
		information = "#";
		information += agent.getId();
		information += "#";
		information += agent.getImagePath();
		information += "#";
		
		HashMap<Agent, Vector3> neighbors = agent.getNeighbors();
		for(Map.Entry<Agent, Vector3> entry: neighbors.entrySet()) {
			information += entry.getKey().getId();
			information += "#";
		}
		
		return information;
	}
	
	// Private methods
	
	private static HashMap<Agent, Vector3> getUpdatedNeighbors(Vector3 referantPosition, HashMap<Agent, Vector3> neighbors, Agent neighbor, Vector3 neighborPosition) {
		double distance = neighborPosition.getDistance(referantPosition);
		
		Optional<Agent> farthestNeighbor = getFarthestNeighbor(neighbors, referantPosition);
		farthestNeighbor.ifPresent(n -> {
			if (neighbors.get(n).getDistance(referantPosition) > distance) {
				neighbors.remove(n);
				neighbors.put(
					neighbor, 
					neighborPosition.getRelativePosition(referantPosition)
				);
			}
		});
		
		return neighbors;
	}
	
	private static Optional<Agent> getFarthestNeighbor(HashMap<Agent, Vector3> neighbors, Vector3 referantPosition) {
		Optional<Agent> farthestAgent = Optional.empty();
		double biggestDistance = 0;
		
		for (Map.Entry<Agent, Vector3> entry: neighbors.entrySet()) {
			double distance = entry.getValue().getDistance(referantPosition); 
			if (biggestDistance < distance) {
				biggestDistance = distance;
				farthestAgent = Optional.of(entry.getKey());
			}
		}
		
		return farthestAgent;
	}
}
