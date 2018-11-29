
public interface AgentAction {
	public void agentSpawned(String id);
	public void agentMoved(String id, Vector3 position);
	public void agentTookScreenshot(String id, String filepath);
	public void communicationEnded();
}
