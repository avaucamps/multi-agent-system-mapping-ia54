import java.util.ArrayList;

public class main {

	private static MapDisplay mapDisplay;

	public static void main(String[] args) {

		ArrayList<Point> points = new ArrayList<>();
		points.add(new Point(10,5));
		points.add(new Point(-3,2));
		points.add(new Point(-5,-2));
		points.add(new Point(7,3));
		points.add(new Point(3,3));

		mapDisplay = new MapDisplay(points);
		/*EnvironmentUpdate envUpdate = new EnvironmentUpdate() {
			@Override
			public void mapEnvironment(ArrayList<Point> points) {
				mapDisplay = new MapDisplay(points);
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
			public void agentWorldFeaturePoint(String id, Point worldPoint, Point screenPoint) {
				environment.agentWorldFeaturePoint(id, worldPoint, screenPoint);
			}

			@Override
			public void allWorldFeaturePointsReceived() {
				environment.allWorldFeaturePointsReceived();
			}
		};

		Server serverImageProcessing = new Server(9992, Server.ClientType.imageProcessing, action);
		Server serverSimulation = new Server(9991, Server.ClientType.simulation, action);

		environment.addPropertyChangeListener(serverImageProcessing.getClientHandler());
		environment.addPropertyChangeListener(serverSimulation.getClientHandler());*/
	}
}