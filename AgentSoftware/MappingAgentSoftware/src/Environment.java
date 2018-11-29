import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Environment {
	
	private HashMap<Agent, Vector3> agents = new HashMap<Agent, Vector3>();
	private final int numberOfNeighbors = 3;
	
	public Environment() {
		setupEnvironment();
	}
	
	private void setupEnvironment() {
		CommunicationHandler.createConnection(new AgentAction() {

			@Override
			public void agentSpawned(String id) {
				agents.put(
					new Agent(id),
					new Vector3(0,0,0)
				);
			}

			@Override
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

			@Override
			public void agentTookScreenshot(String id, String filepath) {
				Optional<Agent> agent = AgentUtils.getAgent(id, agents);
				agent.ifPresent(a -> {
					a.setImagePath(filepath);
				});
			}

			@Override
			public void communicationEnded() {
				for(Map.Entry<Agent, Vector3> entry: agents.entrySet()) {
					entry.getKey().setEdgeImage();
					entry.getKey().addNeighbors(
						AgentUtils.getNeighbors(entry.getKey(), agents, numberOfNeighbors)
					);
				}
			}
		});
	}
}