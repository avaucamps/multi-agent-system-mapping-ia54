import java.util.ArrayList;

public interface Action {
	void onNumberOfAgents(int numberOfAgents);
	void agentSpawned(String id);
	void agentMoved(String id, Vector3 position);
	void agentTookScreenshot(String id, String filepath);
	void onNewMatchingPoint(MatchingPoint matchingPoint);
	void onNewWorldFeaturePoint(String id, Point worldPoint, Point screenPoint, FeatureMatchingType type);
}
