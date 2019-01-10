public class main {

	private static MapDisplay mapDisplaySift;
	private static MapDisplay mapDisplayHarris;

	public static void main(String[] args) {
		EnvironmentUpdate envUpdate = new EnvironmentUpdate() {
			@Override
			public void addPointToMap(Point point, FeatureMatchingType type) {
				if (type.isEqual(FeatureMatchingType.harris)) {
					handleHarrisMap(point);
				} else if (type.isEqual(FeatureMatchingType.sift)) {
					handleSiftMap(point);
				}
			}
		};

		Environment environment = new Environment(envUpdate);

		Action action = new Action() {
			@Override
			public void onNumberOfAgents(int n) { environment.onNumberOfAgents(n); }

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
			public void onNewMatchingPoint(MatchingPoint matchingPoint) {
				environment.onNewMatchingPoint(matchingPoint);
			}

			@Override
			public void onNewWorldFeaturePoint(String id, Point worldPoint, Point screenPoint, FeatureMatchingType type) {
				environment.onNewWorldFeaturePoint(id, worldPoint, screenPoint, type);
			}
		};

		Server serverImageProcessing = new Server(9992, Server.ClientType.imageProcessing, action);
		Server serverSimulation = new Server(9991, Server.ClientType.simulation, action);

		environment.addPropertyChangeListener(serverImageProcessing.getClientHandler());
		environment.addPropertyChangeListener(serverSimulation.getClientHandler());
	}

	private static void handleHarrisMap(Point p) {
		if (mapDisplayHarris == null) {
			mapDisplayHarris = new MapDisplay(FeatureMatchingType.harris);
		}

		mapDisplayHarris.AddPoint(p);
	}

	private static void handleSiftMap(Point p) {
		if (mapDisplaySift == null) {
			mapDisplaySift = new MapDisplay(FeatureMatchingType.sift);
		}

		mapDisplaySift.AddPoint(p);
	}
}