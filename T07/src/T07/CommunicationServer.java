package T07;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import communication.Message;

import lejos.nxt.Sound;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;
import lejos.nxt.comm.RS485Connection;

public class CommunicationServer {
	
	private RS485Connection connection;
	private DataOutputStream dataOut;
	private DataInputStream dataIn;
	
	// This will make the master brick act as a communication server
	public CommunicationServer() {
		connection = RS485.waitForConnection(0, NXTConnection.PACKET);
		
		if (connection == null) {
			return;
		}
		Sound.twoBeeps();
		
		dataOut = connection.openDataOutputStream();
		dataIn = connection.openDataInputStream();
		
	}
	
	// This will send a message to the communication client, or the slave brick
	public void sent(Message message) {
		try {
			// write the message and flush everything to the receiving end
			dataOut.writeUTF(message.getString());
			dataOut.flush();
		} catch (IOException e) {
			//
		}
	}
	
	// This will receive a message from the communication client, or the slave brick
	public Message receive() {
		String messageString = null;
		Message receivedMessage = null;
		try {
			// the data in stream will be read into the received message and returned
			messageString = dataIn.readUTF();
			receivedMessage = new Message(messageString);
		} catch (IOException e) {
		}		
		return receivedMessage;
	}
	
	
	

}
