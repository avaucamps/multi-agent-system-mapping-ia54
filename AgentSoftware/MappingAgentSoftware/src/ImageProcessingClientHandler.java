import java.beans.PropertyChangeEvent;
import java.io.*;
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
        try {
            boolean done = false;

            boolean startText = false;
            ArrayList<Byte> bytes = new ArrayList<>();
            while(!done) {
                byte b = inputStream.readByte();

                if (b == 0) {
                    done = true;
                    continue;
                }

                if (!startText && b == 1) {
                    startText = true;
                    bytes.add(b);
                } else if (startText && b == 2) {
                    setMatchPoints(bytes);
                    bytes.clear();
                    startText = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMatchPoints(ArrayList<Byte> matchPointsBytes) {
        String message = matchPointsBytes.toString();
        String[] splittedMessage = message.split("#");

        String agent1_id = splittedMessage[0];
        String agent2_id = splittedMessage[1];

        int x1 = Integer.parseInt(splittedMessage[2]);
        int y1 = Integer.parseInt(splittedMessage[3]);
        int x2 = Integer.parseInt(splittedMessage[4]);
        int y2 = Integer.parseInt(splittedMessage[5]);

        Point p1 = new Point(x1, y1);
        Point p2 = new Point(x2, y2);

        MatchingPoint m = new MatchingPoint(agent1_id, agent2_id, p1, p2);

        action.receivedMatchPoints(m);
    }
}
