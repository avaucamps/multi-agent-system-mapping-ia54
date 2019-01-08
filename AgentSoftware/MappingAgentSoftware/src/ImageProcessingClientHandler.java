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
            int numberOfCommunicationsFinished = 0;

            boolean startText = false;
            ArrayList<Byte> bytes = new ArrayList<>();
            while(numberOfCommunicationsFinished != FeatureMatchingType.values().length) {
                byte b = inputStream.readByte();

                if (b == 0) {
                    numberOfCommunicationsFinished++;
                    continue;
                }

                if (!startText && b == 1) {
                    startText = true;
                } else if (startText && b == 4) {
                    handleMessageReceived(bytes);
                    bytes.clear();
                    startText = false;
                } else if (startText) {
                    bytes.add(b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        action.received2DMatchPoints(matchingPoints);
    }

    private void handleMessageReceived(ArrayList<Byte> matchPointsBytes) {
        byte[] bytes = new byte[matchPointsBytes.size()];
        for (int i = 0; i < matchPointsBytes.size(); i++) {
            bytes[i] = matchPointsBytes.get(i);
        }

        String message = new String(bytes, StandardCharsets.UTF_8);
        String[] splitMessage = message.split("#");

        if (splitMessage[1].equals(FeatureMatchingType.sift.toString())) {
            setMatchPoints(splitMessage, FeatureMatchingType.sift);
        } else if (splitMessage[1].equals(FeatureMatchingType.harris.toString())) {
            setMatchPoints(splitMessage, FeatureMatchingType.harris);
        }
    }

    private void setMatchPoints(String[] splitMessage, FeatureMatchingType type) {
        String agent1_id = splitMessage[2];
        String agent2_id = splitMessage[3];

        double x1 = Double.parseDouble(splitMessage[4]);
        double y1 = Double.parseDouble(splitMessage[5]);
        double x2 = Double.parseDouble(splitMessage[6]);
        double y2 = Double.parseDouble(splitMessage[7]);

        Point p1 = new Point(x1, y1);
        Point p2 = new Point(x2, y2);

        matchingPoints.add(
                new MatchingPoint(
                        agent1_id,
                        agent2_id,
                        p1,
                        p2,
                        type
                )
        );

        System.out.println("Received feature point from image processing: " + p1.toString()
                + " , " + type.toString() + ".");
    }
}
