import java.lang.reflect.Array;
import java.util.ArrayList;

public class main {

	private static MapDisplay mapDisplaySift;
	private static MapDisplay mapDisplayHarris;

	public static void main(String[] args) {
		EnvironmentUpdate envUpdate = new EnvironmentUpdate() {
			@Override
			public void mapEnvironment(ArrayList<Point> points, FeatureMatchingType type) {
				mapDisplaySift = new MapDisplay(points, type);
			}
		};

		Environment environment = new Environment(envUpdate);

		Action action = new Action() {
			@Override
			public void agentSpawned(String id) {
				environment.agentSpawned(id);
			}

			@Override
			public void agentMoved(String id, Vector3 position) {
				environment.agentMoved(id, position);
			}

			@Override
			public void agentTookScreenshot(String id, String filepath) {
				environment.agentTookScreenshot(id, filepath);
			}

			@Override
			public void allScreenshotsDone() {
				environment.allScreenshotsDone();
			}

			@Override
			public void received2DMatchPoints(ArrayList<MatchingPoint> matchingPoints) {
				environment.received2DMatchPoints(matchingPoints);
			}

			@Override
			public void agentWorldFeaturePoint(String id, Point worldPoint, Point screenPoint, FeatureMatchingType type) {
				environment.agentWorldFeaturePoint(id, worldPoint, screenPoint, type);
			}

			@Override
			public void allWorldFeaturePointsReceived() {
				environment.allWorldFeaturePointsReceived();
			}
		};

		Server serverImageProcessing = new Server(9992, Server.ClientType.imageProcessing, action);
		Server serverSimulation = new Server(9991, Server.ClientType.simulation, action);

		environment.addPropertyChangeListener(serverImageProcessing.getClientHandler());
		environment.addPropertyChangeListener(serverSimulation.getClientHandler());
	}
}