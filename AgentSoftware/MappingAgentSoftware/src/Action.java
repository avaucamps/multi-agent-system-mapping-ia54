
public interface Action {
	void agentSpawned(String id);
	void agentMoved(String id, Vector3 position);
	void agentTookScreenshot(String id, String filepath);
	void allScreenshotsDone();
	void receivedMatchPoints(MatchingPoint matchingPoint);
}
