import java.beans.PropertyChangeEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ImageProcessingClientHandler extends ClientHandler {

    public ImageProcessingClientHandler(Socket socket, DataInputStream inputStream, DataOutputStream outputStream, Action action) {
        this.socket = socket;
        this.action = action;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!evt.getPropertyName().equals(PropertyChangeConstants.IMAGE_PROCESSING_MESSAGE_NAME)) {
            return;
        }

        ArrayList<String> messages = (ArrayList<String>) evt.getNewValue();
        for(String message: messages) {
            sendMessage(message);
        }

        sendMessage("AllAgentsSent");
    }

    @Override
    void listen() {

    }
}
