package client;

/**
 * Sending and receiving data to and from master brick
 */

import communication.Message;

import lejos.util.Timer;
import lejos.util.TimerListener;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;

public class CommunicationController implements TimerListener, Runnable{

	// declare necessary variables for this class
	private Timer communicationTimer;
	private final int DEFAULT_COMMUNICATION_PERIOD = 60;
	private CommunicationClient communicationClient;
	private LightPoller lightPoller;
	private USPoller usPoller;
	// The received data will be recorded into the array lightData
	// the meaning of each index, and the array length is defined in the Message class
	private int[] lightData = new int [Message.NUMBER_OF_ELEMENTS];
	
	/**
	 * constructor of client-end communication controller
	 * upon instantiation, this class will call timedOut() and run() method to send & receive data
	 * @param ls
	 * @param usPoller
	 * @param communicationClient
	 */
	public CommunicationController(LightPoller ls, USPoller usPoller, CommunicationClient communicationClient) {
		// constructor
		this.communicationClient = communicationClient;
		this.lightPoller = ls;
		this.usPoller = usPoller;
		
		// run the data receiver
		new Thread(this).start();
		
		// run the data sender
		this.communicationTimer = new Timer(DEFAULT_COMMUNICATION_PERIOD, this);
		this.communicationTimer.start();
	}
	
	/**
	 * send data to the master brick periodically at DEFAULT_COMMUNICATION_PERIOD
	 */
	@Override
	public void timedOut() {
		sendMidLightSensorValue();
		sendRightUSSensorValue();
	}

	/**
	 * receive data from master brick and process the message received
	 */
	@Override
	// Data Receiver: This run method will receive data from the communication client constantly on the fly
	public void run() {
		while (true) {
			Message message = this.communicationClient.receive();
			if (message != null) {
				// process message
				process(message);
			}
		}
	}
	
	/**
	 * helper method to process message
	 * when a request to perform a task is sent, trigger the method
	 * @param message
	 */
	private void process(Message message) {
		switch(message.getType()) {
		case Message.CLOSE_CLAW:
			FlagHandler.closeClaws();
			break;
		case Message.RAISE_LIFT:
			FlagHandler.raiseClaws();
			break;
		case Message.OPEN_CLAW:
			FlagHandler.openClaws();
			break;
		case Message.LOWER_LIFT:
			FlagHandler.lowerClaws();
			break;
		default:
			this.lightData[message.getType()] = message.getValue();
		}
	}

	/**
	 * send the middle light sensor value to the master brick
	 */
	public void sendMidLightSensorValue() {
		Message message = new Message(Message.MID_LIGHT_SENSOR_VALUE, this.lightPoller.getRawValue());
		this.communicationClient.send(message);
	}

	
	/**
	 * send the right ultrasonic sensor reading to the communication server
	 */
	private void sendRightUSSensorValue() {
		Message message = new Message(Message.RIGHT_US_SENSOR_VALUE, this.usPoller.getRawValue());
		this.communicationClient.send(message);
	}
}
