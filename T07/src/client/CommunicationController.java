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
	
	@Override
	// Data Sender: This timedOut method will send data to the communication client periodically at DEFAULT_COMMUNICATION_PERIOD
	public void timedOut() {
		sendMidLightSensorValue();
		sendRightUSSensorValue();
	}

	@Override
	// Data Receiver: This run method will receive data from the communication client constantly on the fly
	public void run() {
		while (true) {
			Message message = this.communicationClient.receive();
			if (message != null) {
				// Depending on the type of the message, perform the task that is requested by the communication server
				if (message.getType() == Message.OPEN_CLAW) {
					FlagHandler.openClaws();
				}
				else if (message.getType() == Message.CLOSE_CLAW) {
					FlagHandler.closeClaws();
				}
				else if (message.getType() == Message.RAISE_LIFT_DISTANCE){
					FlagHandler.raiseClaws();
				}
				else {
					this.lightData[message.getType()] = message.getValue();
				}
			}
			
		}
		
	}
	
	// send the mid light sensor reading to the communication server
	public void sendMidLightSensorValue() {
		Message message = new Message(Message.MID_LIGHT_SENSOR_VALUE, this.lightPoller.getRawValue());
		this.communicationClient.sent(message);
	}
	
	// send the mid light sensor offset angle to the communication server
	public void sendMidLightSensorTheta() {
		Message message = new Message(Message.MID_LIGHT_SENSOR_THETA, this.lightPoller.getRawValue());
		this.communicationClient.sent(message);
	}
	
	// send the right ultrasonic sensor reading to the communication server
	private void sendRightUSSensorValue() {
		Message message = new Message(Message.RIGHT_US_SENSOR_VALUE, this.usPoller.getRawValue());
		this.communicationClient.sent(message);
	}

	// receive and read the content of a OPEN_CLAW message
	public int getOpenClaw(){
		return this.lightData[Message.OPEN_CLAW];
	}
	
	// receive and read the content of a CLOSE_CLAW message
	public int getCloseClaw(){
		return this.lightData[Message.CLOSE_CLAW];
	}
	
	// receive and read the content of a LIFT_CLAW message
	public int getRaiseLifeDistance(){
		return this.lightData[Message.RAISE_LIFT_DISTANCE];
	}

}
