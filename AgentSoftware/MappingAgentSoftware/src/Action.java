import java.util.ArrayList;

public interface Action {
	void agentSpawned(String id);
	void agentMoved(String id, Vector3 position);
	void agentTookScreenshot(String id, String filepath);
	void allScreenshotsDone();
	void received2DMatchPoints(ArrayList<MatchingPoint> matchingPoint);
	void agentWorldFeaturePoint(String id, Point worldPoint, Point screenPoint);
	void allWorldFeaturePointsReceived();
}
