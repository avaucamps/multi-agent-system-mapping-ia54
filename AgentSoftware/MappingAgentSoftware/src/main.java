
public class main {
	
	public static void main(String[] args) {
		Environment environment = new Environment();

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
		};

		Server serverImageProcessing = new Server(9992, Server.ClientType.imageProcessing, action);
		Server serverSimulation = new Server(9991, Server.ClientType.simulation, action);

		environment.addPropertyChangeListener(serverImageProcessing.getClientHandler());
		environment.addPropertyChangeListener(serverSimulation.getClientHandler());
	}
}