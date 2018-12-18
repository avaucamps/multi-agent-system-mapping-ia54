import java.beans.PropertyChangeEvent;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ImageProcessingClientHandler extends ClientHandler {

    private ArrayList<MatchingPoint> matchingPoints;

    public ImageProcessingClientHandler(Socket socket, DataInputStream inputStream, DataOutputStream outputStream, Action action) {
        this.socket = socket;
        this.action = action;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        matchingPoints = new ArrayList<>();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!evt.getPropertyName().equals(PropertyChangeConstants.IMAGE_PROCESSING_MESSAGE_NAME)) {
            return;
        }

        ArrayList<String> messages = (ArrayList<String>) evt.getNewValue();
        for(String message: messages) {
            System.out.println("Message sent to image processing: " + message);
            sendMessage(message);
        }

        sendMessage("AllMessagesSent");
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
                } else if (startText && b == 4) {
                    setMatchPoints(bytes);
                    bytes.clear();
                    startText = false;
                } else if (startText){
                    bytes.add(b);
                }
            }

            action.received2DMatchPoints(matchingPoints);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMatchPoints(ArrayList<Byte> matchPointsBytes) {
        byte[] bytes = new byte[matchPointsBytes.size()];
        for (int i = 0; i < matchPointsBytes.size(); i++) {
            bytes[i] = matchPointsBytes.get(i);
        }

        String message = new String(bytes, StandardCharsets.UTF_8);
        String[] splittedMessage = message.split("#");

        String agent1_id = splittedMessage[1];
        String agent2_id = splittedMessage[2];

        double x1 = Double.parseDouble(splittedMessage[3]);
        double y1 = Double.parseDouble(splittedMessage[4]);
        double x2 = Double.parseDouble(splittedMessage[5]);
        double y2 = Double.parseDouble(splittedMessage[6]);

        Point p1 = new Point(x1, y1);
        Point p2 = new Point(x2, y2);

        matchingPoints.add(new MatchingPoint(agent1_id, agent2_id, p1, p2));
    }
}
