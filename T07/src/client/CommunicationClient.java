package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import communication.Message;



import lejos.nxt.Sound;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;
import lejos.nxt.comm.RS485Connection;

public class CommunicationClient {

	private RS485Connection connection;
	private DataOutputStream dataOut;
	private DataInputStream dataIn;
	

	
	public CommunicationClient() {

		connection = RS485.connect("0016530E3CEC", NXTConnection.PACKET);
		
		if (connection == null) {
			return;
		}
		Sound.twoBeeps();
		
		dataOut = connection.openDataOutputStream();
		dataIn = connection.openDataInputStream();
		
		
	}
	
	public void sent(Message message) {
		try {
			dataOut.writeUTF(message.getString());
			dataOut.flush();
		} catch (IOException e) {
			//
		}
	}
	
	public Message receive() {
		String messageString = null;
		Message receivedMessage = null;
		try {
			messageString = dataIn.readUTF();
			receivedMessage = new Message(messageString);
		} catch (IOException e) {
		}		
		return receivedMessage;
	}


}
