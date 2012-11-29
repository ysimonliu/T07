package T07;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import communication.Message;

import lejos.nxt.Sound;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;
import lejos.nxt.comm.RS485Connection;

/**
 * This class will be instantiated by the master brick
 * to become the master in the two-way communication
 * @author Simon
 *
 */
public class CommunicationServer {
	
	private RS485Connection connection;
	private DataOutputStream dataOut;
	private DataInputStream dataIn;
	
	/**
	 * Constructs the communication server.
	 * the master brick of a two-brick communications should instantiate this class;
	 * connection via cable;
	 * upon successful connection, robot will sound two beeps.
	 */
	public CommunicationServer() {
		connection = RS485.waitForConnection(0, NXTConnection.PACKET);
		
		if (connection == null) {
			return;
		}
		Sound.twoBeeps();
		
		dataOut = connection.openDataOutputStream();
		dataIn = connection.openDataInputStream();
		
	}
	
	/**
	 * Sends a message to the master brick
	 * @param message - a message that passed in from the master brick
	 */
	public void sent(Message message) {
		try {
			// write the message and flush everything to the receiving end
			dataOut.writeUTF(message.getString());
			dataOut.flush();
		} catch (IOException e) {
			//
		}
	}
	
	/**
	 * Receives a message from the master brick
	 * @return
	 */
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
