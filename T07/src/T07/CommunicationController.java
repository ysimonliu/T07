package T07;

/**
 * Sending and receiving data to and from client brick
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
	
	public CommunicationController(CommunicationServer communicationServer) {
		// constructor
		this.communicationServer = communicationServer;
		
		// start the data receiver
		new Thread(this).start();
		
		// start the data sender
		this.communicationTimer = new Timer(DEFAULT_COMMUNICATION_PERIOD, this);
		this.communicationTimer.start();
	}
	
	@Override
	// Data Sender: This timedOut method will send data to the communication client periodically at DEFAULT_COMMUNICATION_PERIOD
	public void timedOut() {
		//TODO:send more data needed.
	}

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
	
	// send a request message to the communication client to perform a claw open task
	public void sendOpenClaw() {
		Message message = new Message(Message.OPEN_CLAW, 1);
		this.communicationServer.sent(message);
	}
	
	// send a request message to the communication client to perform a claw close task
	public void sendCloseClaw() {
		Message message = new Message(Message.CLOSE_CLAW, 1);
		this.communicationServer.sent(message);
	}
	
	// send a request message to the communication client to perform and lift claw task
	public void sendRaiseLift(){
		Message message = new Message(Message.RAISE_LIFT, 0);
		this.communicationServer.sent(message);
	}
	
	// send a request message to the communication client to perform and lower claw task
	public void sendLowerLift(){
		Message message = new Message(Message.LOWER_LIFT, 0);
		this.communicationServer.sent(message);
	}
	
	// receive the mid light sensor value from the communication client
	// and update in the lightData array
	public int getMidLightSensorValue() {
		return this.lightData[Message.MID_LIGHT_SENSOR_VALUE];
	}
	
	// receive the mid light sensor offset angle from the communication client
	// and update in the lightData array
	public int getLightSensorTheta() {
		return this.lightData[Message.MID_LIGHT_SENSOR_THETA];
	}

	// receive the right ultrasonic sensor value from the communication client
	// and update in the lightData array
	public int getRightUSSensorValue(){
		return this.lightData[Message.RIGHT_US_SENSOR_VALUE];
	}
	
}
