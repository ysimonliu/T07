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
	 * constructor of client-end communication controller
	 * upon instantiation, this class will call timedOut() and run() method to send & receive data
	 * @param communicationServer
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
	 * send data to the master brick periodically at DEFAULT_COMMUNICATION_PERIOD
	 * will run with run() method in parallel at the same time
	 */
	@Override
	// Data Sender: This timedOut method will send data to the communication client periodically at DEFAULT_COMMUNICATION_PERIOD
	public void timedOut() {
		//TODO:send more data needed.
	}

	/**
	 * receives data from the slave brick, and update the local copy of all the readings whenever possible
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
	 * send a message to the slave brick to open the claw
	 */
	// send a request message to the communication client to perform a claw open task
	public void sendOpenClaw() {
		Message message = new Message(Message.OPEN_CLAW, 1);
		this.communicationServer.sent(message);
	}
	
	/**
	 * send a message to the slave brick to close the claw
	 */
	// send a request message to the communication client to perform a claw close task
	public void sendCloseClaw() {
		Message message = new Message(Message.CLOSE_CLAW, 1);
		this.communicationServer.sent(message);
	}
	
	/**
	 * send a message to the slave brick to lift the claw
	 */
	// send a request message to the communication client to perform and lift claw task
	public void sendRaiseLift(){
		Message message = new Message(Message.RAISE_LIFT, 0);
		this.communicationServer.sent(message);
	}
	
	/**
	 * send a message to the slave brick
	 */
	// send a request message to the communication client to perform and lower claw task
	public void sendLowerLift(){
		Message message = new Message(Message.LOWER_LIFT, 0);
		this.communicationServer.sent(message);
	}
	
	/**
	 * return the latest received middle light sensor reading
	 * @return
	 */
	public int getMidLightSensorValue() {
		return this.lightData[Message.MID_LIGHT_SENSOR_VALUE];
	}

	/**
	 * return the middle light sensor offset angle
	 * @return
	 */
	public int getLightSensorTheta() {
		return this.lightData[Message.MID_LIGHT_SENSOR_THETA];
	}

	/**
	 * rteurn the right ultrasonic sensor value
	 * @return
	 */
	public int getRightUSSensorValue(){
		return this.lightData[Message.RIGHT_US_SENSOR_VALUE];
	}
	
}
