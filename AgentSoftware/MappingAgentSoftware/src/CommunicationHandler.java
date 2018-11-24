import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

public class CommunicationHandler {
	
	public static void createConnection(AgentAction agentAction) {
		
		try(ServerSocket serverSocket = new ServerSocket(9991)) {
	        Socket connectionSocket = serverSocket.accept();
	        
	        DataInputStream inputToServer = new DataInputStream(connectionSocket.getInputStream());
	        OutputStream outputFromServer = connectionSocket.getOutputStream();
	        
	        boolean done = false;
	        
	        ArrayList<Byte> bytes = new ArrayList();
	        int index = 0;
	        boolean startOfHeading = false;
	        boolean startOfText = false;
	        boolean endOfText = false;
	        boolean endOfTransmission = false;
	        int messageType = -1;
	        
	        while(!done) {
	        	byte b = inputToServer.readByte();
	        	
	        	if (b == 0) {
	        		done = true;
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
	        		messageType = getMessageType(b);
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
	        		handleAction(messageType, bytesArray, agentAction);
	        		
	        		startOfHeading = false;
	        		startOfText = false;
	        		endOfText = false;
	        		endOfTransmission = false;
	        		index = 0;
	        		messageType = -1;
	        		bytes.removeAll(bytes);
	        	}
	        }
		} catch (IOException e) {
			e.printStackTrace();
		};
	}
	
	private static int getMessageType(byte b) throws UnsupportedEncodingException {
		byte[] bArray = new byte[1];
		bArray[0] = b;
		String bString = new String(bArray, "UTF-8");
		
		return Integer.parseInt(bString);
	}
	
	private static void showMessage(byte[] bytes, int messageType) {
		System.out.println("Message type : " + messageType);
		String message = new String(bytes, StandardCharsets.UTF_8);
		System.out.println(message);
		
	}
	
	private static void handleAction(int messageType, byte[] bytes, AgentAction agentAction) {
		switch (messageType) {
		case 1: // Agent spawned
			handleAgentSpawned(bytes, agentAction);
			break;
		case 2: // Agent moved
			handleAgentMoved(bytes, agentAction);
			break;
		case 3: // Agent took screenshot
			handleAgentTookScreenshot(bytes, agentAction);
		default:
			break;
		}
	}
	
	private static void handleAgentSpawned(byte[] bytes, AgentAction agentAction) {
		String agentId = new String(bytes, StandardCharsets.UTF_8);
		agentAction.agentSpawned(agentId);
	}
	
	private static void handleAgentMoved(byte[] bytes, AgentAction agentAction) {
		String fullMessage = new String(bytes, StandardCharsets.UTF_8);
		String[] splittedMessage = fullMessage.split("#");
		String agentId = splittedMessage[1];
		
		agentAction.agentMoved(
				agentId, 
				extractPosition(splittedMessage[2])
		);
	}
	
	private static void handleAgentTookScreenshot(byte[] bytes, AgentAction agentAction) {
		String fullMessage = new String(bytes, StandardCharsets.UTF_8);
		String[] splittedMessage = fullMessage.split("#");
		String agentId = splittedMessage[0];
		String filepath = splittedMessage[1];
		agentAction.agentTookScreenshot(agentId, filepath);
	}
	
	private static Vector3 extractPosition(String position) {
		position = position.replace("(", "").replace(")","");
		String[] splittedPosition = position.split(",");
		Vector3 position3 = new Vector3(
				Double.parseDouble(splittedPosition[0]),
				Double.parseDouble(splittedPosition[1]),
				Double.parseDouble(splittedPosition[2])
		);
		
		return position3;
	}
}
