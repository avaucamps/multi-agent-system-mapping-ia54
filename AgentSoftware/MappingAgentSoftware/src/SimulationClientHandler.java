import com.sun.security.ntlm.Client;

import java.beans.PropertyChangeEvent;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SimulationClientHandler extends ClientHandler {

    public SimulationClientHandler(Socket socket, DataInputStream inputStream, DataOutputStream outputStream, Action action) {
        this.socket = socket;
        this.action = action;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!evt.getPropertyName().equals(PropertyChangeConstants.SIMULATION_MESSAGE_NAME)) {
            return;
        }

        ArrayList<MatchingPoint> matchingPoints = (ArrayList<MatchingPoint>) evt.getNewValue();
        for(MatchingPoint matchingPoint: matchingPoints) {
            String message = buildMessage(matchingPoint);
            System.out.println("Message sent to simulation: " + message);
            sendMessage(message);
        }

        sendMessage("Done");
    }

    @Override
    void listen() {
        try {
            boolean done = false;

            ArrayList<Byte> bytes = new ArrayList();
            int index = 0;
            boolean startOfHeading = false;
            boolean startOfText = false;
            boolean endOfText = false;
            boolean endOfTransmission = false;
            boolean allScreenshotDone = false;
            int messageType = -1;

            while(!done) {
                byte b = inputStream.readByte();

                if (b == 0) {
                    //done = true;
                    if (!allScreenshotDone) {
                        action.allScreenshotsDone();
                        allScreenshotDone = true;
                    } else {
                        action.allWorldFeaturePointsReceived();
                    }

                    continue;
                }

                switch (b) {
                    case 1:
                        startOfHeading = true;
                        continue;
                    case 2:
                        startOfText = true;
                        continue;
                    case 3:
                        endOfText = true;
                        continue;
                    case 4:
                        endOfTransmission = true;
                        break;
                    default:
                        break;
                }

                if (startOfHeading & !startOfText) {
                    try {
                        messageType = getMessageType(b);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                if (startOfHeading & startOfText & !endOfText) {
                    bytes.add(b);
                    index++;
                }

                if (endOfText & endOfTransmission) {
                    byte[] bytesArray = new byte[bytes.size()];
                    for (int i = 0; i < bytes.size(); i++) {
                        bytesArray[i] = bytes.get(i).byteValue();
                    }
                    //showMessage(bytesArray, messageType);
                    handleAction(messageType, bytesArray);

                    startOfHeading = false;
                    startOfText = false;
                    endOfText = false;
                    endOfTransmission = false;
                    index = 0;
                    messageType = -1;
                    bytes.removeAll(bytes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getMessageType(byte b) throws UnsupportedEncodingException {
        byte[] bArray = new byte[1];
        bArray[0] = b;
        String bString = new String(bArray, "UTF-8");

        return Integer.parseInt(bString);
    }

    private void showMessage(byte[] bytes, int messageType) {
        System.out.println("Message type : " + messageType);
        String message = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(message);

    }

    private void handleAction(int messageType, byte[] bytes) {
        switch (messageType) {
            case 1: // Agent spawned
                handleAgentSpawned(bytes);
                break;
            case 2: // Agent moved
                handleAgentMoved(bytes);
                break;
            case 3: // Agent took screenshot
                handleAgentTookScreenshot(bytes);
                break;
            case 4:
                handleAgentWorldFeaturePoint(bytes);
                break;
            default:
                break;
        }
    }

    private void handleAgentSpawned(byte[] bytes) {
        String fullMessage = new String(bytes, StandardCharsets.UTF_8);
        String[] splitMessage = fullMessage.split("#");
        action.agentSpawned(splitMessage[1]);
    }

    private void handleAgentMoved(byte[] bytes) {
        String fullMessage = new String(bytes, StandardCharsets.UTF_8);
        String[] splitMessage = fullMessage.split("#");
        String agentId = splitMessage[1];

        action.agentMoved(
                agentId,
                extractPosition(splitMessage[2])
        );
    }

    private void handleAgentTookScreenshot(byte[] bytes) {
        String fullMessage = new String(bytes, StandardCharsets.UTF_8);
        String[] splitMessage = fullMessage.split("#");
        String agentId = splitMessage[1];
        String filepath = splitMessage[2];
        action.agentTookScreenshot(agentId, filepath);
    }

    private void handleAgentWorldFeaturePoint(byte[] bytes) {
        String fullMessage = new String(bytes, StandardCharsets.UTF_8);
        String[] splitMessage = fullMessage.split("#");
        String agentId = splitMessage[1];
        double screenX = Double.parseDouble(splitMessage[4]);
        double screenY = Double.parseDouble(splitMessage[5]);
        double worldX = Double.parseDouble(splitMessage[2]);
        double worldY = Double.parseDouble(splitMessage[3]);
        Point worldPoint = new Point(screenX, screenY);
        Point screenPoint = new Point(worldX, worldY);
        System.out.println("World point received: " + worldX + ", " + worldY);
        action.agentWorldFeaturePoint(agentId, worldPoint, screenPoint);
    }

    private Vector3 extractPosition(String position) {
        position = position.replace("(", "").replace(")","");
        String[] splittedPosition = position.split(",");
        Vector3 position3 = new Vector3(
                Double.parseDouble(splittedPosition[0]),
                Double.parseDouble(splittedPosition[1]),
                Double.parseDouble(splittedPosition[2])
        );

        return position3;
    }

    private String buildMessage(MatchingPoint matchingPoint) {
        String message = "";
        message += "#";
        message += matchingPoint.getAgent1Id();
        message += "#";
        message += matchingPoint.getPointAgent1().getX();
        message += "#";
        message += matchingPoint.getPointAgent1().getY();
        message += "#";

        return message;
    }
}
