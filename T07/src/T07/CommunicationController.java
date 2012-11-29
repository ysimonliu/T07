package T07;

/**
 * This class is used by the communication server to send, receive and process messages
 * @author Simon Liu
 *
 */

import communication.Message;

import lejos.util.Timer;
import lejos.util.TimerListener;

public class CommunicationController implements TimerListener, Runnable{
	
	// declare necessary variables for this class
	private Timer communicationTimer;
	private final int DEFAULT_COMMUNICATION_PERIOD = 60;
	private CommunicationServer communicationServer;
	// The received data will be recorded into the array lightData
	// the meaning of each index, and the array length is defined in the Message class
	private int[] lightData = new int [Message.NUMBER_OF_ELEMENTS];
	
	/**
	 * Constructs the client-end communication controller
	 * upon instantiation, this class will call timedOut() and run() method to send & receive data
	 * @param communicationServer - the communication server
	 */
	public CommunicationController(CommunicationServer communicationServer) {
		// constructor
		this.communicationServer = communicationServer;
		
		// start the data receiver
		new Thread(this).start();
		
		// start the data sender
		this.communicationTimer = new Timer(DEFAULT_COMMUNICATION_PERIOD, this);
		this.communicationTimer.start();
	}
	
	/**
	 * Sends data to the master brick periodically at DEFAULT_COMMUNICATION_PERIOD
	 * will run with run() method in parallel at the same time upon instantiation
	 */
	@Override
	// Data Sender: This timedOut method will send data to the communication client periodically at DEFAULT_COMMUNICATION_PERIOD
	public void timedOut() {
		//TODO:send more data needed.
	}

	/**
	 * Receives data from the slave brick, and update the local copy of all the readings whenever possible
	 * will run with the timedOut() method in parallel at the same time upon instantiation
	 */
	@Override
	// Data Receiver: This run method will receive data from the communication client constantly on the fly
	public void run() {
		while (true) {
			Message message = this.communicationServer.receive();
			if (message != null) {
				this.lightData[message.getType()] = message.getValue();
			}
		}
	}
	
	/**
	 * Sends a message to the slave brick to open the claw
	 */
	public void sendOpenClaw() {
		Message message = new Message(Message.OPEN_CLAW, 1);
		this.communicationServer.sent(message);
	}
	
	/**
	 * Sends a message to the slave brick to close the claw
	 */
	public void sendCloseClaw() {
		Message message = new Message(Message.CLOSE_CLAW, 1);
		this.communicationServer.sent(message);
	}
	
	/**
	 * Sends a message to the slave brick to lift the claw
	 */
	public void sendRaiseLift(){
		Message message = new Message(Message.RAISE_LIFT, 0);
		this.communicationServer.sent(message);
	}
	
	/**
	 * Sends a message to the slave brick
	 */
	// send a request message to the communication client to perform and lower claw task
	public void sendLowerLift(){
		Message message = new Message(Message.LOWER_LIFT, 0);
		this.communicationServer.sent(message);
	}
	
	/**
	 * Returns the most recently received middle light sensor reading
	 * @return the most recently received middle light sensor reading
	 */
	public int getMidLightSensorValue() {
		return this.lightData[Message.MID_LIGHT_SENSOR_VALUE];
	}

	/**
	 * Returns the most recently middle light sensor offset angle
	 * @return the most recently middle light sensor offset angle 
	 */
	public int getLightSensorTheta() {
		return this.lightData[Message.MID_LIGHT_SENSOR_THETA];
	}

	/**
	 * Returns the right ultrasonic sensor value
	 * @return the right ultrasonic sensor value
	 */
	public int getRightUSSensorValue(){
		return this.lightData[Message.RIGHT_US_SENSOR_VALUE];
	}
	
}
