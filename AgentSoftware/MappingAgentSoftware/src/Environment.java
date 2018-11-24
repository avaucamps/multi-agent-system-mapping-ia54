import java.util.ArrayList;
import java.util.Optional;

public class Environment {
	
	private ArrayList<Agent> agents = new ArrayList<Agent>();
	
	public Environment() {
		setupEnvironment();
	}
	
	private void setupEnvironment() {
		CommunicationHandler.createConnection(new AgentAction() {

			@Override
			public void agentSpawned(String id) {
				agents.add(new Agent(id));
			}

			@Override
			public void agentMoved(String id, Vector3 position) {
				Optional<Agent> agent = getAgent(id);
				if (agent.isPresent()) {
					agent.get().setPosition(position);
				}
			}

			@Override
			public void agentTookScreenshot(String id, String filepath) {
				Optional<Agent> agent = getAgent(id);
				if (agent.isPresent()) {
					agent.get().setImagePath(filepath);
				}
			}
		});
	}
	
	private Optional<Agent> getAgent(String id) {
		return agents.stream().filter(
				agent -> id.equals(agent.id)	
		).findFirst();
	}
}