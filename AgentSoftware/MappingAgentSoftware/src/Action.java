import java.util.ArrayList;

public interface Action {
	void onNumberOfAgents(int numberOfAgents);
	void agentSpawned(String id);
	void agentMoved(String id, Vector3 position);
	void agentTookScreenshot(String id, String filepath);
	void received2DMatchPoints(ArrayList<MatchingPoint> matchingPoint);
	void agentWorldFeaturePoint(String id, Point worldPoint, Point screenPoint, FeatureMatchingType type);
	void allWorldFeaturePointsReceived();
}
