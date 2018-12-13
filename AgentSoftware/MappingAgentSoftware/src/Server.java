import com.sun.security.ntlm.Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int port;
    private Action action;
    private ClientType clientType;
    private ClientHandler clientHandler;

    public Server(int port, ClientType clientType, Action action) {
        this.port = port;
        this.action = action;
        this.clientType = clientType;
        startServer();
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    private void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            Socket clientSocket = serverSocket.accept();

            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

            switch (clientType) {
                case simulation:
                    clientHandler = new SimulationClientHandler(
                            clientSocket,
                            inputStream,
                            outputStream,
                            action
                    );

                    clientHandler.start();

                    System.out.println("Communication with simulation started.");
                case imageProcessing:
                    clientHandler = new ImageProcessingClientHandler(
                            clientSocket,
                            inputStream,
                            outputStream,
                            action
                    );

                    clientHandler.start();

                    System.out.println("Communication with image processing started.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum ClientType {
        simulation, imageProcessing
    }
}