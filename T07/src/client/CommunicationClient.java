package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import communication.Message;



import lejos.nxt.Sound;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;
import lejos.nxt.comm.RS485Connection;

/**
 * This class will be instantiated by the slave brick
 * to become the client in the two-way communication
 * @author Simon Liu
 *
 */
public class CommunicationClient {

	private RS485Connection connection;
	private DataOutputStream dataOut;
	private DataInputStream dataIn;
	
	/**
	 * constructor for CommunicationClient class
	 * the slave brick of a two-brick communications should instantiate this class
	 * connection via cable
	 * upon successful connection, robot will sound two beeps
	 */
	public CommunicationClient() {

		connection = RS485.connect("0016530E3CEC", NXTConnection.PACKET);
		
		if (connection == null) {
			return;
		}
		Sound.twoBeeps();
		
		dataOut = connection.openDataOutputStream();
		dataIn = connection.openDataInputStream();
		
		
	}
	
	/**
	 * by passing in a message, this method will send a message to the master brick
	 * @param message
	 */
	public void send(Message message) {
		try {
			dataOut.writeUTF(message.getString());
			dataOut.flush();
		} catch (IOException e) {
			//
		}
	}
	
	/**
	 * receive and return a message from the master brick
	 * @return
	 */
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
